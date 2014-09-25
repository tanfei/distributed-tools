package org.systemsbiology.xtandem.testing;

import com.lordjoe.utilities.*;
import org.systemsbiology.xtandem.taxonomy.*;

import javax.annotation.*;
import java.io.*;
import java.util.*;

/**
 * org.systemsbiology.xtandem.testing.SelectedProteinFastaBuilder
 * User: steven
 * Date: 4/20/12
 */
public class SelectedProteinFastaBuilder implements IFastaHandler {
    public static final SelectedProteinFastaBuilder[] EMPTY_ARRAY = {};

    private final File m_FastaFileIn;
    private final File m_FastaFileOut;
    private PrintWriter m_Output;
    private final Set<String> m_KeepProteinIds = new HashSet<String>();
    private final Set<String> m_Ids = new HashSet<String>();
    private int m_NumberSaved;


    public SelectedProteinFastaBuilder(String[] lines, File fastaFileIn, File fastaFileOut) {
        m_KeepProteinIds.addAll(Arrays.asList(lines));

        m_FastaFileIn = fastaFileIn;
        m_FastaFileOut = fastaFileOut;

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
        for (String test : m_KeepProteinIds) {
            if (annotation.startsWith(test)) {
                keepSequence = true;
                break;
            }
        }
        if (!keepSequence)
            return;
        m_Output.print(">");
        m_Output.println(annotation);
        appendFastaLines(m_Output, sequence);
        m_NumberSaved++;
    }


    public static final int FASTA_LINE_LENGTH = 60;

    /**
     * @param out where to write
     * @param s   string to write
     */
    public static void appendFastaLines(@Nonnull Appendable out, @Nonnull String s) {
        try {
            if (s.length() == 0)
                return; // I guess
            if (s.length() <= FASTA_LINE_LENGTH) {
                out.append(s);
                out.append("\n");
                return;
            }
            String first = s.substring(0, FASTA_LINE_LENGTH);
            appendFastaLines(out, first);
            String last = s.substring(FASTA_LINE_LENGTH);
            appendFastaLines(out, last);

        }
        catch (IOException e) {
            throw new RuntimeException(e);

        }
    }

    protected void buildSampleFasta() {
        InputStream is;
        FastaParser fp;
        try {
            m_Output = new PrintWriter(new FileWriter(m_FastaFileOut));
            is = new FileInputStream(m_FastaFileIn);
            fp = new FastaParser();
            fp.addHandler(this);
            fp.parseFastaFile(is, "File://" + m_FastaFileIn);
            m_Output.close();
            m_Output = null;
            int numberTested = m_KeepProteinIds.size();
            int diff = m_NumberSaved - numberTested;
            if (diff != 0)
                System.out.println("Number tested " + numberTested + " NumberSaved " + m_NumberSaved);
        }
        catch (IOException e) {
            throw new RuntimeException(e);

        }
    }


    public static File guaranteeExistingFile(String name) {
        File ret = new File(name);
        if (!ret.exists())
            throw new IllegalArgumentException("File " + name + " must exist ");
        return ret;
    }

    private static void usage() {
        System.out.println("usage <proteinis csv> <fastain> <filteredfastaout>");
    }


    /**
     * given a file listing protein ids and a fasta file
     * write a fasta file with the proteins with those ids
     * <p/>
     * Sample command line
     * union_of_RKH_proteins.csv humanpluscrap.fasta filteredhumanpluscrap.fasta
     *
     * @param args
     */
    public static void main(String[] args) {
        if (args.length < 3) {
            usage();
            return;
        }


        String proteinIDCSV = args[0];
        File dataIn = guaranteeExistingFile(proteinIDCSV);
        String[] lines = FileUtilities.readInLines(dataIn);

        String fastaFileName = args[1];
        File fastaFileIn = guaranteeExistingFile(fastaFileName);
        String outputFileName = args[2];
        File fastaFileOut = new File(outputFileName);

        SelectedProteinFastaBuilder sb = new SelectedProteinFastaBuilder(lines, fastaFileIn, fastaFileOut);
        sb.buildSampleFasta();
    }


}
