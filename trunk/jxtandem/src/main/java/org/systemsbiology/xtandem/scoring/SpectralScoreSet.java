package org.systemsbiology.xtandem.scoring;

import org.systemsbiology.xtandem.*;

import java.util.*;

/**
 * org.systemsbiology.xtandem.scoring.SpectralScoreSet
 * holds the best results for scoring peaks against a number of
 * theoretical spectra
 * User: steven
 * Date: 1/21/11
 */
public class SpectralScoreSet {
    public static final SpectralScoreSet[] EMPTY_ARRAY = {};

    public static final int MAX_SCORES = 10;
    public static final int MAX_STORED_SCORES = 20;

    private final IMeasuredSpectrum m_Target;
    private  ISpectralScore m_LowestScore;
    private final PriorityQueue<ISpectralScore> m_orderedScoreIs =
            new PriorityQueue<ISpectralScore>();

    public SpectralScoreSet(final IMeasuredSpectrum pTarget) {
        m_Target = pTarget;
    }

    protected int getMaxScores() {
        return MAX_SCORES;
    }

    /**
     * add a score unless it is worse then the MAX_SCORES we have
     * sometimes clear out the array
     * @param score
     */
    public void addScore(ISpectralScore score) {
        if(m_LowestScore == null)  {
            m_LowestScore = score;
        }
        else {
            // just filling the array so tack the worst
            if(m_orderedScoreIs.size() < MAX_SCORES)  {
                if(m_LowestScore.compareTo(score) == 1)
                    m_LowestScore = score;
            }
            else {
                // worse than the lowest just ignore
                if(m_LowestScore.compareTo(score) == 1)
                    return;

            }
        }
        m_orderedScoreIs.add(score);
        while (m_orderedScoreIs.size() > MAX_STORED_SCORES) {
            // trim te number of scores to  MAX_SCORES
            SpectralScore[] scores = m_orderedScoreIs.toArray(SpectralScore.EMPTY_ARRAY);
            m_orderedScoreIs.clear();
            for (int i = 0; i < MAX_SCORES; i++) {
                m_orderedScoreIs.add(scores[i]);

            }
            m_LowestScore = scores[scores.length - 1];
        }

    }

    /**
     * get the best score according to the comparison criteria in
     *  SpectralScore
     * @return
     */
    public ISpectralScore getBestScore() {
        return m_orderedScoreIs.peek();
    }

    /**
     * get the best scores
     * @return
     */
    public ISpectralScore[] getScores() {
        return m_orderedScoreIs.toArray(SpectralScore.EMPTY_ARRAY);
    }
}
