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
     * using an array list
     *
     * @param <K> key type
     * @param <V> value type
     */
    public static class KeyAndValues<K, V> implements Serializable {
        public final K key;
        private final List<V> values = new ArrayList<V>();

        public KeyAndValues(final K pKey, V... added) {
            key = pKey;
            values.addAll(Arrays.asList(added));
        }

        public KeyAndValues(final KeyAndValues<K, V> start, V... added) {
            this(start.key);
            values.addAll(start.values);
            values.addAll(Arrays.asList(added));
        }

//        public KeyAndValues(final KeyAndValues<K, V> start, Iterable<V> added) {
//            key = start.key;
//            values = CompositeIterable.composeIterators(start.values, added);
//            values.addAll(Arrays.asList(added));
//        }

        public Iterable<V> getIterable() {
            return values;
        }

        public KeyAndValues<K, V> merge(KeyAndValues<K, V> merged) {
            return new KeyAndValues(this, merged);
        }
    }

    public static class CombineStartKeyAndValues<K, V> implements Function<Tuple2<K, V>, KeyAndValues<K, V>>   {
        public KeyAndValues call(Tuple2<K, V> x) {
              return new KeyAndValues(x._1(),x._2());
        }
    }

    public static class CombineContinueKeyAndValues<K, V> implements Function2<KeyAndValues<K, V>, Tuple2<K, V>, KeyAndValues<K, V>> {
        public KeyAndValues<K, V> call(final KeyAndValues<K, V> kvs, final Tuple2<K, V> added) throws Exception {
                 return new KeyAndValues(kvs ,added._2() );
        }
    }

    public static class CombineMergeKeyAndValues<K, V> implements Function2<KeyAndValues<K, V>, KeyAndValues<K, V>, KeyAndValues<K, V>> {
        public KeyAndValues<K, V> call(final KeyAndValues<K, V> v1, final KeyAndValues<K, V> v2) throws Exception {
            return new KeyAndValues(v1,v2);
          }
    }




}
