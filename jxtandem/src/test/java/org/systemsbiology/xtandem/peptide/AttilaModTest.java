package org.systemsbiology.xtandem.peptide;

import org.junit.*;

/**
 * Created by IntelliJ IDEA.
 * User: attilacsordas
 * Date: Jul 12, 2011
 * Time: 7:56:05 PM
 * To change this template use File | Settings | File Templates.
 */
public class AttilaModTest {

    public static final AttilaModTest[] EMPTY_ARRAY = {};

    public static final String[] ANSWER_STRINGS =
            {
                    "8.014@K",
                    "15.995@M",
                    "10.008@R",
            };

    public static final PeptideModification[] ANSWER =
            {
                    PeptideModification.fromString("8.014199@K",PeptideModificationRestriction.Global,false),
                       PeptideModification.fromString("15.994915@M",PeptideModificationRestriction.Global,false),
                       PeptideModification.fromString("10.008269@R",PeptideModificationRestriction.Global,false),
              };

    public static final String TEST_PEPTIDE2 = "MEQEVREHQMKK";


    public static final String[] MODIFIED_RESULTS2 =
            {
                    "MEQEVREHQMK[8.014]K",
                    "MEQEVREHQMKK[8.014]",
                    "M[15.995]EQEVREHQMKK",
                    "MEQEVREHQM[15.995]KK",
                    "MEQEVR[10.008]EHQMKK",
            };


    public static final String TEST_STRING =
            "15.994915@M,8.014199@K,10.008269@R";


    /**
     * tests inserting >1 modifications into a peptide
     */
    @Test
    public void testAddMoreThanOneModifications() {
        IModifiedPeptide[] peps = ModifiedPolypeptide.buildModifications(new Polypeptide(TEST_PEPTIDE2, 2), ANSWER);
        // save work print the results
        for (int i = 0; i < peps.length; i++) {
            IModifiedPeptide polypeptide = peps[i];
            System.out.println("\"" + polypeptide.getModifiedSequence() + "\",");
        }

        Assert.assertEquals(MODIFIED_RESULTS2.length, peps.length);
        for (int i = 0; i < peps.length; i++) {
            IModifiedPeptide pep = peps[i];
            Assert.assertEquals(MODIFIED_RESULTS2[i], pep.getModifiedSequence());
        }
    }


}
