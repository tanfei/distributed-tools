package org.systemsbiology.xtandem.scoring;

import org.systemsbiology.sax.*;
import org.systemsbiology.xtandem.*;
import org.systemsbiology.xtandem.ionization.*;
import org.systemsbiology.xtandem.sax.*;

import java.util.*;

/**
 * org.systemsbiology.xtandem.scoring.ScoredScan
 * this version of a scan can be modified for scoring
 * Sorts by scan 1d
 *
 * @author Steve Lewis
 * @date Jan 11, 2011
 */
public class ScoredScanOriginal implements IScoredScan {
    public static ScoredScanOriginal[] EMPTY_ARRAY = {};
    public static Class THIS_CLASS = ScoredScanOriginal.class;

    public static final int MAX_SERIALIZED_MATCHED = 16;
    public static final String DEFAULT_ALGORITHM = TandemKScoringAlgorithm.ALGORITHM_NAME;

    public static final String DEFAULT_VERSION = "1.0";

    public static final String TAG = "score";

    private final RawPeptideScan m_Raw;
    private double m_NormalizationFactor = 1;
    private final IonUseScore m_IonUse = new IonUseCounter();
    private IMeasuredSpectrum m_NormalizedRawScan;
    private IMeasuredSpectrum m_ConditionedScan;
    private final List<ISpectralMatch> m_Matches = new ArrayList<ISpectralMatch>();
    private final HyperScoreStatistics m_HyperScores = new HyperScoreStatistics();
    private final VariableStatistics m_ScoreStatistics = new VariableStatistics();
    private ISpectralMatch m_BestMatch;
    private double m_ExpectedValue;
    private ITheoreticalSpectrumSet m_Theory;
    private int m_NumberScoredPeptides;
    private String m_Version = DEFAULT_VERSION;
    private String m_Algorithm = DEFAULT_ALGORITHM;

    public ScoredScanOriginal(RawPeptideScan pRaw) {
        m_Raw = pRaw;
        throw new UnsupportedOperationException("Fix This"); // ToDo   throw away the code
     }

    /**
     * given what is already known build a scan from a serialization string
     *
     * @param serialization
     * @param godObject
     */
    public ScoredScanOriginal(String serialization, IMainData godObject) {
        throw new UnsupportedOperationException("Fix This"); // ToDo     throw away the code
    }

    /**
     * true if some match is scored
     *
     * @return as above
     */
    @Override
    public boolean isMatchPresent() {
        return !m_Matches.isEmpty();
    }

    /**
     * return algorithm name
     *
     * @return as above
     */
    @Override
    public String getAlgorithm() {
        return m_Algorithm;
    }

    public void setAlgorithm(final String pAlgorithm) {
        if(m_Algorithm != null && m_Algorithm != DEFAULT_ALGORITHM)
            throw new IllegalStateException("algorithm cannot be reset from " + m_Algorithm + " to " + pAlgorithm);
        m_Algorithm = pAlgorithm;
    }


    @Override
     public String getVersion() {
         return m_Version;
     }

     public void setVersion(final String pVersion) {
         m_Version = pVersion;
     }



    public int getNumberScoredPeptides() {
        return m_NumberScoredPeptides;
    }

    public void setNumberScoredPeptides(final int pNumberScoredPeptides) {
        m_NumberScoredPeptides = pNumberScoredPeptides;
    }

    public ITheoreticalSpectrumSet getTheory() {
        return m_Theory;
    }

    public void setTheory(final ITheoreticalSpectrumSet pTheory) {
        m_Theory = pTheory;
    }

//    /**
//     * return true if a mass such as that of a throretical peak is
//     * within the range to scpre
//     *
//     * @param mass positive testMass
//     * @return as above
//     */
//    public boolean isMassWithinRange(double mass) {
//        final RawPeptideScan raw = getRaw();
//        return raw.isMassWithinRange(mass);
//    }

    /**
     * combine two scores
     *
     * @param added
     */
    @Override
    public void addTo(IScoredScan added) {
        if (!added.getId().equals(getId()))
            throw new IllegalArgumentException("incompatable scan");
        ISpectralMatch newMatch = added.getBestMatch();
        ISpectralMatch myBest = getBestMatch();
        if (myBest == null) {
            setBestMatch(newMatch);
            setExpectedValue(added.getExpectedValue());
        }
        else {
            if (newMatch != null && newMatch.getHyperScore() > myBest.getHyperScore())
                setBestMatch(newMatch);
            setExpectedValue(added.getExpectedValue());
        }

        getHyperScores().add(added.getHyperScores());
        getScoreStatistics().add(added.getScoreStatistics());
        setNumberScoredPeptides(getNumberScoredPeptides() + added.getNumberScoredPeptides());
    }

    public IMeasuredSpectrum getNormalizedRawScan() {
        return m_NormalizedRawScan;
    }

