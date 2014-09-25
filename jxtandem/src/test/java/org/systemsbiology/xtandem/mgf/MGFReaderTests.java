package org.systemsbiology.xtandem.mgf;


import org.junit.*;
import org.systemsbiology.xtandem.*;


import java.io.*;
import java.util.*;

/**
 * org.systemsbiology.xtandem.mgf.MGFReaderTests
 * User: steven
 * Date: 3/25/13
 */
public class MGFReaderTests {
    public static final MGFReaderTests[] EMPTY_ARRAY = {};

    public static final String MGF_STRING =
            "BEGIN IONS\n" +
                    "TITLE=111749206\n" +
                    "PEPMASS=992.969076\n" +
                    "CHARGE=2+\n" +
                    "320.38278\t3.5\n" +
                    "433.50967\t7.1\n" +
                    "548.22388\t13.5\n" +
                    "685.34204\t46.8\n" +
                    "719.39966\t28.8\n" +
                    "800.37585\t116.0\n" +
                    "914.90295\t598.3\n" +
                    "1038.13965\t75.1\n" +
                    "1092.30835\t57.6\n" +
                    "1193.39893\t63.4\n" +
                    "1340.67883\t10.0\n" +
                    "1437.63416\t79.5\n" +
                    "1552.65173\t51.5\n" +
                    "1640.57629\t18.0\n" +
                    "1738.20154\t2.2\n" +
                    "1810.59561\t73.2\n" +
                    "351.49768\t3.2\n" +
                    "392.05184\t5.4\n" +
                    "555.98425\t5.2\n" +
                    "658.36835\t4.5\n" +
                    "776.53467\t18.2\n" +
                    "833.44678\t21.3\n" +
                    "929.47546\t67.6\n" +
                    "1029.40173\t28.0\n" +
                    "1185.19641\t41.4\n" +
                    "1226.62695\t6.4\n" +
                    "1300.23047\t5.5\n" +
                    "1419.55884\t12.8\n" +
                    "1493.34607\t7.6\n" +
                    "1654.59021\t17.2\n" +
                    "1773.66199\t2.2\n" +
                    "1792.51331\t5.5\n" +
                    "345.3475\t2.7\n" +
                    "446.2587\t3.5\n" +
                    "537.62683\t4.2\n" +
                    "638.17078\t3.3\n" +
                    "768.0047\t6.6\n" +
                    "792.46338\t17.8\n" +
                    "954.98804\t20.3\n" +
                    "1074.37646\t12.7\n" +
                    "1144.33741\t11.9\n" +
                    "1215.49731\t5.2\n" +
                    "1355.49084\t4.9\n" +
                    "1427.69409\t7.7\n" +
                    "1570.2439\t5.1\n" +
                    "1624.04639\t1.2\n" +
                    "1711.16919\t1.7\n" +
                    "1867.42566\t3.5\n" +
                    "326.44226\t2.3\n" +
                    "474.4689\t2.0\n" +
                    "517.2478\t3.9\n" +
                    "677.99765\t3.2\n" +
                    "702.99445\t6.3\n" +
                    "878.41162\t15.1\n" +
                    "893.47498\t12.2\n" +
                    "1022.74512\t6.5\n" +
                    "1124.28613\t7.7\n" +
                    "1233.06348\t2.7\n" +
                    "1368.21655\t3.6\n" +
                    "1443.50793\t5.6\n" +
                    "1534.57971\t3.1\n" +
                    "359.19135\t2.1\n" +
                    "399.28809\t0.8\n" +
                    "563.25531\t3.9\n" +
                    "648.01117\t2.3\n" +
                    "713.17712\t4.3\n" +
                    "807.7666\t6.7\n" +
                    "937.48035\t11.8\n" +
                    "1056.83716\t5.3\n" +
                    "1150.82617\t7.2\n" +
                    "1210.39685\t2.5\n" +
                    "1375.59143\t1.4\n" +
                    "1405.89978\t5.5\n" +
                    "1580.08032\t0.9\n" +
                    "363.18628\t1.8\n" +
                    "439.0647\t0.5\n" +
                    "569.07227\t2.4\n" +
                    "614.20618\t1.9\n" +
                    "784.67834\t4.2\n" +
                    "872.84119\t5.6\n" +
                    "961.63953\t10.6\n" +
                    "1010.22925\t3.4\n" +
                    "1276.5459\t0.8\n" +
                    "1473.61816\t3.8\n" +
                    "377.29718\t1.4\n" +
                    "522.4613\t1.6\n" +
                    "598.22717\t1.5\n" +
                    "737.16754\t3.7\n" +
                    "854.08643\t4.8\n" +
                    "951.91309\t9.9\n" +
                    "1013.76221\t2.3\n" +
                    "1282.32336\t0.4\n" +
                    "1463.65723\t2.7\n" +
                    "286.06775\t1.1\n" +
                    "529.05591\t1.4\n" +
                    "668.30383\t1.3\n" +
                    "695.40674\t2.3\n" +
                    "826.54065\t4.5\n" +
                    "905.2561\t9.3\n" +
                    "1045.7345\t1.6\n" +
                    "1395.59705\t2.3\n" +
                    "632.89044\t1.1\n" +
                    "813.09265\t4.0\n" +
                    "944.09473\t8.7\n" +
                    "1484.94495\t1.7\n" +
                    "848.66418\t3.4\n" +
                    "926.51599\t7.7\n" +
                    "820.80176\t1.5\n" +
                    "891.08875\t2.2\n" +
                    "896.86523\t4.7\n" +
                    "918.5918\t5.3\n" +
                    "921.96448\t4.8\n" +
                    "END IONS\n\n"
            ;

