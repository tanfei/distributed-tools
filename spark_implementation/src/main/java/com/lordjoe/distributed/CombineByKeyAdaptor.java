package com.lordjoe.distributed;


import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import scala.*;

import java.io.Serializable;
import java.util.*;

/**
 * com.lordjoe.distributed.ReduceFunctionAdaptor
 * User: Steve
 * Date: 8/28/2014
 */
public class CombineByKeyAdaptor<K extends Serializable, V extends Serializable> implements Serializable {
    private final IReducerFunction<K, V> reducer;
    private Tuple2<K, V> first;

    public CombineByKeyAdaptor(final IReducerFunction<K, V> pReducer) {
        reducer = pReducer;
    }

    /**
     * a class to store a key and all its values
     *   using an array list
     * @param <K> key type
     * @param <V> value type
     */
    public static class KeyAndValues<K, V> {
        public final K key;
        private final ArrayList<V> values = new ArrayList<V>();
        public KeyAndValues(final K pKey) {
            key = pKey;
        }
         public void addValue(V added) {
            values.add(added);
        }
         public Iterable<V> getIterable() {
            return values;
        }
         public KeyAndValues<K, V> merge(KeyAndValues<K, V> merged) {
            values.addAll(merged.values);
            return this;
        }
    }

       public static class CombineStartKeyAndValues<K, V> implements Function<Tuple2<K,V>, KeyAndValues<K, V>> {
          public KeyAndValues call(Tuple2<K,V> x) {
            KeyAndValues ret = new KeyAndValues(x._1());
            ret.addValue(x._2());
            return ret;
        }
    }

    public static class CombineContinueKeyAndValues<K, V> implements Function2< KeyAndValues< K,V>, Tuple2<K,V>, KeyAndValues<K, V>> {
         public KeyAndValues<K, V> call(final KeyAndValues<K, V> kvs, final Tuple2<K,V> added) throws Exception {
            kvs.addValue(added._2());
            return kvs;
        }
    }

     public static class CombineMergeKeyAndValues<K, V> implements Function2< KeyAndValues<K, V>,KeyAndValues<K, V>,KeyAndValues<K, V>> {
          public KeyAndValues<K, V> call(final KeyAndValues<K, V> v1, final KeyAndValues<K, V> v2) throws Exception {
            return null;
        }
    }



    /**
     * a class to store a key and all its values
     *
     * @param <K> key type
     * @param <V> value type
     */
    public static class KeyAndValuesRDD<K, V> {
        public final K key;
        private final ContinuousIterable<V> values = new ContinuousIterable<V>();

        public KeyAndValuesRDD(final K pKey) {
            key = pKey;
        }

        public void addValue(V added) {
            values.add(added);
        }

        public void finish() {
            values.finish();
        }

        public Iterable<V> getIterable() {
            return values;
        }
    }




}
