package org.systemsbiology.xtandem.sax;

import org.systemsbiology.sax.*;
import org.systemsbiology.xtandem.*;
import org.systemsbiology.xtandem.bioml.sax.*;
import org.systemsbiology.xtandem.mzml.*;
import org.xml.sax.*;

/**
 * org.systemsbiology.xtandem.sax.MzXMLScanHandler
 * handle data from a raw scan return a RawPeptideScan
 *
 * @author Steve Lewis
 * @date Dec 23, 2010
 */
public class MzMLSpectrumHandler extends AbstractXTandemElementSaxHandler<RawPeptideScan> {
    public static MzMLSpectrumHandler[] EMPTY_ARRAY = {};
    public static Class THIS_CLASS = MzMLSpectrumHandler.class;
    
    /*
     * Accession number constants to use when parsing mzML XML file.
     */
    public static final  String ACC_CONTACT_NAME = "MS:1000586";
    public static final  String ACC_CONTACT_ADDRESS = "MS:1000587";
    public static final  String ACC_CONTACT_URL = "MS:1000588";
    public static final  String ACC_CONTACT_EMAIL = "MS:1000589";
    public static final  String ACC_INSTRUMENT_NAME = "MS:1000554";
    public static final  String ACC_INSTRUMENT_SERIAL_NO = "MS:1000529";
    public static final  String ACC_INSTRUMENT_MODEL = "MS:1000031";
    public static final  String ACC_SCAN_TIME = "MS:1000016";
    public static final  String ACC_UNIT_MINUTE_MS = "MS:1000038";
    public static final  String ACC_UNIT_SECOND_MS = "MS:1000039";
    public static final  String ACC_UNIT_MINUTE_UO = "UO:0000031";
    public static final  String ACC_UNIT_SECOND_UO = "UO:0000010";
    public static final  String ACC_MZ_ARRAY = "MS:1000514";
    public static final  String ACC_INTENSITY_ARRAY = "MS:1000515";
    public static final  String ACC_32_BIT_FLOAT = "MS:1000521";
    public static final  String ACC_64_BIT_FLOAT = "MS:1000523";
    public static final  String ACC_ZLIB_COMPRESSION = "MS:1000574";
    public static final  String ACC_NO_COMPRESSION = "MS:1000576";
    public static final  String ACC_SELECTED_MASS_TO_CHARGE_RATIO = "MS:1000744";
    public static final  String ACC_CHARGE_STATE = "MS:1000041";
    public static final  String ACC_INTENSITY = "MS:1000042";
    

    public static final String TAG = "spectrum";

    private double[] m_MzValues;
    private double[] m_Intensities;

    private final StringBuffer m_BinaryText = new StringBuffer();
    private boolean m_InBinaryData;
    private boolean m_32Bits;

    private boolean m_BinaryMZ;
    private int m_PrecursorCharge;
    private double m_PrecursorMz;
    private double m_PrecursorIntensity;


    public MzMLSpectrumHandler(IElementHandler parent) {
        super(TAG, parent);
    }

    public boolean is32Bits() {
        return m_32Bits;
    }

    public void set32Bits(final boolean p32Bits) {
        m_32Bits = p32Bits;
    }

    public boolean isBinaryMZ() {
        return m_BinaryMZ;
    }

    public void setBinaryMZ(final boolean pBinaryMZ) {
        m_BinaryMZ = pBinaryMZ;
    }

    public String getBinaryText() {
        return m_BinaryText.toString();
    }

    public void setBinaryText(final String pBinaryText) {
        if(pBinaryText == null) {
            m_BinaryText.setLength(0);
            return;
        }
        String filteredText = MzMlUtilities.filterBase64(pBinaryText);
        m_BinaryText.append(filteredText);
    }

    public boolean isInBinaryData() {
        return m_InBinaryData;
    }

    public void setInBinaryData(final boolean pInBinaryData) {
        m_InBinaryData = pInBinaryData;
    }

    public double[] getMzValues() {
        return m_MzValues;
    }

    public void setMzValues(final double[] pMzValues) {
        m_MzValues = pMzValues;
    }

