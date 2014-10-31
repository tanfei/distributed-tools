package com.lordjoe.distributed.hydra.fragment;

import com.lordjoe.distributed.hydra.scoring.*;
import org.apache.spark.api.java.*;
import org.apache.spark.api.java.function.*;
import org.systemsbiology.xtandem.*;
import org.systemsbiology.xtandem.peptide.*;
import scala.*;

import java.io.Serializable;
import java.util.*;

/**
 * com.lordjoe.distributed.hydra.fragment.BinChargeMapper
 * User: Steve
 * Date: 10/31/2014
 */
public class BinChargeMapper implements Serializable {

    public static final double binSize = 0.2;
    public static final double examineWidth = 0.6;


    private final XTandemMain application;

    public BinChargeMapper(SparkMapReduceScoringHandler pHandler) {
        this(pHandler.getApplication());
    }

    public BinChargeMapper(XTandemMain app) {
        application = app;
    }

    public JavaPairRDD<BinChargeKey, IMeasuredSpectrum> mapMeasuredSpectrumToKeys(JavaRDD<IMeasuredSpectrum> inp) {
        return inp.flatMapToPair(new mapMeasuredSpectraToBins());
    }

    public JavaPairRDD<BinChargeKey, IPolypeptide> mapFragmentsToKeys(JavaRDD<IPolypeptide> inp) {
        return inp.flatMapToPair(new mapPolypeptidesToBins());
    }

    public BinChargeKey[] keysFromChargeMz(int charge, double mz) {
        List<BinChargeKey> holder = new ArrayList<BinChargeKey>();
        double mzStart = ((int) (0.5  + ((mz - examineWidth) / binSize))) * binSize;
        for (int i = 0; i < examineWidth / binSize; i++) {
            double quantizedMz = mzStart + i + binSize;
            holder.add(new BinChargeKey(charge, quantizedMz)); // todo add meighbors

        }


        BinChargeKey[] ret = new BinChargeKey[holder.size()];
        holder.toArray(ret);
        return ret;
    }

    private class mapPolypeptidesToBins implements PairFlatMapFunction<IPolypeptide, BinChargeKey, IPolypeptide> {
        @Override
        public Iterable<Tuple2<BinChargeKey, IPolypeptide>> call(final IPolypeptide pp) throws Exception {
            double matchingMass = pp.getMatchingMass();
            List<Tuple2<BinChargeKey, IPolypeptide>> holder = new ArrayList<Tuple2<BinChargeKey, IPolypeptide>>();
            for (int charge = 1; charge < 4; charge++) {
                BinChargeKey[] keys = keysFromChargeMz(charge, matchingMass);
                for (int i = 0; i < keys.length; i++) {
                    BinChargeKey key = keys[i];
                    holder.add(new Tuple2<BinChargeKey, IPolypeptide>(key, pp));
                }
            }
            if(holder.isEmpty())
                throw new IllegalStateException("problem"); // ToDo change

            return holder;
        }
    }

    private class mapMeasuredSpectraToBins implements PairFlatMapFunction<IMeasuredSpectrum, BinChargeKey, IMeasuredSpectrum> {
        @Override
        public Iterable<Tuple2<BinChargeKey, IMeasuredSpectrum>> call(final IMeasuredSpectrum spec) throws Exception {
            double matchingMass = spec.getPrecursorMass();
            int charge = spec.getPrecursorCharge();
            List<Tuple2<BinChargeKey, IMeasuredSpectrum>> holder = new ArrayList<Tuple2<BinChargeKey, IMeasuredSpectrum>>();
            BinChargeKey[] keys = keysFromChargeMz(charge, matchingMass);
            for (int i = 0; i < keys.length; i++) {
                BinChargeKey key = keys[i];
                holder.add(new Tuple2<BinChargeKey, IMeasuredSpectrum>(key, spec));
            }
            if(holder.isEmpty())
                throw new IllegalStateException("problem"); // ToDo change
            return holder;
        }
    }
}
