package org.systemsbiology.hadoop;

import org.systemsbiology.remotecontrol.*;

/**
 * +++++++++++++++++++++++++++++++++++++++++++
 * Note under Hadoop 0.2 it is OK to exclude this class from compilation - it is only used
 * with reflection
 * +++++++++++++++++++++++++++++++++++++++++++

/**
 * org.systemsbiology.hadoop.HDFS1_0_ONLYTests
 * User: steven
 * Date: 3/9/11
 */
public class HDFS1_0_ONLYTests {
    public static final HDFS1_0_ONLYTests[] EMPTY_ARRAY = {};

    public static final String NAME_NODE = RemoteUtilities.getHost();
    public static final int HDFS_PORT = RemoteUtilities.getPort();
    public static final String BASE_DIRECTORY = RemoteUtilities.getDefaultPath() + "/test/";
    public static final String FILE_NAME = "little_lamb2.txt";
    public static final String FILE_NAME2 = "little_lamb_stays2.txt";

  //  @Test
    public void setPermissionTest() {
        throw new UnsupportedOperationException("Fix This"); // ToDo
//        // We can tell from the code - hard wired to use security over 0.2
//        // HDFSAccessor.setHDFSHasSecurity(true);
//
//        if (!HDFWithNameAccessor.isHDFSAccessible()) {
//            System.out.println("Not running HDFS tests");
//            return;
//        }
//        try {
//            HDFWithNameAccessor access = (HDFWithNameAccessor) HDFSAccessor.getFileSystem(NAME_NODE, HDFS_PORT);
//            //     access.setPermissions(new Path("/user/slewis"),IHDFSFileSystem.FULL_ACCESS);
//
//            //       access.mkdir(BASE_DIRECTORY + "/ebi/");
//            //      access.mkdir(BASE_DIRECTORY + "/ebi/Sample2/");
//            FsPermission permissions;
//
//
//            Path src = new Path("/user/slewis/ebi");
//            access.expunge("/user/slewis/ebi");
//
//            access.mkdir("/user/slewis/ebi");
//            access.setPermissions(src, IHDFSFileSystem.FULL_ACCESS);
//            permissions = access.getPermissions(src);
//            Assert.assertTrue(HDFWithNameAccessor.canAllRead(permissions));
//
//            String filePath = RemoteUtilities.getDefaultPath() + "/ebi/Sample2/";
//            access.mkdir(filePath);
//            src = new Path(filePath);
//            permissions = access.getPermissions(src);
//
//            access.setPermissions(src, IHDFSFileSystem.FULL_ACCESS);
//            permissions = access.getPermissions(src);
//
//
//            access.writeToFileSystem(filePath, HDFSTests.TEST_CONTENT);
//            access.setPermissions(src, IHDFSFileSystem.FULL_ACCESS);
//            access.setPermissions(new Path(filePath), IHDFSFileSystem.FULL_ACCESS);
//
//            permissions = access.getPermissions("/user/slewis");
//            Assert.assertTrue(HDFWithNameAccessor.canAllRead(permissions));
//
//            permissions = access.getPermissions("/user/slewis");
//            Assert.assertTrue(HDFWithNameAccessor.canAllRead(permissions));
//
//            permissions = access.getPermissions(filePath);
//            Assert.assertTrue(HDFWithNameAccessor.canAllRead(permissions));
//        } catch (Exception e) {
//            Throwable cause = XTandemUtilities.getUltimateCause(e);
//            if (cause instanceof EOFException) {   // hdfs not available
//                return;
//            }
//            throw new RuntimeException(e);
//
//        }
    }


}
