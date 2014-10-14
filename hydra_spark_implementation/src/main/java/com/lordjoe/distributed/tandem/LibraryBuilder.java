package com.lordjoe.distributed.tandem;

import com.lordjoe.distributed.*;
import com.lordjoe.distributed.context.*;
import com.lordjoe.distributed.hydra.protein.*;
import com.lordjoe.distributed.hydra.protein.ProteinMapper;
import com.lordjoe.distributed.hydra.protein.ProteinReducer;
import com.lordjoe.distributed.hydra.scoring.*;
import com.lordjoe.distributed.protein.*;
import com.lordjoe.distributed.spectrum.*;
import org.apache.hadoop.fs.*;
import org.apache.spark.api.java.*;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.*;
import org.systemsbiology.xtandem.*;
import org.systemsbiology.xtandem.hadoop.*;
import org.systemsbiology.xtandem.peptide.*;
import scala.*;

/**
 * com.lordjoe.distributed.tandem.LibraryBuilder
 * User: Steve
 * Date: 9/24/2014
 */
public class LibraryBuilder {

    private final SparkApplicationContext context;
    private final XTandemMain application;
    private final SparkMapReduceScoringHandler tophandler;
    private final SparkMapReduce handler;

    public LibraryBuilder(SparkMapReduceScoringHandler pHandler) {
        this.tophandler = pHandler;
        context = pHandler.getContext();
        application = pHandler.getApplication();
        ProteinMapper pm = new ProteinMapper(application);
         ProteinReducer pr = new ProteinReducer(application);
         handler = new SparkMapReduce(getContext().getSparkConf(),"LibraryBuilder", pm, pr, IPartitionFunction.HASH_PARTITION);
    }

    public SparkApplicationContext getContext() {
        return context;
    }

     public JavaSparkContext getJavaContext() {
        SparkApplicationContext context1 = getContext();
        return context1.getCtx();
    }

    /**
     * generate an RDD of proteins from the database
     * @return
     */
    public  JavaRDD<IProtein>  readProteins()
    {
        JavaSparkContext ctx = handler.getCtx();

         String fasta = getApplication().getDatabaseName();
         Path defaultPath = XTandemHadoopUtilities.getDefaultPath();
         fasta = defaultPath.toString() + "/" + fasta + ".fasta";

         // this is a list of proteins the key is the annotation line
        // the value is the sequence
         JavaPairRDD<String, String> parsed = SparkSpectrumUtilities.parseFastaFile(fasta, ctx);
           // if not commented out this line forces proteins to be realized
           parsed = SparkUtilities.realizeAndReturn(parsed, ctx);
         return parsed.map(new parsedProteinToProtein()) ;
     }


    public XTandemMain getApplication() {
        return application;
    }

    public void buildLibrary() {
        // if not commented out this line forces proteins to be realized
        //    proteins = SparkUtilities.realizeAndReturn(proteins, ctx);

        XTandemMain app = getApplication();


        JavaRDD<IProtein> proteins =  readProteins();

        // uncomment when you want to look
        proteins = SparkUtilities.realizeAndReturn(proteins, getJavaContext());

        // Digest
        JavaRDD<IPolypeptide> digested =  proteins.flatMap(new DigestProteinFunction(app));

        // Peptide Sequence is the key
        JavaPairRDD<String,IPolypeptide> bySequence =  digested.mapToPair(new PairFunction<IPolypeptide, String, IPolypeptide>( ) {
            @Override
            public Tuple2<String, IPolypeptide> call(final IPolypeptide t) throws Exception {
                return new Tuple2<String, IPolypeptide>(t.getSequence(),t);
            }
        });

          // Peptide Sequence is the key
        bySequence = PolypeptideCombiner.combineIdenticalPolyPeptides(bySequence);

          // uncomment when you want to look
        bySequence = SparkUtilities.realizeAndReturn(bySequence, getJavaContext());

        // Peptide Sequence is the key
        JavaPairRDD<Integer,IPolypeptide> byMZ =  bySequence.mapToPair(new PeptideByStringToByMass());


          // uncomment when you want to look
        byMZ = SparkUtilities.realizeAndReturn(byMZ, getJavaContext());




//        //  proteins = proteins.persist(StorageLevel.MEMORY_ONLY());
//        //   proteins = SparkUtilities.realizeAndReturn(proteins, ctx);
//
//        //noinspection unchecked
//        handler.performSourceMapReduce(proteins);
//
//        JavaRDD<KeyValueObject<String, String>> fragments = handler.getOutput();
//
//        /**
//         * now merge fragments and remember all proteins
//         */
//        JavaPairRDD<String, String> tuples = SparkUtilities.toTuples(fragments);
//
//
//    //    JavaPairRDD<String, String> merged = tuples.combineByKey(StringCombiner.STARTER,StringCombiner.CONTINUER,StringCombiner.ENDER);
//
//     //   JavaRDD<KeyValueObject<String, String>> finalFragments = SparkUtilities.fromTuples(merged);
//
//        //noinspection unchecked
//        Iterable<KeyValueObject<String, String>> list = finalFragments.collect();
//
//        for (KeyValueObject<String, String> keyValueObject : list) {
//            System.out.println(keyValueObject);
//        }


    }

    public static class parsedProteinToProtein implements Function<Tuple2<String, String>, IProtein> {
        @Override
        public IProtein call(final Tuple2<String, String> v1) throws Exception {
            String annotation = v1._1();
            String sequence = v1._2();
            String id = Protein.idFromAnnotation(annotation);
            return Protein.getProtein(id, annotation, sequence, null);

        }
    }

    private static class PeptideByStringToByMass implements PairFunction<Tuple2<String, IPolypeptide>, Integer, IPolypeptide> {

        @Override
        public Tuple2<Integer, IPolypeptide> call(final Tuple2<String, IPolypeptide> t) throws Exception {
            IPolypeptide pp = t._2();
            Integer mz = (int)pp.getMatchingMass(); // todo make more precise
            return new Tuple2(mz,pp);
        }
    }


}
