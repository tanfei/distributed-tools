package com.lordjoe.distributed;

import java.io.*;
import java.util.*;

/**
 * com.lordjoe.distributed.AbstractMapReduceEngine
 * User: Steve
 * Date: 8/28/2014
 */
public abstract class AbstractMapReduceEngine<KEYIN extends Serializable,VIN extends Serializable, KEYOUT extends Serializable, VOUT extends Serializable> implements IMapReduce<KEYIN,VIN, KEYOUT, VOUT> {

    public static final int DEFAULT_NUMBER_REDUCERS = 20;

    private int numberReducers = DEFAULT_NUMBER_REDUCERS;
      private IMapperFunction<KEYIN,VIN, KEYOUT, VOUT> map;
    private IReducerFunction<KEYOUT, VOUT> reduce;
    @SuppressWarnings("unchecked")
    private IPartitionFunction<KEYOUT> partitioner = IPartitionFunction.HASH_PARTITION;
    private List<IKeyValueConsumer<KEYOUT, VOUT>> consumers = new ArrayList<IKeyValueConsumer<KEYOUT, VOUT>>();

    protected AbstractMapReduceEngine() {
    }


    public AbstractMapReduceEngine setMap(final IMapperFunction<KEYIN,VIN, KEYOUT, VOUT> pMap) {
        map = pMap;
        return this;
    }

    public AbstractMapReduceEngine setReduce(final IReducerFunction<KEYOUT, VOUT> pReduce) {
        reduce = pReduce;
        return this;
    }

    public AbstractMapReduceEngine setPartitioner(final IPartitionFunction<KEYOUT> p) {
        partitioner = p;
        return this;
    }



    @SuppressWarnings("UnusedDeclaration")
    public AbstractMapReduceEngine setNumberReducers(int n) {
        numberReducers = n;
        return this;
    }

    public AbstractMapReduceEngine addConsumer(IKeyValueConsumer<KEYOUT, VOUT> c) {
        consumers.add(c);
        return this;
    }

    protected List<IKeyValueConsumer<KEYOUT, VOUT>> getConsumers() {
        return consumers;
    }


    public IMapperFunction getMap() {
        return map;
    }

    public IReducerFunction getReduce() {
        return reduce;
    }


    public IPartitionFunction<KEYOUT> getPartitioner() {
        return partitioner;
    }

    public int getNumberReducers() {
        return numberReducers;
    }



}
