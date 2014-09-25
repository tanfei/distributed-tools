package org.systemsbiology.xtandem.morpheus;

import org.systemsbiology.hadoop.*;
import org.systemsbiology.xtandem.*;
import org.systemsbiology.xtandem.ionization.*;
import org.systemsbiology.xtandem.scoring.*;
import org.systemsbiology.xtandem.testing.*;

import java.util.*;

/**
 * org.systemsbiology.xtandem.morpheus.MorpheusScoringAlgorithm
 * User: Steve
 * Date: 1/12/12
 */
public class MorpheusScoringAlgorithm extends AbstractScoringAlgorithm {

    public static final MorpheusScoringAlgorithm[] EMPTY_ARRAY = {};
    public static final int NUMBER_ION_TYPES = 2; // B and I

    public static final double PROTON_MASS = MassCalculator.getDefaultCalculator().calcMass("H");


    public static final double LOG_PI = Math.log(Math.sqrt(2 * Math.PI));
    public static final double NORMALIZATION_MAX = 200.0;
    public static final double DEFAULT_PEPTIDE_ERROR = 1.0;
    public static final double DEFAULT_MASS_TOLERANCE = 0.025;
    public static final int PEAK_BIN_SIZE = 100;
    public static final int PEAK_BIN_NUMBER = 3;
    public static final int PEAK_NORMALIZATION_BINS = 5;
    public static final int MINIMUM_SCORED_IONS = 10;

    public static final String ALGORITHM_NAME = "Morpheus";


    private double m_PeptideError = DEFAULT_PEPTIDE_ERROR;
    private double m_MassTolerance = DEFAULT_MASS_TOLERANCE;

    public MorpheusScoringAlgorithm() {

    }

    @Override
    public String toString() {
        return getName();
    }

    protected double computeProbability(double intensity, double massDifference) {
        double theta = getThetaFactor();
        double denom = 2 * theta * theta;
        double addedDiff = (massDifference * massDifference) / denom;
        double logIntensity = Math.log(intensity);
        double logPi = LOG_PI;
        double logTheta = Math.log(theta);
        double consts = logPi - logTheta;
        double prob = logIntensity - consts - addedDiff;
        return prob;
    }

    protected double getThetaFactor() {
        return getPeptideError() * 0.5;
    }

    public double getPeptideError() {
        return m_PeptideError;
    }

    public void setPeptideError(final double pPeptideError) {
        m_PeptideError = pPeptideError;
    }

//    @Override
//    public float getSpectrumMassError() {
//        return 0;
//    }
//
//    @Override
//    public float getSpectrumMonoIsotopicMassError() {
//        return 0;
//    }
//
//    @Override
//    public float getSpectrumMonoIsotopicMassErrorMinus() {
//        return 0;
//    }
//
//    @Override
//    public float getSpectrumHomologyError() {
//        return 0;
//    }

    /**
     * use the parameters to configure local properties
     *
     * @param !null params
     */
    @Override
    public void configure(final IParameterHolder params) {
        super.configure(params);

    }

    /**
     * return the product of the factorials of the counts
     *
     * @param counter - !null holding counts
     * @return as above
     */
    @Override
    public double getCountFactor(final IonUseScore counter) {
        return 1;
    }

    /**
     * return the low and high limits of a mass scan
     *
     * @param scanMass
     * @return as above
     */
    @Override    // todo fix this
    public int[] highAndLowMassLimits(double scanMass) {
        IMZToInteger defaultConverter = XTandemUtilities.getDefaultConverter();
        int[] ret = new int[2];
        // algorithm makes adjustments in the mass - this does the right thing
        // note this only looks backwards SLewis
        ret[0] = defaultConverter.asInteger(scanMass - getPlusLimit());
        ret[1] = defaultConverter.asInteger(scanMass + -getMinusLimit());

        return ret; // break here interesting result
    }


