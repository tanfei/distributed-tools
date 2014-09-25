package org.systemsbiology.xtandem.scoring;

import org.systemsbiology.sax.*;
import org.systemsbiology.xtandem.*;
import org.systemsbiology.xtandem.ionization.*;
import org.systemsbiology.xtandem.sax.*;

/**
 * org.systemsbiology.xtandem.scoring.SpectralScore
 * hold the score for a single measuredSpectrum  matching a single
 * theoretical spectrum
 * User: steven
 * Date: 1/21/11
 */
public class SpectralScore implements ISpectralScore
{
    public static final SpectralScore[] EMPTY_ARRAY = {};

    private final IMeasuredSpectrum m_Spectrum;
    private final ITheoreticalSpectrum m_Fragment;
    private final double m_Score;
    private final double m_HyperScore;
    private final IonUseScore m_IonScore;

    public SpectralScore(final IMeasuredSpectrum pSpectrum, final ITheoreticalSpectrum pFragment,
                         final double pHyperScore,
                         final double pScore,
                         final IonUseScore use
    )
    {
        m_Spectrum = pSpectrum;
        m_HyperScore = pHyperScore;
        m_Score = pScore;
        m_Fragment = pFragment;
        m_IonScore = use;
    }

    @Override
    public IMeasuredSpectrum getSpectrum()
    {
        return m_Spectrum;
    }

    @Override
    public ITheoreticalSpectrum getFragment()
    {
        return m_Fragment;
    }


    /**
     * get the total peaks matched for all ion types
     *
     * @return
     */
    @Override
    public int getNumberMatchedPeaks()
    {
        return m_IonScore.getNumberMatchedPeaks();
    }

    @Override
    public double getScore()
    {
        return m_Score;
    }

    @Override
    public double getHyperScore()
    {
        return m_HyperScore;
    }

    /**
     * get the score for a given ion type
     *
     * @param type !null iontype
     * @return score for that type
     */
    @Override
    public double getScore(IonType type)
    {
        return m_IonScore.getScore(type);
    }

    /**
     * get the count for a given ion type
     *
     * @param type !null iontype
     * @return count for that type
     */
    @Override
    public int getCount(IonType type)
    {
        return m_IonScore.getCount(type);
    }

    @Override
    public int compareTo(final ISpectralScore pO)
    {
        if (this == pO)
            return 0;
        int ret = Double.compare(getHyperScore(), pO.getHyperScore());
        if (ret != 0)
            return ret;
        ret = Double.compare(getScore(), pO.getScore());
        if (ret != 0)
            return ret;
        ret = Double.compare(getNumberMatchedPeaks(), pO.getNumberMatchedPeaks());
        if (ret != 0)
            return ret;
        return 0;
    }

    /**
     * weak test for equality
     *
     * @param test !null test
     * @return true if equivalent
     */
    @Override
    public boolean equivalent(IonTypeScorer test)
    {
        if(test == this)
              return true;


           for (IonType  type :  IonType.values() ) {
               if(getCount(type) != test.getCount(type))
                    return false;
               if(!XTandemUtilities.equivalentDouble(getScore(type), test.getScore(type)))
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
    @Override
    public void serializeAsString(final IXMLAppender adder) {
           throw new UnsupportedOperationException("Fix This"); // ToDo
    }
}
