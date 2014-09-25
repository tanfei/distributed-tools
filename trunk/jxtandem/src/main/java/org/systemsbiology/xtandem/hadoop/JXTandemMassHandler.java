package org.systemsbiology.xtandem.hadoop;


import com.lordjoe.utilities.*;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.input.*;
import org.apache.hadoop.mapreduce.lib.output.*;
import org.apache.hadoop.util.*;
import org.systemsbiology.hadoop.*;
import org.systemsbiology.xml.*;
import org.systemsbiology.xtandem.*;
import org.systemsbiology.xtandem.peptide.*;

import java.io.*;
import java.util.*;


/**
 * org.systemsbiology.xtandem.hadoop.JXTandemMassHandler
 */
public class JXTandemMassHandler extends ConfiguredJobRunner implements IJobRunner {

    @SuppressWarnings("UnusedDeclaration")
    public static final int MAX_TEST_PROTEINS = 2000;
    public static final int REPORT_INTERVAL_PROTEINS = 10000;


    public static class MassMapper extends AbstractTandemMapper<Writable> {
        private int m_Proteins;

        /**
         * Called once at the beginning of the task.
         */
        @Override
        protected void setup(final Context context) throws IOException, InterruptedException {
            super.setup(context);
        }

        @Override
        public void map(Writable key, Text value, Context context
        ) throws IOException, InterruptedException {

            String label = key.toString();
            String text = value.toString();
            // use text only for debugging when we need to see intermediates
//            if (!JXTandemLauncher.isSequenceFilesUsed()) {
//                String[] split = text.split("\t");
//                label = split[0];
//                text = split[1];
//            }
//            else {
//                context.write((Text) key, value);
//
//            }
            if (label == null || text == null)
                return;
            if (label.length() == 0 || text.length() == 0)
                return;
//
//            String[] items = text.split("\t" );
            Text onlyKey = getOnlyKey();
            Text onlyValue = getOnlyValue();
            onlyKey.set(label);
            onlyValue.set(text);
            context.write(onlyKey, onlyValue);
            // count what was done
            if (m_Proteins++ % REPORT_INTERVAL_PROTEINS == 0) {
                showStatistics();
            }

        }

        private void showStatistics() {
            ElapsedTimer elapsed = getElapsed();
            elapsed.showElapsed("Processed " + REPORT_INTERVAL_PROTEINS + " proteins at " + XTandemUtilities.nowTimeString());
            // how much timeis in my code

            long freemem = setMinimalFree();
            XMLUtilities.outputLine("Free Memory " + String.format("%7.2fmb", freemem / 1000000.0) +
                    " minimum " + String.format("%7.2fmb", getMinimumFreeMemory() / 1000000.0));
            elapsed.reset();
        }

    }

    // purely for debuggomg to walk through interesting sequences
    @SuppressWarnings("UnusedDeclaration")
    public static final String[] INTERESTING_SEQUENCES = {
            "DLKFPLPHR",
            "TAEARAK",
            "FGLSGIR",
            "IGGPINIALAYEAATAGK",

    };
    public static final Set<String> INTERESTING_SEQUENCE_SET = new HashSet<String>(); //Arrays.asList(INTERESTING_SEQUENCES));

    /**
     * test for a sequence I care about
     *
     * @param sequence
     * @return
     */
    @SuppressWarnings("UnusedDeclaration")
    public static boolean isInterestingSequence(String sequence) {
        // debugging code  to see why some sequewnces are not scored
        if (!INTERESTING_SEQUENCE_SET.isEmpty()) {
            if (INTERESTING_SEQUENCE_SET.contains(sequence)) {
                return true;
            }
        }
        return false;
    }


    /**
     * write all sequences at a given mass into a fill with the mass name
     */
    public static class MassReducer extends AbstractTandemReducer {

        private long m_NumberUniquePeptides;
        @SuppressWarnings("UnusedDeclaration")
        private long m_NumberModifiedPeptides;

        @SuppressWarnings("UnusedDeclaration")
        private long m_ParsingTime;
        private int m_Proteins;

