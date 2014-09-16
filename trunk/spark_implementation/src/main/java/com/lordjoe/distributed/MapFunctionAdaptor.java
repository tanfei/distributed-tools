package com.lordjoe.distributed;

import org.apache.spark.api.java.function.*;

import java.io.*;

/**
 * com.lordjoe.distributed.MapFunctionAdaptor
 * User: Steve
 * Date: 8/28/2014
 */
public class MapFunctionAdaptor<VALUEIN,KOUT extends java.io.Serializable,VOUT extends Serializable> implements FlatMapFunction<VALUEIN,KeyValueObject<KOUT,VOUT>>,Serializable{

    private final IMapperFunction<VALUEIN,KOUT,VOUT> mapper;

    public MapFunctionAdaptor(final IMapperFunction< VALUEIN, KOUT, VOUT> pMapper) {
        mapper = pMapper;
    }

    @Override public Iterable<KeyValueObject<KOUT, VOUT>> call(final VALUEIN t) throws Exception {
        Iterable<KeyValueObject<KOUT, VOUT>> keyValueObjects = mapper.mapValues(t);
        return keyValueObjects;
        //.toTuples(keyValueObjects);
    }
}
