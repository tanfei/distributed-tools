package com.lordjoe.distributed;

import org.apache.spark.api.java.function.*;
import scala.*;
import scala.collection.*;

/**
 * com.lordjoe.distributed.PartitionAdaptor
 * User: Steve
 * Date: 9/4/2014
 */

public class PartitionAdaptor<K,V>  implements FlatMapFunction<Iterator<Tuple2<K,V>>,Tuple2<K,V>>, java.io.Serializable {
      private final IPartitionFunction<K> partitioner;

    public PartitionAdaptor(final IPartitionFunction<K> pPartitioner) {
        partitioner = pPartitioner;
    }

    @Override public java.lang.Iterable<Tuple2<K, V>> call(final Iterator<Tuple2<K, V>> t) throws Exception {
        return null;
    }
}
