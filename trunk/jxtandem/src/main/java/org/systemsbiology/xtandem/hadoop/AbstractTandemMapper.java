package org.systemsbiology.xtandem.hadoop;

import com.lordjoe.utilities.*;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.systemsbiology.hadoop.*;
import org.systemsbiology.xtandem.*;

import java.io.*;
import java.util.*;

/**
 * org.systemsbiology.xtandem.hadoop.AbstractTandemReducer
 * User: steven
 * Date: 9/12/11
 */
public abstract class AbstractTandemMapper<T> extends Mapper<T, Text, Text, Text> {
    public static final boolean WRITING_PARAMETERS = true;

    private HadoopTandemMain m_Application;
    private final Text m_OnlyKey = new Text();
    private final Text m_OnlyValue = new Text();
    private long m_MinimumFreeMemory = Long.MAX_VALUE;
    private final ElapsedTimer m_Elapsed = new ElapsedTimer();
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private final List<ITandemScoringAlgorithm> m_Algorithms = new ArrayList<ITandemScoringAlgorithm>();
    private Context m_Context;

    public final Context getContext() {
        return m_Context;
    }

    @Override
    protected void setup(final Context context) throws IOException, InterruptedException {
        super.setup(context);

        m_Context = context;


        // This allows non-hadoop code to report progress
        ProgressManager.INSTANCE.addProgressHandler(new HadoopProgressManager(context));

        // read configuration lines
        Configuration conf = context.getConfiguration();

         // debugging code to show my keys
        if(false)  {
            Iterator<Map.Entry<String, String>> iterator = conf.iterator();
            while(iterator.hasNext())   {
                Map.Entry<String, String> next = iterator.next();
                String key = next.getKey();
             //   if(key.startsWith("org.systemsbiology")) {
                    System.err.println(key + "=" + next.getValue());
             //`   }
            }
            System.err.println("Done showing my keys");

        }


        String defaultPath = conf.get(XTandemHadoopUtilities.PATH_KEY);
        if(defaultPath != null)
            XTandemHadoopUtilities.setDefaultPath(defaultPath);
        String forcePathPrefix = conf.get(XTandemHadoopUtilities.FORCE_PATH_PREFIX_KEY);
        if(forcePathPrefix != null)
            XTandemMain.setRequiredPathPrefix(forcePathPrefix);

        if(defaultPath != null && defaultPath.startsWith("s3n://"))  {
            try {
                Class cls = Class.forName("org.systemsbiology.aws.AWSUtilities");
                IConfigureFileSystem cfg = (IConfigureFileSystem)cls.getField("AWS_CONFIGURE_FILE_SYSTEM").get(null);
                cfg.configureFileSystem(conf,defaultPath);
            }
            catch ( Exception e) {
                throw new RuntimeException(e);

            }
           }



        IAnalysisParameters ap = AnalysisParameters.getInstance();
        ap.setJobName(context.getJobName());


        // m_Factory.setValidationStringency(SAMFileReader.ValidationStringency.LENIENT)

        m_Application = XTandemHadoopUtilities.loadFromContext(context);
         String parameter = m_Application.getParameter(JXTandemLauncher.ALGORITHMS_PROPERTY);
        if (parameter != null)
            addAlternateParameters(parameter, m_Application);


        if (WRITING_PARAMETERS) {
              HadoopTandemMain application = getApplication();


              String[] keys = application.getParameterKeys();
              for (String key : keys) {
                  if (key.startsWith("org.")) {
                      System.err.println(key + " = " + application.getParameter(key));
                  }
              }
          }

        System.err.println("Default path is " + defaultPath);
    }

    protected void addAlternateParameters(final String pParameter, IMainData params) {
        String[] items = pParameter.split(";");
        for (int i = 0; i < items.length; i++) {
            String item = items[i];
            addAlternateParameter(item, params);
        }
    }

    protected void addAlternateParameter(final String pItem, IMainData params) {

        try {
            Class<?> cls = Class.forName(pItem);
            ITandemScoringAlgorithm algorithm = (ITandemScoringAlgorithm) cls.newInstance();
            algorithm.configure(params);
            m_Algorithms.add(algorithm);
        }
        catch (RuntimeException e) {
            throw e;

        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    protected long setMinimalFree() {
        long freemem = Runtime.getRuntime().freeMemory();
        setMinimumFreeMemory(Math.min(freemem, getMinimumFreeMemory()));
        return freemem;
    }


    public ElapsedTimer getElapsed() {
        return m_Elapsed;
    }

    public long getMinimumFreeMemory() {
        return m_MinimumFreeMemory;
    }

    public void setMinimumFreeMemory(final long pMinimumFreeMemory) {
        m_MinimumFreeMemory = pMinimumFreeMemory;
    }

    public final HadoopTandemMain getApplication() {
        return m_Application;
    }

    public final Text getOnlyKey() {
        return m_OnlyKey;
    }

    public final Text getOnlyValue() {
        return m_OnlyValue;
    }

    /**
     * Called once at the end of the task.
     */
    @Override
    protected void cleanup(final Context context) throws IOException, InterruptedException {
        super.cleanup(context);
    }

}
