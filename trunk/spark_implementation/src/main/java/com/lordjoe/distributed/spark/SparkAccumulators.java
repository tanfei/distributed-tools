package com.lordjoe.distributed.spark;

import com.lordjoe.distributed.*;
import com.lordjoe.distributed.wordcount.*;
import org.apache.spark.*;
import org.apache.spark.api.java.*;

import java.io.*;
import java.util.*;

/**
 * com.lordjoe.distributed.spark.SparkAccumulators
 * this class implements a similar ides to Hadoop Accumulators
 * User: Steve
 * Date: 11/12/2014
 */
public class SparkAccumulators implements Serializable {

    public static final int MAX_TRACKED_THREADS = 16;
    private static  SparkAccumulators instance;

    public static SparkAccumulators getInstance() {
           return instance;
    }

    public static void createInstance() {
           instance = new SparkAccumulators();
        for (int i = 0; i < MAX_TRACKED_THREADS; i++) {
             instance.createAccumulator(ThreadUseLogger.getThreadAccumulatorName(i));
        }
      }

    // this is a singleton and should be serialized
    private SparkAccumulators() {
     }

    /**
     * holds accumulators by name
     */
    private final Map<String, Accumulator<Integer>> accumulators = new HashMap<String, Accumulator<Integer>>();



    /**
     * append lines for all accumulators to an appendable
     * NOTE - call only in the Executor
     * @param out where to append
     */
    public static void showAccumulators(Appendable out) {
        try {
            SparkAccumulators me = getInstance();
            List<String> accumulatorNames = me.getAccumulatorNames();
            for (String accumulatorName : accumulatorNames) {
                Accumulator<Integer> accumulator = me.getAccumulator(accumulatorName);
                Integer value = accumulator.value();
                out.append(accumulatorName + " " + value + "\n");
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);

        }
    }

    
    public static class LongAccumulableParam implements AccumulableParam<Long,Long>,Serializable {
         @Override
        public Long addAccumulator(final Long r, final Long t) {
            return r + t;
        }
         @Override
        public Long addInPlace(final Long r1, final Long r2) {
            return  r1 + r2;
        }
         @Override
        public Long zero(final Long initialValue) {
            return 0L;
        }
    }

    /**
     *
     * must be called in the Executor before accumulators can be used
     * @param acc
     */
    public static void createAccumulator(String acc) {
        SparkAccumulators me = getInstance();
          if (me.accumulators.get(acc) != null)
            return; // already done - should an exception be thrown
        JavaSparkContext currentContext = SparkUtilities.getCurrentContext();
        Accumulator<Integer> accumulator = currentContext.accumulator(0, acc );

        me.accumulators.put(acc,accumulator);
    }


    /**
     * append lines for all accumulators to System.out
     * NOTE - call only in the Executor
     */
    public static void showAccumulators(  ) {
        showAccumulators(System.out);
    }


    /**
     * return all registerd aaccumlators
     * @return
     */
    public List<String> getAccumulatorNames( ) {
         List<String> keys = new ArrayList<String>(accumulators.keySet());
         Collections.sort(keys);  // alphapetize
         return keys;
    }

    /**
     * how much work are we spreading across threads
     */
    public void incrementThreadAccumulator()
    {
        int threadNumber = ThreadUseLogger.getThreadNumber();
        if(threadNumber > MAX_TRACKED_THREADS)
            return; // too many threads
        incrementAccumulator(ThreadUseLogger.getThreadAccumulatorName(threadNumber));
      }


    /**
     * true is an accumulator exists
     */
    public boolean isAccumulatorRegistered(String acc) {
        return accumulators.containsKey(acc) ;
    }

    /**
     * @param acc name of am existing accumulator
      * @return !null existing accumulator
     */
    public Accumulator<Integer> getAccumulator(String acc) {
        Accumulator<Integer> ret = accumulators.get(acc);
        if (ret == null)
            throw new IllegalStateException("Accumulators need to be created in advance in the executor");
        return ret;
    }

    /**
     * add one to an existing accumulator
     * @param acc
     */
    public void incrementAccumulator(String acc) {
        incrementAccumulator(acc, 1);
    }

    /**
     * add added to an existing accumulator
     * @param acc name of am existing accumulator
     * @param added  amount to add
     */
    public void incrementAccumulator(String acc, int added) {
        Accumulator<Integer> accumulator = getAccumulator(acc);
        accumulator.add(added);
    }

}
