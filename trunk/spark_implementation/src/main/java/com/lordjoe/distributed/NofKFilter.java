package com.lordjoe.distributed;

import java.io.*;

/**
 * com.lordjoe.distributed.NofKFilter
 * a filter to take a fraction of an RDD
 * persist and then call repeatedly -
 * for example to score in 5 batches call
 *   JavaRDD<MyType> t
 *   t = t.persist();
 *   for(int i = 0; i < 5; i++ ) {
 *        JavaRDD<MyType> current = t.filter(new NofKFilter(5,i) ;
 *        do stuff
 *   }
 *
 *
 * User: Steve
 * Date: 12/2/2014
 */
public class NofKFilter<T extends Serializable> extends AbstractLoggingFunction<T,Boolean> {

    private final int setSize;
    private final int setStart;
    private transient long index;

    public NofKFilter(final int pSetSize, final int pSetStart) {
        setSize = pSetSize;
        setStart = pSetStart;
        if(setSize == 0)
            throw new IllegalArgumentException("set size must be > 1");
        if(setStart  >= setSize )
             throw new IllegalArgumentException("set start must be < setSize");
     }

    /**
     * do work here
     *
     * @param v1
     * @return
     */
    @Override
    public Boolean doCall(final T v1) throws Exception {
        return (((index++ + setStart) % setSize) == 0);
    }
}
