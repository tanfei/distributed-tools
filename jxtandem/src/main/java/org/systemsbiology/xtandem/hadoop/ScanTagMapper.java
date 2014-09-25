package org.systemsbiology.xtandem.hadoop;

import com.lordjoe.utilities.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.util.*;
import org.systemsbiology.xml.*;
import org.systemsbiology.xtandem.*;
import org.systemsbiology.xtandem.peptide.*;
import org.systemsbiology.xtandem.scoring.*;
import org.systemsbiology.xtandem.testing.*;
import org.apache.hadoop.util.VersionInfo;

import java.io.*;
import java.util.*;

/**
 * org.systemsbiology.xtandem.hadoop.ScanTagMapper
 * User: steven
 * Date: 3/7/11
 */
public class ScanTagMapper extends AbstractTandemMapper<Writable> {
    public static final ScanTagMapper[] EMPTY_ARRAY = {};

    /// Some debugging hooks when we walk interesting cases
    public static final int[] INTERESTING_MASSES = { 2336, 2318};

    public static final int REPORT_INTERVAL_SCANS = 1000;

    public static final Random RND = new Random();



    /// Some debugging hooks when we walk interesting cases
    public static boolean isMassInteresting(int mass) {
        for (int i = 0; i < INTERESTING_MASSES.length; i++) {
            if (mass == INTERESTING_MASSES[i])
                return true;

        }
        return false;

    }

    //  private final Map<Integer, List<Path>> m_DatabaseFiles = new HashMap<Integer, List<Path>>();
    private Map<Integer, Integer> m_DatabaseSizes = new HashMap<Integer, Integer>();
    private int m_MaxScoredPeptides;
    private PrintWriter m_LogJamScan;
    private SpectrumCondition m_Condition;
    private int m_TotalScansProcessed;
    private long m_CohortProcessingTime;
    private long m_KeyWriteTime;
    private int m_KeyWriteCount;
    private long m_ParsingTime;

    public ScanTagMapper() {
    }

    public int getTotalScansProcessed() {
        return m_TotalScansProcessed;
    }

    public void incrementTotalScansProcessed() {
        m_TotalScansProcessed++;
    }

    @Override
    protected void setup(final Context context) throws IOException, InterruptedException {
        super.setup(context);

        HadoopTandemMain app = getApplication();
        app.loadScoring();
        m_DatabaseSizes = XTandemHadoopUtilities.readDatabaseSizes(app);
        m_MaxScoredPeptides = XTandemHadoopUtilities.getMaxScoredPeptides(context.getConfiguration());


        boolean doHardCoded = app.getBooleanParameter(JXTandemLauncher.HARDCODED_MODIFICATIONS_PROPERTY,true);
        PeptideModification.setHardCodeModifications(doHardCoded);


        // sneaky trick to extract the version
        String version = VersionInfo.getVersion();
        context.getCounter("Performance",  "Version-" + version ).increment(1);
         // sneaky trick to extract the user
        String uname = System.getProperty("user.name");
        context.getCounter("Performance",  "User-" + uname ).increment(1);

        // Only do this once
        if (XTandemHadoopUtilities.isFirstMapTask(context)) {
            long maxOnePeptide = XTandemHadoopUtilities.maxDatabaseSizes(m_DatabaseSizes);
            context.getCounter("Performance", "MaxFragmentsAtMass").increment(maxOnePeptide);
            long totalDatabaseFragments = XTandemHadoopUtilities.sumDatabaseSizes(m_DatabaseSizes);
            context.getCounter("Performance", "TotalDatabaseFragments").increment(totalDatabaseFragments);
        }

        // Special code for logjam
        if (ScoringReducer.USING_LOGJAM) {
            m_LogJamScan = new PrintWriter(new FileWriter("LogJamScans.tsv"));
        }
        m_Condition = new SpectrumCondition();
        m_Condition.configure(app);
        getElapsed().reset(); // start a clock
    }