    /**
     * score the two spectra
     *
     * @param measured !null measured spectrum
     * @param theory   !null theoretical spectrum
     * @return value of the score
     */
    @Override
    public double scoreSpectrum(final IMeasuredSpectrum measured, final ITheoreticalSpectrum theory, Object... otherdata) {

        IonUseCounter counter = new IonUseCounter();
        List<DebugMatchPeak> holder = new ArrayList<DebugMatchPeak>();
        double dot = dot_product(measured, theory, counter, holder);
        return dot ;
    }

//
//    public double getImmScore(String pepSeq, ISpectrum spec) {
//
//        double returnScore = 0;
//
//        ISpectrumPeak[] peaks = spec.getPeaks();
//        for (int i = 0; i < peaks.length; i++) {
//            ISpectrumPeak peak = peaks[i];
//            for (int j = 0; j < pepSeq.length(); j++) {
//                char c = Character.toUpperCase(pepSeq.charAt(j));
//                switch (c) {
//                    case 'W':
//                        returnScore += Math.log(peak.getPeak());
//                    default:
//                        break;
//                }
//
//            }
//            returnScore += Math.log(peak.getPeak());
//        }
//
//        return returnScore;
//    }
//

    /**
     * return a unique algorithm name
     *
     * @return
     */
    @Override
    public String getName() {
        return ALGORITHM_NAME;
    }

    /**
     * are we scoring mono average
     *
     * @return as above
     */
    @Override
    public MassType getMassType() {
        return null;
    }


    public double getMassTolerance() {
        return m_MassTolerance;
    }

    public void setMassTolerance(double massTolerance) {
        m_MassTolerance = massTolerance;
    }

    /**
     * alter the score from dot_product in algorithm depdndent manner
     *
     * @param score    old score
     * @param measured !null measured spectrum
     * @param theory   !null theoretical spectrum
     * @param counter  !null use counter
     * @return new score
     */
    @Override
    public double conditionScore(final double score, final IMeasuredSpectrum measured, final ITheoreticalSpectrumSet theory, final IonUseScore counter) {
        return score;
    }

    /**
     * find the hyperscore the score from dot_product in algorithm depdndent manner
     *
     * @param score    old score
     * @param measured !null measured spectrum
     * @param theory   !null theoretical spectrum
     * @param counter  !null use counter
     * @return hyperscore
     */
    @Override
    public double buildHyperscoreScore(final double score, final IMeasuredSpectrum measured, final ITheoreticalSpectrumSet theory, final IonUseScore counter) {
        return score;
    }

    /**
     * Cheat by rounding mass to the nearest int and limiting to MAX_MASS
     * then just generate arrays of the masses and multiply them all together
     *
     * @param measured  !null measured spectrum
     * @param theory    !null theoretical spectrum
     * @param counter   !null use counter
     * @param holder    !null holder tro receive matched peaks
     * @param otherData anythiing else needed
     * @return comupted dot product
     */
    @Override
    public double dot_product(final IMeasuredSpectrum measured, final ITheoreticalSpectrum theory, final IonUseCounter counter, final List<DebugMatchPeak> holder, final Object... otherData) {

        int[] items = new int[1];
        double peptideError = getPeptideError();

        ISpectrumPeak[] peaks = measured.getPeaks();

        double TotalIntensity = 0.0;
        for (int i = 0; i < peaks.length; i++) {
            ISpectrumPeak peak = peaks[i];
            TotalIntensity += peak.getPeak();
        }


        final ITheoreticalPeak[] tps = theory.getTheoreticalPeaks();
        if (tps.length == 0)
            return 0;
        int t = 0;
        int e = 0;
        int MatchingProducts = 0;
        int TotalProducts = 0;
        double MatchingIntensity = 0.0;
        int charge = theory.getCharge();
        while (t < tps.length && e < peaks.length) {
            TotalProducts++;
            double massChargeRatio = tps[t].getMassChargeRatio() - PROTON_MASS * charge;
            double mass_difference = peaks[e].getMassChargeRatio() - massChargeRatio;
            if (Math.abs(mass_difference) <= m_MassTolerance) {
                MatchingProducts++;
                MatchingIntensity += peaks[e].getPeak();
                t++;
            }
            else if (mass_difference < 0) {
                e++;
            }
            else if (mass_difference > 0) {
                t++;
            }
        }
        double MatchingProductsFraction = (double) MatchingProducts / TotalProducts;
//
//           int e2 = 0;
//        int t2 = 0;
//        while (e2 < peaks.length && t2 < tps.length) {
//            double mass_difference = peaks[e2].getMassChargeRatio() - tps[t2].getMassChargeRatio();
//            if (Math.abs(mass_difference) <= m_MassTolerance) {
//                MatchingIntensity += peaks[e2].getPeak();
//                e2++;
//            }
//            else if (mass_difference < 0) {
//                e2++;
//            }
//            else if (mass_difference > 0) {
//                t2++;
//            }
//        }
        double MatchingIntensityFraction = MatchingIntensity / TotalIntensity;

        double MorpheusScore = MatchingProducts + MatchingIntensityFraction;

        return (MorpheusScore);
    }


