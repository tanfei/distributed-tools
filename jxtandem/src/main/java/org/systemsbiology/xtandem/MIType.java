package org.systemsbiology.xtandem;

/**
 * org.systemsbiology.xtandem.MIType
 *
 * @author Steve Lewis
 * @date Dec 21, 2010
 */
public class MIType
{
    public static MIType[] EMPTY_ARRAY = {};
    public static Class THIS_CLASS = MIType.class;

    public MIType(int pLM, float pFI, int pLA)
    {
        m_lM = pLM;
        m_fI = pFI;
        m_lA = pLA;
    }


    public MIType(MIType o)
    {
        m_lM = o.m_lM;
        m_fI = o.m_fI;
        m_lA = o.m_lA;
    }

    private final int m_lM; // the M+H + error for an mspectrum
    private final float m_fI; // the M+H - error for an mspectrum
    private final int m_lA; // the index number for an mspectrum, in the m_vSpectra vector

    public int getlM()
    {
        return m_lM;
    }

    public float getfI()
    {
        return m_fI;
    }

    public int getlA()
    {
        return m_lA;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MIType miType = (MIType) o;

        if (Float.compare(miType.m_fI, m_fI) != 0) return false;
        if (m_lA != miType.m_lA) return false;
        if (m_lM != miType.m_lM) return false;

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = m_lM;
        result = 31 * result + (m_fI != +0.0f ? Float.floatToIntBits(m_fI) : 0);
        result = 31 * result + m_lA;
        return result;
    }
}
