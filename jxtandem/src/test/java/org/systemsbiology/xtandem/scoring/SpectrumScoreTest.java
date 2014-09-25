package org.systemsbiology.xtandem.scoring;

import org.junit.*;
import org.systemsbiology.xtandem.*;
import org.systemsbiology.xtandem.ionization.*;
import org.systemsbiology.xtandem.peptide.*;
import org.systemsbiology.xtandem.reporting.*;
import org.systemsbiology.xtandem.testing.*;

import java.io.*;
import java.util.*;

/**
 * org.systemsbiology.xtandem.scoring.SpectrumScoreTest
 *
 * @author Steve Lewis
 * @date Jan 13, 2011
 */
public class SpectrumScoreTest {
    public static SpectrumScoreTest[] EMPTY_ARRAY = {};
    public static Class THIS_CLASS = SpectrumScoreTest.class;

    public static final String TEST = "ITLKEAWDQREAWDQRNGFIQSLK";

    // @Test  todo fix
    public void testScoringOutput() throws Exception {
        XTandemMain main = new XTandemMain(
                XTandemUtilities.getResourceStream("smallSample/tandem.params"),
                "smallSample/tandem.params");
        main.loadTaxonomy();
        main.loadScoring();
        main.loadSpectra();
        XTandemDebugging.setDebugging(true,main);
        Scorer scoreRunner = main.getScoreRunner();
        scoreRunner.score();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        BiomlReporter reporter = new BiomlReporter(main, scoreRunner, out);
        reporter.writeReport();
        out.close();

        final InputStream is1 = XTandemUtilities.getResourceStream("smallSample/output.xml");
        final byte[] buf = out.toByteArray();
        String outStr = new String(buf);
        final InputStream is2 = new ByteArrayInputStream(buf);
        final boolean b = XTandemUtilitiesTest.areStreamsEquivalent(is1, is2,
                new XTandemOutputEquivalent());
        Assert.assertTrue( b);


    }

    public static final String[] STRINGS_TO_REPLACE = {
            "\t",
            "res://",
            "smallSample/",
            "671.426",  // small roundoff issue
            "671.425",

    };
    public static final String[] STRINGS_TO_ACCEPT = {
            "742.465",
    };

    public static final String PARAMEZTER_START = "<group label=\"input parameters\" type=\"parameters\">";

    public static class XTandemOutputEquivalent implements ITextEquivalence {
        private int m_TestCount;
        private boolean m_InParameters;

        /**
         * allow short circuit
         *
         * @return
         */
        public boolean isTestOver() {
            return m_InParameters;
        }

        /**
         * return true when the two lines are equivalent
         *
         * @param !null pLine1
         * @param !null pLine2
         * @return as above
         */
        @Override
        public boolean areLinesEquivalent(String pLine1, String pLine2) {
            m_TestCount++;
            if (isTestOver())
                return true;
            if (pLine1.equals(PARAMEZTER_START) &&
                    pLine2.equals(PARAMEZTER_START)) {
                m_InParameters = true;
                return true;
            }
            String line1 = pLine1.replace(" ", "");
            String line2 = pLine2.replace(" ", "");


            // Formatting issues - just too messy
            for (int i = 0; i < STRINGS_TO_ACCEPT.length; i++) {
                if (line1.startsWith(STRINGS_TO_ACCEPT[i]) &&
                        line2.startsWith(STRINGS_TO_ACCEPT[i]))
                    return true;
            }

            for (int i = 0; i < STRINGS_TO_REPLACE.length; i++) {
                line1 = line1.replace(STRINGS_TO_REPLACE[i], "");
                line2 = line2.replace(STRINGS_TO_REPLACE[i], "");

            }

            final boolean b = line1.equalsIgnoreCase(line2);
            if (b)
                return b;

            /** look in detail at the difference */
            for (int i = 0; i < Math.min(line1.length(), line2.length()); i++) {
                char t1 = Character.toUpperCase(line1.charAt(i));
                char t2 = Character.toUpperCase(line2.charAt(i));
                if (t1 != t2) {
                    System.out.println("Bad Match");
                    System.out.println(pLine1);
                    System.out.println(pLine2);
                    return false;
                }
            }
            System.out.println("Bad Match");
            System.out.println(pLine1);
            System.out.println(pLine2);
            return b; // false case break
        }
    }

