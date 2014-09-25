package org.systemsbiology.xtandem.scoring;

import org.systemsbiology.xtandem.*;

import java.util.*;

/**
 * org.systemsbiology.xtandem.scoring.AndPeakFilter
 *     filter oring the output of a number of filters
 * @author Steve Lewis
 * @date Jan 17, 2011
 */
public class OrPeakFilter implements IPeakFilter
{
    public static OrPeakFilter[] EMPTY_ARRAY = {};
    public static Class THIS_CLASS = OrPeakFilter.class;

    private final IPeakFilter[] m_Filters;

    public OrPeakFilter(IPeakFilter fi, IPeakFilter... others)
    {
        List<IPeakFilter> holder = new ArrayList<IPeakFilter>();
        holder.add(fi);
        for (int i = 0; i < others.length; i++) {
            holder.add(others[i]);
        }
        IPeakFilter[] ret = new IPeakFilter[holder.size()];
        holder.toArray(ret);
        m_Filters = ret;
    }

    /**
     * decide whether to keep a peak or not - at least one filter
     *  must return true
      * @param peak      !null peak
     * @param addedData any additional data
     * @return true if the peak is to be used
     */
    @Override
    public boolean isPeakRetained(ISpectrumPeak peak, Object... addedData)
    {
         for (int i = 0; i < m_Filters.length; i++) {
            if(m_Filters[i].isPeakRetained(peak,addedData))
                return true;
        }
        return false;
    }
}
