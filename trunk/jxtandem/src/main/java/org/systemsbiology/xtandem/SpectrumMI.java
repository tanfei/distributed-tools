package org.systemsbiology.xtandem;

/**
 * org.systemsbiology.xtandem.SpectrumMI
 *
 * @author Steve Lewis
 * @date Dec 22, 2010
 */
/*
 * mi objects contain the m/z - intensity information about a single peak in a spectrum
 */

public class SpectrumMI
{
    public static SpectrumMI[] EMPTY_ARRAY = {};
    public static Class THIS_CLASS = SpectrumMI.class;


	SpectrumMI( ) {
		m_fM = 0.0F;
		m_fI = 1.0F;
	}

	private float m_fM; // the m/z value
	private float m_fI; // the intensity value
/*
 * a simple copy operation, using the = operator
 */
	SpectrumMI(SpectrumMI rhs)	{
		m_fM = rhs.m_fM;
		m_fI = rhs.m_fI;
 	}

    public float getfM()
    {
        return m_fM;
    }

    public void setfM(float pFM)
    {
        m_fM = pFM;
    }

    public float getfI()
    {
        return m_fI;
    }

    public void setfI(float pFI)
    {
        m_fI = pFI;
    }
}
