package org.systemsbiology.xtandem;

/**
 * org.systemsbiology.xtandem.mcleave_single
 *
 * @author Steve Lewis
  */
public class mcleave_single
{
    public static mcleave_single[] EMPTY_ARRAY = {};
    public static Class THIS_CLASS = mcleave_single.class;
/*
 * mcleave is a specialty class meant to store information about protein cleavage specificity
 * and rapidly test a peptide sequence to see if it is cleavable.
 * NOTE: mcleave.h does not have a corresponding .cpp file
 */
    String m_pNCleave = "KR"; // residues that are valid cleavage sites (or invalid if m_bN = false)
     String m_pCCleave = "P"; // residues that are valid cleavage sites (or invalid if m_bC = false)
     boolean m_bN; // if true, all residues in m_pNCleave can be N-temrinal to a cleavable bond
     boolean m_bC; // if true, all residues in m_pNCleave can be C-temrinal to a cleavable bond
     boolean m_bCX;
     boolean m_bNX;
     int m_lType;
  
        mcleave_single( )	{
           m_bN = true;
            m_bC = false;
            m_bNX = false;
            m_bCX = false;
            m_lType = 0;
        }
    
         mcleave_single (final mcleave_single rhs)	{
            m_pNCleave = rhs.m_pNCleave ;
             m_pCCleave = rhs.m_pCCleave;
            m_bN = rhs.m_bN;
            m_bC = rhs.m_bC;
            m_bNX = rhs.m_bNX;
            m_bCX = rhs.m_bCX;
            m_lType = rhs.m_lType;
          }
        /*
     * load takes a string containing cleavage information and parses it
     * these strings take the format:
     * A|B, where A or B are expressions of the form [xyz] or {xyz}. if square brackets, only the single letter
     * abbreviations in the brackets are valid at that position for a cleavage, and if french brackets, those
     * single letter abbreviations are the only non-valid residues at that position. For example, trypsin 
     * can be represented by [KR]|{P}, i.e. cleave C-terminal to a K or R residue, except when followed by
     * a P. The expression [X]|[X] means cleave at all residues.
     * NOTE: use of upper case characters is required for amino acid abbreviations
     */
        boolean load(String _s)	{
            if( "[X]|[X]".equals(_s))	{
                m_lType = 0x01;
                return true;
            }
            else if(  "[KR]|{P}".equals(_s) ||   "[RK]|{P}".equals(_s))	{
                m_lType = 0x02;
                return true;
            }
            m_lType = 0x04;
            int a = 0;
            int b = 0;
            throw new UnsupportedOperationException("Fix This"); // ToDo
//            if(_s[a] == '[')	{
//                m_bN = true;
//                a++;
//                b = 0;
//                while(a < _s.length() && _s[a] != ']')	{
//                    m_pNCleave[b] = _s[a];
//                    a++;
//                    b++;
//                }
//                m_pNCleave[b] = '\0';
//                a = _s.find('|');
//                if(a == _s.npos)
//                    return false;
//                a++;
//                if(_s[a] == '{')	{
//                    m_bC = false;
//                    a++;
//                    b = 0;
//                    while(a < _s.size() && _s[a] != '}')	{
//                        m_pCCleave[b] = _s[a];
//                        a++;
//                        b++;
//                    }
//                    m_pCCleave[b] = '\0';
//                }
//                else if(_s[a] == '[')	{
//                    m_bC = true;
//                    a++;
//                    b = 0;
//                    while(a < _s.size() && _s[a] != ']')	{
//                        m_pCCleave[b] = _s[a];
//                        a++;
//                        b++;
//                    }
//                    m_pCCleave[b] = '\0';
//                }
//            }
//            else if(_s[a] == '{')	{
//                m_bN = false;
//                a++;
//                b = 0;
//                while(a < _s.size() && _s[a] != '}')	{
//                    m_pNCleave[b] = _s[a];
//                    a++;
//                    b++;
//                }
//                m_pNCleave[b] = '\0';
//                a = _s.find('|');
//                if(a == _s.npos)
//                    return false;
//                a++;
//                if(_s[a] == '{')	{
//                    m_bC = false;
//                    a++;
//                    b = 0;
//                    while(a < _s.size() && _s[a] != '}')	{
//                        m_pCCleave[b] = _s[a];
//                        a++;
//                        b++;
//                    }
//                    m_pCCleave[b] = '\0';
//                }
//                else if(_s[a] == '[')	{
//                    m_bC = true;
//                    a++;
//                    b = 0;
//                    while(a < _s.size() && _s[a] != ']')	{
//                        m_pCCleave[b] = _s[a];
//                        a++;
//                        b++;
//                    }
//                    m_pCCleave[b] = '\0';
//                }
//            }
//            if(m_pNCleave[0] == 'X')	{
//                m_bNX = true;
//            }
//            if(m_pCCleave[0] == 'X')	{
//                m_bCX = true;
//            }
//            return true;
        }
/*
 * test takes the abbreviations for the residue N-terminal to a potentially cleaved bond
 * and the residue C-terminal to the bond and checks to see if the bond can be cleaved
 * according to the rules stored in the load method. load must always be called at least once
 * prior to using test, or the results may be unpredictable
 */
        boolean test(final char _n,final char _c)	{
            if((m_lType & 0x01)  != 0)
                return true;
            if((m_lType & 0x021)  != 0)	{
                if(_n == 'K' || _n == 'R')	{
                    if(_c != 'P')	{
                        return true;
                    }
                }
                return false;
            }
            boolean bReturn = false;
            boolean bN = false;
            boolean bC = false;
            if(m_bNX)	{
                bN = true;
            }
            throw new UnsupportedOperationException("Fix This"); // ToDo
//            else	{
//                if(strchr(m_pNCleave,_n))
//                    bN = true;
//            }
//            if(m_bN)	{
//                bReturn = bN;
//            }
//            else	{
//                bReturn = !bN;
//            }
//            if(!bReturn)
//                return false;
//            if(m_bCX)	{
//                bC = true;
//            }
//            else	{
//                if(strchr(m_pCCleave,_c))
//                    bC = true;
//            }
//            if(m_bC && bC)	{
//                return true;
//            }
//            else if(!m_bC && !bC)	{
//                return true;
//            }
//            return false;
        }
    } 

