package com.lordjoe.distributed;


import org.apache.spark.api.java.function.*;
import scala.*;

import java.io.Serializable;
import java.util.*;

/**
 * com.lordjoe.distributed.ReduceFunctionAdaptor
 * User: Steve
 * Date: 8/28/2014
 */
public class ReduceFunctionAdaptor<K extends Serializable, V extends Serializable> implements FlatMapFunction<Tuple2<K,CombineByKeyAdaptor.KeyAndValues<K, V>>, KeyValueObject<K, V>>, Serializable {
    private final IReducerFunction<K, V> reducer;
    private Tuple2<K, V> first;

    public ReduceFunctionAdaptor(final IReducerFunction<K, V> pReducer) {
        reducer = pReducer;
    }

    @Override
    public Iterable<KeyValueObject<K, V>> call(Tuple2<K,CombineByKeyAdaptor.KeyAndValues<K, V>> inp) throws Exception {
        final CombineByKeyAdaptor.KeyAndValues<K, V> itr =  inp._2();
        first = null;
        final List<KeyValueObject<K, V>> holder = new ArrayList<KeyValueObject<K, V>>();

        Iterable<V> iterable = itr.getIterable();
        K key = itr.key;

        final IKeyValueConsumer<K, V> consumer = new IKeyValueConsumer<K, V>() {
            @Override
            public void consume(final KeyValueObject<K, V> kv) {
                holder.add(kv);
            }
        };
        reducer.handleValues(key, iterable, consumer);
        return holder;
    }


}
