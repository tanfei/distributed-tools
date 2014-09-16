package com.lordjoe.distributed.wordcount;

import com.lordjoe.distributed.*;
import com.lordjoe.distributed.chapter_and_verse.*;
import net.ricecode.similarity.*;
import org.apache.spark.*;
import org.apache.spark.api.java.*;
import org.apache.spark.api.java.function.*;
import scala.*;

import java.util.*;

/**
 * com.lordjoe.distributed.wordcount.ChapterAndVerse
 * User: Steve
 * Date: 9/12/2014
 */
public  class ChapterAndVerse {

    /**
     * make a string of the letters sorted alphabetically
     * @param pLine
     * @return
     */
    public static String buildSortedLatters(final String pLine) {
        StringBuilder sb = new StringBuilder();
        Character[] letters = new Character[pLine.length()];
        for (int i = 0; i < pLine.length(); i++) {
            Character letter = pLine.charAt(i);

        }
        Arrays.sort(letters);
        for (int i = 0; i < letters.length; i++) {
            Character letter = letters[i];
            sb.append(letter);
        }
        return sb.toString();
      }

    private final SimilarityStrategy similarity = new LevenshteinDistanceStrategy();

    public   JavaRDD<LineAndLocationMatch>  findBestMatchesLikeHadoop(JavaRDD<LineAndLocation> inputs) {

        // So this is what the mapper does - make key value pairs
        JavaPairRDD<ChapterKeyClass, LineAndLocation > mappedKeys = inputs.mapToPair(new PairFunction<LineAndLocation, ChapterKeyClass, LineAndLocation>() {

                   @Override public Tuple2<ChapterKeyClass, LineAndLocation > call(final LineAndLocation  v) throws Exception {
                       return new Tuple2(new ChapterKeyClass(v.chapter,v.lineNumber),v);
                   }
               });

        // Partition by chapters ?? is this right??
        mappedKeys = mappedKeys.partitionBy(new Partitioner() {
            @Override public int numPartitions() {
                return 20;
            }

            @Override public int getPartition(final Object key) {
                return ((ChapterKeyClass)key).lineNumber % numPartitions();
            }
        });


        // Now I get very fuzzy - I for every partition I want sort on line number
     //   JavaPairRDD<KeyClass , LineAndLocation > sortedKeys = ??? WHAT HAPPENS HERE

        // Now I need to to a reduce operation What I want is
    //    JavaRDD<LineAndLocationMatch> bestMatches = sortedKeys.<SOME FUNCTION>();

        throw new UnsupportedOperationException("Fix This"); // ToDo
        // return bestMatches;
    }


    public void reduceFunction(ChapterKeyClass key,Iterator<LineAndLocation> values)  {
       }

    public static void main(String[] args) {
        if (args.length < 1) {
          System.err.println("Usage: ChapterAndVerse <file>");
          return;
        }

        SparkConf sparkConf = new SparkConf().setAppName("JavaWordCount");
         sparkConf.set("spark.mesos.coarse","true");

         sparkConf.setMaster("local");
      //  sparkConf.setExecutorEnv("spark.executor.extraClassPath","/SparkExamples/target/classes");
       // String[] jars = { "/SparkExamples/target/word-count-examples_2.10-1.0.0.jar" };
      //  sparkConf.setJars(jars);

        JavaSparkContext ctx = new JavaSparkContext(sparkConf);
        JavaPairRDD<String,String> lines = ctx.wholeTextFiles(args[0]);

        SparkUtilities.showPairRDD(lines);

    }
}
