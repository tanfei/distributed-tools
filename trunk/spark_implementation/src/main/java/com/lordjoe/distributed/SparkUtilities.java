package com.lordjoe.distributed;

import com.lordjoe.distributed.database.*;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.fs.FileSystem;
import org.apache.spark.*;
import org.apache.spark.api.java.*;
import org.apache.spark.api.java.function.*;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.sql.api.java.*;
import org.apache.spark.storage.*;
import org.systemsbiology.common.*;
import org.systemsbiology.hadoop.*;
import scala.*;

import javax.annotation.*;
import java.io.*;
import java.io.Serializable;
import java.util.*;

/**
 * com.lordjoe.distributed.SpareUtilities
 * A very useful class representing a number of static functions useful in Spark
 * User: Steve
 * Date: 8/28/2014
 */
public class SparkUtilities implements Serializable {

    //  private transient static ThreadLocal<JavaSparkContext> threadContext;
    private transient static JavaSparkContext threadContext;
    //  private transient static ThreadLocal<JavaSQLContext> threadContext;
    private transient static JavaSQLContext sqlContext;
    private static final Properties sparkProperties = new Properties();
    private static String appName = "Anonymous";
    private static String pathPrepend = "";
    private static boolean local;

    public static boolean isLocal() {
        return local;
    }

    public static void setLocal(final boolean pLocal) {
        local = pLocal;
    }

    public static final int DEFAULT_NUMBER_PARTITIONS = 20;
    private static int defaultNumberPartitions = DEFAULT_NUMBER_PARTITIONS;

    public static int getDefaultNumberPartitions() {
        return defaultNumberPartitions;
    }

    public static void setDefaultNumberPartitions(final int pDefaultNumberPartitions) {
        defaultNumberPartitions = pDefaultNumberPartitions;
    }

    public static synchronized JavaSQLContext getCurrentSQLContext() {
        if (sqlContext != null)
            return sqlContext;

        sqlContext = new JavaSQLContext(getCurrentContext());
        return sqlContext;
    }


    public static synchronized Configuration getHadoopConfiguration() {
        Configuration configuration = getCurrentContext().hadoopConfiguration();
        return configuration;
    }


    /**
     * tuen an RDD of Tuples into a JavaPairRdd
     * @param imp
     * @param <K>
     * @param <V>
     * @return
     */
     public static <K,V> JavaPairRDD<K,V>  mapToPairs(JavaRDD<Tuple2<K,V>> imp)   {
         return imp.mapToPair(new PairFunction<Tuple2<K, V>, K, V>() {
             @Override
             public Tuple2<K, V> call(final Tuple2<K, V> t) throws Exception {
                 return t;
             }
         });
     }

    /**
     * convert a JavaPairRDD into onw with the tuples so that combine by key can know the key
     * @param imp
     * @param <K>
     * @param <V>
     * @return
     */
     public static <K,V> JavaPairRDD<K,Tuple2<K,V>>  mapToKeyedPairs(JavaPairRDD< K,V> imp)   {
         return imp.mapToPair(new PairFunction<Tuple2<K, V>, K, Tuple2<K, V>>() {
             @Override
             public Tuple2<K, Tuple2<K, V>> call(final Tuple2<K, V> t) throws Exception {
                 return new Tuple2<K, Tuple2<K, V>>(t._1(), t);
             }
         });
      }

    /**
     * dump all spark properties to System.err
     */
    public static void showSparkProperties()
    {
        showSparkProperties(System.err);
    }


    /**
     * dump all spark properties to out
     * @param out
     */
    public static void showSparkProperties(Appendable out)
    {
        try {
            SparkConf sparkConf = new SparkConf();
            Tuple2<String, String>[] all = sparkConf.getAll();
            for (Tuple2<String, String> prp  : all) {
                out.append(prp._1().toString() + "=" + prp._2());
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);

        }
    }

