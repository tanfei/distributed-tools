package org.systemsbiology.xtandem.hadoop;

import org.apache.hadoop.conf.*;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.partition.*;

import java.io.*;
import java.util.*;

/**
 * org.systemsbiology.xtandem.hadoop.MapDirectoryReader
 * a reader for a directory of MapFiles where key is String (Text) and value is String (Text)
 * User: steven
 * Date: 1/11/12
 */
public class MapDirectoryReader {
    public static final MapDirectoryReader[] EMPTY_ARRAY = {};

    private final Partitioner<Text, Text> m_Partitioner = new HashPartitioner<Text, Text>();
    private final Text m_OnlyText = new Text();
    private final Text m_OutText = new Text();
    private final Path m_Directory;
    private final int m_NumberReducers;
    private final MapFile.Reader[] m_Mappers;

    public MapDirectoryReader(final Path pDirectory, Configuration conf) {
        try {
            FileSystem m_FS = FileSystem.get(conf);
            m_Directory = pDirectory;
               boolean exists = m_FS.exists(m_Directory);
            if (!exists)
                throw new IllegalStateException("problem"); // ToDo change
            boolean isDor = m_FS.getFileStatus(m_Directory).isDir();
            if (!isDor)
                throw new IllegalStateException("problem"); // ToDo change

            FileStatus[] fileStatuses = m_FS.listStatus(m_Directory);
            m_NumberReducers = fileStatuses.length;
            m_Mappers = new MapFile.Reader[getNumberReducers()];
            for (int i = 0; i < fileStatuses.length; i++) {
                FileStatus fileStatuse = fileStatuses[i];
                String dirName = fileStatuse.getPath().toString();
                m_Mappers[i] = new MapFile.Reader(m_FS, dirName, conf);
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);

        }
        finally {
        }
    }

    protected Partitioner<Text, Text> getPartitioner() {
        return m_Partitioner;
    }

    public Path getDirectory() {
        return m_Directory;
    }

    public int getNumberReducers() {
        return m_NumberReducers;
    }

    protected MapFile.Reader[] getMappers() {
        return m_Mappers;
    }

    /**
     * lookup a value by key
     * @param key  !null key
     * @return possibly null value
     */
    public String lookup(String key) {
        int index = getReducerPath(key);
        m_OnlyText.set(key);
        MapFile.Reader mapper = m_Mappers[index];
        try {
            Writable writable = mapper.get(m_OnlyText, m_OutText);
            if (writable != null)
                return writable.toString();
            else
                return null;
        }
        catch (IOException e) {
            throw new RuntimeException(e);

        }
    }

    /**
     * which reducer willbe used for this key
     * @param key   !null key
     * @return  retuder number 0 ..getNumberReducers() - 1
     */
    protected int getReducerPath(String key) {
        m_OnlyText.set(key);
        return m_Partitioner.getPartition(m_OnlyText, null, getNumberReducers());
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.out.println("Usage <fasta file> <map directory>");
            return;
        }
        List<String> holder = new ArrayList<String>();
        Configuration conf = new Configuration();
        File fasta = new File(args[0]);
        MapDirectoryReader rdr = new MapDirectoryReader(new Path(args[1]), conf);
        LineNumberReader inp = new LineNumberReader((new FileReader(fasta)));
        String line = inp.readLine();
        while (line != null) {
            if (line.startsWith(">")) {
                holder.add(line.substring(1));
            }
            line = inp.readLine();
        }
        String[] ret = new String[holder.size()];
        holder.toArray(ret);
        for (int i = 0; i < ret.length; i++) {
            String s = ret[i];
            String val = rdr.lookup(s);
            if (val == null)
                throw new IllegalStateException("problem"); // ToDo change
        }
    }

}