    public void setNormalizedRawScan(IMeasuredSpectrum pNormalizedRawScan) {
        m_NormalizedRawScan = pNormalizedRawScan;
    }

    public double getNormalizationFactor() {
        return m_NormalizationFactor;
    }

    public void setNormalizationFactor(final double pNormalizationFactor) {
        m_NormalizationFactor = pNormalizationFactor;
    }

    public IonUseScore getIonUse() {
        return m_IonUse;
    }


    @Override
    public double getMassDifferenceFromBest() {
        final ISpectralMatch bestMatch = getBestMatch();
        final IMeasuredSpectrum measured = bestMatch.getMeasured();
        final double mass = getMassPlusHydrogen();
        double pm = bestMatch.getPeptide().getMass();
        double peptideMass = pm + XTandemUtilities.getProtonMass() + XTandemUtilities.getCleaveCMass() + XTandemUtilities.getCleaveNMass();  // M + h
        double del = Math.abs(peptideMass - mass);

        return del;
    }

    /**
     * get the total peaks matched for all ion types
     *
     * @return
     */
    @Override
    public int getNumberMatchedPeaks() {
        return getIonUse().getNumberMatchedPeaks();
    }

    /**
     * get the score for a given ion type
     *
     * @param type !null iontype
     * @return score for that type
     */
    @Override
    public double getScore(IonType type) {
        return getIonUse().getScore(type);
    }

    /**
     * get the count for a given ion type
     *
     * @param type !null iontype
     * @return count for that type
     */
    @Override
    public int getCount(IonType type) {
        return getIonUse().getCount(type);
    }

    /**
     * @return
     */
    public boolean isValid() {
        final RawPeptideScan raw = getRaw();
        if (raw == null)
            return false;
        if (raw.getPrecursorMz() == null)
            return false;
        if (raw.getPeaksCount() < 8) // 20)  // todo make it right
            return false;
        if (m_ConditionedScan == null) {
            return false; // we have already tried and failed to condition the scan
        }
        return true;
    }

    @Override
    public HyperScoreStatistics getHyperScores() {
        return m_HyperScores;
    }

    @Override
    public VariableStatistics getScoreStatistics() {
        return m_ScoreStatistics;
    }

    @Override
    public ISpectralMatch getBestMatch() {
        return m_BestMatch;
    }

    public void setBestMatch(ISpectralMatch newBest) {
        m_BestMatch = newBest;
        m_ExpectedValue = 0;
    }

    @Override
    public ISpectralMatch getNextBestMatch() {
        ISpectralMatch ret = null;
        for (ISpectralMatch test : m_Matches) {
            if (test == getBestMatch())
                continue;
            if (ret == null || ret.getHyperScore() < test.getHyperScore())
                ret = test;
        }
        return ret;
    }

    public void addSpectralMatch(ISpectralMatch added) {
        final double score = added.getHyperScore();
        m_HyperScores.add(score);
        final double v = added.getScore();
        m_ScoreStatistics.add(v);
        m_Matches.add(added);
        final ISpectralMatch bestMatch = getBestMatch();
        if (bestMatch == null) {
            setBestMatch(added);
        }
        else {
            if (added.getHyperScore() > (bestMatch.getHyperScore()))
                setBestMatch(added);
        }
    }

    public ISpectralMatch[] getSpectralMatches() {
        ISpectralMatch[] ret = m_Matches.toArray(ISpectralMatch.EMPTY_ARRAY);
        Arrays.sort(ret);
        return ret;
    }

    @Override
    public RawPeptideScan getRaw() {
        return m_Raw;
    }

    @Override
    public String getRetentionTimeString() {
        RawPeptideScan raw = getRaw();
        if(raw != null)
            return raw.getRetentionTime();
        return null;
    }


    /**
     * rention time as a seconds
     *
     * @return possibly null 0
     */
    @Override
    public double getRetentionTime() {
        String str = getRetentionTimeString();
        if(str != null)  {
            str = str.replace("S","");   // handle PT5.5898S"
            str = str.replace("PT","");
            str =  str.trim();
            if(str.length() > 0) {
                try {
                    double ret = Double.parseDouble(str);
                }
                catch (NumberFormatException e) {
                    return 0;
                 }
             }
        }
        return 0;
    }


    @Override
    public IMeasuredSpectrum getConditionedScan() {
        return m_ConditionedScan;
    }

    public void setConditionedScan(final IMeasuredSpectrum pConditionedScan) {
        m_ConditionedScan = pConditionedScan;
    }

    @Override
    public IMeasuredSpectrum conditionScan(IScoringAlgorithm alg, final SpectrumCondition sc) {
        if (m_ConditionedScan == null) {
            m_ConditionedScan = alg.conditionSpectrum(this, sc);
        }
        return m_ConditionedScan;
    }


