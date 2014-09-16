package com.lordjoe.distributed;

import com.lordjoe.distributed.util.*;

import javax.annotation.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

/**
 * com.lordjoe.distributed.StreamingMapReduce
 * User: Steve
 * Date: 8/25/2014
 */
public class StreamingMapReduce<K extends Serializable,V extends Serializable> extends AbstractMapReduceEngine<K,V> {


    /**
     * all the work is done here
     *
     * @param source
     * @param sink
     */
    @Override public void performMapReduce(final Path source, final Path sink) {
        Iterator<V> iterator = getSource().readInput(source);

        Spliterator<V> vSpliterator = Spliterators.spliteratorUnknownSize(iterator, 0);
        Stream<V> stream = StreamSupport.stream(vSpliterator, false);
        Stream<KeyValueObject<K, V>> keyValueObjectStream = stream.flatMap(new MapperAdaptor<V, K,V>(getMap()));

        Map<K, List<KeyValueObject<K, V>>> collect = keyValueObjectStream.collect(Collectors.groupingBy(kv -> kv.key));



    }

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
