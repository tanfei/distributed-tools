package org.systemsbiology.xtandem.mzml;

import org.systemsbiology.xtandem.*;

import java.util.*;
import java.util.concurrent.*;

/**
 * org.systemsbiology.xtandem.mzml.CountingSpectrumHandler
 * User: Steve
 * Date: Apr 26, 2011
 */
public class ScanGeneratingSpectrumHandler implements TagEndListener<ExtendedSpectrumImpl> {
    public static final ScanGeneratingSpectrumHandler[] EMPTY_ARRAY = {};

    private final List<ScanReadListener> m_ScanReadListeners;
    // Add to constructor 

    public ScanGeneratingSpectrumHandler() {
        m_ScanReadListeners = new CopyOnWriteArrayList<ScanReadListener>();

    }


    /**
     * add a change listener
     * final to make sure this is not duplicated at multiple levels
     *
     * @param added non-null change listener
     */
    public final void addScanReadListener(ScanReadListener added) {
        if (!m_ScanReadListeners.contains(added))
            m_ScanReadListeners.add(added);
    }

    /**
     * remove a change listener
     *
     * @param removed non-null change listener
     */
    public final void removeScanReadListener(ScanReadListener removed) {
        while (m_ScanReadListeners.contains(removed))
            m_ScanReadListeners.remove(removed);
    }


    /**
     * notify any state change listeners - probably should
     * be protected but is in the interface to form an event cluster
     *
     * @param oldState
     * @param newState
     * @param commanded
     */
    public void notifyScanListeners(RawPeptideScan scan) {
        if (m_ScanReadListeners.isEmpty())
            return;
        for (ScanReadListener listener : m_ScanReadListeners) {
            listener.onScanRead(scan);
        }
    }


    @Override
    public Class<? extends ExtendedSpectrumImpl> getDesiredClass() {
        return ExtendedSpectrumImpl.class;
    }

    @Override
    public void onTagEnd(final String tag, final ExtendedSpectrumImpl inp) {
        RawPeptideScan spectrum = MzMlUtilities.buildSpectrum(inp);
        notifyScanListeners(spectrum);
    }
}
