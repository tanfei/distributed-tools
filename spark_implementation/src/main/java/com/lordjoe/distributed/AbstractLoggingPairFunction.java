package com.lordjoe.distributed;

import org.apache.spark.api.java.function.*;
import scala.*;

import java.io.Serializable;

/**
 * org.apache.spark.api.java.function.AbstraceLoggingFunction
 * superclass for defined functions that will log on first call making it easier to see
 * do work in doCall
 * User: Steve
 * Date: 10/23/2014
 */
public abstract class AbstractLoggingPairFunction<T extends Serializable,K extends Serializable,V extends Serializable> implements PairFunction<T,K,V> {

    private boolean logged;

    public boolean isLogged() {
        return logged;
    }

    public void setLogged(final boolean pLogged) {
        logged = pLogged;
    }

    /**
     * NOTE override doCall not this
     * @param t
     * @return
     */
    @Override
    public final  Tuple2<K, V> call(final T t)  {
        if(!logged)  {
            System.err.println("Starting Function " + getClass().getSimpleName());
            setLogged(true);
        }
        try {
            return doCall(t);
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

    public abstract Tuple2<K, V> doCall(final T t) throws Exception;

}