    public static int peakSearch(double lowMass, double highMass, int[] indexArray, ISpectrumPeak[] peaks) {
        if (true)
            throw new UnsupportedOperationException("Fix This"); // ToDo

        int index = indexArray[0];
        while (index < peaks.length) {
            ISpectrumPeak test = peaks[index++];

            double mass = test.getMassChargeRatio();
            if (mass < lowMass)
                continue;
            indexArray[0] = index - 1;
            if (mass > highMass) {
                return -1; // not found
            }
            else {
                return index - 1;
            }
        }
        indexArray[0] = index;
        return -1;
    }


    private double scoreAddPeakScore(final IonUseCounter counter, final List<DebugMatchPeak> holder, final int pImass, final IonType pType, final double pAdded, final int pDel) {
        if (true)
            throw new UnsupportedOperationException("Fix This"); // ToDo
        if (pAdded == 0)
            return 0;

        DebugMatchPeak match = new DebugMatchPeak(pDel, pAdded, pImass - pDel, pType);


        holder.add(match);

        counter.addScore(pType, pAdded);
        if (pDel == 0)  // only count direct hits
            counter.addCount(pType);
        return pAdded;
    }


    /**
     * test for acceptability and generate a new conditioned spectrum for
     * scoring
     * See Spectrum.truncatePeakList()
     *
     * @param in !null spectrum
     * @return null if the spectrum is to be ignored otherwise a conditioned spectrum
     */
    @Override
    public IMeasuredSpectrum conditionSpectrum(final IScoredScan pScan, final SpectrumCondition sc) {
        OriginatingScoredScan scan = (OriginatingScoredScan) pScan;
        IMeasuredSpectrum in = new MutableMeasuredSpectrum(pScan.getRaw());
 //     IMeasuredSpectrum iMeasuredSpectrum = truncatePeakList(in);
        in = normalize(in.asMmutable());
        return in;
    }

//    protected IMeasuredSpectrum truncatePeakList(final MutableMeasuredSpectrum in) {
//        ISpectrumPeak[] peaks = in.getPeaks();
//        if (peaks.length < PEAK_BIN_NUMBER * PEAK_BIN_SIZE)
//            return in; // nothing to do
//
//        double minMass = (peaks[0].getMassChargeRatio());
//        double maxMass = ((peaks[peaks.length - 1].getMassChargeRatio()));
//        int step = (int) ((maxMass - minMass) / PEAK_BIN_NUMBER);
//
//        MutableMeasuredSpectrum out = new MutableMeasuredSpectrum(in);
//        in.setPeaks(ISpectrumPeak.EMPTY_ARRAY);
//        addHighestPeaks(out, peaks, minMass, minMass + step);
//        addHighestPeaks(out, peaks, minMass + step, minMass + 2 * step);
//        addHighestPeaks(out, peaks, minMass + 2 * step, maxMass);
//        return out;
//    }

    public static double getMassDifference(ISpectrum spec) {
        ISpectrumPeak[] peaks = spec.getPeaks();
        double lowMZ = peaks[0].getMassChargeRatio();
        double highMZ = peaks[peaks.length - 1].getMassChargeRatio();

        //System.out.println("lowMZ "+lowMZ+" highMZ "+highMZ);
        double diffMZ = 0;

        if (lowMZ == highMZ)
            diffMZ = 1f;
        else
            diffMZ = highMZ - lowMZ;


        return diffMZ;
    }


