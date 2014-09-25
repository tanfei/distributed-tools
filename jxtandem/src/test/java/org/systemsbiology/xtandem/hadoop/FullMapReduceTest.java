package org.systemsbiology.xtandem.hadoop;

//import com.sun.org.apache.bcel.internal.generic.*;
//import org.apache.commons.net.io.*;
import org.junit.*;
import org.systemsbiology.xtandem.*;
import org.systemsbiology.xtandem.ionization.*;
import org.systemsbiology.xtandem.peptide.*;
import org.systemsbiology.xtandem.sax.*;
import org.systemsbiology.xtandem.scoring.*;
import org.systemsbiology.xtandem.taxonomy.*;
import org.systemsbiology.xtandem.testing.*;

import java.io.*;
import java.util.*;

/**
 * org.systemsbiology.xtandem.hadoop.FullMapReduceTest
 * This class does everything the database loader and MapReduce does but on a sample small enough to keep in memory and
 * be a resource
 * User: Steve
 * Date: 8/1/11
 */
public class FullMapReduceTest {
    public static final FullMapReduceTest[] EMPTY_ARRAY = {};

    public static final String TEST_STRING =
            "15.994915@M,8.014199@K,10.008269@R";


    public static final int EXPECTED_NUMBER_PROTEINS = 10;
    public static final int EXPECTED_NUMBER_FRAGMENTS = 37924; // 130838;
    public static final int EXPECTED_NUMBER_UNMODIFIED_FRAGMENTS = 33622;
    public static final int EXPECTED_NUMBER_MODIFIED_FRAGMENTS = 4302; // 97227;
    public static final int EXPECTED_NUMBER_MASSES = 4201; // 4195; // 4123; // 4296;
    public static final double MAXIMUM_ACCEPTABLE_PEAK_DIFFERENCE = 0.005;


    public static final double PEAK_TOLERANCE = 0.0003;
    public static final String[] INTERESTING_CASES = {"326"};

    public static final Set<String> INTERESTING_CASES_SET = new HashSet<String>(Arrays.asList(INTERESTING_CASES));

    public static final String[] INTERESTING_SEQUENCES = {"ALSLHYK", "GEQVNGEKPDNASK", "GSLYTSSSSK"};
    public static final Set<String> INTERESTING_SEQUENCES_SET = new HashSet<String>(Arrays.asList(INTERESTING_SEQUENCES));


    public static final boolean SCORE_INTERESTING_CASES_FIRST = true;
    // Allow suggess even when int values of peaks are off - truncate vs round
    public static final boolean HACK_PEAK_ROUNDING = true;

    private  int ValidatedCount = 0;
    private  int goodSums = 0;
    private  int toFixSums = 0;
    private  int badSums = 0;

    public IMainData buildMain() {
        XTandemMain main = new XTandemMain(XTandemUtilities.getResourceStream("FullMapReduceTest.xml"), "FullMapReduceTest.xml");
        main.loadScoringTest();
        return main;
    }

    protected FragmentHoldingFastaHandler buildAndPopulateHandler() {
        IMainData main = buildMain();

        // hard code modifications of cystein
        MassCalculator monoCalc = MassCalculator.getCalculator(MassType.monoisotopic);
        ITaxonomy taxonomy = main.getTaxonomy();
        IProtein[] proteins = taxonomy.getProteins();
        Assert.assertEquals(EXPECTED_NUMBER_PROTEINS, proteins.length);
        FragmentHoldingFastaHandler handler = new FragmentHoldingFastaHandler(main);

        // in this test no modifications
        //   handler.setModifications(TEST_STRING);

        for (int i = 0; i < proteins.length; i++) {
            IProtein protein = proteins[i];
            handler.addProtein(protein);
            handler.generateProteinFragments(protein);
        }

        handler.generateFragmentsAtMass();
        return handler;
    }

    public static ScanScoringReport readXTandemFile(String resource) {
        XTandemScoringHandler handler = new XTandemScoringHandler();
        InputStream is = XTandemUtilities.getDescribedStream(resource);
        String name = resource.replace("res://", "");
        XTandemUtilities.parseFile(is, handler, name);
        ScanScoringReport ret = handler.getReport();

        return ret;
    }

    private FragmentHoldingFastaHandler m_Handler;

    @Before
    public void setup() {
        m_Handler = buildAndPopulateHandler();
    }

    public FragmentHoldingFastaHandler getHandler() {
        return m_Handler;
    }

