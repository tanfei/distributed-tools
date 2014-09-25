package org.systemsbiology.xtandem.hadoop;

import org.apache.hadoop.conf.*;
import org.systemsbiology.common.*;
import org.systemsbiology.remotecontrol.*;

import java.io.*;

/**
 * org.systemsbiology.xtandem.hadoop.RemoteHadoopConfiguration
 * User: steven
 * Date: 5/31/11
 */
public class RemoteHadoopConfiguration {
    public static final RemoteHadoopConfiguration[] EMPTY_ARRAY = {};

    public static final String LOCAL_FILE = "file:///";

    private final Configuration m_Conf = new Configuration( );  // do not load

    public RemoteHadoopConfiguration() {
    }

    /**
     * return a configuration capable of running a job remotely
     *
     * @return !null configuration
     */
    public IFileSystem getRemoteFiles() {
        String user = RemoteUtilities.getUser();
        String password = RemoteUtilities.getPassword();
        String host = RemoteUtilities.getHost();

        FTPWrapper ret = new FTPWrapper(user, password, host);

        return ret;
    }


    public void copyToRemote(InputStream  is, String target) {
        IFileSystem fs =  getRemoteFiles();
        fs.writeToFileSystem(target,is);
    }

    public void copyToRemote(File thefile, String target) {
        try {
            FileInputStream is = new FileInputStream(thefile);
            copyToRemote(is,target);
        }
        catch (FileNotFoundException e) {
            throw new RuntimeException(e);

        }
    }

    /**
     * return a conficuration capable of running a job remotely
     *
     * @return !null configuration
     */
    public void buildRemoteConfiguration() {
        IFileSystem fs = getRemoteFiles();
        //noinspection UnusedDeclaration
        HadoopConfigurationPropertySet ps = new HadoopConfigurationPropertySet();
        readConfigurationFile(fs, "mapred-site.xml");
        readConfigurationFile(fs, "core-site.xml");
        readConfigurationFile(fs, "hdfs-site.xml");
    }

    protected void readConfigurationFile(IFileSystem fs, String file) {
        String home = RemoteUtilities.getProperty(RemoteUtilities.HADOOP_HOME);
        String hdfsPath = home + "/conf/" + file;

        String configString = fs.readFromFileSystem(hdfsPath);
        //noinspection deprecation
        InputStream is = new StringBufferInputStream(configString);

        HadoopConfigurationPropertySet ps = XTandemHadoopUtilities.parseHadoopProperties(is);

        for (HadoopConfigurationProperty prop : ps.getHadoopProperties()) {
            String name = prop.getName();
            String value = prop.getValue();
            m_Conf.set(name, value);
        }

    }

    public Configuration getConf() {
        if (!LOCAL_FILE.equals(m_Conf.get("fs.default.name")) )
            buildRemoteConfiguration();
        return m_Conf;
    }


}