        @Override
        protected void setup(final Context context) throws IOException, InterruptedException {
            super.setup(context);
        }

        @Override
        public void reduceNormal(Text key, Iterable<Text> values,
                                 Context context) throws IOException, InterruptedException {

            String keyStr = key.toString();
            int mass = Integer.parseInt(keyStr);
            int numberPeptidesThisMass = 0;
            PrintWriter out = null;
            String fileName = XTandemHadoopUtilities.buildFileNameFromMass(mass);

            try {
                   for (Text val : values) {
                    String valStr = val.toString();
                    if (valStr == null || valStr.length() == 0)
                        continue;
                    String[] items = valStr.split(",");
                    String sequence = items[0];

                    // this is a debugging step
                    //     if(isInterestingSequence(   sequence))
                    //          XTandemUtilities.breakHere();

                    //noinspection UnusedDeclaration
                    IPolypeptide pp = Polypeptide.fromString(sequence);
                       //noinspection UnusedDeclaration
                       double pep_mass = Double.parseDouble(items[1]);
                    // be lazy
                    if (out == null)
                        out = getOutputWriter(context, mass);
                    m_NumberUniquePeptides++;
                    numberPeptidesThisMass++;
                    out.println(valStr);
                }
                // count what was done
                Counter counter = context.getCounter(XTandemHadoopUtilities.DATABASE_COUNTER_GROUP_NAME, fileName);
                counter.increment(numberPeptidesThisMass);

                if (m_Proteins++ % REPORT_INTERVAL_PROTEINS == 0) {
                    showStatistics();
                }
                if (out != null)
                    out.close();


            } catch (IOException e) {
                throw new RuntimeException(e);

            } finally {
                if (out != null)
                    try {
                        out.close();
                    } catch (Exception e) {
                        // forgive
                    }

            }


        }


        private void showStatistics() {
            ElapsedTimer elapsed = getElapsed();
            elapsed.showElapsed("Processed " + REPORT_INTERVAL_PROTEINS + "  proteins at " + XTandemUtilities.nowTimeString());
            // how much timeis in my code

            long freemem = setMinimalFree();
            XMLUtilities.outputLine("Free Memory " + String.format("%7.2fmb", freemem / 1000000.0) +
                    " minimum " + String.format("%7.2fmb", getMinimumFreeMemory() / 1000000.0));
            elapsed.reset();
        }


        /**
         * Called once at the end of the task.
         */
        @Override
        protected void cleanup(final Context context) throws IOException, InterruptedException {
            Counter counter = context.getCounter(XTandemHadoopUtilities.PARSER_COUNTER_GROUP_NAME, "NumberUniqueFragments");
            counter.increment(m_NumberUniquePeptides);
            System.err.println("Handled " + m_NumberUniquePeptides + "Unique peptides");

            writeParseParameters(context);
            super.cleanup(context);

        }

        /**
         * remember we built the database
         *
         * @param context
         * @throws IOException
         */
        protected void writeParseParameters(final Context context) throws IOException {
            Configuration cfg = context.getConfiguration();
            HadoopTandemMain application = getApplication();
            TaskAttemptID tid = context.getTaskAttemptID();
            //noinspection UnusedDeclaration
            String taskStr = tid.getTaskID().toString();
            String paramsFile = application.getDatabaseName() +  ".params";
            Path dd = XTandemHadoopUtilities.getRelativePath(paramsFile);

            FileSystem fs = FileSystem.get(cfg);

            if (!fs.exists(dd)) {
                try {
                    FastaHadoopLoader ldr = new FastaHadoopLoader(application);
                    String x = ldr.asXMLString();
                    FSDataOutputStream fsout = fs.create(dd);
                    PrintWriter out = new PrintWriter(fsout);
                    out.println(x);
                    out.close();
                } catch (IOException e) {
                    try {
                        fs.delete(dd,false);
                    } catch (IOException e1) {
                        throw new RuntimeException(e1);
                    }
                    throw new RuntimeException(e);
                }

            }

        }

