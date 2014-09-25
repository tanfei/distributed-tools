package org.systemsbiology.xtandem.fdr;

import org.junit.*;

import java.util.*;

/**
 * org.systemsbiology.xtandem.fdr.FDRUtilitiesTests
 *
 * @author Steve Lewis
 * @date 09/05/13
 */
public class FDRUtilitiesTests {
    public static FDRUtilitiesTests[] EMPTY_ARRAY = {};
    public static Class THIS_CLASS = FDRUtilitiesTests.class;

    /**
     * make sure the static asbin function has reasonable limits and enough resolution - > 1 part in 20
     */
    @Test
    public void testAsBin() {
        // check the limits
        Assert.assertEquals(0, FDRUtilities.asBin(0));
        Assert.assertEquals(0, FDRUtilities.asBin(FDRUtilities.MINIMUM_SCORE));
        Assert.assertEquals(FDRUtilities.NUMBER_BINS - 1, FDRUtilities.asBin(FDRUtilities.MAXIMUM_SCORE));
        Assert.assertEquals(FDRUtilities.NUMBER_BINS - 1, FDRUtilities.asBin(Double.MAX_VALUE));

        double factor = 1.05;
        int start = -1;
        // make sure we have enough distinct values
        for (double v = FDRUtilities.MINIMUM_SCORE; v < FDRUtilities.MAXIMUM_SCORE; v *= factor) {
            int test = FDRUtilities.asBin(v); //    get a new value
            Assert.assertNotSame(test, start);
            start = test;

        }
    }

    @Test
    public void testGetNumberTruePositivesAbove() {
        // generate TPs at a score and calculate their numbers above

        // create local variable
        IDiscoveryDataHolder dh = FDRUtilities.getDiscoveryDataHolder();
        Assert.assertEquals(0, dh.getNumberTruePositivesAbove(1));

        double score1 = 10;

        double score2 = 11;

        dh.addTrueDiscovery(score2);
        Assert.assertEquals(1, dh.getNumberTruePositivesAbove(score2));
        Assert.assertEquals(0, dh.getNumberTruePositivesAbove(score2 + 1));

    }

    @Test
    public void testGetNumberFalsePositivesAbove() {

        // create local variable
        IDiscoveryDataHolder dh = FDRUtilities.getDiscoveryDataHolder();
        Assert.assertEquals(0, dh.getNumberFalsePositivesAbove(1));

        double score1 = 10;

        dh.addFalseDiscovery(score1);

        Assert.assertEquals(1, dh.getNumberFalsePositivesAbove(score1 - 1));
        Assert.assertEquals(0, dh.getNumberFalsePositivesAbove(score1 + 1));


    }

    @Test
    public void testComputeFDR() {


        IDiscoveryDataHolder dh = FDRUtilities.getDiscoveryDataHolder();

        double score1 = 10;

        dh.addFalseDiscovery(score1);

        double score2 = 11;

        dh.addTrueDiscovery(score2);

        Assert.assertEquals(dh.computeFDR(score1), 0.5, 0.0001);
        Assert.assertEquals(dh.computeFDR(score2), 0.0, 0.001);

        // randomly generate 1 FD + 1 TP at a score3 with one loop

        double score3 = 3;
        double score4 = 4;

        for (int i = 0; i < 1; i++) {

            dh.addFalseDiscovery(score3);
            dh.addTrueDiscovery(score4);

        }

        Assert.assertEquals(dh.computeFDR(score3), 0.5, 0.001);

        double score5 = 5;
        double score6 = 6;

        for (int i = 0; i < 2; i++) {

            dh.addFalseDiscovery(score5);
            dh.addTrueDiscovery(score6);

        }

        Assert.assertEquals(dh.computeFDR(score5), 0.5, 0.001);


        Random r = new Random();

        for (int i = 0; i < 5; i++) {
            dh.addTrueDiscovery(r.nextInt(50));

            Assert.assertEquals(0, dh.getNumberTruePositivesAbove(51));

        }

    }

        @Test
        public void testGetFirstScore() {


            IDiscoveryDataHolder dh = FDRUtilities.getDiscoveryDataHolder();

            double score1 = 10;

            dh.addFalseDiscovery(score1);

            double score2 = 11;

            dh.addTrueDiscovery(score2);

            Assert.assertEquals(score1,dh.getFirstScore(),0.0001);


        }


    @Test
    public void testGetLastScore() {


        IDiscoveryDataHolder dh = FDRUtilities.getDiscoveryDataHolder();

        double score1 = 10;

        dh.addFalseDiscovery(score1);

        double score2 = 11;

        dh.addTrueDiscovery(score2);

        Assert.assertEquals(score2,dh.getLastScore(),score2 * 0.01);


    }

    @Test
    public void testGetRandomFirstAndLastScore() {


        IDiscoveryDataHolder dh = FDRUtilities.getDiscoveryDataHolder();

        Random r = new Random();
        double score1 = r.nextInt(100);

        dh.addFalseDiscovery(score1);

        double score2 = r.nextInt(100);

        dh.addTrueDiscovery(score2);

        Assert.assertEquals(Math.min(score1,score2),dh.getFirstScore(),0.5);
        Assert.assertEquals(Math.max(score1,score2),dh.getLastScore(),0.5);


    }


}