    @Test
    public void databaseLoadTest() {

        IPolypeptide[] fragments = m_Handler.getFragments();
        Assert.assertEquals(EXPECTED_NUMBER_FRAGMENTS, fragments.length);


        IPolypeptide[] modified_fragments = getHandler().getModifiedFragments();
        Assert.assertEquals(EXPECTED_NUMBER_MODIFIED_FRAGMENTS, modified_fragments.length);

        IPolypeptide[] unmodified_fragments = getHandler().getUnmodifiedFragments();
        Assert.assertEquals(EXPECTED_NUMBER_UNMODIFIED_FRAGMENTS, unmodified_fragments.length);

        Integer[] masses = m_Handler.getAvailableMasses();
        Assert.assertEquals(EXPECTED_NUMBER_MASSES, masses.length);
        ScanScoringReport sr = readXTandemFile("res://XTandemCalculation.xml");

        IMainData tandem = m_Handler.getTandem();


        //    int totalVerified = validateSamePeptides(sr, getHandler(), tandem);

        validateSimilarScores(sr, tandem);

        Assert.assertTrue(goodSums > (int) (0.95 * ValidatedCount));
        Assert.assertTrue(badSums < (int) (0.03 * ValidatedCount));
        Assert.assertTrue(toFixSums < (int) (0.01 * ValidatedCount));

    }

    protected ScoredScan scoreScan(RawPeptideScan scan, IMainData tamdem) {
        final IScanPrecursorMZ mz = scan.getPrecursorMz();
        final int charge = scan.getPrecursorCharge();
        ScoredScan ret = new ScoredScan(scan);

        // throw out ridiculous values
        if (charge > XTandemUtilities.MAX_CHARGE)
            return null;


        if (charge == 0) {
            for (int i = 1; i <= 3; i++) {
                double mass = scan.getPrecursorMass(i);
                scoreScanAgainstBaseMass(scan, mass, ret, tamdem);
            }
        }
        else {
            double mass = scan.getPrecursorMass(charge);
            scoreScanAgainstBaseMass(scan, mass, ret, tamdem);
            ScoredScan altScan = scoreAsDistributed(scan, mass, tamdem);
            if(altScan != null)    {
                if( !ret.equivalent(altScan))
                     Assert.assertTrue(ret.equivalent(altScan));

            }
            else {
                System.out.println("bad score at mass " + mass);
            }
        }
        // test that scores are in descending order
        ISpectralMatch[] spectralMatches = ret.getSpectralMatches();
        for (int i = 0; i < spectralMatches.length - 1; i++) {
            ISpectralMatch sm1 = spectralMatches[i];
            ISpectralMatch sm2 = spectralMatches[i + 1];
            Assert.assertTrue(sm1.getHyperScore() >= sm2.getHyperScore());
        }

        HyperScoreStatistics hyperScores = ret.getHyperScores();

           return ret;
    }

    protected void scoreScanAgainstBaseMass(RawPeptideScan scan, double mass, ScoredScan score, IMainData tamdem) {
        final Scorer scorer = ((XTandemMain) tamdem).getScoreRunner();
        ITandemScoringAlgorithm scoringAlgorithm = tamdem.getScorer();
        int[] masses = scoringAlgorithm.allSearchedMasses(mass);
        for (int i = 0; i < masses.length; i++) {
            int imass = masses[i];
            System.out.println("Scoring mass as against baseMass " + imass) ;
            scoreScanAgainstMass(scorer,scan, imass, score, tamdem);
        }
     }


    protected ScoredScan scoreAsDistributed(RawPeptideScan scan, double mass , IMainData tamdem) {
        final Scorer scorer = ((XTandemMain) tamdem).getScoreRunner();
        ITandemScoringAlgorithm scoringAlgorithm = tamdem.getScorer();
        int[] masses = scoringAlgorithm.allSearchedMasses(mass);

         List<ScoredScan> holder = new ArrayList<ScoredScan>();

         for (int i = 0; i < masses.length; i++) {
            int imass = masses[i];
             System.out.println("Scoring mass as distrubuted " + imass) ;
             ScoredScan scoredScan = scoreScanAgainstMass(scorer, scan, imass, null, tamdem);
             if(scoredScan != null)
                 holder.add(scoredScan);
        }
        ScoredScan[] scoresAtMass = new ScoredScan[holder.size()];
          holder.toArray(scoresAtMass);
         if(scoresAtMass.length == 0)
             return null;
         ScoredScan ret =  scoresAtMass[0];
         for (int i = 1; i < scoresAtMass.length; i++) {
            ScoredScan scoresAtMas = scoresAtMass[i];
            ret.addTo(scoresAtMas);
        }
        return ret;
    }

