package org.systemsbiology.xtandem.hadoop;

import com.lordjoe.utilities.*;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.input.*;
import org.apache.hadoop.mapreduce.lib.output.*;

import java.io.*;

public class NLine {

    @SuppressWarnings("deprecation")
    public static class Map     extends
            Mapper<LongWritable, Text, Text, LongWritable> {

        public void map(LongWritable key, Text value, Context context
          ) throws IOException, InterruptedException {
   //            output.collect(value, key);
              context.write(  value,key);
        }
    }

    @SuppressWarnings("deprecation")
    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        Job job = new Job(conf, "NLine");
        conf = job.getConfiguration(); // NOTE JOB Copies the configuraton

        conf.set( NLineInputFormat.LINES_PER_MAP,"100");

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        job.setMapperClass(Map.class);
        // conf.setCombinerClass(Reduce.class);
        // conf.setReducerClass(Reduce.class);


        job.setInputFormatClass(NLineInputFormat.class);
        //conf.setInputFormat(TextInputFormat.class);
        job.setOutputFormatClass(TextOutputFormat.class);

        //conf.set(mapred.line.input.format.linespermap(100));

        job.setNumReduceTasks(new Integer(0));

        FileInputFormat.setInputPaths( job, new Path(args[0]));

        // kill existing output directory
        File out = new File(args[1]);
        if (out.exists()) {
                FileUtilities.expungeDirectory(out);
                out.delete();
            }

        FileOutputFormat.setOutputPath(job , new Path(args[1]));

        boolean ans = job.waitForCompletion(true);
    }

}

	
