package uk.ac.ebi.pride.spectracluster;

import com.lordjoe.distributed.*;
import uk.ac.ebi.pride.spectracluster.cluster.*;
import uk.ac.ebi.pride.spectracluster.keys.*;

import java.util.*;

/**
 * main.uk.ac.ebi.pride.spectracluster.BinKeyMapper
 * User: Steve
 * Date: 9/2/2014
 */
public class MzKeyMapper implements IMapperFunction<ICluster, MZKey, ICluster> {

    public MzKeyMapper() {
    }

    /**
     * this is what a Mapper does
     *
     * @param valuein @return iterator over mapped key values
     */
    @Override public Iterable<KeyValueObject<MZKey, ICluster>> mapValues(final ICluster cluster) {

        List<KeyValueObject<MZKey, ICluster>> ret = new ArrayList<KeyValueObject<MZKey, ICluster>>();
        double precursorMZ = cluster.getPrecursorMz();
        MZKey mzKey = new MZKey(precursorMZ);
        ret.add(new KeyValueObject<MZKey, ICluster>(mzKey, cluster));

        return ret;
    }
}
