package org.systemsbiology.xtandem;

/**
 * org.systemsbiology.xtandem.SemiState
 *
 * @author Steve Lewis
 * @date Dec 21, 2010
 */
public class SemiState
{
    public static SemiState[] EMPTY_ARRAY = {};
    public static Class THIS_CLASS = SemiState.class;
    
//private:  for serialization access bpratt
    boolean m_bActive;
    long m_lStart;
    long m_lEnd;
    long m_lStartI;
    long m_lEndI;
    boolean m_bStart;
    long m_lLimit;
    long m_lLastCleave;
    
    public SemiState( ) { 
      m_lLastCleave = -1;
        m_bStart = true;
      m_lLimit = 5;
      m_bActive = false;
    }
 
    boolean activate( final boolean _b)	{
        m_bActive = _b;
        return m_bActive;
    }

    boolean is_active()	{
        return m_bActive;
    }

    long limit( final long _l)	{
        if(_l >= 0)	{
            m_lLimit = _l;
        }
        return m_lLimit;
    }

    boolean reset( final long _s, final long _e, final long _c)	{
        if(!m_bActive)	{
            return false;
        }
        m_lLastCleave = _c;
        m_lStartI = _s;
        m_lEndI =_e;
        m_lStart = _s;
        m_lEnd = _e;
        m_lEnd--;
        m_bStart = true;
        return true;
    }

    public boolean next(long[] _s,long[] _e)	 {
        if(!m_bActive)	{
            return false;
        }
        if(m_bStart)	{
            _s[0] = m_lStart;
            _e[0] = m_lEnd;
            m_lEnd--;
            if(m_lEnd == m_lLastCleave || m_lEnd - m_lStart < m_lLimit)	{
                m_bStart = false;
                m_lEnd = m_lEndI;
                m_lStart++;
            }
            return true;
        }
        else	{
            if((m_lLastCleave != -1 && m_lStart > m_lLastCleave) || m_lEnd - m_lStart < m_lLimit)	{
                _s[0] = m_lStartI;
                _e[0] = m_lEndI;
                return false;
            }
            _s[0] = m_lStart;
            _e[0] = m_lEnd;
            m_lStart++;
            return true;
        }
     }

 }
