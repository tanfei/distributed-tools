package org.systemsbiology.xtandem;

import com.lordjoe.utilities.*;
import org.systemsbiology.hadoop.*;
import org.systemsbiology.xtandem.hadoop.*;

import java.io.*;
import java.util.*;

/**
 * org.systemsbiology.xtandem.JXTandemDeployer
 * User: steven
 * Date: 10/18/11
 */
public class JXTandemDeployer extends Deployer {
    public static final JXTandemDeployer[] EMPTY_ARRAY = {};


    public static final String[] EXCLUDED_LIBRARIES =
            {
                    "openjpa-persistence-2.0.0.jar",
                    "openjpa-kernel-2.0.0.jar",
                    "openjpa-lib-2.0.0.jar",
                    //            "commons-logging-1.1.1.jar",
                    //            "commons-lang-2.1.jar",
                    //            "commons-collections-3.2.1.jar",
                    "serp-1.13.1.jar",
                    "geronimo-jms_1.1_spec-1.1.1.jar",
                    "geronimo-jta_1.1_spec-1.1.1.jar",
                    "commons-pool-1.3.jar",
                    "geronimo-jpa_2.0_spec-1.0.jar",
                    "mysql-connector-java-5.0.4.jar",
                    //            "commons-dbcp-1.2.2.jar",
                    //            "commons-cli-1.2.jar",
                    //            "jsch-0.1.44-1.jar",
                    //            "hadoop-core-0.20.2.jar",
                    //             "xmlenc-0.52.jar",
                    //            "commons-httpclient-3.0.1.jar",
                    //             "commons-codec-1.3.jar",
                    //            "commons-net-1.4.1.jar",
                    "oro-2.0.8.jar",
                    "jetty-6.1.25.jar",
                    "jetty-util-6.1.14.jar",
                    "servlet-api-2.5-6.1.14.jar",
                    "jasper-runtime-5.5.12.jar",
                    "jasper-compiler-5.5.12.jar",
                    "jsp-api-2.1-6.1.14.jar",
                    "jsp-2.1-6.1.14.jar",
                    //           "core-3.1.1.jar",
                    "ant-1.6.5.jar",
                    //           "commons-el-1.0.jar",
                    "jets3t-0.7.1.jar",
                    "kfs-0.3.jar",
                    "hsqldb-1.8.0.10.jar",
                    "servlet-api-2.5-20081211.jar",
                    "slf4j-log4j12-1.4.3.jar",
                    "slf4j-api-1.4.3.jar",
                    //          "log4j-1.2.9.jar",
                    "xml-apis-1.0.b2.jar",
                    "xml-apis-ext-1.3.04.jar",
                    "spring-jdbc-2.5.6.jar",
                    //          "spring-beans-2.5.6.jar",
                    //         "spring-core-2.5.6.jar",
                    //         "spring-context-2.5.6.jar",
                    //         "aopalliance-1.0.jar",
                    //        "spring-tx-2.5.6.jar",

            };

    public JXTandemDeployer() {
        super();
    }

    @Override
    protected void buildCommandLine(final Class mainClass, final String[] args, final StringBuilder pSb) {
        if (isQuiet()) {
            pSb.append(/* "jre" + WINDOWS_DIRECTORY_SEPARATOR + "bin" + WINDOWS_DIRECTORY_SEPARATOR + */ "javaw ");
        } else {
            pSb.append(/* "jre\\bin\\" + */ "java ");
        }
        pSb.append(" -Xmx1024m -Xms128m -cp %q4path% " + mainClass.getName() + " ");
        for (int i = 2; i < args.length; i++) {
            pSb.append(" " + args[i]);
        }
        if (mainClass == JXTandemLauncher.class) {
            pSb.append(" config=%HYDRA_HOME%/data/Launcher.properties jar=%HYDRA_HOME%/data/Hydra.jar ");
            pSb.append("params=%1 %2 %3 %4 \n");
        } else
            pSb.append("%1 %2 %3 %4 \n");

    }

