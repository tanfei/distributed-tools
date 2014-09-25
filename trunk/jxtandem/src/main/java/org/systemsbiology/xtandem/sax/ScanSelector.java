package org.systemsbiology.xtandem.sax;

import org.systemsbiology.xml.*;

import java.io.*;

/**
 * org.systemsbiology.xtandem.sax.ScanSelector
 *
 * @author Steve Lewis
 * @date Feb 1, 2011
 */
public class ScanSelector
{
    public static ScanSelector[] EMPTY_ARRAY = {};
    public static Class THIS_CLASS = ScanSelector.class;

    private final File m_InputFile;
    private final File m_OutputFile;
    private final int m_MaxScans;

    public ScanSelector(File pInputFile, File pOutputFile, int pMaxScans)
    {
        m_InputFile = pInputFile;
        m_OutputFile = pOutputFile;
        m_MaxScans = pMaxScans;
    }

    public File getInputFile()
    {
        return m_InputFile;
    }

    public File getOutputFile()
    {
        return m_OutputFile;
    }

    public int getMaxScans()
    {
        return m_MaxScans;
    }

    public int getScanCount()
    {
        TagCounter handler = new TagCounter();
        handler.parseDocument(getInputFile());
        return handler.getTagCount("scan");
    }

    public void selectScans()
    {
        int existingScans = getScanCount();
        if (existingScans <= getMaxScans()) {
            XMLUtilities.copyFileLines(getInputFile(), getOutputFile());
            return;
        }
        // take the middle scans
        int skippedScans = (existingScans - getMaxScans()) / 2;
        copyScanLines(skippedScans);


    }

    protected void copyScanLines(int skippedScans)
    {
        LineNumberReader reader = null;
        PrintWriter outWriter = null;
        try {
            reader = new LineNumberReader(
                    new InputStreamReader(new FileInputStream(getInputFile())));
            outWriter = new PrintWriter(new FileWriter(getOutputFile()));
            copyDesignatedScans(reader, outWriter, skippedScans);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        finally {
            try {
                if (reader != null)
                    reader.close();
                if (outWriter != null)
                    outWriter.close();
            }
            catch (IOException e) {
            }
        }

    }

    private void copyDesignatedScans(LineNumberReader pReader, PrintWriter pOutWriter,
                                     int skippedScans)
            throws IOException
    {
        boolean inScan = false;
        boolean skipScan = false;
        int writtenScans = 0;
        String line = pReader.readLine();
        while (line != null) {
            if (line.contains("<index") || line.contains("</index>") || line.contains("<offset")) {
                line = pReader.readLine();
                continue;
            }
            if (line.contains("<scan")) {
                skipScan = ((skippedScans-- > 0) || (writtenScans++ > getMaxScans()));
                if (skipScan) {
                    line = pReader.readLine();
                    continue;
                }
                inScan = true;
            }
            if (line.contains("</msRun>")) {
                skipScan = false;
            }
            if (skipScan) {
                line = pReader.readLine();
                continue;
            }
            pOutWriter.println(line);
            if (line.contains("</scan>")) {
                skipScan = ((skippedScans  > 0) || (writtenScans  > getMaxScans()));
            }


            line = pReader.readLine();
        }
    }

    public int computeSkippedScans(int totalScans)
    {
        return Math.max(0, (totalScans - getMaxScans()) / 2);
    }


    public static final int NUMBER_KEPT_SCANS = 10;

    public static void main(String[] args)
    {
        final File inp = new File(args[0]);
        final File out = new File(args[1]);
        ScanSelector ss = new ScanSelector(inp, out, NUMBER_KEPT_SCANS);
        TagCounter handler = new TagCounter();
        handler.parseDocument(inp);
        final int count = handler.getTagCount("scan");
        int skippedScans = ss.computeSkippedScans(count);
        ss.copyScanLines(skippedScans);

    }
}
