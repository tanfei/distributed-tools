package org.systemsbiology.xtandem;

import org.junit.*;

import java.io.*;
import java.util.*;

/**
 * org.systemsbiology.xtandem.NotesReaderTest
 *
 * @author Steve Lewis
 * @date Jan 4, 2011
 */
public class MspReaderTest
{
    public static MspReaderTest[] EMPTY_ARRAY = {};
    public static Class THIS_CLASS = MspReaderTest.class;


    @Test
    public void testReadMSPFile()
    {
        final Class<? extends MspReaderTest> aClass = getClass();
        String url = "test_spectra.msp";
        InputStream is = aClass.getResourceAsStream(url);
        Assert.assertNotNull(is);
        MassSpecRun[] runs = XTandemUtilities.parseMspFile(is, url);
        Assert.assertEquals(runs.length, 1);
        MassSpecRun run = runs[0];
        RawPeptideScan[] scans = run.getScans();
        Assert.assertEquals(scans.length, 10);
        RawPeptideScan ps0 = scans[0];
        ISpectrumPeak[] peaks = ps0.getPeaks();
        Assert.assertEquals( 83, peaks.length );
        ISpectrumPeak peak0 = peaks[0];
        Assert.assertEquals(399.6, peak0.getMassChargeRatio(),0.01);

        RawPeptideScan ps9 = scans[9];
        ISpectrumPeak[] peaks9 = ps9.getPeaks();
        Assert.assertEquals(82, peaks9.length );
        ISpectrumPeak peak90 = peaks9[81];
        Assert.assertEquals(938.4, peak90.getMassChargeRatio(),0.01);


    }


}
