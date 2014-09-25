package org.systemsbiology.xtandem.hadoop;

import com.lordjoe.utilities.*;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.systemsbiology.common.*;
import org.systemsbiology.sax.*;
import org.systemsbiology.xml.*;
import org.systemsbiology.xtandem.*;
import org.systemsbiology.xtandem.ionization.*;
import org.systemsbiology.xtandem.peptide.*;
import org.systemsbiology.xtandem.scoring.*;
import org.systemsbiology.xtandem.taxonomy.*;
import org.systemsbiology.xtandem.testing.*;

import java.io.*;
import java.util.*;

//import org.springframework.jdbc.core.simple.*;

/**
 * org.systemsbiology.xtandem.hadoop.ScoringReducer
 * User: steven
 * Date: 3/7/11
 */
public class ScoringReducer extends AbstractTandemReducer implements SpectrumGenerationListener, INoticationListener {


    public static final ScoringReducer[] EMPTY_ARRAY = {};

    public static final String[] INTERESTING_SEQUENCES =
            {
                    "VLSKTAGIRVSPLILGGASIGDAWSGFMGSMNK",
                    "QVYHLGIKFALETTDLSEMYPIEYSPYK",
            };

    public static final Set<String> INTERESTING_SEQUENCE_SET = new HashSet<String>(Arrays.asList(INTERESTING_SEQUENCES));

    public static boolean isInterestingSequence(IPolypeptide peptide) {

        if(peptide.isModified() ) {
            String s = peptide.toString();
            if(s.contains("[7"))
                return true;
        }
        // debugging code  to see why some sequewnces are not scored
        if (INTERESTING_SEQUENCES.length > 0) {
            String sequence = peptide.getSequence();
            if (INTERESTING_SEQUENCE_SET.contains(sequence)) {
                double matchingMass = peptide.getMatchingMass();
                XTandemUtilities.breakHere();
                return true;
            }
        }
        return false;
    }


    // Special output for Josh Patterson LogJam
    public static final boolean USING_LOGJAM = false;
    // Special output for timing  on windows (in single mode)
 //   public static final boolean STORING_SCANS = HadoopUtilities.isWindows() && false;
    // special debug code
    public static final boolean LOG_DOT_PRODUCT = false;

    private PrintWriter m_Logger;
    private ITaxonomy m_Taxonomy;
    private long m_MaxPeptides;
    private PrintWriter m_LogJamSpectra;
    private int m_Notifications;
    private Set<PeptideModification> m_Modifications;
    private boolean m_CreateDecoyPeptides;

    public ScoringReducer() {
    }

    public boolean isCreateDecoyPeptides() {
        return m_CreateDecoyPeptides;
    }

    public void setCreateDecoyPeptides(boolean createDecoyPeptides) {
        m_CreateDecoyPeptides = createDecoyPeptides;
    }

    /**
     * notification to keep job running during long scoring
     *
     * @param data
     */
    @Override
    public void onNotification(final Object... data) {
        if (m_Notifications++ % 100 == 0) {
            Context context = getContext();
            Counter counter = context.getCounter("Performance", "ScoredScanProducts");
            counter.increment(100);
        }

    }