        protected PrintWriter getOutputWriter(final Context context, final int pMass) throws IOException {
            Configuration cfg = context.getConfiguration();
            Path outPath = XTandemHadoopUtilities.buildPathFromMass(pMass, getApplication());
            FileSystem fs = FileSystem.get(cfg);
            FSDataOutputStream fsout = fs.create(outPath);
            return new PrintWriter(fsout);
        }
    }

    public int runJob(Configuration conf, final String[] args) throws Exception {
        try {
            if (args.length == 0)
                throw new IllegalStateException("needs a file name");
            String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();

            // GenericOptionsParser stops after the first non-argument
            otherArgs = XTandemHadoopUtilities.handleGenericInputs(conf, otherArgs);


//        if (otherArgs.length != 2) {
//            System.err.println("Usage: wordcount <in> <out>");
//            System.exit(2);
//        }
            Job job = new Job(conf, "Mass Handler");
            setJob(job);

            conf = job.getConfiguration(); // NOTE JOB Copies the configuraton

            // make default settings
            XTandemHadoopUtilities.setDefaultConfigurationArguments(conf);

            // sincs reducers are writing to hdfs we do NOT want speculative execution
            conf.set("mapred.reduce.tasks.speculative.execution", "false");


            String params = conf.get(XTandemHadoopUtilities.PARAMS_KEY);
            if (params == null)
                conf.set(XTandemHadoopUtilities.PARAMS_KEY, otherArgs[0]);
            job.setJarByClass(JXTandemMassHandler.class);

            if (JXTandemLauncher.isSequenceFilesUsed()) {
                job.setInputFormatClass(SequenceFileInputFormat.class);
            } else {
                job.setInputFormatClass(TextInputFormat.class);
            }

            job.setOutputFormatClass(TextOutputFormat.class);
            job.setMapperClass(MassMapper.class);
            job.setReducerClass(MassReducer.class);

            job.setMapOutputKeyClass(Text.class);
            job.setMapOutputValueClass(Text.class);
            job.setOutputKeyClass(Text.class);
            job.setOutputValueClass(Text.class);


            // Do not set reduce tasks - ue whatever cores are available
            // this does not work just set a number for now
            XTandemHadoopUtilities.setRecommendedMaxReducers(job);


            if (otherArgs.length > 1) {
                String otherArg = otherArgs[0];
                XTandemHadoopUtilities.setInputPath(job, otherArg);
                System.err.println("Input path mass finder " + otherArg);
            }

            // you must pass the output directory as the last argument
            String athString = otherArgs[otherArgs.length - 1];
 //           File out = new File(athString);
//        if (out.exists()) {
//            FileUtilities.expungeDirectory(out);
//            out.delete();
//        }

            if (athString.startsWith("s3n://"))
                athString = athString.substring(athString.lastIndexOf("s3n://"));
            Path outputDir = new Path(athString);

            FileSystem fileSystem = outputDir.getFileSystem(conf);
            XTandemHadoopUtilities.expunge(outputDir, fileSystem);    // make sure thia does not exist
            FileOutputFormat.setOutputPath(job, outputDir);
            System.err.println("Output path mass finder " + outputDir);


            boolean ans = job.waitForCompletion(true);
            int ret = ans ? 0 : 1;
            if (ans)
                XTandemHadoopUtilities.saveCounters(fileSystem, XTandemHadoopUtilities.buildCounterFileName(this, conf), job);
            else
                throw new IllegalStateException("Job Failed");


            //    if (numberMapped != numberReduced)
            //       throw new IllegalStateException("problem"); // ToDo change

            return ret;
        } catch (IOException e) {
            throw new RuntimeException(e);

        } catch (InterruptedException e) {
            throw new RuntimeException(e);

        } catch (ClassNotFoundException e) {
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
        if (conf == null)
            conf = HDFSAccessor.getSharedConfiguration();
        return runJob(conf, args);
    }

    public static void main(String[] args) throws Exception {
        ToolRunner.run(new JXTandemMassHandler(), args);
    }
}