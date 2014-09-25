package org.systemsbiology.xtandem;

/**
 * org.systemsbiology.xtandem.ISpectrum
 *  interface implemented by all spectra  - represents
 *  a collection of peaks
 * @author Steve Lewis
  */
public interface ISpectrum extends IEquivalent<ISpectrum>
{
    public static ISpectrum[] EMPTY_ARRAY = {};
    public static Class THIS_CLASS = ISpectrum.class;

    /**
     * get the number of peaks without returning the peaks
     * @return  as above
     */
    public int getPeaksCount();
    
    /**
     * spectrum - this might have been adjusted
     * @return  1=!null array
     */
    public ISpectrumPeak[] getPeaks();

    /**
     * get all peaks with non-zero intensity
     * @return
     */
    public ISpectrumPeak[] getNonZeroPeaks();

    /**
     * as stated
     * @return
     */
    public double getMaxIntensity();

    /**
     * as stated
     * @return
     */
    public double getSumIntensity();

}
