package com.lordjoe.distributed.tandem;

import com.lordjoe.distributed.*;
import com.lordjoe.distributed.database.*;
import com.lordjoe.distributed.hydra.peptide.*;
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
import org.apache.spark.sql.api.java.*;
import org.systemsbiology.xtandem.*;
import org.systemsbiology.xtandem.hadoop.*;
import org.systemsbiology.xtandem.peptide.*;
import scala.*;

import java.lang.Boolean;
import java.util.*;

/**
 * com.lordjoe.distributed.tandem.LibraryBuilder
 * User: Steve
 * Date: 9/24/2014
 */
public class LibraryBuilder implements Serializable {

    private final XTandemMain application;

    public LibraryBuilder(SparkMapReduceScoringHandler pHandler) {
        application = pHandler.getApplication();
        ProteinMapper pm = new ProteinMapper(application);
        ProteinReducer pr = new ProteinReducer(application);
    }

//  //  public SparkApplicationContext getContext() {
//        return context;
//    }

//     public JavaSparkContext getJavaContext() {
//        SparkApplicationContext context1 = getContext();
//        return context1.getCtx();
//    }

    /**
     * generate an RDD of proteins from the database
     *
     * @return
     */
    public JavaRDD<IProtein> readProteins(JavaSparkContext ctx) {
        //      JavaSparkContext ctx = getJavaContext();


        String fasta = getApplication().getDatabaseName();
        Path defaultPath = XTandemHadoopUtilities.getDefaultPath();
        fasta = defaultPath.toString() + "/" + fasta + ".fasta";

        // this is a list of proteins the key is the annotation line
        // the value is the sequence
        JavaPairRDD<String, String> parsed = SparkSpectrumUtilities.parseFastaFile(fasta, ctx);

        // if not commented out this line forces proteins to be realized
        //  parsed = SparkUtilities.realizeAndReturn(parsed, ctx);
        return parsed.map(new parsedProteinToProtein());
    }


    public XTandemMain getApplication() {
        return application;
    }

