package org.systemsbiology.xtandem.scoring;

import org.junit.*;

/**
 * org.systemsbiology.xtandem.scoring.ExpectationTest
 * User: Steve
 * Date: 10/4/11
 */
public class ExpectationTest {
    public static final ExpectationTest[] EMPTY_ARRAY = {};

    public static int[] ORIGINAL_DIST = {
            16,
            450,
            1431,
            2137,
            2161,
            1786,
            1388,
            808,
            452,
            256,
            89,
            49,
            27,
            8,
            7,
            5,
            1,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            1,
    };

    public static int[] ORIGINAL_DIST_0 = {
            0,
            0,
            10,
            65,
            220,
            541,
            977,
            1306,
            1432,
            1342,
            1133,
            857,
            566,
            402,
            234,
            111,
            57,
            30,
            8,
            4,
            0,
            6,
    };

    public static int[] MODIFIED_DIST = {
            11071,
            11055,
            10605,
            9174,
            7037,
            4876,
            3090,
            1702,
            894,
            442,
            186,
            97,
            48,
            21,
            13,
            6,
            1,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            1,
    };

    public static int[] MODIFIED_DIST_0 = {
            9295,
            9295,
            9295,
            9285,
            9220,
            9000,
            8459,
            7482,
            6176,
            4744,
            3402,
            2269,
            1412,
            846,
            444,
            210,
            99,
            42,
            12,
            4,
            0,
            6,
    };


      public static double COMPUTED_FAO_0 = 6.6964;
    public static double COMPUTED_FA1_0 = -0.298692;
    public static final double SCORE_0  =  423;
    public static double EXPECTED_0 = 2.4;

     public static double COMPUTED_FAO = 5.27985;
    public static double COMPUTED_FA1 = -0.299148;
    public static final double SCORE =  757;
    public static final double EXPECT = 1.0e-006;

    @Test
    public void testExpectation() {

  
        int[] original = ORIGINAL_DIST; // (ORIGINAL_VALUES);
        HyperScoreStatistics hs = new HyperScoreStatistics();
        for (int i = 0; i < original.length; i++) {
            hs.incrementBin(i, original[i]);
        }
        int[] modified = hs.getModified();
        int[] reverse = MODIFIED_DIST;
        Assert.assertArrayEquals(modified, reverse);

        double expectation = buildExpectation(hs, COMPUTED_FAO, COMPUTED_FA1, SCORE);

        double pow = EXPECT;
         Assert.assertEquals(pow, expectation, 0.2 * pow);
           original = ORIGINAL_DIST_0;
        hs.clear();
        for (int i = 0; i < original.length; i++) {
            hs.incrementBin(i, original[i]);
        }
        modified = hs.getModified();
        int[] modified1 = MODIFIED_DIST_0;
        Assert.assertArrayEquals(modified, modified1);
        expectation = buildExpectation(hs, COMPUTED_FAO_0, COMPUTED_FA1_0, SCORE_0);
          pow = EXPECTED_0;
        Assert.assertEquals(pow, expectation, 0.2 * pow);
        //   Assert.assertEquals(Math.pow(EXPECTED,10), Math.pow(expectation,10), 0.1);

    }

    private double buildExpectation(HyperScoreStatistics hs, double expectedIntercept,
                                    double expectedSlope, double bestScore) {
        double slope = -0.18;
        double intercept = 3.5;
          slope = hs.getSlope();
        Assert.assertEquals(slope, expectedSlope, 0.01);

        intercept = hs.getYIntercept();
        Assert.assertEquals(intercept, expectedIntercept, 0.1);

        double ret = Math.pow(10.0, (double) (intercept + slope * (0.05 * bestScore)));

        return ret;
    }

    public static int[] reverse(int[] arr) {
        int[] ret = new int[arr.length];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = arr[arr.length - 1 - i];

        }
        System.out.println("public static int[] ORIGINAL_DIST = {");
        for (int i = 0; i < ret.length; i++) {
            int i1 = ret[i];
//            int next = i + 1;
//            if (next < ret.length)
//                System.out.println((i1 - ret[next]) + ",");
//            else
            System.out.println(i1 + ",");

        }
        System.out.println("};");
        return ret;
    }

    ///*
//		survival();
//		m_fA0 = (float)3.5;
//		m_fA1 = (float)-0.18;
///*
// * use default values if the statistics in the survival function is too meager
// */
//    if(m_lLength == 0 || m_vlSurvive[0] < 200)	{
//        return false;
//    }
//    float *pfX = new float[m_lLength];
//    float *pfT = new float[m_lLength];
//    long a = 1;
//    long lMaxLimit = (long)(0.5 + m_vlSurvive[0]/2.0);
//    const long lMinLimit = 10;
///*
// * find non zero points to use for the fit
// */
//    a = 0;
//    while(a < m_lLength && m_vlSurvive[a] > lMaxLimit)	{
//        a++;
//    }
//    long b = 0;
//    while(a < m_lLength-1 && m_vlSurvive[a] > lMinLimit)	{
//        pfX[b] = (float)a;
//        pfT[b] = (float)log10((double)m_vlSurvive[a]);
//        b++;
//        a++;
//    }
///*
// * calculate the fit
// */
//    double dSumX = 0.0;
//    double dSumT = 0.0;
//    double dSumXX = 0.0;
//    double dSumXT = 0.0;
//    long iMaxValue = 0;
//    double dMaxT = 0.0;
//    long iValues = b;
//    a = 0;
//    while(a < iValues)	{
//        if(pfT[a] > dMaxT)	{
//            dMaxT = pfT[a];
//            iMaxValue = a;
//        }
//        a++;
//    }
//    a = iMaxValue;
//    while(a < iValues)	{
//        dSumX += pfX[a];
//        dSumXX += pfX[a]*pfX[a];
//        dSumXT += pfX[a]*pfT[a];
//        dSumT += pfT[a];
//        a++;
//    }
//    iValues -= iMaxValue;
//    double dDelta = (double)iValues*dSumXX - dSumX*dSumX;
//    if(dDelta == 0.0)	{
//        delete pfX;
//        delete pfT;
//        return false;
//    }
//    m_fA0 = (float)((dSumXX*dSumT -dSumX*dSumXT)/dDelta);
//    m_fA1 = (float)(((double)iValues*dSumXT - dSumX*dSumT)/dDelta);
//
//    cout << "public static double COMPUTED_FAO = " << m_fA0 << ";\n";
//    cout << "public static double COMPUTED_FA1 = " << m_fA1 << ";\n";
//    delete pfX;
//    delete pfT;
//    m_vlSurvive.clear();
//    return true;
//
// */

}
