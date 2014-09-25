package org.systemsbiology.xtandem.testing;

/**
 * org.systemsbiology.xtandem.testing.MassScoring
 * User: steven
 * Date: 6/22/11
 */
public class MassScoring {
    public static final MassScoring[] EMPTY_ARRAY = {};

    private final Integer m_MassChargeRatio;
    private final double m_Score;
    private final int m_Offset;

    public MassScoring(final Integer pMassChargeRatio, final double pScore, final int pOffset) {
        m_MassChargeRatio = pMassChargeRatio;
        m_Score = pScore;
        m_Offset = pOffset;
    }


    @Override
    public String toString() {
        return getMassChargeRatio().toString() + " " + getScore();    //To change body of overridden methods use File | Settings | File Templates.
    }



    public Integer getMassChargeRatio() {
        return m_MassChargeRatio;
    }

    public double getScore() {
        return m_Score;
    }

    public int getOffset() {
        return m_Offset;
    }
}
