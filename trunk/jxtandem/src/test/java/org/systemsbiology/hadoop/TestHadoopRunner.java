package org.systemsbiology.hadoop;

import org.apache.hadoop.conf.*;
import org.junit.*;
import org.systemsbiology.hadoopgenerated.*;
import org.systemsbiology.remotecontrol.*;
import org.systemsbiology.xtandem.*;
import org.systemsbiology.xtandem.hadoop.*;

import java.io.*;
import java.text.*;
import java.util.*;

/**
 * org.systemsbiology.hadoop.TestHadoopRunner
 * User: steven
 * Date: 5/31/11
 */
public class TestHadoopRunner {
    public static final TestHadoopRunner[] EMPTY_ARRAY = {};

    public static final String[] EXCLUDED_LIBRARIES =
            {
                    "junit-rt.jar",
                    "commons-pool-1.3.jar",
                    "mysql-connector-java-5.0.4.jar",
                    "commons-dbcp-1.2.2.jar",
                    "jsch-0.1.44-1.jar",
                    "spring-jdbc-2.5.6.jar",
                    "spring-beans-2.5.6.jar",
                    "spring-core-2.5.6.jar",
                    "spring-context-2.5.6.jar",
                    "aopalliance-1.0.jar",
                    "spring-tx-2.5.6.jar",
                    "spring-test-2.5.4.jar",
            };

    @Test
    public void testReadConfiguration() throws Exception {
        RemoteHadoopConfiguration rc = new RemoteHadoopConfiguration();
        Configuration conf = rc.getConf();

        String s = conf.get("fs.default.name");
        Assert.assertNotNull(s);
    }

    @Test
    public void testRemoteRun() throws Exception {
        if(true)
            return;  // this takes too long
         for (int i = 0; i < EXCLUDED_LIBRARIES.length; i++) {
            HadoopDeployer.addExcludedLibrary(EXCLUDED_LIBRARIES[i]);

        }
         RemoteHadoopConfiguration rc = new RemoteHadoopConfiguration();
        Configuration conf = rc.getConf();
        String host = RemoteUtilities.getHost();
        int port = RemoteUtilities.getPort();
        String jobtracker =  RemoteUtilities.getJobTracker();
        conf.set("fs.default.name", "hdfs://" + host + ":" + port + "/");
        conf.set("mapred.job.tracker", jobtracker);

        String name = new SimpleDateFormat("MMMddHHmm").format(new Date());
        String jarFile = "MyJar" + name + ".jar";
        HadoopDeployer.makeHadoopJar(jarFile);
        conf.set("mapred.jar", jarFile);

        String[] args = {"TestOut"};
        IHDFSFileSystem hfs = null;
        try {
            hfs = HDFSAccessor.getFileSystem();
        }
        catch (Exception e) {
              Throwable cause = XTandemUtilities.getUltimateCause(e);
              if (cause instanceof EOFException) {   // hdfs not available
                  return;
              }
              throw new RuntimeException(e);

          }
        hfs.expunge("TestOut");

        throw new UnsupportedOperationException("Fix This"); // ToDo
//        NShotTest.runNShotTest(conf, args);
//
//        String output = hfs.readFromFileSystem("TestOut/part-r-00000");
//        String[] split = output.split("\n");
//        Assert.assertEquals(24,split.length);
//        Assert.assertEquals("foo0\tbar0",split[4]);

    }


}
