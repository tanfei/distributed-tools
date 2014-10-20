package com.lordjoe.distributed.hydra;

import com.lordjoe.distributed.*;
import com.lordjoe.distributed.hydra.scoring.*;
import com.lordjoe.distributed.spectrum.*;
import org.apache.hadoop.fs.*;
import org.apache.spark.api.java.*;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.*;
import org.systemsbiology.xtandem.*;
import org.systemsbiology.xtandem.hadoop.*;
import org.systemsbiology.xtandem.pepxml.*;
import org.systemsbiology.xtandem.reporting.*;
import org.systemsbiology.xtandem.scoring.*;
import scala.*;

import java.io.*;
import java.util.*;


/**
 * com.lordjoe.distributed.hydra.ScanScorer
 * User: Steve
 * Date: 10/7/2014
 */
public class ScanScorer {

    public static class writeScoresMapper implements Function<Tuple2<String, IScoredScan>, Tuple2<String, String>> {
        final BiomlReporter reporter;

        private writeScoresMapper(final BiomlReporter pReporter) {
            reporter = pReporter;
        }



        @Override
        public Tuple2<String, String> call(final Tuple2<String, IScoredScan> v1) throws Exception {
            IScoredScan scan = v1._2();
            StringWriter sw = new StringWriter();
            Appendable out = new PrintWriter(sw);
            reporter.writeScanScores(scan, out, 1);
            return new Tuple2(v1._1(), sw.toString());
        }
    }

    public static class ScanKeyMapper implements PairFlatMapFunction<Iterator<KeyValueObject<String, IScoredScan>>, String, IScoredScan> {
        @Override
        public Iterable<Tuple2<String, IScoredScan>> call(final Iterator<KeyValueObject<String, IScoredScan>> t) throws Exception {
            List<Tuple2<String, IScoredScan>> mapped = new ArrayList<Tuple2<String, IScoredScan>>();
            while (t.hasNext()) {
                KeyValueObject<String, IScoredScan> kscan = t.next();
                IScoredScan value = kscan.value;
                String id = value.getId(); //  now we match scans
                mapped.add(new Tuple2(id, value));
            }
            return mapped;
        }
    }

    public static class DropNoMatchScansFilter implements Function<KeyValueObject<String, IScoredScan>, java.lang.Boolean> {
        @Override
        public java.lang.Boolean call(final KeyValueObject<String, IScoredScan> v1) throws Exception {
            IScoredScan vx = v1.value;
            return v1.value.isMatchPresent();
        }
    }

    public static class chooseBestScanScore implements Function2<IScoredScan, IScoredScan, IScoredScan> {
        @Override
        public IScoredScan call(final IScoredScan v1, final IScoredScan v2) throws Exception {
            ISpectralMatch match1 = v1.getBestMatch();
            ISpectralMatch match2 = v2.getBestMatch();
            if (match1.getHyperScore() > match2.getHyperScore())
                return v1;
            else
                return v2;
        }
    }

    public static final int SPARK_CONFIG_INDEX = 0;
    public static final int TANDEM_CONFIG_INDEX = 1;
    public static final int SPECTRA_INDEX = 2;

    /**
     * call with args like or20080320_s_silac-lh_1-1_11short.mzxml in Sample2
     *
     * @param args
     */
    public static void main(String[] args) throws Exception {

        if (args.length < TANDEM_CONFIG_INDEX + 1) {
            System.out.println("usage sparkconfig configFile fastaFile");
            return;
        }
          SparkUtilities.readSparkProperties(args[SPARK_CONFIG_INDEX]);

        String pathPrepend = SparkUtilities.getSparkProperties().getProperty("com.lordjoe.distributed.PathPrepend") ;
        if(pathPrepend != null)
            XTandemHadoopUtilities.setDefaultPath(pathPrepend);


        String configStr = SparkUtilities.buildPath(args[TANDEM_CONFIG_INDEX] );

        String spectra = SparkUtilities.buildPath(args[SPECTRA_INDEX] );

        SparkMapReduceScoringHandler handler = new SparkMapReduceScoringHandler( configStr);
        JavaSparkContext ctx = SparkUtilities.getCurrentContext();

        if(false)
            handler.buildLibraryIfNeeded();

        Map<Integer, Integer> databaseSizes = handler.getDatabaseSizes();


        JavaPairRDD<String, IMeasuredSpectrum> scans = SparkSpectrumUtilities.parseSpectrumFile(spectra, ctx);
        JavaRDD<KeyValueObject<String, IMeasuredSpectrum>> scansKV = SparkUtilities.fromTuples(scans);


        /**
         *
         * COMBINING KEYS DOES not WORk but single keys does
         scansKV = scansKV.persist(StorageLevel.MEMORY_AND_DISK_2());
         //   scansKV = SparkUtilities.realizeAndReturn(scansKV, ctx);

         handler.performSourceMapReduce(scansKV);
         JavaRDD<KeyValueObject<String, IScoredScan>> scores1 = handler.getHandler().getOutput();
         List<KeyValueObject<String, IScoredScan>> collect1 = scores1.collect();
         for (KeyValueObject<String, IScoredScan> scoredScan : collect1) {
         IScoredScan value = scoredScan.value;
         System.out.println(scoredScan.key + " " + value);
         }
         */

        handler.performSingleReturnMapReduce(scansKV);

        JavaRDD<KeyValueObject<String, IScoredScan>> scores = handler.getOutput();

          /*
          Drop unmatched scans
         */
        scores = scores.filter(new DropNoMatchScansFilter());

        /**
         * make into tuples
         */
        JavaPairRDD<String, IScoredScan> mappedByScanKey = scores.mapPartitionsToPair(new ScanKeyMapper());

        /**
            *  find the best score
            */
        JavaPairRDD<String, IScoredScan> bestScores = mappedByScanKey.reduceByKey(new chooseBestScanScore());


        /**
         * collect and write
         */
        bestScores = bestScores.sortByKey();

        XTandemMain application = handler.getApplication();
        PepXMLWriter pwrtr = new PepXMLWriter(application);
        PepXMLScoredScanWriter pWrapper = new PepXMLScoredScanWriter(pwrtr);
        SparkConsolidator consolidator = new SparkConsolidator(pWrapper,application);


         String outputPath = BiomlReporter.buildDefaultFileName(application);
         outputPath = outputPath.replace(".xml",".pep.xml");
         Path prepent = XTandemHadoopUtilities.getRelativePath(outputPath);
         FileOutputStream os = null;
         String pathAsString = prepent.toString();




        JavaRDD<IScoredScan> values = bestScores.values();

        PrintWriter out = new PrintWriter(new FileWriter(pathAsString));
        consolidator.writeScores(out, values);
        out.close();

//        JavaRDD<String> text =  consolidator.scoreStrings(values);
//
//
//
//        text = SparkUtilities.realizeAndReturn(text);
//
//        text.saveAsTextFile(pathAsString);


//
//        // comment out unless you want a look
//        // SparkUtilities.realizeAndReturn(values);
//
//
//


//
//        List<Tuple2<String, IScoredScan>> scoredScans = bestScores.collect();
//
//        TandemXMLWriter writer = new TandemXMLWriter(application);
//
//        writer.buildReport(scoredScans);
        


    }


}
