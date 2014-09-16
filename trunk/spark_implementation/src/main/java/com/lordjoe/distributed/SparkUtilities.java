package com.lordjoe.distributed;

import org.apache.spark.api.java.*;
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
     * read a stream into memory and return it as an RDD
     * @param is
     * @param sc
     * @return
     */
    public static JavaRDD<String>  fromInputStream(InputStream is, JavaSparkContext sc)
    {
        try {
            List<String> lst = new ArrayList<String>();
            BufferedReader rdr = new BufferedReader(new InputStreamReader(is));
            List< String> holder = new ArrayList< String>();
            String line = rdr.readLine();
            while(line != null)  {
                holder.add(line);
                line = rdr.readLine();
            }
            return sc.parallelize(lst) ;
        }
        catch (IOException e) {
            throw new RuntimeException(e);

        }
    }

    /**
     * collector to examine RDD
     * @param inp
     * @param <K>
     */
    public static <K> void showRDD(JavaRDD<K> inp)  {
        List<K> collect = inp.collect();
        for (K k : collect) {
            System.out.println(k.toString());
        }
    }
    /**
       * collector to examine RDD
       * @param inp
       * @param <K>
       */
      public static <K,V> void showPairRDD(JavaPairRDD<K,V> inp)  {
          List<Tuple2<K, V>> collect = inp.collect();
          for (Tuple2<K, V> kvTuple2 : collect) {
              System.out.println(kvTuple2._1().toString() + " : " + kvTuple2._2().toString());
          }
      }


    /**
     * convert an iterable of KeyValueObject (never heard of Spark) into an iterable of Tuple2
     * @param inp
     * @param <K> key
     * @param <V>
     * @return
     */
    public static @Nonnull <K extends java.io.Serializable,V extends java.io.Serializable>  Iterable<Tuple2<K,V>>  toTuples(@Nonnull Iterable<KeyValueObject<K,V>> inp)  {
        final Iterator<KeyValueObject<K, V>> originalIterator = inp.iterator();
        return new Iterable<Tuple2<K, V>>() {
            @Override public Iterator<Tuple2<K, V>> iterator() {
                return new Iterator<Tuple2<K, V>>() {
                    @Override public boolean hasNext() {
                        return originalIterator.hasNext();
                    }

                    @Override public Tuple2<K, V> next() {
                        KeyValueObject<K, V> next = originalIterator.next();
                        return new Tuple2(next.key,next.value);
                    }

                    @Override public void remove() {
                       throw new UnsupportedOperationException("not supported");
                    }
                };
            }
        };
    }

    /**
      * convert an iterable of Tuple2 (never heard of Spark) into an iterable of KeyValueObject
      * @param inp
      * @param <K> key
      * @param <V>
      * @return
      */
     public static @Nonnull <K extends java.io.Serializable,V extends java.io.Serializable>  Iterable<KeyValueObject<K,V>>  toKeyValueObject(@Nonnull Iterable<Tuple2<K,V>> inp)  {
         final Iterator<Tuple2<K, V>> originalIterator = inp.iterator();
         return new Iterable<KeyValueObject<K, V>>() {
             @Override public Iterator<KeyValueObject<K, V>> iterator() {
                 return new Iterator<KeyValueObject<K, V>>() {
                     @Override public boolean hasNext() {
                         return originalIterator.hasNext();
                     }

                     @Override public KeyValueObject<K, V> next() {
                         Tuple2<K, V> next = originalIterator.next();
                         return new KeyValueObject(next._1(),next._2());
                     }

                     @Override public void remove() {
                        throw new UnsupportedOperationException("not supported");
                     }
                 };
             }
         };
     }
}
