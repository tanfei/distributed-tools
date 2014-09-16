package uk.ac.ebi.pride.spectracluster;

import uk.ac.ebi.pride.spectracluster.cluster.*;
import uk.ac.ebi.pride.spectracluster.io.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * main.uk.ac.ebi.pride.spectracluster.PathMgfSource
 * User: Steve
 * Date: 9/2/2014
 */
public class PathMgfSource {

    public Iterable<ICluster> readClusters(Path p) {
        File file = p.toFile();
        if (file.isFile()) {
            return readClusters(file);
        }
        else {
            List allClusters = new ArrayList();
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                File file1 = files[i];
                List<ICluster> c = ParserUtilities.readMGFClusters(file1);
                allClusters.addAll(c);

            }
           return allClusters;
        }
    }

    public Iterable<ICluster> readClusters(File p) {
        return ParserUtilities.readMGFClusters(p);
    }

}
