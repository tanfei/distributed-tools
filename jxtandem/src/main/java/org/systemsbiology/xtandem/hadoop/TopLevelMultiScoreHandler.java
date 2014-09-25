package org.systemsbiology.xtandem.hadoop;

import org.systemsbiology.sax.*;
import org.systemsbiology.xtandem.*;
import org.systemsbiology.xtandem.sax.*;

/**
 * org.systemsbiology.xtandem.hadoop.TopLevelScanScoreHandler
 * User: steven
 * Date: 3/28/11
 */
public class TopLevelMultiScoreHandler extends MultiScoreHandler implements ITopLevelSaxHandler    {
    public TopLevelMultiScoreHandler(IMainData data, DelegatingSaxHandler parent) {
        super(data,parent);
    }

}
