package org.systemsbiology.xtandem.testing;

import com.lordjoe.utilities.*;
import org.systemsbiology.xtandem.taxonomy.*;

import java.io.*;
import java.util.*;

/**
 * org.systemsbiology.xtandem.testing.SmallSampleBuilder
 * User: steven
 * Date: 4/20/12
 */
public class SmallSampleBuilder implements IFastaHandler {
    public static final SmallSampleBuilder[] EMPTY_ARRAY = {};

    private final File m_FastaFileIn;
    private final File m_FastaFileOut;
    private final File m_MzXMLFileIn;
    private final File m_MzXMLFileOut;
    private PrintWriter m_Output;
    private final Set<String> m_Peptides = new HashSet<String>();
    private final Set<String> m_UsedPeptides = new HashSet<String>();
    private final Set<String> m_Ids = new HashSet<String>();

    public static File guaranteeExistingFile(String name) {
        File ret = new File(name);
        if (!ret.exists())
            throw new IllegalArgumentException("File " + name + " must exist ");
        return ret;
    }

    private static void usage() {
    }

    public SmallSampleBuilder(String[] lines, File fastaFileIn, File fastaFileOut, File mzXMLFileIn, File mzXMLFileOut) {
        m_FastaFileIn = fastaFileIn;
        m_FastaFileOut = fastaFileOut;
        m_MzXMLFileIn = mzXMLFileIn;
        m_MzXMLFileOut = mzXMLFileOut;
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            handleLine(line);
        }
    }

    /**
     * do whatever you what with the protein
     *
     * @param annotation !null annotation - should be unique
     * @param sequence   !null sequence
     */
    @Override
    public void handleProtein(String annotation, String sequence) {
        boolean keepSequence = false;
        for (String test : m_Peptides) {
            if (sequence.contains(test)) {
                keepSequence = true;
                m_UsedPeptides.add(test);
                break;
            }
        }
        if (!keepSequence)
            return;
        m_Output.print(">");
        m_Output.println(annotation);
        m_Output.println(sequence);
    }

    protected void buildSampleFasta() {
        InputStream is = null;
        FastaParser fp = null;
        try {
            m_Output = new PrintWriter(new FileWriter(m_FastaFileOut));
            is = new FileInputStream(m_FastaFileIn);
            fp = new FastaParser();
            fp.addHandler(this);
            fp.parseFastaFile(is, "File://" + m_FastaFileIn);
            m_Output.close();
            m_Output = null;
            Set<String> unused = new HashSet<String>(m_Peptides);
            unused.removeAll(unused);
            unused = null;
        }
        catch (IOException e) {
            throw new RuntimeException(e);

        }
    }

    public void buildOutputs() {
        buildSampleFasta();
        buildSampleMzXML();
    }

    protected void buildSampleMzXML() {
        Reader is = null;
        FastaParser fp = null;
        try {
            m_Output = new PrintWriter(new FileWriter(m_MzXMLFileOut));
            is = new FileReader(m_MzXMLFileIn);
            BufferedReader rdr = new BufferedReader(is);
            String line = copyHeader(rdr);
            line = copyProperScans(rdr, line);
            writeFooter();
            m_Output.close();
            m_Output = null;
        }
        catch (IOException e) {
            throw new RuntimeException(e);

        }
    }

    protected String copyHeader(BufferedReader rdr) {
        try {
            String line = rdr.readLine();
            while (!line.contains("<scan ")) {
                m_Output.println(line);
                line = rdr.readLine();
            }
            return line;
        }
        catch (IOException e) {
            throw new RuntimeException(e);

        }

    }

    protected void writeFooter() {
        m_Output.println("  </msRun>");
        m_Output.println("</mzXML>");
    }

    protected void skipToScanLine(BufferedReader rdr) {
        try {
            String line = rdr.readLine();
            while (!line.contains("</scan>")) {
                line = rdr.readLine();
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);

        }

    }

    protected void copyScan(BufferedReader rdr, String line) {
        try {
            while (!line.contains("</scan>")) {
                m_Output.println(line);
                line = rdr.readLine();
            }
            m_Output.println(line);
        }
        catch (IOException e) {
            throw new RuntimeException(e);

        }

    }


    protected String copyProperScans(BufferedReader rdr, String line) {
        try {
            while (line.contains("<scan ") ) {
                if (isInterestingScan(line)) {
                    copyScan(rdr, line);
                }
                else {
                    skipToScanLine(rdr);
                }
                line = rdr.readLine();
                if(line.contains("</scan>"))
                    line = rdr.readLine();

            }
            return line;
        }
        catch (IOException e) {
            throw new RuntimeException(e);

        }


    }

    private boolean isInterestingScan(String line) {
        line = line.trim();
        line = line.replace("<scan num=\"", "");
        line = line.replace("\"", "");
        if(m_Ids.contains(line))
            return true;
        else
            return false;
    }

    protected void handleLine(String line) {
        String[] items = line.trim().split(",");
        m_Ids.add(items[0]);
        m_Peptides.add(items[1]);
        m_Peptides.add(items[3]);

    }

    public static void main(String[] args) {
        if (args.length < 5) {
            usage();
            return;
        }
        File dataIn = guaranteeExistingFile(args[0]);
        File fastaFileIn = guaranteeExistingFile(args[1]);
        File fastaFileOut = new File(args[2]);
        File mzXMLFileIn = guaranteeExistingFile(args[3]);
        File mzXMLFileOut = new File(args[4]);
        String[] lines = FileUtilities.readInLines(dataIn);
        SmallSampleBuilder sb = new SmallSampleBuilder(lines, fastaFileIn, fastaFileOut, mzXMLFileIn, mzXMLFileOut);
        sb.buildOutputs();
    }


}
