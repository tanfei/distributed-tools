package org.systemsbiology.xtandem.hadoop;

import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.systemsbiology.hadoop.*;
import org.systemsbiology.xtandem.*;
import org.systemsbiology.xtandem.mgf.*;
import org.systemsbiology.xtandem.peptide.*;
import org.systemsbiology.xtandem.pepxml.*;
import org.systemsbiology.xtandem.reporting.*;
import org.systemsbiology.xtandem.scoring.*;

import java.io.*;
import java.util.*;

/**
 * org.systemsbiology.xtandem.hadoop.XTandemXMLWritingReducer
 * This is designed to be the only reducer and will write the XML file written by
 * XTanden - the input is scored Scans will read text from iput and append headers
 * User: steven
 * Date: 3/7/11
 */
public class XTandemConcatenatingWritingReducer extends AbstractTandemReducer {
    public static final XTandemConcatenatingWritingReducer[] EMPTY_ARRAY = {};

    private boolean m_WriteScans;
    private boolean m_WritePepXML;
    private boolean m_WriteHighScoringMGF;
    private double m_MinimumScoreForMGFWrite;  // ToDo should this be on a per algorithm basis
    private double m_MinimumExpectedValueMGFWrite;  // ToDo should this be on a per algorithm basis

    private PrintWriter m_Writer;
    private PrintWriter m_ScansWriter;
    private BiomlReporter m_Reporter;
    private PepXMLWriter[] m_pepXMLWriter;
     private MGFWriter[] m_MFGWriters; //
    private PrintWriter[] m_PepXmlsOutWriter;
    private PrintWriter[] m_MGFOutWriter;
    private String m_OutputFile;
    private String[] m_OutputFiles;
    private boolean m_UseMultipleOutputFiles;


    public XTandemConcatenatingWritingReducer() {
    }


    public PrintWriter getWriter() {
        return m_Writer;
    }

    public BiomlReporter getReporter() {
        return m_Reporter;
    }

    public boolean isWritePepXML() {
        return m_WritePepXML;
    }

    public void setWritePepXML(final boolean pWritePepXML) {
        m_WritePepXML = pWritePepXML;
    }

    public boolean isWriteHighScoringMGF() {
        return m_WriteHighScoringMGF;
    }

    public void setWriteHighScoringMGF(boolean writeHighScoringMGF) {
        m_WriteHighScoringMGF = writeHighScoringMGF;
    }

    public PepXMLWriter getPepXMLWriter(int index) {
        return m_pepXMLWriter[index];
    }

    public MGFWriter getMFGWriter(int index) {
        return m_MFGWriters[index];
    }

    public PrintWriter getPepXmlsOutWriter(int index) {
        return m_PepXmlsOutWriter[index];
    }

    public PrintWriter getMgfOutWriter(int index) {
        return m_MGFOutWriter[index];
    }

    public double getMinimumExpectedValueMGFWrite() {
        return m_MinimumExpectedValueMGFWrite;
    }

    public void setMinimumExpectedValueMGFWrite(double minimumExpectedValueMGFWrite) {
        m_MinimumExpectedValueMGFWrite = minimumExpectedValueMGFWrite;
    }

    public double getMinimumScoreForMGFWrite() {
        return m_MinimumScoreForMGFWrite;
    }

    public void setMinimumScoreForMGFWrite(double minimumScoreForMGFWrite) {
        m_MinimumScoreForMGFWrite = minimumScoreForMGFWrite;
    }

    public PrintWriter getScansWriter() {
        return m_ScansWriter;
    }

    public void setScansWriter(final PrintWriter pScansWriter) {
        m_ScansWriter = pScansWriter;
    }

    public boolean isWriteScans() {
        return m_WriteScans;
    }

    public void setWriteScans(final boolean pWriteScans) {
        m_WriteScans = pWriteScans;
    }

    public boolean isUseMultipleOutputFiles() {
        return m_UseMultipleOutputFiles;
    }

