package com.lordjoe.distributed;

import com.lordjoe.distributed.util.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;


/**
 * com.lordjoe.distributed.StreamingMapReduce
 * User: Steve
 * Date: 8/25/2014
 */
public class JavaMapReduce<VALUEIN extends Serializable, K extends Serializable, V extends Serializable> extends AbstractMapReduceEngine<VALUEIN, K, V> {


    public JavaMapReduce(final IMapperFunction<VALUEIN, K, V> mapper, final IReducerFunction<K, V> reducer) {
        //noinspection unchecked
        this(mapper, reducer, IPartitionFunction.HASH_PARTITION,new ListKeyValueConsumer<K, V>());
    }

    public JavaMapReduce(final IMapperFunction<VALUEIN, K, V> pMapper,
                         final IReducerFunction<K, V> pRetucer,
                         IPartitionFunction<K> pPartitioner,
                         IKeyValueConsumer<K, V>... consumer) {
        setMap(pMapper);
        setReduce(pRetucer);
        setPartitioner(pPartitioner);
        for (int i = 0; i < consumer.length; i++) {
            IKeyValueConsumer<K, V> cnsmr = consumer[i];
            addConsumer(cnsmr);

        }
     }


    /**
     * all the work is done here
     *
     * @param source
     * @param sink
     */
    @Override
    public void performMapReduce(final Path source, final Path sink) {
        Iterable<KeyValueObject<K, V>> holder = performMap(source);
        Iterable<List<KeyValueObject<K, V>>> partition = partition(holder);
        performReduce(partition);

    }



    /**
     * all the work is done here
     *
     * @param source
     * @param sink
     */
    //@Override
    public void performSourceMapReduce(final Iterable<VALUEIN> pInputs) {
        Iterable<KeyValueObject<K, V>> holder = performSourceMap(pInputs);
        Iterable<List<KeyValueObject<K, V>>> partition = partition(holder);
        performReduce(partition);
   //      reportValues();


    }

    protected void reportValues() {
        // todo move out and replace
        List<IKeyValueConsumer<K, V>> consumers = getConsumers();
        ListKeyValueConsumer<K, V> cnsmr = (ListKeyValueConsumer<K, V>) consumers.get(0);
        List<KeyValueObject<K, V>> output = cnsmr.getList();
        Collections.sort(output, KeyValueObject.KEY_COMPARATOR);
        for (KeyValueObject<K, V> kv : output) {
            System.out.println(kv.key + ":" + kv.value);
        }
    }

    protected void performReduce(Iterable<List<KeyValueObject<K, V>>> partitions) {
        for (List<KeyValueObject<K, V>> partition : partitions) {
            Collections.sort(partition,KeyValueObject.KEY_COMPARATOR);
            handlePartition(partition);
        }
    }


    protected void handlePartition(Iterable<KeyValueObject<K, V>> partition) {
        IReducerFunction reduce = getReduce();
        List<IKeyValueConsumer<K, V>> consumersList = getConsumers();
        IKeyValueConsumer<K, V>[] consumers = consumersList.toArray(new IKeyValueConsumer[consumersList.size()]);
        K key = null;
        List<V> holder = new ArrayList();
        for (KeyValueObject<K, V> kv : partition) {
            if (key == null || !kv.key.equals(key)) {
                if (!holder.isEmpty()) {
                    reduce.handleValues(key, holder, consumers);// todo this is values
                }
                holder.clear();
                key = kv.key;
            }
            holder.add(kv.value);
        }
    }


    protected Iterable<KeyValueObject<K, V>> performMap(final Path source) {
        ISourceFunction source1 = getSource();
        Iterable<VALUEIN> inputs = source1.readInput(source);
        return performSourceMap(inputs);
    }

    protected Iterable<KeyValueObject<K, V>> performSourceMap(final Iterable<VALUEIN> pInputs) {
        IMapperFunction map = getMap();
        List<KeyValueObject<K, V>> holder = new ArrayList<>();
        for (VALUEIN input : pInputs) {
            Iterable<KeyValueObject<K, V>> iterable = map.mapValues(input);
            for (KeyValueObject<K, V> kv : iterable) {
                holder.add(kv);
            }
        }
        return holder;
    }

    protected List<KeyValueObject<K, V>>[] buildPartitions() {
        int numberReducers = getNumberReducers();
        List<KeyValueObject<K, V>>[] partitions = new List[numberReducers];
        for (int i = 0; i < partitions.length; i++) {
            partitions[i] = new ArrayList<KeyValueObject<K, V>>();

        }
        return partitions;
    }

    /**
     * perform shuffle in memory end up with
     *
     * @param maps
     * @return
     */
    protected Iterable<List<KeyValueObject<K, V>>> partition(Iterable<KeyValueObject<K, V>> maps) {
        IPartitionFunction partitioner = getPartitioner();
        int numberReducers = getNumberReducers();
        List<KeyValueObject<K, V>>[] partitions = buildPartitions();

        for (KeyValueObject<K, V> kv : maps) {
            int index = partitioner.getPartition(kv.key) % numberReducers;
            partitions[index].add(kv);
        }
        for (List<KeyValueObject<K, V>> partition : partitions) {
            Collections.sort(partition, KeyValueObject.KEY_COMPARATOR);
        }
        return Arrays.asList(partitions);
    }


}
