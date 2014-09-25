package org.systemsbiology.xtandem;

import java.util.*;

/**
 * org.systemsbiology.xtandem.IRefiner
 *
 * @author Steve Lewis
 * @date Dec 21, 2010
 */

/**
 * Interface implemented by refiners - these alter the initial scores in
 * defined ways
 */
public interface IRefiner
{
    public static IRefiner[] EMPTY_ARRAY = {};
    public static Class THIS_CLASS = IRefiner.class;

    /**
     * initialize from the configuration
     * @param params !null configuration
     */
    public void initialize(Map<String,String> params);
}
