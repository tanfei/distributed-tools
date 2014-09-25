/**
 * 
 */
package org.systemsbiology.xtandem.testing.pickscored;

import org.systemsbiology.xtandem.scoring.*;

import static org.systemsbiology.xtandem.scoring.ISpectralMatch.SCORE_COMPARATOR;

/**
 * compares the hyberscores of the best matches for the {@link ScoredScan}s
 * @author michael
 */
public class HyperscoreCriteria implements TopscoredCriteria {
	/*
	 * (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(ScoredScan ss1, ScoredScan ss2) {
		if (ss1 == ss2)
		{
			return 0;
		}
		return SCORE_COMPARATOR.compare(ss1.getBestMatch(), ss2.getBestMatch());
	}
}