    /**
     * return the scan identifier
     *
     * @return as above
     */
    @Override
    public String getId() {
        return getRaw().getId();
    }

    /**
     * return the base ion charge
     *
     * @return as above
     */
    @Override
    public int getCharge() {
        return getRaw().getPrecursorCharge();
    }

    /**
     * return the base mass plus  the mass of a proton
     *
     * @return as above
     */
    @Override
    public double getMassPlusHydrogen() {
        final double mass = getRaw().getPrecursorMass();  // todo fix

        return mass;
    }


    /**
     * return
     *
     * @return as above
     */
    @Override
    public double getExpectedValue() {
        if (Double.isNaN(m_ExpectedValue))
            return 0;
        return m_ExpectedValue;

    }

    public void setExpectedValue(double pExpectedValue) {
        m_ExpectedValue = pExpectedValue;
    }

    @Override
    public double getSumIntensity() {
        final double factor = getNormalizationFactor();
        final IMeasuredSpectrum spectrum = getNormalizedRawScan();
        final double sumPeak = XTandemUtilities.getSumPeaks(spectrum) / factor;
        return sumPeak;
    }

    @Override
    public double getMaxIntensity() {
        final double factor = getNormalizationFactor();
        final IMeasuredSpectrum spectrum = getNormalizedRawScan();
        final double sumPeak = XTandemUtilities.getMaxPeak(spectrum) / factor;
        return sumPeak;
    }

    @Override
    public double getFI() {
        throw new UnsupportedOperationException("Fix This"); // ToDo
    }


    @Override
    public String toString() {
        return "scan " + getId() +
                "  precursorMass " + getMassPlusHydrogen();

    }


    @Override
    public int compareTo(final IScoredScan o) {
        if (o == this)
            return 0;
        return getId().compareTo(o.getId());

    }

    public boolean equivalent(IScoredScan scan) {
        if (scan == this)
            return true;
        if (getCharge() != scan.getCharge())
            return false;
        if (!XTandemUtilities.equivalentDouble(getExpectedValue(), scan.getExpectedValue()))
            return false;
        final RawPeptideScan raw1 = getRaw();
        final RawPeptideScan raw2 = scan.getRaw();
        if (!raw1.equivalent(raw2))
            return false;
        final ISpectralMatch[] sm1 = getSpectralMatches();
        final ISpectralMatch[] sm2 = scan.getSpectralMatches();
        if (sm1.length != sm2.length)
            return false;
        for (int i = 0; i < sm2.length; i++) {
            ISpectralMatch m1 = sm1[i];
            ISpectralMatch m2 = sm2[i];
            if (!m1.equivalent(m2))
                return false;
        }
        return true;
    }

    /**
     * make a form suitable to
     * 1) reconstruct the original given access to starting conditions
     *
     * @param adder !null where to put the data
     */
    public void serializeAsString(IXMLAppender adder) {
        int indent = 0;
        String tag = TAG;
        adder.openTag(tag);
        adder.appendAttribute("id ", getId());
        adder.appendAttribute("version ", getVersion());
        double expectedValue = getExpectedValue();
        adder.appendAttribute("expectedValue", expectedValue);
        adder.appendAttribute("numberScoredPeptides", getNumberScoredPeptides());
        adder.endTag();

        final RawPeptideScan raw = getRaw();
        if (raw != null)
            raw.serializeAsString(adder);
        final IMeasuredSpectrum conditioned = getConditionedScan();
        if (conditioned != null) {
            adder.openEmptyTag("ConditionedScan");
            adder.cr();
            conditioned.serializeAsString(adder);
            adder.closeTag("ConditionedScan");
        }
        IMeasuredSpectrum scan = getNormalizedRawScan();
        if (scan != null) {
            adder.openEmptyTag("NormalizedRawScan");
            adder.cr();
            scan.serializeAsString(adder);
            adder.closeTag("NormalizedRawScan");
        }

        final ISpectralMatch[] matches = getSpectralMatches();
        Arrays.sort(matches);

        int count = MAX_SERIALIZED_MATCHED;
        for (int i = matches.length - 1; i > 0; i--) {
            ISpectralMatch match = matches[i];
            match.serializeAsString(adder);
            // limit scored matched put in descending order
            if(count-- <= 0)
                break;
        }
        adder.closeTag(tag);

    }

    /**
     * weak test for equality
     *
     * @param test !null test
     * @return true if equivalent
     */
    @Override
    public boolean equivalent(IonTypeScorer test) {
        if (test == this)
            return true;


        for (IonType type : IonType.values()) {
            if (getCount(type) != test.getCount(type))
                return false;
            if (!XTandemUtilities.equivalentDouble(getScore(type), test.getScore(type)))
                return false;

        }
        return true;
    }
}
