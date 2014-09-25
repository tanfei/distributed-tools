package org.systemsbiology.xtandem.hadoop;

import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.systemsbiology.hadoop.*;
import org.systemsbiology.xtandem.reporting.*;
import org.systemsbiology.xtandem.scoring.*;

import java.io.*;

/**
 * org.systemsbiology.xtandem.hadoop.BoimlWritingMapper
 * Mapper used in the consolidation task which generates output xml fragments to unburden the reducer
 * User: steven
 * Date: 3/7/11
 */
public class BoimlWritingMapper extends AbstractTandemMapper<Text> {
    public static final BoimlWritingMapper[] EMPTY_ARRAY = {};


    private BiomlReporter m_Reporter;
    private boolean m_UseMultipleOutputFiles;
    private String[] m_OutputFiles;

    public BoimlWritingMapper() {
    }

    @Override
    protected void setup(final Context context) throws IOException, InterruptedException {
        super.setup(context);
        // read configuration lines
        Configuration conf = context.getConfiguration();

        IAnalysisParameters ap = AnalysisParameters.getInstance();
        ap.setJobName(context.getJobName());

        // m_Factory.setValidationStringency(SAMFileReader.ValidationStringency.LENIENT)

        //      getApplication().loadTaxonomy();
        m_Reporter = new BiomlReporter(getApplication(), null);
        String muliple = conf.get(JXTandemLauncher.MULTIPLE_OUTPUT_FILES_PROPERTY);
        m_UseMultipleOutputFiles = "yes".equals(muliple);
        if(m_UseMultipleOutputFiles) {
            String files = conf.get(JXTandemLauncher.INPUT_FILES_PROPERTY);
             if (files != null) {
                 String[] items = files.split(",");
                 m_OutputFiles = items;
              }

        }

    }

    public boolean isUseMultipleOutputFiles() {
        return m_UseMultipleOutputFiles;
    }

    public String[] getOutputFiles() {
        return m_OutputFiles;
    }

    public BiomlReporter getReporter() {
        return m_Reporter;
    }

    /**
     * map each scanned score to the key
     *
     * @param key
     * @param value
     * @param context
     * @throws java.io.IOException
     * @throws InterruptedException
     */
    @Override
    protected void map(final Text key, final Text value, final Context context) throws IOException, InterruptedException {
        String text = value.toString();
        //   text = XTandemHadoopUtilities.cleanXML(text);
        //       ScoredScan scan = XTandemHadoopUtilities.readScoredScan(text, getApplication());
        MultiScorer multiScorer = XTandemHadoopUtilities.readMultiScoredScan(text, getApplication());
        ScoredScan scan = (ScoredScan) multiScorer.getScoredScans()[0];

        String id = scan.getKey(); // key holds charge


        final Text onlyKey = getOnlyKey();
        onlyKey.set(id);

        BiomlReporter reporter = getReporter();
        StringWriter sw = new StringWriter();
        PrintWriter out = new PrintWriter(sw);
        reporter.writeScanScores(scan, out, 1);

        if(isUseMultipleOutputFiles())  {
            String url = scan.getRaw().getUrl();
            if(url == null)
                return;
            String[] outputFiles = getOutputFiles();
            int index = -1;
            for (int i = 0; i < outputFiles.length; i++) {
                String outputFile = outputFiles[i];
                if(outputFile.equals(url)) {
                    index = i;
                    break;
                }
            }
            if(index != -1)  {
                String format = String.format("%04d", index);
                onlyKey.set(format + "|" + id);
            }
        }


        Text onlyValue = getOnlyValue();
        onlyValue.set(sw.toString() + "\f" + text);    /// form feed separated
        context.write(onlyKey, onlyValue);

        context.getCounter("Performance", "TotalScoringScans").increment(1);
        getApplication().clearRetainedData();

    }


    @Override
    protected void cleanup(final Context context) throws IOException, InterruptedException {
        super.cleanup(context);    //To change body of overridden methods use File | Settings | File Templates.
    }
}
