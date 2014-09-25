package org.systemsbiology.peptide;

import org.junit.*;
 import org.systemsbiology.xtandem.peptide.*;

/**
 * org.systemsbiology.peptide.PeptideParserTest
 * User: steven
 * Date: 9/9/11
 */
public class PeptideParserTest {
    public static final PeptideParserTest[] EMPTY_ARRAY = {};

    public static final String SAMPLE_MODIFIED_STRING = "AACTM[15.995]SVCSSACSDSWR";
    public static final String[] SAMPLE_MODIFIED_STRINGS = {
            "AACTM[15.995]SVCSSACSDSWR" ,
            "AA[8.9997]CTM[15.995]SVCSSACSDSWR" ,
            "AACTMSVCSSAC[15.995]SDSWR" ,
            "A[15.995]ACTMSVCSSACSDSWR" ,
            "AACTMSVCSSACSDSWR[15.995]" ,
      };


    /**
     * make sure we can parse strings with multiple modifications
     */
    @Test
    public void testParse()
    {
        IPolypeptide pp  = Polypeptide.fromString("AACTMSVCSSACSDSWR");
        Assert.assertFalse(pp instanceof IModifiedPeptide);

        pp  = Polypeptide.fromString(SAMPLE_MODIFIED_STRING);
        Assert.assertTrue(pp instanceof  IModifiedPeptide);

        IModifiedPeptide mp = (IModifiedPeptide)pp;
        PeptideModification[] pms = mp.getModifications();
        Assert.assertNull(pms[0]);
        Assert.assertNotNull(pms[4]);


        pp  = Polypeptide.fromString("A[15.995]ACTMSVCSSACSDSWR");
        Assert.assertTrue(pp instanceof  IModifiedPeptide);

        mp = (IModifiedPeptide)pp;
         pms = mp.getModifications();
        Assert.assertNotNull(pms[0]);
        Assert.assertNull(pms[4]);

        pp  = Polypeptide.fromString("AACTMSVCSSACSDSWR[15.995]");
        Assert.assertTrue(pp instanceof  IModifiedPeptide);

        mp = (IModifiedPeptide)pp;
         pms = mp.getModifications();
        Assert.assertNotNull(pms[pms.length - 1]);
        Assert.assertNull(pms[4]);


        for (int i = 0; i < SAMPLE_MODIFIED_STRINGS.length; i++) {
            pp  = Polypeptide.fromString(SAMPLE_MODIFIED_STRINGS[i]);
            Assert.assertTrue(pp instanceof  IModifiedPeptide);

        }

    }

}
