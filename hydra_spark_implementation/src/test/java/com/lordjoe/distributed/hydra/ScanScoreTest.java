package com.lordjoe.distributed.hydra;

import com.lordjoe.distributed.*;
import com.lordjoe.distributed.hydra.scoring.*;
import com.lordjoe.distributed.spectrum.*;
import com.lordjoe.distributed.tandem.*;
import org.apache.spark.api.java.*;
import org.systemsbiology.xtandem.*;

import java.io.*;

/**
 * com.lordjoe.distributed.input.hydra.ScanScoreTest
 * User: Steve
 * Date: 10/7/2014
 */
public class ScanScoreTest {

    /**
     * call with args like or20080320_s_silac-lh_1-1_11short.mzxml in Sample2
     *
     * @param args
     */
    public static void main(String[] args) {

        if (args.length == 0) {
            System.out.println("usage configFile fastaFile");
            return;
        }
        File config = new File(args[0]);
        String spectra = args[1];
        LibraryBuilder lb = new LibraryBuilder(config);

        SparkMapReduceScoringHandler handler = new SparkMapReduceScoringHandler(config);
        JavaSparkContext ctx = handler.getJavaContext();


        JavaPairRDD<String, IMeasuredSpectrum> scans = SparkSpectrumUtilities.parseSpectrumFile(spectra, ctx);
        JavaRDD<KeyValueObject<String, IMeasuredSpectrum>> scansKV = SparkUtilities.fromTuples(scans);

        scansKV = SparkUtilities.realizeAndReturn(scansKV, ctx);
        handler.performSourceMapReduce(scansKV);


    }

}
