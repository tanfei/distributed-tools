package com.lordjoe.distributed.hydra.scoring;

import com.lordjoe.distributed.*;
import com.lordjoe.distributed.hydra.*;
import com.lordjoe.distributed.hydra.fragment.*;
import com.lordjoe.distributed.tandem.*;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.fs.*;
import org.apache.spark.*;
import org.apache.spark.api.java.*;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.systemsbiology.common.*;
import org.systemsbiology.xtandem.*;
import org.systemsbiology.xtandem.hadoop.*;
import org.systemsbiology.xtandem.peptide.*;
import org.systemsbiology.xtandem.scoring.*;
import org.systemsbiology.xtandem.taxonomy.*;
import scala.*;

import java.io.*;
import java.io.Serializable;
import java.util.*;

/**
 * com.lordjoe.distributed.hydra.scoring.SparkMapReduceScoringHandler
 * User: Steve
 * Date: 10/7/2014
 */
public class SparkMapReduceScoringHandler implements Serializable {

    private XTandemMain application;

    private final JXTandemStatistics m_Statistics = new JXTandemStatistics();
    private final SparkMapReduce<String, IMeasuredSpectrum, String, IMeasuredSpectrum, String, IScoredScan> handler;
    private Map<Integer, Integer> sizes;
    private final BinChargeMapper binMapper;
    private transient Scorer scorer;
    private transient ITandemScoringAlgorithm algorithm;


    public SparkMapReduceScoringHandler(String congiguration) {

        SparkUtilities.setAppName("SparkMapReduceScoringHandler");

        InputStream is = SparkUtilities.readFrom(congiguration);

        application = new SparkXTandemMain(is, congiguration);

        handler = new SparkMapReduce("Score Scans", new ScanTagMapperFunction(application), new ScoringReducer(application));

        SparkConf sparkConf = SparkUtilities.getCurrentContext().getConf();
        /**
         * copy application parameters to spark context
         */
        for (String key : application.getParameterKeys()) {
            sparkConf.set(key, application.getParameter(key));
        }

        ITaxonomy taxonomy = application.getTaxonomy();
        System.err.println(taxonomy.getOrganism());

        binMapper = new BinChargeMapper(this);

    }


//    public IFileSystem getAccessor() {
//
//        return accessor;
//    }

    public Scorer getScorer() {
        if (scorer == null)
            scorer = getApplication().getScoreRunner();

        return scorer;
    }


    public ITandemScoringAlgorithm getAlgorithm() {
        if (algorithm == null)
            algorithm = application.getAlgorithms()[0];

        return algorithm;
    }

    public JXTandemStatistics getStatistics() {
        return m_Statistics;
    }

    public SparkMapReduce<String, IMeasuredSpectrum, String, IMeasuredSpectrum, String, IScoredScan> getHandler() {
        return handler;
    }


    public JavaRDD<KeyValueObject<String, IScoredScan>> getOutput() {
        JavaRDD<KeyValueObject<String, IScoredScan>> output = handler.getOutput();
        return output;
    }


    /**
     * read any cached database parameters
     *
     * @param context     !null context
     * @param application !null application
     * @return possibly null descripotion - null is unreadable
     */
    public DigesterDescription readDigesterDescription(XTandemMain application) {
        try {
            String paramsFile = application.getDatabaseName() + ".params";
            InputStream fsin = SparkHydraUtilities.nameToInputStream(paramsFile, application);
            if (fsin == null)
                return null;
            DigesterDescription ret = new DigesterDescription(fsin);
            return ret;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;

        }
    }

    public boolean isDatabaseBuildRequired() {
        XTandemMain application = getApplication();
        boolean buildDatabase;
        // Validate build parameters
        DigesterDescription existingDatabaseParameters = null;
        try {
            existingDatabaseParameters = readDigesterDescription(application);
        }
        catch (Exception e) {
            return true; // bad descriptor
        }
        // we have a database
        if (existingDatabaseParameters != null) {
            DigesterDescription desired = DigesterDescription.fromApplication(application);
            if (desired.equivalent(existingDatabaseParameters)) {
                buildDatabase = false;
            }
            else {
                buildDatabase = true;
                // kill the database
                Path dpath = XTandemHadoopUtilities.getRelativePath(application.getDatabaseName());
                IFileSystem fileSystem = SparkUtilities.getHadoopFileSystem();
                fileSystem.expunge(dpath.toString());
            }

        }
        else {
            return true;
        }
        Configuration configuration = SparkUtilities.getCurrentContext().hadoopConfiguration();
        Map<Integer, Integer> sizeMap = XTandemHadoopUtilities.guaranteeDatabaseSizes(application, configuration);
        if (sizeMap == null) {
            return true;
        }
        JXTandemStatistics statistics = getStatistics();
        long totalFragments = XTandemHadoopUtilities.sumDatabaseSizes(sizeMap);
        if (totalFragments < 1) {
            return true;
        }

//        long MaxFragments = XTandemHadoopUtilities.maxDatabaseSizes(sizeMap);
//        statistics.setData("Total Fragments", Long.toString(totalFragments));
//        statistics.setData("Max Mass Fragments", Long.toString(MaxFragments));

        return buildDatabase;
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
        System.err.println("setup Done");
        handler.performSingleReturnMapReduce(pInputs);
        System.err.println("Map Reduce Done");
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
        JavaSparkContext ctx = SparkUtilities.getCurrentContext();
        ((AbstractTandemFunction) handler.getMap()).setup(ctx);
        ((AbstractTandemFunction) handler.getReduce()).setup(ctx);
        System.err.println("Setup Performed");
    }

