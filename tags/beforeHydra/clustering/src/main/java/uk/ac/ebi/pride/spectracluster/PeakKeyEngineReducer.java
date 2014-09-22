package uk.ac.ebi.pride.spectracluster;

import uk.ac.ebi.pride.spectracluster.engine.*;
import uk.ac.ebi.pride.spectracluster.hadoop.*;
import uk.ac.ebi.pride.spectracluster.keys.*;

/**
 * uk.ac.ebi.pride.spectracluster.PeakKeyEngineReducer
 * User: Steve
 * Date: 9/2/2014
 */
public class PeakKeyEngineReducer extends AbstractEngineReducer {

    public PeakKeyEngineReducer() {
    }

    /**
     * build a new engine
     *
     * @param pKey
     * @return
     */
    @Override protected IIncrementalClusteringEngine buildClusteringEngine(final IKeyable pKey) {
        return getFactory().getIncrementalClusteringEngine((float)HadoopDefaults.getMajorPeakMZWindowSize());
    }



    /**
     * do these keys match
     *
     * @param pCurrentkey
     * @param pKey
     * @return
     */
    @Override protected boolean keysMatch(final IKeyable pCurrentkey, final IKeyable pKey) {
        return ((PeakMZKey)pCurrentkey).getPeakMZ() ==
                ((PeakMZKey)pKey).getPeakMZ();
    }
}
