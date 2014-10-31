package com.lordjoe.distributed.tandem;

import com.lordjoe.distributed.hydra.fragment.*;
import com.lordjoe.distributed.hydra.scoring.*;
import org.apache.spark.api.java.*;
import org.apache.spark.api.java.function.*;
import org.systemsbiology.xtandem.*;
import org.systemsbiology.xtandem.peptide.*;
import scala.*;

import java.util.*;

/**
 * com.lordjoe.distributed.tandem.LibraryBuilder
 * User: Steve
 * Date: 9/24/2014
 */
public class MassToBinMapper implements Serializable {

    public static final boolean USE_PARQUET_DATABASE = false;

    private final XTandemMain application;   // ToDo use to find bins
    private final double binSize = 0.1; // 10 bins per dalton ToDo do better
    private final double binMargin = 0.005; // 10 bins per dalton ToDo do better

    public MassToBinMapper(SparkMapReduceScoringHandler pHandler) {
        this(pHandler.getApplication());
    }

    public MassToBinMapper(XTandemMain app) {
        application = app; // todo find bins
    }

    /**
     * send polypeptides to bins - ones close to boundaries go to 2 bins
     *
     * @param peptides
     * @return
     */
    public JavaPairRDD<MassBin, IPolypeptide> mapToBins(JavaRDD<IPolypeptide> peptides) {
        return peptides.flatMapToPair(new mapToBinTuplesFunction());
    }

    private MassBin[] getBinsFromMass(final double mass) {
        double val = mass / binSize;
        int ret0 = (int) val;
        MassBin mainBin = new MassBin(ret0, ret0 * binSize, binSize);
        // check upper margin
        double val_1 = val + binMargin;
        int ret1 = (int) val_1;
        if (ret1 != ret0) {

            MassBin[] ret = {mainBin, new MassBin(ret1, ret1 * binSize, binSize)};
            return ret;
        }

        // check lower margin
        double val_2 = val - binMargin;
        ret1 = (int) val_2;
        if (ret1 != ret0) {
            MassBin[] ret = {mainBin, new MassBin(ret1, ret1 * binSize, binSize)};
            return ret;
        }
        // not on margine
        MassBin[] ret = {mainBin};
        return ret;
    }


    private class mapToBinTuplesFunction implements PairFlatMapFunction<IPolypeptide, MassBin, IPolypeptide> {
        @Override
        public Iterable<Tuple2<MassBin, IPolypeptide>> call(final IPolypeptide t) throws Exception {
            MassBin[] bins = getBinsFromMass(t.getMatchingMass());
            List<Tuple2<MassBin, IPolypeptide>> holder = new ArrayList<Tuple2<MassBin, IPolypeptide>>();
            for (int i = 0; i < bins.length; i++) {
                MassBin bin = bins[i];
                holder.add(new Tuple2<MassBin, IPolypeptide>(bin, t));
            }
            return holder;
        }
    }
}