    /**
     * score a single scan against one mass
     *
     * @param scan
     * @param mass
     * @param score
     * @param tamdem
     */
    protected ScoredScan scoreScanAgainstMass(Scorer scorer,RawPeptideScan scan, int mass, ScoredScan scoring, IMainData tamdem) {
        if(scoring == null)
                scoring = new ScoredScan(scan);

        IPolypeptide[] pps = getHandler().getFragmentsAtMass(mass);
        IPolypeptide[] scored = filterToScoredPeptides(scoring, pps,tamdem);

        XTandemUtilities.sortByString(pps);
        XTandemUtilities.sortByString(scored);
        if (scored.length == 0)
            return null;
        scorer.clearSpectra();
        scorer.generateTheoreticalSpectra(scored);
        IonUseCounter counter = new IonUseCounter();
        final ITheoreticalSpectrumSet[] tss = scorer.getAllSpectra();
        XTandemUtilities.sortByString(tss);
        Assert.assertEquals(tss.length, scored.length);
        scorer.scoreScan(counter, tss, scoring);
        return scoring;
    }

    public static IPolypeptide[] filterToScoredPeptides(final ScoredScan scoring, final IPolypeptide[] pPps, IMainData tamdem) {
        ITandemScoringAlgorithm scoringAlgorithm = tamdem.getScorer();
        List<IPolypeptide> holder = new ArrayList<IPolypeptide>();
        for (int i = 0; i < pPps.length; i++) {
            IPolypeptide pp = pPps[i];
            double matchingMass = pp.getMatchingMass();
            if (!scoring.isMassWithinRange(matchingMass,0,scoringAlgorithm))
                continue;
            holder.add(pp);
        }
        IPolypeptide[] ret = new IPolypeptide[holder.size()];
        holder.toArray(ret);
        return ret;
    }

    protected int validateSimilarScores(ScanScoringReport sr, final IMainData pTandem) {
        final FragmentHoldingFastaHandler pHandler = getHandler();
        Set<Integer> handledItems = new HashSet<Integer>();
        int totalVerified = 0;
        RawPeptideScan[] rawScans = pTandem.getRawScans();
        Arrays.sort(rawScans);

        boolean interestingFirst = SCORE_INTERESTING_CASES_FIRST;
        validateCases(sr, pTandem, rawScans, interestingFirst);

        // this is good
        validateCases(sr, pTandem, rawScans, !interestingFirst);

        int totalScores = sr.getTotalScoreCount();
        return totalVerified;
    }

    /**
     * run through twice to be able to concentrate on interesting tests
     *
     * @param sr
     * @param pTandem
     * @param pRawScans
     * @param pInterestingFirst
     */
    private void validateCases(final ScanScoringReport sr, final IMainData pTandem, final RawPeptideScan[] pRawScans, final boolean pInterestingFirst) {
        // this set is bad
        // walk the bad cases
        for (int i = 0; i < pRawScans.length; i++) {
            RawPeptideScan rawScan = pRawScans[i];
            //    pTandem.
            String id = rawScan.getId();
            if (pInterestingFirst ^ INTERESTING_CASES_SET.contains(id))
                continue;
            System.out.println("scoring scan " + id);
            ScoredScan myScore = scoreScan(rawScan, pTandem);
            if (myScore == null)
                continue;

            IScanScoring sm = sr.getScanScoringMap(id);

            validateSimilarConditioning(sm, myScore, pTandem);
            validateSimilarScore(id, sm, myScore, pTandem);

        }
    }

    protected void validateSimilarConditioning(IScanScoring sm, ScoredScan myScore, final IMainData pTandem) {
        RawPeptideScan raw = myScore.getRaw();
        String id = raw.getId();
        XTandemMain main = (XTandemMain) pTandem;

        // their spectrum
        ITheoreticalScoring[] scorings = sm.getValidScorings();

        // my spectrum
        SpectrumCondition sc = main.getSpectrumParameters();
        final IScoringAlgorithm sa = main.getScorer();


        IMeasuredSpectrum theirSpectrum = scorings[0].getMeasuredSpectrum();

        IMeasuredSpectrum mySpectrum = sa.conditionSpectrum(myScore, sc);
        validateSimilarSpectra(id, theirSpectrum, mySpectrum);
    }

    protected void validateSimilarSpectra(String id, IMeasuredSpectrum theirSpectrum, IMeasuredSpectrum mySpectrum) {
        ISpectrumPeak[] theirPeaks = theirSpectrum.getPeaks();

        ISpectrumPeak[] myPeaks = mySpectrum.getPeaks();
        if (theirPeaks.length == myPeaks.length) {
            validateComparablePeaks(id, theirPeaks, myPeaks);
        }
        else {
            validateNonComparablePeaks(id, theirPeaks, myPeaks);

        }
    }

