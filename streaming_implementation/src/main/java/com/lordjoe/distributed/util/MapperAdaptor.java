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
public class MapperAdaptor<KEYIN extends Serializable,VALUEIN extends Serializable,KOUT extends Serializable,VOUT extends Serializable> implements Function<  KeyValueObject<KEYIN,VALUEIN>, Stream<KeyValueObject<KOUT,VOUT>>> {

    private final IMapperFunction<KEYIN,VALUEIN,KOUT,VOUT> mapper;

    public MapperAdaptor(final IMapperFunction<KEYIN,VALUEIN, KOUT, VOUT> pMapper) {
        mapper = pMapper;
    }



    /**
     * Applies this function to the given argument.
     *
     * @param t the function argument
     * @return the function result
     */
    @Override public Stream<KeyValueObject<KOUT, VOUT>> apply(final KeyValueObject<KEYIN,VALUEIN> t) {
        Iterable<KeyValueObject<KOUT, VOUT>> keyValueObjects = mapper.mapValues(t.key,t.value);
        return StreamSupport.stream(keyValueObjects.spliterator(),false);
    }


}
