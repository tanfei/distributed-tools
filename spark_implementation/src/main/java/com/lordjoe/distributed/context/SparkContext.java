package com.lordjoe.distributed.context;

import org.apache.spark.*;
import org.apache.spark.api.java.*;

/**
 * com.lordjoe.distributed.context.SparkContext
 * User: Steve
 * Date: 9/24/2014
 */
public class SparkContext {

    private final SparkConf sparkConf;
    private  JavaSparkContext ctx;

    public SparkContext(String name) {
        sparkConf = new SparkConf().setAppName(name);
    }


    public SparkConf getSparkConf() {
        return sparkConf;
    }

    public JavaSparkContext getCtx() {
        if(ctx == null)
            ctx = new JavaSparkContext(sparkConf);
        return ctx;
    }
}