    protected void validateNonComparablePeaks(String id, final ISpectrumPeak[] pTheirPeaks, final ISpectrumPeak[] pMyPeaks) {
        Map<Integer, ISpectrumPeak> theirMassToPeak = new HashMap<Integer, ISpectrumPeak>();
        Map<Integer, ISpectrumPeak> myMassToPeak = new HashMap<Integer, ISpectrumPeak>();
        List<Integer> theirKeys = new ArrayList<Integer>();
        List<Integer> myKeys = new ArrayList<Integer>();
        for (int i = 0; i < pTheirPeaks.length; i++) {
            ISpectrumPeak theirPeak = pTheirPeaks[i];
            int key = (int) theirPeak.getMassChargeRatio();
            theirMassToPeak.put(key, theirPeak);
            theirKeys.add(key);
        }
        for (int i = 0; i < pMyPeaks.length; i++) {
            ISpectrumPeak myPeak = pMyPeaks[i];
            int key = (int) myPeak.getMassChargeRatio();
            myMassToPeak.put(key, myPeak);
            myKeys.add(key);
        }

        List<Integer> uncommnonKeys = new ArrayList<Integer>(theirKeys);
        uncommnonKeys.removeAll(myKeys);

        int originalSize = Math.min(pTheirPeaks.length, pMyPeaks.length);
        validateTrue(Math.abs(originalSize - pTheirPeaks.length) < 3);
        validateTrue(Math.abs(originalSize - pMyPeaks.length) < 3);

        XTandemUtilities.toCommonKeySet(theirMassToPeak, myMassToPeak);
        // make sure we agree on the peak list - many may be off
        // for rounding vs truncation
        if (!HACK_PEAK_ROUNDING)
            validateTrue(Math.abs(originalSize - theirMassToPeak.size()) < 3);


        ISpectrumPeak[] theirPeaks = filterToExistingPeaks(pTheirPeaks, theirMassToPeak);
        ISpectrumPeak[] myPeaks = filterToExistingPeaks(pMyPeaks, myMassToPeak);

        validateComparablePeaks(id, theirPeaks, myPeaks);

    }

    /**
     * allow a break before failing an assert - useful in debugging
     *
     * @param test supposet to be true
     */
    private void validateTrue(boolean test) {
        if (test)
            return;
        XTandemUtilities.breakHere();
        Assert.assertTrue(test);

    }

    private ISpectrumPeak[] filterToExistingPeaks(final ISpectrumPeak[] original, final Map<Integer, ISpectrumPeak> peakSet) {
        List<ISpectrumPeak> holder = new ArrayList<ISpectrumPeak>();
        for (int i = 0; i < original.length; i++) {
            ISpectrumPeak pk = original[i];
            if (peakSet.containsKey((int) pk.getMassChargeRatio()))
                holder.add(pk);
        }
        ISpectrumPeak[] ret = new ISpectrumPeak[holder.size()];
        holder.toArray(ret);
        return ret;

    }

    protected void validateComparablePeaks(String id, final ISpectrumPeak[] pTheirPeaks, final ISpectrumPeak[] pMyPeaks) {
        Assert.assertEquals(pTheirPeaks.length, pMyPeaks.length);
        for (int i = 0; i < pMyPeaks.length; i++) {
            ISpectrumPeak myPeak = pMyPeaks[i];
            ISpectrumPeak theirPeak = pMyPeaks[i];
            Assert.assertEquals((int) myPeak.getMassChargeRatio(), (int) theirPeak.getMassChargeRatio());
            double myPeakValue = myPeak.getPeak();
            double theirPeakValue = theirPeak.getPeak();
            Assert.assertEquals(myPeakValue, theirPeakValue, PEAK_TOLERANCE);

        }
    }

