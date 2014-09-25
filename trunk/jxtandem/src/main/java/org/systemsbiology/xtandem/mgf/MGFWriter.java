package org.systemsbiology.xtandem.mgf;

import org.systemsbiology.xtandem.*;
import org.systemsbiology.xtandem.scoring.*;

import java.io.*;

/**
 * org.systemsbiology.xtandem.mgf.MGFHighScoreWriter
 * User: Steve
 * Date: 1/26/12
 */
public class MGFWriter {
    public static final MGFWriter[] EMPTY_ARRAY = {};

    private static int gMatchesToPrint = OriginatingScoredScan.MAX_SERIALIZED_MATCHED;

    public static int getMatchesToPrint() {
        return gMatchesToPrint;
    }

    public static void setMatchesToPrint(final int matchesToPrint) {
        gMatchesToPrint = matchesToPrint;
    }

    private final IMainData m_Application;
    private final double m_MinimumHyperscore;
    private String m_Path = "";

    public MGFWriter(final IMainData pApplication, double minimumHyperscore) {
        m_Application = pApplication;
        m_MinimumHyperscore = minimumHyperscore;
    }

    public IMainData getApplication() {
        return m_Application;
    }

    public double getMinimumHyperscore() {
        return m_MinimumHyperscore;
    }

    public String getPath() {
        return m_Path;
    }

    public void setPath(String pPath) {
        int extIndex = pPath.lastIndexOf(".");
        if (extIndex > 1)
            pPath = pPath.substring(0, extIndex);
        m_Path = pPath;
    }

    public void writeMGF(IScoredScan scan, File out) {
        OutputStream os = null;
        try {
            System.setProperty("line.separator", "\n"); // linux style cr
            os = new FileOutputStream(out);
            writeMGF(scan, out.getAbsolutePath().replace("\\", "/"), os);
        }
        catch (FileNotFoundException e) {
            throw new RuntimeException(e);

        }
    }

    public void writeMGF(IScoredScan scan, String path, OutputStream out) {

        try {
            PrintWriter pw = new PrintWriter(out);
            setPath(path);
            writeMGFElement(scan, pw);
        }
        finally {
            try {
                out.close();
            }
            catch (IOException e) {   // ignore
            }
        }

    }

    public void writeMGFElement(IScoredScan scan, PrintWriter out) {
        ISpectralMatch bestMatch = scan.getBestMatch();
        if(bestMatch == null)
            return;
        double testScore = bestMatch.getHyperScore();
        if(  testScore < getMinimumHyperscore())
            return;
        RawPeptideScan raw = scan.getRaw();
        raw.appendAsMGF(out);

    }

}
