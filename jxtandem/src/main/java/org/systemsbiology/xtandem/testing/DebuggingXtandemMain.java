package org.systemsbiology.xtandem.testing;

import org.systemsbiology.xml.*;
import org.systemsbiology.xtandem.*;

import java.io.*;

/**
 * org.systemsbiology.xtandem.testing.DebuggingXtandemMain
 * User: steven
 * Date: 3/14/11
 */
public class DebuggingXtandemMain {
    public static final DebuggingXtandemMain[] EMPTY_ARRAY = {};


    public static void usage() {
        XMLUtilities.outputLine("Usage - JXTandem <inputfile>");
     }

     public static void main(String[] args) {
         if (args.length == 0) {
             usage();
             return;
         }
         File TaskFile = new File(args[0]);
         if (!TaskFile.exists() || !TaskFile.canRead()) {
             usage();
             return;
         }
         XTandemMain main = new XTandemMain(TaskFile);
         XTandemDebugging.setDebugging(true, main);
         main.loadScoringTest();
         main.loadSpectra();

         XTandemDebugging.loadXTandemValues("log1.txt");


         main.process();
     //    main.getElapsed().showElapsed(System.out);
     }

}
