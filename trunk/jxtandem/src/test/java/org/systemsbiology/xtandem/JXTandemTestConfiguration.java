package org.systemsbiology.xtandem;

import org.apache.hadoop.conf.*;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.security.*;
import org.systemsbiology.hadoop.*;
import org.systemsbiology.remotecontrol.*;
import org.systemsbiology.xtandem.taxonomy.*;

import java.io.*;
import java.security.*;
import java.util.*;

/**
 * org.systemsbiology.xtandem.JXTandemTestConfiguration
 * manages whether tests can run on the basis of access to resources such as
 * the cluster, hdfs, databases ...
 * User: Steve
 * Date: 8/25/11
 */
public class JXTandemTestConfiguration {
    public static final JXTandemTestConfiguration[] EMPTY_ARRAY = {};

    private static Map<String, Boolean> gConditionToAvailability = new HashMap<String, Boolean>();

    public static boolean isHDFSAccessible() {

        IHDFSFileSystem access = null;
        final String host = RemoteUtilities.getHost();
        final int port = RemoteUtilities.getPort();
        final String user = RemoteUtilities.getUser();

        access = HDFSAccessor.getFileSystem(host,port) ;
        return true;

//        //     RemoteUtilities.getPassword()
//        String connStr = host + ":" + port + ":" + user + ":" + RemoteUtilities.getPassword();
//        // do we already know
//        Boolean ret = gConditionToAvailability.get(connStr);
//        if (ret == null) {
//
//        final String userDir =  "/user/" + user;
//        try {
//            UserGroupInformation ugi
//                    = UserGroupInformation.createRemoteUser(user);
//
//            ugi.doAs(new PrivilegedExceptionAction<Void>() {
//
//                public Void run() throws Exception {
//
//                    Configuration conf = new Configuration();
//                    conf.set("fs.defaultFS", "hdfs://" + host + ":" + port + userDir);
//                    conf.set("hadoop.job.ugi", user);
//
//                    FileSystem fs = FileSystem.get(conf);
//
////                    fs.createNewFile(new Path(userDir + "/test"));
////
////                    FileStatus[] status = fs.listStatus(new Path("/user/" + user));
////                    for (int i = 0; i < status.length; i++) {
////                        System.out.println(status[i].getPath());
////                    }
//                    return null;
//
//                }
//            });
//            ret = true;
//             gConditionToAvailability.put(connStr,ret);
//        } catch (Exception e) {
//             ret = false;
//            gConditionToAvailability.put(connStr,ret);
//
//        }
//         gConditionToAvailability.put(connStr,Boolean.TRUE);
//
//        }
//        return ret;
//        // never get here
//
//        UserGroupInformation currentUser = null;
//        try {
//            Configuration conf = HDFSAccessor.getSharedConfiguration();
//            UserGroupInformation.setConfiguration(conf);
//            File keyTab = RemoteUtilities.getKeyTabFile();
//            String canonicalPath = keyTab.getCanonicalPath();
//            SecurityUtil.login(conf, canonicalPath, "dfs.namenode.kerberos.principal");
//            currentUser = UserGroupInformation.getCurrentUser();
//
//            //    if(kt != null)
//            //         UserGroupInformation.loginUserFromKeytab(user,kt.getPath());
//            currentUser = UserGroupInformation.getCurrentUser();
//        } catch (IOException e) {
//            throw new UnsupportedOperationException(e);
//        }
//
//          if (ret == null) {
//            try {
//                access = HDFSAccessor.getFileSystem(host, port);
//                ret = Boolean.TRUE;
//            } catch (Exception e) {
//                ret = Boolean.FALSE;
//            }
//
////             try {
////                 new FTPWrapper(RemoteUtilities.getUser(), RemoteUtilities.getPassword(), RemoteUtilities.getHost());
////                 ret = Boolean.TRUE;
////             }
////             catch (Exception e) {
////                 ret = Boolean.FALSE;
////
////             }
//            gConditionToAvailability.put(connStr, ret);
//        }
//        return ret;
    }


//    public static boolean isDatabaseAccessible(IParameterHolder data) {
//        String connStr = SpringJDBCUtilities.buildConnectionString(data);
//        Boolean ret = gConditionToAvailability.get(connStr);
//        if (ret == null) {
//            try {
//                SpringJDBCUtilities.buildDataSource(data);
//                ret = Boolean.TRUE;
//            }
//            catch (Exception e) {
//                ret = Boolean.FALSE;
//
//            }
//            gConditionToAvailability.put(connStr, ret);
//        }
//        return ret;
//    }

}
