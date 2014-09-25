package org.systemsbiology.xtandem.scoring;

import com.lordjoe.utilities.*;
import org.systemsbiology.xtandem.*;
import org.systemsbiology.xtandem.bioml.*;

import java.io.*;
import java.util.*;

/**
 * org.systemsbiology.xtandem.scoring.FalseDiscoveryReporter
 * User: steven
 * Date: 3/7/12
 */
public class FalseDiscoveryReporter {
    public static final FalseDiscoveryReporter[] EMPTY_ARRAY = {};

    private boolean m_Normalized;
    private double m_MaxScore;
    private double m_MinScore;
    private final List<Double> m_Scores = new ArrayList<Double>();
    private final List<Double> m_DecoyScores = new ArrayList<Double>();

    protected boolean isNormalized() {
        return m_Normalized;
    }

    protected void setNormalized(final boolean pNormalized) {
        m_Normalized = pNormalized;
    }

    public void guaranteeNormalized() {
        if (isNormalized())
            return;
        Collections.sort(m_Scores);
        Collections.sort(m_DecoyScores);
        m_MaxScore = computeMaximumScore();
        m_MinScore = computeMinimumScore();
        setNormalized(true);
    }

    public double computeMinimumScore() {
        guaranteeNormalized();
        double ret = 0;
        if (!m_Scores.isEmpty())
            ret = Math.min(ret, m_Scores.get(m_Scores.size() - 1));
        if (!m_DecoyScores.isEmpty())
            ret = Math.min(ret, m_DecoyScores.get(m_DecoyScores.size() - 1));
        return ret;
    }

    public double computeMaximumScore() {
        guaranteeNormalized();
        double ret = 0;
        if (!m_Scores.isEmpty())
            ret = Math.max(ret, m_Scores.get(m_Scores.size() - 1));
        if (!m_DecoyScores.isEmpty())
            ret = Math.max(ret, m_DecoyScores.get(m_DecoyScores.size() - 1));
        return ret;
    }

    public double getMaxScore() {
        return m_MaxScore;
    }

    public double getMinScore() {
        return m_MinScore;
    }

    public void addScore(double score) {
        m_Scores.add(score);
        setNormalized(false);
    }

    public void addDecoyScore(double score) {
        m_DecoyScores.add(score);
        setNormalized(false);

    }

    public double getFalseDiscoveryRate(double score) {
        throw new UnsupportedOperationException("Fix This"); // ToDo
    }

    private static Double[] getScores(final String fileName) {

        XTandemScoringReport report = XTandemUtilities.readXTandemFile(fileName);
        ScoredScan[] scans = report.getScans();
        return getScores(scans);
    }

    protected static Double[] getScores(ScoredScan[] scans) {
        List<Double> holder = new ArrayList<Double>();
        for (int i = 0; i < scans.length; i++) {
            ScoredScan scan = scans[i];
            ISpectralMatch bestMatch = scan.getBestMatch();
            if (bestMatch == null)
                continue;
            double hyperScore = bestMatch.getHyperScore();
            holder.add(hyperScore);
        }
        Double[] ret = new Double[holder.size()];
        holder.toArray(ret);
        Arrays.sort(ret);
        return ret;
    }


    protected static Double[] getScores(String[] keys, Map<String, ScoredScan> scanMsp) {
        List<Double> holder = new ArrayList<Double>();
        for (int i = 0; i < keys.length; i++) {
            ScoredScan scan = scanMsp.get(keys[i]);
            ISpectralMatch bestMatch = scan.getBestMatch();
            if (bestMatch == null)
                continue;
            double hyperScore = bestMatch.getHyperScore();
            holder.add(hyperScore);
        }
        Double[] ret = new Double[holder.size()];
        holder.toArray(ret);
        Arrays.sort(ret);
        return ret;
    }

    private static void evaluateScores(String[] args) {

        XTandemScoringReport report = XTandemUtilities.readXTandemFile(args[0]);
        XTandemScoringReport report2 = XTandemUtilities.readXTandemFile(args[1]);
        ScoredScan[] scans = report.getScans();
        Map<String, ScoredScan> holder = new HashMap<String, ScoredScan>();
        for (int i = 0; i < scans.length; i++) {
            ScoredScan scan = scans[i];
            holder.put(scan.getId(), scan);
        }
        ScoredScan[] scans2 = report2.getScans();
        Map<String, ScoredScan> holder2 = new HashMap<String, ScoredScan>();
        for (int i = 0; i < scans2.length; i++) {
            ScoredScan scan = scans2[i];
            holder2.put(scan.getId(), scan);
        }

        Set commonKeys = new HashSet<String>(holder.keySet());
        commonKeys.retainAll(holder2.keySet());
        String[] common = (String[]) commonKeys.toArray(Util.EMPTY_STRING_ARRAY);

        Set uncommonKeys = new HashSet<String>(holder.keySet());
        uncommonKeys.removeAll(holder2.keySet());
        String[] uncommon = (String[]) uncommonKeys.toArray(Util.EMPTY_STRING_ARRAY);
        Arrays.sort(uncommon,Util.STRING_AS_NUMBER_COMPARE);

        Double[] myScores = getScores(common, holder);
         writeScores(args[0].replace(".xml","_jx.scores"), myScores);

        Double[] theirScores = getScores(common, holder2);
        writeScores(args[1].replace(".xml","_tandem.scores"), theirScores);
//
//        for (int i = 0; i < uncommon.length; i++) {
//            String s = uncommon[i];
//            ScoredScan scan = holder.get(s);
//            System.out.println(s + " " + scan.getBestMatch().getHyperScore());
//        }
    }

    private static void showScores(final String pNormalFile) throws IOException {
        Double[] normalScores = getScores(pNormalFile);

        writeScores(pNormalFile, normalScores);
    }

    private static void writeScores(final String pNormalFile, final Double[] pNormalScores)  {
        try {
            PrintWriter out = new PrintWriter(new FileWriter(pNormalFile.replace(".xml", ".txt")));
            for (int i = 0; i < pNormalScores.length; i++) {
                Double normalScore = pNormalScores[i];
                out.println(String.format("%10.4f", normalScore).trim());
            }
            out.close();
        }
        catch (IOException e) {
            throw new RuntimeException(e);

        }
    }

    public static void main(String[] args) throws IOException {
        evaluateScores(args);
        String normalFile = args[0];
    //    showScores(normalFile);
    }


}
