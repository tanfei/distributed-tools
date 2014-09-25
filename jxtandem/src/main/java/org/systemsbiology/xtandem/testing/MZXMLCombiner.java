package org.systemsbiology.xtandem.testing;

import org.systemsbiology.xml.*;
import org.systemsbiology.xtandem.*;

import java.io.*;

/**
 * org.systemsbiology.xtandem.testing.MZXMLCombiner
 * User: steven
 * Date: 10/25/11
 */
public class MZXMLCombiner {
    public static final MZXMLCombiner[] EMPTY_ARRAY = {};

    private static int gMaxScans;
    private static int gTotalScans;

    public static int getMaxScans() {
        return gMaxScans;
    }

    public static void setMaxScans(final int pMaxScans) {
        gMaxScans = pMaxScans;
    }

    private static void appendFile(final PrintWriter out, final File pFile, final boolean isFirst, final boolean isLast) throws IOException {
        String name = pFile.getAbsolutePath();
        XMLUtilities.outputLine(name);
        InputStream is = XTandemUtilities.getDescribedStream(name);
        LineNumberReader rdr = new LineNumberReader(new InputStreamReader(is));
        String line = rdr.readLine();
        int scanLevel = 0;
        while (line != null) {
            if (line.contains("<scan")) {
                scanLevel++;
                break;
            }
            if (isFirst)
                out.println(line);
            line = rdr.readLine();
        }
        do {
            out.println(line);

            line = rdr.readLine();
            if (line.contains("<scan")) {
                gTotalScans++;
                if (isMaxScansReached()) {
                    while (scanLevel-- > 0)
                        out.println("</scan>");    // close nested scan tags
                    break;
                }
                scanLevel++;
            }
            if (line.contains("</scan")) {
                scanLevel--;
            }
        }
        while (!line.contains("</msRun>"));


        if (isLast) {
            out.println("</msRun>");
            out.println("</mzXML>");
        }
    }

    private static boolean isMaxScansReached() {
        return getMaxScans() > 0 && gTotalScans > getMaxScans();
    }


    private static void saveCombinedScans(final String fileName, File[] files) throws IOException {
        PrintWriter outw = new PrintWriter(new FileWriter(fileName));
        try {
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                appendFile(outw, file, i == 0, i == files.length - 1);
                if (isMaxScansReached())
                    break;
            }
            if (isMaxScansReached()) {
                outw.println("</msRun>");
                outw.println("</mzXML>");
            }
            XMLUtilities.outputLine("total scans " + gTotalScans);
        }
        catch (IOException e) {
            throw new RuntimeException(e);

        }
        finally {
            if (outw != null)
                outw.close();
        }
    }


    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        File indir = new File(args[1]);
        File[] files = indir.listFiles();
        String baseFile = args[0];
        saveCombinedScans(baseFile, files);

        int totalScans = gTotalScans;
        for (int maxScans = 1000; maxScans < totalScans; maxScans *= 4) {
            gTotalScans = 0;
            setMaxScans(maxScans);
            String fileName = baseFile.replace(".mzxml", Integer.toString(maxScans) + ".mzxml");
            saveCombinedScans(fileName, files);
            XMLUtilities.outputLine(fileName);
        }

    }

}
