package org.systemsbiology.xtandem.hadoop;

import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.systemsbiology.hadoop.*;
import org.systemsbiology.xtandem.*;
import org.systemsbiology.xtandem.reporting.*;
import org.systemsbiology.xtandem.scoring.*;

import java.io.*;
import java.util.*;

/**
 * org.systemsbiology.xtandem.hadoop.XTandemXMLWritingReducer
 * This is designed to be the only reducer and will write the XML file written by
 * XTanden - the input is scored Scans
 * User: steven
 * Date: 3/7/11
 */
public class XTandemXMLWritingReducer extends AbstractTandemReducer {
    public static final XTandemXMLWritingReducer[] EMPTY_ARRAY = {};



    private PrintWriter m_Writer;
    private PrintWriter m_ScansWriter;
    private BiomlReporter m_Reporter;
    private int m_NumberScoredScans;
    private String m_OutputFile;
    private String[] m_OutputFiles;
    private boolean m_UseMultipleOutputFiles;

    public XTandemXMLWritingReducer() {
    }

    public int getNumberScoredScans() {
        return m_NumberScoredScans;
    }

    public void addNumberScoredScans(final int added) {
        m_NumberScoredScans += added;
    }

    public PrintWriter getWriter() {
        return m_Writer;
    }

    public BiomlReporter getReporter() {
        return m_Reporter;
    }

    @Override
    protected void setup(final Context context) throws IOException, InterruptedException {
        super.setup(context);

        ScoredScan.setReportExpectedValue(true);

        // read configuration lines
        Configuration conf = context.getConfiguration();

        m_NumberScoredScans = 0;

        IAnalysisParameters ap = AnalysisParameters.getInstance();
        ap.setJobName(context.getJobName());


        HadoopTandemMain application = getApplication();
        String muliple = conf.get(JXTandemLauncher.MULTIPLE_OUTPUT_FILES_PROPERTY);
        m_UseMultipleOutputFiles = "yes".equals(muliple);
        if(m_UseMultipleOutputFiles) {
            String files = conf.get(JXTandemLauncher.INPUT_FILES_PROPERTY);
             if (files != null) {
                 String[] items = files.split(",");
                 if(items.length > 1)  {
                     m_OutputFiles = items;
                    m_UseMultipleOutputFiles = items.length > 1;

                 }
                 else {
                     m_OutputFile = items[0];
                 }

              }

        }

        String fileName = conf.get(BiomlReporter.FORCED_OUTPUT_NAME_PARAMETER);
        if (fileName != null) {
            if (fileName.contains(":") || fileName.charAt(0) != '/') {
                String path = conf.get(XTandemHadoopUtilities.PATH_KEY);

                fileName = path + "/" + fileName;
            }
            application.setParameter(BiomlReporter.FORCED_OUTPUT_NAME_PARAMETER, fileName);
            m_OutputFile = fileName;
        }
        else {
            String paramsFile = XTandemHadoopUtilities.buildOutputFileName(context, application);
            System.err.println("Writing output to file " + paramsFile);
            m_OutputFile = paramsFile;
        }

        m_Writer = XTandemHadoopUtilities.buildWriter(context, application);
        m_ScansWriter = XTandemHadoopUtilities.buildWriter(context, application, ".scans");



        m_ScansWriter.println("<scans>");
        m_Reporter = new BiomlReporter(application, null);

        m_Reporter.writeHeader(getWriter(), 0);

    }


    protected void reduceNormal(final Text key, final Iterable<Text> values, final Context context)
            throws IOException, InterruptedException {
        String keyStr = key.toString().trim();
        int fileIndex = -1;
        int index = keyStr.indexOf("|");
        if(index > -1)    {
           fileIndex = Integer.parseInt(keyStr.substring(0,index));
           keyStr = keyStr.substring(index + 1);
        }
        String scanXML = null;
        int id = 0;
        try {
// Debug stuff
            try {
                id = ScoredScan.idFromKey(keyStr); // key includes charge
            }
            catch (NumberFormatException e) {
                return; // todo why would this happen
            }
            if (id == 7858)
                XTandemUtilities.breakHere();

            final HadoopTandemMain app = getApplication();
            //        final Scorer scorer = app.getScoreRunner();
            Iterator<Text> textIterator = values.iterator();
            Text first = textIterator.next();
            scanXML = first.toString();

            // write raw it has more scoring
            if (m_ScansWriter != null)
                m_ScansWriter.println(scanXML);

            //     XTandemUtilities.outputLine(scanXML);
            MultiScorer multi = XTandemUtilities.readMultiScore(scanXML, app);

            IScoredScan start = multi.getScoredScans()[0];
            addNumberScoredScans(start.getNumberScoredPeptides());

            BiomlReporter reporter = getReporter();
            reporter.writeScanScores(start, getWriter(), 1);


        }
        catch (NumberFormatException e) {
            Text onlyKey = getOnlyKey();
            Text onlyValue = getOnlyValue();
            onlyKey.set(keyStr);
            if (scanXML != null) {
                String message = e.getClass().getName() + " " + e.getMessage() + " " + scanXML;
                onlyValue.set(message);
                context.write(onlyKey, onlyValue);
            }
            else {
                String message = e.getClass().getName() + " " + e.getMessage();
                onlyValue.set(message);
                context.write(onlyKey, onlyValue);

            }
            return; // todo why would this happen
        }
        catch (RuntimeException e) {
            Text onlyKey = getOnlyKey();
            Text onlyValue = getOnlyValue();
            if (scanXML != null) {
                String message = e.getClass().getName() + " " + e.getMessage() + " " + scanXML;
                onlyValue.set(message);
                context.write(onlyKey, onlyValue);
                throw e;
            }
            else {
                String message = e.getClass().getName() + " " + e.getMessage();
                onlyValue.set(message);
                context.write(onlyKey, onlyValue);
                throw e;
            }
        }
        catch (Exception e) {
            Text onlyKey = getOnlyKey();
            Text onlyValue = getOnlyValue();
            if (scanXML != null) {
                String message = e.getClass().getName() + " " + e.getMessage() + " " + scanXML;
                onlyValue.set(message);
                context.write(onlyKey, onlyValue);
                throw new RuntimeException(message);
            }
            else {
                String message = e.getClass().getName() + " " + e.getMessage();
                onlyValue.set(message);
                context.write(onlyKey, onlyValue);
                throw new RuntimeException(message);
            }
        }


    }


    @Override
    protected void cleanup(final Context context) throws IOException, InterruptedException {
        String total = "<!-- Total Scored Scans " + getNumberScoredScans() + " -->";
        if (m_Writer != null) {
            m_Reporter.setNumberScoredScans(getNumberScoredScans());
            m_Reporter.writeReportEnd(m_Writer);
            m_Writer.println(total);
            m_Writer.close();
            m_Writer = null;

        }

        if (m_ScansWriter != null) {
            m_ScansWriter.println("</scans>");
            m_ScansWriter.close();
            m_ScansWriter = null;
        }


        System.err.println(total);
        super.cleanup(context);
    }
}
