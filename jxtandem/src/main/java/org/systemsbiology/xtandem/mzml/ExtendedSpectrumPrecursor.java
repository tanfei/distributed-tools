package org.systemsbiology.xtandem.mzml;

import org.proteios.io.*;

/**
 * org.systemsbiology.xtandem.mzml.ExtendedSpectrumPrecursor
 * added to override  SpectrumPrecursor and set valiues from extraData
 * especally needed to prevent nullpointerexceptions when reading Double
 * User: Steve
 * Date: May 3, 2011
 */
public class ExtendedSpectrumPrecursor extends SpectrumPrecursor {
    public static final ExtendedSpectrumPrecursor[] EMPTY_ARRAY = {};

    public ExtendedSpectrumPrecursor() {
    }


    /**
     * Add spectrum precursor extra data.
     *
     * @param extraData StringPairInterface The spectrum precursor extra data to add.
     */
    @Override
    public void addExtraData(final StringPairInterface extraData) {
        super.addExtraData(extraData);    //To change body of overridden methods use File | Settings | File Templates.
        if("m/z".equals(extraData.getName())) {
            String value = extraData.getValue();
            double dValue = Double.parseDouble(value);
            setMassToChargeRatio(dValue);
            return;
        }
         if("collision-induced dissociation".equals(extraData.getName()))   {
             setFragmentationType(FragmentationType.CID);
             return;

         }
        if("collision energy".equals(extraData.getName())) {
             return;

         }
        if("collision energy".equals(extraData.getName())) {
             return;

         }
     }


}
