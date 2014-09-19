package com.lordjoe.distributed.util;

import com.lordjoe.distributed.*;

import java.io.*;
import java.util.*;
import java.util.function.*;

/**
 * com.lordjoe.distributed.util.ReduceAdaptor
 * User: Steve
 * Date: 9/18/2014
 */
public class ReduceAdaptor<K extends Serializable,V   extends Serializable> implements Consumer<K> {

    private final IReducerFunction<K,V> reducer;
    private final Map<K, List<KeyValueObject<K, V>>> values;
      private final KeyValueConsumerAdaptor<K, V> consumer;

    public ReduceAdaptor(final IReducerFunction pReducer,Map<K, List<KeyValueObject<K, V>>> pvalues,
                         Consumer< KeyValueObject<K, V>>  pout) {
        reducer = pReducer;
        values = pvalues;
         consumer = new KeyValueConsumerAdaptor(pout);
    }


    /**
     * Performs this operation on the given argument.
     *
     * @param t the input argument
     */
    @Override public void accept(final K t) {
        List<KeyValueObject> list = ( List<KeyValueObject> )(Object)values.get(t);
        Iterable<V> values1 = IterableUtilities.asIterableValues(list);
        reducer.handleValues(t, values1,consumer);
    }
}