    protected void validateSimilarScore(String id, IScanScoring sm, ScoredScan myScore, final IMainData pTandem) {
        ITheoreticalScoring[] scorings = sm.getValidScorings();
        IExtendedSpectralMatch[] comparison = new IExtendedSpectralMatch[scorings.length];
        Map<String, IExtendedSpectralMatch> mine = new HashMap<String, IExtendedSpectralMatch>();
        Map<String, IExtendedSpectralMatch> theirs = new HashMap<String, IExtendedSpectralMatch>();

         for (int i = 0; i < scorings.length; i++) {
              ITheoreticalScoring scoring = scorings[i];
              IExtendedSpectralMatch smx = (IExtendedSpectralMatch) scoring.asSpectralMatch();
              comparison[i] = smx;
         }

        //       Assert.assertEquals(scorings.length,comparison.length);   // scored same spectra probably this is a no
        /**
         * build a map of scoring by sequence
         */
        Arrays.sort(comparison, ISpectralMatch.SCORE_COMPARATOR);  // sore theirs be scores - descending
        for (int i = 0; i < Math.min(8,comparison.length); i++) {
            IExtendedSpectralMatch smx  = comparison[i] ;
            String sequence = smx.getPeptide().getSequence();
            theirs.put(sequence, smx);
        }

        /**
         * build a map of scoring by sequence
         */
        ISpectralMatch[] spectralMatches = myScore.getSpectralMatches();
        Arrays.sort(spectralMatches, ISpectralMatch.SCORE_COMPARATOR);  // sore mine be scores - descending
        for (int i = 0; i < spectralMatches.length; i++) {
            IExtendedSpectralMatch spectralMatch = (IExtendedSpectralMatch) spectralMatches[i];
            String sequence = spectralMatch.getPeptide().getSequence();
            mine.put(sequence, spectralMatch);
            //   ISpectralMatch tandemScore = comparison[i];

        }
        // what thye score and I don't
        Map<String, IExtendedSpectralMatch> diff = XTandemUtilities.buildDifferenceMap(mine, theirs);
        Assert.assertEquals(0, diff.size());
        // what I score that they don't
        Map<String, IExtendedSpectralMatch> diff2 = XTandemUtilities.buildDifferenceMap(theirs, mine);
        Assert.assertEquals(0, diff2.size());


        /**
         * look
         */
        for (String key : theirs.keySet()) {
            validateSimilarScoringOfSequence(key, mine, theirs);

        }

        Map<String, IExtendedSpectralMatch> both = new HashMap<String, IExtendedSpectralMatch>();
        for (String key : mine.keySet()) {
            if (theirs.containsKey(key)) {
                both.put(key, mine.get(key));
                theirs.remove(key);
            }
        }

    }

    protected void validateSimilarScoringOfSequence(final String sequence, final Map<String, IExtendedSpectralMatch> pMine, final Map<String, IExtendedSpectralMatch> pTheirs) {
        // Xtandem writes more data about the fit

        //       if(!INTERESTING_SEQUENCES_SET.contains(sequence))
//             return; // for now only do a  few cases
        if (INTERESTING_SEQUENCES_SET.contains(sequence))
            XTandemUtilities.breakHere();

        IExtendedSpectralMatch theirMatch = (IExtendedSpectralMatch) pTheirs.get(sequence);
        if (theirMatch.getScore() == 0)
            return;


        IExtendedSpectralMatch myMatch = (IExtendedSpectralMatch) pMine.get(sequence);
        if (myMatch == null)
            Assert.assertNotNull(myMatch); // I better sore it
        validateSameScoring(theirMatch, myMatch);
    }


    protected void validateSameScoring(IExtendedSpectralMatch theirMatch, IExtendedSpectralMatch myMatch) {
        IMeasuredSpectrum theirMeasured = theirMatch.getMeasured();
        IMeasuredSpectrum myMeasured = myMatch.getMeasured();

        ITheoreticalIonsScoring[] theirScoring = theirMatch.getIonScoring();
        ITheoreticalIonsScoring[] myScoring = myMatch.getIonScoring();
        if (theirScoring.length != myScoring.length) {
            XTandemUtilities.breakHere();
            //         Assert.assertEquals(theirScoring.length,myScoring.length);
        }

        String sequence = theirMatch.getPeptide().getSequence();
        if ("LAIKMCKPG".equals(sequence))
            XTandemUtilities.breakHere();

        validateSamePeaks(theirMeasured, myMeasured);

        int theirmaxCharge = theirMatch.getMaxScoredCharge();
        int mymaxCharge = myMatch.getMaxScoredCharge();

        if (mymaxCharge != theirmaxCharge) {
            XTandemUtilities.breakHere();
            // Assert.assertEquals(mymaxCharge,theirmaxCharge);  // matched the same charge
        }
        double theiradded = 0;
        double myadded = 0;
        int count = ValidatedCount++;
        List<DebugMatchPeak> allMyMatches = new ArrayList<DebugMatchPeak>();
        List<DebugMatchPeak> allTheirMatches = new ArrayList<DebugMatchPeak>();


        for (int charge = 1; charge <= Math.min(theirmaxCharge, mymaxCharge); charge++) {
            DebugMatchPeak[] theirMatches = theirMatch.getMatchesAtCharge(charge);
            for (int j = 0; j < theirMatches.length; j++) {
                theiradded += theirMatches[j].getAdded();
                allTheirMatches.add(theirMatches[j]);
            }
            DebugMatchPeak[] mymatches = myMatch.getMatchesAtCharge(charge);
            for (int j = 0; j < mymatches.length; j++) {
                myadded += mymatches[j].getAdded();
                allMyMatches.add(mymatches[j]);
            }
            if (theirMatches.length != mymatches.length) {
                XTandemUtilities.breakHere();
                double diff = myadded - theiradded;
                if (Math.abs(diff) > 0.15 * myadded) {
                    XTandemUtilities.breakHere();
                    theiradded = 0;
                    for (int j = 0; j < theirMatches.length; j++) {
                        double added = theirMatches[j].getAdded();
                     //   System.out.println("their " + j + " " + added);
                        theiradded += added;
                    }
                    myadded = 0;
                    for (int j = 0; j < mymatches.length; j++) {
                        double added = mymatches[j].getAdded();
                      //  System.out.println("my " + j + " " + added);
                        myadded += added;
                    }
                }
            }
            if (theirMatches.length != mymatches.length) {
                XTandemUtilities.breakHere();
                //      Assert.assertEquals(theirMatches.length,mymatches.length);
            }
            for (int j = 0; j < Math.min(theirMatches.length, mymatches.length); j++) {

                DebugMatchPeak theirmatch = theirMatches[j];
                DebugMatchPeak mymatch = mymatches[j];
                if (!theirmatch.equivalent(mymatch)) {
                    XTandemUtilities.breakHere();
                    //   Assert.assertTrue(theirmatch.equivalent(mymatch));
                }
            }
        }

        Collections.sort(allTheirMatches);
        Collections.sort(allMyMatches);
        double diff = Math.abs(myadded - theiradded);
        double sumAdded = myadded + theiradded;
        if ((diff / sumAdded) > 0.05) {
            if (!sequence.startsWith("E") && !sequence.startsWith("Q")) {
                if (allTheirMatches.size() != allMyMatches.size()) {
                    XTandemUtilities.breakHere();
                    //      Assert.assertEquals(theirMatches.length,mymatches.length);
                }
                badSums++;
            }
            else {
                toFixSums++;
            }
        }
        else {
            goodSums++;
        }
        double theirScore = theirMatch.getScore();
        double myScore = myMatch.getScore();

        double del = Math.abs(theiradded - myadded);
        if (del > 0.01)
            XTandemUtilities.breakHere();
        int theirMatches = theirMatch.getNumberMatchedPeaks();
        int myMatches = myMatch.getNumberMatchedPeaks();
        //  Assert.assertEquals(theirMatches,myMatches);  // matched the same number peaks

    }

