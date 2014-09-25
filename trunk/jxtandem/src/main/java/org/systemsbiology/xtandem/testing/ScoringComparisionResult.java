package org.systemsbiology.xtandem.testing;

import org.systemsbiology.xtandem.peptide.*;
import org.systemsbiology.xtandem.scoring.*;

/**
 * org.systemsbiology.xtandem.testing.ScoringComparisionResult
 * User: steven
 * Date: 4/18/12
 */
public enum ScoringComparisionResult {
    Equal, Close, XtandemBetter, JXTandemBetter, XtandemMuchBetter, JXTandemMuchBetter;
    public static final ScoringComparisionResult[] EMPTY_ARRAY = {};


    public static final double MINIMUM_DIFFERENCE = 0.20;
    public static final double BIG_DIFFERENCE = 0.50;

    public static final ScoringComparisionResult compareScans(ScoredScan xScan, ScoredScan jxScan) {
        ISpectralMatch xMatch = xScan.getBestMatch();
        ISpectralMatch jxMatch = jxScan.getBestMatch();
        if (xMatch == null || jxMatch == null) {
            return null;

        }
        IPolypeptide pepX = xMatch.getPeptide();
        IPolypeptide pepJ = jxMatch.getPeptide();

        double xScore = xMatch.getHyperScore();
        double jxScore = jxMatch.getHyperScore();

        if (pepX.equivalent(pepJ)) {
            return Equal;
        }
        double xdel = (xScore - jxScore) / ((xScore + jxScore) / 2);
        double del = Math.abs(xdel);
        if (del < ScoringComparisionResult.MINIMUM_DIFFERENCE) {
            return Close;
        }
        System.out.println("old result " + jxMatch);
        System.out.println("new result " + xMatch);
         if (xdel > 0) {
            if (del > ScoringComparisionResult.BIG_DIFFERENCE)
                return XtandemMuchBetter;
            return XtandemBetter;
        }
        else {
            if (del > ScoringComparisionResult.BIG_DIFFERENCE)
                return JXTandemMuchBetter;
            return JXTandemBetter;
        }
    }


}
