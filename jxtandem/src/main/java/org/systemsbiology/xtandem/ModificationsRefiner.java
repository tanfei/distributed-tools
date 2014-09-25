package org.systemsbiology.xtandem;

import java.util.*;

/**
 * org.systemsbiology.xtandem.ModificationsRefiner
 *
 * @author Steve Lewis
 * @date Dec 21, 2010
 */

/*
 * process potential modifications using a minimum of at least 5 missed cleavages
 * derived from mpmods
 */
public class ModificationsRefiner implements IRefiner
{
    public static ModificationsRefiner[] EMPTY_ARRAY = {};
    public static Class THIS_CLASS = ModificationsRefiner.class;

    public static final int MAXIMUM_MISSED_CLEAVES = 5;

    private double m_dMaxExpect;
    // was mprocess->m_bAnnotation
    private boolean m_bAnnotation = true;
    // was mprocess->m_tMissedCleaves
    private int m_tMissedCleaves;
    // was mprocess->m_semiState
    private int m_semiState;

    /**
     * initialize from the configuration
     *
     * @param params !null configuration
     */
    public void initialize(Map<String, String> values)
    {
        /*
        *  set the maximum expectation value
        */
        String val;
        String strKey = "refine, maximum valid expectation value";
        val = values.get(strKey);
        if (val != null) {
            m_dMaxExpect = Double.parseDouble(val);
        }
        strKey = "refine, use annotations";
        val = values.get(strKey);
        if ("no".equals(val)) {
            m_bAnnotation = false;
        }
/*
 * set up the modification masses and cleavage specs
 */
        strKey = "refine, tic percent";
        val = values.get(strKey);
        double dTicPercent = Double.parseDouble(val);
        if (dTicPercent == 0) {
            dTicPercent = 20.0;
        }
//	int tTicMax = (int)((double)m_pProcess->m_vseqBest.size()*dTicPercent/100.0);
//	if(tTicMax < 1)	{
//		tTicMax = 1;
//	}
//	strKey = "scoring, maximum missed cleavage sites";
//	val  = values.get(strKey);
//	 m_tMissedCleaves = Integer.parseInt(val));
//	if( m_tMissedCleaves < 5)	{
//		 m_tMissedCleaves = 5;
//	}
//	strKey = "refine, cleavage semi";
//	val  = values.get(strKey);
//	if("yes".equals(val))	{
//		 m_semiState.activate(true);
//	}
//	else	{
//		m_pProcess->m_semiState.activate(false);
//	}
//
//	strKey = "refine, potential modification mass";
//	val  = values.get(strKey);
//	m_pProcess->m_pScore->m_seqUtil.modify_maybe(strValue);
//	strKey = "refine, potential modification motif";
//	val  = values.get(strKey);
//	m_pProcess->m_pScore->m_seqUtil.modify_motif(strValue);
//	m_pProcess->m_strLastMods.clear();
//
///*
// * use additional modifications if present, creating a rollback vector each time
// */
//	long lPMCount = 1;
//	char pValue[8];
//	sprintf(pValue," %i",lPMCount);
//	strKey = "refine, potential modification mass";
//	strKey += pValue;
//	string strMods;
//	m_pProcess->m_xmlValues.get(strKey,strMods);
//	strKey = "refine, potential modification motif";
//	strKey += pValue;
//	string strMotifs;
//	m_pProcess->m_xmlValues.get(strKey,strMotifs);
//	m_pProcess->m_strLastMods.clear();
//	while(strMods.find('@') != strMods.npos || strMotifs.find('@') != strMotifs.npos)	{
//		if(m_pProcess->m_lThread == 0 || m_pProcess->m_lThread == 0xFFFFFFFF)	{
//			cout << " done.\n";
//			m_pProcess->m_prcLog.log("done");
//			cout << "\tpartial cleavage " << lPMCount << " ";
//			cout.flush();
//		}
//		m_pProcess->m_pScore->m_seqUtil.modify_maybe(strMods);
//		m_pProcess->m_pScore->m_seqUtil.modify_motif(strMotifs);
//		if(m_pProcess->m_tMissedCleaves < 5)	{
//			m_pProcess->m_tMissedCleaves = 5;
//		}
//		a = 0;
//		tPips = 0;
//		while(a < m_pProcess->m_vseqBest.size())	{
//			m_pProcess->score(m_pProcess->m_vseqBest[a]);
//			tPips++;
//			if(tPips == tTicMax)	{
//				if(m_pProcess->m_lThread == 0 || m_pProcess->m_lThread == 0xFFFFFFFF)	{
//					cout << ".";
//					m_pProcess->m_prcLog.log(".");
//					cout.flush();
//				}
//				tPips = 0;
//			}
//			a++;
//		}
//		lPMCount++;
//		sprintf(pValue," %i",lPMCount);
//		strKey = "refine, potential modification mass";
//		strKey += pValue;
//		m_pProcess->m_xmlValues.get(strKey,strMods);
//		strKey = "refine, potential modification motif";
//		strKey += pValue;
//		m_pProcess->m_xmlValues.get(strKey,strMotifs);
//		m_pProcess->m_strLastMods.clear();
//	}
///*
// * update active state of spectra
// */
//	m_pProcess->load_best_vector();
//	a = 0;
//	while(a < m_pProcess->m_vSpectra.size())	{
//		if(!m_pProcess->m_vSpectra[a].m_bActive)
//			tActiveNow++;
//		a++;
//	}
//	if(tActiveNow >= m_pProcess->m_tActive)	{
//		m_pProcess->m_tRefinePartial = tActiveNow - m_pProcess->m_tActive;
//	}
//	m_pProcess->m_tActive = tActiveNow;
//
///*
// *  clear the potential modifications, if required
// */
//	strKey = "refine, use potential modifications for full refinement";
//	val  = values.get(strKey);
//	if(strValue != "yes")	{
//		strKey = "residue, potential modification mass";
//		val  = values.get(strKey);
//		m_pProcess->m_pScore->m_seqUtil.modify_maybe(strValue);
//		strKey = "residue, potential modification motif";
//		val  = values.get(strKey);
//		m_pProcess->m_pScore->m_seqUtil.modify_motif(strValue);
//	}
//
//	if(m_pProcess->m_lThread == 0 || m_pProcess->m_lThread == 0xFFFFFFFF)	{
//		cout << " done.\n";
//		cout.flush();
//	}
//	m_pProcess->m_bAnnotation = false;
//	m_pProcess->m_strLastMods.clear();
//	m_pProcess->m_semiState.activate(false);
//	return true;
        throw new UnsupportedOperationException("Fix This"); // ToDo
    }

//
///*
// * score the spectra against each sequence
// */
//    int a = 0;
//    while(a < m_pProcess->m_vseqBest.size())	{
//        m_pProcess->score(m_pProcess->m_vseqBest[a]);
//        tPips++;
//        if(tPips == tTicMax)	{
//            if(m_pProcess->m_lThread == 0 || m_pProcess->m_lThread == 0xFFFFFFFF)	{
//                cout << ".";
//                cout.flush();
//                m_pProcess->m_prcLog.log(".");
//            }
//            tPips = 0;
//        }
//        a++;
//    }

}
