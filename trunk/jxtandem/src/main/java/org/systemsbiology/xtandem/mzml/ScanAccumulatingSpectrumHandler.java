package org.systemsbiology.xtandem.mzml;

import org.systemsbiology.xtandem.*;

import java.util.*;
import java.util.concurrent.*;

/**
 * org.systemsbiology.xtandem.mzml.ScanAccumulatingSpectrumHandler
 * User: Steve
 * Date: Apr 26, 2011
 */
public class ScanAccumulatingSpectrumHandler implements TagEndListener<ExtendedSpectrumImpl> {
    public static final ScanAccumulatingSpectrumHandler[] EMPTY_ARRAY = {};

    private final List<RawPeptideScan> m_Scans;

    /**
     * NOTE DO not use for large numbers of scans - this is useful for
     * tests and handling fewer scans
     */
    public ScanAccumulatingSpectrumHandler() {
        m_Scans = new CopyOnWriteArrayList<RawPeptideScan>();

    }


    /**
     * add a change listener
     * final to make sure this is not duplicated at multiple levels
     *
     * @param added non-null change listener
     */
    public void addScan(RawPeptideScan added) {
        if (!m_Scans.contains(added))
            m_Scans.add(added);
    }

    /**
     * remove a change listener
     *
     * @param removed non-null change listener
     */
    public void removeScan(RawPeptideScan removed) {
        while (m_Scans.contains(removed))
            m_Scans.remove(removed);
    }


   
    public RawPeptideScan[] getScans(   ) {
       return m_Scans.toArray(RawPeptideScan.EMPTY_ARRAY);
    }
 
   


    @Override
    public Class<? extends ExtendedSpectrumImpl> getDesiredClass() {
        return ExtendedSpectrumImpl.class;
    }

    @Override
    public void onTagEnd(final String tag, final ExtendedSpectrumImpl inp) {
        RawPeptideScan spectrum = MzMlUtilities.buildSpectrum(inp);
        addScan(spectrum);
    }
}
