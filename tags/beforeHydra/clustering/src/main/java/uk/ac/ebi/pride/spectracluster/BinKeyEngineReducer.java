package uk.ac.ebi.pride.spectracluster;

import uk.ac.ebi.pride.spectracluster.engine.*;
import uk.ac.ebi.pride.spectracluster.keys.*;

/**
 * uk.ac.ebi.pride.spectracluster.PeakKeyEngineReducer
 * User: Steve
 * Date: 9/2/2014
 */
public class BinKeyEngineReducer extends AbstractEngineReducer {

    private final double windowSize;

    public BinKeyEngineReducer(final double pWindowSize) {
        windowSize = pWindowSize;
    }

    /**
     * build a new engine
     *
     * @param pKey
     * @return
     */
    @Override protected IIncrementalClusteringEngine buildClusteringEngine(final IKeyable pKey) {
        return getFactory().getIncrementalClusteringEngine((float)windowSize);
    }



    /**
     * do these keys match
     *
     * @param pCurrentkey
     * @param pKey
     * @return
     */
    @Override protected boolean keysMatch(final IKeyable pCurrentkey, final IKeyable pKey) {
        return ((BinMZKey)pCurrentkey).getBin() ==
                ((BinMZKey)pKey).getBin();
    }
}
