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
public class ReduceFunctionAdaptor<K extends Serializable, V extends Serializable> implements FlatMapFunction<Iterator<Tuple2<K, V>>, KeyValueObject<K, V>>, Serializable {
    private final IReducerFunction<K, V> reducer;
    private K key;

    public ReduceFunctionAdaptor(final IReducerFunction<K, V> pReducer) {
        reducer = pReducer;
    }

    @Override public Iterable<KeyValueObject<K, V>> call(final Iterator<Tuple2<K, V>> itr) throws Exception {
        final List<KeyValueObject<K, V>> holder = new ArrayList<KeyValueObject<K, V>>();
        key = null;
        if(!itr.hasNext())
            return holder;

        final Tuple2<K, V> first = itr.next();
        final Iterator<V> itx = new Iterator<V>() {
            Tuple2<K, V> current = first;
              @Override public boolean hasNext() {
                 return current != null;
              }

            @Override public V next() {
                V ret = current._2();
                if(itr.hasNext())
                    current = itr.next();
                else
                    current = null;
                return ret;
            }


            @Override public void remove() {
                throw new UnsupportedOperationException("Fix This"); // ToDo
            }
        };

        final Iterable<V> vals = new Iterable<V>() {
            @Override public Iterator<V> iterator() {
                return itx;
            }
        };
         final IKeyValueConsumer<K, V> consumer = new IKeyValueConsumer<K, V>() {
            @Override public void consume(final KeyValueObject<K, V> kv) {
                holder.add(kv);
            }
        };
        reducer.handleValues((K)first._1(), vals, consumer);

        key = null;
        return holder;
    }


}
