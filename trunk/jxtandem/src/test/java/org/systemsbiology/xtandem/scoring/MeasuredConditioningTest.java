package org.systemsbiology.xtandem.scoring;

import org.junit.*;
import org.systemsbiology.xtandem.*;
import org.systemsbiology.xtandem.ionization.*;
import org.systemsbiology.xtandem.peptide.*;

import java.util.*;

/**
 * org.systemsbiology.xtandem.scoring.MeasuredConditioningTest
 *  test filtering the measured spectrum
 * @author Steve Lewis
 * @date Feb 16, 2011
 */
public class MeasuredConditioningTest
{
    public static MeasuredConditioningTest[] EMPTY_ARRAY = {};
    public static Class THIS_CLASS = MeasuredConditioningTest.class;

    public static final double[] peaks =
            {
                    248, 0.240994, //  decrease=0.00240994, old=0.243404,
                    305, 0.217966, //  decrease=0.00456932, old=0.222536,
                    319, 0.234397, //  decrease=0.00456932, old=0.238966,
                    443, 0.0482607, //  decrease=0.00442413, old=0.0526849,
                    452, 0.0876378, //  decrease=0.00442413, old=0.092062,
                    460, 0.0430432, //  decrease=0.00495599, old=0.0479991,
                    467, 0.0375284, //  decrease=0.00736593, old=0.0448944,
                    477, 0.201831, //  decrease=0.00736593, old=0.209197,
                    507, 0.04652, //  decrease=0.00719705, old=0.053717,
                    515, 0.236682, //  decrease=0.00672181, old=0.243404,
                    541, 0.0764456, //  decrease=0.00519923, old=0.0816448,
                    543, 0.0408465, //  decrease=0.00519923, old=0.0460457,
                    589, 0.0877364, //  decrease=0.0125743, old=0.100311,
                    594, 0.0450417, //  decrease=0.0119741, old=0.0570157,
                    597, 0.199649, //  decrease=0.0119741, old=0.211623,
                    599, 0.140993, //  decrease=0.0129863, old=0.153979,
                    607, 0.118835, //  decrease=0.0166625, old=0.135497,
                    612, 0.124812, //  decrease=0.0166625, old=0.141474,
                    619, 0.201571, //  decrease=0.018944, old=0.220515,
                    621, 0.102145, //  decrease=0.0197513, old=0.121897,
                    641, 0.0483097, //  decrease=0.0187581, old=0.0670678,
                    649, 0.0850283, //  decrease=0.0172085, old=0.102237,
                    651, 0.0532664, //  decrease=0.015684, old=0.0689503,
                    655, 0.227206, //  decrease=0.0161982, old=0.243404,
                    656, 0.0427448, //  decrease=0.0161982, old=0.058943,
                    663, 0.216968, //  decrease=0.0134559, old=0.230424,
                    671, 0.0695882, //  decrease=0.011953, old=0.0815412,
                    697, 0.100907, //  decrease=0.0112219, old=0.112129,
                    705, 0.0424149, //  decrease=0.00952695, old=0.0519418,
                    720, 0.0635997, //  decrease=0.00511403, old=0.0687137,
                    742, 0.107748, //  decrease=0.00737608, old=0.115124,
                    762, 0.0805094, //  decrease=0.00655726, old=0.0870666,
                    771, 0.0600941, //  decrease=0.0065101, old=0.0666042,
                    789, 0.233181, //  decrease=0.0102231, old=0.243404,
                    806, 0.0716341, //  decrease=0.0097357, old=0.0813698,
                    818, 0.053225, //  decrease=0.0107265, old=0.0639516,
                    830, 0.231861, //  decrease=0.0100671, old=0.241928,
                    832, 0.123014, //  decrease=0.0100671, old=0.133082,
                    855, 0.0554192, //  decrease=0.0104805, old=0.0658997,
                    868, 0.17613, //  decrease=0.0110113, old=0.187141,
                    884, 0.0429478, //  decrease=0.0093244, old=0.0522722,
                    899, 0.0809263, //  decrease=0.0093244, old=0.0902507,
                    902, 0.130901, //  decrease=0.0117343, old=0.142636,
                    909, 0.037983, //  decrease=0.0110819, old=0.0490649,
                    918, 0.0748338, //  decrease=0.0110819, old=0.0859157,
                    927, 0.124209, //  decrease=0.00922899, old=0.133438,
                    932, 0.0407634, //  decrease=0.00922899, old=0.0499924,
                    934, 0.0759252, //  decrease=0.00922899, old=0.0851542,
                    952, 0.235586, //  decrease=0.00781787, old=0.243404,
                    1030, 0.137916, //  decrease=0.00137916, old=0.139295,
            };

