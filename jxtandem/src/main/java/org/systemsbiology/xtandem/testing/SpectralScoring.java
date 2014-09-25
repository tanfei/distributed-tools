package org.systemsbiology.xtandem.testing;

import org.systemsbiology.xtandem.*;
import org.systemsbiology.xtandem.ionization.*;

import java.util.*;

/**
 * org.systemsbiology.xtandem.testing.SpectralScoring
 *
 * @author Steve Lewis
 * @date Feb 22, 2011
 */
public class SpectralScoring
{
    public static SpectralScoring[] EMPTY_ARRAY = {};
    public static Class THIS_CLASS = SpectralScoring.class;

    private final IMeasuredSpectrum m_Measured;
    private final ITheoreticalSpectrum m_TheoreticalSpectrum;
    private final IonUseCounter m_UseCounter = new IonUseCounter();
        private List<MatchedPeak> m_MatchedPeaks = new ArrayList<MatchedPeak>();


    public SpectralScoring(IMeasuredSpectrum pMeasured, ITheoreticalSpectrum pTheoreticalSpectrum)
    {
        m_Measured = pMeasured.asImmutable();
        m_TheoreticalSpectrum = pTheoreticalSpectrum ;
    }

    public IMeasuredSpectrum getMeasured()
    {
        return m_Measured;
    }

    public ITheoreticalSpectrum getTheoreticalSpectrum()
    {
        return m_TheoreticalSpectrum;
    }

    public IonUseCounter getUseCounter()
    {
        return m_UseCounter;
    }


    public void addMatchedPeaks(MatchedPeak added) {
        m_MatchedPeaks.add(added);
    }


    public void removeMatchedPeaks(MatchedPeak removed) {
        m_MatchedPeaks.remove(removed);
    }

    public MatchedPeak[] getMatchedPeakss( ) {
        return m_MatchedPeaks.toArray(new MatchedPeak[0]);
    }
    

    /**
      * copy the contents from another counter
      * @param source
      */
     public void loadFrom(IonUseCounter source)  {
         getUseCounter().loadFrom(source);
        
     }

}
