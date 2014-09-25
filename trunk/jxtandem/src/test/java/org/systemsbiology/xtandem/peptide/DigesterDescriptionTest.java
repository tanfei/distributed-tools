package org.systemsbiology.xtandem.peptide;

import org.junit.*;

/**
 * org.systemsbiology.xtandem.peptide.DigesterDescriptionTest
 * tests parse and equivalent for  DigesterDescription
 * User: Steve
 * Date: 9/20/11
 */
public class DigesterDescriptionTest {
    public static final DigesterDescriptionTest[] EMPTY_ARRAY = {};

    public static final String SAMPLE_STRING1 =
            "<digester  version=\"0\" hasDecoys=\"false\" rules=\"[RK]|{P}\" semityptic=\"true\" numberMissedCleavages=\"2\" >\n" +
                    "    <modifications  >\n" +
                    "        <modification  aminoacid=\"C\" massChange=\"-57.0215\" restriction=\"Global\" />\n" +
                    "    </modifications>\n" +
                    "</digester>";

    // should be ok to add a mod
    public static final String SAMPLE_STRING2 =
            "<digester  rules=\"[RK]|{P}\" semityptic=\"true\" numberMissedCleavages=\"2\" >\n" +
                    "    <modifications  >\n" +
                    "        <modification  aminoacid=\"C\" massChange=\"-57.0215\" restriction=\"Global\" />\n" +
                    "        <modification  aminoacid=\"K\" massChange=\"-17.034\" restriction=\"Global\" />\n" +
                     "    </modifications>\n" +
                    "</digester>";
    // should be ok to not be semitryptic
    public static final String SAMPLE_STRING3 =
            "<digester  rules=\"[RK]|{P}\" semityptic=\"false\" numberMissedCleavages=\"1\" >\n" +
                    "    <modifications  >\n" +
                    "        <modification  aminoacid=\"C\" massChange=\"-57.0215\" restriction=\"Global\" />\n" +
                    "        <modification  aminoacid=\"K\" massChange=\"-17.034\" restriction=\"Global\" />\n" +
                     "    </modifications>\n" +
                    "</digester>";

    @Test
    public void testParse() throws Exception {
        DigesterDescription dd = new DigesterDescription(SAMPLE_STRING1);
        PeptideBondDigester digester = dd.getDigester();
        Assert.assertTrue(digester.isSemiTryptic());

        Assert.assertEquals("[RK]|{P}", digester.toString());

        PeptideModification[] modifications = dd.getModifications();
        Assert.assertEquals(1,modifications.length);

        PeptideModification mod1 = PeptideModification.fromString("-57.0215@C", PeptideModificationRestriction.Global, false);
        Assert.assertEquals(mod1,modifications[0]);

        Assert.assertTrue(dd.equivalent(dd));

        String actual = dd.asXMLString().trim();
        Assert.assertEquals(SAMPLE_STRING1.trim(), actual);
    }

    @Test
    public void testEquivalent() throws Exception {
        DigesterDescription dd = new DigesterDescription(SAMPLE_STRING1);
        DigesterDescription dd2 = new DigesterDescription(SAMPLE_STRING2);
        DigesterDescription dd3 = new DigesterDescription(SAMPLE_STRING3);
        // ok to add a mod
        Assert.assertTrue(dd.equivalent(dd2));
        // not ok to remove a mod
        Assert.assertFalse(dd2.equivalent(dd));

        // ok to not be semitryptic
        Assert.assertTrue(dd3.equivalent(dd2));
       // not ok to remove a semitryptic
        Assert.assertFalse(dd2.equivalent(dd3));

        PeptideModification[] modifications = dd2.getModifications();
         Assert.assertEquals(2,modifications.length);

         PeptideModification mod1 = PeptideModification.fromString("-17.034@K", PeptideModificationRestriction.Global, false);
         Assert.assertEquals(mod1,modifications[1]);


    }
}
