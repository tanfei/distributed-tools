package com.lordjoe.distributed.util;

import com.lordjoe.distributed.*;
import uk.ac.ebi.pride.spectracluster.cluster.*;
import uk.ac.ebi.pride.spectracluster.io.*;
import uk.ac.ebi.pride.spectracluster.keys.*;

import java.io.*;
import java.nio.file.*;


/**
 * com.lordjoe.distributed.util.PathOutputMZConsumer
 * User: Steve
 * Date: 9/2/2014
 */
public class PathOutputMZConsumer  implements IKeyValueConsumer<MZKey,ICluster>,Closeable {

    private final Path parentPath;
    private MZKey currentMZ;
    private PrintWriter output;

    public PathOutputMZConsumer(final Path pParentPath) {
        parentPath = pParentPath;
    }

    @Override public void consume(final KeyValueObject<MZKey,ICluster> kv) {
         if(currentMZ == null ||  currentMZ.getAsInt() != kv.key.getAsInt())  {
             close();
             output =  buildWriter(parentPath,kv.key);
             currentMZ = kv.key;
         }
        CGFClusterAppender.INSTANCE.appendCluster(output,kv.value);
    }

    protected static PrintWriter buildWriter(Path path,MZKey key) {
        String filename = "ClusterBin" + String.format("%04d",key.getAsInt()) + ".cgf";
        File parent = path.toFile();
        parent.mkdirs();
        File outFile = new File(parent, filename);
        try {
            return new PrintWriter(new FileWriter(outFile)) ;
        }
        catch (IOException e) {
            throw new RuntimeException(e);

        }
    }

    public void close()
    {
        if(output != null) {
            output.close();
            output = null;
        }
    }


}
