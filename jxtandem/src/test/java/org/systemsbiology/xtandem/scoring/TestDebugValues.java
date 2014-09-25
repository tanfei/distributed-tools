package org.systemsbiology.xtandem.scoring;

import org.junit.*;
import org.systemsbiology.xtandem.*;
import org.systemsbiology.xtandem.testing.*;

/**
 * org.systemsbiology.xtandem.scoring.TestDebugValues
 * User: steven
 * Date: 3/3/11
 */
public class TestDebugValues {
    public static final TestDebugValues[] EMPTY_ARRAY = {};

    /**
     * this test makes sure that the mass a spectrum exposes for scoring is exactly that used by XTandem
     *
     * @throws Exception
     */
    // @Test  todo fix
    public void testSpectrumMass() throws Exception {
        XTandemMain main = new XTandemMain(
                XTandemUtilities.getResourceStream("largeSample/tandem.params"),
                "largeSample/tandem.params");
        XTandemDebugging.setDebugging(true, main);
        main.loadScoringTest();
        main.loadSpectra();

        main.process();

        DebugValues localValues = XTandemDebugging.getLocalValues();

        DebugValues dv = new DebugValues(main);
        //      dv.loadDebugValues("E:\\MapReduce\\JXTandem\\data\\largeSample\\LT20100217SCX4_FILTERED_debug.2011_03_03_12_32_17.t.txt");
        dv.loadDebugValues("res://largeSample//log1.txt");
        final DebugDotProduct[] dps = dv.getDebugDotProducts();
        Assert.assertEquals(3, dps.length);

        String id =  "7858";
        testLocalAndXTandemEquivalent(localValues, dv, id);


    }

    public void testLocalAndXTandemEquivalent(DebugValues pLocalValues, DebugValues pDv, String pId) {
        String tag = "do not condition";
        testTagEquivalent(pLocalValues, pDv, pId, tag);

        tag = "sqrt modification";
        testTagEquivalent(pLocalValues, pDv, pId, tag);

        tag = "before Perform mix-range modification";
        testTagEquivalent(pLocalValues, pDv, pId, tag);


        tag = "add_mi_conditioned";
        testTagEquivalent(pLocalValues, pDv, pId, tag);

        testScoringEquivalent(pLocalValues, pDv, pId);
    }

    public void testScoringEquivalent(DebugValues pLocalValues, DebugValues pDv, String pId) {
        DebugDotProduct[] dpLocal = pLocalValues.getDebugDotProductsWithId(pId);
        DebugDotProduct[] dpRemote = pDv.getDebugDotProductsWithId(pId);
        Assert.assertEquals(dpLocal.length, dpRemote.length);
        for (int i = 0; i < dpRemote.length; i++) {
            DebugDotProduct dp1Remote = dpRemote[i];
            DebugDotProduct dp1Local = findEquivalentDotProduct(dp1Remote,dpLocal);
            Assert.assertNotNull(dp1Local);
            testDotProductEquivalent(dp1Remote,dp1Local);
        }
     }

    protected void testDotProductEquivalent(final DebugDotProduct d1, final DebugDotProduct d2) {
        Assert.assertEquals(d1.getScanId(),d2.getScanId());
        Assert.assertEquals(d1.getCharge(),d2.getCharge());
        Assert.assertEquals(d1.getType(),d2.getType());
        DebugMatchPeak[] mp1 = d1.getMatchedPeakss();
        DebugMatchPeak[] mp2 = d2.getMatchedPeakss();
        if(mp1.length !=  mp2.length)
            Assert.assertEquals(mp1.length, mp2.length);  // break here look at issues
        for (int i = 0; i < mp2.length; i++) {
            DebugMatchPeak test2 = mp2[i];
            DebugMatchPeak test1 = mp1[i];

        }
    }

    protected DebugDotProduct findEquivalentDotProduct(final DebugDotProduct pDp1Remote, final DebugDotProduct[] pDpLocal) {
        for (int i = 0; i < pDpLocal.length; i++) {
            DebugDotProduct test = pDpLocal[i];
            if(isEquivalentLocalANdRemote(pDp1Remote,test))
                return test;
        }
        return null; // not found
    }

    protected boolean isEquivalentLocalANdRemote(final DebugDotProduct d1, final DebugDotProduct d2) {
        if(d1.getScanId() != d2.getScanId())
            return false;
        if(d1.getCharge() != d2.getCharge())
            return false;
        if(d1.getType() != d2.getType())
            return false;

        return true;
    }

    public static void testTagEquivalent(final DebugValues pLocalValues, final DebugValues pDv, String id, final String pTag) {
        IMeasuredSpectrum theirs = pDv.getMeasuredSpectrum(id, pTag);
        IMeasuredSpectrum mine = pLocalValues.getMeasuredSpectrum(id, pTag);

        Assert.assertNotNull(theirs);
        Assert.assertNotNull(mine);
        boolean equivalent = XTandemTestUtilities.equivalent(theirs, mine);
        if (!equivalent) {
            //  return;
            Assert.assertTrue(equivalent);
        }
    }

}
