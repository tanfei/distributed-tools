package org.systemsbiology.xtandem.testing;

import org.systemsbiology.xtandem.scoring.*;

import java.util.*;

/**
 * org.systemsbiology.xtandem.testing.BestDataSampler
 * User: steven
 * Date: 5/14/12
 */
public class BestDataSampler {
    public static final BestDataSampler[] EMPTY_ARRAY = {};
    public static final double MAXIMUM_EXPECTED = 0.001;
    public static final int MAX_SPECTRA = 6;

    public static void main(String[] args) {
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            JXTandem_XTandemCrossValidator xt = new JXTandem_XTandemCrossValidator(arg);
            Map<String, ScoredScan> mp = xt.readXTandemFile();
            List<ScoredScan> holder = new ArrayList<ScoredScan>();
            for (ScoredScan scan : mp.values()) {
                double expectedValue = scan.getExpectedValue();
                if (expectedValue < MAXIMUM_EXPECTED) {
                    holder.add(scan);
                    if (holder.size() >= MAX_SPECTRA)
                        break;
                }
            }

            ScoredScan[] ret = new ScoredScan[holder.size()];
            holder.toArray(ret);
        }
    }

}
