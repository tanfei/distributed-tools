package org.systemsbiology.xtandem.hadoop;

/**
 * User: steven
 * Date: 3/7/11
 */

import org.systemsbiology.hadoop.*;

/**
 * org.systemsbiology.xtandem.hadoop.MzXMLInputFormat
 * Splitter that reads scan tags from a MzXML file
 */
public class ScoredScanXMLInputFormat extends KeyValueXMLTagInputFormat
{
    public static final ScoredScanXMLInputFormat[] EMPTY_ARRAY = {};

    public static final String SCAN_TAG = "score";

    public ScoredScanXMLInputFormat() {
        super(SCAN_TAG);
    }


}
