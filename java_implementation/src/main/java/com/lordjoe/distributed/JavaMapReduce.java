package com.lordjoe.distributed;

import com.lordjoe.distributed.util.*;
import com.lordjoe.distributed.wordcount.*;

import javax.annotation.*;
import java.io.*;
import java.util.*;


/**
 * com.lordjoe.distributed.StreamingMapReduce
 * User: Steve
 * Date: 8/25/2014
 */
public class JavaMapReduce<KEYIN extends Serializable,VALUEIN extends Serializable, K extends Serializable, V extends Serializable, KOUT extends Serializable, VOUT extends Serializable> extends AbstractMapReduceEngine<KEYIN,VALUEIN,K,V, KOUT, VOUT> {

    public static final MapReduceEngineFactory FACTORY = new MapReduceEngineFactory() {
        /**
         * build an engine having been passed a
         *
         * @param pMapper  map function
         * @param pRetucer reduce function
         * @return
         */
        @Override public <KEYIN extends Serializable, VALUEIN extends Serializable, K extends Serializable, V extends Serializable, KOUT extends Serializable, VOUT extends Serializable> IMapReduce<  KEYIN, VALUEIN,   KOUT,VOUT> buildMapReduceEngine(String name,@Nonnull final IMapperFunction<KEYIN, VALUEIN, K, V> pMapper, @Nonnull final IReducerFunction<K, V,KOUT,VOUT> pRetucer) {
            return new JavaMapReduce(pMapper,pRetucer);
        }

        /**
         * build an engine having been passed a
         *
         * @param pMapper      map function
         * @param pRetucer     reduce function
         * @param pPartitioner partition function default is HashPartition
         * @return
         */
        @Override public <KEYIN extends Serializable, VALUEIN extends Serializable, K extends Serializable, V extends Serializable, KOUT extends Serializable, VOUT extends Serializable>
           IMapReduce<KEYIN, VALUEIN, KOUT,VOUT>
             buildMapReduceEngine(String name,@Nonnull final IMapperFunction<KEYIN, VALUEIN, K, V> pMapper,
                                            @Nonnull final IReducerFunction<K, V,KOUT,VOUT> pRetucer,
                                            final IPartitionFunction<K> pPartitioner) {
            return new JavaMapReduce(pMapper,pRetucer,pPartitioner);
        }
    };

    private final ListKeyValueConsumer results = new ListKeyValueConsumer<K, V>();

    public JavaMapReduce(final IMapperFunction<KEYIN,VALUEIN, K, V> mapper, final IReducerFunction<K, V,KOUT,VOUT> reducer) {
        //noinspection unchecked
        this(mapper, reducer, IPartitionFunction.HASH_PARTITION,new ListKeyValueConsumer<KOUT,VOUT>());
    }

    public JavaMapReduce(final IMapperFunction<KEYIN,VALUEIN, K, V> pMapper,
                         final IReducerFunction<K, V,KOUT,VOUT> pRetucer,
                         IPartitionFunction<K> pPartitioner,
                         IKeyValueConsumer<KOUT, VOUT>... consumer) {
        setMap(pMapper);
        setReduce(pRetucer);
        setPartitioner(pPartitioner);
        for (int i = 0; i < consumer.length; i++) {
            IKeyValueConsumer<KOUT, VOUT> cnsmr = consumer[i];
            addConsumer(cnsmr);
         }
        addConsumer(results);
     }


    public ListKeyValueConsumer getResults() {
        return results;
    }






    protected void reportValues() {
        // todo move out and replace
        List<IKeyValueConsumer<KOUT,VOUT>> consumers = getConsumers();
        ListKeyValueConsumer<KOUT,VOUT> cnsmr = (ListKeyValueConsumer<KOUT,VOUT>) consumers.get(0);
        List<KeyValueObject<KOUT,VOUT>> output = cnsmr.getList();
        Collections.sort(output, KeyValueObject.KEY_COMPARATOR);
        for (KeyValueObject<KOUT,VOUT> kv : output) {
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
        List<IKeyValueConsumer<KOUT,VOUT>> consumersList = getConsumers();
        @SuppressWarnings("unchecked")
        IKeyValueConsumer<K, V>[] consumers = consumersList.toArray(new IKeyValueConsumer[consumersList.size()]);
        K key = null;
        //noinspection unchecked
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


    protected void performMapReduce(final Iterable<KeyValueObject<KEYIN, VALUEIN>> pInputs)
    {
        Iterable<KeyValueObject<K, V>> mapping = performSourceMap( pInputs);
        Iterable<List<KeyValueObject<K, V>>> partitions = partition(mapping);
        performReduce( partitions) ;
    }
//
//    protected Iterable<KeyValueObject<K, V>> performMap(final Path source) {
//        ISourceFunction source1 = getSource();
//        Iterable<VALUEIN> inputs = source1.readInput(source);
//        return performSourceMap(inputs);
//    }

    protected Iterable<KeyValueObject<K, V>> performSourceMap(final Iterable<KeyValueObject<KEYIN, VALUEIN>> pInputs) {
        IMapperFunction map = getMap();
        List<KeyValueObject<K, V>> holder = new ArrayList<>();
        for (KeyValueObject<KEYIN, VALUEIN> kvx : pInputs) {
            Iterable<KeyValueObject<K, V>> iterable = map.mapValues(kvx.key,kvx.value);
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


    /**
     * sources may be very implementation specific
     *
     * @param source    some source of data - might be a hadoop directory or a Spark RDD - this will be cast internally
     * @param otherData
     */
    @Override
    public void mapReduceSource(@Nonnull final Object source, final Object... otherData) {
          throw new UnsupportedOperationException("Fix This"); // ToDo
    }


    /**
     * take the results of another engine and ues it as the input
     *
     * @param source some other engine - usually this will be cast to a specific type
     */
    //@Override
    public void chain(final IMapReduce source) {
        Iterable<KeyValueObject<KEYIN, VALUEIN>> sourceResults = source.collect();
        mapReduceSource(sourceResults);
    }

    /**
     * the last step in mapReduce - returns the output as an iterable
     *
     * @return
     */
    @Override public Iterable<KeyValueObject<KOUT,VOUT>> collect() {
        //noinspection unchecked
        return getResults().getList();
    }
}
