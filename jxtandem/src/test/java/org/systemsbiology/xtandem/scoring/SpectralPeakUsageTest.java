package org.systemsbiology.xtandem.scoring;

import org.junit.*;
import org.systemsbiology.sax.*;
import org.systemsbiology.xtandem.*;
import org.systemsbiology.xtandem.sax.*;

/**
 * org.systemsbiology.xtandem.scoring.SpectralPeakUsageTest
 * User: Steve
 * Date: 9/4/11
 */
public class SpectralPeakUsageTest {
    public static final SpectralPeakUsageTest[] EMPTY_ARRAY = {};


    public static final float[] FLOAT_DATA = {
            17856.34f, 892, 453, 2657.0f,
            2.1f, 167654.3f, 67943.345f,
    };

    public static final double[] USAGE_DATA = {
            0.12, 0.34, 0.066,
            0.12, 0.34, 0.066,
            0.12,
    };

    @Test
    public void testUsageSerialization() {
        SpectralPeakUsage peaks = new SpectralPeakUsage(XTandemUtilities.getDefaultConverter());

        for (int i = 0; i < FLOAT_DATA.length; i++) {
            peaks.getAddedAfterUsageCorrection(FLOAT_DATA[i], USAGE_DATA[i]);

        }

        for (int i = 0; i < FLOAT_DATA.length; i++) {
            double usage = peaks.getUsage(FLOAT_DATA[i]);
            Assert.assertEquals(USAGE_DATA[i], usage, 0.0001);
        }

        StringBuilder sb = new StringBuilder();
        XMLAppender appender = new XMLAppender(sb);
        peaks.serializeAsString(appender);
        String serialization = sb.toString();

        SpectralPeakUsage readpeaks = SpectralPeakUsage.deserializeUsage(serialization);
        for (int i = 0; i < FLOAT_DATA.length; i++) {
            double usage = readpeaks.getUsage(FLOAT_DATA[i]);
            Assert.assertEquals(USAGE_DATA[i], usage, 0.0001);
        }

        Assert.assertTrue(peaks.equivalent(readpeaks));

    }


}
