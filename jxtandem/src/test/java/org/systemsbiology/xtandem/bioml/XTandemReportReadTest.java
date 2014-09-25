package org.systemsbiology.xtandem.bioml;

import org.junit.*;
import org.systemsbiology.xtandem.*;
import org.systemsbiology.xtandem.scoring.*;

import java.io.*;

/**
 * org.systemsbiology.xtandem.bioml.XTandemReportReadTest
 * User: steven
 * Date: 8/22/11
 */
public class XTandemReportReadTest {
    public static final XTandemReportReadTest[] EMPTY_ARRAY = {};

    public static final String MZXML_RESOURCE = "res://smallSample/or20080317_s_silac-lh_1-1_01short.mzxml";   // "E:/TestSetForSteveLewis1/data/020306_yeast1_SCX_28.mzXML"; //

    public MassSpecRun readMZXMLRuns() {
        InputStream is = XTandemUtilities.getDescribedStream(MZXML_RESOURCE);
        MassSpecRun[] runs = XTandemUtilities.parseMZXMLStream(is);
        return runs[0];
    }

    @Test
    public void testReadXTandemReport() {
        XTandemScoringReport report = XTandemUtilities.readXTandemFile("res://bioml/TestBiomlFile.xml");    // "E:/TestSetForSteveLewis1/020306_yeast1_SCX_28.tandem" ); //
        ScoredScan[] scans = report.getScans();
        MassSpecRun run = readMZXMLRuns();

        for (int i = 0; i < scans.length; i++) {
            ScoredScan scan = scans[i];
            RawPeptideScan origScan = run.getScan(scan.getId());
            if(origScan != null)
                validateFirstScan(scan, origScan);
        }
      }

    private void validateFirstScan(final ScoredScan scan, RawPeptideScan orig) {
        String id = scan.getId();
        Assert.assertEquals(orig.getId(), id);
        RawPeptideScan raw = scan.getRaw();
        validateSamePeaks(orig, raw);
        validateSameData(orig, raw);
    }

    private void validateSameData(final RawPeptideScan orig, final RawPeptideScan pRaw) {
        int pc1 = pRaw.getPrecursorCharge();
        int pc2 = orig.getPrecursorCharge();
        if(pc1 == 0)
            return;
        if(pc2 == 0)
            return;
        if(pc1 != pc2  )
            Assert.assertEquals(pc1, pc2);
        double pm1 = pRaw.getPrecursorMassChargeRatio();
        double pm2 = orig.getPrecursorMassChargeRatio();
        Assert.assertEquals(pm2, pm1, 0.1);

    }

    private void validateSamePeaks(final RawPeptideScan orig, final RawPeptideScan pRaw) {
        ISpectrumPeak[] origpeaks = orig.getPeaks();
        ISpectrumPeak[] peaks = pRaw.getPeaks();
        Assert.assertEquals(origpeaks.length, peaks.length);
        for (int i = 0; i < peaks.length; i++) {
            ISpectrumPeak peak = peaks[i];
            ISpectrumPeak pk2 = origpeaks[i];
            Assert.assertEquals(peak.getMassChargeRatio(), pk2.getMassChargeRatio(), 0.1);
            Assert.assertEquals((int) (0.5 + peak.getPeak()), (int) (0.5 + pk2.getPeak()));

        }
    }

    @Test
    public void testEquivalentReadXTandemReport() {
        //   throw new UnsupportedOperationException("Fix This"); // ToDo
    }



}