    /**
     * jar all directories into one big jar called Target.jar
     *
     * @param libDir
     * @param jarDirectories
     * @param holder
     */
    @Override
    protected void makeJars(File libDir, Collection<File> jarDirectories, Collection<File> holder) {
        //noinspection SimplifiableIfStatement,PointlessBooleanExpression,ConstantConditions,RedundantIfStatement
        if(jarDirectories.size() != 1 && false)     // todo put back
            throw new IllegalStateException("all modules need to compile to a single jar!!!");
        super.makeJars(libDir,  jarDirectories,  holder);
    //    File jarFile = new File(libDir, "Target.jar");
   //     makeOneJar(jarDirectories, holder, jarFile);
    }

    @Override
    protected String buildShellCommandLine(final Class mainClass, final String[] args) {
        StringBuilder pSb = new StringBuilder();

        pSb.append("java ");
        pSb.append(" -Xmx1024m   ");
        pSb.append(" -cp $q4path " + mainClass.getName() + " ");
        for (int i = 2; i < args.length; i++) {
            pSb.append(" " + args[i].replace('%', '$'));
        }
        if (mainClass == JXTandemLauncher.class) {
            pSb.append("config=$HYDRA_HOME/data/Launcher.properties jar=$HYDRA_HOME/data/Hydra.jar  ");
            pSb.append("params=$1 $2 $3 $4 $5 $6 $7 $8\n");
        } else
            pSb.append("$1 $2 $3 $4 $5 $6 $7 $8\n");

        return pSb.toString();
    }


    public void makeTestRunners(File[] pathLibs, File deployDir, final String commandName, String[] commandText) {
        File binDir = new File(deployDir, "bin");
        File rb = new File(binDir, commandName + ".bat");
        String bf = commandText[0];
        writeFile(rb, bf);
        File rs = new File(binDir, commandName + ".sh");
        String sf = commandText[0];
        writeFile(rs, sf);

    }


    @Override
    public String[] makeRunners(File[] pathLibs, File deployDir, Class mainClass, String commandName, String[] args) {
        String[] text = super.makeRunners(pathLibs, deployDir, mainClass, commandName, args);
        makeTestRunners(pathLibs, deployDir, "Nshot", text);
        return text;
    }

    @Override
    public void deploy(final File pDeployDir, final Class mainClass, final String[] pRightArgs) {
        super.deploy(pDeployDir, mainClass, pRightArgs);
        String deployPath = pDeployDir.getAbsolutePath();
        File[] data = new File("installer").listFiles();
        if (data == null)
            throw new IllegalStateException("installer must be a subdirectory of user.dir and hold Launcher.properties");
        File datadir = new File(pDeployDir, "data");
        //noinspection ResultOfMethodCallIgnored
        datadir.mkdirs();
        for (int i = 0; i < data.length; i++) {
            File file = data[i];
            FileUtilities.copyFile(file, new File(datadir, file.getName()));
        }
        HadoopDeployer.makeHadoopJar(datadir.getAbsolutePath() + "/Hydra.jar");
    }

    private static void usage() {
        System.out.println("Usage: <DeployDirectory> ");
    }

    public static void main(String[] args) {

        if (args.length == 0) {
            usage();
            return;
        }
        JXTandemDeployer depl = new JXTandemDeployer();
        depl.clearTaskExcludeJars();
        for (int i = 0; i < EXCLUDED_LIBRARIES.length; i++) {
            String arg = EXCLUDED_LIBRARIES[i];
            depl.addTaskExcludeJar(arg);
        }
        //   testRegex();
        String[] rightArgs;
        String[] otherArgs = new String[0];
        JXTandemDeployer d = new JXTandemDeployer();
        if ("-q".equals(args[0])) {
            d.setQuiet(true);
            rightArgs = new String[args.length - 1];
            System.arraycopy(args, 1, rightArgs, 0, args.length - 1);
        } else {
            rightArgs = args;
        }
        if (rightArgs.length > 0) {
            otherArgs = new String[rightArgs.length - 1];
            System.arraycopy(rightArgs, 1, otherArgs, 0, rightArgs.length - 1);
        }

        File deployDir = new File(args[0]);
        Class mainClass = JXTandemLauncher.class;
        d.deploy(deployDir, mainClass, otherArgs);

    }
}
