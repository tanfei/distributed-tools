package org.systemsbiology.hadoop;

/**
 * org.systemsbiology.hadoop.RunAsUserUseWithReflection
 *
 * @author Steve Lewis
 * @date 21/05/13
 */
public class RunAsUserUseWithReflection implements RunAsUser {
    public static RunAsUser[] EMPTY_ARRAY = {};
    public static Class THIS_CLASS = RunAsUserUseWithReflection.class;



    /**
     * run the method in the name of the user user
     * @param staticMethod
     * @param user
     * @param args
     */
    @Override
    public  void runAsUser(  final String user, final Object[] args) {
        throw new UnsupportedOperationException("Fix This"); // ToDo
//        try {
//            UserGroupInformation ugi = HDFWithNameAccessor.getCurrentUserGroup();
//            if(true)    throw new UnsupportedOperationException("Uncomment when using version 1.0.*");
////            UserGroupInformation current = UserGroupInformation.getCurrentUser();
////
////            final String[] realArgs = (String[])args[0];
////
////            ugi.doAs(new PrivilegedExceptionAction<Void>() {
////
////                public Void run() throws Exception {
////                    UserGroupInformation current = UserGroupInformation.getCurrentUser();
////                    JXTandemLauncher.workingMain(realArgs);
////                    return null;
////
////                }
////            });
//        } catch ( Exception e) {
//            throw new RuntimeException(e);
//        }
    }


}
