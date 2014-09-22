package com.lordjoe.distributed;


import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;

import java.io.*;

/**
 * com.lordjoe.distributed.PartitionAdaptor
 * User: Steve
 * Date: 9/4/2014
 */

public class PartitionAdaptor<K,V>  extends Partitioner<Text, Text> implements Serializable {
    private final IPartitionFunction<K> partitioner;
    private final IStringSerializer<K> serializer;

    public PartitionAdaptor(final IPartitionFunction<K> pPartitioner, final IStringSerializer<K> pSerializer) {
        partitioner = pPartitioner;
        serializer = pSerializer;
    }

    @Override
    public int getPartition(final Text pKey, final Text pValue, final int nPartitions) {
        K k = serializer.fromString(pKey.toString());  // todo handle number partitions
        return partitioner.getPartition(k);
    }


}
