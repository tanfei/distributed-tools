package org.systemsbiology.xtandem.peptide;

import org.junit.*;

import java.util.*;

/**
 * org.systemsbiology.xtandem.peptide.PeptideModificationTests
 * User: steven
 * Date: 6/30/11
 */
public class PeptideModificationTests {
    public static final PeptideModificationTests[] EMPTY_ARRAY = {};

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

    public static final String TEST_PEPTIDE = "EQEVREHQMKK";

    public static final String[] MODIFIED_RESULTS =
            {

                    "EQEVREHQMK[8.014]K",
                    "EQEVREHQMKK[8.014]",
                    "EQEVREHQM[15.995]KK",
                    "EQEVR[10.008]EHQMKK",
                    "E[-18.01]QEVREHQMKK",

            };

    public static final String TEST_PEPTIDE2 = "MEQEVREHQMKK";


    public static final String[] MODIFIED_RESULTS2 =
            {
                    "MEQEVREHQMK[8.014]K",
                    "MEQEVREHQMKK[8.014]",
                    "M[15.995]EQEVREHQMKK",
                    "MEQEVREHQM[15.995]KK",
                    "MEQEVR[10.008]EHQMKK",
                    "MEQEVREHQMK[8.014]K[8.014]",
                    "M[15.995]EQEVREHQMK[8.014]K",
                    "M[15.995]EQEVREHQMKK[8.014]",
                    "MEQEVREHQM[15.995]K[8.014]K",
                    "MEQEVREHQM[15.995]KK[8.014]",
                    "MEQEVR[10.008]EHQMK[8.014]K",
                    "MEQEVR[10.008]EHQMKK[8.014]",
                    "M[15.995]EQEVREHQM[15.995]KK",
                    "M[15.995]EQEVR[10.008]EHQMKK",
                    "MEQEVR[10.008]EHQM[15.995]KK",
                    "M[15.995]EQEVREHQMK[8.014]K[8.014]",
                    "MEQEVREHQM[15.995]K[8.014]K[8.014]",
                    "M[15.995]EQEVREHQM[15.995]K[8.014]K",
                    "M[15.995]EQEVREHQM[15.995]KK[8.014]",
                    "M[15.995]EQEVR[10.008]EHQMK[8.014]K",
                    "MEQEVR[10.008]EHQM[15.995]K[8.014]K",
                    "M[15.995]EQEVR[10.008]EHQMKK[8.014]",
                    "MEQEVR[10.008]EHQM[15.995]KK[8.014]",
                    "MEQEVR[10.008]EHQMK[8.014]K[8.014]",
                    "M[15.995]EQEVR[10.008]EHQM[15.995]KK",
                    "M[15.995]EQEVR[10.008]EHQMK[8.014]K[8.014]",
                    "MEQEVR[10.008]EHQM[15.995]K[8.014]K[8.014]",
                    "M[15.995]EQEVR[10.008]EHQM[15.995]K[8.014]K",
                    "M[15.995]EQEVR[10.008]EHQM[15.995]KK[8.014]",

            };


    public static final String TEST_STRING =
            "15.994915@M,8.014199@K,10.008269@R";


    /**
     * tests parsing and sorting
     */
    @Test
    public void testPeptideModificationParse() {
        PeptideModification[] pms = PeptideModification.parsePeptideModifications(TEST_STRING,PeptideModificationRestriction.Global,false);
        Assert.assertArrayEquals(pms, ANSWER);
        for (int i = 0; i < pms.length; i++) {
            PeptideModification pm = pms[i];
            Assert.assertEquals(ANSWER_STRINGS[i], pm.toString());
        }
    }

    /**
     * testsinserting modifications into a peptide
     */
    @Test
    public void testAddModifications() {
        IModifiedPeptide[] peps = ModifiedPolypeptide.buildModifications(new Polypeptide(TEST_PEPTIDE, 1), ANSWER);
        // save work print the results
        for (int i = 0; i < peps.length; i++) {
            IModifiedPeptide polypeptide = peps[i];
            System.out.println("\"" + polypeptide.getModifiedSequence() + "\",");
        }

        Assert.assertEquals(MODIFIED_RESULTS.length, peps.length);
        for (int i = 0; i < peps.length; i++) {
            IModifiedPeptide pep = peps[i];
            Assert.assertEquals(MODIFIED_RESULTS[i], pep.getModifiedSequence());
        }
    }


    /**
     * tests inserting >1 modifications into a peptide
     */
    @Test
    public void testAddMoreThanOneModifications() {
        // allow multiple modifications
        final Polypeptide eptide = new Polypeptide(TEST_PEPTIDE2, 2);
        IModifiedPeptide[] peps = ModifiedPolypeptide.buildAllModifications(
                eptide, ANSWER);
        // save work print the results
        for (int i = 0; i < peps.length; i++) {
            IModifiedPeptide polypeptide = peps[i];
            System.out.println("\"" + polypeptide.getModifiedSequence() + "\",");
        }

        // MODIFIED_RESULTS2 is not complete but must be present
        // so make a set of modified sequwences
        Set<String> holder = new HashSet<String>();
        for (int i = 0; i < peps.length; i++) {
            IModifiedPeptide pep = peps[i];
            final String modifiedSequence = pep.getModifiedSequence();
            holder.add(modifiedSequence);
        }
        // and make sure all the n=hand generated results are there
        for (int i = 0; i < MODIFIED_RESULTS2.length; i++) {
            final String modifiedSequence = MODIFIED_RESULTS2[i];
            Assert.assertTrue(holder.contains(modifiedSequence));
        }

    }


}
