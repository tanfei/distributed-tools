package org.systemsbiology.xtandem.scoring;

import java.util.*;

/**
 * org.systemsbiology.xtandem.scoring.SpectrumHistogram
 *
 * @author Steve Lewis
 * @date Dec 21, 2010
 */
/*
 *	The mhistogram class is used for storing information about scoring in the mspectrum class.
 * Two types of histogram classes are used: the mhistogram which stores information about
 * convolution and hyper scores and the count_mhistogram class which is used to store the number
 * of specific ion types (e.g. y or b ions). The mhistgram class converts the scores into base-10 logs
 * to make the histogram more compact and to linearize the extreme value distribution, which
 * governs the scoring distribution. In order to calculate expectation values from these distributions,
 * they are first converted to survival functions (by the survival methdod), the outlying non-random
 * scores are removed from the distribution and the high scoring tail of the distribution is fitted
 * to a log-log linear distribution using least-squares in the model method.
 * mhistogram is included in the mspectrum.h file only
 */


public class HistogramFunction<T> {
    public static HistogramFunction[] EMPTY_ARRAY = {};
    public static Class THIS_CLASS = HistogramFunction.class;

    public static final int MAX_BINS = 100000;

    private double m_dProteinFactor = 1; // a weighting factor
    private float m_fA0 = 4.8F; // the intercept of the least-squares fit performed in model
    private float m_fA1 = -0.28F; // the slope of the least-squares fit performed in model
    private int[] m_vlSurvive;  // the survival function array, reduced to [96] from [256]
    private final int[] m_Bins;
    private long m_lSum;


    public HistogramFunction(T[] inputData, IValueFetcher<T> fetcher) {
        double max = Double.MIN_VALUE;
        double min = Double.MAX_VALUE;
        for (int i = 0; i < inputData.length; i++) {
            T t = inputData[i];
            double value = fetcher.fetch(t);
            max = Math.max(max, value);
            min = Math.min(min, value);
        }
        int imax = (int) max;
        int imin = (int) min;
        int del = imax - imin;
        if (del > MAX_BINS)
            throw new IllegalStateException("too many bins " + del + " > " + MAX_BINS);
        if (del == 0) {
            m_Bins = null;
            return;
        }
        m_Bins = new int[del];
        for (int i = 0; i < inputData.length; i++) {
            T t = inputData[i];
            double value = fetcher.fetch(t);
            int ivalue = (int) (del * (value - imin));
            m_Bins[ivalue]++;
        }


    }

    public HistogramFunction(double[] inputData ) {
        double max = Double.MIN_VALUE;
        double min = Double.MAX_VALUE;
        for (int i = 0; i < inputData.length; i++) {
            double value = inputData[i];
             max = Math.max(max, value);
            min = Math.min(min, value);
        }
        int imax = (int) max;
        int imin = (int) min;
        int del = imax - imin;
        if (del > MAX_BINS)
            throw new IllegalStateException("too many bins " + del + " > " + MAX_BINS);
        if (del == 0) {
            m_Bins = null;
            return;
        }
        m_Bins = new int[del];
        for (int i = 0; i < inputData.length; i++) {
            double value = inputData[i];
              int ivalue = (int) (del * (value - imin));
            m_Bins[ivalue]++;
        }


    }


    /*
    * expect uses the equation derived in model to convert scores into expectation values
    * survival and model must be called before expect will return reasonable values
    */

    public float expect(float _f) {
        return (float) Math.pow(10.0, (double) (m_fA0 + m_fA1 * _f));
    }
/*
 * list returns a specified value from the stochastic distribution of the histogram
 */

    public int list(int _l) {
        if (_l >= m_Bins.length)
            return 0;
        return m_Bins[_l];
    }

    /*
 * survive returns a specified value from the stochastic distribution of the survival function
 */

    public int survive(final int _l) {
        return m_vlSurvive[_l];
    }

    public long sum() {
        return m_lSum;
    }
/*
 * length returns the maximum length of the histogram
 */

    public int length() {
        return m_Bins.length;
    }
/*
 * a0 returns the first parameter in the linear fit to the survival function
 */

    public float a0() {
        return m_fA0;
    }
/*
 * a1 returns the first parameter in the linear fit to the survival function
 */

    public float a1() {
        return m_fA1;
    }
/*
 * expect_protein allows for a modified expectation value to be used for proteins
 */