    @Override
    protected void setup(final Context context) throws IOException, InterruptedException {
        super.setup(context);
        // read configuration lines
        Configuration conf = context.getConfiguration();

//        IAnalysisParameters ap = AnalysisParameters.getInstance();
//        ap.setJobName(context.getJobName());

        HadoopTandemMain application = getApplication();
        m_Taxonomy = application.getTaxonomy();

        boolean makeDecoys = application.getBooleanParameter(XTandemUtilities.CREATE_DECOY_PEPTIDES_PROPERTY,false);
        //setCreateDecoyPeptides(makeDecoys);

        boolean doHardCoded = application.getBooleanParameter(JXTandemLauncher.HARDCODED_MODIFICATIONS_PROPERTY,true);
        PeptideModification.setHardCodeModifications(doHardCoded);

        int NMatches = application.getIntParameter(JXTandemLauncher.NUMBER_REMEMBERED_MATCHES,XTandemHadoopUtilities.DEFAULT_CARRIED_MATCHES);
        XTandemHadoopUtilities.setNumberCarriedMatches(NMatches);


        // Special code for logjam
        if (USING_LOGJAM) {
            m_LogJamSpectra = new PrintWriter(new FileWriter("LogJamSpectra.tsv"));
        }

        // log if running in local mode
        // we need to test performance
        //noinspection SimplifiableIfStatement,PointlessBooleanExpression,ConstantConditions,RedundantIfStatement
        if (false && XTandemHadoopUtilities.isLocal(conf)) {
            m_Logger = XTandemHadoopUtilities.buildDebugWriter(context, application);
            XTandemDebugging.setDebugging(true, application);
            appendLine("<JXTandem>");
        }


        Scorer scoreRunner = application.getScoreRunner();
        scoreRunner.addINoticationListener(this);
        scoreRunner.addSpectrumGenerationListener(this);
        // maybe do some logging
        scoreRunner.setLogger(m_Logger);

        //  scoreRunner.digest();

        String dir = application.getDatabaseName();
        if (dir != null) {
            HadoopFileTaxonomy ftax = new HadoopFileTaxonomy(application, m_Taxonomy.getOrganism(), conf);
            m_Taxonomy = ftax;
        }
        else {
            // make sure the latest table is present
            throw new UnsupportedOperationException("we dropped databases for now"); // ToDo
            //guaranteeModifiedPeptideTable();
        }
        int task_number = XTandemHadoopUtilities.getTaskNumber(context.getConfiguration());
        ScoringModifications scoringMods = application.getScoringMods();
        PeptideModification[] modifications = scoringMods.getModifications();
        m_Modifications = new HashSet<PeptideModification>(Arrays.asList(modifications));
    }


    /**
     * the table is new and we need it even if we don't use it
     */
//    private void guaranteeModifiedPeptideTable() {
//        if (m_Taxonomy instanceof JDBCTaxonomy) {
//            JDBCTaxonomy tax = (JDBCTaxonomy) m_Taxonomy;
//            SimpleJdbcTemplate template = tax.getTemplate();
//
//            TaxonomyDatabase db = new TaxonomyDatabase(template);
//
//            db.guaranteeTable("semi_mono_modified_mz_to_fragments");
//        }
//    }

    /**
     * we may refactor the key in different ways int mass will always be encoded
     *
     * @param keyStr
     * @return
     */
    public static int keyStringToMass(String keyStr) {
        return Integer.parseInt(keyStr);
    }

    @Override
    public void onSpectrumGeneration(final ITheoreticalSpectrumSet spec) {
        Counter counter = getContext().getCounter("Performance", "TotalSpectra");
        counter.increment(1);

    }

