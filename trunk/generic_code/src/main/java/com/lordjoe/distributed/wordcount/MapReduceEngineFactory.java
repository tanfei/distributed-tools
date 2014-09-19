package com.lordjoe.distributed.wordcount;

import com.lordjoe.distributed.*;

import javax.annotation.*;
import java.io.*;

/**
 * com.lordjoe.distributed.wordcount.MapReductEngineFactory
 * User: Steve
 * Date: 9/18/2014
 */
public interface MapReduceEngineFactory {


    /**
     * build an engine having been passed a
     * @param pMapper   map function
     * @param pRetucer  reduce function
      * @param <KEYIN>  type of input key
     * @param <VALUEIN>  type of input value
     * @param <K>   type of output key
     * @param <V>   type of output value
     * @return return a constructed instance
     */
    public <KEYIN extends Serializable, VALUEIN extends Serializable, K extends Serializable, V extends Serializable>
      IMapReduce<KEYIN, VALUEIN, K, V> buildMEngine(@Nonnull IMapperFunction<KEYIN, VALUEIN, K, V> pMapper,
                                                           @Nonnull IReducerFunction<K, V> pRetucer);

    /**
     * build an engine having been passed a
     * @param pMapper   map function
     * @param pRetucer  reduce function
     * @param pPartitioner  partition function default is HashPartition
     * @param <KEYIN>  type of input key
     * @param <VALUEIN>  type of input value
     * @param <K>   type of output key
     * @param <V>   type of output value
     * @return return a constructed instance
     */
    @SuppressWarnings("UnusedDeclaration")
    public <KEYIN extends Serializable, VALUEIN extends Serializable, K extends Serializable, V extends Serializable>
      IMapReduce<KEYIN, VALUEIN, K, V> buildMEngine(@Nonnull IMapperFunction<KEYIN, VALUEIN, K, V> pMapper,
                                                           @Nonnull IReducerFunction<K, V> pRetucer,
                                                           IPartitionFunction<K> pPartitioner);

}
