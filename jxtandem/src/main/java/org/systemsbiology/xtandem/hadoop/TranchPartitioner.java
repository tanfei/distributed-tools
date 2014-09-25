package org.systemsbiology.xtandem.hadoop;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;

/**
 * org.systemsbiology.xtandem.hadoop.TranchPartitioner
 * User: Steve
 * Date: Apr 6, 2011
 */
public class TranchPartitioner extends Partitioner<Text, Text> {
    public static final TranchPartitioner[] EMPTY_ARRAY = {};
    public static final Class THIS_CLASS = TranchPartitioner.class;

    /**
     * Get the partition number for a given key (hence record) given the total
     * number of partitions i.e. number of reduce-tasks for the job.
     * <p/>
     * <p>Typically a hash function on a all or a subset of the key.</p>
     *
     * @param key           the key to be partioned.
     * @param value         the entry value.
     * @param numPartitions the total number of partitions.
     * @return the partition number for the <code>key</code>.
     */
    @Override
    public int getPartition(Text key, Text value, int numPartitions) {
        String s = key.toString();
        String[] split = s.split("|");
        int index = Integer.parseInt(split[0]);
        return index % numPartitions; // to do make better

    }
}
