/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.spark.examples;

import com.lordjoe.distributed.*;
import com.lordjoe.distributed.util.*;
import com.lordjoe.distributed.wordcount.*;
import org.apache.spark.*;
import org.apache.spark.api.java.*;
import org.apache.spark.api.java.function.*;
import scala.*;

import java.util.*;
import java.util.regex.*;

public final class JavaLargeWordCount {
    private static final Pattern SPACE = Pattern.compile(" ");

    public static void main(String[] args) throws Exception {

        if (args.length < 1) {
            System.err.println("Usage: JavaWordCount <file>");
            return;
        }

        SparkConf sparkConf = new SparkConf().setAppName("JavaWordCount");
        SparkUtilities.guaranteeSparkMaster(sparkConf);
         //  sparkConf.setExecutorEnv("spark.executor.extraClassPath","/SparkExamples/target/classes");
        // String[] jars = { "/SparkExamples/target/word-count-examples_2.10-1.0.0.jar" };
        //  sparkConf.setJars(jars);

        JavaSparkContext ctx = new JavaSparkContext(sparkConf);
        JavaRDD<String> lines = ctx.textFile(args[0], 1);

        // use my function not theirs
        JavaRDD<String> words = lines.flatMap(new WordsMapFunction());

//            new FlatMapFunction<String, String>() {
//      @Override
//      public Iterable<String> call(String s) {
//         String[] split = SPACE.split(s);
//         for (int i = 0; i < split.length; i++) {
//             String trim = split[i].trim();
//             split[i] = trim.toUpperCase();
//         }
//         return Arrays.asList(split);
//     }    });

        JavaPairRDD<String, Integer> ones = words.mapToPair(new PairFunction<String, String, Integer>() {
            @Override
            public Tuple2<String, Integer> call(String s) {
                return new Tuple2<String, Integer>(s, 1);
            }
        });


//        JavaPairRDD<String, Integer> counts = ones.reduceByKey(new Function2<Integer, Integer, Integer>() {
//            @Override
//            public Integer call(Integer i1, Integer i2) {
//                return i1 + i2;
//            }
//        });
//
//        JavaPairRDD<String, Integer> sorted = counts.sortByKey();

        ones = ones.partitionBy(new PartitionByStart());
        JavaPairRDD<String, Integer> sorted = ones.sortByKey();
        JavaRDD<WordNumber> answer = sorted.mapPartitions(new WordCountFlatMapFinction());

        List<WordNumber> objects = answer.toArray();
        for (WordNumber o : objects) {
            System.out.println(o);
        }
    }

    public static class PartitionByStart extends Partitioner {
        @Override public int numPartitions() {
            return 26;
        }

        @Override public int getPartition(final Object key) {
            String s = (String)key;
            if(s.length() == 0)
                throw new IllegalStateException("problem"); // ToDo change
            int ret = s.charAt(0) - 'A';
            ret = Math.min(25,ret) ;
            ret = Math.max(0,ret);
            return 25 - ret;
        }
    }
}

