package org.systemsbiology.xtandem.dta;

import com.lordjoe.utilities.*;
import org.apache.hadoop.metrics.spi.*;

import java.io.*;

/**
 * org.systemsbiology.xtandem.dta.DTAtoMGF
 * main converts a directory of dta files to mgf
 * User: Steve
 * Date: 1/19/12
 */
public class DTAtoMGF {
    public static final DTAtoMGF[] EMPTY_ARRAY = {};

    public DTAtoMGF() {
    }

    public void convertToMGF(File src,String name)
    {
        if(!src.exists() || !src.isDirectory())
            throw new IllegalArgumentException("bad source directory " + src.getAbsolutePath());
        File dst = new File(name);
        PrintWriter out = null;
        try {
            File[] files = src.listFiles();
            if(files == null)
                return;
            out = new PrintWriter(new FileWriter(dst));
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                if(file.getName().toLowerCase().endsWith(".dta"))
                    appendDTA(out,file, i + 1);
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);

        }
        finally {
            if(out != null)
                out.close();
        }
    }

    private void appendDTA(final PrintWriter out, final File pFile, int index) {
        String[] strings = FileUtilities.readInAllLines(pFile);
        out.println("BEGIN IONS");
        out.println("TITLE=" + index);
        String[] items = strings[0].split(" ");
        out.println("PEPMASS=" + items[0]);
        out.println("CHARGE=" + items[1]);

        for (int i = 1; i < strings.length; i++) {
            String string = strings[i];
            out.println(string.replace(" ","\t"));

        }
        out.println("END IONS");
    }

    public static void main(String[] args) {
        if(args.length == 0)  {
            System.out.println("usage DTAtoMGF  dta_directory  <out_file_name>");
            return;
        }
         DTAtoMGF converter = new DTAtoMGF();
        File src = new File(args[0]);
        String name = src.getName() + ".mgf";
        if(args.length > 1)
            name = args[1];
        converter.convertToMGF(src,name);
    }

}
