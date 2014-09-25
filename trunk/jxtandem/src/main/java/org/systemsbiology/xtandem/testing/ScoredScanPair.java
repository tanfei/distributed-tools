package org.systemsbiology.xtandem.testing;

import org.systemsbiology.xtandem.peptide.*;
import org.systemsbiology.xtandem.scoring.*;

/**
 * org.systemsbiology.xtandem.testing.ScoredScanPair
 * User: steven
 * Date: 4/18/12
 */
public class ScoredScanPair {
    public static final ScoredScanPair[] EMPTY_ARRAY = {};

    private final String m_Id;
    private final ScoringComparisionResult m_Comparison;
    private final ScoredScan m_XTandemScoring;
    private final ScoredScan m_JXTandemScoring;
    private ISpectralMatch m_EquivalentMatch;

    public ScoredScanPair(ScoredScan xScan, ScoredScan jxScan) {
        m_Id = xScan.getId();
        if (!m_Id.equals(jxScan.getId()))
            throw new IllegalArgumentException("different scans");
        m_XTandemScoring = xScan;
        m_JXTandemScoring = jxScan;
        m_Comparison = ScoringComparisionResult.compareScans(xScan, jxScan);
        if (m_Comparison == ScoringComparisionResult.Equal) {
            m_EquivalentMatch = jxScan.getBestMatch();
        }
        else {
            IPolypeptide pepX = xScan.getBestMatch().getPeptide();
            ISpectralMatch jxtMatch = null;
            ISpectralMatch[] spectralMatches = jxScan.getSpectralMatches();
            for (int i = 0; i < spectralMatches.length; i++) {
                ISpectralMatch sm = spectralMatches[i];
                if (sm.getPeptide().equivalent(pepX)) {
                    m_EquivalentMatch = sm;
                    break;
                }
            }
        }
    }

    public String getId() {
        return m_Id;
    }

    public ScoredScan getXTandemScoring() {
        return m_XTandemScoring;
    }

    public ScoredScan getJXTandemScoring() {
        return m_JXTandemScoring;
    }

    public ScoringComparisionResult getComparison() {
        return m_Comparison;
    }

    public ISpectralMatch getEquivalentMatch() {
        return m_EquivalentMatch;
    }
}
