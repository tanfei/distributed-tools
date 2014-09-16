package com.lordjoe.distributed;

import javax.annotation.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * com.lordjoe.distributed.AbstractMapReduceEngine
 * User: Steve
 * Date: 8/28/2014
 */
public abstract class AbstractMapReduceEngine<VIN extends Serializable, K extends Serializable,V extends Serializable> implements IMapReduce<K,V> {

    public static final int DEFAULT_NUMBER_REDUCERS = 20;

    private int numberReducers = DEFAULT_NUMBER_REDUCERS;
    private ISourceFunction<VIN> source;
    private IMapperFunction<VIN,K,V> map;
    private IReducerFunction<K,V> reduce;
    private IPartitionFunction<K> partitioner = IPartitionFunction.HASH_PARTITION;
    private List<IKeyValueConsumer<K,V>> consumer = new ArrayList<IKeyValueConsumer<K,V>>();
    private ISinkFunction sink;

    protected AbstractMapReduceEngine() {
    }

    public AbstractMapReduceEngine setSource(final ISourceFunction<VIN> pSource) {
        source = pSource;
        return this;
    }

    public AbstractMapReduceEngine setMap(final IMapperFunction<VIN,K,V> pMap) {
        map = pMap;
        return this;
    }

    public AbstractMapReduceEngine setReduce(final IReducerFunction<K,V> pReduce) {
        reduce = pReduce;
        return this;
    }

    public AbstractMapReduceEngine setPartitioner(final IPartitionFunction<K> p) {
        partitioner = p;
        return this;
    }

    public AbstractMapReduceEngine setSink(final ISinkFunction<K,V> pSink) {
        sink = pSink;
        return this;
    }

    public AbstractMapReduceEngine setSink(final IPartitionFunction<K> pPartitioner) {
        partitioner = pPartitioner;
        return this;
    }

    public AbstractMapReduceEngine setNumberReducers(int n) {
        numberReducers = n;
        return this;
    }

    public AbstractMapReduceEngine addConsumer(IKeyValueConsumer<K, V> c) {
        consumer.add(c);
        return this;
    }

    protected List<IKeyValueConsumer<K,V>> getConsumers() {
        return consumer;
    }

    public ISourceFunction getSource() {
        return source;
    }

    public IMapperFunction getMap() {
        return map;
    }

    public IReducerFunction getReduce() {
        return reduce;
    }

    public ISinkFunction getSink() {
        return sink;
    }

    public IPartitionFunction<K> getPartitioner() {
        return partitioner;
    }

    public int getNumberReducers() {
        return numberReducers;
    }

    /**
     * all the work is done here
     * @param source
     * @param sink
     */
    public abstract void performMapReduce(Path source,Path sink);

    /**
     * perform any steps before mapping
     */
    @Override public void setupMap() {

    }

    /**
     * this is what a Mapper does
     *
     * @param value input value
     * @return iterator over mapped key values
     */
    @Nonnull @Override public Iterable<KeyValueObject<K, V>> mapValues(@Nonnull final V value) {
        return null;
    }

    /**
     * preform any steps after mapping
     */
    @Override public void cleanUpMap() {

    }

    /**
     * perform any steps before mapping
     */
    @Override public void setupReduce() {

    }

    /**
     * this is what a reducer does
     *
     * @param key
     * @param values
     * @return iterator over mapped key values
     */
    @Nonnull @Override public Iterable<KeyValueObject<K, V>> returnValues(@Nonnull final K key, @Nonnull final Iterable<V> values) {
        return null;
    }
}
