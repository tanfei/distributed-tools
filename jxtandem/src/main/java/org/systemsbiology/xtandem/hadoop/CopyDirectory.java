package org.systemsbiology.xtandem.hadoop;

import org.systemsbiology.common.*;
import org.systemsbiology.hadoop.*;

import java.io.*;

/**
 * org.systemsbiology.xtandem.hadoop.CopyDirectory
 *  deep directory copy from hdfs to a locat directory
 * User: steven
 * Date: 11/16/11
 */
public class CopyDirectory {
    public static final CopyDirectory[] EMPTY_ARRAY = {};

    public static void copyDirectory(IFileSystem fs, String path, File localFile) {
        localFile.mkdirs();
        if (!fs.exists(path))
            throw new IllegalArgumentException("remote path " + path + " does not exist"); // ToDo change
        if (!fs.isDirectory(path))
            throw new IllegalArgumentException("remote path " + path + " is not a directory"); // ToDo change
        if (!localFile.exists())
            throw new IllegalArgumentException("local path " + localFile + " does not exist"); // ToDo change      \
        String[] files = fs.ls(path);
        for (int i = 0; i < files.length; i++) {
            String file = files[i];
            String filePath = path + "/" + file;
            File localPath = new File(localFile, file);
            generalCopy(fs, filePath, localPath);
            if(i % 100 == 0)
                 System.out.print(".");
            if(i % 4000 == 0)
                 System.out.println( );
         }
        System.out.println();
    }

    public static void generalCopy(final IFileSystem fs, final String pFilePath, final File pLocalPath) {
        if (fs.isFile(pFilePath)) {
            fs.copyFromFileSystem(pFilePath, pLocalPath);
        }
        else {
            copyDirectory(fs, pFilePath, pLocalPath);
        }
    }


    public static void usage() {
        System.out.println("CopyDirectory<remotepath><localpath >");
    }

    public static void main(String[] args) {
        if (args.length < 2) {
            usage();
            return;
        }

        IHDFSFileSystem accessor = HDFSAccessor.getFileSystem();
        String source = args[0];
        File localFile = new File(args[1]);
        if (!accessor.exists(source)) {
            throw new IllegalArgumentException("remote path " + source + " does not exist");

        }
          if (accessor.isFile(source)) {
             generalCopy(accessor, source, localFile) ;
         }
        if (accessor.isDirectory(source)) {
             copyDirectory(accessor, source, localFile) ;
         }


     }
}
