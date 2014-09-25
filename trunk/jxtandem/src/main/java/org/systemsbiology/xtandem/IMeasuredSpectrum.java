package org.systemsbiology.xtandem;

import org.systemsbiology.sax.*;
import org.systemsbiology.xtandem.sax.*;

/**
 * org.systemsbiology.xtandem.IMeasuredSpectrum
 *   A spedctrum used for scoring - characteristics of the
 * run are in alother object
 * @author Steve Lewis
  */
public interface IMeasuredSpectrum extends ISpectrum
{
    public static IMeasuredSpectrum[] EMPTY_ARRAY = {};
    public static Class THIS_CLASS = IMeasuredSpectrum.class;

    /**
      * make a form suitable to
      * 1) reconstruct the original given access to starting conditions
      *
      * @param adder !null where to put the data
      */
     public void serializeAsString(IXMLAppender adder);

    /**
     * weak test for equality
     * @param test !null test
     * @return  true if equivalent
     */
    public boolean equivalent(IMeasuredSpectrum test);
    
    /**
     * return true if the spectrum is immutable
     * @return
     */
    public boolean isImmutable();

    /**
     * if the spectrum is not immutable build an immutable version
     * Otherwise return this
     * @return as above
     */
    public IMeasuredSpectrum asImmutable();

    /**
     * if the spectrum is not  mutable build an  mutable version
     * Otherwise return this
     * @return as above
     */
    public MutableMeasuredSpectrum asMmutable();


    /**
     * get the charge of the spectrum precursor
     * @return   as above
     */
    public int getPrecursorCharge();

    /**
     * get the mass of the spectrum precursor
     * @return  as above
     */
    public double getPrecursorMass();

    /**
     * get the mz of the spectrum precursor
     * @return  as above
     */
    public double getPrecursorMassChargeRatio();



    /**
     * get run identifier
     * @return  as above
     */
    public String getId();

    /**
     * Mass spec characteristics
     * @return  as above
     */
    public  ISpectralScan getScanData();

}
