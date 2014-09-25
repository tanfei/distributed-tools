package org.systemsbiology.xtandem.testing;

import com.lordjoe.utilities.*;
import org.systemsbiology.xml.*;
import org.systemsbiology.xtandem.*;
import org.systemsbiology.xtandem.bioml.*;
import org.systemsbiology.xtandem.peptide.*;
import org.systemsbiology.xtandem.sax.*;
import org.systemsbiology.xtandem.scoring.*;

import java.io.*;
import java.util.*;

/**
 * org.systemsbiology.xtandem.testing.JXTandem_XTandemCrossValidator
 * User: steven
 * Date: 6/23/11
 */
public class JXTandem_XTandemCrossValidator {
    public static final JXTandem_XTandemCrossValidator[] EMPTY_ARRAY = {};

    private final File m_XTandemFile;
    private final File m_JXTandemFile;
    private ScanScoringReport m_XTandemReport;
    private ScanScoringReport m_JXTandemReport;

    public JXTandem_XTandemCrossValidator(final String xtandemFileName, final String jxtandemFileName) {

        m_XTandemFile = new File(xtandemFileName);
        if (!m_XTandemFile.exists() || m_XTandemFile.isDirectory())
            throw new IllegalStateException("Bad XTamdemFile");
        m_JXTandemFile = new File(jxtandemFileName);
        if (!m_XTandemFile.exists() || m_JXTandemFile.isDirectory())
            throw new IllegalStateException("Bad JXTamdemFile");
    }

    public JXTandem_XTandemCrossValidator(final String xtandemFileName) {

        m_XTandemFile = new File(xtandemFileName);
        if (!m_XTandemFile.exists() || m_XTandemFile.isDirectory())
            throw new IllegalStateException("Bad XTamdemFile " + xtandemFileName);
        m_JXTandemFile = null;
     }

    public ScanScoringReport getXTandemReport() {
        return m_XTandemReport;
    }

    public ScanScoringReport getJXTandemReport() {
        return m_JXTandemReport;
    }

    public Map<String, ScoredScan> readXTandemFile() {
        InputStream is = null;
        try {
            is = new FileInputStream(m_XTandemFile);
        }
        catch (FileNotFoundException e) {
            throw new RuntimeException(e);

        }
        XTandemScoringHandler handler = new XTandemScoringHandler();
        XTandemUtilities.parseFile(is, handler, m_XTandemFile.getName());
        m_XTandemReport = handler.getReport();
        return handler.getScans();
    }

    public void showScoringReport(ScanScoringReport report) {

        if (!report.equivalent(report))
            throw new IllegalStateException("problem"); // ToDo change
        int totalScores = report.getTotalScoreCount();
        IScanScoring[] scanScoring = report.getScanScoring();
        for (int j = 0; j < scanScoring.length; j++) {
            IScanScoring scoring = scanScoring[j];
            XMLUtilities.outputLine("Scored " + scoring.getId());
            ITheoreticalScoring[] theoreticalScorings = scoring.getScorings();
            for (int k = 0; k < theoreticalScorings.length; k++) {
                ITheoreticalScoring theoreticalScoring = theoreticalScorings[k];
                XMLUtilities.outputLine("Scored   " + theoreticalScoring);
                ITheoreticalIonsScoring[] scoringMasses = theoreticalScoring.getIonScorings();
                for (int l = 0; l < scoringMasses.length; l++) {
                    ITheoreticalIonsScoring scoringMass = scoringMasses[l];
                    XMLUtilities.outputLine("Scored one " + scoringMass);
                }
            }
        }

    }

    public Map<String, ScoredScan> readJXTandemFile() {
        if (m_JXTandemFile.getName().endsWith(".xml")) {
            return readXTandemOutputFile();

        }
        if (m_JXTandemFile.getName().endsWith(".scans")) {
            return readScansFile();

        }
        throw new UnsupportedOperationException("Fix This"); // ToDo
    }


