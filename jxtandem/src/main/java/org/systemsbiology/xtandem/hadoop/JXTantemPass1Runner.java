package org.systemsbiology.xtandem.hadoop;

import com.lordjoe.utilities.*;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.output.*;
import org.apache.hadoop.util.*;
import org.systemsbiology.hadoop.*;

import java.io.*;
import java.util.*;

/**
 * org.systemsbiology.xtandem.hadoop.JXTantemPass1Runner
 * User: steven
 * Date: 3/7/11
 */
public class JXTantemPass1Runner extends ConfiguredJobRunner implements IJobRunner {
    public static final JXTantemPass1Runner[] EMPTY_ARRAY = {};


    /**
     * Force loading of needed classes to make
     */
    public static final Class[] NEEDED =
            {
                    //                  org.apache.commons.logging.LogFactory.class,
                    org.apache.commons.cli.ParseException.class
            };


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


    public int runJob(Configuration pConf, final String[] args) throws Exception {
        try {
            for (int i = 0; i < args.length; i++) {
                String arg = args[i];
                System.err.println(arg);
            }
            String[] otherArgs = new GenericOptionsParser(pConf, args).getRemainingArgs();

            // GenericOptionsParser stops after the first non-argument
            otherArgs = XTandemHadoopUtilities.handleGenericInputs(pConf, otherArgs);
            Job job = new Job(pConf, "JXTandemPass1");
            setJob(job);
              long original = 0;

            pConf = job.getConfiguration(); // NOTE JOB Copies the configuraton

            String childOpts = pConf.get("mapred.child.java.opts");

            // see http://www.mentby.com/jacob-r-rideout/shuffle-in-memory-outofmemoryerror.html
            // pConf.setFloat("mapred.job.shuffle.input.buffer.percent", 0.2F);

            // make default settings
            XTandemHadoopUtilities.setDefaultConfigurationArguments(  pConf);
            childOpts = pConf.get("mapred.child.java.opts");
            // force the splitter to use more mappers
            XTandemHadoopUtilities.addMoreMappers(pConf);
            childOpts = childOpts = pConf.get("mapred.child.java.opts");
            XTandemHadoopUtilities.setInputArguments(otherArgs, job);

            String inputFile = otherArgs[0];
            boolean inputFormatSet = false;
            int beginIndex = inputFile.lastIndexOf(".");
            // this is a single file
            if (beginIndex > -1) {
                String extension = inputFile.substring(beginIndex).toLowerCase();
                if (".gz".equalsIgnoreCase(extension)) {
                    String lcfile = inputFile.toLowerCase();
                    if (lcfile.endsWith(".mzml.gz")) {
                        job.setInputFormatClass(MzMLInputFormat.class);
                    }
                    else if (lcfile.endsWith(".mzxml.gz")) {
                        job.setInputFormatClass(MzXMLInputFormat.class);
                    }
                    else if (lcfile.endsWith(".mgf.gz")) {
                        job.setInputFormatClass(MGFInputFormat.class);
                    }
                }
                else {
                    if (".mzml".equalsIgnoreCase(extension)) {
                        job.setInputFormatClass(MzMLInputFormat.class);
                    }
                    else if (".mgf".equalsIgnoreCase(extension)) {
                        job.setInputFormatClass(MGFInputFormat.class);
                    }
                    else {
                        job.setInputFormatClass(MzXMLInputFormat.class);
                    }
                }
                inputFormatSet = true;
            }
            else // this is a directory
            {
                FileSystem fs1 = FileSystem.get(pConf);
                HDFSAccessor fs = new HDFSAccessor(fs1);
                String[] ls = fs.ls(inputFile);
                for (int i = 0; i < ls.length; i++) {
                    String fileName = ls[i].toLowerCase();
                    if (fileName.endsWith(".mzml")) {
                        job.setInputFormatClass(MzMLInputFormat.class);
                        inputFormatSet = true;
                        break;
                    }
                    if (fileName.endsWith(".mzml.gz")) {
                        job.setInputFormatClass(MzMLInputFormat.class);
                        inputFormatSet = true;
                        break;
                    }
                    if (fileName.endsWith(".mzxml")) {
                        job.setInputFormatClass(MzXMLInputFormat.class);
                        inputFormatSet = true;
                        break;
                    }
                    if (fileName.endsWith(".mzxml.gz")) {
                        job.setInputFormatClass(MzXMLInputFormat.class);
                        inputFormatSet = true;
                        break;
                    }
                    if (fileName.endsWith(".mgf")) {
                        job.setInputFormatClass(MGFInputFormat.class);
                        inputFormatSet = true;
                        break;
                    }
                    if (fileName.endsWith(".mgf.gz")) {
                        job.setInputFormatClass(MGFInputFormat.class);
                        inputFormatSet = true;
                        break;
                    }
                }
            }
            if (!inputFormatSet)
                throw new IllegalStateException("No mzxml or mzml or mgf files to read");

            if (JXTandemLauncher.isSequenceFilesUsed()) {
                FileOutputFormat.setCompressOutput(job, true);
                job.setOutputFormatClass(SequenceFileOutputFormat.class);
            }
            else {
                job.setOutputFormatClass(TextOutputFormat.class);

            }
            //   job.setPartitionerClass(TranchPartitioner.class);

            job.setJarByClass(JXTantemPass1Runner.class);
            job.setMapperClass(ScanTagMapper.class);
            //    job.setCombinerClass(IntSumReducer.class);
            job.setReducerClass(ScoringReducer.class);


            job.setMapOutputKeyClass(Text.class);
            job.setMapOutputValueClass(Text.class);

            job.setOutputKeyClass(Text.class);
            job.setOutputValueClass(Text.class);

            // Do not set reduce tasks - ue whatever cores are available
            // this does not work just set a number for now
            XTandemHadoopUtilities.setRecommendedMaxReducers(job);

            //  http://www.mentby.com/ted-yu-2/shuffle-in-memory-outofmemoryerror.html
            // this causes out of memory errors
            //   pConf.set("mapred.job.shuffle.input.buffer.percent","0.20");
            //   pConf.set("mapred.job.reduce.input.buffer.percent","0.30");


            String athString = otherArgs[otherArgs.length - 1];
            System.err.println("Pass1 Output path " + athString);

            if(athString.startsWith("s3n://"))
                athString = athString.substring(athString.lastIndexOf("s3n://"));
             Path outputDir = new Path(athString);
            XTandemHadoopUtilities.setOutputDirecctory(outputDir, job);
            FileSystem fileSystem = outputDir.getFileSystem(pConf);


            boolean ans = false;
            try {
                ans = job.waitForCompletion(true);
            }
            catch (ClassNotFoundException e) {
                 Throwable trr = e;
                while(trr.getCause() != null && trr.getCause() != trr)
                    trr = trr.getCause();
                trr.printStackTrace();
                throw e;
            }
            if(ans)
                   XTandemHadoopUtilities.saveCounters(fileSystem,  XTandemHadoopUtilities.buildCounterFileName(this, pConf),job);
             else
                throw new IllegalStateException("Job Failed");
            int ret = ans ? 0 : 1;
              return ret;
        }
        catch (IOException e) {
            ExceptionUtilities.printCausalStacks(e);
            throw new RuntimeException(e);

        }
        catch (IllegalStateException e) {
            ExceptionUtilities.printCausalStacks(e);
            throw new RuntimeException(e);

        }
        catch (InterruptedException e) {
            ExceptionUtilities.printCausalStacks(e);
            throw new RuntimeException(e);

        }
        catch (ClassNotFoundException e) {
            ExceptionUtilities.printCausalStacks(e);
            throw new RuntimeException(e);

        }
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
        int res = ToolRunner.run(new JXTantemPass1Runner(), args);
    }
}
