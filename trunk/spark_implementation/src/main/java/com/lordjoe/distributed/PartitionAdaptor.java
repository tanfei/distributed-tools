package com.lordjoe.distributed;

import org.apache.spark.*;

/**
 * com.lordjoe.distributed.PartitionAdaptor
 * User: Steve
 * Date: 9/4/2014
 */

public class PartitionAdaptor<K>  extends Partitioner {
    private final IPartitionFunction<K> partitioner;
    private final int numberPartitions;

    public PartitionAdaptor(final IPartitionFunction<K> pPartitioner,int pnumberPartitions) {
        partitioner = pPartitioner;
        numberPartitions = pnumberPartitions;
    }

    @Override
    public int numPartitions() {
        return numberPartitions;
    }

    @Override
    public int getPartition(final Object inp) {
        return partitioner.getPartition((K)inp);
    }
}
