package com.lordjoe.distributed.util;

import com.lordjoe.distributed.*;

import java.io.*;
import java.util.*;
import java.util.function.*;

/**
 * com.lordjoe.distributed.util.ListConsumer
 * User: Steve
 * Date: 9/17/2014
 */
public class ListConsumer<K extends Serializable, V extends Serializable>
        implements Consumer<KeyValueObject<K, V>> {


    private  List<KeyValueObject<K, V>> list = new ArrayList<>();

    public Iterable<KeyValueObject<K, V>> getValues() {
        return list;
    }

    public List<KeyValueObject<K, V>> getList() {
        return list;
    }

    public int size() {
        return list.size();
    }

    /**
     * Performs this operation on the given argument.
     *
     * @param t the input argument
     */
    @Override public void accept(final KeyValueObject<K, V> t) {
        list.add(t);
    }
}
