package com.lordjoe.distributed.hydra;

import org.systemsbiology.xtandem.*;
import org.systemsbiology.xtandem.reporting.*;
import org.systemsbiology.xtandem.scoring.*;
import scala.*;

import java.io.*;
import java.util.*;

/**
 * com.lordjoe.distributed.hydra.TandemXMLWriter
 * User: Steve
 * Date: 10/8/2014
 */
public class TandemXMLWriter {
    private final XTandemMain application;

    public TandemXMLWriter(final XTandemMain pApplication) {
        application = pApplication;
    }

    public void buildReport(List<Tuple2<String, IScoredScan>> scorings) {

        String outputPath = application.getOutputPath();
        FileOutputStream os = null;
        try {
            os = new FileOutputStream(outputPath);
            List<IScoredScan> holder = new ArrayList<IScoredScan>();
            for (Tuple2<String, IScoredScan> tp : scorings) {
                holder.add(tp._2());
            }
            IScoredScan[] ret = new IScoredScan[holder.size()];
            holder.toArray(ret);
            BiomlReporter reporter = new BiomlReporter(application, ret, os);
            reporter.writeReport();
        }
        catch (IOException e) {
            throw new RuntimeException(e);

        }
        finally {
            try {
                os.close();
            }
            catch (IOException e) {
                throw new RuntimeException(e);

            }
        }

    }
}
