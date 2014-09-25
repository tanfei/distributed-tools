package org.systemsbiology.xtandem.scoring;

import org.systemsbiology.xtandem.ionization.*;

/**
 * org.systemsbiology.xtandem.scoring.IWeightAdjuster
 *
 * @author Steve Lewis
 * @date Jan 17, 2011
 */
public interface IWeightAdjuster
{
    public static IWeightAdjuster[] EMPTY_ARRAY = {};
    public static Class THIS_CLASS = IWeightAdjuster.class;

    /**
     * t6he instnsity of a theoretical peak is frequently adjusted in scoring
     *   implementing classes will adjust the insensity returning a new peak
     *   This is important to keep the ITheoreticalPeak as a immutable object
     * @param in !null peak
     * @return !null  adjusted peak
     */
    public ITheoreticalPeak adjustWeight(ITheoreticalPeak in);
}
