package org.systemsbiology.xtandem.scoring;

/**
 * org.systemsbiology.xtandem.scoring.IValueFetcher
 * User: steven
 * Date: 1/28/11
 */
public interface IValueFetcher<T> {
    public static final IValueFetcher[] EMPTY_ARRAY = {};

    /**
     * wrapper for a getter designed to turn an array of objects into a n array of doubles
     * @param data
     * @return
     */
    public double fetch(T data);

}
