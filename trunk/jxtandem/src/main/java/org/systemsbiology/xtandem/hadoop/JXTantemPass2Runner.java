package org.systemsbiology.xtandem.hadoop;

import org.apache.hadoop.conf.*;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.input.*;
import org.apache.hadoop.mapreduce.lib.output.*;
import org.apache.hadoop.util.*;
import org.systemsbiology.hadoop.*;

import java.io.*;

/**
 * org.systemsbiology.xtandem.hadoop.JXTantemPass2Runner
 * User: steven
 * Date: 3/7/11
 */
public class JXTantemPass2Runner extends ConfiguredJobRunner implements IJobRunner {
    public static final JXTantemPass2Runner[] EMPTY_ARRAY = {};


    public String[] handleConfiguration(final String pArg, final Configuration pConf) {
        String[] lines;
        String configUrl = pConf.get(HadoopUtilities.CONF_KEY);
        if (configUrl == null)

        {
            System.err.println("Could not find config");
            configUrl = pArg;
        }

        if (configUrl.contains("="))
            configUrl = configUrl.split("=")[1];
        System.err.println("Config file is " + configUrl);
        lines = HadoopUtilities.readConfigFileFS(configUrl);
        pConf.setStrings(HadoopUtilities.CONFIGURATION_KEY, lines);
        return lines;
    }


    public int runJob(Configuration conf, final String[] args) throws Exception {
        try {
            //      conf.set(BamHadoopUtilities.CONF_KEY,"config/MotifLocator.config");
            String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();

            otherArgs = XTandemHadoopUtilities.handleGenericInputs(conf, otherArgs);
            Job job = new Job(conf, "JXTandemPass2");
            setJob(job);
            conf = job.getConfiguration(); // NOTE JOB Copies the configuraton


            // make default settings
            XTandemHadoopUtilities.setDefaultConfigurationArguments(conf);
            // see http://www.mentby.com/jacob-r-rideout/shuffle-in-memory-outofmemoryerror.html
            // conf.setFloat("mapred.job.shuffle.input.buffer.percent", 0.2F);


            long original = 0;

            conf = job.getConfiguration(); // maybe we make a copy
            String childOpts = conf.get("mapred.child.java.opts");
            // use if we use sequence files


            if (JXTandemLauncher.isSequenceFilesUsed()) {
                job.setInputFormatClass(SequenceFileInputFormat.class);
                FileOutputFormat.setCompressOutput(job, true);
                job.setOutputFormatClass(SequenceFileOutputFormat.class);
            }
            else {
                job.setOutputFormatClass(TextOutputFormat.class);
                job.setInputFormatClass(ScoredScanXMLInputFormat.class);
            }

            job.setJarByClass(JXTantemPass2Runner.class);
            job.setMapperClass(ScanScoreMapper.class);
            //    job.setCombinerClass(IntSumReducer.class);
            job.setReducerClass(ScoreCombiningReducer.class);


            job.setMapOutputKeyClass(Text.class);
            job.setMapOutputValueClass(Text.class);

            job.setOutputKeyClass(Text.class);
            job.setOutputValueClass(Text.class);

            // Do not set reduce tasks - ue whatever cores are available
            // this does not work just set a number for now

            XTandemHadoopUtilities.setRecommendedMaxReducers(job);


            XTandemHadoopUtilities.setInputArguments(otherArgs, job);

            String athString = otherArgs[otherArgs.length - 1];
            System.err.println("Pass 2 Output path " + athString);
            if (athString.startsWith("s3n://"))
                athString = athString.substring(athString.lastIndexOf("s3n://"));
            Path outputDir = new Path(athString);
            XTandemHadoopUtilities.setOutputDirecctory(outputDir, job);
            FileSystem fileSystem = outputDir.getFileSystem(conf);


            boolean ans = job.waitForCompletion(true);
            if(ans)
                    XTandemHadoopUtilities.saveCounters(fileSystem,  XTandemHadoopUtilities.buildCounterFileName(this,conf),job);
            else
                  throw new IllegalStateException("Job Failed");

        }
        catch (IOException e) {
            throw new RuntimeException(e);

        }
        catch (IllegalStateException e) {
            throw new RuntimeException(e);

        }
        catch (InterruptedException e) {
            throw new RuntimeException(e);

        }
        catch (ClassNotFoundException e) {
            throw new RuntimeException(e);

        }
        return 0;
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
        int res = ToolRunner.run(new JXTantemPass2Runner(), args);


    }
}
