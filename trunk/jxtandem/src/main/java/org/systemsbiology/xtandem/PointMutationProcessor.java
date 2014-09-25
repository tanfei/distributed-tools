package org.systemsbiology.xtandem;

import java.util.*;

/**
 * org.systemsbiology.xtandem.PointMutationProcessor
 *
 * @author Steve Lewis
 * @date Dec 21, 2010
 */

/**
 * derived from mpam
 */
public class PointMutationProcessor
{
    public static PointMutationProcessor[] EMPTY_ARRAY = {};
    public static Class THIS_CLASS = PointMutationProcessor.class;
    
    public void loadFromConfig(Map<String,String> values) {
/*
 *  set the maximum expectation value
 */     String val;
        String strKey = "refine, tic percent";
        val  = values.get(strKey);
        double dTicPercent = Double.parseDouble(val);
        if(dTicPercent == 0)	{
            dTicPercent = 20.0;
        }
        throw new UnsupportedOperationException("Fix This"); // ToDo
//        int tTicMax = (int)((double)m_pProcess->m_vseqBest.size()*dTicPercent/100.0);
//        if(tTicMax < 1)	{
//            tTicMax = 1;
//        }
//        m_pProcess->m_semiState.activate(false);
//        strKey = "refine, maximum valid expectation value";
//        val  = values.get(strKey);
//        if(strValue.size() > 0)	{
//            m_dMaxExpect = Double.parseDouble(val);
//        }
//        if(m_pProcess->m_lThread == 0 || m_pProcess->m_lThread == 0xFFFFFFFF)	{
//            cout << "\tpoint mutations ";
//            cout.flush();
//            m_pProcess->m_prcLog.log("point mutations");
//        }
//        m_pProcess->create_rollback(vspRollback);
//        strKey = "protein, cleavage site";
//        val  = values.get(strKey);
//        m_pProcess->m_Cleave.load(strValue);
//        strKey = "scoring, maximum missed cleavage sites";
//        val  = values.get(strKey);
//        m_pProcess->m_tMissedCleaves = atoi(strValue.c_str());
//        m_pProcess->m_pScore->set_pam(true);
//
    }
}
