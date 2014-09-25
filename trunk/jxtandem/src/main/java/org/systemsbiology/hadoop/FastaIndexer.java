package org.systemsbiology.hadoop;


import org.apache.hadoop.conf.*;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.io.*;
 import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.*;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.*;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;
import org.apache.hadoop.util.*;

import java.io.*;
import java.util.*;

//import org.systemsbiology.remotecontrol.*;

/**
 * org.systemsbiology.hadoop.FastaIndexer
 */
public class FastaIndexer extends ConfiguredJobRunner implements IJobRunner {


    public static class ProteinIndexMapper
            extends Mapper<Text, Text, Text, Text>
    {

        private Text word = new Text();

        @Override
        public void map(Text key, Text value, Context context) throws IOException, InterruptedException {
            String ketStr = key.toString();
            String valStr = value.toString();
            context.write(key,   value);
          }
    }


    public static class ProteinMapperReducer
            extends Reducer<Text, Text, Text, Text> {
        private Text result = new Text();

        @Override
        public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            List<String> holder = new ArrayList<String>();

            String keyStr = key.toString();
            for (Text val : values) {
                holder.add(val.toString());
            }
            String[] ret = new String[holder.size()];
            holder.toArray(ret);
            if (ret.length != 1)
                throw new IllegalStateException("Protein id is not unique " + keyStr);
            result.set(ret[0]);
            context.write(key,   result);

        }
    }


    /**
     * kill a directory and all contents
     *
     * @param src
     * @param fs
     * @return
     */
    public static boolean expunge(Path src, FileSystem fs) {


        try {
            if (!fs.exists(src))
                return true;
            // break these out
            if (fs.getFileStatus(src).isDir()) {
                boolean doneOK = fs.delete(src, true);
                doneOK = !fs.exists(src);
                return doneOK;
            }
            if (fs.isFile(src)) {
                boolean doneOK = fs.delete(src, false);
                return doneOK;
            }
            throw new IllegalStateException("should be file of directory if it exists");
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


    public int runJob(Configuration conf, String[] args) throws Exception {
        String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
//        if (otherArgs.length != 2) {
//            System.err.println("Usage: wordcount <in> <out>");
//            System.exit(2);
//        }
        Job job = new Job(conf, "Index Proteins");
        conf = job.getConfiguration(); // NOTE JOB Copies the configuraton
        job.setJarByClass(FastaIndexer.class);
        job.setInputFormatClass(FastaInputFormat.class);
        job.setMapperClass(ProteinIndexMapper.class);
        job.setReducerClass(ProteinMapperReducer.class);

        job.setOutputFormatClass(MapFileOutputFormat.class);

        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(Text.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);


        // added Slewis
        job.setNumReduceTasks(HadoopUtilities.DEFAULT_REDUCE_TASKS);
        //    job.setPartitionerClass(MyPartitioner.class);

        String input = otherArgs[0];
              FileInputFormat.addInputPath(job, new Path(input));

        // you must pass the output directory as the last argument
        String athString = input.toLowerCase().replace(".fasta",".map"); //[otherArgs.length - 1];
        Path outputDir = new Path(athString);

        FileSystem fileSystem = outputDir.getFileSystem(conf);
        expunge(outputDir, fileSystem);    // make sure thia does not exist
        FileOutputFormat.setOutputPath(job, outputDir);


        boolean ans = job.waitForCompletion(true);
        int ret = ans ? 0 : 1;
        return ret;
    }


    /**
     * Execute the command with the given arguments.
     *
     * @param args command specific arguments.
     * @return exit code.
     * @throws Exception
     */
    @Override
    public int run(final String[] args) throws Exception {
        Configuration conf = getConf();
         if(conf == null)
             conf = HDFSAccessor.getSharedConfiguration();
         return runJob(conf, args);
    }


    public static void main(String[] args) throws Exception {
        ToolRunner.run(new FastaIndexer(), args);
    }
}