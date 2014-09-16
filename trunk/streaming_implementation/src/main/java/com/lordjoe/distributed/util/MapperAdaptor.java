package com.lordjoe.distributed.util;

import com.lordjoe.distributed.*;

import java.io.*;
import java.util.function.*;
import java.util.stream.*;

/**
 * com.lordjoe.distributed.util.MapperAdaptor
 * User: Steve
 * Date: 8/29/2014
 */
public class MapperAdaptor<VALUEIN,KOUT extends Serializable,VOUT extends Serializable> implements Function< VALUEIN, Stream<KeyValueObject<KOUT,VOUT>>> {

     private final IMapperFunction<VALUEIN,KOUT,VOUT> mapper;

    public MapperAdaptor(final IMapperFunction<VALUEIN, KOUT, VOUT> pMapper) {
        mapper = pMapper;
    }

    /**
     * Applies this function to the given argument.
     *
     * @param t the function argument
     * @return the function result
     */
    @Override public Stream<KeyValueObject<KOUT, VOUT>> apply(final VALUEIN t) {
        Iterable<KeyValueObject<KOUT, VOUT>> keyValueObjects = mapper.mapValues(t);
        return StreamSupport.stream(keyValueObjects.spliterator(),false);
    }


}
