package org.systemsbiology.xtandem;

import org.junit.*;
import org.systemsbiology.xtandem.sax.*;

import java.io.*;
import java.util.*;

/**
 * org.systemsbiology.xtandem.NotesReaderTest
 *
 * @author Steve Lewis
 * @date Jan 4, 2011
 */
public class NotesReaderTest
{
    public static NotesReaderTest[] EMPTY_ARRAY = {};
    public static Class THIS_CLASS = NotesReaderTest.class;

    public static final String INPUT_NOTES = "input.xml";
    public static final String DEFAULT_NOTES = "default_input.xml";

    public static final Map<String, String> INPUT_MAP = new HashMap<String, String>();
    public static final Map<String, String> DEFAULT_MAP = new HashMap<String, String>();

    static {
        INPUT_MAP.put("list path, default parameters", "res://default_input.xml");
        INPUT_MAP.put("list path, taxonomy information", "res://taxonomy4.xml");
        INPUT_MAP.put("protein, taxon", "yeast");
        INPUT_MAP.put("spectrum, path", "res://test_spectra.mgf");
        INPUT_MAP.put("output, path", "output.xml");
        INPUT_MAP.put("output, results", "valid");

        DEFAULT_MAP.put("list path, default parameters", "default_input.xml");
        DEFAULT_MAP.put("list path, taxonomy information", "taxonomy.xml");
        DEFAULT_MAP.put("spectrum, fragment monoisotopic mass error", "0.4");
        DEFAULT_MAP.put("spectrum, parent monoisotopic mass error plus", "4.0");
        DEFAULT_MAP.put("spectrum, parent monoisotopic mass error minus", "2.0");
        DEFAULT_MAP.put("spectrum, parent monoisotopic mass isotope error", "yes");
        DEFAULT_MAP.put("spectrum, fragment monoisotopic mass error units", "Daltons");
        DEFAULT_MAP.put("spectrum, parent monoisotopic mass error units", "Daltons");  // was ppm
        DEFAULT_MAP.put("spectrum, fragment mass type", "monoisotopic");
        DEFAULT_MAP.put("spectrum, dynamic range", "100.0");
        DEFAULT_MAP.put("spectrum, total peaks", "50");
        DEFAULT_MAP.put("spectrum, maximum parent charge", "4");
        DEFAULT_MAP.put("spectrum, use noise suppression", "yes");
        DEFAULT_MAP.put("spectrum, minimum parent m+h", "500.0");
        DEFAULT_MAP.put("spectrum, minimum fragment mz", "150.0");
        DEFAULT_MAP.put("spectrum, minimum peaks", "15");
        DEFAULT_MAP.put("spectrum, threads", "1");
        DEFAULT_MAP.put("spectrum, sequence batch size", "1000");
        DEFAULT_MAP.put("residue, modification mass", "57.022@C");
        DEFAULT_MAP.put("residue, potential modification mass", "");
        DEFAULT_MAP.put("residue, potential modification motif", "");
        DEFAULT_MAP.put("protein, taxon", "other mammals");
        DEFAULT_MAP.put("protein, cleavage site", "[RK]|{P}");
        DEFAULT_MAP.put("protein, modified residue mass file", "");
        DEFAULT_MAP.put("protein, cleavage C-terminal mass change", "+17.002735");
        DEFAULT_MAP.put("protein, cleavage N-terminal mass change", "+1.007825");
        DEFAULT_MAP.put("protein, N-terminal residue modification mass", "0.0");
        DEFAULT_MAP.put("protein, C-terminal residue modification mass", "0.0");
        DEFAULT_MAP.put("protein, homolog management", "no");
        DEFAULT_MAP.put("refine", "yes");
        DEFAULT_MAP.put("refine, modification mass", "");
        DEFAULT_MAP.put("refine, sequence path", "");
        DEFAULT_MAP.put("refine, tic percent", "20");
        DEFAULT_MAP.put("refine, spectrum synthesis", "yes");
        DEFAULT_MAP.put("refine, maximum valid expectation value", "0.1");
        DEFAULT_MAP.put("refine, potential N-terminus modifications", "+42.010565@[");
        DEFAULT_MAP.put("refine, potential C-terminus modifications", "");
        DEFAULT_MAP.put("refine, unanticipated cleavage", "yes");
        DEFAULT_MAP.put("refine, potential modification mass", "");
        DEFAULT_MAP.put("refine, point mutations", "no");
        DEFAULT_MAP.put("refine, use potential modifications for full refinement", "no");
        DEFAULT_MAP.put("refine, point mutations", "no");
        DEFAULT_MAP.put("refine, potential modification motif", "");
        DEFAULT_MAP.put("scoring, minimum ion count", "4");
        DEFAULT_MAP.put("scoring, maximum missed cleavage sites", "1");
        DEFAULT_MAP.put("scoring, x ions", "no");
        DEFAULT_MAP.put("scoring, y ions", "yes");
        DEFAULT_MAP.put("scoring, z ions", "no");
        DEFAULT_MAP.put("scoring, a ions", "no");
        DEFAULT_MAP.put("scoring, b ions", "yes");
        DEFAULT_MAP.put("scoring, c ions", "no");
        DEFAULT_MAP.put("scoring, cyclic permutation", "no");
        DEFAULT_MAP.put("scoring, include reverse", "no");
        DEFAULT_MAP.put("scoring, cyclic permutation", "no");
        DEFAULT_MAP.put("scoring, include reverse", "no");
        DEFAULT_MAP.put("output, log path", "");
        DEFAULT_MAP.put("output, message", "testing 1 2 3");
        DEFAULT_MAP.put("output, one sequence copy", "no");
        DEFAULT_MAP.put("output, sequence path", "");
        DEFAULT_MAP.put("output, path", "output.xml");
        DEFAULT_MAP.put("output, sort results by", "protein");
        DEFAULT_MAP.put("output, path hashing", "yes");
        DEFAULT_MAP.put("output, xsl path", "tandem-style.xsl");
        DEFAULT_MAP.put("output, parameters", "yes");
        DEFAULT_MAP.put("output, performance", "yes");
        DEFAULT_MAP.put("output, spectra", "yes");
        DEFAULT_MAP.put("output, histograms", "yes");
        DEFAULT_MAP.put("output, proteins", "yes");
        DEFAULT_MAP.put("output, sequences", "yes");
        DEFAULT_MAP.put("output, one sequence copy", "no");
        DEFAULT_MAP.put("output, results", "valid");
        DEFAULT_MAP.put("output, maximum valid expectation value", "0.1");
        DEFAULT_MAP.put("output, histogram column width", "30");

    }

