package org.systemsbiology.peptide;

import com.lordjoe.utilities.*;
import org.junit.*;
import org.systemsbiology.xtandem.*;
import org.systemsbiology.xtandem.peptide.*;

import static org.junit.Assert.assertTrue;

/**
 * org.systemsbiology.peptide.DecoyTests
 * User: Steve
 * Date: 4/12/13
 */
public class DecoyTests {
    public static final DecoyTests[] EMPTY_ARRAY = {};

    public static final IPolypeptide TEST_1 = new Polypeptide("ABCDEFGHIJK");
    public static final IPolypeptide TEST_1DECOY = new Polypeptide("KJIHGFEDCBA");

    public static IPolypeptide[] buildSequenceAndDecoy()
    {
        IPolypeptide[] ret = new IPolypeptide[2];
        ret[0] = Polypeptide.randomPeptide();
        ret[1] =  ret[0].asDecoy();
        return ret;
    }

    public static IModifiedPeptide[] buildModifiedSequenceAndDecoy()
    {
        IModifiedPeptide[] ret = new IModifiedPeptide[2];
        ret[0] = ModifiedPolypeptide.randomModifiedPeptide();
        ret[1] =  (IModifiedPeptide)ret[0].asDecoy();
        return ret;
    }

    public static final int NUMBER_RANDOM_TESTS = 100;
    @Test
    public void testRandom() throws Exception {
        Assert.assertEquals(20,FastaAminoAcid.UNIQUE_AMINO_ACIDS.length);
        for(int i = 0; i < NUMBER_RANDOM_TESTS; i++) {
           testRandomPeptide();
            testRandomModifiedPeptide();
        }
    }


    private void testRandomModifiedPeptide() {
        IPolypeptide[] rd = buildModifiedSequenceAndDecoy();
        IPolypeptide tp = rd[0];
        IPolypeptide decoy = rd[1];

        Assert.assertFalse(tp.isDecoy());
        assertTrue(decoy.isDecoy());

        IPolypeptide tpp =  ((IDecoyPeptide)decoy).asNonDecoy();
        Assert.assertFalse(tpp.isDecoy());
        assertTrue(tp.equivalent(tpp));
        assertTrue(decoy.equivalent(tpp.asDecoy()));
        assertTrue(decoy.equivalent(tp.asDecoy()));

        Assert.assertEquals(tp.getMass(),decoy.getMass(),0.001);
        Assert.assertEquals(decoy.getMatchingMass(),tpp.getMatchingMass(),0.001);

    }

    private void testRandomPeptide() {
        IPolypeptide[] rd = buildSequenceAndDecoy();
        IPolypeptide tp = rd[0];
        IPolypeptide decoy = rd[1];

        Assert.assertFalse(tp.isDecoy());
        assertTrue(decoy.isDecoy());

        IPolypeptide tpp =  ((IDecoyPeptide)decoy).asNonDecoy();
        Assert.assertFalse(tpp.isDecoy());
        assertTrue(tp.equivalent(tpp));
        assertTrue(decoy.equivalent(tpp.asDecoy()));
        assertTrue(decoy.equivalent(tp.asDecoy()));

        Assert.assertEquals(tp.getMass(),decoy.getMass(),0.001);
        Assert.assertEquals(decoy.getMatchingMass(),tpp.getMatchingMass(),0.001);

    }


    @Test
    public void testDecoy() throws Exception {
         IPolypeptide generatedDecoy = TEST_1.asDecoy();

        final String sequence = generatedDecoy.getSequence();
        Assert.assertEquals(sequence,TEST_1DECOY.getSequence());
    }

    @Test
    public void testInvert() throws Exception {
        FastaAminoAcid[] aas = FastaAminoAcid.asAminoAcids(TEST_1.getSequence());
        FastaAminoAcid[] aasReversed = Util.invert(aas);
        FastaAminoAcid[] aasDecoy = FastaAminoAcid.asAminoAcids(TEST_1DECOY.getSequence());

        Assert.assertArrayEquals(aasReversed,aasDecoy);
    }


    @Test
    public void testMass() throws Exception {

        Assert.assertEquals(TEST_1.getMass(),TEST_1DECOY.getMass(),0.001);
        Assert.assertEquals(TEST_1.getMatchingMass(),TEST_1DECOY.getMatchingMass(),0.001);

       }




}
