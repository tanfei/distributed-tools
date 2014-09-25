package org.systemsbiology.xtandem.mzml;

import org.systemsbiology.xtandem.*;

/**
 * org.systemsbiology.xtandem.mzml.ScanReadListener
 * User: Steve
 * Date: Apr 26, 2011
 */
public interface ScanReadListener {
    public static final ScanReadListener[] EMPTY_ARRAY = {};

    public void onScanRead(RawPeptideScan scan);

}