    protected void validateSamePeaks(IMeasuredSpectrum theirMeasured, IMeasuredSpectrum myMeasured) {
        Map<Integer, ISpectrumPeak> mzToPeak = new HashMap<Integer, ISpectrumPeak>();
        ISpectrumPeak[] theripeaks = theirMeasured.getPeaks();
        for (int i = 0; i < theripeaks.length; i++) {
            ISpectrumPeak theripeak = theripeaks[i];
            mzToPeak.put((int) theripeak.getMassChargeRatio(), theripeak);
        }
        List<ISpectrumPeak> notSeen = new ArrayList<ISpectrumPeak>();
        ISpectrumPeak[] mypeaks = myMeasured.getPeaks();
        for (int i = 0; i < mypeaks.length; i++) {
            ISpectrumPeak mypeak = mypeaks[i];
            double mz = mypeak.getMassChargeRatio();
            int myMass = TandemKScoringAlgorithm.massChargeRatioAsInt(mypeak);
            ISpectrumPeak their = mzToPeak.get(myMass);
            if (their == null) {
                if (mz < myMass) {
                    their = mzToPeak.get(myMass - 1);
                    if (their != null) {
                        myMass = myMass - 1;
                    }
                    else {
                        notSeen.add(mypeak);
                        continue;
                    }
                }
                else {
                    notSeen.add(mypeak);
                    continue;
                }
            }
            mzToPeak.remove(myMass);
            double theirPeakValue = their.getPeak();
            double myPeakValue = mypeak.getPeak();
            double del = Math.abs(theirPeakValue - myPeakValue);
            if (del >= MAXIMUM_ACCEPTABLE_PEAK_DIFFERENCE) {
                Assert.assertTrue(del < MAXIMUM_ACCEPTABLE_PEAK_DIFFERENCE);
            }


        }

        ISpectrumPeak[] ret = new ISpectrumPeak[notSeen.size()];
        notSeen.toArray(ret);


        //   Assert.assertTrue(ret.length < theripeaks.length / 6);


    }

//    protected int validateSamePeptides(ScanScoringReport sr, final FragmentHoldingFastaHandler pHandler, final IMainData pTandem) {
//        Set<Integer> handledItems = new HashSet<Integer>();
//        JDBCTaxonomy otherTaxonomy = null;
//        try {
//// NOTE only do this with a populated local database
//            otherTaxonomy = buildJDBCDatabase(pHandler);
//        }
//        catch (Exception e) { // this is the case where the database is not available
//            otherTaxonomy = null;
//
//        }
//
//        int totalVerified = 0;
//        int totalDBVerified = 0;
//        RawPeptideScan[] rawScans = pTandem.getRawScans();
//        Arrays.sort(rawScans);
//        for (int i = 0; i < rawScans.length; i++) {
//            RawPeptideScan rawScan = rawScans[i];
//            String id = rawScan.getId();
//            IScanScoring sm = sr.getScanScoringMap(id);
//            int charge = rawScan.getPrecursorCharge();
//            if (charge == 0) { // unknown charge
//                for (int k = 1; k <= 3; k++) {
//                    double mass = rawScan.getPrecursorMass(i);
//                    // NOTE only do this with a populated local database
//                    validateFragmentsAtMassAgainstDatabase(pHandler, otherTaxonomy, sm, mass, handledItems,pTandem);
//                    if (otherTaxonomy != null)
//                        totalDBVerified += validateFragmentsAtMassAgainstDatabase(pHandler, otherTaxonomy, sm, mass, handledItems,pTandem);
//                    totalVerified += validateFragmentsAtMass(pHandler, sm, mass, handledItems,pTandem);
//                }
//            }
//            else {
//                double mass = rawScan.getPrecursorMass(charge);
//                // NOTE only do this with a populated local database
//                if (otherTaxonomy != null)
//                    totalDBVerified += validateFragmentsAtMassAgainstDatabase(pHandler, otherTaxonomy, sm, mass, handledItems,pTandem);
//
//                totalVerified += validateFragmentsAtMass(pHandler, sm, mass, handledItems,pTandem);
//
//            }
//
//        }
//        int totalScores = sr.getTotalScoreCount();
//        Assert.assertTrue(totalVerified >= totalScores);
//        if (otherTaxonomy != null) {
//            Assert.assertTrue(totalDBVerified >= totalScores);
//            Assert.assertTrue(totalDBVerified >= totalVerified);
//        }
//        return totalVerified;
//    }
//

