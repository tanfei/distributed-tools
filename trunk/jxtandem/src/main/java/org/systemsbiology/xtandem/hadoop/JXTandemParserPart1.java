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
import org.systemsbiology.xml.*;
import org.systemsbiology.xtandem.*;

import java.io.*;


/**
 * org.systemsbiology.xtandem.hadoop.JXTandemParserPart1
 * do the first part of JXTandem parser - use when we don't want overload the mapper
 */
public class JXTandemParserPart1 extends ConfiguredJobRunner implements IJobRunner {

    /**
     * do almost nothing
     */
    public static class ProteinIdentityMapper extends AbstractTandemMapper<Writable>  {


        @Override
        public void map(Writable key, Text value, Context context
        ) throws IOException, InterruptedException {

            Text onlyKey = getOnlyKey();
            onlyKey.set(key.toString());
            context.write(onlyKey, value);
        }
    }

    public static final int MAX_TEST_PROTEINS = 0; // 2000;
    public static final int REPORT_INTERVAL_PROTEINS = 10000; // 2000;


    public static class ProteinFragmenterReducer extends AbstractTandemReducer {

        private FastaHadoopLoader m_Loader;
        private int m_Proteins;
        private int m_ProteinsReported;
        private boolean m_GenerateDecoys;
        private boolean m_ShowProteins;

        public FastaHadoopLoader getLoader() {
            return m_Loader;
        }

        public boolean isShowProteins() {
            return m_ShowProteins;
        }

        public void setShowProteins(boolean showProteins) {
            m_ShowProteins = showProteins;
        }

        public boolean isGenerateDecoys() {
            return m_GenerateDecoys;
        }

        public void setGenerateDecoys(boolean generateDecoys) {
            m_GenerateDecoys = generateDecoys;
        }

        public void incrementNumberMappedProteins(Context context) {
            Counter counter = context.getCounter("Parser", "TotalProteins");
            counter.increment(1);
        }

        public static final boolean COUNT_AMINO_ACIDS = true;

        public void incrementNumberAminoAcids(Context context, String sequence) {
            Counter counter = context.getCounter("Parser", "TotalAminoAcids");
            counter.increment(sequence.length());

            if (COUNT_AMINO_ACIDS) {
                int[] aaCount = new int[20];
                for (int i = 0; i < sequence.length(); i++) {
                    FastaAminoAcid aa = null;
                    try {
                        aa = FastaAminoAcid.fromChar(sequence.charAt(i));
                    } catch (BadAminoAcidException e) {
                        continue;
                    }
                    if (aa == null)
                        continue;
                    int index = FastaAminoAcid.asIndex(aa);
                    if (index < 0 || index >= 20)
                        continue;
                    aaCount[index]++;
                }
                for (int i = 0; i < aaCount.length; i++) {
                    int aaCounts = aaCount[i];
                    FastaAminoAcid aa = FastaAminoAcid.fromIndex(i);
                    Counter aacounter = context.getCounter("Parser", "AminoAcid" + aa);
                    aacounter.increment(aaCounts);
                }
            }
        }


        public void incrementNumberDecoysProteins(Context context) {
            Counter counter = context.getCounter("Parser", "TotalDecoyProteins");
            counter.increment(1);
        }

        /**
         * Called once at the beginning of the task.
         */
        @Override
        protected void setup(final Context context) throws IOException, InterruptedException {
            super.setup(context);
            HadoopTandemMain application = getApplication();
            //    application.loadTaxonomy();
            m_Loader = new FastaHadoopLoader(application);

            setGenerateDecoys(application.getBooleanParameter(XTandemUtilities.CREATE_DECOY_PEPTIDES_PROPERTY, Boolean.FALSE));
            long freemem = setMinimalFree();
            Configuration configuration = context.getConfiguration();

            setShowProteins(application.getBooleanParameter("org.systemsbiology.useSingleFastaItemSplit", false));

            XMLUtilities.outputLine("Free Memory " + String.format("%7.2fmb", freemem / 1000000.0) +
                    " minimum " + String.format("%7.2fmb", getMinimumFreeMemory() / 1000000.0));
        }

        @Override
        public void reduceNormal(Text key, Iterable<Text> values,
                                 Context context) throws IOException, InterruptedException {


            String label = key.toString();
            boolean isDecoy = false;
            label = XTandemUtilities.conditionProteinLabel(label);
            for (Text value : values) {
                String sequence = value.toString();

                // to deal with problems we need to show the protein handled
                if (isShowProteins()) {
                    System.err.println("Label:" + label);
                    System.err.println("Sequence:" + sequence);
                }
                // drop terminating *
                if (sequence.endsWith("*"))
                    sequence = sequence.substring(0, sequence.length() - 1);

                // if this returns true than the protein is already a decoy
                String decoyLabel = XTandemHadoopUtilities.asDecoy(label);
                if (decoyLabel != null) {
                    label = decoyLabel;
                    incrementNumberDecoysProteins(context);
                    isDecoy = true;
                }

                FastaHadoopLoader loader = getLoader();

                incrementNumberAminoAcids(context, sequence);
                incrementNumberMappedProteins(context);
                loader.handleProtein(label, sequence, context);

                // make a decoy
                if (isGenerateDecoys() && !isDecoy) {
                    // reverse the sequence
                    String reverseSequence = new StringBuffer(sequence).reverse().toString();
                    incrementNumberAminoAcids(context, reverseSequence);
                    incrementNumberMappedProteins(context);
                    loader.handleProtein("DECOY_" + label, reverseSequence, context);
                }


                if (m_Proteins++ % REPORT_INTERVAL_PROTEINS == 0) {
                    showStatistics();
                }

            }
        }