    public static double getMissScore(int numMissIons, ISpectrum spec) {
        double diffMZ = getMassDifference(spec);
        return numMissIons * (-Math.log(diffMZ));
    }

    /**
     * is this correct or should all spectra do this????? todo
     * @param in
     * @return
     */
    protected IMeasuredSpectrum normalize(final MutableMeasuredSpectrum in) {
        double proton = MassCalculator.getDefaultCalculator().calcMass("H");
        ISpectrumPeak[] peaks = in.getPeaks();
        int charge = in.getPrecursorCharge();
        ISpectrumPeak[] newpeaks = new ISpectrumPeak[peaks.length];
        for (int i = 0; i < peaks.length; i++) {
            ISpectrumPeak peak = peaks[i];
            double massChargeRatio = peak.getMassChargeRatio() + charge * proton;
            newpeaks[i] = new SpectrumPeak(massChargeRatio, peak.getPeak());

        }
        MutableMeasuredSpectrum out =  new MutableMeasuredSpectrum(in);
        out.setPeaks(newpeaks);
        return out; // I do not see normalization
    }


    public static MutableSpectrumPeak[] asMutable(final ISpectrumPeak[] peaks) {
        MutableSpectrumPeak[] ret = new MutableSpectrumPeak[peaks.length];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = new MutableSpectrumPeak(peaks[i]);

        }
        return ret;
    }

    public static void normalizePeaks(MutableSpectrumPeak[] myPeaks) {
        normalizePeakRange(myPeaks, 0, myPeaks.length);
    }

    public static void normalizePeakRange(MutableSpectrumPeak[] myPeaks, int start, int end) {
        double maxValue = Double.MIN_VALUE;
        for (int i = start; i < Math.min(end, myPeaks.length); i++) {
            ISpectrumPeak peak = myPeaks[i];
            maxValue = Math.max(maxValue, peak.getPeak());
        }
        double factor = NORMALIZATION_MAX / maxValue;
        for (int i = start; i < Math.min(end, myPeaks.length); i++) {
            MutableSpectrumPeak myPeak = myPeaks[i];
            myPeak.setPeak((float) (factor * myPeak.getPeak()));

        }
    }


    public static double getMaximumIntensity(ISpectrumPeak[] peaks) {
        double ret = Double.MIN_VALUE;
        for (int i = 0; i < peaks.length; i++) {
            ISpectrumPeak peak = peaks[i];
            ret = Math.max(ret, peak.getPeak());
        }
        return ret;
    }

    protected void addHighestPeaks(final MutableMeasuredSpectrum pOut, final ISpectrumPeak[] pPeaks, final double pStart, final double pEnd) {
        List<ISpectrumPeak> holder = new ArrayList<ISpectrumPeak>();
        for (int i = 0; i < pPeaks.length; i++) {
            ISpectrumPeak peak = pPeaks[i];
            double mass = peak.getMassChargeRatio();
            if (Math.abs(mass - 850) < 2)
                XTandemUtilities.breakHere();

            if (mass >= pStart && mass < pEnd)
                holder.add(peak);
        }
        ISpectrumPeak[] used = new ISpectrumPeak[holder.size()];
        holder.toArray(used);
        // sort by size
        Arrays.sort(used, ScoringUtilities.PeakIntensityComparatorHiToLowINSTANCE);
        // add the top PEAK_BIN_SIZE peaks in this bin
        for (int i = 0; i < Math.min(PEAK_BIN_SIZE, used.length); i++) {
            ISpectrumPeak added = used[i];
            pOut.addPeak(added);

        }

    }


    /**
     * return the expected value for the best score
     *
     * @param scan !null scan
     * @return as above
     */
    @Override
    public double getExpectedValue(final IScoredScan scan) {
        return 0;
    }

    /**
     * modify the theoretical spectrum before scoring - this may
     * involve reweighting or dropping peaks
     *
     * @param pTs !null spectrum
     * @return !null modified spectrum
     */
    @Override
    public ITheoreticalSpectrum buildScoredScan(final ITheoreticalSpectrum pTs) {
        return pTs;
    }
}
