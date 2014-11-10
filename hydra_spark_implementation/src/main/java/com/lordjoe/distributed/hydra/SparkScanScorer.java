package com.lordjoe.distributed.hydra;

import com.lordjoe.distributed.*;
import com.lordjoe.distributed.database.*;
import com.lordjoe.distributed.hydra.fragment.*;
import com.lordjoe.distributed.hydra.scoring.*;
import com.lordjoe.distributed.spectrum.*;
import com.lordjoe.utilities.*;
import org.apache.log4j.*;
import org.apache.spark.api.java.*;
import org.apache.spark.api.java.function.*;
import org.apache.spark.storage.*;
import org.systemsbiology.xtandem.*;
import org.systemsbiology.xtandem.hadoop.*;
import org.systemsbiology.xtandem.peptide.*;
import org.systemsbiology.xtandem.pepxml.*;
import org.systemsbiology.xtandem.reporting.*;
import org.systemsbiology.xtandem.scoring.*;
import scala.*;

import java.io.*;
import java.util.*;


/**
 * com.lordjoe.distributed.hydra.SparkScanScorer
 * Attempt to exploit Spark - not converted Hadoop design for scoring
 * User: Steve
 * Date: 10/7/2014
 */
public class SparkScanScorer {

    public static final String[] InterestingPeptides = {
            "WYEK[79.966]AAGNEDK[79.966]",
            "KH[79.966]FAATEK",
            "GRGVSDNK",
            "LALKAPPSSK",
            "T[79.966]MT[79.966]SFESGMDQESLPK",
            "HAIAVIK"

    };

    public static class FindInterestingPeptides implements ObjectFoundListener<IPolypeptide> {

        /**
         * do something on finding the obejcts
         * for debugging thiis may just find interesting cases
         *
         * @param found
         */
        @Override
        public void onObjectFound(final IPolypeptide found) {
            if (found.isModified())
                System.out.println(found);
            String s = found.toString();
            for (int i = 0; i < InterestingPeptides.length; i++) {
                String interestingPeptide = InterestingPeptides[i];
                if (interestingPeptide.equalsIgnoreCase(s))
                    System.out.println(s); // break here
            }
        }
    }

    public static class FindInterestingBinnedPeptides implements ObjectFoundListener<Tuple2<BinChargeKey, IPolypeptide>> {

        /**
         * do something on finding the obejcts
         * for debugging thiis may just find interesting cases
         *
         * @param found
         */
        @Override
        public void onObjectFound(final Tuple2<BinChargeKey, IPolypeptide> found) {
            String s = found._2().toString();
            for (int i = 0; i < InterestingPeptides.length; i++) {
                String interestingPeptide = InterestingPeptides[i];
                if (interestingPeptide.equalsIgnoreCase(s))
                    System.out.println(s); // break here
            }
        }
    }

    public static class writeScoresMapper extends AbstractLoggingFunction<Tuple2<String, IScoredScan>, Tuple2<String, String>> {
        private boolean logged;

        @Override
        public boolean isLogged() {
            return logged;
        }

        @Override
        public void setLogged(final boolean pLogged) {
            logged = pLogged;
        }

        final BiomlReporter reporter;

        private writeScoresMapper(final BiomlReporter pReporter) {
            reporter = pReporter;
        }


