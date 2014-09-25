package org.systemsbiology.xtandem.taxonomy;

import org.systemsbiology.xml.*;
import org.systemsbiology.xtandem.*;

import java.io.*;
/**
 * Class to count the number of Proteins in a Fasta File
 * User: Steve
 * Date: 6/20/11
  */
public class FastaFileSizer
{
    public int numberProteins(String fileName) {
        return numberProteins(new File(fileName));
     }
    public int numberProteins(File fileName) {
        try {
            return numberProteins(new FileInputStream(fileName));
        }
        catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    public int numberProteins(InputStream str) {
        int ret = 0;
        LineNumberReader rdr = new LineNumberReader(new InputStreamReader(str));
        try {
            String line = rdr.readLine();
            while(line != null)  {
                if(line.startsWith(">"))
                    ret++;
               line = rdr.readLine();
            }

            return ret;
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }

        finally {
            try {
                rdr.close();
            }
            catch (IOException e) {
               throw new RuntimeException(e);
            }
        }
    }

    public static void main(String[] args)
    {
        FastaFileSizer fs = new FastaFileSizer();
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            XMLUtilities.outputLine("File " + arg + " has " + fs.numberProteins(arg));
        }
    }
}
