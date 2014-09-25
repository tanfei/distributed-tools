package org.systemsbiology.xtandem;

import org.systemsbiology.xtandem.ionization.*;
import org.systemsbiology.xtandem.peptide.*;

/**
 * org.systemsbiology.xtandem.IGeneratedSpectrumPeak
 *   When spectra are generated we know a lot more
 *   about the generating ion
 * @author Steve Lewis
   */
public interface IGeneratedSpectrumPeak  extends ISpectrumPeak
{
    public static IGeneratedSpectrumPeak[] EMPTY_ARRAY = {};
    public static Class THIS_CLASS = IGeneratedSpectrumPeak.class;

    /**
     * A,B,C,X,Y,Z
     * @return !null type
     */
    public IonType getType();


    /**
     * generating peptide
      * @return
     */
     public IPolypeptide getPeptide();

}