    public double[] getIntensities() {
        return m_Intensities;
    }

    public void setIntensities(final double[] pIntensities) {
        m_Intensities = pIntensities;
    }

    public int getPrecursorCharge() {
        return m_PrecursorCharge;
    }

    public void setPrecursorCharge(final int pPrecursorCharge) {
        m_PrecursorCharge = pPrecursorCharge;
    }

    public double getPrecursorIntensity() {
        return m_PrecursorIntensity;
    }

    public void setPrecursorIntensity(final double pPrecursorIntensity) {
        m_PrecursorIntensity = pPrecursorIntensity;
    }

    public double getPrecursorMz() {
        return m_PrecursorMz;
    }

    public void setPrecursorMz(final double pPrecursorMz) {
        m_PrecursorMz = pPrecursorMz;
    }

    @Override
    public void handleAttributes(String elx, String localName, String el, Attributes attr)
            throws SAXException {
        String id = XTandemSaxUtilities.getRequiredAttribute("id", attr);
         RawPeptideScan rs = new RawPeptideScan(id, id);
        String numberStr = attr.getValue("scanNumber");
         if(numberStr == null)
                numberStr = attr.getValue("index");
        if(numberStr != null)    {
            int scanNumber = Integer.parseInt(numberStr);
             rs.setScanNumber(scanNumber);

        }
        else {
            XTandemUtilities.breakHere();
        }
         setElementObject(rs);

        return;
    }

    /**
     * Receive notification of the start of an element.
     * <p/>
     * <p>By default, do nothing.  Application writers may override this
     * method in a subclass to take specific actions at the start of
     * each element (such as allocating a new tree node or writing
     * output to a file).</p>
     *
     * @param uri        The Namespace URI, or the empty string if the
     *                   element has no Namespace URI or if Namespace
     *                   processing is not being performed.
     * @param localName  The local name (without prefix), or the
     *                   empty string if Namespace processing is not being
     *                   performed.
     * @param el         The qualified name (with prefix), or the
     *                   empty string if qualified names are not available.
     * @param attributes The attributes attached to the element.  If
     *                   there are no attributes, it shall be an empty
     *                   Attributes object.
     * @throws org.xml.sax.SAXException Any SAX exception, possibly
     *                                  wrapping another exception.
     * @see org.xml.sax.ContentHandler#startElement
     */
    @Override
    public void startElement(String uri, String localName, String el, Attributes attributes)
            throws SAXException {
        if ("cvParam".equals(el)) {
            handleCvParam(attributes);
            return;
        }
        if ("activation".equals(el)) {
            return;
        }
        if ("binaryDataArray".equals(el)) {
            return;
        }
        if ("binary".equals(el)) {
            setInBinaryData(true);
            return;
        }
        super.startElement(uri, localName, el, attributes);

    }

    @Override
    public void endElement(String elx, String localName, String el) throws SAXException {
        if ("binary".equals(el)) {
            String binaryText = getBinaryText();
             double[] realData = MzMlUtilities.decodeDataString(binaryText,is32Bits());
            setBinaryText(null);
            if (isBinaryMZ())
                setMzValues(realData);
            else
                setIntensities(realData);
            setInBinaryData(false);
            return;
        }
        if ("cvParam".equals(el)) {
            return;
        }
        if ("activation".equals(el)) {
            return;
        }
        if ("ionSelection".equals(el)) {
            return;
        }
        if ("precursorList".equals(el)) {
            return;
        }
        if ("precursor".equals(el)) {
            buildPrecursor();
            return;
        }
        if ("paramGroupRef".equals(el)) {
            return;
        }
        if ("selectionWindow".equals(el)) {
            return;
        }
        if ("selectionWindowList".equals(el)) {
            return;
        }
        if ("scan".equals(el)) {
              return;
          }
        if ("scanList".equals(el)) {
              return;
          }
        if ("scanWindowList".equals(el)) {
               return;
           }
        if ("scanWindow".equals(el)) {
               return;
           }
         if ("userParam".equals(el)) {
              return;
          }
          if ("spectrumDescription".equals(el)) {
            return;
        }
        if ("binaryDataArray".equals(el)) {
              return;
          }
        if ("binaryDataArrayList".equals(el)) {
              return;
          }
           if ("isolationWindow".equals(el)) {
              return;
          }
        if ("selectedIonList".equals(el)) {
               return;
           }
        if ("selectedIon".equals(el)) {
               return;
           }

        //        if ("peaks".equals(el)) {
//            MzXMLPeaksHandler handler = (MzXMLPeaksHandler) getHandler().popCurrentHandler();
//            setPeaks(handler.getElementObject());
//            return;
//        }
//        else if ("precursorMz".equals(el)) {
//            MzXMLPrecursorMzHandler handler = (MzXMLPrecursorMzHandler) getHandler().popCurrentHandler();
//            setPrecursorMz(handler.getElementObject());
//            return;
//        }
        super.endElement(elx, localName, el);
    }

