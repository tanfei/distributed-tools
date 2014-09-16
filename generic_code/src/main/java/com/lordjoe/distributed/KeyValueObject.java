package com.lordjoe.distributed;

import javax.annotation.*;
import java.io.*;
import java.util.*;

/**
 * com.lordjoe.distributed.IKeyValue
 * User: Steve
 * Date: 8/25/2014
 */
public class KeyValueObject<K   extends Serializable,V  extends Serializable> implements Serializable  {

    /**
     * compare by keys using String as a last resort comparator
     */
    public static  final Comparator<KeyValueObject > KEY_COMPARATOR = new Comparator<KeyValueObject>() {

        @Override public int compare(final KeyValueObject o1, final KeyValueObject o2) {
            Object key = o1.key;
            if(key instanceof Comparable) {
                return ((Comparable) key).compareTo(o2.key) ;
              }
             else {
                return key.toString().compareTo(o2.key.toString());
            }
        }


    } ;
    public final K key;
    public final V value;

    public KeyValueObject(final @Nonnull K pKey, final @Nonnull V pValue) {
        key = pKey;
        value = pValue;
        if(! (key instanceof Serializable) || !(value instanceof Serializable))
          throw new IllegalArgumentException("problem"); // ToDo change
    }

    @Override public String toString() {
        return  key.toString() +
                ":" + value;
    }


}
