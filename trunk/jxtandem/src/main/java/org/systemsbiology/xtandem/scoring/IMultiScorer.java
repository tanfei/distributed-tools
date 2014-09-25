package org.systemsbiology.xtandem.scoring;

/**
 * org.systemsbiology.xtandem.scoring.IMultiScorer
 * User: Steve
 * Date: 1/12/12
 */
public interface IMultiScorer {
    public static final IMultiScorer[] EMPTY_ARRAY = {};

    /**
     * return a list of all algorithm names
     *
     * @return !null array
     */
    public String[] getScoringAlgorithms();

    /**
     * if present return a  IScoredScan for an algorithm
     *
     * @param algorithm
     * @return either data of blank if null
     */
    public IScoredScan getScoredScan(String algorithm);

    /**
     * return a list of all scors
     *
     * @return !null array
     */
    public IScoredScan[] getScoredScans();

    /**
     * true if some match is scored
     * @return as above
     */
    public boolean isMatchPresent();


}
