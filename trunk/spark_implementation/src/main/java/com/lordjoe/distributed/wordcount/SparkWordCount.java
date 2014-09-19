package com.lordjoe.distributed.wordcount;

import com.lordjoe.distributed.*;
import com.lordjoe.distributed.util.*;
import org.apache.spark.api.java.*;

import java.io.*;

/**
 * com.lordjoe.distributed.SparkWordCount
 * User: Steve
 * Date: 9/2/2014
 */
public class SparkWordCount {
    public static final String MY_BOOK = "/war_and_peace.txt";



    public static void main(String[] args) {
        ListKeyValueConsumer<String,Integer> consumer = new ListKeyValueConsumer();
         SparkMapReduce handler = new SparkMapReduce(new WordCountMapper(),new WordCountReducer(),IPartitionFunction.HASH_PARTITION,consumer);
        JavaSparkContext ctx = handler.getCtx();

        JavaRDD<KeyValueObject<String,String>> lines;
        if(args.length == 0) {
            final InputStream is = SparkWordCount.class.getResourceAsStream(MY_BOOK);
             lines = SparkUtilities.keysFromInputStream(MY_BOOK, is, ctx);
        }
        else {
              lines = null;
            throw new UnsupportedOperationException("Fix This"); // ToDo
         //   lines = ctx.textFile(args[0], 1);
        }

          handler.performSourceMapReduce(lines);

        Iterable<KeyValueObject<String,Integer>> answer = handler.collect();
        for (KeyValueObject<String,Integer> o : answer) {
            System.out.println(o.toString());
        }

     }
}
