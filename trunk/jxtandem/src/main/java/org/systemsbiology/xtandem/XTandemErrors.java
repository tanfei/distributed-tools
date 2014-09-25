package org.systemsbiology.xtandem;

/**
 * org.systemsbiology.xtandem.XTandemErrors
 *     This is NEVER used but was copied fron X!Tandem
 * @author Steve Lewis
 * @date Jan 4, 2011
 */
//public class XTandemErrors
//{
//    public static XTandemErrors[] EMPTY_ARRAY = {};
//    public static Class THIS_CLASS = XTandemErrors.class;
//
//   private boolean m_bPpm;
//   private boolean m_bIsotope;
//   private float m_fPlus;
//   private float m_fMinus;
//
//    /*
// * the process object coordinates the function of tandem. it contains the information
// * loaded from the input XML file in the m_xmlValues object and performance
// * information in the m_xmlPerformance object. The mass spectra to be analyzed are
// * in the m_vSpectra vector container. A set of input parameters are used to
// * initialize constants that are used in processing the mass spectra.
// */
//
//	public XTandemErrors() {
//		m_bPpm = true;
//		m_bIsotope = false;
//		m_fPlus = 100.F;
//		m_fMinus = 100.F;
//	}
// 	boolean check(double _s,double _m)	{
//		float fDelta = (float)(_s - _m);
//		float fPlus = m_fPlus;
//		float fMinus = m_fMinus;
//		if(m_bPpm)	{
//			fPlus *= (float)(_m*1.0e-6);
//			fMinus*= (float)(_m*1.0e-6);
//		}
//		if(fDelta < 0.0)	{
//			if(fDelta >= fMinus)	{
//				return true;
//			}
//		}
//		else	{
//			if(fDelta <= fPlus)	{
//				return true;
//			}
//		}
//		if(!m_bIsotope)	{
//			return false;
//		}
//		if(_s > 1000.0)	{
//			fDelta -= (float)1.00335;
//			if(fDelta < 0.0)	{
//				if(fDelta >= fMinus)	{
//					return true;
//				}
//			}
//			else	{
//				if(fDelta <= fPlus)	{
//					return true;
//				}
//			}
//		}
//		if(_s > 1500.0)	{
//			fDelta -= (float)1.00335;
//			if(fDelta < 0.0)	{
//				if(fDelta >= fMinus)	{
//					return true;
//				}
//			}
//			else	{
//				if(fDelta <= fPlus)	{
//					return true;
//				}
//			}
//		}
//		return false;
//	}
//}