    public static final int[] MISSING_MASSES = { /* 588, isotope */ 643, 886, 954 };
    public static final int[] ADDED_MASSES = { 305,467, 663, 818 };


    @Test
    public void testConditioning()
    {
        XTandemMain main = new XTandemMain(
                XTandemUtilities.getResourceStream("smallSample/tandem.params"),
                "smallSample/tandem.params");
        main.loadScoringTest();
        main.loadSpectra();
         Scorer sa = main.getScoreRunner();
        IScoredScan[] conditionedScans = sa.getScans();
        final IScoredScan conditionedScan = conditionedScans[0];

        SpectrumCondition sc = sa.getSpectrumCondition();
        final IScoringAlgorithm alg = sa.getAlgorithm();
         IMeasuredSpectrum scan = alg.conditionSpectrum(conditionedScan,sc);
        Set<Integer> goodMasses =  buildMassSet();

        final ISpectrumPeak[] sps = scan.getPeaks();
        for (int i = 0; i < sps.length; i++) {
            ISpectrumPeak sp = sps[i];
            validateMass(sp,peaks[2 * i],goodMasses);
       //     validatePeak(sp,peaks[2 * i],peaks[2 * i + 1]);
        }
        Assert.assertEquals(goodMasses.size(),0);
        XTandemUtilities.breakHere();

    }

    private static void validateMass(ISpectrumPeak pSp, double pPeak, Set<Integer> goodMasses)
    {
        final double mass = pSp.getMassChargeRatio();
        final int imass = asInt(mass);
        if(!goodMasses.contains(imass ))   {
            // try one off
            if(goodMasses.contains(imass - 1 ))  {
                goodMasses.remove(imass - 1);
                return;
            }
            if(goodMasses.contains(imass + 1 ))  {
                goodMasses.remove(imass + 1);
                return;
            }
        }

        if(!goodMasses.contains(imass))
             System.out.println("Missing mass " + imass);

        goodMasses.remove(imass);
    }

    /**
     * these are the masses XTandem has
     * @return
     */
    public static Set<Integer>  buildMassSet()
    {
        Set<Integer> holder = new HashSet<Integer>();
        for (int i = 0; i < peaks.length; i += 2) {
              holder.add( asInt(peaks[i]));
          }


        return holder;
    }
    public static final double ALLOWED_INTENSITY_ERROR = 0.001;

    /**
     * This version just checks the mass and prints errors
     * @param pSp
     * @param pPeak
     * @param pPeak1
     */
    private static  void validatePeak(ISpectrumPeak pSp, double pPeak, double pPeak1)
     {
         Set<Integer> goodMasses =  buildMassSet();
         final double chargeRatio = pSp.getMassChargeRatio();
         validateAsInts(chargeRatio,pPeak);
         double peak = pSp.getPeak();
         Assert.assertEquals(pPeak1,peak,ALLOWED_INTENSITY_ERROR);

     }
    private static  void validatePeak2(ISpectrumPeak pSp, double pPeak, double pPeak1)
     {
         Set<Integer> goodMasses =  buildMassSet();
         final double chargeRatio = pSp.getMassChargeRatio();
         validateAsInts(chargeRatio,pPeak);
         double peak = pSp.getPeak();
         Assert.assertEquals(pPeak1,peak,ALLOWED_INTENSITY_ERROR);

     }

    private static void validateAsInts(double pChargeRatio, double pPeak)
    {
        Assert.assertEquals(asInt(pChargeRatio),asInt(pPeak));
    }

    private static int asInt(double val)
    {
        return (int)(val + 0.5);
    }


}
