package org.systemsbiology.xtandem;

import org.junit.*;
import org.systemsbiology.xtandem.testing.*;

/**
 * org.systemsbiology.xtandem.XTandemMainTest
 *
 * @author Steve Lewis
 * @date Jan 6, 2011
 */
public class XTandemMainTest {
    public static XTandemMainTest[] EMPTY_ARRAY = {};
    public static Class THIS_CLASS = XTandemMainTest.class;

    /**
     * this only works when pointed at the user.dir is the data directory
     */
    @Test
    public void testSimpleInput() {

        XTandemMain main = new XTandemMain(XTandemUtilities.getResourceStream("input2.xml"), "input2.xml");

        main.loadScoringTest();
        main.loadSpectra();
        XTandemDebugging.setDebugging(true, main);
        main.process();

        final MassSpecRun[] specRuns = main.getRuns();
        Assert.assertEquals(specRuns.length, 1);

        MassSpecRun onlyRun = specRuns[0];
        Assert.assertEquals(onlyRun.getScanCount(), 35);

        validateRun(onlyRun);

    }

    @Test
    public void testGzSimpleInput() {
        XTandemMain main = new XTandemMain(XTandemUtilities.getResourceStream("input2.xml"), "input2.xml");
        main.loadScoringTest();
        main.loadSpectra();
        XTandemDebugging.setDebugging(true, main);
        main.process();

        final MassSpecRun[] specRuns = main.getRuns();
        Assert.assertEquals(specRuns.length, 1);

        MassSpecRun onlyRun = specRuns[0];
        Assert.assertEquals(onlyRun.getScanCount(), 35);

        validateRun(onlyRun);

    }


    public void validateRun(MassSpecRun onlyRun) {
        final RawPeptideScan[] scans = onlyRun.getScans();
        Assert.assertEquals(onlyRun.getScanCount(), scans.length);

        for (int i = 0; i < scans.length; i++) {
            RawPeptideScan scan = scans[i];
            validateScan(scan);

            // if there is an instrument we better have it
            final String id = scan.getInstrumentId();
            if (id != null) {
                final MassSpecInstrument specInstrument = onlyRun.getInstrument(id);
                Assert.assertNotNull(specInstrument);
            }
        }
    }


    public void validateScan(RawPeptideScan scan) {
        int peaksCount = scan.getPeaksCount();
        final ISpectrumPeak[] spectrumPeaks = scan.getPeaks();
        Assert.assertEquals(peaksCount, spectrumPeaks.length);


    }


}
