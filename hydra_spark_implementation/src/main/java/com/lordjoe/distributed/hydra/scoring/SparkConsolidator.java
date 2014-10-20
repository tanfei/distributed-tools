package com.lordjoe.distributed.hydra.scoring;

import com.lordjoe.distributed.*;
import org.apache.spark.api.java.*;
import org.apache.spark.api.java.function.*;
import org.systemsbiology.xtandem.*;
import org.systemsbiology.xtandem.scoring.*;

import java.io.*;
import java.util.*;

/**
 * com.lordjoe.distributed.hydra.scoring.SparkConsolidator
 * User: Steve
 * Date: 10/20/2014
 */
public class SparkConsolidator implements Serializable {

    private final ScoredScanWriter writer;
    private final XTandemMain application;

    public SparkConsolidator(final ScoredScanWriter pWriter, final XTandemMain pApplication) {
        writer = pWriter;
        application = pApplication;
    }

    public ScoredScanWriter getWriter() {
        return writer;
    }

    public XTandemMain getApplication() {
        return application;
    }

    /**
     * turn scores into a list of text
     *
     * @param scans
     * @return
     */
    public JavaRDD<String> consolidateScores(JavaRDD<IScoredScan> scans) {
        return scans.map(new Function<IScoredScan, String>() {
            @Override
            public String call(final IScoredScan scan) throws Exception {
                StringBuilder sb = new StringBuilder();
                writer.appendScan(sb, application, scan);
                return sb.toString();
            }
        });
    }


    /**
     * write scores into a file
     *
     * @param out
     * @param scans
     */
    public void writeScores(final Appendable out, JavaRDD<IScoredScan> scans) {
        writer.appendHeader(out, getApplication());

        Iterator<IScoredScan> scanIterator = scans.toLocalIterator();
        while(scanIterator.hasNext())  {
            IScoredScan scan = scanIterator.next();
            writer.appendScan(out, getApplication(), scan);
        }
        writer.appendFooter(out, getApplication());
    }


    /**
     * write scores into a file
     *
     * @param out
     * @param scans
     */
    public JavaRDD<String> scoreStrings(  JavaRDD<IScoredScan> scans) {
        StringBuilder sb = new StringBuilder();
         writer.appendHeader(sb, getApplication());
         List<String> header = new ArrayList<String>();
         header.add(sb.toString());
        JavaSparkContext currentContext = SparkUtilities.getCurrentContext();
        JavaRDD<String> headerRDD = currentContext.parallelize(header);

//        List<IScoredScan> collect = scans.collect();
//        for (IScoredScan scan : collect) {
//            writer.appendScan(out, getApplication(), scan);
//        }

        JavaRDD<String> scanStrings = scans.map(new Function<IScoredScan, String>() {
            @Override
            public String call(final IScoredScan scan) throws Exception {
                StringBuilder sb = new StringBuilder();
                writer.appendScan(sb, getApplication(), scan);
                return sb.toString();
            }
        });

         sb.setLength(0);
         List<String> footer = new ArrayList<String>();
        writer.appendFooter(sb, getApplication());
        footer.add(sb.toString());
        JavaRDD<String> footerRDD = currentContext.parallelize(footer);

        return headerRDD.union(scanStrings).union(footerRDD);

    }



}
