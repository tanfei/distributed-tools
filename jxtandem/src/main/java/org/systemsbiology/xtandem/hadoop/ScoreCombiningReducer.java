package org.systemsbiology.xtandem.hadoop;

import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.systemsbiology.sax.*;
import org.systemsbiology.xml.*;
import org.systemsbiology.xtandem.*;
import org.systemsbiology.xtandem.peptide.*;
import org.systemsbiology.xtandem.sax.*;
import org.systemsbiology.xtandem.scoring.*;

import java.io.*;
import java.util.*;

/**
 * org.systemsbiology.xtandem.hadoop.ScoreCombiningReducer
 * in this reducer the key is the scan id ( as text) and the values are xml fragments
 * corresponding to scoring
 * output is an XML fragment for addition to the ultimate xml file
 * User: steven
 * Date: 3/7/11
 */
public class ScoreCombiningReducer extends AbstractTandemReducer {
    public static final ScoreCombiningReducer[] EMPTY_ARRAY = {};

    private int m_TotalScoredScans;

    public ScoreCombiningReducer() {
    }

    public int getTotalScoredScans() {
        return m_TotalScoredScans;
    }

    public void addTotalScoredScans(final int added) {
        m_TotalScoredScans += added;
    }


    

    @Override
    protected void setup(final Context context) throws IOException, InterruptedException {
        super.setup(context);
        // read configuration lines
        Configuration conf = context.getConfiguration();

        HadoopTandemMain application = getApplication();

        boolean doHardCoded = application.getBooleanParameter(JXTandemLauncher.HARDCODED_MODIFICATIONS_PROPERTY,true);
        PeptideModification.setHardCodeModifications(doHardCoded);


        int NMatches = application.getIntParameter(JXTandemLauncher.NUMBER_REMEMBERED_MATCHES,XTandemHadoopUtilities.DEFAULT_CARRIED_MATCHES);
        XTandemHadoopUtilities.setNumberCarriedMatches(NMatches);

    }
    
    private boolean gPrintThisLine = false;
    /**
     * key is the scan id - values are all scores
     *
     * @param key
     * @param values
     * @param context
     * @throws IOException
     * @throws InterruptedException
     */

    protected void reduceNormal(final Text key, final Iterable<Text> values, final Context context)
            throws IOException, InterruptedException {
        String keyStr = key.toString().trim();
        String id = "";

        try {
            // key ads charge to treat different charge states differently
            id =  keyStr;
        }
        catch (NumberFormatException e) {
            return; // todo fix

        }


        final HadoopTandemMain app = getApplication();
        final Scorer scorer = app.getScoreRunner();
        Iterator<Text> textIterator = values.iterator();
        if(!textIterator.hasNext())
            return; // I don't think this can happen
        
        Text first = textIterator.next();
        String scanXML = first.toString();

        if (gPrintThisLine)
            XMLUtilities.outputLine(scanXML);
        //   scanXML = XTandemHadoopUtilities.cleanXML(scanXML); // we find bad characters i.e 0
        MultiScorer start = XTandemUtilities.readMultiScore(scanXML, app);

        boolean isScanAdded = false;
        /**
         * add each scan to the first producing a global scan
         */
        while (textIterator.hasNext()) {
            Text text = textIterator.next();
            String textStr = text.toString();
            //       textStr = XTandemHadoopUtilities.cleanXML(textStr); // we find bad characters i.e 0
            if (gPrintThisLine)   {
                XMLUtilities.outputLine(textStr);
              }

            MultiScorer next = XTandemUtilities.readMultiScore(textStr, app);

            start.addTo(next);
            isScanAdded = true;
        }

        Text onlyKey = getOnlyKey();
        Text onlyValue = getOnlyValue();
         // if there is only one score pass that on
        if (!isScanAdded) {
            onlyKey.set(keyStr);
            onlyValue.set(scanXML);
            context.write(onlyKey, onlyValue);
            return;
        }
        else {
            StringBuilder sb = new StringBuilder();
            IXMLAppender appender = new XMLAppender(sb);

    //        double expectedValue = start.getExpectedValue();
            start.serializeAsString(appender);
            scanXML = sb.toString();

            if (gPrintThisLine)    {
                XMLUtilities.outputLine(scanXML);
                gPrintThisLine = false;
              }

            onlyKey.set(keyStr);
            onlyValue.set(scanXML);
            context.write(onlyKey, onlyValue);
        }
        getApplication().clearRetainedData();

    }


    @Override
    protected void cleanup(final Context context) throws IOException, InterruptedException {
        super.cleanup(context);
    }
}
