package com.lordjoe.distributed.tandem;

import com.lordjoe.distributed.*;
import com.lordjoe.distributed.context.*;
import com.lordjoe.distributed.hydra.protein.*;
import com.lordjoe.distributed.hydra.scoring.*;
import com.lordjoe.distributed.spectrum.*;
import org.apache.hadoop.fs.*;
import org.apache.spark.api.java.*;
import org.systemsbiology.xtandem.*;
import org.systemsbiology.xtandem.hadoop.*;

/**
 * com.lordjoe.distributed.tandem.LibraryBuilder
 * User: Steve
 * Date: 9/24/2014
 */
public class LibraryBuilder {

    private final SparkApplicationContext context;
    private final XTandemMain application;
    private final SparkMapReduceScoringHandler handler;

    public LibraryBuilder(SparkMapReduceScoringHandler handler) {
        this.handler = handler;
        context = handler.getContext();
        application = handler.getApplication();
    }

    public SparkApplicationContext getContext() {
        return context;
    }

     public JavaSparkContext getJavaContext() {
        SparkApplicationContext context1 = getContext();
        return context1.getCtx();
    }


    public XTandemMain getApplication() {
        return application;
    }

    public void buildLibrary() {
        // if not commented out this line forces proteins to be realized
        //    proteins = SparkUtilities.realizeAndReturn(proteins, ctx);

        XTandemMain app = getApplication();
        ProteinMapper pm = new ProteinMapper(app);
        ProteinReducer pr = new ProteinReducer(app);

        //       ListKeyValueConsumer<String,String> consumer = new ListKeyValueConsumer();
        //noinspection unchecked
        SparkMapReduce handler = new SparkMapReduce(getContext().getSparkConf(),"LibraryBuilder", pm, pr, IPartitionFunction.HASH_PARTITION);
        JavaSparkContext ctx = handler.getCtx();

        String fasta = app.getDatabaseName();
        Path defaultPath = XTandemHadoopUtilities.getDefaultPath();
        fasta = defaultPath.toString() + "/" + fasta + ".fasta";

        JavaPairRDD<String, String> parsed = SparkSpectrumUtilities.parseFastaFile(fasta, ctx);

        // if not commented out this line forces proteins to be realized
        //   parsed = SparkUtilities.realizeAndReturn(parsed, ctx);
         JavaRDD<KeyValueObject<String, String>> proteins = SparkUtilities.fromTuples(parsed);


        //  proteins = proteins.persist(StorageLevel.MEMORY_ONLY());
           proteins = SparkUtilities.realizeAndReturn(proteins, ctx);

        //noinspection unchecked
        handler.performSourceMapReduce(proteins);

        //noinspection unchecked
        Iterable<KeyValueObject<String, String>> list = handler.collect();

        for (KeyValueObject<String, String> keyValueObject : list) {
            System.out.println(keyValueObject);
        }


    }

//    public static void main(String[] args) {
//        if (args.length == 0) {
//            System.out.println("usage configFile fastaFile");
//            return;
//        }
//        File config = new File(args[0]);
//        String fasta = args[1];
//
//
//        LibraryBuilder lb = new LibraryBuilder(config);
//
//        lb.buildLibrary();
//    }
}
