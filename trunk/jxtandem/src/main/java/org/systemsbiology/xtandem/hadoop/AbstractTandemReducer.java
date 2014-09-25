package org.systemsbiology.xtandem.hadoop;

import com.lordjoe.utilities.*;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.util.*;
import org.systemsbiology.hadoop.*;
import org.systemsbiology.xtandem.*;

import java.io.*;
import java.util.*;

/**
 * org.systemsbiology.xtandem.hadoop.AbstractTandemReducer
 * User: steven
 * Date: 9/12/11
 */
public abstract class AbstractTandemReducer extends Reducer<Text, Text, Text, Text> {

    public static final boolean WRITING_PARAMETERS = true;

    private HadoopTandemMain m_Application;
    private final Text m_OnlyKey = new Text();
    private final Text m_OnlyValue = new Text();
    private boolean m_AllSpecialKeysHandled;
    private long m_MinimumFreeMemory = Long.MAX_VALUE;
    private final ElapsedTimer m_Elapsed = new ElapsedTimer();
    private Context m_Context;

    public boolean isAllSpecialKeysHandled() {
        return m_AllSpecialKeysHandled;
    }

    public void setAllSpecialKeysHandled(final boolean pAllSpecialKeysHandled) {
        m_AllSpecialKeysHandled = pAllSpecialKeysHandled;
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


    protected long setMinimalFree() {
        long freemem = Runtime.getRuntime().freeMemory();
        setMinimumFreeMemory(Math.min(freemem, getMinimumFreeMemory()));
        return freemem;
    }

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

        IAnalysisParameters ap = AnalysisParameters.getInstance();
        ap.setJobName(context.getJobName());


        String defaultPath = conf.get(XTandemHadoopUtilities.PATH_KEY);
        XTandemHadoopUtilities.setDefaultPath(defaultPath);


        // sneaky trick to extract the version
        String version = VersionInfo.getVersion();
        context.getCounter("Performance", "Version-" + version).increment(1);
        // sneaky trick to extract the user
        String uname = System.getProperty("user.name");
        context.getCounter("Performance", "User-" + uname).increment(1);


        if (defaultPath.startsWith("s3n://")) {
            try {
                Class cls = Class.forName("org.systemsbiology.aws.AWSUtilities");
                IConfigureFileSystem cfg = (IConfigureFileSystem) cls.getField("AWS_CONFIGURE_FILE_SYSTEM").get(null);
                cfg.configureFileSystem(conf, defaultPath);
            } catch (Exception e) {
                throw new RuntimeException(e);

            }
        }

        // sometimes we need to add a prefix to a file
        String forcePathPrefix = conf.get(XTandemHadoopUtilities.FORCE_PATH_PREFIX_KEY);
        XTandemMain.setRequiredPathPrefix(forcePathPrefix);
        // m_Factory.setValidationStringency(SAMFileReader.ValidationStringency.LENIENT)

        m_Application = XTandemHadoopUtilities.loadFromContext(context);

        if (WRITING_PARAMETERS) {
            HadoopTandemMain application = getApplication();


            String[] keys = application.getParameterKeys();
            for (String key : keys) {
                if (key.startsWith("org.")) {
                    System.err.println(key + " = " + application.getParameter(key));
                }
            }
        }


        m_Application.loadTaxonomy();
        m_Application.loadScoring();

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


    protected boolean isKeySpecial(String s) {
        return s.startsWith("#");
    }

    @Override
    public void reduce(Text key, Iterable<Text> values,
                       Context context) throws IOException, InterruptedException {
        // keys starting with # come BEFORE ALL other keys
        String sequence = key.toString();
        // these are special and will ALL behandled FIRST
        if (sequence.startsWith("#"))
            reduceSpecial(key, values, context);
        else {
            // we will get NO MORE special keys
            setAllSpecialKeysHandled(true);
            reduceNormal(key, values, context);
        }
    }

    /**
     * Called once at the end of the task.
     */
    @Override
    protected void cleanup(final Context context) throws IOException, InterruptedException {
        super.cleanup(context);
    }

    protected abstract void reduceNormal(Text key, Iterable<Text> values,
                                         Context context) throws IOException, InterruptedException;

    protected void reduceSpecial(Text key, Iterable<Text> values,
                                 Context context) throws IOException, InterruptedException {
        // Handle special early keys
    }
}
