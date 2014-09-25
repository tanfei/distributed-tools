package org.systemsbiology.xtandem;

/**
 * org.systemsbiology.xtandem.ProtienMotif
 *
 * @author Steve Lewis
 * @date Dec 21, 2010
 */
/*
 * mmotif is a specialty class meant to store information about protein sequence motifs.
 * NOTE: mmotif.h does not have a corresponding .cpp file
 */

public class ProteinMotifRes
{
    public static ProteinMotifRes[] EMPTY_ARRAY = {};
    public static Class THIS_CLASS = ProteinMotifRes.class;
    
    String m_pMotif;
	boolean m_bRes;
	boolean m_bPos;
	boolean m_bIsX;

	boolean set( final String _p)	{
		if(_p == null)	{
			return false;
		}
		if(_p.length() == 0)	{
			return false;
		}
		m_pMotif= "";
		m_bIsX = false;
		m_bRes = true;
		m_bPos = true;
		if(!_p.contains("!") )	{
			m_bRes = false;
		}
		if(_p.contains("X"))	{
			m_bIsX = true;
			m_bPos = true;
			 m_pMotif = "X" ;
			return true;
		}
		String pValue = _p ;
// 		int pStart = pValue;
//		int pEnd = pStart;
//		int a = 0;
//		if(pValue.charAt(a) == '[') != null)	{
//			pStart = strchr(pValue,'[');
//			pStart++;
//			while(*pStart != ']' && *pStart != '\0')	{
//				if(isalpha(*pStart))	{
//					m_pMotif[a] = *pStart;
//				}
//				pStart++;
//				a++;
//			}
//			m_pMotif[a] = '0';
//			m_bPos = true;
//			return true;
//		}
//		else if(strchr(pValue,'{') != null)	{
//			pStart = strchr(pValue,'{');
//			pStart++;
//			while(*pStart != ']' && *pStart != '\0')	{
//				if(isalpha(*pStart))	{
//					m_pMotif[a] = *pStart;
//				}
//				pStart++;
//				a++;
//			}
//			m_pMotif[a] = '0';
//			m_bPos = false;
//			return true;
//		}
//		else	{
//			pStart = pValue;
//			while(*pStart != '0')	{
//				if(isalpha(*pStart))	{
//					m_pMotif[a] = *pStart;
//					a++;
//					break;
//				}
//				pStart++;
//			}
//			m_pMotif[a] = '\0';
//			m_bPos = true;
//		}
//		return false;
        throw new UnsupportedOperationException("Fix This"); // ToDo
	}

	boolean check( final char _c)	{
		if(m_bIsX)	{
			return true;
		}
//		if(m_bPos)	{
//			if( m_pMotif.contains(_c))	{
//				return true;
//			}
//			return false;
//		}
//		if(strchr(m_pMotif,_c) == null)	{
//			return true;
//		}
//		return false;
        throw new UnsupportedOperationException("Fix This"); // ToDo
	}

}
