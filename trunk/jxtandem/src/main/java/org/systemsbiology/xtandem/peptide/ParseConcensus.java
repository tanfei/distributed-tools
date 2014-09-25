package org.systemsbiology.xtandem.peptide;

import java.io.*;
import java.util.*;
/**
 * org.systemsbiology.xtandem.peptide.ParseConcensus
 * User: Steve
 * Date: 8/15/12
 */
public class ParseConcensus {
    public static final ParseConcensus[] EMPTY_ARRAY = {};

    public static final int SAMPLE_LINES = 20000;

    protected static String peptideFromLine(String line) {
        String[] items = line.split(" ");
        return items[1];
    }

    protected static String proteinFromLine(String line) {
        String[] items = line.split(" ");
        for (int i = 0; i < items.length; i++) {
            String item = items[i];
            if (item.startsWith("Protein="))
                return item.substring("Protein=".length());
        }
        return null;
    }

    public static void main(String[] args) throws Exception {
        File in = new File(args[0]);
        File outFile = new File(args[1]);
        if (!in.exists())
            throw new IllegalStateException("input file " + args[0] + " does not exist");
        PrintWriter out = new PrintWriter(new FileWriter(outFile));
        //  ZipInputStream in1 = new ZipInputStream(new FileInputStream(in));
        LineNumberReader rdr = new LineNumberReader(new InputStreamReader(new FileInputStream(in)));
        String line = rdr.readLine();
        long count = 0;
        String peptide = null;
        String proteim = null;
        List<FoundPeptide> holder = new ArrayList<FoundPeptide>();
        while (line != null) {
     //       if (count++ > SAMPLE_LINES)
      //          break;
            if (line.startsWith("Name:")) {
                peptide = peptideFromLine(line);
            }
            if (line.startsWith("Comment:")) {
                proteim = proteinFromLine(line);
                if (proteim != null) {
                     FoundPeptide fp =  new FoundPeptide(peptide, proteim);
                    holder.add(fp);
                    peptide = null;
                    proteim = null;
                }
            }
             line = rdr.readLine();
        }
         FoundPeptide[] ret = new FoundPeptide[holder.size()];
        holder.toArray(ret);
        for (int i = 0; i < ret.length; i++) {
            FoundPeptide foundPeptide = ret[i];
            out.append(foundPeptide.toString());
             out.append("\n");

        }
        out.close();

    }

}
