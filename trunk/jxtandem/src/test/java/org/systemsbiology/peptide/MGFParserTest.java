package org.systemsbiology.peptide;

import org.junit.*;
import org.systemsbiology.xtandem.*;

import java.io.*;
import java.util.*;

/**
 * org.systemsbiology.peptide.MGFParserTest
 * User: steven
 * Date: 12/14/11
 */
public class MGFParserTest {
    public static final MGFParserTest[] EMPTY_ARRAY = {};

    public static final String MGF_RESOURCE = "res://mgf/7962salivaryglandhealthycontrol.mgf";

    public static final String[] PEPMASS_STRINGS = {
            "PEPMASS=459.17000000000002 8795.7734375",
            "PEPMASS=445.20999999999998 7451.50732421875",
            "PEPMASS=476.27999999999997 5728.51513671875",
    };
    public static final double[] PEPMASSES = {
            459.17000000000002,
            445.20999999999998,
            476.27999999999997,
    };

    @Test
    public void testPepMassParse() {
        for (int i = 0; i < PEPMASS_STRINGS.length; i++) {
            double mass = XTandemUtilities.parsePepMassLine(PEPMASS_STRINGS[i]);
            Assert.assertEquals(PEPMASSES[i], mass, 0.0001);
        }
    }


    @Test
    public void testMGFRead() {
        InputStream is = XTandemUtilities.getDescribedStream(MGF_RESOURCE);
        Assert.assertNotNull(is);  // make sure it exists
        LineNumberReader inp = new LineNumberReader(new InputStreamReader(is));
        int number_scans = 1;
        RawPeptideScan rawPeptideScan = XTandemUtilities.readMGFScan(inp, "");
         String id = rawPeptideScan.getId();
        Assert.assertEquals(number_scans, Integer.parseInt(id));

        while (rawPeptideScan != null) {

            id = rawPeptideScan.getId();
            Assert.assertNotNull(id);  // make sure it exists
            Assert.assertEquals(number_scans, Integer.parseInt(id));
            String label = rawPeptideScan.getLabel();
            Assert.assertNotNull(label);  // make sure it exists

            ISpectrumPeak[] peaks = rawPeptideScan.getPeaks();
            if(peaks.length < 2)
                 Assert.assertTrue(peaks.length > 2);  // make there are some peaks

            if("4663".equals(id))
                XTandemUtilities.breakHere();// walk through last case
            number_scans++;
            rawPeptideScan = XTandemUtilities.readMGFScan(inp, "");
          }

        Assert.assertEquals(4664, number_scans);

    }

    /**
      * test reading an MGF Scan and that all ids are unique
      * @param pIs
      */
    @Test
    public void testUniqueIds() {
        InputStream is = XTandemUtilities.getDescribedStream(MGF_RESOURCE);
        testMGFStream(is);

    }

    /**
     * test reading an MGF Scan and that all ids are unique
     * @param pIs
     */
    protected void testMGFStream(final InputStream pIs) {
        Set<String> seenIds = new HashSet<String>();
        Assert.assertNotNull(pIs);  // make sure it exists
        LineNumberReader inp = new LineNumberReader(new InputStreamReader(pIs));
          RawPeptideScan rawPeptideScan = XTandemUtilities.readMGFScan(inp, "");
        String id = rawPeptideScan.getId();

        while (rawPeptideScan != null) {

            id = rawPeptideScan.getId();
            Assert.assertTrue(!seenIds.contains(id));   // make sure is is unique
            seenIds.add(id);
            Assert.assertNotNull(id);  // make sure it exists
             String label = rawPeptideScan.getLabel();
            Assert.assertNotNull(label);  // make sure it exists

            ISpectrumPeak[] peaks = rawPeptideScan.getPeaks();
            if(peaks.length < 2)
                 Assert.assertTrue(peaks.length > 2);  // make there are some peaks

              rawPeptideScan = XTandemUtilities.readMGFScan(inp, "");
          }

      }

    /**
     * test read an MGF file
     * @param args   artgs[0] is the name of the file
     * @throws Exception
     */
    public static void main(String[] args) throws Exception
    {
        InputStream is = new FileInputStream(args[0]);
        new MGFParserTest().testMGFStream(is);
    }

}
