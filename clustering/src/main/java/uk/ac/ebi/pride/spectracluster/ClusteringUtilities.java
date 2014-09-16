package uk.ac.ebi.pride.spectracluster;

import com.lordjoe.distributed.*;

import java.io.*;
import java.util.*;

/**
 * uk.ac.ebi.pride.spectracluster.ClusteringUtilities
 * User: Steve
 * Date: 9/2/2014
 */
public class ClusteringUtilities {

    public static <K extends Serializable,V extends Serializable> Iterable<V>  fromKeyValues(final Iterable<KeyValueObject<K ,V >> in)
    {
        final Iterator<KeyValueObject<K , V >> iterator = in.iterator();
        return new Iterable<V>() {
            @Override public Iterator<V> iterator() {
                return new Iterator<V>() {
                    @Override public boolean hasNext() {
                        return iterator.hasNext();
                    }

                    @Override public V next() {
                        return iterator.next().value;
                    }

                    @Override public void remove() {
                       throw new UnsupportedOperationException("Not supported");
                    }
                };
            }
        };
    }

}