    private void buildPrecursor() {
        RawPeptideScan rs = getElementObject();
        FragmentationMethod f = FragmentationMethod.CID;
        double intensity = 0; // getPrecursorMz();
        double mz = getPrecursorMz();
        int charge = getPrecursorCharge();
        IScanPrecursorMZ sp = new ScanPrecursorMz(intensity, charge, mz, f);
        rs.setPrecursorMz(sp);
    }

    @Override
    public void characters(final char[] s, final int start, final int length) throws SAXException {
        if (isInBinaryData()) {
            setBinaryText(new String(s, start, length ));
            return;
        }
        super.characters(s, start, length);    //To change body of overridden methods use File | Settings | File Templates.
    }

    protected void handleCvParam(Attributes attributes) {
        RawPeptideScan rs = getElementObject();
        String name = XTandemSaxUtilities.getRequiredAttribute("name", attributes);
        String accession = XTandemSaxUtilities.getRequiredAttribute("accession", attributes);
        String value = XTandemSaxUtilities.getRequiredAttribute("value", attributes);

        rs.addAddedValue(name,value);
        if ("MS:1000133".equals(accession)) {
            rs.setActivationMethod(FragmentationMethod.CID);
            return;
        }
        if ("MS:1000598".equals(accession)) {
            rs.setActivationMethod(FragmentationMethod.ETD);
            return;
        }
        // todo add other activations
        if ("MS:1000528".equals(accession)) // "lowest m/z value\"
        {
            rs.setLowMz(XTandemSaxUtilities.getRequiredDoubleAttribute("value", attributes));
            return;
        }
        if ("MS:1000527".equals(accession))  // "highest m/z value\"
        {
            rs.setHighMz(XTandemSaxUtilities.getRequiredDoubleAttribute("value", attributes));
            return;
        }
        if ("MS:1000505".equals(accession))  // "base peak intensity
        {
            rs.setBasePeakIntensity(XTandemSaxUtilities.getRequiredDoubleAttribute("value", attributes));
            return;
        }

        if (ACC_SELECTED_MASS_TO_CHARGE_RATIO.equals(accession))  // "total ion current
        {
             setPrecursorMz(XTandemSaxUtilities.getRequiredDoubleAttribute("value", attributes)) ;
            return;
        }

        //  "<cvParam cvLabel=\"MS\" accession=\"MS:1000514\" name=\"m/z array\" value=\"\"/>\n" +
        if ("MS:1000514".equals(accession))  // "total ion current
        {
            setBinaryMZ(true);
            return;
        }
        // "<cvParam cvLabel=\"MS\" accession=\"MS:1000515\" name=\"intensity array\" value=\"\"/>\n" +
        if ("MS:1000515".equals(accession))  // "total ion current
        {
            setBinaryMZ(false);
            return;
        }
        //"<cvParam cvLabel=\"MS\" accession=\"MS:1000040\" name=\"m/z\" value=\"445.34\"/>\n" +
        if ("MS:1000040".equals(accession))  // "total ion current
        {
            setPrecursorMz(Double.parseDouble(value));
            return;
        }

        //      "<cvParam cvLabel=\"MS\" accession=\"MS:1000041\" name=\"charge state\" value=\"2\"/>\n" +
        if ("MS:1000041".equals(accession))  // "total ion current
        {
            setPrecursorCharge(Integer.parseInt(value));
            return;
        }
        //"<cvParam cvLabel=\"MS\" accession=\"MS:1000580\" name=\"MSn spectrum\" value=\"\"/>\n" +
        if ("MS:1000580".equals(accession))  // "total ion current
        {
            return;
        }
        // <cvParam cvLabel=\"MS\" accession=\"MS:1000127\" name=\"centroid mass spectrum\" value=\"\"/>\n"
        if ("MS:1000127".equals(accession))  // "total ion current
        {
            return;
        }
//        "<cvParam cvLabel=\"MS\" accession=\"MS:1000504\" name=\"base peak m/z\" value=\"456.347\"/>\n" +
        if ("MS:1000504".equals(accession))  // "total ion current
        {
            double value1 = XTandemSaxUtilities.getRequiredDoubleAttribute("value", attributes);
            rs.setBasePeakMz(value1);
            setPrecursorMz(value1);
            return;
        }
//               "<cvParam cvLabel=\"MS\" accession=\"MS:1000505\" name=\"base peak intensity\" value=\"23433\"/>\n" +
        if ("MS:1000505".equals(accession))
        {
            rs.setBasePeakIntensity(XTandemSaxUtilities.getRequiredDoubleAttribute("value", attributes));
            return;
        }
//               "<cvParam cvLabel=\"MS\" accession=\"MS:1000285\" name=\"total ion current\" value=\"1.66755e+007\"/>\n" +
        if ("MS:1000285".equals(accession))  // "total ion current
        {
            return;
        }
        // "<cvParam cvLabel=\"MS\" accession=\"MS:1000045\" name=\"collision energy\" value=\"35.00\" unitAccession=\"MS:1000137\" unitName=\"Electron Volt\"/>\n

        if ("MS:1000045".equals(accession))
        {
            return;
        }
//        "<cvParam cvLabel=\"MS\" accession=\"MS:1000016\" name=\"scan time\" value=\"5.990500\" unitAccession=\"MS:1000038\" unitName=\"minute\"/>\n" +
        if ("MS:1000016".equals(accession))
        {
            rs.setIonInjectionTime(XTandemSaxUtilities.getRequiredDoubleAttribute("value", attributes));
            return;
        }
//                  "<cvParam cvLabel=\"MS\" accession=\"MS:1000512\" name=\"filter string\" value=\"+ c d Full ms2  445.35@cid35.00 [ 110.00-905.00]\"/>\n" +
        if ("MS:1000512".equals(accession))  // "total ion current
        {
            rs.setFilterLine(attributes.getValue("value"));
             return;
        }
//                  "<cvParam cvLabel=\"MS\" accession=\"MS:1000501\" name=\"scan m/z lower limit\" value=\"110.000000\"/>\n" +
        if ("MS:1000501".equals(accession))
        {
            rs.setScanWindowLowerLimit(XTandemSaxUtilities.getRequiredDoubleAttribute("value", attributes));
             return;
        }
//                  "<cvParam cvLabel=\"MS\" accession=\"MS:1000500\" name=\"scan m/z upper limit\" value=\"905.000000\"/>\n" +
        if ("MS:1000500".equals(accession))
        {
            rs.setScanWindowUpperLimit(XTandemSaxUtilities.getRequiredDoubleAttribute("value", attributes));
            return;
        }
       // "<cvParam cvLabel=\"MS\" accession=\"MS:1000523\" name=\"64-bit float\" value=\"\"/>\n" +
        if ("MS:1000523".equals(accession))
        {
            set32Bits(false);
               return;
        }
       // "<cvParam cvLabel=\"MS\" accession=\"MS:1000523\" name=\"32-bit float\" value=\"\"/>\n" +
        if ("MS:1000521".equals(accession))
        {
            set32Bits(true);
            return;
        }
      //  "<cvParam cvLabel=\"MS\" accession=\"MS:1000576\" name=\"profile spectrum\" value=\"\"/>\n" +
        if ("MS:1000128".equals(accession))
        {
            // todo handle
            return;
        }
    //  "<cvParam cvLabel=\"MS\" accession=\"MS:1000576\" name=\"no combination\" value=\"\"/>\n" +
        if ("MS:1000795".equals(accession))
        {
              // todo handle
            return;
        }
  //  "<cvParam cvLabel=\"MS\" accession=\"MS:1000576\" name=\"isolation window lower offset\" value=\"\"/>\n" +
        if ("MS:1000828".equals(accession))  // "total ion current
        {
            rs.setScanWindowLowerLimit(XTandemSaxUtilities.getRequiredDoubleAttribute("value", attributes));
             return;
        }
  //  "<cvParam cvLabel=\"MS\" accession=\"MS:1000576\" name=\"isolation window upper offset\" value=\"\"/>\n" +
        if ("MS:1000829".equals(accession))  // "total ion current
        {
            rs.setScanWindowUpperLimit(XTandemSaxUtilities.getRequiredDoubleAttribute("value", attributes));
              return;
        }
   //  "<cvParam cvLabel=\"MS\" accession=\"MS:1000576\" name=\"selected ion m/z\" value=\"\"/>\n" +
        if (ACC_SELECTED_MASS_TO_CHARGE_RATIO.equals(accession))  // "total ion current
        {
            IScanPrecursorMZ precursorMz = rs.getPrecursorMz();
  //          precursorMz.(XTandemSaxUtilities.getRequiredDoubleAttribute("value", attributes));
            return;
        }
   //  "<cvParam cvLabel=\"MS\" accession=\"MS:1000576\" name=\"isolation window target m/z\" value=\"\"/>\n" +
        if ("MS:1000827".equals(accession))
        {
            // todo handle
            return;
        }
      //  "<cvParam cvLabel=\"MS\" accession=\"MS:1000576\" name=\"preset scan configuration\" value=\"\"/>\n" +
        if ("MS:1000616".equals(accession))
        {
            // todo handle
            return;
        }
    //  "<cvParam cvLabel=\"MS\" accession=\"MS:1000576\" name=\"no compression\" value=\"\"/>\n" +
        if ("MS:1000576".equals(accession))
        {
            return;
        }
        //  "<cvParam cvLabel=\"MS\" accession=\"MS:1000576\" name=\"ms level\" value=\"2\"/>\n" +
        if ("MS:1000511".equals(accession))
        {
            rs.setMsLevel(XTandemSaxUtilities.getRequiredIntegerAttribute("value", attributes));
            return;
        }
      //  "<cvParam cvLabel=\"MS\" accession=\"MS:1000576\" name=\"positive scan\" value=\"2\"/>\n" +
        if ("MS:1000130".equals(accession))
         {
             rs.setPolarity(ScanPolarity.plus);
             return;
         }
        if ("MS:1000498".equals(accession))     // full scan
         {
            // rs.setPolarity(ScanPolarity.plus);
             return;
         }
        if ("MS:1000042".equals(accession))     // peak intensity
         {
            // rs.setPolarity(ScanPolarity.plus);
             return;
         }
        if ("MS:1000579".equals(accession))     // MS1 spectrum
         {
            // rs.setPolarity(ScanPolarity.plus);
             return;
         }
        if ("MS:1000927".equals(accession))     // ion injection time
         {
            // rs.setPolarity(ScanPolarity.plus);
             return;
         }


        throw new IllegalArgumentException("cannot handle cvParam " + accession + "  name=" + name);

    }

    /**
     * finish handling and set up the enclosed object
     * Usually called when the end tag is seen
     */
    @Override
    public void finishProcessing() {
        RawPeptideScan rs = getElementObject();
        IScanPrecursorMZ precursorMz = rs.getPrecursorMz();
        if(precursorMz.getMassChargeRatio() == 0)    {
            precursorMz = new ScanPrecursorMz(getPrecursorIntensity(),getPrecursorCharge(),getPrecursorMz(),FragmentationMethod.CID);
            rs.setPrecursorMz(precursorMz);
        }
        MzMlUtilities.setRawScanPeaks(rs, getMzValues(), getIntensities());
    }
}
