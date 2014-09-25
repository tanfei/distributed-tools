package org.systemsbiology.xtandem.hadoop;

import org.junit.*;
import org.systemsbiology.sax.*;
import org.systemsbiology.xtandem.*;
import org.systemsbiology.xtandem.sax.*;
import org.systemsbiology.xtandem.scoring.*;

/**
 * org.systemsbiology.xtandem.scoring.TestHadoopJob2Parse
 * testing the 
 * User: steven
 * Date: 3/3/11
 */
public class TestHadoopJob2Parse {
    public static final TestHadoopJob2Parse[] EMPTY_ARRAY = {};


    public static final String SAMPLE_DATA =
                      "<score id=\"7858\"   expectedValue=\"86.74789659223316\"  >\n" +
                              "  <scan num=\"7858\"\n"+
                                      "    msLevel=\"2\"\n"+
                                      "    peaksCount=\"345\"\n"+
                                      "    polarity=\"+\"\n"+
                                      "    scanType=\"Full\"\n"+
                                      "    filterLine=\"ITMS + c NSI d Full ms2 1096.63@cid35.00 [290.00-2000.00]\"\n"+
                                      "    retentionTime=\"PT2897.2S\"\n"+
                                      "    lowMz=\"324.828\"\n"+
                                      "    highMz=\"1998.61\"\n"+
                                      "    basePeakMz=\"1231.62\"\n"+
                                      "    basePeakIntensity=\"143.081\"\n"+
                                      "    totIonCurrent=\"3862.91\"\n"+
                                      "    collisionEnergy=\"35\" >\n"+
                                      "    <precursorMz precursorIntensity=\"69490.9\" activationMethod=\"CID\" >1096.63</precursorMz>\n"+
                                      "    <peaks precision=\"32\"\n"+
                                      "     byteOrder=\"network\"\n"+
                                      "     contentType=\"m/z-int\"\n"+
                                      "     compressionType=\"none\"\n"+
                                      "     compressedLen=\"0\" >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</peaks>\n"+
                                      "  </scan>\n" +
                            "<match  peak=\"VNG0675C VNG0675C:25(19)\" score=\"173.526783956915\" hyperscore=\"173.526783956915\">\n" +
                            "<IonScore  A_count=\"0\" A_score=\"0.0\" B_count=\"8\" B_score=\"0.31470590317621827\" C_count=\"0\" C_score=\"0.0\" Y_count=\"6\" Y_score=\"0.45595267112366855\" X_count=\"0\" X_score=\"0.0\" Z_count=\"0\" Z_score=\"0.0\" />\n" +
                            "</match>\n" +
                            "</score>"
           ;

    /**
     * this test makes sure that the mass a spectrum exposes for scoring is exactly that used by XTandem
     *
     * @throws Exception
     */
   // @Test
    public void testSpectrumMass() throws Exception {
        XTandemMain main = new XTandemMain(
                XTandemUtilities.getResourceStream("largeSample/tandem.params"),
                "largeSample/tandem.params");
        main.loadScoringTest();
        main.loadSpectra();
        IScoredScan  scan = XTandemUtilities.readScore(SAMPLE_DATA,main);

        Assert.assertNotNull(scan);

        StringBuilder sb = new StringBuilder();
        IXMLAppender appender = new XMLAppender(sb);
        scan.serializeAsString(appender);
        String serialization =  sb.toString();

        IScoredScan scan2 = XTandemUtilities.readScore(serialization,main);
        Assert.assertNotNull(scan2);

        System.out.println(serialization);

        // Test serialization and deserialization
        StringBuilder sb2 = new StringBuilder();
        IXMLAppender appender2 = new XMLAppender(sb2);
        scan.serializeAsString(appender2);
         String serialization2 =  sb2.toString();

        final boolean b1 = XTandemUtilities.equivalentExceptSpace(serialization,
                serialization2);
        Assert.assertTrue(b1);

        final boolean b = scan.equivalent(scan2);
        Assert.assertTrue(b);

        final RawPeptideScan raw = scan.getRaw();


        ISpectralMatch[] spectralMatches = scan.getSpectralMatches();
        Assert.assertEquals(spectralMatches.length,1);
        Assert.assertEquals(scan.getExpectedValue(),86.74789659223,0.0001);

        ISpectralMatch bestMatch = scan.getBestMatch();
        Assert.assertNotNull(bestMatch);
        Assert.assertEquals(spectralMatches[0],bestMatch);
        Assert.assertNotNull(scan.getRaw());

    }



}