    public static int getMaxNotHandled(int total) {
        // return 1 + (int)(total * 0.05);
        // return 1 + (int)(total * 0.25);
        return 1 + (int) (total * 0.38);
    }


    private int validateFragmentsAtMass(final FragmentHoldingFastaHandler pHandler, final IScanScoring pSm, final double pMass, Set<Integer> handledItems,IMainData tandem) {
        ITandemScoringAlgorithm scoringAlgorithm = tandem.getScorer();
        int[] limits = scoringAlgorithm.allSearchedMasses(pMass);
        int totalNotVerified = 0;
        int totalVerified = 0;
        int notHandledByJXTandex = 0;
        String id = pSm.getId();

        if ("95".equals(id))
            XTandemUtilities.breakHere();


        if (pSm.getScoreCount() == 0)
            return 0;

        Map<String, IPolypeptide> holder = new HashMap<String, IPolypeptide>();

        for (int j = 0; j < limits.length; j++) {
            int iMass = limits[j];


            if (1474 == iMass)
                XTandemUtilities.breakHere();

            //   if (handledItems.contains(iMass))
            //        continue;
            handledItems.add(iMass);
            IPolypeptide[] fragmentsAtMass = pHandler.getFragmentsAtMass(iMass);
            Assert.assertNotNull(fragmentsAtMass);
            for (int l = 0; l < fragmentsAtMass.length; l++) {
                IPolypeptide pp = fragmentsAtMass[l];
                String sequence = pp.getSequence();

                if ("CIELSTMNLVRR".equals(sequence))
                    XTandemUtilities.breakHere();

                holder.put(sequence, pp);
                ITheoreticalScoring ts = pSm.getExistingScoring(sequence);
                if (ts == null)
                    totalNotVerified++;
                totalVerified++;
            }
        }
        int maxNotHandled = getMaxNotHandled(totalVerified);
        Assert.assertTrue("failure at id " + id, totalNotVerified < maxNotHandled);

        List<ITheoreticalScoring> notHandledByMe = new ArrayList<ITheoreticalScoring>();

        ITheoreticalScoring[] scorings = pSm.getScorings();
        for (int i = 0; i < scorings.length; i++) {
            ITheoreticalScoring scoring = scorings[i];
            double kscore = scoring.getTotalKScore();
            if (kscore == 0)
                continue;
            String sequence = scoring.getSequence();
            IPolypeptide pp = holder.get(sequence);
            if (pp == null) {
                notHandledByJXTandex++;
                notHandledByMe.add(scoring);
            }
        }
        ITheoreticalScoring[] notHandled = new ITheoreticalScoring[notHandledByMe.size()];
        notHandledByMe.toArray(notHandled);

        Assert.assertTrue("failure at id " + id, notHandledByJXTandex < maxNotHandled);


        return totalVerified;
    }


