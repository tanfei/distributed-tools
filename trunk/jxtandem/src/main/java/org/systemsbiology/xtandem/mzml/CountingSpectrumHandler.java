package org.systemsbiology.xtandem.mzml;

import org.systemsbiology.xml.*;
import org.systemsbiology.xtandem.*;

/**
 * org.systemsbiology.xtandem.mzml.CountingSpectrumHandler
 *  debuggin gclass which simply counts spectra
 * User: Steve
 * Date: Apr 26, 2011
 */
public class CountingSpectrumHandler implements TagEndListener<ExtendedSpectrumImpl> {
    public static final CountingSpectrumHandler[] EMPTY_ARRAY = {};

    private int m_Count;

    public CountingSpectrumHandler() {
    }

    public int getCount() {
        return m_Count;
    }

    public void resetCount() {
          m_Count = 0;
    }

    @Override
    public Class<? extends ExtendedSpectrumImpl> getDesiredClass() {
        return ExtendedSpectrumImpl.class;
    }

    @Override
    public void onTagEnd(final String tag, final ExtendedSpectrumImpl lastGenerated) {
       m_Count++;
        // for debugging and development - show progress
        if(m_Count % 1000 == 0)
            XMLUtilities.outputText(".");
        if(m_Count % 5000 == 0) {
            XMLUtilities.outputLine();
            // make sure we are not leaking memory
            XMLUtilities.outputLine("free memory " + Runtime.getRuntime().freeMemory());
        }
    }
}
