package org.systemsbiology.xtandem;

import java.util.*;

/**
 * org.systemsbiology.xtandem.ProteinClevage
 *
 * @author Steve Lewis
 * @date Jan 4, 2011
 */
public class ProteinClevage
{
    public static ProteinClevage[] EMPTY_ARRAY = {};
    public static Class THIS_CLASS = ProteinClevage.class;

    public ProteinClevage()
    {
        m_lType = 0;
        m_vCleaves.clear();
    }

    private final List<mcleave_single> m_vCleaves = new ArrayList<mcleave_single>();
    private int m_lType;
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

    boolean load(String _s)
    {
        m_lType = 0x04;
        int a = 0;
        int tEnd = _s.length();
        String strTemp;
        mcleave_single clvTemp;
        m_vCleaves.clear();
//        while (a < tEnd) {
//            if (_s[a] == ',') {
//                if (clvTemp.load(strTemp)) {
//                    m_vCleaves.push_back(clvTemp);
//                }
//                strTemp.erase(0, strTemp.size());
//            }
//            else if (strchr("ABCDEFGHIJKLMNOPQRSTUVWXYZ[]{}|", _s[a])) {
//                strTemp += _s[a];
//            }
//            else if (_s[a] >= 'a' && _s[a] <= 'z') {
//                strTemp += (char) (_s[a] - 32);
//            }
//            a++;
//        }
//        if (!strTemp.empty()) {
//            if (clvTemp.load(strTemp)) {
//                m_vCleaves.push_back(clvTemp);
//            }
//        }
//        m_itStart = m_vCleaves.begin();
//        m_itEnd = m_vCleaves.end();
//        if (m_vCleaves.size() == 1) {
//            m_lType = m_vCleaves[0].m_lType;
//        }
//        return !m_vCleaves.isEmpty();
        throw new UnsupportedOperationException("Fix This"); // ToDo
    }
/*
 * test takes the abbreviations for the residue N-terminal to a potentially cleaved bond
 * and the residue C-terminal to the bond and checks to see if the bond can be cleaved
 * according to the rules stored in the load method. load must always be called at least once
 * prior to using test, or the results may be unpredictable
 */

    boolean test(final char _n, final char _c)
    {
        if ((m_lType & 0x01) == 0)
            return true;
        if ((m_lType & 0x02) == 0) {
            if (_n == 'K' || _n == 'R') {
                if (_c != 'P') {
                    return true;
                }
            }
            return false;
        }
        throw new UnsupportedOperationException("Fix This"); // ToDo
//        m_itValue = m_itStart;
//        while (m_itValue != m_itEnd) {
//            if (m_itValue - > test(_n, _c)) {
//                return true;
//            }
//            m_itValue++;
//        }
//        return false;
    }
}
