package com.lordjoe.distributed.input;

import com.lordjoe.distributed.*;
import com.lordjoe.distributed.spectrum.*;
import org.apache.spark.*;
import org.apache.spark.api.java.*;
import org.systemsbiology.xtandem.*;

/**
 * com.lordjoe.distributed.input.ScanReaderTest
 * User: Steve
 * Date: 9/24/2014
 */
public class ScanReaderTest {


    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("usage <file holding mgfs>");
            return;
        }
        SparkConf sparkConf = new SparkConf().setAppName("JavaWordCount");
        SparkUtilities.guaranteeSparkMaster(sparkConf);    // use local if no master provided

        JavaSparkContext ctx = new JavaSparkContext(sparkConf);

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg.endsWith(".fasta")) {
                JavaPairRDD<String, String> parsed = SparkSpectrumUtilities.parseFastaFile(arg, ctx);
                SparkUtilities.showPairRDD(parsed);

            }
            else {
                JavaPairRDD<String, IMeasuredSpectrum> parsed = SparkSpectrumUtilities.parseSpectrumFile(args[i], ctx);
                SparkUtilities.showPairRDD(parsed);
            }

        }


    }

}
