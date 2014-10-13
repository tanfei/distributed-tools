package com.lordjoe.distributed;

import org.apache.spark.*;
import org.apache.spark.api.java.*;
import org.apache.spark.api.java.function.*;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.storage.*;
import scala.*;

import javax.annotation.*;
import java.io.*;
import java.io.Serializable;
import java.util.*;

/**
 * com.lordjoe.distributed.SpareUtilities
 * User: Steve
 * Date: 8/28/2014
 */
public class SparkUtilities implements Serializable {



    /**
     * read a file with a list of desired properties
     * @param fileName
     * @return
     */
    public static Properties readSparkProperties(String fileName) {
        try {
            Properties sparkProperties = new Properties();
            File f = new File(fileName);
            String path = f.getAbsolutePath();
            sparkProperties.load(new FileReader(f));  // read spark properties
            return sparkProperties;
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
    public static void guaranteeSparkMaster(@Nonnull SparkConf sparkConf, Properties props) {
        Option<String> option = sparkConf.getOption("spark.master");

        if (!option.isDefined()) {   // use local over nothing   {
            sparkConf.setMaster("local[4]");
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
        // ste all properties in the SparkProperties file
        for (String property : props.stringPropertyNames()) {
            if (!property.startsWith("spark."))
                continue;
            sparkConf.set(property, props.getProperty(property));

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
     *
     * @param pathName given path - we may need to predend hdfs access
     * @param props
     * @return
     */
    public static String buildPath(final String pathName, Properties props) {
        if(pathName.startsWith("hdfs://"))
            return pathName;
        String prepend = props.getProperty(PATH_PREPEND_PROPERTY);
        if(prepend == null)
            return pathName;
        return prepend + pathName;
    }


    public static class KeyValueObjectToTuple2<K extends Serializable, V extends Serializable> implements FlatMapFunction2<KeyValueObject<K, V>, K, V> {
        @Override
        public Iterable<V> call(final KeyValueObject<K, V> ppk, final K pK) throws Exception {
            Object[] items =  { ppk.value };
            return  Arrays.asList((V[])items);
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
        PairFunction<KeyValueObject<K, V>, K, V> pf = new PairFunction<KeyValueObject<K, V>, K, V>() {
            @Override
            public Tuple2<K, V> call(KeyValueObject<K, V> kv) {
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
        return inp.map(new Function<Tuple2<K, V>, KeyValueObject<K, V>>() {
            @Override
            public KeyValueObject<K, V> call(final Tuple2<K, V> t) throws Exception {
                KeyValueObject ret = new KeyValueObject(t._1(), t._2());
                return ret;
            }
        });
    }


    /**
     * force a JavaRDD to evaluate then return the results as a JavaRDD
     *
     * @param inp this is an RDD - usually one you want to examine during debugging
     * @param <T> whatever inp is a list of
     * @return non-null RDD of the same values but realized
     */
    @Nonnull
    public static JavaRDD realizeAndReturn(@Nonnull final JavaRDD inp, @Nonnull JavaSparkContext jcx) {
        List collect = inp.collect();    // break here and take a look
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
    public static <K, V> JavaPairRDD<K, V> realizeAndReturn(@Nonnull final JavaPairRDD<K, V> inp, @Nonnull JavaSparkContext jcx) {
        // Todo Why to I need to cast
        List<Tuple2<Object, Object>> collect = (List<Tuple2<Object, Object>>) (List) inp.collect();    // break here and take a look
        return (JavaPairRDD<K, V>) jcx.parallelizePairs(collect);
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
    <T> JavaRDD<T> fromIterable(@Nonnull final Iterable<T> inp, @Nonnull final JavaSparkContext ctx) {
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
}
