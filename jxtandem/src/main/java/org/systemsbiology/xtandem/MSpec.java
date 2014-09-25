package org.systemsbiology.xtandem;

/**
 * org.systemsbiology.xtandem.MSpec
 *
 * @author Steve Lewis
 * @date Dec 21, 2010
 */
public class MSpec
{
    public static MSpec[] EMPTY_ARRAY = {};
    public static Class THIS_CLASS = MSpec.class;

    public MSpec(MSpec o)
     {
         m_fMH = o.m_fMH;
         m_fZ = o.m_fZ;
     }
    public MSpec(double pFMH, float pFZ)
     {
         m_fMH = pFMH;
         m_fZ = pFZ;
     }
    public MSpec(double pFMH )
     {
         this(pFMH,1.0F);
     }

    private final double m_fMH; // the M+H value of an mspectrum
	private final float m_fZ ; // the charge value of an mspectrum
 }
