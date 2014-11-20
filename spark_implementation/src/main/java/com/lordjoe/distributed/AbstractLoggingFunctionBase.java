package com.lordjoe.distributed;

import com.lordjoe.distributed.spark.*;

import java.io.*;

/**
 * org.apache.spark.api.java.function.AbstraceLoggingFunction
 * superclass for defined functions that will log on first call making it easier to see
 * do work in doCall
 * User: Steve
 * Date: 10/23/2014
 */
public abstract class AbstractLoggingFunctionBase implements Serializable {



    private static int callReportInterval = 50000;

    public static int getCallReportInterval() {
        return callReportInterval;
    }

    public static void setCallReportInterval(final int pCallReportInterval) {
        callReportInterval = pCallReportInterval;
    }

    private transient boolean logged;   // transient so every machine keeps its own
    private transient int numberCalls;   // transient so every machine keeps its own
    private SparkAccumulators accumulators; // member so it will be serialized from the executor

    protected AbstractLoggingFunctionBase() {
        if(!isFunctionCallsLogged())
              return;
        SparkAccumulators instance = SparkAccumulators.getInstance();
        if(instance != null)
            accumulators = instance; // might be null but serialization should set
        if(accumulators != null) {
            String className = getClass().getSimpleName();
            SparkAccumulators.createAccumulator(className);
        }
    }

    /**
     * Override this to prevent logging
     * @return
     */
    public boolean isFunctionCallsLogged() {
         return SparkAccumulators.isFunctionsLoggedByDefault();
     }

    public final boolean isLogged() {
        return logged;
    }

    public final void setLogged(final boolean pLogged) {
        logged = pLogged;
    }

    public final int getNumberCalls() {
        return numberCalls;
    }

    public final void incrementNumberCalled() {
        numberCalls++;
    }

    public SparkAccumulators getAccumulators() {
        return accumulators;
    }

    public void reportCalls() {
        if(!isFunctionCallsLogged())
            return;
        String className = getClass().getSimpleName();
        if ( !isLogged()) {
            System.err.println("Starting Function " + className);
            setLogged(true);
        }
        if (getCallReportInterval() > 0) {
            if (getNumberCalls() % getCallReportInterval() == 0)
                System.err.println("Calling Function " + className + " " + getNumberCalls() / 1000 + "k times");
        }
        incrementNumberCalled();
        SparkAccumulators accumulators1 = getAccumulators();
        if(accumulators1 == null)
            return;
        if ( accumulators1.isAccumulatorRegistered(className)) {
            accumulators1.incrementAccumulator(className);
        }
        if(SparkUtilities.isLocal()) {
            accumulators1.incrementThreadAccumulator(); // track which thread we are using
        }
        else {
            accumulators1.incrementThreadAccumulator(); // track which thread we are using
            accumulators1.incrementMachineAccumulator();
        }
    }

}