     public static RawPeptideScan readTestScan(String s)
     {
              LineNumberReader inp = getLineNumberReader(s);
             RawPeptideScan ret = XTandemUtilities.readMGFScan(inp, "");
             return ret;
      }

    private static LineNumberReader getLineNumberReader(String s)  {
        try {
            byte[] bytes = s.getBytes("UTF8");
            ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
            InputStreamReader isr = new InputStreamReader(bais);
            return new LineNumberReader(isr);
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);

        }
    }

    /**
     * set of mz as ints * 10000 - needed to work with a set for fast lookup
     * @return
     */
    public static Set<Integer> getPeakAsInts()
    {
        Set<Integer> ret = new HashSet<Integer>();
        try {
            LineNumberReader inp = getLineNumberReader(MGF_STRING);
            String line = inp.readLine();
            while(line != null) {
                String test = line;
                line = inp.readLine();
                if(test.length() == 0)
                    continue;;
                if(test.contains("IONS"))
                    continue;
                if(test.contains("="))
                      continue;
                 String[] split = test.split("\t");
                if(split.length  != 2)
                    throw new IllegalStateException("bad line " + test);
                double mz = Double.parseDouble(split[0]);
                int saved = (int)(mz * 10000 );
                ret.add(saved);
            }
            return ret;
        }
        catch (IOException e) {
            throw new RuntimeException(e);

        }
    }

    /**
     * make sure when we read the peaks (which in the original may be in randon order ) that
     * we see all the peaks and no more
     */
    @Test
    public void testRead()
    {
        RawPeptideScan scan=  readTestScan(MGF_STRING);
        ISpectrumPeak[] peaks = scan.getPeaks();
        // independent peak read
        Set<Integer> mzSet = getPeakAsInts() ;
        Assert.assertEquals(mzSet.size(),peaks.length);
        for (int i = 0; i < peaks.length; i++) {
            ISpectrumPeak peak = peaks[i];
            int test = (int)(peak.getMassChargeRatio() * 10000 );
            if(mzSet.contains(test))
                continue;
            if(mzSet.contains(test - 1 ))
                 continue;
            if(mzSet.contains(test + 1))
                 continue;
            Assert.fail();
        }
     }

    /**
     * make sure when we write an mgf we get the same mgf when we read it
     */
    @Test
    public void testReadWrite()
    {
        RawPeptideScan scan=  readTestScan(MGF_STRING);
        StringBuilder sb = new StringBuilder();
        scan.appendAsMGF(sb);

        RawPeptideScan scan2 =  readTestScan(sb.toString());

        assertEquivalentScans(scan, scan2);
     }

    /**
     * use asserts to show two RawScans are equivalent =
     * We may beed to use in other testing
     * @param scan !null first scan
     * @param scan2  !null first scan
     */
    public static void assertEquivalentScans(RawPeptideScan scan, RawPeptideScan scan2) {
        Assert.assertEquals(scan.getId(), scan2.getId());
        IScanPrecursorMZ p1 = scan.getPrecursorMz();
        IScanPrecursorMZ p2 = scan2.getPrecursorMz();
        Assert.assertEquals(p1, p2);
        Assert.assertEquals(scan.getPrecursorCharge(),scan2.getPrecursorCharge());
        Assert.assertEquals(scan.getPrecursorMass(),scan2.getPrecursorMass(),0.001);

        ISpectrumPeak[] peaks = scan.getPeaks();
        ISpectrumPeak[] peaks2 = scan2.getPeaks();
        Assert.assertEquals(peaks.length,peaks2.length);
        for (int i = 0; i < peaks.length; i++) {
            ISpectrumPeak peak = peaks[i];
            ISpectrumPeak peak2 = peaks[i];
            Assert.assertEquals(peak.getMassChargeRatio(),peak2.getMassChargeRatio(),0.001);
            Assert.assertEquals(peak.getPeak(),peak2.getPeak(),0.001);
        }
    }

}