    // @Test  todo fix
    public void testScoring() {
        XTandemMain main = new XTandemMain(XTandemUtilities.getResourceStream("input3.xml"),
                "input3.xml");
        main.loadTaxonomy();
        main.loadScoring();
        main.loadSpectra();
 
        final MassSpecRun[] specRuns = main.getRuns();
        Assert.assertEquals(specRuns.length, 1);

        MassSpecRun onlyRun = specRuns[0];
        final IMeasuredSpectrum[] scans = onlyRun.getScans();
        Assert.assertEquals(scans.length, onlyRun.getScanCount());

        final IScoredScan[] scores = new ScoredScanOriginal[scans.length];
        for (int i = 0; i < scores.length; i++) {
            scores[i] = new ScoredScanOriginal((RawPeptideScan) scans[i]);

        }

        final SpectrumCondition sc = main.getSpectrumParameters();
        IMeasuredSpectrum[] conditionedScans = sc.conditionSpectra(scores);

        Assert.assertEquals(conditionedScans.length, onlyRun.getScanCount());

        IMeasuredSpectrum scan0 = conditionedScans[1];
        ISpectrumPeak peak;
        peak = getPeakWithMass(scan0, 275.3264);
        Assert.assertEquals( 5.9345984,peak.getPeak(), 0.001);

        peak = getPeakWithMass(scan0, 388.42);
        Assert.assertEquals( 2.2253618,peak.getPeak(), 0.001);

        peak = getPeakWithMass(scan0, 503.3033);
        Assert.assertEquals( 40.505623,peak.getPeak(), 0.001);

        peak = getPeakWithMass(scan0, 632.429);
        Assert.assertEquals(64.29064,peak.getPeak(),  0.001);

        peak = getPeakWithMass(scan0, 719.4764);
        Assert.assertEquals( 32.11596,peak.getPeak(), 0.001);

        for (int i = 0; i < conditionedScans.length; i++) {
            IMeasuredSpectrum scan = conditionedScans[i];

            validateConditioning(sc, scan);
        }

        Scorer runner = main.getScoreRunner();

        runner.digest();
        IPolypeptide[] peptides = runner.getPeptides();
        SequenceUtilities su = main.getSequenceUtilities();

        // save work print the results

        for (int i = 0; i < peptides.length; i++) {
            IPolypeptide peptide = peptides[i];
            double massPlusH = peptide.getMass() + XTandemUtilities.getProtonMass() + XTandemUtilities.getCleaveCMass() + XTandemUtilities.getCleaveNMass();

            ITheoreticalSpectrumSet spectrumSet = new TheoreticalSpectrumSet(3, massPlusH, peptide);
            PeptideSpectrum ps = new PeptideSpectrum(spectrumSet, 1, IonType.B_ION_TYPES, su);
            ITheoreticalSpectrum conditioned = ScoringUtilities.applyConditioner(ps,
                    new XTandemTheoreticalScoreConditioner());
            String sequence = peptide.getSequence();
            System.out.println(sequence);
        }
        runner.generateTheoreticalSpectra();
        IonUseCounter counter = new IonUseCounter();
        ITheoreticalSpectrumSet[] spectrums = runner.getAllSpectra();
        double MaxMassDIfferenceForScore = 1;
        for (int i = 0; i < conditionedScans.length; i++) {
            IMeasuredSpectrum scan = conditionedScans[i];
            double testmass = scan.getPrecursorMass();
            for (int j = 0; j < spectrums.length; j++) {
                ITheoreticalSpectrumSet tsSet = spectrums[j];

                final IPolypeptide pp = tsSet.getPeptide();

                final ITheoreticalSpectrum[] spectrums1 = tsSet.getSpectra();
                for (int k = 0; k < spectrums1.length; k++) {
                    ITheoreticalSpectrum ts = spectrums1[k];
                    double mass = pp.getMass();
                    if (true) { // Math.abs(mass - testmass) < MaxMassDIfferenceForScore) {
                        counter.clear();
                        List<DebugMatchPeak> holder = new ArrayList<DebugMatchPeak>();
                        double product = runner.getAlgorithm().dot_product(scan, ts, counter,holder);
                        if (product == 0)
                            continue;
                        double factor = runner.getCountFactor(counter);

                        double score = product * factor;
                        System.out.println(
                                "dot product " + product + " factor " + factor + " score " + score);
                    }

                }
            }
        }


    }

