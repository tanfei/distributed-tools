package com.lordjoe.distributed;

import com.lordjoe.distributed.util.*;
import com.lordjoe.distributed.wordcount.*;

import java.io.*;

/**
 * com.lordjoe.distributed.Java7WordCount
 * User: Steve
 * Date: 8/25/2014
 */
public class Java7WordCount {

    public static final String MY_BOOK = "/war_and_peace.txt";

    public static Iterable<String> getFileLines()
    {
        final InputStream is = Java7WordCount.class.getResourceAsStream(MY_BOOK);
        return JavaMapReduceUtilities.fromPath( is);
     }


    public static void main(String[] args) {

         JavaMapReduce handler = new JavaMapReduce(new WordCountMapper(),new WordCountReducer());

        Iterable<String> lines = getFileLines();
        handler.performSourceMapReduce(lines);



    }
}
