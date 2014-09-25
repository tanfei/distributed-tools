package org.systemsbiology.xtandem.mzml;

/**
 * org.systemsbiology.xtandem.mzml.TagEndListener
 * User: steven
 * Date: 4/25/11
 */
public interface TagEndListener<T> {
    public static final TagEndListener[] EMPTY_ARRAY = {};

    public Class<? extends T>  getDesiredClass();

    public void onTagEnd(String tag,T lastGenerated);

}
