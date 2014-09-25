package org.systemsbiology.xtandem;

/**
 * org.systemsbiology.xtandem.ProteinMofif
 *
 * @author Steve Lewis
 * @date Dec 21, 2010
 */
public class ProteinMofif
{
    public static ProteinMofif[] EMPTY_ARRAY = {};
    public static Class THIS_CLASS = ProteinMofif.class;


    private String m_pMotif;
    private boolean m_bRes;
    private boolean m_bPos;
    private boolean m_bIsX;

    ProteinMofif()
    {
    }

    ProteinMofif(final ProteinMofif rhs)
    {
        m_pMotif = rhs.m_pMotif;
        m_bRes = rhs.m_bRes;
        m_bPos = rhs.m_bPos;
        m_bIsX = rhs.m_bIsX;
    }

    public void initialize() {
        
    }


    boolean set(String _p)
    {
        if (_p == null) {
            return false;
        }
        if (_p.length() == 0) {
            return false;
        }
        m_pMotif = _p;
        m_bIsX = false;
        m_bRes = true;
        m_bPos = true;
        if (!_p.contains("!")) {
            m_bRes = false;
        }
        if (_p.contains("X")) {
            m_bIsX = true;
            m_bPos = true;
            m_pMotif = "X";
            return true;
        }

        String pValue = _p;
        int pStart = 0;
        int a = 0;
        int index = pValue.indexOf("[");
        if (index > -1) {
            pStart = index;
            pStart++;
            StringBuilder sb = new StringBuilder();
            while (pStart < _p.length() && _p.charAt(pStart) != ']') {
                char c = _p.charAt(pStart);
                if (Character.isLetter(c)) {
                    sb.append(c);
                }
                pStart++;
            }
            m_pMotif = sb.toString();
            m_bPos = true;
            return true;
        }
        index = pValue.indexOf("{");
        if (index > -1) {
            pStart = index;
            pStart++;
            StringBuilder sb = new StringBuilder();
            while (pStart < _p.length() && _p.charAt(pStart) != ']') {
                char c = _p.charAt(pStart);
                if (Character.isLetter(c)) {
                    sb.append(c);
                }
                pStart++;
            }
            m_pMotif = sb.toString();
            m_bPos = false;
            return true;
        }
        else {
            pStart = 0;
            while ( _p.charAt(pStart) != '0'){
                char c = _p.charAt(pStart);
                if (Character.isLetter(c)) {
                    break;
                }
                  pStart++;
            }
            m_pMotif = _p.substring(pStart,pStart + 1);
            m_bPos = true;
        }
        return false;
    }

    boolean check(final char _c)
    {
        if (m_bIsX) {
            return true;
        }
        if (m_bPos) {
            if (m_pMotif.indexOf(_c) > -1) {
                return true;
            }
            return false;
        }
        if (m_pMotif.indexOf(_c) == -1) {
            return true;
        }
        return false;
    }


}
