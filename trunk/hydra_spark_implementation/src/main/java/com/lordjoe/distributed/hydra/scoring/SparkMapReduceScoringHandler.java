package com.lordjoe.distributed.hydra.scoring;

import com.lordjoe.distributed.*;
import com.lordjoe.distributed.context.*;
import com.lordjoe.distributed.tandem.*;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.*;
import org.apache.spark.*;
import org.apache.spark.api.java.*;
import org.systemsbiology.common.*;
import org.systemsbiology.remotecontrol.*;
import org.systemsbiology.xtandem.*;
import org.systemsbiology.xtandem.hadoop.*;
import org.systemsbiology.xtandem.peptide.*;
import org.systemsbiology.xtandem.scoring.*;

import java.io.*;
import java.util.*;

/**
 * com.lordjoe.distributed.hydra.scoring.SparkMapReduceScoringHandler
 * User: Steve
 * Date: 10/7/2014
 */
public class SparkMapReduceScoringHandler {

    private final SparkApplicationContext context;
    private XTandemMain application;
    private IFileSystem accessor = new LocalMachineFileSystem();

    private final JXTandemStatistics m_Statistics = new JXTandemStatistics();
    private final SparkMapReduce<String, IMeasuredSpectrum, String, IMeasuredSpectrum, String, IScoredScan> handler;

    public SparkMapReduceScoringHandler( Properties sparkProperties,File congiguration) {
        context = new SparkApplicationContext("LibraryBuilder");
        SparkConf sparkConf = context.getSparkConf();
        SparkUtilities.guaranteeSparkMaster(sparkConf,sparkProperties);    // use local if no master provided
        // needed to fix norway bug

        application = new XTandemMain(congiguration);

        handler = new SparkMapReduce("Score Scans", new ScanTagMapperFunction(application), new ScoringReducer(application));

        /**
         * copy application parameters to spark context
         */
        for (String key : application.getParameterKeys()) {
            sparkConf.set(key,application.getParameter(key) );
        }

    }

    public SparkApplicationContext getContext() {
        return context;
    }

    public IFileSystem getAccessor() {
        return accessor;
    }

    @SuppressWarnings("UnusedDeclaration")
    public JavaSparkContext getJavaContext() {
        SparkApplicationContext context1 = getContext();
        return context1.getCtx();
    }

      public Configuration getHadoopConfiguration() {
        return getJavaContext().hadoopConfiguration();
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
            Path dd = XTandemHadoopUtilities.getRelativePath(paramsFile);
            File hdfsPath = new File(dd.toString());
             if (!hdfsPath.exists( ))
                return null;


            InputStream fsin = new FileInputStream(hdfsPath);


            DigesterDescription ret = new DigesterDescription(fsin);
            return ret;
        } catch (Exception e) {
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
          } catch (Exception e) {
              return true; // bad descriptor
          }
          // we have a database
          if (existingDatabaseParameters != null) {
              DigesterDescription desired = DigesterDescription.fromApplication(application);
              if (desired.equivalent(existingDatabaseParameters)) {
                  buildDatabase = false;
              } else {
                  buildDatabase = true;
                  // kill the database
                  Path dpath = XTandemHadoopUtilities.getRelativePath(application.getDatabaseName());
                  try {
                      FileSystem fileSystem = dpath.getFileSystem(getHadoopConfiguration());
                      XTandemHadoopUtilities.expunge(dpath, fileSystem);
                  } catch (IOException e) {
                      throw new RuntimeException(e);

                  }
              }

          } else {
              buildDatabase = true;
          }
          Map<Integer, Integer> sizeMap = XTandemHadoopUtilities.guaranteeDatabaseSizes(application,getHadoopConfiguration());
          if (sizeMap == null) {
              buildDatabase = true;
          }
          JXTandemStatistics statistics = getStatistics();
          long totalFragments = XTandemHadoopUtilities.sumDatabaseSizes(sizeMap);
          if (totalFragments < 1) {
              buildDatabase = true;
          }

          long MaxFragments = XTandemHadoopUtilities.maxDatabaseSizes(sizeMap);
          statistics.setData("Total Fragments", Long.toString(totalFragments));
          statistics.setData("Max Mass Fragments", Long.toString(MaxFragments));

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

    public  void buildLibraryIfNeeded() {
          if(false && !isDatabaseBuildRequired())   // todo for now we force a library build
              return;
          buildLibrary();
    }

    public void buildLibrary() {
        clearAllParams(getApplication());

        LibraryBuilder libraryBuilder = new LibraryBuilder(this);
        libraryBuilder.buildLibrary();

        Map<Integer, Integer> sizes = XTandemHadoopUtilities.guaranteeDatabaseSizes(getApplication(),getHadoopConfiguration());

   //     throw new UnsupportedOperationException("Fix This"); // ToDo
    }

    protected void clearAllParams(XTandemMain application) {
         String databaseName = application.getDatabaseName();
         String paramsFile = databaseName + ".params";
         Path dd = XTandemHadoopUtilities.getRelativePath(paramsFile);
         IFileSystem fs = getAccessor();
         String hdfsPath = dd.toString();
         if (fs.exists(hdfsPath))
             fs.deleteFile(hdfsPath);

     }

}