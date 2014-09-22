package com.lordjoe.distributed;

import org.apache.hadoop.io.*;
import org.systemsbiology.hadoop.*;

import java.io.*;
import java.util.*;

/**
 * com.lordjoe.distributed.ReduceAdaptor
 * User: Steve
 * Date: 9/19/2014
 */
public class ReduceAdaptor<K extends Serializable,V extends Serializable> extends AbstractParameterizedReducer {

    private final IReducerFunction<K,V> reducer;

    public ReduceAdaptor(final IReducerFunction<K, V> pReducer) {
        reducer = pReducer;
    }

    @Override
    protected void reduceNormal(final Text keyx, final Iterable<Text> valuesx, final Context context) throws IOException, InterruptedException {
        K key = (K)HadoopDistributedUtilities.fromHadoopText(keyx.toString());
        Iterable<V>  values = new Iterable<V>() {
            final Iterator<Text> itx = valuesx.iterator();
            public Iterator<V> iterator() {
                return new Iterator<V>() {
                    @Override
                    public boolean hasNext() {
                        return itx.hasNext();
                    }

                    @Override
                    public V next() {
                        Text next = itx.next();
                        V ret = (V)HadoopDistributedUtilities.fromHadoopText(next.toString());
                        return ret;
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException("No can do");
                    }
                };
            }
        };
        IKeyValueConsumer<K,V> consumer =  HadoopDistributedUtilities.contextConsumer(context);
        reducer.handleValues(key,values,consumer);

    }
}
