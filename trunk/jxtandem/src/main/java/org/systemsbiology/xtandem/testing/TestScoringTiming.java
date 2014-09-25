package org.systemsbiology.xtandem.testing;

import com.lordjoe.utilities.*;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.systemsbiology.xtandem.*;
import org.systemsbiology.xtandem.hadoop.*;
import org.systemsbiology.xtandem.ionization.*;
import org.systemsbiology.xtandem.peptide.*;
import org.systemsbiology.xtandem.scoring.*;
import org.systemsbiology.xtandem.taxonomy.*;

import java.io.*;
import java.util.*;

/**
 * org.systemsbiology.xtandem.testing.TestScoringTiming
 * User: steven
 * Date: 12/1/11
 */
public class TestScoringTiming {
    public static final TestScoringTiming[] EMPTY_ARRAY = {};

    public static int[] SAVED_MASSES =
            {
                    1080, 1091, 1190, 2070, 2081, 2092, 2180,
                    2191, 1060, 1071, 1082, 1093, 1170, 1181, 1192,
                    1280,
            };

    public static Set<Integer> SAVED_MASS_SET = new HashSet<Integer>();

    static {
        for (int i = 0; i < SAVED_MASSES.length; i++) {
            SAVED_MASS_SET.add(SAVED_MASSES[i]);

        }
    }

    //    Finished mass 1080 numberPeptides 28332 scored 62 not scored 0 scans pre msec   1.98 at 03:06 in 57.989 min
//357.0 0.67
//Finished mass 1091 numberPeptides 31860 scored 36 not scored 4 scans pre msec   2.02 at 03:49 in 42.921 min
//272.0 0.32
//Finished mass 1190 numberPeptides 30494 scored 90 not scored 0 scans pre msec   1.99 at 05:20 in 90.917 min
//533.0 0.71
//Finished mass 2070 numberPeptides 25644 scored 9 not scored 0 scans pre msec   1.85 at 05:27 in 426.819 sec
//758.0 0.66
//Finished mass 2081 numberPeptides 26678 scored 16 not scored 0 scans pre msec   2.06 at 05:42 in 878.327 sec
//548.0 0.47
//Finished mass 2092 numberPeptides 26062 scored 19 not scored 0 scans pre msec   2.08 at 05:59 in 17.166 min
//534.0 0.39
//Finished mass 2180 numberPeptides 25862 scored 16 not scored 0 scans pre msec   1.50 at 06:09 in 619.789 sec
//445.0 0.45
//Finished mass 2191 numberPeptides 25468 scored 19 not scored 0 scans pre msec   1.01 at 06:17 in 489.761 sec
//358.0 0.38
//Finished mass 1060 numberPeptides 31636 scored 70 not scored 0 scans pre msec   2.20 at 02:17 in 81.333 min
// 42.0 0.10
//Finished mass 1071 numberPeptides 28638 scored 23 not scored 14 scans pre msec   0.72 at 02:29 in 757.631 sec
//124.0 0.28
//Finished mass 1082 numberPeptides 27290 scored 78 not scored 5 scans pre msec   1.68 at 03:33 in 63.264 min
//432.0 0.49
//Finished mass 1093 numberPeptides 29916 scored 54 not scored 5 scans pre msec   1.60 at 04:20 in 47.108 min
//113.0 0.21
//Finished mass 1170 numberPeptides 28316 scored 54 not scored 0 scans pre msec   1.71 at 05:03 in 43.63 min
// 70.0 0.18
//Finished mass 1181 numberPeptides 27964 scored 159 not scored 0 scans pre msec   1.77 at 07:15 in 131.307 min
// 70.0 0.26
//Finished mass 1192 numberPeptides 30488 scored 33 not scored 6 scans pre msec   0.87 at 07:32 in 17.171 min
//124.0 0.29
//Finished mass 1280 numberPeptides 28988 scored 39 not scored 0 scans pre msec   1.05 at 07:52 in 19.735 min
// 34.0 0.11
//Finished mass 1291 numberPeptides 30356 scored 85 not scored 11 scans pre msec   0.97 at 08:38 in 46.887 min
//171.0 0.38
    private static void testMassRead(JXTandemLauncher main, Configuration configuration) {
        IPolypeptide pp = Polypeptide.fromString("AVLEFTPETPSPLIGILENK[8.014]");
        double matchingMass = pp.getMatchingMass();
        testMassRead(main, configuration,(int)matchingMass);
        for (int i = 0; i < SAVED_MASSES.length; i++) {
            testMassRead(main, configuration, SAVED_MASSES[i]);

        }
    }

    private static void testMassRead(JXTandemLauncher main, Configuration configuration, final int mass) {
        ElapsedTimer elapsed = new ElapsedTimer();
        SequenceFile.Reader rdr = XTandemHadoopUtilities.buildScanStoreReader(configuration, mass);
        try {
            HadoopTandemMain application = main.getApplication();
            String dir = application.getDatabaseName();
            HadoopFileTaxonomy ftax = new HadoopFileTaxonomy(application, "foo", configuration);
            main.loadTaxonomy();
            application.loadScoring();

            IPolypeptide[] st = ftax.getPeptidesOfExactMass(mass, application.isSemiTryptic());
            elapsed.showElapsed("Read " + st.length + " peptides");

            String scan = XTandemHadoopUtilities.readNextScan(rdr);
            final Scorer scorer = application.getScoreRunner();


            ElapsedTimer specTine = new ElapsedTimer();
                scorer.clearSpectra();
             scorer.clearPeptides();

             scorer.generateTheoreticalSpectra(st);
            specTine.showElapsed("Generated  " + st.length + " spectra");
             while (scan != null) {
                 ElapsedTimer scanTine = new ElapsedTimer();
                RawPeptideScan rscan = XTandemHadoopUtilities.readScan(scan,null);
                 IScoredScan iScoredScan = handleScan(rscan, scorer);
                 scan = XTandemHadoopUtilities.readNextScan(rdr);
                scanTine.showElapsed("Handled scan " + rscan.getId());
            }

            elapsed.showElapsed("Handled mass " + mass);
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

    private static IScoredScan handleScan(RawPeptideScan scan, Scorer scorer) {
         IScoredScan scoring = new OriginatingScoredScan(scan);
        IonUseCounter counter = new IonUseCounter();
        final ITheoreticalSpectrumSet[] tss = scorer.getAllSpectra();
        int numberDotProducts = scorer.scoreScan(counter, tss, scoring);
        return scoring;
    }

    public static void main(String[] args) {
        Configuration configuration = new Configuration();
        String paramsFile = args[0];
        InputStream is = XTandemUtilities.getDescribedStream(paramsFile);
        JXTandemLauncher main = new JXTandemLauncher(is, paramsFile, configuration);
   //     Scorer.USE_CLASSIC_SCORING = false; // use experimental versions of the code
        testMassRead(main, configuration);
    }


}
