package uk.ac.ebi.pride.spectracluster;

import com.lordjoe.distributed.*;
import com.lordjoe.distributed.util.*;
import com.lordjoe.utilities.*;
import uk.ac.ebi.pride.spectracluster.cluster.*;
import uk.ac.ebi.pride.spectracluster.hadoop.*;
import uk.ac.ebi.pride.spectracluster.keys.IPartitionable;
import uk.ac.ebi.pride.spectracluster.keys.*;

import java.nio.file.*;
import java.util.*;

/**
 * test.java.uk.ac.ebi.pride.spectracluster.ClusteringMain
 * User: Steve
 * Date: 9/2/2014
 */
public class SparkClusteringMain {

    /**
      * default implementation use hash code
      */
     public static final IPartitionFunction  PARTITIONER = new IPartitionFunction() {
         @Override public  int getPartition(final Object  inp) {

             int ret = ((IPartitionable)inp).getPartitionHash();
             if(ret < 0)
                 ret = -ret; // % does not work on negative numbers
             return ret;
         }
     };


    /**
      * default implementation use hash code
      */
     public static final IPartitionFunction  MZ_PARTITIONER = new IPartitionFunction() {
         @Override public  int getPartition(final Object  inp) {

             return((MZKey)inp).getAsInt();

         }
     };

    public static void reportResults(Iterable<ICluster> inp) {
        for (ICluster iCluster : inp) {
            System.out.println(iCluster);
        }

    }

    public static void main(String[] args) {

        ElapsedTimer timer = new ElapsedTimer();
        Path inp = FileSystems.getDefault().getPath(args[0]);
        Path out = FileSystems.getDefault().getPath(args[1]);


        PathMgfSource pathMgfSource = new PathMgfSource();
        Iterable<ICluster> inputs = pathMgfSource.readClusters(inp);

        timer.showElapsed("Read Clusters " + ((List)inputs).size());
        timer.reset();

        ListKeyValueConsumer holder = new ListKeyValueConsumer();
        SparkMapReduce handler = new SparkMapReduce(new PeakKeyMapper(),
                new PeakKeyEngineReducer(),
                PARTITIONER,
                holder);
         handler.performSourceMapReduce(inputs);
        Iterable<ICluster> serializables = ClusteringUtilities.fromKeyValues(holder.getValues());

        timer.showElapsed("Finished Peak Clustering  number clusters " + holder.size());
        timer.reset();

        holder = new ListKeyValueConsumer();
        handler = new SparkMapReduce(new BinKeyMapper(),
                new BinKeyEngineReducer(HadoopDefaults.getSpectrumMergeMZWindowSize() / 4),
                PARTITIONER,
                holder);
         handler.performSourceMapReduce(serializables);

        timer.showElapsed("Finished Merge Pass 1 number clusters "  + holder.size());
        timer.reset();
        serializables = ClusteringUtilities.fromKeyValues(holder.getValues());
        holder = new ListKeyValueConsumer();

        handler = new SparkMapReduce(new BinKeyMapper(),
                new BinKeyEngineReducer(HadoopDefaults.getSpectrumMergeMZWindowSize()),
                PARTITIONER,
                holder);
          handler.performSourceMapReduce(serializables);

        timer.showElapsed("Finished  Merge Pass 2  number clusters "  + holder.size());

        serializables = ClusteringUtilities.fromKeyValues(holder.getValues());

        try (PathOutputMZConsumer outFiles = new PathOutputMZConsumer(out)) {
            handler = new SparkMapReduce(new MzKeyMapper(),
                   new ConsolidatingReducer(),
                   MZ_PARTITIONER,
                   outFiles);
            handler.performSourceMapReduce(serializables);
        }


        timer.showElapsed("Finished Consolidation");
        timer.reset();


    }


}