    public void buildLibrary(JavaSparkContext jctx) {
        // if not commented out this line forces proteins to be realized
        //    proteins = SparkUtilities.realizeAndReturn(proteins, ctx);

        XTandemMain app = getApplication();


        JavaRDD<IProtein> proteins = readProteins(jctx);

        // uncomment when you want to look
        // proteins = SparkUtilities.realizeAndReturn(proteins, jctx);

        // Digest
        JavaRDD<IPolypeptide> digested = proteins.flatMap(new DigestProteinFunction(app));

        // uncomment when you want to look
        //  digested = SparkUtilities.realizeAndReturn(digested, jctx);


        // Peptide Sequence is the key
        JavaPairRDD<String, IPolypeptide> bySequence = digested.mapToPair(new MapPolyPeptideToSequenceKeys());

        // uncomment when you want to look
        // bySequence = SparkUtilities.realizeAndReturn(bySequence, jctx);

        // Peptide Sequence is the key
        bySequence = PolypeptideCombiner.combineIdenticalPolyPeptides(bySequence);

        // uncomment when you want to look
        // bySequence = SparkUtilities.realizeAndReturn(bySequence, jctx);


        // Peptide Sequence is the key
        JavaPairRDD<Integer, IPolypeptide> byMZ = bySequence.mapToPair(new PeptideByStringToByMass());


        // uncomment when you want to look
        //  byMZ = SparkUtilities.realizeAndReturn(byMZ, jctx);

        saveAsDatabase(byMZ);
        //saveAsFiles(byMZ);


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

    protected void saveAsDatabase(final JavaPairRDD<Integer, IPolypeptide> pByMZ) {
        JavaRDD<IPolypeptide> peptides = pByMZ.map(SparkUtilities.TUPLE_VALUES);
        JavaRDD<PeptideSchemaBean> beans = peptides.map(PeptideSchemaBean.TO_BEAN);
        String dbName = buildDatabaseName();

        // uncomment to see what is going on
        // beans = SparkUtilities.realizeAndReturn(beans);

        DatabaseUtilities.buildParaquetDatabase(dbName, beans, PeptideSchemaBean.class);
    }

    protected String buildDatabaseName() {
        String fasta = getApplication().getDatabaseName();
        Path defaultPath = XTandemHadoopUtilities.getDefaultPath();
        return defaultPath.toString() + "/" + fasta + ".parquet";
    }

    /**
     * build text files to save the library
     *
     * @param pByMZ
     */
    protected void saveAsFiles(final JavaPairRDD<Integer, IPolypeptide> pByMZ) {
        JavaPairRDD<Integer, LibraryWriter.WriterObject> files = LibraryWriter.writeDatabase(pByMZ);

        // close all files as a side effect - return nothing
        files = files.filter(new Function<Tuple2<Integer, LibraryWriter.WriterObject>, Boolean>() {
            @Override
            public Boolean call(final Tuple2<Integer, LibraryWriter.WriterObject> v1) throws Exception {
                v1._2().close();
                return false;
            }
        });

        // because of the filter there is nothing there
        files.collect();
    }

    public Map<Integer, Integer> getDatabaseSizes() {
        JavaSparkContext sc = SparkUtilities.getCurrentContext();
        JavaSQLContext sqlContext = new JavaSQLContext(sc);
           // Read in the Parquet file created above.  Parquet files are self-describing so the schema is preserved.
           // The result of loading a parquet file is also a JavaSchemaRDD.
           String dbName = buildDatabaseName();
           JavaSchemaRDD parquetFile = sqlContext.parquetFile(dbName);
            //Parquet files can also be registered as tables and then used in SQL statements.
           parquetFile.registerAsTable("peptides");
           JavaSchemaRDD binCounts = sqlContext.sql("SELECT massBin,COUNT(massBin) FROM " +  "peptides" + "  GROUP BY  massBin");
           final Map<Integer, Integer> ret = new HashMap<Integer, Integer>() ;
        JavaRDD<Tuple2<Integer, Integer>> counts = binCounts.map(new Function<Row, Tuple2<Integer, Integer>>() {
            public Tuple2<Integer, Integer> call(Row row) {
                int mass = row.getInt(0);
                int count = (int) row.getLong(1);
                ret.put(mass, count);
                return new Tuple2<Integer, Integer>(mass, count);
            }
        });
        for (Tuple2<Integer,Integer> countTuple : counts.collect()) {
           ret.put(countTuple._1(),countTuple._2());
        }
            return ret;
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


    public static class processByKey implements PairFunction<Tuple2<String, IPolypeptide>, Integer, IPolypeptide> {

        private transient List<String> lines;
        private transient String currentKey;

        @Override
        public Tuple2<Integer, IPolypeptide> call(final Tuple2<String, IPolypeptide> t) throws Exception {
            String key = t._1();
            if (!key.equals(currentKey))
                processNewKey(key);
            IPolypeptide pp = t._2();
            Integer mz = (int) pp.getMatchingMass(); // todo make more precise
            return new Tuple2(mz, pp);
        }

        private JavaSparkContext getCurrentContext() {
            throw new UnsupportedOperationException("Then a Miracle Happens"); // ToDo   How do I do this
        }

        private void processNewKey(final String pKey) {
            JavaSparkContext ctx = getCurrentContext();
            String path = pKey + ".data";
            JavaRDD<String> linesRDD = ctx.textFile(path);
            lines = linesRDD.collect();
            currentKey = pKey;
        }
    }


    private static class PeptideByStringToByMass implements PairFunction<Tuple2<String, IPolypeptide>, Integer, IPolypeptide> {


        @Override
        public Tuple2<Integer, IPolypeptide> call(final Tuple2<String, IPolypeptide> t) throws Exception {
            IPolypeptide pp = t._2();
            Integer mz = (int) pp.getMatchingMass(); // todo make more precise
            return new Tuple2(mz, pp);
        }
    }


    private static class MapPolyPeptideToSequenceKeys implements PairFunction<IPolypeptide, String, IPolypeptide> {
        @Override
        public Tuple2<String, IPolypeptide> call(final IPolypeptide t) throws Exception {
            return new Tuple2<String, IPolypeptide>(t.getSequence(), t);
        }
    }
}
