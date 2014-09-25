package org.systemsbiology.xtandem.hadoop;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;

/**
 * expects keys of the form FileName:ScanID - every file goes to a different reducer
 */
public class FileNamePartitioner extends Partitioner<Text, Text> {
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
        if (s.length() == 0)
            return 0;
        int fileNameIndex = s.indexOf(":");
        if(fileNameIndex == -1)
            return 0;

        int hash = s.substring(0,fileNameIndex).hashCode();
        return hash % numPartitions;
    }
}
