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
        // needed to fix norway bug

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


    public SparkMapReduce<String, IMeasuredSpectrum, String, IMeasuredSpectrum, String, IScoredScan> getHandler() {
        return handler;
    }


    public JavaRDD<KeyValueObject<String, IScoredScan>> getOutput() {
        JavaRDD<KeyValueObject<String, IScoredScan>> output = handler.getOutput();
        return output;
    }


    public XTandemMain getApplication() {
        return application;
    }


    /**
     * all the work is done here
     *
     * @param pInputs
     */
    public void performSingleReturnMapReduce(final JavaRDD<KeyValueObject<String, IMeasuredSpectrum>> pInputs) {
        performSetup();
        handler.performSingleReturnMapReduce(pInputs);
    }

    /**
     * all the work is done here
     *
     * @param pInputs
     */
    public void performSourceMapReduce(final JavaRDD<KeyValueObject<String, IMeasuredSpectrum>> pInputs) {
        performSetup();
        handler.performSourceMapReduce(pInputs);
    }

    protected void performSetup() {
        ((AbstractTandemFunction) handler.getMap()).setup(getJavaContext());
        ((AbstractTandemFunction) handler.getReduce()).setup(getJavaContext());
    }
}