    protected void reduceNormal(final Text key, final Iterable<Text> values, final Context context)
            throws IOException, InterruptedException {
        String keyStr = key.toString();
        int charge = 1;

        MassPeptideInterval interval = new MassPeptideInterval(keyStr);
        int mass = interval.getMass();
        if(!interval.isUnlimited())
            System.err.println(interval.toString());

        // Special code to store scans at mass for timing studeies
//        if (STORING_SCANS) {
//            if (!TestScoringTiming.SAVED_MASS_SET.contains(mass))
//                return;
//        }

        int numberScans = 0;
        int numberScored = 0;
        int numberNotScored = 0;

        HadoopTandemMain application = getApplication();

        // if we do not score this mass continue
        SpectrumCondition sp = application.getSpectrumParameters();
        if (!sp.isMassScored(mass))
            return;


        Configuration configuration = context.getConfiguration();
        int task_number = XTandemHadoopUtilities.getTaskNumber(configuration);


        // Special code to store scans at mass for timing stueies
        SequenceFile.Writer writer = null;
//        if (STORING_SCANS) {
//            writer = XTandemHadoopUtilities.buildScanStoreWriter(configuration, mass);
//        }
//        // Debug stuff
        //    if (mass > 2187 && mass < 2192)
        //        XTandemUtilities.breakHere();
       // if (mass == 1392)
      //      XTandemUtilities.breakHere();
        //       if (  mass > 3203  )
        //          XTandemUtilities.breakHere();

        int numberScoredPeptides = 0;

        ElapsedTimer et = new ElapsedTimer();
        try {
            IPolypeptide[] pps = getPeptidesOfExactMass(interval);


            pps = filterPeptides(pps); // drop non-complient peptides

            System.err.println("Number peptides = " + pps.length );

            if(isCreateDecoyPeptides())   {
                pps = addDecoyPeptides(pps);
            }

//            pps = interval.filterPeptideList(pps);  // we may not use all peptides depending on the size
            numberScoredPeptides = pps.length;

            if (numberScoredPeptides == 0) {
                //noinspection SimplifiableIfStatement,PointlessBooleanExpression,ConstantConditions,RedundantIfStatement
                if (true || doNotHandleScans(values))
                    return; // todo or is an exception proper
                appendLine("<mass value=\"" + mass + "\" >");
                appendLine("</mass>");
                return;  // nothing to score
            }

            if (m_MaxPeptides < numberScoredPeptides) {
                m_MaxPeptides = numberScoredPeptides;
                System.err.println("Max peptides " + m_MaxPeptides + " for mass " + mass);
            }

            final HadoopTandemMain app = application;
            final Scorer scorer = app.getScoreRunner();


            scorer.clearSpectra();
            scorer.clearPeptides();

            ITandemScoringAlgorithm[] alternateScoring = application.getAlgorithms();




            scorer.generateTheoreticalSpectra(pps);
            if (USING_LOGJAM) {
                saveTheoreticalSpectra(scorer);
            }


            Iterator<Text> textIterator = values.iterator();
            while (textIterator.hasNext()) {
                Text text = textIterator.next();
                String textStr = text.toString();


                RawPeptideScan scan = XTandemHadoopUtilities.readScan(textStr, null);
                if (scan == null)
                    return; // todo or is an exception proper
                numberScans++;
                String id = scan.getId();

                // debugging code
//                if (XTandemHadoopUtilities.isNotScored(scan))
//                    XTandemUtilities.breakHere();


                // special code to store scans fo rtiming later
//                if (STORING_SCANS) {
//                    Text onlyKey = getOnlyKey();
//                    onlyKey.set(id);
//                    writer.append(onlyKey, text);
//                }

                // Find out who the user really is
               // String userName = System.getProperty("user.name");
              //  context.getCounter("Performance","UserIs." + userName).increment(1);
                // counter will tell name of user


                context.getCounter("Performance", "TotalScans").increment(1);

                IScoredScan scoredScan = null; // handleScan(scorer, scan, pps);
                MultiScorer ms = new MultiScorer();
                //              ms.addAlgorithm(scoredScan);
                // run any other algorithms
                for (int i = 0; i < alternateScoring.length; i++) {
                    ITandemScoringAlgorithm algorithm = alternateScoring[i];
                    Context context1 = this.getContext();
                    scoredScan = algorithm.handleScan(scorer,  scan, pps,context1);
                    ms.addAlgorithm(scoredScan);
                }


                if (ms.isMatchPresent()) {
                    StringBuilder sb = new StringBuilder();
                    IXMLAppender appender = new XMLAppender(sb);

                    ms.serializeAsString(appender);
                    //     scoredScan.serializeAsString(appender);

                    @SuppressWarnings("ConstantConditions") String outKey = ((OriginatingScoredScan) scoredScan).getKey();
                    while (outKey.length() < 8)
                        outKey = "0" + outKey; // this causes order to be numeric
                    String value = sb.toString();
                    writeKeyValue(outKey, value, context);
                    numberScored++;
                }
                else {
                    // debug repeat
                    //    scoredScan = handleScan(scorer, scan, pps);
                    //   bestMatch = scoredScan.getBestMatch();
                    numberNotScored++;
                    // XTandemUtilities.outputLine("No score for " + id + " at mass " + mass);
                }


            }
            appendLine("</mass>");

            long elapsedMillisec = et.getElapsedMillisec();
            double millisecPerScan = elapsedMillisec / (double) (numberScans * numberScoredPeptides);
            String rateStr = String.format(" scans pre msec %6.2f", millisecPerScan);
            et.showElapsed("Finished mass " + mass +
                    " numberPeptides " + numberScoredPeptides +
                    " scored " + numberScored +
                    " not scored " + numberNotScored +
                    rateStr +
                    " at " + XTandemUtilities.nowTimeString()
            );

            XMLUtilities.outputLine(XMLUtilities.freeMemoryString());
            scorer.clearPeptides();
            scorer.clearSpectra();
            application.clearRetainedData();

            // special code to store scans fo rtiming later
//            if (STORING_SCANS) {
//                writer.close();
//            }

        }
        catch (RuntimeException e) {
            // look at any issues
            XTandemUtilities.breakHere();
            String message = e.getMessage();
            e.printStackTrace(System.err);
            throw e;

        }

    }