    public float expect_protein(final float _f) {
        return (float) Math.pow(10.0, (double) (m_fA0 + m_fA1 * _f)) * (float) m_dProteinFactor;
    }
/*
 * set_protein_factor simply sets the value for m_dProteinFactor
 */

    public void set_protein_factor(final double _d) {
        if (_d <= 0.0)
            throw new IllegalArgumentException("bad protien factor");
        m_dProteinFactor = _d;
    }


/*
* survival converts the scoring histogram in m_pList into a survival function
*/

    public void survival() {

        int a = length() - 1;
        long lSum = 0;
        // make a copy of the bins
        int[] plValues = new int[m_Bins.length];
        System.arraycopy(m_Bins, 0, plValues, 0, m_Bins.length);

        Arrays.sort(plValues);
        /*
        * first calculate the raw survival function
        */


/*
* determine the value of the survival function that is 1/5 of the maximum
*/
        final long lPos = plValues[0] / 5;
        while (a < plValues.length && plValues[a] > lPos) {
            a++;
        }
        final int lMid = a;
        a = plValues.length - 1;
        while (a > -1 && plValues[a] == 0) {
            a--;
        }
        lSum = 0;
        long lValue = 0;
/*
* remove potentially valid scores from the stochastic distribution
*/
        while (a > 0) {
            if (plValues[a] == plValues[a - 1]
                    && plValues[a] != plValues[0]
                    && a > lMid) {
                lSum = plValues[a];
                lValue = plValues[a];
                a--;
                while (plValues[a] == lValue) {
                    plValues[a] -= lSum;
                    a--;
                }
            }
            else {
                plValues[a] -= lSum;
                a--;
            }
        }
        plValues[a] -= lSum;

        a = 0;
/*
* replace the scoring distribution with the survival function
*/
        m_vlSurvive = new int[plValues.length];
        System.arraycopy(plValues, 0, m_vlSurvive, 0, plValues.length);
        Arrays.sort(m_vlSurvive);
        m_lSum = m_vlSurvive[0];

    }


/*
* model performs a least-squares fit to the log score vs log survival function.
* survival must be called prior to using model
*/

    public void model() {
        survival();
        m_fA0 = (float) 3.5;
        m_fA1 = (float) -0.18;
/*
* use default values if the statistics in the survival function is too meager
*/
        if (length() == 0 || m_vlSurvive[0] < 200) {
            return;
        }
        float[] pfX = new float[length()];
        float[] pfT = new float[length()];
        int a = 1;
        int lMaxLimit = (int) (0.5 + m_vlSurvive[0] / 2.0);
        final int lMinLimit = 10;
/*
* find non zero points to use for the fit
*/
        a = 0;
        while (a < m_vlSurvive.length && m_vlSurvive[a] > lMaxLimit) {
            a++;
        }
        int b = 0;
        while (a < m_vlSurvive.length - 1 && m_vlSurvive[a] > lMinLimit) {
            pfX[b] = (float) a;
            pfT[b] = (float) Math.log10((double) m_vlSurvive[a]);
            b++;
            a++;
        }
/*
* calculate the fit
*/
        double dSumX = 0.0;
        double dSumT = 0.0;
        double dSumXX = 0.0;
        double dSumXT = 0.0;
        int iMaxValue = 0;
        double dMaxT = 0.0;
        long iValues = b;
        a = 0;
        while (a < iValues) {
            if (pfT[a] > dMaxT) {
                dMaxT = pfT[a];
                iMaxValue = a;
            }
            a++;
        }
        a = iMaxValue;
        while (a < iValues) {
            dSumX += pfX[a];
            dSumXX += pfX[a] * pfX[a];
            dSumXT += pfX[a] * pfT[a];
            dSumT += pfT[a];
            a++;
        }
        iValues -= iMaxValue;
        double dDelta = (double) iValues * dSumXX - dSumX * dSumX;
        if (dDelta == 0.0) {
            throw new UnsupportedOperationException("Fix This"); // ToDo
        }
        m_fA0 = (float) ((dSumXX * dSumT - dSumX * dSumXT) / dDelta);
        m_fA1 = (float) (((double) iValues * dSumXT - dSumX * dSumT) / dDelta);
    }


}

/*
 * count_mhistogram stores information about the number of specific ion-types
 * discovered while modeling mspectrum object against a sequence collection.
 * model and survival are not used
 * starting in version 2004.04.01, this class is no longer a public mhistogram
 * this change was made to reduce memory usage, by eliminating the 
 * m_pSurvive array & reducing the size of the m_pList array
 */