    private Map<String, ScoredScan> readScansFile() {
        Map<String, ScoredScan> ret = new HashMap<String, ScoredScan>();
        XTandemScoringReport report = XTandemUtilities.readScanScoring(m_JXTandemFile.getAbsolutePath());
        ScoredScan[] scans = report.getScans();
        for (int i = 0; i < scans.length; i++) {
            ScoredScan scan = scans[i];
            String key = scan.getId() + ":" + scan.getCharge();
            ret.put(key, scan);
        }
        return ret;
    }


    private Map<String, ScoredScan> readXTandemOutputFile() {
        XTandemScoringHandler handler = new XTandemScoringHandler();
        //     JXTandemScoringHandler handler = new JXTandemScoringHandler();
        InputStream is = null;
        try {
            is = new FileInputStream(m_JXTandemFile);
        }
        catch (FileNotFoundException e) {
            throw new RuntimeException(e);

        }
        String name = m_JXTandemFile.getName();
        XTandemUtilities.parseFile(is, handler, name);
        m_JXTandemReport = handler.getReport();
        return handler.getScans();
    }


    protected String validateScoredScans() {
        ScanScoringReport xt = getXTandemReport();
        ScanScoringReport jt = getJXTandemReport();

        IScanScoring[] xScores = xt.getScanScoring();
        IScanScoring[] jScores = jt.getScanScoring();

        writeScanReport(xScores, jScores);


        if (xScores.length > jScores.length)
            return "Different Number Scans Scored " +
                    " X " + xScores.length +
                    " J " + jScores.length;

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < xScores.length; i++) {
            IScanScoring jScore = jScores[i];
            IScanScoring xScore = xScores[i];
            String ret = validateScanScoring(jScore, xScore);
            if (ret != null)
                sb.append(ret + "\n");

        }

        if (sb.length() > 0)
            return sb.toString();