    @Override
    protected void setup(final Context context) throws IOException, InterruptedException {
        super.setup(context);

        getElapsed().reset(); // start the clock
        ScoredScan.setReportExpectedValue(true);

        // read configuration lines
        Configuration conf = context.getConfiguration();

        HadoopTandemMain application = getApplication();



        IAnalysisParameters ap = AnalysisParameters.getInstance();
        ap.setJobName(context.getJobName());


          if ( application.getBooleanParameter(JXTandemLauncher.TURN_ON_SCAN_OUTPUT_PROPERTY,false))
            setWriteScans(true);


        boolean doHardCoded = application.getBooleanParameter(JXTandemLauncher.HARDCODED_MODIFICATIONS_PROPERTY,true);
        PeptideModification.setHardCodeModifications(doHardCoded);


        int NMatches = application.getIntParameter(JXTandemLauncher.NUMBER_REMEMBERED_MATCHES,XTandemHadoopUtilities.DEFAULT_CARRIED_MATCHES);
        XTandemHadoopUtilities.setNumberCarriedMatches(NMatches);




        String muliple = conf.get(JXTandemLauncher.MULTIPLE_OUTPUT_FILES_PROPERTY);
        m_UseMultipleOutputFiles = "yes".equals(muliple);
        if (m_UseMultipleOutputFiles) {
            String files = conf.get(JXTandemLauncher.INPUT_FILES_PROPERTY);
            if (files != null) {
                System.err.println("Input files " + files);
                String[] items = files.split(",");
                m_OutputFiles = items;
                if (items.length == 1) {
                    m_OutputFiles = null;
                    application.setParameter(BiomlReporter.FORCED_OUTPUT_NAME_PARAMETER, items[0]);
                    m_UseMultipleOutputFiles = false;
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
        }
        else {
            String paramsFile = XTandemHadoopUtilities.buildOutputFileName(context, application);
            System.err.println("Writing output to file " + paramsFile);
        }

        System.err.println("Setup Consolidator");
        if (!isUseMultipleOutputFiles())
            setWriters(context, application, fileName);

    }


    protected void setWriters(Context context, HadoopTandemMain application, String inputFileNamex) {

        System.err.println("Setting up writers");
        String s = inputFileNamex;
        if (isUseMultipleOutputFiles()) {
            s = XTandemHadoopUtilities.dropExtension(inputFileNamex);
            if (s.equals(m_OutputFile))
                return;
            m_OutputFile = s;
            System.err.println("Setting writer to " + m_OutputFile);
            cleanupWriters();
            m_Writer = XTandemHadoopUtilities.buildPrintWriter(context, s, ".hydra");

            if (isWriteScans()) {
                m_ScansWriter = XTandemHadoopUtilities.buildPrintWriter(context, s, ".scans");
                m_ScansWriter.println("<scans>");
            }
        }
        else {
            m_Writer = XTandemHadoopUtilities.buildPrintWriter(context, application);
            if (isWriteScans()) {
                m_ScansWriter = XTandemHadoopUtilities.buildPrintWriter(context, application, ".scans");
                m_ScansWriter.println("<scans>");
            }
        }
        m_Reporter = new BiomlReporter(application, null);

        m_Reporter.writeHeader(getWriter(), 0);

        if ( application.getBooleanParameter(XTandemUtilities.WRITING_PEPXML_PROPERTY,false)) {
            setWritePepXML(true);
            ITandemScoringAlgorithm[] algorithms = application.getAlgorithms();
            m_pepXMLWriter = new PepXMLWriter[algorithms.length];
            m_PepXmlsOutWriter = new PrintWriter[algorithms.length];
            for (int i = 0; i < algorithms.length; i++) {
                ITandemScoringAlgorithm algorithm = algorithms[i];
                String algo = algorithm.getName();
                String extension = ".pep.xml";
                if (!"KScore".equals(algo))
                    extension = "." + algo + extension;
                m_PepXmlsOutWriter[i] = XTandemHadoopUtilities.buildPrintWriter(context, s, extension);
                PepXMLWriter px = new PepXMLWriter(application);

                px.setPath(s);
                m_pepXMLWriter[i] = px;

                String spectrumPath = application.getParameter("spectrum, path");
                getPepXMLWriter(i).writePepXMLHeader(spectrumPath, algo, getPepXmlsOutWriter(i));

            }

        }

        double limit = application.getDoubleParameter(XTandemUtilities.WRITING_MGF_PROPERTY, 0);
        double limit_ExpectedValue = application.getDoubleParameter(XTandemUtilities.WRITING_MGF_PROPERTY_2,0);
        if (limit > 0 || limit_ExpectedValue > 0) {
            setWriteHighScoringMGF(true);
            setMinimumScoreForMGFWrite(limit);
            setMinimumExpectedValueMGFWrite(limit_ExpectedValue);
            ITandemScoringAlgorithm[] algorithms = application.getAlgorithms();
            m_MFGWriters = new MGFWriter[algorithms.length];
            m_MGFOutWriter = new PrintWriter[algorithms.length];
            for (int i = 0; i < algorithms.length; i++) {
                ITandemScoringAlgorithm algorithm = algorithms[i];
                String algo = algorithm.getName();
                String extension = ".mgf";
                if (!"KScore".equals(algo))
                    extension = "." + algo + extension;
                m_MGFOutWriter[i] = XTandemHadoopUtilities.buildPrintWriter(context, s, extension);
                MGFWriter px;

                if (limit_ExpectedValue > 0)

                    px = new MGFExpectedWriter(application, limit_ExpectedValue);

                else

                    px = new MGFWriter(application, limit);

                px.setPath(s);
                m_MFGWriters[i] = px;


                String spectrumPath = application.getParameter("spectrum, path");

            }

        }

    }


    protected void reduceNormal(final Text key, final Iterable<Text> values, final Context context)
            throws IOException, InterruptedException {
        String keyStr = key.toString().trim();
        String scanXML = null;
        String id = "";
        int fileIndex = -1;
        final HadoopTandemMain app = getApplication();
        try {
// Debug stuff
            id = keyStr; // key includes charge
            String usedFileName;
            int index = keyStr.indexOf("|");
            if (index > -1) {
                //               System.err.println("key " + keyStr + " index " + index + " m_OutputFiles.length " + m_OutputFiles.length);
                fileIndex = Integer.parseInt(keyStr.substring(0, index));
                System.err.println("fileIndex " + fileIndex);
                if (isUseMultipleOutputFiles() && fileIndex >= 0 && fileIndex < m_OutputFiles.length) {
                    usedFileName = m_OutputFiles[fileIndex];
                    System.err.println("usedFileName " + usedFileName);
                    setWriters(context, app, usedFileName);
                }
                keyStr = keyStr.substring(index + 1);
            }

            //        final Scorer scorer = app.getScoreRunner();
            Iterator<Text> textIterator = values.iterator();
            Text first = textIterator.next();
            String rawText = first.toString();

            String[] split = rawText.split("\f");
            String biomlText = split[0];
            scanXML = split[1];
            // write raw it has more scoring
            if (m_ScansWriter != null)
                m_ScansWriter.println(scanXML);


            if (getWriter() != null)
                getWriter().println(biomlText);

            if (isWritePepXML()) {
                MultiScorer multiScorer = XTandemHadoopUtilities.readMultiScoredScan(scanXML, getApplication());
                IScoredScan[] scoredScans = multiScorer.getScoredScans();
                // todo support multiple
                for (int i = 0; i < scoredScans.length; i++) {
                    IScoredScan scan = scoredScans[i];
                    getPepXMLWriter(i).writePepXML(scan, getPepXmlsOutWriter(i));

                }
                  ScoredScan scan = (ScoredScan) scoredScans[0];
            }

            if (isWriteHighScoringMGF()) {
                MultiScorer multiScorer = XTandemHadoopUtilities.readMultiScoredScan(scanXML, getApplication());
                IScoredScan[] scoredScans = multiScorer.getScoredScans();
                // todo support multiple
                for (int i = 0; i < scoredScans.length; i++) {
                    IScoredScan scan = scoredScans[i];
                    MGFWriter mfgWriter = getMFGWriter(i);
                    PrintWriter mgfOutWriter = getMgfOutWriter(i);
                    mfgWriter.writeMGFElement(scan, mgfOutWriter);

                }
                ScoredScan scan = (ScoredScan) scoredScans[0];
            }
            app.clearRetainedData();

        }
        // Look more closely - why are you doing this - what else is possible
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
        cleanupWriters();

        System.err.println(getElapsed().formatElapsed("Finished with Consolidation"));

        super.cleanup(context);
    }

    protected void cleanupWriters() {
        if (m_Writer != null) {
            m_Reporter.writeReportEnd(m_Writer);
            m_Writer.close();
            m_Writer = null;

        }

        if (m_ScansWriter != null) {
            m_ScansWriter.println("</scans>");
            m_ScansWriter.close();
            m_ScansWriter = null;
        }

        if (m_PepXmlsOutWriter != null) {
            for (int i = 0; i < m_PepXmlsOutWriter.length; i++) {
                PrintWriter pepXmlsOutWriter = getPepXmlsOutWriter(i);
                getPepXMLWriter(i).writePepXMLFooter(pepXmlsOutWriter);
                pepXmlsOutWriter.close();

            }
            m_PepXmlsOutWriter = null;
        }

        if (m_MFGWriters != null) {
            for (int i = 0; i < m_MFGWriters.length; i++) {
                PrintWriter mgfsOutWriter = getMgfOutWriter(i);
                mgfsOutWriter.close();

            }
            m_MFGWriters = null;
        }

    }
}
