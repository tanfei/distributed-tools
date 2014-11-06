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

    public static final int SPARK_CONFIG_INDEX = 0;
    public static final int INPUT_FILE_INDEX = 1;

    /**
     * spark-submit --class  org.apache.spark.examples.JavaLargeWordCount SparkJar.jar   SparkCluster.properties  war_and_peace.txt
    *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {

        if (args.length < INPUT_FILE_INDEX + 1) {
            System.err.println("Usage: SparkProperties JavaWordCount <file>");
            return;
        }

         SparkUtilities.readSparkProperties(args[SPARK_CONFIG_INDEX]);
        SparkUtilities.setAppName("JavaWordCount");

        JavaSparkContext ctx = SparkUtilities.getCurrentContext();

        String inputPath = SparkUtilities.buildPath(args[INPUT_FILE_INDEX] );
        JavaRDD<String> lines = ctx.textFile(inputPath, 1);

        // use my function not theirs
        JavaRDD<String> words = lines.flatMap(new WordsMapFunction());


        JavaPairRDD<String, Integer> ones = words.mapToPair(new PairFunction<String, String, Integer>() {
            @Override
            public Tuple2<String, Integer> call(String s) {
                return new Tuple2<String, Integer>(s, 1);
            }
        });



        ones = ones.partitionBy(new PartitionByStart());
        JavaPairRDD<String, Integer> sorted = ones.sortByKey();
        JavaRDD<WordNumber> answer = sorted.mapPartitions(new WordCountFlatMapFunction());

        List<WordNumber> objects = answer.toArray();
        for (WordNumber o : objects) {
            System.out.println(o);
        }
    }

}

