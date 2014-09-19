package com.lordjoe.distributed.util;

import com.lordjoe.distributed.*;

import java.io.*;
import java.util.function.*;

/**
 * com.lordjoe.distributed.util.KeyValueConsumerAdaptor
 * User: Steve
 * Date: 9/17/2014
 */
public class KeyValueConsumerAdaptor<K extends Serializable, V extends Serializable> implements IKeyValueConsumer<K,V> {

    private final Consumer<KeyValueObject<K, V>> realConsumer;

    public KeyValueConsumerAdaptor(final Consumer<KeyValueObject<K, V>> pRealConsumer) {
        realConsumer = pRealConsumer;
    }

    @Override public void consume(final KeyValueObject<K, V> kv) {
        realConsumer.accept(kv);
    }
}
