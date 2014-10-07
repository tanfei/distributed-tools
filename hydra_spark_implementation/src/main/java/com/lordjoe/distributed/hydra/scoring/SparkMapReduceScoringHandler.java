package com.lordjoe.distributed.hydra.scoring;

import com.lordjoe.distributed.*;
import com.lordjoe.distributed.context.*;
import com.lordjoe.distributed.tandem.*;
import org.apache.spark.api.java.*;
import org.systemsbiology.xtandem.*;
import org.systemsbiology.xtandem.scoring.*;

import java.io.*;

/**
 * com.lordjoe.distributed.hydra.scoring.SparkMapReduceScoringHandler
 * User: Steve
 * Date: 10/7/2014
 */
public class SparkMapReduceScoringHandler {

    private final SparkContext context;
    private XTandemMain application;
    private final SparkMapReduce<String, IMeasuredSpectrum, String, IMeasuredSpectrum, String, IScoredScan> handler;

    public SparkMapReduceScoringHandler(File congiguration) {
        context = new SparkContext("LibraryBuilder");
        SparkUtilities.guaranteeSparkMaster(context.getSparkConf());    // use local if no master provided

        application = new XTandemMain(congiguration);
        handler = new SparkMapReduce("Score Scans", new ScanTagMapperFunction(application), new ScoringReducer(application));
    }

    public SparkContext getContext() {
        return context;
    }

    @SuppressWarnings("UnusedDeclaration")
    public JavaSparkContext getJavaContext() {
        SparkContext context1 = getContext();
        return context1.getCtx();
    }


    public XTandemMain getApplication() {
        return application;
    }


    /**
     * all the work is done here
     *
     * @param pInputs
     */
    public void performSourceMapReduce(final JavaRDD<KeyValueObject<String, IMeasuredSpectrum>> pInputs) {
        ((AbstractTandemFunction)handler.getMap()).setup(getJavaContext());
        ((AbstractTandemFunction)handler.getReduce()).setup(getJavaContext());
         handler.performSourceMapReduce(pInputs);
    }
}