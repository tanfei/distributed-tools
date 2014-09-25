package org.systemsbiology.xtandem.testing;

import org.systemsbiology.xtandem.scoring.*;

import java.util.*;

/**
 * org.systemsbiology.xtandem.testing.ScoringComparisonReport
 * User: Steve
 * Date: 4/17/12
 */
public class ScoringComparisonReport {
    public static final ScoringComparisonReport[] EMPTY_ARRAY = {};

    private final Map<String, ScoredScan> m_XTandemReport;
    private final Map<String, ScoredScan> m_JXTandemReport;
    private final List<ScoredScanPair> m_Pairs = new ArrayList<ScoredScanPair>();
    private int m_NumberEqual;
    private int m_NumberClose;
    private int m_NumberXTandemBetter;
    private int m_NumberJXTandemBetter;
    private int m_NumberXTandemMuchBetter;
    private int m_NumberJXTandemMuchBetter;
    private int m_NumberXTandemOnly;
    private int m_NumberJXTandemOnly;
    private int m_NumberInTopEight;
    private int m_NumberCompared;


    public ScoringComparisonReport(final Map<String, ScoredScan> XTandemReport, final Map<String, ScoredScan> JXTandemReport) {
        m_XTandemReport = XTandemReport;
        m_JXTandemReport = JXTandemReport;
    }

    public Map<String, ScoredScan> getXTandemReport() {
        return m_XTandemReport;
    }

    public Map<String, ScoredScan> getJXTandemReport() {
        return m_JXTandemReport;
    }

    public Iterable<ScoredScanPair> getPairs() {
        return Collections.unmodifiableList(m_Pairs);
    }


    public void reportScanDifferences(boolean answer) {
        Map<String, ScoredScan> xtandemScans = getXTandemReport();
        Map<String, ScoredScan> jxTandemScans = getJXTandemReport();

        Set<String> xTandemScored = xtandemScans.keySet();
        Set<String> jxTandemScored = jxTandemScans.keySet();
        Set<String> commonScored = new HashSet<String>(xTandemScored);
        commonScored.retainAll(jxTandemScored);
        Set<String> xtandemOnly = new HashSet<String>(xTandemScored);
        xtandemOnly.removeAll(jxTandemScored);
        Set<String> jxtandemOnly = new HashSet<String>(xTandemScored);
        jxtandemOnly.removeAll(xTandemScored);

        String[] xtandemOnlyIds = xtandemOnly.toArray(new String[0]) ;
        Arrays.sort(xtandemOnlyIds);
        System.out.println("xtandemOnly " + xtandemOnlyIds.length);
        for (String key : xtandemOnlyIds)
            System.out.println("\"" + key + "\",");

        System.out.println("jxtandemOnly");
        for (String key : jxtandemOnly)
            System.out.println(key);

        System.out.println("commonScored");
        int sameSequence = 0;
        for (String key : commonScored) {
            ScoredScan xScan = xtandemScans.get(key);
            ScoredScan jxScan = jxTandemScans.get(key);
            if (xScan == null || jxScan == null)
                throw new IllegalStateException("problem"); // ToDo change
            compareScans(xScan, jxScan);
        }
        if (commonScored.size() < 10) {
            for (String key : commonScored) {
                ScoredScan xScan = xtandemScans.get(key);
                ScoredScan jxScan = jxTandemScans.get(key);
                System.out.print(xScan.getBestMatch().getPeptide());
                System.out.print(" ");
                System.out.print(jxScan.getBestMatch().getPeptide());
                System.out.print(" ");
            }
            System.out.println();
        }
    }


    protected void compareScans(final ScoredScan xScan, final ScoredScan jxScan) {
        ScoringComparisionResult res = ScoringComparisionResult.compareScans(xScan, jxScan);
        if (res == null) {
            if (compareUnscoredScans(xScan, jxScan)) return;

        }
        try {
            ScoredScanPair e = new ScoredScanPair(xScan, jxScan);
            m_Pairs.add(e);
            if (e.getEquivalentMatch() != null)
                m_NumberInTopEight++;

            m_NumberCompared++;
        }
        catch (Exception e) {
            throw new RuntimeException(e);

        }
        switch (res) {
            case Equal:
                m_NumberEqual++;
                return;
            case Close:
                m_NumberClose++;
                return;
            case XtandemBetter:
                m_NumberXTandemBetter++;
                return;
            case XtandemMuchBetter:
                m_NumberXTandemMuchBetter++;
                return;
            case JXTandemBetter:
                m_NumberJXTandemBetter++;
                return;
            case JXTandemMuchBetter:
                m_NumberJXTandemMuchBetter++;
                return;
            default:
                throw new UnsupportedOperationException("Never get here");
        }
    }

    private boolean compareUnscoredScans(ScoredScan xScan, ScoredScan jxScan) {
        ISpectralMatch xMatch = xScan.getBestMatch();
        ISpectralMatch jxMatch = jxScan.getBestMatch();
        if (xMatch == null && jxMatch == null) {
            return true;
        }

        if (xMatch == null || jxMatch == null) {
            if (xMatch == null) {
                m_NumberJXTandemOnly++;
                System.out.println("not scored by new - old result " + jxMatch);
                return true;
            }
            if (jxMatch == null) {
                System.out.println("not scored ny old - new result " + jxMatch);
                m_NumberXTandemOnly++;
                return true;
            }

        }
        return false;
    }

    public int getNumberCompared() {
        return m_NumberCompared;
    }

    public int getNumberInTopEight() {
        return m_NumberInTopEight;
    }

    public void setNumberInTopEight(int numberInTopEight) {
        m_NumberInTopEight = numberInTopEight;
    }

    public int getNumberEqual() {
        return m_NumberEqual;
    }

    public int getNumberClose() {
        return m_NumberClose;
    }

    public int getNumberXTandemBetter() {
        return m_NumberXTandemBetter;
    }

    public int getNumberJXTandemBetter() {
        return m_NumberJXTandemBetter;
    }

    public int getNumberXTandemOnly() {
        return m_NumberXTandemOnly;
    }

    public int getNumberJXTandemOnly() {
        return m_NumberJXTandemOnly;
    }

    public int getNumberXTandemMuchBetter() {
        return m_NumberXTandemMuchBetter;
    }

    public int getNumberJXTandemMuchBetter() {
        return m_NumberJXTandemMuchBetter;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Number Equal " + getNumberEqual() + "\n");
        sb.append("Number Close " + getNumberClose() + "\n");
        sb.append("Number XTandemBetter " + getNumberXTandemBetter() + "\n");
        sb.append("Number XTandemMuchBetter " + getNumberXTandemMuchBetter() + "\n");
        sb.append("Number JXTandemBetter " + getNumberJXTandemBetter() + "\n");
        sb.append("Number JXTandemMuchBetter " + getNumberJXTandemMuchBetter() + "\n");
        int numberInTopEight = getNumberInTopEight();
        if (numberInTopEight > 0)
            sb.append("Number in top eight " + numberInTopEight + "\n");
        sb.append("Number Compared " + getNumberCompared() + "\n");
        if (getNumberJXTandemOnly() > 0)
            sb.append("Number JXTandemOnly " + getNumberJXTandemOnly() + "\n");
        if (getNumberXTandemOnly() > 0)
            sb.append("Number XTandemOnly " + getNumberXTandemOnly() + "\n");
        return sb.toString();
    }
}