    public int getMaxScoredPeptides() {
        return m_MaxScoredPeptides;
    }

//    public void addDatabaseFiles(Integer key, List<Path> added) {
//        m_DatabaseFiles.put(key, added);
//    }
//
//    public List<Path> getDatabaseFiles(Integer key) {
//        return m_DatabaseFiles.get(key);
//    }


    public SpectrumCondition getCondition() {
        return m_Condition;
    }


    protected boolean isMassScored(double mass) {
        return getCondition().isMassScored(mass);
    }

    public int getDatabaseSize(Integer key) {
        Integer size = m_DatabaseSizes.get(key);
        if (size == null)
            return 0;
        return size;
    }


    @Override
    protected void map(final Writable key, final Text value, final Context context) throws IOException, InterruptedException {
        long startTime = System.currentTimeMillis();
        String textv = value.toString().trim();
        // ignore level 1 scans
        if (textv.contains("msLevel=\"1\""))
            return;
        String fileName = key.toString();
        String extension = fileName.substring(fileName.lastIndexOf(".")).toLowerCase();
        // handle gz files
        if (".gz".equals(extension)) {
            String realName = fileName;
            realName = realName.substring(0, realName.length() - 3); // drop .gz
            extension = realName.substring(fileName.lastIndexOf("."));
        }

        RawPeptideScan scan = null;
//        if (".mzml".equalsIgnoreCase(extension)) {
//            //   RawPeptideScan scan2 = MzMLReader.scanFromFragment(text);
//            scan = XTandemHadoopUtilities.readSpectrum(textv);
//            //      if (!scan2.equivalent(scan))
//            //         throw new IllegalStateException("problem"); // ToDo change
//        }
        if (".mgf".equalsIgnoreCase(extension)) {
            //   RawPeptideScan scan2 = MzMLReader.scanFromFragment(text);
            scan = XTandemHadoopUtilities.readMGFText(textv);
            //      if (!scan2.equivalent(scan))
            //         throw new IllegalStateException("problem"); // ToDo change
        }
        else {
            scan = XTandemHadoopUtilities.readScan(textv,fileName);
            if(!textv.contains("url=\"") && fileName != null)
                textv = textv.replace("msLevel=\"", " url=\"" + fileName  + "\" " +    "msLevel=\"");
        }
        if (scan == null)
            return; // todo or is an exception proper


        String id = scan.getId();

        // debugging code
//        if(XTandemHadoopUtilities.isNotScored(scan))
//            XTandemUtilities.breakHere();

 //       if("8840".equals(id))
//            XTandemUtilities.breakHere();
        // top level nested scans lack this
        if (scan.getPrecursorMz() == null)
            return;

        scan.setUrl(fileName);
   //     textv = scan.toMzMLFragment();
         IScanPrecursorMZ mz = scan.getPrecursorMz();
         int charge = scan.getPrecursorCharge();
        if(charge == 0)   {
           int guess =  XTandemUtilities.guessCharge(scan,mz.getMassChargeRatio());
            mz  = new ScanPrecursorMz(mz,guess);
           scan.setPrecursorMz(mz);
           charge = mz.getPrecursorCharge();
        }

        // cost of parsing
        m_ParsingTime += System.currentTimeMillis() - startTime;

        final Counter totalScans = context.getCounter("Performance", "TotalScoredScans");
        totalScans.increment(1);

//        if ("42".equals(id))
//            XTandemUtilities.breakHere();

        // throw out ridiculous values
        if (charge > XTandemUtilities.MAX_CHARGE)
            return;

        // Special code for logjam
        if (ScoringReducer.USING_LOGJAM) {
            saveMeasuredSpectrum(scan);
        }


        if (charge == 0) {
            for (int i = 1; i <= 3; i++) {
                double mass = scan.getPrecursorMass(i);
                writeScansToMassAtCharge( textv, context, id, mass, i,fileName);
            }
        }
        else {
            double mass = scan.getPrecursorMass(charge);
            writeScansToMass(textv, context, id, mass );
        }

        // give performance statistics
        incrementTotalScansProcessed();
        long elapsedProcessingTime = System.currentTimeMillis() - startTime;
        m_CohortProcessingTime += elapsedProcessingTime;
        if (getTotalScansProcessed() % REPORT_INTERVAL_SCANS == 0) {
            showStatistics();
        }

        getApplication().clearRetainedData();
    }

