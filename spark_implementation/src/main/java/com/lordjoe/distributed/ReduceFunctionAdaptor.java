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
    private   Tuple2<K, V> first;

    public ReduceFunctionAdaptor(final IReducerFunction<K, V> pReducer) {
        reducer = pReducer;
    }

    @Override public Iterable<KeyValueObject<K, V>> call(final Iterator<Tuple2<K, V>> itr) throws Exception {
        first = null;
        final List<KeyValueObject<K, V>> holder = new ArrayList<KeyValueObject<K, V>>();
         if(!itr.hasNext())
            return holder;

         first = itr.next();

        final Iterator<V> itx = new Iterator<V>() {
            Tuple2<K, V> current = first;
              @Override public boolean hasNext() {
                 if(current == null)
                     return false;
                 return true;
              }

            @Override public V next() {
                V ret = current._2();
                if(itr.hasNext()) {
                    Tuple2<K, V> test = itr.next();
                    if(test._1().equals(first._1()))
                        current = test;
                    else {
                        first = test;     // different key
                        current = null;
                    }
                }
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
        K key = (K) first._1();
        reducer.handleValues(key, vals, consumer);
         return holder;
    }

    protected void  handleKeyValues(final Iterator<Tuple2<K, V>> itr,IKeyValueConsumer<K, V> ... consumer)
    {
       if(first == null)
               return;
        final K key = first._1();

        final Iterator<V> itx = new Iterator<V>() {
            Tuple2<K, V> current = first;
               @Override public boolean hasNext() {
                 return current != null;
              }

            @Override public V next() {
                V ret = current._2();
                if(itr.hasNext()) {
                    current = itr.next();
                    if(!current._1().equals(key))   {
                        first = current;
                        current = null;
                    }
                }
                else
                    current = null;
                return ret;
            }


            @Override public void remove() {
                throw new UnsupportedOperationException("Fix This"); // ToDo
            }
        };


    }

}
