package com.lordjoe.distributed;

import com.lordjoe.distributed.chapter_and_verse.*;

import java.io.*;
import java.util.*;

/**
 * com.lordjoe.distributed.Java7WordCount
 * User: Steve
 * Date: 8/25/2014
 */
public class StreamingChapterAndVerse {


    public static final String readFile(File f) {
        try {
            StringBuilder sb = new StringBuilder();
            BufferedReader rdr = new BufferedReader(new FileReader(f));
            String line = rdr.readLine();
            while (line != null) {
                sb.append(line + "\n");
                line = rdr.readLine();
            }
            return sb.toString();
        }
        catch (IOException e) {
            throw new RuntimeException(e);

        }
    }

    public static void showMostSimilarLines(final Iterable<KeyValueObject<ChapterKeyClass, LineAndLocationMatch>> pList) {
        for (KeyValueObject<ChapterKeyClass, LineAndLocationMatch> ky : pList) {
            LineAndLocationMatch value = ky.value;
            if (value.similarity < 0.5)
                continue;
            if (value.similarity == 1)
                   continue;
              if (value.thisLine.line.length() < 20)
                continue;
            if (value.bestFit.line.length() < 20)
                continue;

            System.out.println(value.thisLine.line);
            System.out.println(value.bestFit.line);
            System.out.println(value.thisLine.chapter + ":" + value.thisLine.lineNumber);
            System.out.println(value.bestFit.chapter + ":" + value.bestFit.lineNumber);
            System.out.println(value.similarity);
            System.out.println();
        }
    }
    /**
     * sample - run with data as user.dir and books as the argument
     * will read all books and report most similar lines
     * @param args
     */
    public static void main(String[] args) {


        JavaMapReduce handler = new JavaMapReduce(new ChapterLinesMapper(), new LineSimilarityReducer() );
        if (args.length < 1) {
            System.err.println("Usage: ChapterAndVerse <file>");
            return;
        }

        File dir = new File(args[0]);
        File[] files = dir.listFiles();
        List<KeyValueObject<String, String>> holder = new ArrayList<KeyValueObject<String, String>>();
        for (int i = 0; i < files.length; i++) {
            String fileText = readFile(files[i]);
            holder.add(new KeyValueObject<String, String>(files[i].getName(), fileText));
        }

        handler.mapReduceSource(holder);

        Iterable<KeyValueObject<ChapterKeyClass, LineAndLocationMatch>> list = handler.collect();
        showMostSimilarLines(list);


    }


}