    public static void  showSparkPropertiesInAnotherThread()
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                showSparkProperties();
            }
        }).start();
    }

    /**
     * create a JavaSparkContext for the thread if none exists
     *
     * @return
     */
    public static synchronized JavaSparkContext getCurrentContext() {
//        if (threadContext == null)
//            threadContext = new ThreadLocal<JavaSparkContext>();
//        JavaSparkContext ret = threadContext.get();
        JavaSparkContext ret = threadContext;
        if (ret != null)
            return ret;
        SparkConf sparkConf = new SparkConf();
         sparkConf.setAppName(getAppName());
        SparkUtilities.guaranteeSparkMaster(sparkConf);
        SparkContext sc = new SparkContext(sparkConf);


        // what are we using as a serializer
        //showOption("spark.serializer",sparkConf);

        Option<String> option = sparkConf.getOption("spark.serializer");
        if (true || !option.isDefined())
            sparkConf.set("spark.serializer", "org.apache.spark.serializer.JavaSerializer");   // todo use kryo
//        else {
//             if(option.get().equals("org.apache.spark.serializer.KryoSerializer"))
//                   sparkConf.set("spark.kryo.registrator", "com.lordjoe.distributed.hydra.HydraKryoSerializer");
//           }
        // if we use Kryo register classes


         sparkConf.set("spark.mesos.coarse", "true");
        sparkConf.set("spark.executor.memory", "2500m");


        //

//        option = sparkConf.getOption("spark.default.parallelism");
//        if (option.isDefined())
//            System.err.println("Parellelism = " + option.get());
//
//        option = sparkConf.getOption("spark.executor.heartbeatInterval");
//        if (option.isDefined())
//            System.err.println("timeout = " + option.get());


        ret = new JavaSparkContext(sparkConf);
        threadContext = ret;
        //      threadContext.set(ret);
        return ret;
    }


    public static void showOption(String optionName, SparkConf sparkConf) {
        Option<String> option = sparkConf.getOption(optionName);
        if (option.isDefined())
            System.err.println(optionName + "=" + getOption(optionName, sparkConf));
        else
            System.err.println(optionName + "= undefined");
    }

    public static String getOption(String optionName, SparkConf sparkConf) {
        Option<String> option = sparkConf.getOption(optionName);
        if (option.isDefined())
            return option.get();
        else
            return null;
    }

    /**
     * return the name of the current App
     *
     * @return
     */
    public static String getAppName() {
        return appName;
    }

    public static void setAppName(final String pAppName) {
        appName = pAppName;
    }

    public static Properties getSparkProperties() {
        return sparkProperties;
    }

    /**
     * return the content of an existing file in the path
     *
     * @param path
     * @return the content of what is presumed to be a text file as an strings  one per line
     */
    public static
    @Nonnull
    String[] pathLines(@Nonnull String path) {
        String wholeFile = getPathContent(path);
        return wholeFile.split("\n");
    }

    /**
     * return the content of an existing file in the path
     *
     * @param path
     * @return the content of what is presumed to be a text file as a string
     */
    public static
    @Nonnull
    String getPathContent(@Nonnull String path) {
        IFileSystem accessor = getHadoopFileSystem();
        path = mapToPath(path); // make sure we understand the path
        return accessor.readFromFileSystem(path);
    }


    /**
     * a string prepended to the path =
     * might be   hdfs://daas/steve/Sample2/
     * usually reflects a mapping from user.dir to whatever files Spark is using
     * - I assume hdfs
     *
     * @return
     */
    public static String getPathPrepend() {
        return pathPrepend;
    }


    public static void setPathPrepend(final String pPathPrepend) {
        pathPrepend = pPathPrepend;
    }

    public static String mapToPath(String cannonicPath) {
        return getPathPrepend() + cannonicPath;
    }

    /**
     * return a reference to the current hadoop file system using the currrent Spark Context
     */
    public static IFileSystem getHadoopFileSystem() {
        final IFileSystem accessor;
        try {
            JavaSparkContext ctx = getCurrentContext();
            Configuration entries = ctx.hadoopConfiguration();
            accessor = new HDFSAccessor(FileSystem.get(entries));
        }
        catch (IOException e) {
            throw new RuntimeException(e);

        }
        return accessor;
    }

    /**
     * read a path and return it as a LineNumber reader of the content
     * Needed to fake reading a file
     *
     * @param ctx
     * @param path
     * @return
     */
    public static InputStream readFrom(String path) {
        IFileSystem accessor = getHadoopFileSystem();
        path = mapToPath(path); // make sure we understand the path
        return accessor.openPath(path);
    }

    /**
     * read a file with a list of desired properties
     *
     * @param fileName
     * @return
     */
    public static void readSparkProperties(String fileName) {
        try {
            File f = new File(fileName);
            String path = f.getAbsolutePath();
            sparkProperties.load(new FileReader(f));  // read spark properties
        }
        catch (IOException e) {
            throw new RuntimeException(" bad spark properties file " + fileName);

        }
    }

    /**
     * if no spark master is  defined then use "local
     *
     * @param sparkConf the configuration
     */
    public static void guaranteeSparkMaster(@Nonnull SparkConf sparkConf) {
        Option<String> option = sparkConf.getOption("spark.master");

        if (!option.isDefined()) {   // use local over nothing   {
            sparkConf.setMaster("local[*]");
            setLocal(true);
            /**
             * liquanpei@gmail.com suggests to correct
             * 14/10/08 09:36:35 ERROR broadcast.TorrentBroadcast: Reading broadcast variable 0 failed
             14/10/08 09:36:35 INFO broadcast.TorrentBroadcast: Reading broadcast variable 0 took 5.006378813 s
             14/10/08 09:36:35 INFO broadcast.TorrentBroadcast: Started reading broadcast variable 0
             14/10/08 09:36:35 ERROR executor.Executor: Exception in task 0.0 in stage 0.0 (TID 0)
             java.lang.NullPointerException
             at java.nio.ByteBuffer.wrap(ByteBuffer.java:392)
             at org.apache.spark.scheduler.ResultTask.runTask(ResultTask.scala:58)

             */
            //  sparkConf.set("spark.broadcast.factory","org.apache.spark.broadcast.HttpBroadcastFactory" );
        }
        else {
            setLocal(option.get().startsWith("local"));
        }
        // ste all properties in the SparkProperties file
        for (String property : sparkProperties.stringPropertyNames()) {
            if (!property.startsWith("spark."))
                continue;
            sparkConf.set(property, sparkProperties.getProperty(property));

        }

    }

    /**
     * read a stream into memory and return it as an RDD
     * of lines
     *
     * @param is the stream
     * @param sc the configuration
     * @return
     */
    @SuppressWarnings("UnusedDeclaration")
    public static JavaRDD<String> fromInputStream(@Nonnull InputStream is, @Nonnull JavaSparkContext sc) {
        try {
            List<String> lst = new ArrayList<String>();
            BufferedReader rdr = new BufferedReader(new InputStreamReader(is));
            String line = rdr.readLine();
            while (line != null) {
                lst.add(line);
                line = rdr.readLine();
            }
            rdr.close();
            return sc.parallelize(lst);
        }
        catch (IOException e) {
            throw new RuntimeException(e);

        }
    }


    /**
     * read a stream into memory and return it as an RDD
     * of lines
     *
     * @param is the stream
     * @param sc the configuration
     * @return
     */
    public static JavaRDD<KeyValueObject<String, String>> keysFromInputStream(@Nonnull String key, @Nonnull InputStream is, @Nonnull JavaSparkContext sc) {
        try {
            List<KeyValueObject<String, String>> lst = new ArrayList<KeyValueObject<String, String>>();
            BufferedReader rdr = new BufferedReader(new InputStreamReader(is));
            String line = rdr.readLine();
            while (line != null) {
                lst.add(new KeyValueObject<String, String>(key, line));
                line = rdr.readLine();
            }
            return sc.parallelize(lst);
        }
        catch (IOException e) {
            throw new RuntimeException(e);

        }
    }

    public static final String PATH_PREPEND_PROPERTY = "com.lordjoe.distributed.PathPrepend";

    /**
     * @param pathName given path - we may need to predend hdfs access
     * @param props
     * @return
     */
    public static String buildPath(final String pathName) {
        if (pathName.startsWith("hdfs://"))
            return pathName;
        String prepend = sparkProperties.getProperty(PATH_PREPEND_PROPERTY);
        if (prepend == null)
            return pathName;
        return prepend + pathName;
    }


    /**
     * function that returns the values of a Tuple as an RDD
     */
    public static final TupleValues TUPLE_VALUES = new TupleValues();

    public static class TupleValues<K extends Serializable> extends AbstractLoggingFunction<Tuple2<Object, K>, K> {
        private TupleValues() {
        }

        ;

        @Override
        public K doCall(final Tuple2<Object, K> v1) throws Exception {
            return v1._2();
        }
    }

    /**
     * function that returns the original object
     */
    public static final IdentityFunction IDENTITY_FUNCTION = new IdentityFunction();

    public static class IdentityFunction<K extends Serializable> extends AbstractLoggingFunction<K, K> {
        private IdentityFunction() {
        }

        @Override
        public K doCall(final K v1) throws Exception {
            return v1;
        }
    }


    public static class KeyValueObjectToTuple2<K extends Serializable, V extends Serializable> implements FlatMapFunction2<KeyValueObject<K, V>, K, V> {
        @Override
        public Iterable<V> call(final KeyValueObject<K, V> ppk, final K pK) throws Exception {
            Object[] items = {ppk.value};
            return Arrays.asList((V[]) items);
        }

    }

    /**
     * convert anRDD of KeyValueObject to a JavaPairRDD of keys and values
     *
     * @param inp input RDD
     * @param <K> key
     * @param <V> value
     * @return
     */
    @Nonnull
    public static <K extends Serializable, V extends Serializable> JavaPairRDD<K, V> toTuples(@Nonnull JavaRDD<KeyValueObject<K, V>> inp) {
        PairFunction<KeyValueObject<K, V>, K, V> pf = new AbstractLoggingPairFunction<KeyValueObject<K, V>, K, V>() {
            @Override
            public Tuple2<K, V> doCall(KeyValueObject<K, V> kv) {
                return new Tuple2<K, V>(kv.key, kv.value);
            }
        };
        return inp.mapToPair(pf);
    }

    /**
     * convert anRDD of KeyValueObject to a JavaPairRDD of keys and values
     *
     * @param inp input RDD
     * @param <K> key
     * @param <V> value
     * @return
     */
    @Nonnull
    public static <K extends Serializable, V extends Serializable> JavaRDD<KeyValueObject<K, V>> fromTuples(@Nonnull JavaPairRDD<K, V> inp) {
        return inp.map(new AbstractLoggingFunction<Tuple2<K, V>, KeyValueObject<K, V>>() {
            @Override
            public KeyValueObject<K, V> doCall(final Tuple2<K, V> t) throws Exception {
                KeyValueObject ret = new KeyValueObject(t._1(), t._2());
                return ret;
            }
        });
    }


    public static final int NUMBER_ELEMENTS_TO_VIEW = 100;
    /**
     * force a JavaRDD to evaluate then return the results as a JavaRDD
     *
     * @param inp this is an RDD - usually one you want to examine during debugging
     * @param <T> whatever inp is a list of
     * @return non-null RDD of the same values but realized
     */
    @Nonnull
    public static JavaRDD realizeAndReturn(@Nonnull final JavaRDD inp) {
        JavaSparkContext jcx = getCurrentContext();
        if (!isLocal())     // not to use on the cluster - only for debugging
            return inp;
        List collect = inp.collect();    // break here and take a look

        System.out.println("Realized with " + collect.size() + " elements");
        // look at a few elements
        for (int i = 0; i < Math.min(collect.size(),NUMBER_ELEMENTS_TO_VIEW); i++) {
            Object value = collect.get(i);
            value = null; // break hera
        }
        return jcx.parallelize(collect);
    }


    /**
     * force a JavaPairRDD to evaluate then return the results as a JavaPairRDD
     *
     * @param inp this is an RDD - usually one you want to examine during debugging
     * @param <T> whatever inp is a list of
     * @return non-null RDD of the same values but realized
     */
    @Nonnull
    public static <K, V> JavaPairRDD<K, V> realizeAndReturn(@Nonnull final JavaPairRDD<K, V> inp) {
        JavaSparkContext jcx = getCurrentContext();
        if (!isLocal())    // not to use on the cluster - only for debugging
            return inp; //
        List<Tuple2<K, V>> collect = (List<Tuple2<K, V>>) (List) inp.collect();    // break here and take a look
        System.out.println("Realized with " + collect.size() + " elements");
        // look at a few elements
        for (int i = 0; i < Math.min(collect.size(),NUMBER_ELEMENTS_TO_VIEW); i++) {
            Tuple2<K, V> value = collect.get(i);
            value = null; // break hera
        }
        return (JavaPairRDD<K, V>) jcx.parallelizePairs(collect);
    }


    /**
     * force a JavaPairRDD to evaluate then return the results as a JavaPairRDD
     *
     * @param inp this is an RDD - usually one you want to examine during debugging
     * @param handler all otuples are passed here
      * @param <T> whatever inp is a list of
     * @return non-null RDD of the same values but realized
     */
    @Nonnull
    public static <K, V> JavaPairRDD<K, V> realizeAndReturn(@Nonnull final JavaPairRDD<K, V> inp,ObjectFoundListener<Tuple2<K, V>> handler) {
        JavaSparkContext jcx = getCurrentContext();
        if (!isLocal())    // not to use on the cluster - only for debugging
            return inp; //
        List<Tuple2<K, V>> collect = (List<Tuple2<K, V>>) (List) inp.collect();    // break here and take a look
        for (Tuple2<K, V> kvTuple2 : collect) {
            handler.onObjectFound(kvTuple2);
        }
        return (JavaPairRDD<K, V>) jcx.parallelizePairs(collect);
    }


    /**
     * force a JavaRDD to evaluate then return the results as a JavaRDD
     *
     * @param inp this is an RDD - usually one you want to examine during debugging
     * @param handler all objects are passed here
      * @param <T> whatever inp is a list of
     * @return non-null RDD of the same values but realized
     */
    @Nonnull
    public static <K, V> JavaRDD< V> realizeAndReturn(@Nonnull final JavaRDD<V> inp,ObjectFoundListener<V> handler) {
        JavaSparkContext jcx = getCurrentContext();
        if (!isLocal())    // not to use on the cluster - only for debugging
            return inp; //
        List<V> collect = (List<V>) (List) inp.collect();    // break here and take a look
        for (V value : collect) {
            handler.onObjectFound(value);
        }
        return (JavaRDD<V>) jcx.parallelize(collect);
    }



    /**
     * persist in the best way - saves remembering which storage level
     * @param inp
     * @return
     */
    @Nonnull
    public static <V> JavaRDD<V>  presist(@Nonnull final JavaRDD<V>  inp) {
         return inp.persist(StorageLevel.MEMORY_AND_DISK());
    }

    /**
     * persist in the best way - saves remembering which storage level
     * @param inp
     * @return
     */
    @Nonnull
    public static <K, V> JavaPairRDD<K, V> persist(@Nonnull final JavaPairRDD<K, V> inp) {
         return inp.persist(StorageLevel.MEMORY_AND_DISK());
    }




    /**
     * make an RDD from an iterable
     *
     * @param inp input iterator
     * @param ctx context
     * @param <T> type
     * @return rdd from inerator as a list
     */
    public static
    @Nonnull
    <T> JavaRDD<T> fromIterable(@Nonnull final Iterable<T> inp) {
        JavaSparkContext ctx = SparkUtilities.getCurrentContext();

        List<T> holder = new ArrayList<T>();
        for (T k : inp) {
            holder.add(k);
        }
        return ctx.parallelize(holder);
    }


    /**
     * collector to examine RDD
     *
     * @param inp
     * @param <K>
     */
    public static void showRDD(JavaRDD inp) {
        List collect = inp.collect();
        for (Object k : collect) {
            System.out.println(k.toString());
        }
        // now we must exit
        throw new IllegalStateException("input RDD is consumed by show");
    }

    /**
     * collector to examine JavaPairRDD
     *
     * @param inp
     * @param <K>
     */
    public static void showPairRDD(JavaPairRDD inp) {
        inp.persist(StorageLevel.MEMORY_ONLY());
        List<Tuple2> collect = inp.collect();
        for (Tuple2 kvTuple2 : collect) {
            System.out.println(kvTuple2._1().toString() + " : " + kvTuple2._2().toString());
        }
        // now we must exit
        //  throw new IllegalStateException("input RDD is consumed by show");
    }


    /**
     * convert an iterable of KeyValueObject (never heard of Spark) into an iterable of Tuple2
     *
     * @param inp
     * @param <K> key
     * @param <V>
     * @return
     */
    public static
    @Nonnull
    <K extends java.io.Serializable, V extends java.io.Serializable> Iterable<Tuple2<K, V>> toTuples(@Nonnull Iterable<KeyValueObject<K, V>> inp) {
        final Iterator<KeyValueObject<K, V>> originalIterator = inp.iterator();
        return new Iterable<Tuple2<K, V>>() {
            @Override
            public Iterator<Tuple2<K, V>> iterator() {
                return new Iterator<Tuple2<K, V>>() {
                    @Override
                    public boolean hasNext() {
                        return originalIterator.hasNext();
                    }

                    @Override
                    public Tuple2<K, V> next() {
                        KeyValueObject<K, V> next = originalIterator.next();
                        return new Tuple2(next.key, next.value);
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException("not supported");
                    }
                };
            }
        };
    }

    /**
     * convert an iterable of Tuple2 (never heard of Spark) into an iterable of KeyValueObject
     *
     * @param inp
     * @param <K> key
     * @param <V>
     * @return
     */
    public static
    @Nonnull
    <K extends java.io.Serializable, V extends java.io.Serializable> Iterable<KeyValueObject<K, V>> toKeyValueObject(@Nonnull Iterable<Tuple2<K, V>> inp) {
        final Iterator<Tuple2<K, V>> originalIterator = inp.iterator();
        return new Iterable<KeyValueObject<K, V>>() {
            @Override
            public Iterator<KeyValueObject<K, V>> iterator() {
                return new Iterator<KeyValueObject<K, V>>() {
                    @Override
                    public boolean hasNext() {
                        return originalIterator.hasNext();
                    }

                    @Override
                    public KeyValueObject<K, V> next() {
                        Tuple2<K, V> next = originalIterator.next();
                        return new KeyValueObject(next._1(), next._2());
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException("not supported");
                    }
                };
            }
        };
    }


    /**
     * return a key representing the sort index of some value
     *
     * @param values
     * @param <K>
     * @return
     */
    public static <K extends Serializable> JavaPairRDD<Integer, K> indexByOrder(JavaRDD<K> values) {
        values = values.sortBy(new Function<K, K>() {
                                   @Override
                                   public K call(final K v1) throws Exception {
                                       return v1;
                                   }
                               }, true,
                getDefaultNumberPartitions()
        );
        return values.mapToPair(new PairFunction<K, Integer, K>() {
            private int index = 0;

            @Override
            public Tuple2<Integer, K> call(final K t) throws Exception {
                return new Tuple2(index++, t);
            }
        });
    }

    /**
     * use as a properties file when logging
     *
     * @param mainClass
     * @param args
     * @return
     */
    public static String buildLoggingClassLoaderPropertiesFile(Class mainClass, String[] args) {
        StringBuilder sb = new StringBuilder();
        sb.append("#\n" +
                "# classpath - it is a good ides to drop any of the java jars\n");
        String classPath = System.getProperty("java.class.path");
        String classPathSeparator = System.getProperty("path.separator");
        String[] items = classPath.split(classPathSeparator);
        sb.append("classpath = ");
        for (int i = 0; i < items.length; i++) {
            String item = items[i];
            sb.append("   " + item);
            if (i < items.length - 1)
                sb.append(";\\\n");
            else
                sb.append("\n");
        }
        sb.append("\n");

        sb.append("classpath_excludes=*IntelliJ IDEA*\n" +
                "\n" +
                "#\n" +
                "# if specified this will be the main in the mainfest\n");
        sb.append("mainclass=" + mainClass.getCanonicalName() + "\n");

        sb.append("#\n" +
                "# if specified run the program using this user directory\n" +
                "user_dir=" + System.getProperty("user.dir").replace("\\", "/") + "\n");

        sb.append("#\n" +
                "# if specified the main will run with these arguments\n" +
                "arguments =");

        for (int i = 0; i < args.length; i++) {
            String item = items[i];
            sb.append(item + " ");
        }
        sb.append("\n");

        return sb.toString();
    }


}