        return null;  // all ok
    }

    private void writeScanReport(final IScanScoring[] pXScores, final IScanScoring[] pJScores) {
        XMLUtilities.outputLine("<scan_report>");
        for (int i = 0; i < pXScores.length; i++) {
            IScanScoring theirScore = pXScores[i];
            IScanScoring myScore = pJScores[i];
            ITheoreticalScoring[] myScoreScorings = myScore.getScorings();
            Arrays.sort(myScoreScorings, TheoreticalScoring.K_SCORE_COMPARATOR);

            ITheoreticalScoring[] theirScorings = theirScore.getScorings();
            Arrays.sort(theirScorings, TheoreticalScoring.K_SCORE_COMPARATOR);

            // drop sequences only I score
            myScoreScorings = filterToBothScored(theirScorings, myScoreScorings);


            XMLUtilities.outputLine("<scored_id id=\"" + myScore.getId() + "\" size=\"" + myScoreScorings.length + "\"  >");
            int numberToPrint = Math.min(myScoreScorings.length, theirScorings.length);
            for (int k = 0; k < numberToPrint; k++) {
                ITheoreticalScoring jscore = myScoreScorings[k];
                double totalJKScore = jscore.getTotalKScore();
                ITheoreticalScoring xtscore = theirScorings[k];
                double totalKScore = xtscore.getTotalKScore();
                XMLUtilities.outputLine("<scored_fragment" +
                        " seguence=\"" + xtscore.getSequence() +
                        "\" score=\"" + totalKScore +
                        " jseguence=\"" + jscore.getSequence() +
                        "\" jscore=\"" + totalJKScore +

                        "\" />");

            }
            XMLUtilities.outputLine("</scored_id>");


        }
        XMLUtilities.outputLine("</scan_report>");
    }

    /**
     * for comparison ignore sequences that only I score
     *
     * @param pTheirScorings
     * @param pMyScoreScorings
     * @return
     */
    private ITheoreticalScoring[] filterToBothScored(final ITheoreticalScoring[] pTheirScorings, final ITheoreticalScoring[] pMyScoreScorings) {
        Set<String> seenSequences = new HashSet<String>();
        for (int i = 0; i < pTheirScorings.length; i++) {
            seenSequences.add(pTheirScorings[i].getSequence());

        }

        List<ITheoreticalScoring> holder = new ArrayList<ITheoreticalScoring>();
        for (int i = 0; i < pMyScoreScorings.length; i++) {
            ITheoreticalScoring ts = pMyScoreScorings[i];
            if (seenSequences.contains(ts.getSequence()))
                holder.add(ts);
            else
                continue;
        }
        ITheoreticalScoring[] ret = new ITheoreticalScoring[holder.size()];
        holder.toArray(ret);
        return ret;

    }

    protected String validateScanScoring(IScanScoring jScore, IScanScoring xScore) {
        String xid = xScore.getId();
        String jid = jScore.getId();
        if (!jid.equals(xid))
            return "Different is XTandem " + xid + " JXTandem  " + jid;
        ITheoreticalScoring[] jscorings = jScore.getScorings();
        ITheoreticalScoring[] xscorings = xScore.getScorings();
        if (xscorings.length != jscorings.length) {
            String msg = "Different Number Scored under id " + jid +
                    " X " + xscorings.length +
                    " J " + jscorings.length;
            XMLUtilities.outputLine(msg);
            showScoringDifferences(jscorings, xscorings);
            return msg;
        }

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < jscorings.length; i++) {
            ITheoreticalScoring jScore1 = jscorings[i];
            ITheoreticalScoring xScore1 = xscorings[i];
            String ret = validateTheoreticalScoring(jScore1, xScore1);
            if (ret != null)
                sb.append(ret + "\n");

        }


        if (sb.length() > 0)
            return sb.toString();

        return null;  // all ok
    }

    private void showScoringDifferences(final ITheoreticalScoring[] pJscorings, final ITheoreticalScoring[] pXscorings) {
        Set<String> xscores = new HashSet<String>();
        Set<String> jscores = new HashSet<String>();
        for (int i = 0; i < pXscorings.length; i++) {
            xscores.add(pXscorings[i].getSequence().toUpperCase());

        }
        for (int i = 0; i < pJscorings.length; i++) {
            jscores.add(pJscorings[i].getSequence().toUpperCase());

        }
        for (int i = 0; i < pXscorings.length; i++) {
            ITheoreticalScoring xscoring = pXscorings[i];
            String seq = xscoring.getSequence().toUpperCase();
            if (jscores.contains(seq))
                continue;
            jscores.add(seq); // do not repeat
            if (xscoring.getTotalKScore() <= 0)
                continue;
            XMLUtilities.outputLine("Only XScore " + seq + "  " + xscoring.getMZRatio());

        }
        for (int i = 0; i < pJscorings.length; i++) {
            String seq = pJscorings[i].getSequence().toUpperCase();
            if (xscores.contains(seq))
                continue;
            xscores.add(seq); // do not repeat
            XMLUtilities.outputLine("Only JScore " + seq);

        }

    }

    private String validateTheoreticalScoring(final ITheoreticalScoring jScore, final ITheoreticalScoring xscore) {
        String jsequence = jScore.getSequence();
        String xsequence = xscore.getSequence();
        if (!jsequence.equals(xsequence))
            return "Different Sequence Scored " + jsequence + " x " + xsequence;


        return null;  // all ok
    }


    private static void usage() {
        XMLUtilities.outputLine("Usage XTandemFile JXTandemFile");
    }