    /**
     * read a simplr file with 6 parameters - used as input to JXTandem
     *
     */
    @Test
    public void testReadInput()
    {
        final Class<? extends NotesReaderTest> aClass = getClass();
        InputStream is = aClass.getResourceAsStream(INPUT_NOTES);
        Assert.assertNotNull(is);
        Map<String, String> notes = XTandemUtilities.readNotes(is,INPUT_NOTES);
        mapsAreEqual(notes, INPUT_MAP);


    }

    @Test
    public void testReadDefaultInput()
    {
        final Class<? extends NotesReaderTest> aClass = getClass();
        InputStream is = aClass.getResourceAsStream(DEFAULT_NOTES);
        Assert.assertNotNull(is);

        Map<String, String> notes = XTandemUtilities.readNotes(is,DEFAULT_NOTES);
        mapsAreEqual(notes, DEFAULT_MAP);

    }

    /**
     * test 2 maps for equality
     *
     * @param m1
     * @param m2
     */
    public static void mapsAreEqual(Map<String, String> m1, Map<String, String> m2)
    {
        Assert.assertEquals(m1.size(), m2.size());
        for (String key : m1.keySet()) {
            String actual = m2.get(key);
            String expected = m1.get(key);
            if(!expected.equals(actual))
                 Assert.assertEquals(expected, actual);
        }

    }
}
