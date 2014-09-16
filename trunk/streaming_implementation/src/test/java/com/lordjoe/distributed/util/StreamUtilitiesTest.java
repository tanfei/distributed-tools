package com.lordjoe.distributed.util;

import org.junit.*;

import java.util.*;
import java.util.stream.*;

/**
 * com.lordjoe.distributed.util.StreamUtilitiesTest
 * User: Steve
 * Date: 8/27/2014
 */
public class StreamUtilitiesTest {

    public static final int LIST_LENGTH = 10;

    public static Stream<String> buildTestStream(String s) {
        List<String> holder = buildTestList(s);
        return holder.stream();
    }

    private static List<String> buildTestList(final String s) {
        List<String> holder = new ArrayList<String>();
        for (int i = 0; i < LIST_LENGTH; i++) {
            holder.add(s + (i + 1));

        }
        String[] ret = new String[holder.size()];
        holder.toArray(ret);
        return holder;
    }

    public static final String[] prefixes = {"a", "b", "c", "d", "e", "f",};

    public static List<Stream<String>> buildTestStreams() {
        List<Stream<String>> holder = new ArrayList<Stream<String>>();
        for (int i = 0; i < prefixes.length; i++) {
            String prefix = prefixes[i];
            holder.add(buildTestStream(prefix));
        }
        return holder;
       };



    @Test
    public void testStreamMerge() {
        // OK this code is ugly but it works
          Stream<Stream<String>> stream = buildTestStreams().stream();

        Stream<String> objectStream = StreamUtilities.streamsToStream(stream);
        List<String> all = objectStream.collect(Collectors.toList());
        List<String> l;
        for (int i = 0; i < prefixes.length; i++) {
            String prefix = prefixes[i];
            l = buildTestList(prefix);
            Assert.assertTrue(all.containsAll(l));      // do we have this part
            all.removeAll(l);                          // ok drop it

        }

        Assert.assertTrue(all.isEmpty());   // we should have nothing

    }

    @Test
     public void testParallelStreamMerge() {
         // OK this code is ugly but it works
        Stream<Stream<String>> stream = buildTestStreams().stream();


         Stream<String> objectStream = StreamUtilities.streamsToParallelStream(stream);
         List<String> all = objectStream.collect(Collectors.toList());
         List<String> l;
         for (int i = 0; i < prefixes.length; i++) {
             String prefix = prefixes[i];
             l = buildTestList(prefix);
             Assert.assertTrue(all.containsAll(l));      // do we have this part
             all.removeAll(l);                          // ok drop it

         }

         Assert.assertTrue(all.isEmpty());   // we should have nothing

     }



}