    public void validateConditioning(SpectrumCondition sc, IMeasuredSpectrum scan) {
        Assert.assertTrue(scan.getPeaksCount() <= sc.gettMaxPeaks());
        SpectrumStatistics stats = new SpectrumStatistics(scan);

        // test normalization
        Assert.assertEquals(stats.getMaxPeak(), sc.getfDynamicRange(), 0.0001);
        final ISpectrumPeak[] peaks = scan.getPeaks();


        for (int i = 0; i < peaks.length - 1; i++) {
            double delMass = peaks[i + 1].getMassChargeRatio() - peaks[i].getMassChargeRatio();
            // peaks are acsending
            Assert.assertTrue(0 <= delMass);
            // remove isotopes does this
            Assert.assertTrue(0.95 <= delMass);
            //    System.out.println("mass = " + peaks[i].getMass() + " peak= " + peaks[i].getPeak());
        }
    }

    public static ISpectrumPeak getPeakWithMass(ISpectrum spec, double mass) {
        for (ISpectrumPeak test : spec.getPeaks()) {
            final double mass1 = test.getMassChargeRatio();
            if (Math.abs(mass - mass1) < 0.1)
                return test;
        }
        return null;
    }

    /*
    matched sequence ADEQPPDPLPGTRTTTRWR at mass 2194.1 upper = 2192.25 lower = 2190.25index = 0
   match at mass+1 362 added=0.0177298 modifier=0 score=0.0177298
   match at mass 517 added=0.0346736 modifier=0 score=0.0524034
   match at mass 1077 added=0.0786768 modifier=0 score=0.13108
   match at mass-1 1076 added=0.039104 modifier=0 score=0.170184
   match at mass+1 1078 added=0.0160587 modifier=0 score=0.186243
   match at mass-1 1133 added=0.0279104 modifier=0 score=0.214153
   match at mass 1231 added=0.0294977 modifier=0 score=0.243651
   match at mass-1 1230 added=0.0538537 modifier=0 score=0.297505
   match at mass+1 1232 added=0.00539075 modifier=0 score=0.302896
   match at mass 1750 added=0.0486003 modifier=0 score=0.351496
   match at mass+1 1879 added=0.024419 modifier=0 score=0.375915
    score=0.375915 count 4
   ADEQPPDPLPGTRTTTRWR type Y charge:1 total calls 0 score=0 count=4
   match at mass+1 941 added=0.00466782 modifier=0 score=0.00466782
   match at mass 1061 added=0.0358557 modifier=0 score=0.0405235
    score=0.0405235 count 1
   match at mass-1 443 added=0.0158312 modifier=0 score=0.0158312
   match at mass 541 added=0.0276887 modifier=0 score=0.0435199
   match at mass+1 542 added=0.0140709 modifier=0 score=0.0575908
   match at mass 753 added=0.0416209 modifier=0 score=0.0992117
   match at mass-1 752 added=0.0149272 modifier=0 score=0.114139
   match at mass-1 962 added=0.0159074 modifier=0 score=0.130046
   match at mass+1 964 added=0.00498223 modifier=0 score=0.135028
   match at mass-1 1059 added=0.0124797 modifier=0 score=0.147508
   match at mass-1 1373 added=0.0161003 modifier=0 score=0.163608
   match at mass+1 1375 added=0.0520461 modifier=0 score=0.215655
   match at mass 1833 added=0.0392255 modifier=0 score=0.25488
    score=0.25488 count 3
   ADEQPPDPLPGTRTTTRWR type B charge:1 total calls 1 score=0.416438 count=3
   ADEQPPDPLPGTRTTTRWR charge:2 score=151.159 hyperscore=151.159 hFactor1=1 hFactor2=1
   */