//    public static void reportScanDifferences(final Map<String,ScoredScan> xtandemScans, final Map<String,ScoredScan> jxTandemScans) {
//        Set<String> xTandemScored = xtandemScans.keySet();
//        Set<String> jxTandemScored = jxTandemScans.keySet();
//        Set<String>  commonScored = new HashSet<String>(xTandemScored) ;
//        commonScored.retainAll(jxTandemScored);
//        Set<String>  xtandemOnly = new HashSet<String>(xTandemScored) ;
//        xtandemOnly.removeAll(jxTandemScored);
//        Set<String>  jxtandemOnly = new HashSet<String>(xTandemScored) ;
//        jxtandemOnly.removeAll(xTandemScored);
//
//        System.out.println("xtandemOnly");
//        for(String key : xtandemOnly)
//            System.out.println(key);
//
//        System.out.println("jxtandemOnly");
//        for(String key : jxtandemOnly)
//            System.out.println(key);
//
//        System.out.println("commonScored");
//        int sameSequence = 0;
//        for(String key : commonScored) {
//            ScoredScan xScan = xtandemScans.get(key);
//            ScoredScan jxScan = jxTandemScans.get(key);
//            sameSequence += compareScans(xScan, jxScan);
//
//        }
//
//
//
//    }
//
//    private static int compareScans(final ScoredScan xScan, final ScoredScan jxScan) {
//
//        ISpectralMatch xMatch = xScan.getBestMatch();
//        ISpectralMatch jxMatch = jxScan.getBestMatch();
//        IPolypeptide pepX = xMatch.getPeptide();
//        IPolypeptide pepJ = jxMatch.getPeptide();
//        if(pepX.equivalent(pepJ))
//            return 1;
//        double xScore = xMatch.getHyperScore();
//        double jxScore = jxMatch.getHyperScore();
//        double del = Math.abs((xScore - jxScore) / (xScore + jxScore));
//        if(del < 0.15)
//            return 0;
//         return 0;
//    }

    private static void writeInterestingCases(ScoringComparisonReport sr) {
        ScoringComparisionResult[] answers = ScoringComparisionResult.values();
        for (int i = 0; i < answers.length; i++) {
            ScoringComparisionResult answer = answers[i];
            writeInterestingCases(sr, answer);
        }
    }

    private static void writeInterestingCases(ScoringComparisonReport sr, ScoringComparisionResult answer) {
        try {
            PrintWriter out = new PrintWriter(new FileWriter(answer.toString() + ".res"));
            for (ScoredScanPair res : sr.getPairs()) {
                ScoringComparisionResult comparison = res.getComparison();
                if (comparison != answer)
                    continue;
                writePairData(res, out);
            }
            out.close();
        }
        catch (IOException e) {
            throw new RuntimeException(e);

        }
    }

    private static void writePairData(ScoredScanPair res, PrintWriter out) {
        out.print(res.getId());
        out.print(",");
        printResult(res.getXTandemScoring(), out);
        printResult(res.getJXTandemScoring(), out);
        out.println();
    }

    private static void printResult(ScoredScan score, PrintWriter out) {
        ISpectralMatch bestMatch = score.getBestMatch();
        double hyperScore = bestMatch.getHyperScore();
        IPolypeptide peptide = bestMatch.getPeptide();
        out.print(peptide);
        out.print(",");
        out.print(String.format("%8.1f", hyperScore).trim());
        out.print(",");

    }
    private static void compareXTandemAndHydra(String xTandemFile, String jxTandemFile) {
         JXTandem_XTandemCrossValidator validator = new JXTandem_XTandemCrossValidator(xTandemFile, jxTandemFile);
         Map<String, ScoredScan> xtandemScans = validator.readXTandemFile();
         Map<String, ScoredScan> jxTandemScans = validator.readJXTandemFile();
         ScoringComparisonReport sr = new ScoringComparisonReport(xtandemScans, jxTandemScans);
         sr.reportScanDifferences(true);
         System.out.println(sr);
         writeInterestingCases(sr);
     }


    public static void main(String[] args) throws Exception {
        String xTandemFile = null;


        if (args.length > 0)
            xTandemFile = args[0];
        else
            xTandemFile = FileUtilities.getLatestFileWithExtension(".t.txt").getName();
        String JxTandemFile;
        if (args.length > 1)
            JxTandemFile = args[1];
        else
            JxTandemFile = FileUtilities.getLatestFileWithExtension("_debug.xml").getName();

        compareXTandemAndHydra(xTandemFile, JxTandemFile);
        //       sr.reportScanDifferences();
//        reportScanDifferences(xtandemScans,jxTandemScans);
//        String valid = validator.validateScoredScans();
//        if (valid != null)
//            XMLUtilities.outputLine(valid);
//        //     throw new IllegalStateException(valid);

    }


}

