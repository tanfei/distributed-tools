package org.systemsbiology.xtandem.hadoop;

import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.systemsbiology.hadoop.*;
import org.systemsbiology.xtandem.peptide.*;
import org.systemsbiology.xtandem.scoring.*;

import java.io.*;

/**
 * org.systemsbiology.xtandem.hadoop.ScanTagMapper
 * User: steven
 * Date: 3/7/11
 */
public class ScanScoreMapper extends AbstractTandemMapper<Text> {
    public static final ScanScoreMapper[] EMPTY_ARRAY = {};


    public ScanScoreMapper() {
    }

    @Override
    protected void setup(final Context context) throws IOException, InterruptedException {
        super.setup(context);
        // read configuration lines
        Configuration conf = context.getConfiguration();

        IAnalysisParameters ap = AnalysisParameters.getInstance();
        ap.setJobName(context.getJobName());

        // m_Factory.setValidationStringency(SAMFileReader.ValidationStringency.LENIENT)

        HadoopTandemMain application = getApplication();
        application.loadTaxonomy();


        boolean doHardCoded = application.getBooleanParameter(JXTandemLauncher.HARDCODED_MODIFICATIONS_PROPERTY,true);
        PeptideModification.setHardCodeModifications(doHardCoded);

    }

    /**
     * map each scanned score to the key
     *
     * @param key
     * @param value
     * @param context
     * @throws IOException
     * @throws InterruptedException
     */
    @Override
    protected void map(final Text key, final Text value, final Context context) throws IOException, InterruptedException {
        String text = value.toString();
        //   text = XTandemHadoopUtilities.cleanXML(text);
        MultiScorer multiScorer = XTandemHadoopUtilities.readMultiScoredScan(text, getApplication());
        ScoredScan scan = (ScoredScan)multiScorer.getScoredScans()[0];

        String id = scan.getKey(); // key holds charge


        final Text onlyKey = getOnlyKey();
        onlyKey.set( id);

        Text onlyValue = getOnlyValue();
        onlyValue.set(text);
        context.write(onlyKey, onlyValue);
        getApplication().clearRetainedData();

    }


    @Override
    protected void cleanup(final Context context) throws IOException, InterruptedException {
        super.cleanup(context);    //To change body of overridden methods use File | Settings | File Templates.
    }
}
