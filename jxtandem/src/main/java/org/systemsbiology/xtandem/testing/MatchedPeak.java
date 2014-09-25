package org.systemsbiology.xtandem.testing;

import org.systemsbiology.xtandem.*;

/**
 * org.systemsbiology.xtandem.testing.MatchedPeak
 *   used to hold peaks that have been matched in the dot product - this is used for
 *   holding matches in the dot product for testing
 * @author Steve Lewis
 * @date Feb 22, 2011
 */
public class MatchedPeak implements Comparable<MatchedPeak>
{
    public static MatchedPeak[] EMPTY_ARRAY = {};
    public static Class THIS_CLASS = MatchedPeak.class;

    private final ISpectrumPeak m_Peak;
    private final double m_AddedValue;

    public MatchedPeak(ISpectrumPeak pPeak, double pAddedValue)
    {
        m_Peak = pPeak;
        m_AddedValue = pAddedValue;
    }

    public ISpectrumPeak getPeak()
    {
        return m_Peak;
    }

    public double getAddedValue()
    {
        return m_AddedValue;
    }

     @Override
    public int compareTo(MatchedPeak o)
    {
        if (o == this)
            return 0;
        int ret = Double.compare(getPeak().getMassChargeRatio(),o.getPeak().getMassChargeRatio());
        if(ret != 0)
            return ret;
        ret = Double.compare(getAddedValue(),o.getAddedValue());
        return ret;
    }

    public static final double TOLERANCE = 0.001;
    
    public boolean equivalent(MatchedPeak o)
    {
        if (o == this)
            return true;
        if(getPeak().getMassChargeRatio() != o.getPeak().getMassChargeRatio() )
            return false;
        if(Math.abs(getAddedValue() - o.getAddedValue())  > TOLERANCE)
            return false;
        return true;
    }

}