    private String buildUrlNameValue(final String fileName) {
        StringBuilder sb = new StringBuilder();
        sb.append("   ");
        sb.append("<nameValue name=\"url\" " );
        sb.append("value=\"" + fileName + "\" " );
        sb.append("/>");
        return sb.toString();
    }

    private void showStatistics() {
        ElapsedTimer elapsed = getElapsed();
        elapsed.showElapsed("Processed " + REPORT_INTERVAL_SCANS + " scans at " + XTandemUtilities.nowTimeString());
        // how much timeis in my code
        XMLUtilities.outputLine("Parsing time " + String.format("%7.2f", m_ParsingTime / 1000.0));
        XMLUtilities.outputLine("processing time " + String.format("%7.2f", m_CohortProcessingTime / 1000.0));
        XMLUtilities.outputLine("writing time " + String.format("%7.2f", m_KeyWriteTime / 1000.0) +
                " for " + m_KeyWriteCount + " writes");

        // how much timeis in my code

        long freemem = setMinimalFree();
        XMLUtilities.outputLine("Free Memory " + String.format("%7.2fmb", freemem / 1000000.0) +
                " minimum " + String.format("%7.2fmb", getMinimumFreeMemory() / 1000000.0));
        elapsed.reset();

        m_CohortProcessingTime = 0;
        m_ParsingTime = 0;
        m_KeyWriteTime = 0;
        m_KeyWriteCount = 0;
        elapsed.reset();
    }

    protected void writeScansToMassAtCharge(final String value, final Context context, final String pId, double mass, int charge,String filename) throws IOException, InterruptedException {
        if (!isMassScored(mass))
            return;
        IScoringAlgorithm scorer = getApplication().getScorer();

        int[] limits = scorer.allSearchedMasses(mass);
        for (int j = 0; j < limits.length; j++) {
            int limit = limits[j];
            writeScanToMassAtCharge(limit, pId, value, charge,filename, context);
        }
    }

    public static final String CHARGE_0_STRING = "precursorCharge=\"0\"";
    public static final String PRECURSOR_MZ_TAG_STRING = "<precursorMz ";

    protected void writeScanToMassAtCharge(int mass, String id, String value, int charge,String filename, Context context)
            throws IOException, InterruptedException {

       // Special code to store scans at mass for timing studies
//        if(ScoringReducer.STORING_SCANS ) {
//            if(!TestScoringTiming.SAVED_MASS_SET.contains(mass))
//                return;
//        }

        HadoopTandemMain application = getApplication();
        SpectrumCondition sp = application.getSpectrumParameters();
        // if mass is too low or too high forget it
        if (sp != null && !sp.isMassScored(mass)) {
            Counter performance = context.getCounter("Performance", "Not Scored Mass");
            performance.increment(1);
            return;
        }
        long startTime = System.currentTimeMillis();
        String keyStr = String.format("%06d", mass);
        String valueStr = value.toString();
        int numberEntries = getDatabaseSize(mass);
        int maxScored = getMaxScoredPeptides();

        // patch the charge to a known state matching the masses
        String chargeString = "precursorCharge=\"" + charge + "\" ";
        if (valueStr.contains(CHARGE_0_STRING)) {
            valueStr = valueStr.replace(CHARGE_0_STRING, chargeString);
        }
        else {
            valueStr = valueStr.replace(PRECURSOR_MZ_TAG_STRING, PRECURSOR_MZ_TAG_STRING + chargeString);
        }

        //   System.err.println("Sending mass " + mass + " for id " + id );
        final Text onlyKey = getOnlyKey();
        final Text onlyValue = getOnlyValue();
        onlyValue.set(valueStr);

        int numberSeparateReducers =  1 + (numberEntries / maxScored);

        if (numberSeparateReducers == 1) {

            //   System.err.println("Sending mass " + mass + " for id " + id );
            onlyKey.set(keyStr);
            context.write(onlyKey, onlyValue);
            m_KeyWriteCount++;
        }
        else {
            int chosen = RND.nextInt(numberSeparateReducers); // choose one reducer

            // too big split into multiple tasks
            int start = maxScored * chosen;
            int end = (maxScored + 1)  * chosen;
           // while (numberEntries > 0) {
                String key = keyStr + ":" + start + ":" + end;
                start += maxScored;
                numberEntries -= maxScored;
                onlyKey.set(key);
                context.write(onlyKey, onlyValue);
                m_KeyWriteCount++;
          //  }
        }
        long elapsedProcessingTime = System.currentTimeMillis() - startTime;
        m_KeyWriteTime += elapsedProcessingTime;
    }

