package com.lordjoe.distributed;

import com.lordjoe.distributed.wordcount.*;
import org.apache.spark.api.java.*;

/**
 * com.lordjoe.distributed.SparkWordCount
 * User: Steve
 * Date: 9/12/2014
 */
public class SparkWordCountTest {
    public static final String MY_BOOK = "/war_and_peace.txt";



    public static void main(String[] args) {
        SparkMapReduce handler = new SparkMapReduce<String,String,Integer>(new WordCountMapper(),new WordCountReducer());

//        Path inp = Paths.get(args[0]);
//        Path outp = Paths.get(args[1]);

        JavaSparkContext ctx = handler.getCtx();
        JavaRDD<String> lines = ctx.textFile(args[0], 1);
        handler.performSourceMapReduce(lines);



    }

}
