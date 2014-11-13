package com.lordjoe.distributed.hydra.scoring;

import com.lordjoe.distributed.*;
import com.lordjoe.distributed.hydra.*;
import com.lordjoe.distributed.hydra.fragment.*;
import com.lordjoe.distributed.tandem.*;
import com.lordjoe.utilities.*;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.fs.*;
import org.apache.spark.*;
import org.apache.spark.api.java.*;
import org.apache.spark.api.java.function.*;
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


    public JavaRDD< IScoredScan> scoreBinPairs(JavaPairRDD<BinChargeKey, Tuple2<IMeasuredSpectrum, IPolypeptide>> binPairs) {
        ElapsedTimer timer = new ElapsedTimer();


        // drop the key s- we no longer ned them
        JavaRDD<Tuple2<IMeasuredSpectrum, IPolypeptide>> values = binPairs.values();

        // next line is for debugging
        //values = SparkUtilities.realizeAndReturn(values);

        // convert to a PairRDD to keep spark happy
        JavaPairRDD<IMeasuredSpectrum, IPolypeptide> valuePairs = SparkUtilities.mapToPairs(values);

        // next line is for debugging
        // valuePairs = SparkUtilities.realizeAndReturn(valuePairs);

        // bring key (original data) into value since we need to for scoring
        JavaPairRDD<IMeasuredSpectrum, Tuple2<IMeasuredSpectrum, IPolypeptide>> keyedScoringPairs = SparkUtilities.mapToKeyedPairs(
                valuePairs);

        /// next line is for debugging
        //keyedScoringPairs = SparkUtilities.realizeAndReturn(keyedScoringPairs);

        timer.showElapsed("built scoring pairs");


        JavaPairRDD<IMeasuredSpectrum, IScoredScan> scorings = keyedScoringPairs.combineByKey(
                new generateFirstScore(),
                new addNewScore(),
                new combineScoredScans(),
                SparkHydraUtilities.getMeasuredSpectrumPartitioner()
        );

        timer.showElapsed("built scorings");

      /// next line is for debugging
       // scorings = SparkUtilities.realizeAndReturn(scorings);


        JavaPairRDD<String, IScoredScan> scoreByID = scorings.mapToPair(new PairFunction<Tuple2<IMeasuredSpectrum, IScoredScan>, String, IScoredScan>() {
            @Override
            public Tuple2<String, IScoredScan> call(final Tuple2<IMeasuredSpectrum, IScoredScan> t) throws Exception {
                return new Tuple2<String, IScoredScan>(t._1().getId(), t._2());
            }
        });


        // next line is for debugging
        // scoreByID = SparkUtilities.realizeAndReturn(scoreByID);

        scoreByID = scoreByID.combineByKey(SparkUtilities.IDENTITY_FUNCTION, new combineScoredScans(),
                new combineScoredScans()
        );

        timer.showElapsed("built score by ids");


        // sort by id
        scoreByID = scoreByID.sortByKey();
        // next line is for debugging
        //scoreByID = SparkUtilities.realizeAndReturn(scoreByID);


        return scoreByID.values();
    }

    /**
     * delegate scoring to the algorithm
     *
     * @param spec
     * @param pp
     * @return
     */
    protected IScoredScan scoreOnePeptide(RawPeptideScan spec, IPolypeptide pp) {
        IPolypeptide[] pps = {pp};
        Scorer scorer1 = getScorer();
        scorer1.addPeptide(pp);
        scorer1.generateTheoreticalSpectra();
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
        return libraryBuilder.buildLibrary();
    }

    private class generateFirstScore extends AbstractLoggingFunction<Tuple2<IMeasuredSpectrum, IPolypeptide>, IScoredScan> {
        @Override
        public IScoredScan doCall(final Tuple2<IMeasuredSpectrum, IPolypeptide> v1) throws Exception {
            IMeasuredSpectrum spec = v1._1();
            IPolypeptide pp = v1._2();
            if (!(spec instanceof RawPeptideScan))
                throw new IllegalStateException("We can only handle RawScans Here");
            RawPeptideScan rs = (RawPeptideScan) spec;
            IScoredScan ret = scoreOnePeptide(rs, pp);
            return ret;
        }
    }

    private class addNewScore extends AbstractLoggingFunction2<IScoredScan, Tuple2<IMeasuredSpectrum, IPolypeptide>, IScoredScan> {
        @Override
        public IScoredScan doCall(final IScoredScan original, final Tuple2<IMeasuredSpectrum, IPolypeptide> v2) throws Exception {
            IMeasuredSpectrum spec = v2._1();
            IPolypeptide pp = v2._2();
            if (!(spec instanceof RawPeptideScan))
                throw new IllegalStateException("We can only handle RawScans Here");
            RawPeptideScan rs = (RawPeptideScan) spec;
            IScoredScan ret = scoreOnePeptide(rs, pp);
            if (original.getBestMatch() == null)
                return ret;
            if (ret.getBestMatch() == null)
                return original;


            // Add new matches - only the best will be retaind
            for (ISpectralMatch match : ret.getSpectralMatches()) {

                ((OriginatingScoredScan) original).addSpectralMatch(match);
            }
            return original;
        }
    }


    private static class combineScoredScans extends AbstractLoggingFunction2<IScoredScan, IScoredScan, IScoredScan> {
        @Override
        public IScoredScan doCall(final IScoredScan original, final IScoredScan added) throws Exception {
            if (original.getBestMatch() == null)
                return added;
            if (added.getBestMatch() == null)
                return original;

            // Add new matches - only the best will be retaind
            for (ISpectralMatch match : added.getSpectralMatches()) {
                ((OriginatingScoredScan) original).addSpectralMatch(match);
            }
            return original;
        }
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