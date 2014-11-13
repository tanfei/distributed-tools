package com.lordjoe.distributed;

import org.apache.spark.api.java.function.*;

import java.io.*;

/**
 * org.apache.spark.api.java.function.AbstraceLoggingFunction
 * superclass for defined functions that will log on first call making it easier to see
 * do work in doCall
 * User: Steve
 * Date: 10/23/2014
 */
public abstract class AbstractLoggingFunction<K extends Serializable,V extends Serializable>
        extends AbstractLoggingFunctionBase implements Function<K,V> {


    /**
     * override doCall
     * @param v1
     * @return
     * @throws Exception
     */
    @Override
    public final V call(final K v1)  {
       reportCalls();
        try {
            return doCall(v1);
        }
        catch (Exception e) {
            throw new RuntimeException(e);

        }
    }

    /**
     * do work here
     * @param v1
     * @return
     */
    public abstract V doCall(final K v1)  throws Exception;
}
