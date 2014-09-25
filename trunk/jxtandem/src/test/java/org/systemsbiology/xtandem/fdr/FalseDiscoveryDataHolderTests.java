package org.systemsbiology.xtandem.fdr;

import org.junit.*;
/**
 * org.systemsbiology.xtandem.fdr.FalseDiscoveryDataHolderTests
 *
 * @author Steve Lewis
 * @date 09/05/13
 */
public class FalseDiscoveryDataHolderTests {
    public static FalseDiscoveryDataHolderTests[] EMPTY_ARRAY = {};
    public static Class THIS_CLASS = FalseDiscoveryDataHolderTests.class;

    /**
      * make sure the static asbin function has reasonable limits and enough resolution - > 1 part in 20
      */
     @Test
     public void testFromBin() {
         // check the limits
          double factor = 1.05;
         int start = -1;
         // make sure we have enough distinct values
         for (double v = FDRUtilities.MINIMUM_SCORE; v < FDRUtilities.MAXIMUM_SCORE; v *= factor) {
             int test = FDRUtilities.asBin( v); //    get a new value
             double score2 = FDRUtilities.fromBin(test);
             Assert.assertEquals(v,score2,0.1 * v);
             Assert.assertNotSame(test,start);
             start = test;

         }

      }

}
