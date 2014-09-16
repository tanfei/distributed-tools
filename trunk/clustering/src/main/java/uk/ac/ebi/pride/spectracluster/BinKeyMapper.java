package uk.ac.ebi.pride.spectracluster;

import com.lordjoe.algorithms.*;
import com.lordjoe.distributed.*;
import uk.ac.ebi.pride.spectracluster.cluster.*;
import uk.ac.ebi.pride.spectracluster.hadoop.*;
import uk.ac.ebi.pride.spectracluster.keys.*;

import java.util.*;

/**
 * main.uk.ac.ebi.pride.spectracluster.BinKeyMapper
 * User: Steve
 * Date: 9/2/2014
 */
public class BinKeyMapper implements IMapperFunction<ICluster, BinMZKey, ICluster> {

    private double spectrumMergeWindowSize = HadoopDefaults.getSpectrumMergeMZWindowSize();  // this was not initialized!! 7/22/14 SL
    private IWideBinner binner = HadoopDefaults.DEFAULT_WIDE_MZ_BINNER;

    public BinKeyMapper() {
    }

    /**
     * this is what a Mapper does
     *
     * @param valuein @return iterator over mapped key values
     */
    @Override public Iterable<KeyValueObject<BinMZKey, ICluster>> mapValues(final ICluster cluster) {

        List<KeyValueObject<BinMZKey, ICluster>> ret = new ArrayList<KeyValueObject<BinMZKey, ICluster>>();
        double precursorMZ = cluster.getPrecursorMz();
        int[] bins = binner.asBins(precursorMZ);
        //noinspection ForLoopReplaceableByForEach
        for (int j = 0; j < bins.length; j++) {
            int bin = bins[j];
            BinMZKey mzKey = new BinMZKey(bin, precursorMZ);
            ret.add(new KeyValueObject<BinMZKey, ICluster>(mzKey, cluster));
        }

        return ret;
    }
}