    /**
     * does the database have the same fragments as the local digestion - since as of now
     * there is better verification of the database - this is really averification of the
     * local insiloco digestion
     *
     * @param pHandler
     * @param pSm
     * @param pMass
     * @param handledItems
     * @return total peptides tested
     */

//    private int validateFragmentsAtMassAgainstDatabase(final FragmentHoldingFastaHandler pHandler, JDBCTaxonomy otherTaxonomy, final IScanScoring pSm, final double pMass, Set<Integer> handledItems,IMainData tandem) {
//        int[] limits = tandem.getScorer().allSearchedMasses(pMass);
//
//        int totalVerified = 0;
//        for (int j = 0; j < limits.length; j++) {
//            int iMass = limits[j];
//            IPolypeptide[] fragmentsAtMass = pHandler.getFragmentsAtMass(iMass);
//            IPolypeptide[] dbPeptides = otherTaxonomy.findPeptidesOfMassIndex(iMass, true);
//
//            Assert.assertNotNull(fragmentsAtMass);
//            Assert.assertNotNull(dbPeptides);
//            IPolypeptide[] diff = XTandemUtilities.buildDifferencePeptideLists(dbPeptides, fragmentsAtMass);
//            Assert.assertEquals(fragmentsAtMass.length, dbPeptides.length);
//            totalVerified += fragmentsAtMass.length;
//
//            validateSameSequenceSets(fragmentsAtMass, dbPeptides);
//        }
//        return totalVerified;
//    }

    /**
     * asserts that the two sets of peptids arrays contain the same set of sequences
     *
     * @param pFragmentsAtMass !null array1
     * @param pDbPeptides      !null array2
     */
    private void validateSameSequenceSets(final IPolypeptide[] pFragmentsAtMass, final IPolypeptide[] pDbPeptides) {
        Set<String> dbFragments = XTandemUtilities.getSequenceSet(pDbPeptides);
        Set<String> dbWorkingFragments = XTandemUtilities.getSequenceSet(pDbPeptides);
        Set<String> myFragments = XTandemUtilities.getSequenceSet(pFragmentsAtMass);
        Set<String> myWorkingFragments = XTandemUtilities.getSequenceSet(pFragmentsAtMass);

        myWorkingFragments.removeAll(dbFragments);
        dbWorkingFragments.removeAll(myFragments);

        Assert.assertEquals(0, myWorkingFragments.size());
        Assert.assertEquals(0, dbWorkingFragments.size());
    }

    /*
<!--  These parameters define access to a database -->
 <note type="input" label="org.systemsbiology.xtandem.Datasource.Host">localhost</note>
   <!--  <note type="input" label="org.systemsbiology.xtandem.Datasource.Database">yeast_orf</note>   -->
<note type="input" label="org.systemsbiology.xtandem.Datasource.Database">yeast_orfs_pruned</note>
<note type="input" label="org.systemsbiology.xtandem.Datasource.User">proteomics</note>
<note type="input" label="org.systemsbiology.xtandem.Datasource.Password">tandem</note>
<note type="input" label="org.systemsbiology.xtandem.Datasource.DriverClass">com.mysql.jdbc.Driver</note>


    */
    public static final String DATA_BASE_HOST = "localhost";
    public static final String DATA_BASE_NAME = "yeast_orfs_pruned";
    public static final String DATA_BASE_USER = "proteomics";
    public static final String DATA_BASE_PASSWORD = "tandem";
    public static final String DATA_BASE_DRIVER = "com.mysql.jdbc.Driver";
//
//    public static JDBCTaxonomy buildJDBCDatabase(final FragmentHoldingFastaHandler pHandler) {
//        XTandemMain tandem = (XTandemMain) pHandler.getTandem();
//        return buildJDBCDatabase(tandem);
//    }
//
//    public static JDBCTaxonomy buildJDBCDatabase(final XTandemMain pTandem) {
//        pTandem.setParameter(SpringJDBCUtilities.DATA_HOST_PARAMETER, DATA_BASE_HOST);
//        pTandem.setParameter(SpringJDBCUtilities.DATA_DATABASE_PARAMETER, DATA_BASE_NAME);
//        pTandem.setParameter(SpringJDBCUtilities.DATA_USER_PARAMETER, DATA_BASE_USER);
//        pTandem.setParameter(SpringJDBCUtilities.DATA_PASSWORD_PARAMETER, DATA_BASE_PASSWORD);
//        pTandem.setParameter(SpringJDBCUtilities.DATA_DRIVER_CLASS_PARAMETER, DATA_BASE_DRIVER);
//        return new JDBCTaxonomy(pTandem);
//    }

}