        private void showStatistics() {
            ElapsedTimer elapsed = getElapsed();
            elapsed.showElapsed("Processed " + (m_Proteins - m_ProteinsReported) + " proteins at " + XTandemUtilities.nowTimeString() +
                    " total " + m_Proteins);
            // how much timeis in my code
            m_ProteinsReported = m_Proteins;

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
            super.cleanup(context);    //To change body of overridden methods use File | Settings | File Templates.
            System.err.println("cleanup up Parser map");
            FastaHadoopLoader loader = getLoader();
            long numberPeptides = loader.getFragmentIndex();
            Counter counter = context.getCounter("Parser", "NumberFragments");
            counter.increment(numberPeptides);
            System.err.println("cleanup up Parser map done");
        }
    }




    /**
     * Execute the command with the given arguments.
     *
     * @param args command specific arguments.
     * @return exit code.
     * @throws Exception
     */
    public int runJob(Configuration conf, final String[] args) {
        if (args.length == 0)
            throw new IllegalStateException("needs a file name");

        try {
            GenericOptionsParser gp = new GenericOptionsParser(conf, args);

            String[] otherArgs = gp.getRemainingArgs();
            // GenericOptionsParser  stops at the first non-define
            otherArgs = XTandemHadoopUtilities.internalProcessArguments(conf, args);
//        if (otherArgs.length != 2) {
//            System.err.println("Usage: wordcount <in> <out>");
//            System.exit(2);
//        }

            Job job = new Job(conf, "Fasta Format Part 1");
            setJob(job);
            conf = job.getConfiguration(); // NOTE JOB Copies the configuraton
            // make default settings
            XTandemHadoopUtilities.setDefaultConfigurationArguments(conf);

            // force the splitter to use more mappers
            XTandemHadoopUtilities.addMoreMappers(conf);

            String params = conf.get(XTandemHadoopUtilities.PARAMS_KEY);
            if (params == null)
                conf.set(XTandemHadoopUtilities.PARAMS_KEY, otherArgs[0]);
            job.setJarByClass(JXTandemParserPart1.class);

            String anotherString = conf.get("org.systemsbiology.useSingleFastaItemSplit", "");
            if ("yes".equalsIgnoreCase(anotherString)) {
                job.setInputFormatClass(SingleFastaInputFormat.class);   // force one split per protein - use for complex files
            } else {
                job.setInputFormatClass(FastaInputFormat.class);

            }
            job.setMapperClass(ProteinIdentityMapper.class);
            job.setReducerClass(ProteinFragmenterReducer.class);
            // try to reduce duplicates in a combiner
            // is the combiner a bad idea???
            // job.setCombinerClass(FragmentCombiner.class);

            job.setMapOutputKeyClass(Text.class);
            job.setMapOutputValueClass(Text.class);
            job.setOutputKeyClass(Text.class);
            job.setOutputValueClass(Text.class);

            if (JXTandemLauncher.isSequenceFilesUsed())
                job.setOutputFormatClass(SequenceFileOutputFormat.class);
            else
                job.setOutputFormatClass(TextOutputFormat.class);


            // Do not set reduce tasks - ue whatever cores are available
            // this does not work just set a number for now
            XTandemHadoopUtilities.setRecommendedMaxReducers(job);

            job.setNumReduceTasks(32);  // should not need more

            if (otherArgs.length > 1) {
                for (int i = 0; i < otherArgs.length - 1; i++) {
                    String inputFile = otherArgs[i];
                    String remoteDirectory = conf.get(XTandemHadoopUtilities.PATH_KEY);
                    if (remoteDirectory != null && !inputFile.startsWith(remoteDirectory))
                        inputFile = remoteDirectory + "/" + inputFile;

                    XTandemHadoopUtilities.setInputPath(job, inputFile);
                }
            }

            // you must pass the output directory as the last argument
            String athString = otherArgs[otherArgs.length - 1];


            if (athString.startsWith("s3n://"))
                athString = athString.substring(athString.lastIndexOf("s3n://"));
            Path outputDir = new Path(athString);
            System.err.println("Output path Parser  " + athString);

            FileSystem fileSystem = outputDir.getFileSystem(conf);
            XTandemHadoopUtilities.expunge(outputDir, fileSystem);    // make sure thia does not exist
            FileOutputFormat.setOutputPath(job, outputDir);


            System.err.println("Waiting for completion  ");

            boolean ans = job.waitForCompletion(true);
            if (ans)
                XTandemHadoopUtilities.saveCounters(fileSystem, XTandemHadoopUtilities.buildCounterFileName(this, conf), job);


            int ret = ans ? 0 : 1;

            //    if (numberMapped != numberReduced)
            //       throw new IllegalStateException("problem"); // ToDo change

            if (!ans)
                throw new IllegalStateException("Job Failed");


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
        //      conf.set(BamHadoopUtilities.CONF_KEY,"config/MotifLocator.config");
        return runJob(conf, args);
    }


    public static void main(String[] args) throws Exception {
        ToolRunner.run(new JXTandemParserPart1(), args);
    }
}