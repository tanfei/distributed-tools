package com.lordjoe.distributed;

import javax.annotation.*;
import java.io.*;

/**
 * com.lordjoe.distributed.IMapReduce
 * User: Steve
 * Date: 8/25/2014
 */
public interface IMapReduce<K   extends Serializable,V   extends Serializable>{

    /**
     * perform any steps before mapping
     */
    public void setupMap();

    /**
     * this is what a Mapper does
     * @param value  input value
     * @return iterator over mapped key values
     */
    public @Nonnull Iterable<KeyValueObject<K,V>> mapValues(@Nonnull V value);

    /**
     * preform any steps after mapping
     */
    public void cleanUpMap();

    /**
     * perform any steps before mapping
     */
    public void setupReduce();

    /**
     * this is what a reducer does
     * @param value  input value
     * @return iterator over mapped key values
     */
    public @Nonnull Iterable<KeyValueObject<K,V>> returnValues(@Nonnull K key,@Nonnull Iterable<V> values);

}
