package com.lordjoe.distributed;

import com.lordjoe.distributed.util.*;
import org.junit.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

import static com.lordjoe.distributed.util.LineToWords.*;

/**
 * com.lordjoe.distributed.StreamigWordCount
 * User: Steve
 * Date: 8/25/2014
 */
public class StreamingWordCount {

    public static final String MY_BOOK = "/war_and_peace.txt";

    public static Stream<String> getFileLines()
    {
        InputStream is = StreamingWordCount.class.getResourceAsStream(MY_BOOK);
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        return br.lines();
     }

    public static Stream<String> getLineWords(String s)
    {
       return StreamSupport.stream(fromLine(s).spliterator(),false);
  }


    @Test
    public void testMapReduce() throws Exception
    {
        LineToWords splitter = new LineToWords();
        Stream<String> fileLines = getFileLines();
        // Map step
        Stream<WordNumber> wordCount =
               fileLines
              .flatMap(s -> Stream.of(splitLine(s))) //                    .flatMap(s -> Stream.of(s.split("\\s+")))
              .map(s -> new WordNumber(s,1))
               .sorted();


        wordCount.collect(Collector)
        // reduce step
     //   wordCount.collect(Collectors.groupingBy(s -> s.getWord())).


    //    wordCount.forEach(p -> System.out.println(p));
    //   List<WordNumber> result =
    //           wordCount.collect(Collectors.groupingBy(WordNumber::getWord );


    }

    @Test
    public void testMapping() throws Exception
    {
        LineToWords splitter = new LineToWords();
        Stream<String> fileLines = getFileLines();
        Map<String, Integer> wordCount =
               fileLines
              .flatMap(s -> Stream.of(splitLine(s))) //                    .flatMap(s -> Stream.of(s.split("\\s+")))
               .collect(Collectors
                       .toMap(s -> s, s -> 1, Integer::sum));


        for (String s : wordCount.keySet()) {
            System.out.println(s + ":" + wordCount.get(s));
        }


    }

    public static void main(String[] args) {
        Path path = new File(args[0]).toPath();

    }
}
