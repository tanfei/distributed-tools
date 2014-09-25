package org.systemsbiology.xtandem.testing;

import org.systemsbiology.xtandem.*;

import java.util.*;

/**
 * org.systemsbiology.xtandem.testing.TestingDataCache
 *
 * @author Steve Lewis
 * @date Feb 22, 2011
 */
public class TestingDataCache
{
    public static TestingDataCache[] EMPTY_ARRAY = {};
    public static Class THIS_CLASS = TestingDataCache.class;

    private static TestingDataCache gInstance;

    public static synchronized TestingDataCache getInstance()
    {
        if (gInstance == null)
            gInstance = new TestingDataCache();
        return gInstance;
    }

    private List<IMeasuredSpectrum> m_MeasuredSpectrums = new ArrayList<IMeasuredSpectrum>();


    // make sure this is a singleton

    private TestingDataCache()
    {

    }


    public void addMeasuredSpectrums(IMeasuredSpectrum added)
    {
        m_MeasuredSpectrums.add(added);
    }


    public void removeMeasuredSpectrums(IMeasuredSpectrum removed)
    {
        m_MeasuredSpectrums.remove(removed);
    }

    public IMeasuredSpectrum[] getMeasuredSpectrumss()
    {
        return m_MeasuredSpectrums.toArray(new IMeasuredSpectrum[0]);
    }

}
