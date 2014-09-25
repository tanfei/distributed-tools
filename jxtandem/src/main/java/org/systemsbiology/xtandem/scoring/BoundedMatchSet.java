package org.systemsbiology.xtandem.scoring;

import   com.lordjoe.utilities.*;
 import org.systemsbiology.xtandem.hadoop.*;

import java.util.*;

/**
 * org.systemsbiology.xtandem.scoring.BoundedMatchSet
 * User: steven
 * Date: 12/5/11
 */
public class BoundedMatchSet extends BoundedTreeSet<ISpectralMatch> {
    public static final BoundedMatchSet[] EMPTY_ARRAY = {};
     public static final Comparator<ISpectralMatch> COOMPARER = new SpectralMatchComparator();


     public static class SpectralMatchComparator implements Comparator<ISpectralMatch> {
         @Override
         public int compare(final ISpectralMatch o1, final ISpectralMatch o2) {
             if (o1 == o2)
                 return 0;
             double s1 = o1.getHyperScore();
             double s2 = o2.getHyperScore();
             if(s1 != s2)  {
                 return s1 < s2 ? 1 : -1;
             }
             return Util.objectCompareTo(o1, o2);

         }

      }

    public BoundedMatchSet( ) {
         super(COOMPARER, XTandemHadoopUtilities.getNumberCarriedMatches());
     }
    public BoundedMatchSet(Collection<? extends ISpectralMatch> col) {
         this();
         addAll(col);
     }

    public ISpectralMatch nextBest()
    {
             return nthBest(1);

    }


}