    /**
     * add decoy peptides
     * @param pps
     * @return
     */
    protected IPolypeptide[] addDecoyPeptides(IPolypeptide[] pps) {
      IPolypeptide[] ret = new IPolypeptide[pps.length * 2];
        for (int i = 0; i < pps.length; i++) {
            IPolypeptide pp = pps[i];
            ret[ 2 * i]  = pp;
            ret[ 2 * i + 1] = pp.asDecoy();

        }
        return ret;
    }

    /**
     * this method removes any peptides that do not meet the current search criteria
     * @param pPps
     * @return
     */
    protected IPolypeptide[] filterPeptides(final IPolypeptide[] pPps) {
        List<IPolypeptide> holder = new ArrayList<IPolypeptide>();
        HadoopTandemMain application = getApplication();
        IPeptideDigester digester = application.getDigester();
        for (int i = 0; i < pPps.length; i++) {
            IPolypeptide pp = pPps[i];
            if (isAcceptablePeptide(application, digester, pp))   // the work here
                holder.add(pp);
//            else {
//                if (!XTandemUtilities.isProbablySemitryptic(pp)) {
//                    isAcceptablePeptide(application, digester, pp);
//                    System.out.println("Dropped peptide " + pp);
//                }
//            }
        }

        IPolypeptide[] ret = new IPolypeptide[holder.size()];
        holder.toArray(ret);
        return ret;
    }

    /**
     * test if a peptide matches the current search criteria
     * @param pApplication
     * @param digester
     * @param pp
     * @return
     */
    protected boolean isAcceptablePeptide(final HadoopTandemMain pApplication, final IPeptideDigester digester, final IPolypeptide pp) {
        boolean semiTryptic = digester.isSemiTryptic();
//        if (!semiTryptic) {
//            if (XTandemUtilities.isProbablySemitryptic(pp))
//                return false;
//        }
        // are there optional modifications
        if (pp.isModified()) {
            IModifiedPeptide mp = (IModifiedPeptide) pp;
            // if we have no mofidications then reject
            if (m_Modifications.isEmpty())
                return false;
            // if any modifications in the peptide are NOT in the current search set then reject
            return XTandemUtilities.isModificationsCompatable(mp, m_Modifications);
            //  pApplication.getDigestandModificationsString()
        }
        return true;
    }
//
//    protected IScoredScan handleScan(final Scorer scorer, final ITandemScoringAlgorithm pAlgorithm, final RawPeptideScan scan, final IPolypeptide[] pPps) {
//        String id = scan.getId();
//        OriginatingScoredScan scoring = new OriginatingScoredScan(scan);
//        scoring.setAlgorithm(pAlgorithm.getName());
//        IonUseCounter counter = new IonUseCounter();
//        final ITheoreticalSpectrumSet[] tss = scorer.getAllSpectra();
//
//        int numberDotProducts = scoreScan(scorer, pAlgorithm, counter, tss, scoring);
//        getContext().getCounter("Performance", "TotalDotProducts").increment(numberDotProducts);
//        return scoring;
//    }

    public static int gNumberNotScored = 0;