    /*
  matched sequence HAFYQSANVPAGLLDYQHR at mass 2187.07 upper = 2189.85 lower = 2187.85index = 17
 match at mass 312 added=0.0324891 modifier=0 score=0.0324891
 match at mass-1 439 added=0.00142156 modifier=0 score=0.0339107
 match at mass-1 602 added=0.00209515 modifier=0 score=0.0360058
 match at mass+1 604 added=0.0114226 modifier=0 score=0.0474284
 match at mass 718 added=0.037519 modifier=0 score=0.0849474
 match at mass-1 717 added=0.0915188 modifier=0 score=0.176466
 match at mass+1 719 added=0.00133038 modifier=0 score=0.177797
 match at mass-1 943 added=0.00856826 modifier=0 score=0.186365
 match at mass+1 1073 added=0.0178012 modifier=0 score=0.204166
 match at mass-1 1168 added=0.0122501 modifier=0 score=0.216416
 match at mass+1 1269 added=0.0392205 modifier=0 score=0.255637
  score=0.255637 count 2
 HAFYQSANVPAGLLDYQHR type Y charge:1 total calls 2 score=0 count=2
 match at mass-1 358 added=0.0106114 modifier=0 score=0.0106114
 match at mass+1 360 added=0.010349 modifier=0 score=0.0209604
 match at mass-1 415 added=0.0182335 modifier=0 score=0.0391939
 match at mass+1 474 added=0.00445829 modifier=0 score=0.0436521
 match at mass 537 added=0.00420672 modifier=0 score=0.0478589
 match at mass 635 added=0.00522487 modifier=0 score=0.0530837
 match at mass-1 634 added=0.00372225 modifier=0 score=0.056806
 match at mass 771 added=0.0048254 modifier=0 score=0.0616314
 match at mass-1 770 added=0.0158836 modifier=0 score=0.077515
 match at mass+1 836 added=0.0192418 modifier=0 score=0.0967568
 match at mass+1 917 added=0.00343335 modifier=0 score=0.10019
 match at mass-1 1024 added=0.00134526 modifier=0 score=0.101535
 match at mass+1 1026 added=0.0071909 modifier=0 score=0.108726
  score=0.108726 count 3
 HAFYQSANVPAGLLDYQHR type Y charge:2 total calls 3 score=0 count=3
 match at mass 356 added=0.0305577 modifier=0 score=0.0305577
 match at mass-1 355 added=0.0112765 modifier=0 score=0.0418343
 match at mass-1 518 added=0.0153908 modifier=0 score=0.0572251
 match at mass+1 648 added=0.00448343 modifier=0 score=0.0617085
  score=0.0617085 count 1
 HAFYQSANVPAGLLDYQHR type B charge:1 total calls 4 score=0.364363 count=1
 match at mass 260 added=0.0327831 modifier=0 score=0.0327831
 match at mass 367 added=0.0291462 modifier=0 score=0.0619293
 match at mass 509 added=0.0126072 modifier=0 score=0.0745365
 match at mass 558 added=0.0336938 modifier=0 score=0.10823
 match at mass 622 added=0.0784294 modifier=0 score=0.18666
 match at mass-1 621 added=0.00231154 modifier=0 score=0.188971
 match at mass+1 623 added=0.0026859 modifier=0 score=0.191657
 match at mass 679 added=0.000550281 modifier=0 score=0.192207
 match at mass-1 678 added=0.00361116 modifier=0 score=0.195819
 match at mass-1 792 added=0.0438869 modifier=0 score=0.239706
 match at mass+1 794 added=0.0164773 modifier=0 score=0.256183
 match at mass+1 875 added=0.00322111 modifier=0 score=0.259404
 match at mass 1006 added=0.00569348 modifier=0 score=0.265097
  score=0.265097 count 7
 HAFYQSANVPAGLLDYQHR type B charge:2 total calls 5 score=0.364363 count=7

    */

}
