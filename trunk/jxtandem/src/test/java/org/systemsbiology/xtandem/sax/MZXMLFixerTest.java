package org.systemsbiology.xtandem.sax;

import org.junit.*;
import org.systemsbiology.sax.*;
import org.systemsbiology.xml.*;
import org.systemsbiology.xtandem.*;
import org.xml.sax.*;

import java.io.*;

/**
 * org.systemsbiology.xtandem.sax.MZXMLFixerTest   
 * It is common to find MZXML files with unmatched tags and malformed URLS like the above
 *  The code attempts to fix these files and this test verifies that the fixes work
 * User: steven
 * Date: 4/29/11
 */
public class MZXMLFixerTest {
    public static final MZXMLFixerTest[] EMPTY_ARRAY = {};
    
    public static final String BAD_MZXML =
            "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n" +
                    "<mzXML xmlns=\"http://sashimi.sourceforge.net/schema_revision/mzXML_3.0\"\n" +
                    " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                    " xsi:schemaLocation=\"http://sashimi.sourceforge.net/schema_revision/mzXML_3.0 http://sashimi.sourceforge.net/schema_revision/mzXML_3.0/mzXML_idx_3.0.xsd\" >\n" +
                    " <msRun scanCount=\"6006\" startTime=\"PT600.036S\" endTime=\"PT4319.82S\" >\n" +
                    "  <parentFile fileName=\"file://MASSSPEC2-XP/T/2DLCSE100318/2DLCSE100318_L_014.RAW\" fileType=\"RAWData\" fileSha1=\"f150373350b8ea2f9f200d1cafb5b9ea8fb5c513\" />\n" +
                    "   <scan num=\"1\"\n" +
                    "    totIonCurrent=\"1.90894e+007\" >\n" +
                    "   <peaks precision=\"32\"\n" +
                    "    byteOrder=\"network\"\n" +
                    "    pairOrder=\"m/z-int\" >jxGPqJxRDzkskXahkVEPR7QRgJOJUQ9VqY7I=</peaks>\n" +
                    "   <scan num=\"2\"\n" +
                    "     totIonCurrent=\"44787.5\"\n" +
                    "    collisionEnergy=\"35\" >\n" +
                    "    <precursorMz precursorIntensity=\"151881\" activationMethod=\"CID\" >262.83</precursorMz>\n" +
                    "    <peaks precision=\"32\"\n" +
                    "     byteOrder=\"network\"\n" +
                    "     pairOrder=\"m/z-int\" >QuGZkk</peaks>\n" +
                    "   </scan>\n" +
                    "   <scan num=\"3\"\n" +
                    "     totIonCurrent=\"74755.6\"\n" +
                    "    collisionEnergy=\"35\" >\n" +
                    "    <precursorMz precursorIntensity=\"148479\" activationMethod=\"CID\" >336.74</precursorMz>\n" +
                    "    <peaks precision=\"32\"\n" +
                    "     byteOrder=\"network\"\n" +
                    "     pairOrder=\"m/z-int\" >QuG/a0OWEBA0PQ=</peaks>\n" +
                    "   </scan>\n" +
                    "   <scan num=\"4\"\n" +
                    "     collisionEnergy=\"35\" >\n" +
                    "    <precursorMz precursorIntensity=\"116305\" activationMethod=\"CID\" >260.81</precursorMz>\n" +
                    "    <peaks precision=\"32\"\n" +
                    "     byteOrder=\"network\"\n" +
                    "     pairOrder=\"m/z-int\" >Qt3mmj+7p</peaks>\n" +
                    "   </scan>\n" +
                    "   <scan num=\"5\"\n" +
                    "      collisionEnergy=\"35\" >\n" +
                    "    <precursorMz precursorIntensity=\"102843\" activationMethod=\"CID\" >334.74</precursorMz>\n" +
                    "    <peaks precision=\"32\"\n" +
                    "     byteOrder=\"network\"\n" +
                    "     pairOrder=\"m/z-int\" >QuHE</peaks>\n" +
                    "   </scan>\n" +
                    "   <scan num=\"6\"\n" +
                    "       collisionEnergy=\"35\" >\n" +
                    "    <precursorMz precursorIntensity=\"102560\" activationMethod=\"CID\" >338.66</precursorMz>\n" +
                    "    <peaks precision=\"32\"\n" +
                    "     byteOrder=\"network\"\n" +
                    "     pairOrder=\"m/z-int\" >QuHEy</peaks>\n" +
                    "   </scan>\n" +
                    " </msRun>\n" +
                    "</mzXML>";

    @Test
    public void testFix() throws Exception {

        // the bad stuff should not work
        byte[] badBytes = BAD_MZXML.getBytes();
        try {
            parseBytes(badBytes);
            Assert.fail("This should not work");
        }
        catch (RuntimeException e) {
            Throwable cause = e.getCause();
            Assert.assertTrue(cause instanceof SAXParseException);
        }

        // so fix it
        LineNumberReader inp = new LineNumberReader(XMLUtilities.stringToReader(BAD_MZXML));
        ByteArrayOutputStream outBytes = new ByteArrayOutputStream();
        PrintWriter out = new PrintWriter(outBytes);
         XTandemUtilities.fixScanTags(inp, out);

        // and now it should work
        byte[] buf = outBytes.toByteArray();
        String str = new String(buf); // look at what we have
        // we have dropped the  xsi:schemaLocation= which might be a malformed url
        Assert.assertFalse(str.contains(XTandemUtilities.SCHEMA_LOCATION_TAG));

        parseBytes(buf);

    }

    protected void parseBytes(final byte[] pBuf) {
        InputStream inp2 = new ByteArrayInputStream(pBuf);
        AbstractElementSaxHandler handler  = new DiscardingSaxParser("mzXML",(DelegatingSaxHandler)null);
        XMLUtilities.parseFile(inp2, handler, "");
    }
}