        @Override
        public Tuple2<String, String> doCall(final Tuple2<String, IScoredScan> v1) throws Exception {
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

    public static class DropNoMatchScansFilter extends AbstractLoggingFunction<KeyValueObject<String, IScoredScan>, java.lang.Boolean> {
        private boolean logged;

        @Override
        public boolean isLogged() {
            return logged;
        }

        @Override
        public void setLogged(final boolean pLogged) {
            logged = pLogged;
        }

        @Override
        public java.lang.Boolean doCall(final KeyValueObject<String, IScoredScan> v1) throws Exception {
            IScoredScan vx = v1.value;
            return v1.value.isMatchPresent();
        }
    }

    public static class chooseBestScanScore extends AbstractLoggingFunction2<IScoredScan, IScoredScan, IScoredScan> {
        private boolean logged;

        @Override
        public boolean isLogged() {
            return logged;
        }

        @Override
        public void setLogged(final boolean pLogged) {
            logged = pLogged;
        }

        @Override
        public IScoredScan doCall(final IScoredScan v1, final IScoredScan v2) throws Exception {
            ISpectralMatch match1 = v1.getBestMatch();
            ISpectralMatch match2 = v2.getBestMatch();
            if (match1.getHyperScore() > match2.getHyperScore())
                return v1;
            else
                return v2;
        }
    }

    /**
     * create an output writer
     *
     * @param pApplication
     * @return
     * @throws java.io.IOException
     */
    public static PrintWriter buildWriter(final XTandemMain pApplication) throws IOException {
        String outputPath = BiomlReporter.buildDefaultFileName(pApplication);
        outputPath = outputPath.replace(".xml", ".pep.xml");
        return SparkHydraUtilities.nameToPrintWriter(outputPath, pApplication);
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

        // code to run class loader
        //String runner = SparkUtilities.buildLoggingClassLoaderPropertiesFile(ScanScorer.class  , args);
        //System.out.println(runner);
        ElapsedTimer timer = new ElapsedTimer();
        ElapsedTimer totalTime = new ElapsedTimer();

        if (args.length < TANDEM_CONFIG_INDEX + 1) {
            System.out.println("usage sparkconfig configFile fastaFile");
            return;
        }
        SparkUtilities.readSparkProperties(args[SPARK_CONFIG_INDEX]);

        System.out.println("Set Log to Warn");
        Logger rootLogger = Logger.getRootLogger();
        rootLogger.setLevel(Level.WARN);


        Properties sparkProperties = SparkUtilities.getSparkProperties();
        String pathPrepend = sparkProperties.getProperty("com.lordjoe.distributed.PathPrepend");
        if (pathPrepend != null)
            XTandemHadoopUtilities.setDefaultPath(pathPrepend);


        String configStr = SparkUtilities.buildPath(args[TANDEM_CONFIG_INDEX]);

        String spectra = SparkUtilities.buildPath(args[SPECTRA_INDEX]);

        SparkMapReduceScoringHandler handler = new SparkMapReduceScoringHandler(configStr);


        // handler.buildLibraryIfNeeded();
        // find all polypeptides and modified polypeptides
        JavaRDD<IPolypeptide> databasePeptides = handler.buildLibrary();
        //databasePeptides =  databasePeptides.persist(StorageLevel.MEMORY_AND_DISK());


        // next line is for debugging
        // databasePeptides = SparkUtilities.realizeAndReturn(databasePeptides,new FindInterestingPeptides());
        // System.out.println("Scoring " + databasePeptides.count() + " Peptides");

        // read spectra
        JavaPairRDD<String, IMeasuredSpectrum> scans = SparkSpectrumUtilities.parseSpectrumFile(spectra);
        JavaRDD<IMeasuredSpectrum> spectraToScore = scans.values();

        spectraToScore = spectraToScore.persist(StorageLevel.MEMORY_AND_DISK());
        System.out.println("Scoring " + spectraToScore.count() + " spectra");

        // next line is for debugging
        //  spectraToScore = SparkUtilities.realizeAndReturn(spectraToScore);
        timer.showElapsed("got Spectra to Score");


        // Map peptides into bins
        JavaPairRDD<BinChargeKey, IPolypeptide> keyedPeptides = handler.mapFragmentsToKeys(databasePeptides);

        // next line is for debugging
        //  keyedPeptides =  keyedPeptides.persist(StorageLevel.MEMORY_AND_DISK());
        keyedPeptides = SparkUtilities.realizeAndReturn(keyedPeptides, new FindInterestingBinnedPeptides());

        timer.showElapsed("Mapped Peptides");

        // Map spectra into bins
        JavaPairRDD<BinChargeKey, IMeasuredSpectrum> keyedSpectra = handler.mapMeasuredSpectrumToKeys(spectraToScore);

        // next line is for debugging
        // keyedSpectra = SparkUtilities.realizeAndReturn(keyedSpectra);

        // find spectra-peptide pairs to score
        JavaPairRDD<BinChargeKey, Tuple2<IMeasuredSpectrum, IPolypeptide>> binPairs = keyedSpectra.join(keyedPeptides,
                BinChargeKey.getPartitioner());

        // next line is for debugging
        // binPairs = SparkUtilities.realizeAndReturn(binPairs);
        timer.showElapsed("Joined Pairs");

        binPairs = binPairs.persist(StorageLevel.MEMORY_AND_DISK());

        System.out.println("Pairs to Score " + binPairs.count() + " Pairs");

        Map<BinChargeKey, Object> binChargeKeyObjectMap = binPairs.countByKey();
        for (BinChargeKey binChargeKey : binChargeKeyObjectMap.keySet()) {
            System.out.println(binChargeKey.toString() + " has " + binChargeKeyObjectMap.get(binChargeKey));
        }

        // now produce all peptide spectrum scores where spectrum and peptide are in the same bin
        JavaRDD<IScoredScan> bestScores = handler.scoreBinPairs(binPairs);

        // next line is for debugging
        //bestScores = SparkUtilities.realizeAndReturn(bestScores);

        timer.showElapsed("built best scores");
        //bestScores =  bestScores.persist(StorageLevel.MEMORY_AND_DISK());
        // System.out.println("Total Scores " + bestScores.count() + " Scores");

        XTandemMain application = handler.getApplication();
        PepXMLWriter pwrtr = new PepXMLWriter(application);
        PepXMLScoredScanWriter pWrapper = new PepXMLScoredScanWriter(pwrtr);
        SparkConsolidator consolidator = new SparkConsolidator(pWrapper, application);


        PrintWriter out = buildWriter(application);
        consolidator.writeScores(out, bestScores);
        out.close();

        totalTime.showElapsed("Finished Scoring");


    }

    public static void oldCodeToLookAt(SparkMapReduceScoringHandler handler,
                                       JavaPairRDD<String, IMeasuredSpectrum> scans
    ) throws Exception {

        // we need Tuple2 not KeyValueObject
        JavaRDD<KeyValueObject<String, IMeasuredSpectrum>> scansKV = SparkUtilities.fromTuples(scans);

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
        SparkConsolidator consolidator = new SparkConsolidator(pWrapper, application);


        JavaRDD<IScoredScan> values = bestScores.values();

        PrintWriter out = buildWriter(application);
        consolidator.writeScores(out, values);
        out.close();

    }


}