    public static void showPeaks(final ISpectrum pConditionedScan, final PrintWriter out) {
        ISpectrumPeak[] peaks = pConditionedScan.getPeaks();
        int lastPeak = 0;
        for (int i = 0; i < peaks.length; i++) {
            ISpectrumPeak peak = peaks[i];
            double mz = peak.getMassChargeRatio();
            int ipeak = (int) (mz + 0.5);
            if (ipeak == lastPeak)
                continue;
            if (i > 0)
                out.print(",");
            out.print(Integer.toString(ipeak));
        }
        out.println();
    }

    private void saveTheoreticalSpectra(final Scorer pScorer) {
        final ITheoreticalSpectrumSet[] tss = pScorer.getAllSpectra();
        for (int i = 0; i < tss.length; i++) {
            ITheoreticalSpectrumSet ts = tss[i];
            ITheoreticalSpectrum[] spectra = ts.getSpectra();
            for (int j = 0; j < spectra.length; j++) {
                ITheoreticalSpectrum tsx = spectra[j];
                String id = tsx.getPeptide() + ":" + tsx.getCharge();
                m_LogJamSpectra.print(id);
                m_LogJamSpectra.print("\t");
                showPeaks(tsx, m_LogJamSpectra);

            }
        }
    }

    protected boolean doNotHandleScans(final Iterable<Text> values) {
        //     JXTandemLauncher.logMessage("Nothing to score at " + mass);
        Iterator<Text> textIterator = values.iterator();
        while (textIterator.hasNext()) {
            Text text = textIterator.next();
            String textStr = text.toString();
            RawPeptideScan scan = XTandemHadoopUtilities.readScan(textStr, null);
            if (scan == null)
                return true;

            String id = scan.getId();
            //          JXTandemLauncher.logMessage("Not scoring scan " + id + " at mass" + mass);
        }
        return false;
    }

    public void writeKeyValue(String key, String value, Context context) {
        Text onlyKey = getOnlyKey();
        Text onlyValue = getOnlyValue();
        onlyKey.set(key);
        onlyValue.set(value);
        try {
            context.write(onlyKey, onlyValue);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    protected IScoredScan handleScan(Scorer scorer, RawPeptideScan scan, IPolypeptide[] pPps) {
        String id = scan.getId();
        OriginatingScoredScan scoring = new OriginatingScoredScan(scan);
        IonUseCounter counter = new IonUseCounter();
        final ITheoreticalSpectrumSet[] tss = scorer.getAllSpectra();

        int numberDotProducts = scorer.scoreScan(counter, tss, scoring);
        getContext().getCounter("Performance", "TotalDotProducts").increment(numberDotProducts);

//        if (m_Logger != null) {
//            for (int i = 0; i < tss.length; i++) {
//                ITheoreticalSpectrumSet ts = tss[i];
//                if (scorer.isTheoreticalSpectrumScored(scoring, ts))
//                    appendScan(scan, ts, scoring);
//
//            }
//        }

        return scoring;
    }

    public void appendLine(String text) {
        if (m_Logger != null)
            m_Logger.append(text + "\n");

    }


    protected IPolypeptide[] getPeptidesOfExactMass(MassPeptideInterval interval) {
        final HadoopTandemMain application = getApplication();
        boolean semiTryptic = application.isSemiTryptic();
        IPolypeptide[] st = m_Taxonomy.getPeptidesOfExactMass(interval, semiTryptic);
//        if (m_Taxonomy instanceof JDBCTaxonomy) {
//            IPolypeptide[] not_st = ((JDBCTaxonomy) m_Taxonomy).findPeptidesOfMassIndex( interval, semiTryptic);
//            if (st.length != not_st.length)
//                XTandemUtilities.breakHere();
//
//        }
        return st;
    }

    @Override
    protected void cleanup(final Context context) throws IOException, InterruptedException {
        if (m_Logger != null) {
            appendLine("</JXTandem>");
              m_Logger.close();
        }
        //   context.getCounter("Performance", "TotalPeptide").increment(m_TotalPeptidesScored);

        //    System.err.println("Total scans " + m_TotalScansScored);
        //    System.err.println("Total peptide " + m_TotalPeptidesScored);
        // Special code for logjam
        if (USING_LOGJAM) {
            m_LogJamSpectra.close();
        }
        super.cleanup(context);
    }
}
