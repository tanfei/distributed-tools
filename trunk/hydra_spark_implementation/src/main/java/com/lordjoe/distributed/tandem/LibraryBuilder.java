package com.lordjoe.distributed.tandem;

import com.lordjoe.distributed.*;
import com.lordjoe.distributed.context.*;
import com.lordjoe.distributed.protein.*;
import com.lordjoe.distributed.spectrum.*;
import org.apache.spark.api.java.*;
import org.systemsbiology.xtandem.*;

import java.io.*;

/**
 * com.lordjoe.distributed.tandem.LibraryBuilder
 * User: Steve
 * Date: 9/24/2014
 */
public class LibraryBuilder {

    private final SparkContext context;
       private final XTandemMain application;

    public LibraryBuilder(File congiguration) {
        context = new SparkContext("LibraryBuilder");
        SparkUtilities.guaranteeSparkMaster(context.getSparkConf());    // use local if no master provided

        application = new XTandemMain(congiguration);
    }

    public SparkContext getContext() {
        return context;
    }

    public JavaSparkContext getJavaContext() {
        SparkContext context1 = getContext();
        return context1.getCtx();
    }


    public XTandemMain getApplication() {
        return application;
    }

    public static void main(String[] args) {
        if(args.length == 0)    {
             System.out.println("usage configFile fastaFile");
             return;
         }
        File config = new File(args[0]);
        String fasta = args[1] ;
         LibraryBuilder lb = new LibraryBuilder(config);

        JavaSparkContext ctx = lb.getJavaContext();
        JavaPairRDD<String, String> parsed = SparkSpectrumUtilities.parseFastaFile(fasta, ctx);

      // if not commented out this line forces proteins to be realized
        parsed = SparkUtilities.realizeAndReturn(parsed, ctx);

        JavaRDD<KeyValueObject<String, String>> proteins = SparkUtilities.fromTuples(parsed);

     // if not commented out this line forces proteins to be realized
         proteins = SparkUtilities.realizeAndReturn(proteins, ctx);

        ProteinMapper pm = new ProteinMapper(lb.getApplication());
        ProteinReducer pr = new ProteinReducer(lb.getApplication());

        SparkMapReduce handler = new SparkMapReduce(pm, pr );


       //  proteins = proteins.persist(StorageLevel.MEMORY_ONLY());
     //   proteins = SparkUtilities.realizeAndReturn(proteins, ctx);

        handler.performSourceMapReduce(proteins);

        Iterable<KeyValueObject<String, String>> list = handler.collect();

        for (KeyValueObject<String, String> keyValueObject : list) {
            System.out.println(keyValueObject);
        }


    }
}
