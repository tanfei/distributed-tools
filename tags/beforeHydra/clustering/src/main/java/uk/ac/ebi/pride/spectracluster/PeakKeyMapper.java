package  uk.ac.ebi.pride.spectracluster;

import com.lordjoe.distributed.*;
import uk.ac.ebi.pride.spectracluster.cluster.*;
import uk.ac.ebi.pride.spectracluster.keys.*;
import uk.ac.ebi.pride.spectracluster.spectrum.*;
import uk.ac.ebi.pride.spectracluster.util.*;

import java.util.*;

/**
 * main.uk.ac.ebi.pride.spectracluster.PeakKeyMapper
 * User: Steve
 * Date: 9/2/2014
 */
public class PeakKeyMapper implements IMapperFunction<ICluster, PeakMZKey, ICluster> {


    public PeakKeyMapper() {
    }

    /**
     * this is what a Mapper does
     *
     * @param valuein @return iterator over mapped key values
     */
    @Override public Iterable<KeyValueObject<PeakMZKey, ICluster>> mapValues(final ICluster valuein) {
        double precursorMZ = valuein.getPrecursorMz();

        List<KeyValueObject<PeakMZKey, ICluster>> ret = new ArrayList<KeyValueObject<PeakMZKey, ICluster>>();
        List<ISpectrum> clusteredSpectra = valuein.getClusteredSpectra();
        if (!clusteredSpectra.isEmpty() && precursorMZ < MZIntensityUtilities.HIGHEST_USABLE_MZ) {
            for (ISpectrum match : clusteredSpectra) {
                ICluster cluster = ClusterUtilities.asCluster(match);
                for (int peakMz : match.asMajorPeakMZs(Defaults.getMajorPeakCount())) {
                    PeakMZKey mzKey = new PeakMZKey(peakMz, precursorMZ);
                   ret.add(new KeyValueObject<PeakMZKey, ICluster>(mzKey,cluster));
                 }
            }
         }
        return ret;
    }
}