    protected void writeScansToMass(final String value, final Context context, final String pId, double mass ) throws IOException, InterruptedException {
        IScoringAlgorithm scorer = getApplication().getScorer();
        int[] limits = scorer.allSearchedMasses(mass);
        for (int j = 0; j < limits.length; j++) {
            int limit = limits[j];

            if(isMassInteresting(limit))
                 XTandemUtilities.breakHere();

            writeScanToMass(limit, pId, value, context);
        }
    }

    private void saveMeasuredSpectrum(final RawPeptideScan scan) {
        final HadoopTandemMain app = getApplication();
        final Scorer scorer = app.getScoreRunner();
        SpectrumCondition sc = scorer.getSpectrumCondition();
        final IScoringAlgorithm sa = scorer.getAlgorithm();
        ScoredScan ssc = new ScoredScan(scan);
        IMeasuredSpectrum scn = ssc.conditionScan(sa, sc);
        if (scn == null)
            return;
        ISpectrumPeak[] peaks = scn.getPeaks();
        if (peaks == null || peaks.length < 4)
            return;
        String id = scan.getId();
        m_LogJamScan.print(id);
        m_LogJamScan.print("\t");
        ScoringReducer.showPeaks(scn, m_LogJamScan);
    }


    protected void writeScanToMass(int mass, String id, String value, Context context )
            throws IOException, InterruptedException {

        int numberEntries = getDatabaseSize(mass);

        if (numberEntries == 0)
            return; // we will find no peptides to score

        String keyStr = String.format("%06d", mass);
        int maxScored = getMaxScoredPeptides();
        final Text onlyKey = getOnlyKey();
        final Text onlyValue = getOnlyValue();
         long startTime = System.currentTimeMillis();
        if (numberEntries < maxScored) {    // few entries score in one task

            //   System.err.println("Sending mass " + mass + " for id " + id );
            onlyKey.set(  keyStr);
            onlyValue.set(  value);
                context.write(onlyKey, onlyValue);
            m_KeyWriteCount++;
        }
        else {
            // too big split into multiple tasks
            int start = 0;
            while (numberEntries > 0) {
                String key = keyStr + ":" + start + ":" + (start + maxScored);
                start += maxScored;
                numberEntries -= maxScored;
                onlyKey.set(  key);
                onlyValue.set(  value);
                  context.write(onlyKey, onlyValue);
                m_KeyWriteCount++;
            }
        }
        // cost of writing
        long elapsedProcessingTime = System.currentTimeMillis() - startTime;
        m_KeyWriteTime += elapsedProcessingTime;

    }

    @Override
    protected void cleanup
            (
                    final Context context) throws IOException, InterruptedException {
        // Special code for logjam
        if (ScoringReducer.USING_LOGJAM) {
            m_LogJamScan.close();
        }
        super.cleanup(context);    //To change body of overridden methods use File | Settings | File Templates.
    }
}
