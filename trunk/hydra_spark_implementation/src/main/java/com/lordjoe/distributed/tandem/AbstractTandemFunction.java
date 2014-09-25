package com.lordjoe.distributed.tandem;

import org.systemsbiology.xtandem.*;

import java.io.*;

/**
 * com.lordjoe.distributed.tandem.AbstractTandemFunction
 * User: Steve
 * Date: 9/24/2014
 */
public abstract class AbstractTandemFunction implements Serializable {

    private final XTandemMain application;

    public AbstractTandemFunction(final XTandemMain pMain) {
        application = pMain;
    }

    public XTandemMain getApplication() {
        return application;
    }


}
