package org.systemsbiology.xtandem.probidx;

import org.systemsbiology.hadoop.*;
import org.systemsbiology.xtandem.*;
import org.systemsbiology.xtandem.ionization.*;
import org.systemsbiology.xtandem.scoring.*;
import org.systemsbiology.xtandem.testing.*;

import java.util.*;

/**
 * org.systemsbiology.xtandem.probidx.ProbIdScoringAlgorithm
 * User: Steve
 * Date: 1/12/12
 */
public class ProbIdScoringAlgorithm extends AbstractScoringAlgorithm  {

    public static final ProbIdScoringAlgorithm[] EMPTY_ARRAY = {};
    public static final int NUMBER_ION_TYPES = 2; // B and I

    public static final double LOG_PI = Math.log(Math.sqrt(2 * Math.PI));
    public static final double NORMALIZATION_MAX = 200.0;
    public static final double DEFAULT_PEPTIDE_ERROR = 1.0;
    public static final int PEAK_BIN_SIZE = 100;
    public static final int PEAK_BIN_NUMBER = 3;
    public static final int PEAK_NORMALIZATION_BINS = 5;
    public static final int MINIMUM_SCORED_IONS = 10;

    public static final String ALGORITHM_NAME = "ProbId";


    private double m_PeptideError = DEFAULT_PEPTIDE_ERROR;

    public ProbIdScoringAlgorithm() {

    }

    @Override
     public String toString() {
         return  getName();
     }

    protected double computeProbability(double intensity, double massDifference) {
        double theta = getThetaFactor();
        double denom = 2 * theta * theta;
        double addedDiff = (massDifference  *  massDifference) / denom;
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
    public   int[] highAndLowMassLimits(double scanMass) {
        IMZToInteger defaultConverter = XTandemUtilities.getDefaultConverter();
         int[] ret = new int[2];
        // algorithm makes adjustments in the mass - this does the right thing
        // note this only looks backwards SLewis
        ret[0] =  defaultConverter.asInteger(scanMass - getPlusLimit());
        ret[1] =  defaultConverter.asInteger( scanMass + - getMinusLimit());

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

        String sequence = theory.getPeptide().getSequence();
        int numTotalIons = sequence.length() * NUMBER_ION_TYPES;
        int numMatchedIons = counter.getCount(IonType.B) +  counter.getCount(IonType.Y);
        double logMissScore = getMissScore(numTotalIons - numMatchedIons,measured);
        // seems to be 0;
        double logImmScore = getImmScore(sequence,measured);

        if(otherdata.length > 0)  {
            double[] scores = (double[])otherdata[0];
            scores[0] = dot;
            scores[1] = logMissScore;
            scores[2] = numTotalIons;
            scores[3] = numMatchedIons;
             }
        double factor = getCountFactor(counter);

        return dot  + logImmScore + logMissScore;
    }


    public double getImmScore(String pepSeq,ISpectrum spec) {

        double returnScore = 0;

        ISpectrumPeak[] peaks = spec.getPeaks();
        for (int i = 0; i < peaks.length; i++) {
            ISpectrumPeak peak = peaks[i];
            for (int j = 0; j < pepSeq.length(); j++) {
                char c = Character.toUpperCase(pepSeq.charAt(j));
                switch(c)  {
                    case  'W' :
                       returnScore += Math.log(peak.getPeak());
                    default:
                        break;
                }

            }
            returnScore += Math.log(peak.getPeak());
        }

        return returnScore;
    }


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

        final ITheoreticalPeak[] tps = theory.getTheoreticalPeaks();
        if (tps.length == 0)
            return 0;
        int charge = theory.getCharge();

        // debugging why are we not handling higher charges right
        if (charge > 1)
            XTandemUtilities.breakHere();

        double product = 0;
        for (int i = 0; i < tps.length; i++) {
            ITheoreticalPeak tp = tps[i];
            double mz = tp.getMassChargeRatio();

            if(Math.abs(756 - mz) < 1)
                XTandemUtilities.breakHere();

            double lowMass = mz - peptideError;
            double highMass = mz + peptideError;
            int foundIndex = peakSearch(lowMass, highMass, items, peaks);

            if (foundIndex > -1) {
                if (tp.getType() == IonType.B)
                     counter.addCount(IonType.B);
                if (tp.getType() == IonType.Y)
                     counter.addCount(IonType.Y);
                ISpectrumPeak peak = peaks[foundIndex];
                double peakMZ = peak.getMassChargeRatio();
                double massDifference = mz - peakMZ;
                double prob = computeProbability(peak.getPeak(), massDifference);

                product += prob;
 //               System.out.println("Match " + String.format("%f9.1",mz) + " prob " + prob + " Score " + product);
                counter.addScore(tp.getType(), prob);
              }
            if (items[0] >= peaks.length)
                break;
        }

        int numMatchIons = counter.getCount(IonType.B) + counter.getCount(IonType.Y);
        if (numMatchIons != 0)
            product += numMatchIons * Math.log(numMatchIons);

        return (product);
    }


    public static int peakSearch(double lowMass, double highMass, int[] indexArray, ISpectrumPeak[] peaks) {

        int index = indexArray[0];
        while (index < peaks.length) {
            ISpectrumPeak test = peaks[index++];

            double mass = test.getMassChargeRatio();
            if(mass < lowMass)
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
        MutableMeasuredSpectrum in = new MutableMeasuredSpectrum(pScan.getRaw());
        IMeasuredSpectrum iMeasuredSpectrum = truncatePeakList(in);
        iMeasuredSpectrum = normalize(iMeasuredSpectrum.asMmutable());
        return iMeasuredSpectrum;
    }

    protected IMeasuredSpectrum truncatePeakList(final MutableMeasuredSpectrum in) {
        ISpectrumPeak[] peaks = in.getPeaks();
        if (peaks.length < PEAK_BIN_NUMBER * PEAK_BIN_SIZE)
            return in; // nothing to do

        double minMass =  (peaks[0].getMassChargeRatio());
        double maxMass =   ((peaks[peaks.length - 1].getMassChargeRatio()));
        int step = (int)((maxMass - minMass) / PEAK_BIN_NUMBER);

        MutableMeasuredSpectrum out = new MutableMeasuredSpectrum(in);
        in.setPeaks(ISpectrumPeak.EMPTY_ARRAY);
        addHighestPeaks(out, peaks, minMass, minMass + step);
        addHighestPeaks(out, peaks, minMass + step, minMass + 2 * step);
        addHighestPeaks(out, peaks, minMass + 2 * step , maxMass);
        return out;
    }

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


    protected IMeasuredSpectrum normalize(final MutableMeasuredSpectrum in) {
        ISpectrumPeak[] peaks = in.getPeaks();
        MutableSpectrumPeak[] myPeaks = asMutable(peaks);
        double max = getMaximumIntensity(myPeaks);
        int numberPeaks = peaks.length;
        int space = (int) numberPeaks / PEAK_NORMALIZATION_BINS;
        if (space < 1)
            return in;
        for (int start = 0; start < numberPeaks; start += space) {
            int end = Math.min(start + space, numberPeaks);
            normalizePeakRange(myPeaks, start, end);
        }
        MutableMeasuredSpectrum out = new MutableMeasuredSpectrum(in.getPrecursorCharge(), in.getPrecursorMassChargeRatio(), in.getScanData(), myPeaks);
        return out;
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
            if(Math.abs(mass - 850) < 2)
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