    public void buildLibraryIfNeeded() {
        boolean dbNeedes = isDatabaseBuildRequired();
        if (false && !dbNeedes)
            return;
        buildLibrary();
    }

    public JavaPairRDD<BinChargeKey, IMeasuredSpectrum> mapMeasuredSpectrumToKeys(JavaRDD<IMeasuredSpectrum> inp) {
        return binMapper.mapMeasuredSpectrumToKeys(inp);
    }

    public JavaPairRDD<BinChargeKey, IPolypeptide> mapFragmentsToKeys(JavaRDD<IPolypeptide> inp) {
        return binMapper.mapFragmentsToKeys(inp);
    }


    public JavaPairRDD<IMeasuredSpectrum, IScoredScan> scoreBinPairs(JavaPairRDD<BinChargeKey, Tuple2<IMeasuredSpectrum, IPolypeptide>> binPairs) {

        JavaRDD<Tuple2<IMeasuredSpectrum, IPolypeptide>> values = binPairs.values();

        // next line is for debugging
        //values = SparkUtilities.realizeAndReturn(values);

        JavaPairRDD<IMeasuredSpectrum, IPolypeptide> valuePairs = SparkUtilities.mapToPairs(values);

       // next line is for debugging
       // valuePairs = SparkUtilities.realizeAndReturn(valuePairs);


        JavaPairRDD<IMeasuredSpectrum, Tuple2<IMeasuredSpectrum, IPolypeptide>> keyedScoringPairs = SparkUtilities.mapToKeyedPairs(valuePairs);

       /// next line is for debugging
        //keyedScoringPairs = SparkUtilities.realizeAndReturn(keyedScoringPairs);


        JavaPairRDD<IMeasuredSpectrum, IScoredScan> ret = keyedScoringPairs.combineByKey(
                new Function<Tuple2<IMeasuredSpectrum, IPolypeptide>, IScoredScan>() {
                    @Override
                    public IScoredScan call(final Tuple2<IMeasuredSpectrum, IPolypeptide> v1) throws Exception {
                        IMeasuredSpectrum spec = v1._1();
                        IPolypeptide pp = v1._2();
                        if (!(spec instanceof RawPeptideScan))
                            throw new IllegalStateException("We can only handle RawScans Here");
                        RawPeptideScan rs = (RawPeptideScan) spec;
                        IScoredScan ret = scoreOnePeptide(rs, pp);
                        return ret;
                    }
                },
                new Function2<IScoredScan, Tuple2<IMeasuredSpectrum, IPolypeptide>, IScoredScan>() {
                    @Override
                    public IScoredScan call(final IScoredScan v1, final Tuple2<IMeasuredSpectrum, IPolypeptide> v2) throws Exception {
                        IMeasuredSpectrum spec = v2._1();
                          IPolypeptide pp = v2._2();
                        if (!(spec instanceof RawPeptideScan))
                                    throw new IllegalStateException("We can only handle RawScans Here");
                             RawPeptideScan rs = (RawPeptideScan) spec;
                        IScoredScan ret = scoreOnePeptide(rs, pp);
                        if(v1.getBestMatch() == null)
                            return ret;
                        if(ret.getBestMatch() == null)
                              return v1;


                        if (v1.getExpectedValue() > ret.getExpectedValue())
                            return v1;
                        else
                            return ret;
                    }
                },
                new Function2<IScoredScan, IScoredScan, IScoredScan>() {
                    @Override
                    public IScoredScan call(final IScoredScan v1, final IScoredScan v2) throws Exception {
                        if (v1.getExpectedValue() > v2.getExpectedValue())
                            return v1;
                        else
                            return v2;
                    }
                }

        );

        return ret;
    }

    protected IScoredScan scoreOnePeptide(RawPeptideScan spec, IPolypeptide pp) {
        IPolypeptide[] pps = {pp};
        Scorer scorer1 = getScorer();
        ITandemScoringAlgorithm algorithm1 = getAlgorithm();
        return algorithm1.handleScan(scorer1, spec, pps);
    }

    public Map<Integer, Integer> getDatabaseSizes() {
        if (sizes == null) {
            LibraryBuilder libraryBuilder = new LibraryBuilder(this);
            sizes = libraryBuilder.getDatabaseSizes();
        }
        return sizes;

    }

    public JavaRDD<IPolypeptide> buildLibrary() {
        //clearAllParams(getApplication());

        LibraryBuilder libraryBuilder = new LibraryBuilder(this);
        JavaSparkContext ctx = SparkUtilities.getCurrentContext();
        return libraryBuilder.buildLibrary();
    }

//    protected void clearAllParams(XTandemMain application) {
//        String databaseName = application.getDatabaseName();
//        String paramsFile = databaseName + ".params";
//        Path dd = XTandemHadoopUtilities.getRelativePath(paramsFile);
//        IFileSystem fs = getAccessor();
//        String hdfsPath = dd.toString();
//        if (fs.exists(hdfsPath))
//            fs.deleteFile(hdfsPath);
//
//    }


}