package com.lordjoe.distributed.test;

import com.lordjoe.distributed.*;
import com.lordjoe.distributed.util.*;
import com.lordjoe.distributed.wordcount.*;

import java.io.*;
import java.util.*;

/**
 * com.lordjoe.distributed.WordCountOperator
 * User: Steve
 * Date: 9/18/2014
 */
public class WordCountOperator {

    public static final String MY_BOOK = "/war_and_peace.txt";
    public static final String BOOK_COUNTS = "/war_and_peace_counts.txt";

    public static Iterable<KeyValueObject<String,String>> getFileLines()
    {
        final InputStream is = WordCountOperator.class.getResourceAsStream(MY_BOOK);
        return JavaMapReduceUtilities.fromPath(MY_BOOK, is);
     }

    public static List<KeyValueObject<String, Integer>> getRealCounts()
    {
        final InputStream is = WordCountOperator.class.getResourceAsStream(BOOK_COUNTS);
        String[] strings = JavaMapReduceUtilities.readStreamLines(is);
        List<KeyValueObject<String, Integer>> holder = new ArrayList<KeyValueObject<String, Integer>>();
        for (String string : strings) {
            String[] items = string.split(":");
            holder.add(new KeyValueObject<String, Integer>(items[0],Integer.parseInt(items[1])));
        }

        return holder;
    }


    public static void validateWordCount(MapReduceEngineFactory factory) {


        IMapReduce handler = factory.buildMapReduceEngine("WordCount", new WordCountMapper(), new WordCountReducer());

        Iterable<KeyValueObject<String,String>> lines = getFileLines();
        handler.mapReduceSource(lines);

        Iterable<KeyValueObject<String,Integer>> results = handler.collect();

        List<KeyValueObject<String, Integer>> real = getRealCounts();
        Collections.sort(real);

        Iterator<KeyValueObject<String, Integer>> iterator = results.iterator();
          for (KeyValueObject<String, Integer> realCount : real) {
              KeyValueObject<String, Integer> next = iterator.next();
              if(!realCount.equals(next))
                throw new IllegalStateException("Test Failed"); // this drops dependency on JUnit
        }

     }
}
