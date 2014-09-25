package org.systemsbiology.xtandem.scoring;

/**
 * org.systemsbiology.xtandem.scoring.ScoredScanDeserializationTest
 *
 * @author Steve Lewis
 * @date Mar 8, 2011
 */
public class ScoredScanDeserializationTest
{
    public static ScoredScanDeserializationTest[] EMPTY_ARRAY = {};
    public static Class THIS_CLASS = ScoredScanDeserializationTest.class;

    public static final String SERICLIZED_SCAN =
            "<score id=\"7858\"   expectedValue=\"86.74789659223316\"  >\n" +
                    "<match  peak=\"VNG0675C VNG0675C:25(19)\" score=\"173.526783956915\" hyperscore=\"173.526783956915\">\n" +
                    "<IonScore  A_count=\"0\" A_score=\"0.0\" B_count=\"8\" B_score=\"0.31470590317621827\" C_count=\"0\" C_score=\"0.0\" Y_count=\"6\" Y_score=\"0.45595267112366855\" X_count=\"0\" X_score=\"0.0\" Z_count=\"0\" Z_score=\"0.0\" />\n" +
                    "</match>\n" +
                    "</score>"
            ;
}
