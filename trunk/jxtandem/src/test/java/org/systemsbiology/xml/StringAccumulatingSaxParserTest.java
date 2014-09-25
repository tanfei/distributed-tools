package org.systemsbiology.xml;

import com.lordjoe.utilities.*;
import org.junit.*;
import org.systemsbiology.sax.*;
import org.systemsbiology.xtandem.*;
import org.systemsbiology.xtandem.mzml.*;
import org.systemsbiology.xtandem.hadoop.*;
import org.systemsbiology.xtandem.sax.*;

/**
 * org.systemsbiology.xml.StringAccumulatingSaxParserTest
 * User: steven
 * Date: 4/27/11
 */
public class StringAccumulatingSaxParserTest {
    public static final StringAccumulatingSaxParserTest[] EMPTY_ARRAY = {};

    /**
       * tests that StringAccumulatingSaxParser returns a string equivanent to the original xml
       *  this allows fragments to be saved and later processed
       */
      @Test
      public void testStringAccumulatingParser()
      {
          String testXML = FileUtilities.readInResource(MzMLReaderTests.class,"10spectra_32.mzXML");
          int length = testXML.length();
          Assert.assertTrue(length == 490336);
          testXML = testXML.substring(testXML.indexOf("?>") + 2); // drop <?xml ... ?>

          StringAccumulatingSaxParser handler = new StringAccumulatingSaxParser("mzXML",(IElementHandler)null);

       //   StringAccumulatingSaxParser handler = new StringAccumulatingSaxParser("mzXML",new DelegatingSaxHandler());
           XMLUtilities.parseXMLString(testXML, handler);
          String result = handler.getElementObject();
          boolean condition = XTandemUtilities.equivalentExceptSpace(result, testXML);
          Assert.assertTrue(condition);
      }
    /**
       * tests that StringAccumulatingSaxParser returns a string equivalent to the original xml
       *  this allows fragments to be saved and later processed - uses a convenience static call
       */
      @Test
      public void testStringAccumulatingParserCall()
      {
          String testXML = FileUtilities.readInResource(MzMLReaderTests.class,"10spectra_32.mzXML");
          int length = testXML.length();
          Assert.assertTrue(length == 490336);
          testXML = testXML.substring(testXML.indexOf("?>") + 2); // drop <?xml ... ?>

           String result = StringAccumulatingSaxParser.accumulateStringFromTag(testXML,"mzXML");

       //   StringAccumulatingSaxParser handler = new StringAccumulatingSaxParser("mzXML",new DelegatingSaxHandler());
             boolean condition = XTandemUtilities.equivalentExceptSpace(result, testXML);
          Assert.assertTrue(condition);
      }

}
