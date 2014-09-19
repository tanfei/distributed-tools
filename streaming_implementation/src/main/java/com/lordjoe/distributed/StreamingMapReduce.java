package com.lordjoe.distributed;

import com.lordjoe.distributed.util.*;
import com.lordjoe.distributed.wordcount.*;

import javax.annotation.*;
import java.io.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

/**
 * com.lordjoe.distributed.StreamingMapReduce
 * User: Steve
 * Date: 8/25/2014
 */
public class StreamingMapReduce<KEYIN extends Serializable, VALUEIN extends Serializable, K extends Serializable, V extends Serializable>
        extends AbstractMapReduceEngine<KEYIN, VALUEIN, K, V> implements Serializable {


    public static final MapReduceEngineFactory FACTORY = new MapReduceEngineFactory() {
        /**
         * build an engine having been passed a
         *
         * @param pMapper  map function
         * @param pRetucer reduce function
         * @return return a constructed instance
         */
        @Override public <KEYIN extends Serializable, VALUEIN extends Serializable, K extends Serializable, V extends Serializable> IMapReduce<KEYIN, VALUEIN, K, V> buildMEngine(@Nonnull final IMapperFunction<KEYIN, VALUEIN, K, V> pMapper, @Nonnull final IReducerFunction<K, V> pRetucer) {
            return new StreamingMapReduce(pMapper, pRetucer);
        }

        /**
         * build an engine having been passed a
         *
         * @param pMapper      map function
         * @param pRetucer     reduce function
         * @param pPartitioner partition function default is HashPartition
         * @return return a constructed instance
         */
        @Override public <KEYIN extends Serializable, VALUEIN extends Serializable, K extends Serializable, V extends Serializable> IMapReduce<KEYIN, VALUEIN, K, V> buildMEngine(@Nonnull final IMapperFunction<KEYIN, VALUEIN, K, V> pMapper, @Nonnull final IReducerFunction<K, V> pRetucer, final IPartitionFunction<K> pPartitioner) {
            return new StreamingMapReduce(pMapper, pRetucer, pPartitioner);
        }
    };

    ListConsumer<K, V> answer = new ListConsumer<K, V>();
    // NOTE these are not serializable so they must be transient or an exception will be thrown on serialization

    public StreamingMapReduce(final IMapperFunction<KEYIN, VALUEIN, K, V> mapper, final IReducerFunction<K, V> reducer) {
        //noinspection unchecked
        this(mapper, reducer, IPartitionFunction.HASH_PARTITION);
    }

    public StreamingMapReduce(final IMapperFunction<KEYIN, VALUEIN, K, V> pMapper,
                              final IReducerFunction<K, V> pRetucer,
                              IPartitionFunction<K> pPartitioner,
                              Consumer<KeyValueObject<K, V>>... pConsumer) {
        setMap(pMapper);
        setReduce(pRetucer);
        setPartitioner(pPartitioner);

        for (int i = 0; i < pConsumer.length; i++) {
            IKeyValueConsumer<K, V> cns = new KeyValueConsumerAdaptor<>(pConsumer[i]);
            addConsumer(cns);

        }
    }

//
//    protected Partitioner sparkPartitioner = new Partitioner() {
//        @Override public int numPartitions() {
//            return getNumberReducers();
//        }
//
//        @Override public int getPartition(final Object key) {
//            IPartitionFunction<K> partitioner = getPartitioner();
//            int value = partitioner.getPartition((K) key);
//            return value % numPartitions();
//        }
//    };

    /**
     * all the work is done here
     *
     * @param source
     * @param sink
     */
    public void performMapReduce(Stream<V> stream) {
//        Iterable iterable = getSource().readInput(source);
//        Iterator<V> iterator = iterable.iterator();
//
//        Spliterator<V> vSpliterator = Spliterators.spliteratorUnknownSize(iterator, 0);
//        Stream<V> stream = StreamSupport.stream(vSpliterator, false);
        final IMapperFunction map = getMap();
        Stream<KeyValueObject<K, V>> kvsMapped = stream.flatMap(new MapperAdaptor(map));

        // if not commented out this will force evaluation
        //kvsMapped =  StreamUtilities.realizeAndReturn(kvsMapped);    // checked slewis

        Stream<KeyValueObject<K, V>> sorted = kvsMapped.sorted(KeyValueObject.KEY_COMPARATOR);

        // if not commented out this will force evaluation
        //sorted =  StreamUtilities.realizeAndReturn(sorted);   // checked slewis

        Map<K, List<KeyValueObject<K, V>>> collect = sorted.collect(Collectors.groupingBy(kv -> kv.key));


        IReducerFunction reduce = getReduce();
        ReduceAdaptor ra = new ReduceAdaptor(reduce, collect, answer);

        Stream<K> sorted2 = collect.keySet().stream().sorted();
        sorted2.forEach(ra);


    }


    /**
     * sources may be very implementation specific
     *
     * @param source    some source of data - might be a hadoop directory or a Spark RDD - this will be cast internally
     * @param otherData
     */
    @Override public void mapReduceSource(@Nonnull final Object source, final Object... otherData) {
        if (source instanceof Stream) {
            performMapReduce((Stream) source);
            return;
        }
        if (source instanceof Collection) {
            performMapReduce(((Collection) source).stream());
            return;
        }
        if (source instanceof Iterable) {
            Iterable source1 = (Iterable) source;
            Iterator iterator = source1.iterator();
            Spliterator spliterator = Spliterators.spliteratorUnknownSize(iterator, 0);
            performMapReduce(StreamSupport.stream(spliterator, false));
            return;
        }
    }


    /**
     * take the results of another engine and ues it as the input
     *
     * @param source some other engine - usually this will be cast to a specific type
     */
    @Override public void chain(@Nonnull final IMapReduce<?, ?, KEYIN, VALUEIN> source) {
        performMapReduce(((StreamingMapReduce) source).answer.getList().stream());
    }

    /**
     * the last step in mapReduce - returns the output as an iterable
     *
     * @return
     */
    @Nonnull @Override public Iterable<KeyValueObject<K, V>> collect() {
        return answer.getList();
    }
}
