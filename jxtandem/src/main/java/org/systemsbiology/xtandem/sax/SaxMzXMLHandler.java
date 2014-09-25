package org.systemsbiology.xtandem.sax;

import org.systemsbiology.hadoop.*;
import org.systemsbiology.xtandem.*;
import org.xml.sax.*;

import java.io.*;
import java.util.*;

/**
 * org.systemsbiology.xtandem.sax.SaxMzXMLHandler
 *
 * @author Steve Lewis
 * @date Dec 23, 2010
 */
public class SaxMzXMLHandler extends AbstractSAXSpectraHandler
{
    public static SaxMzXMLHandler[] EMPTY_ARRAY = {};
    public static Class THIS_CLASS = SaxMzXMLHandler.class;

    private final Set<Integer> m_sId = new HashSet<Integer>();
    // Flags indicating parser is inside a particular tag.
    private boolean m_bInMsLevel2;
    private boolean m_bInPrecursorMz;
    private boolean m_bInPeaks;
    private List<Integer> m_viPossiblePrecursorCharges = new ArrayList<Integer>();


    public SaxMzXMLHandler(List<Spectrum> _vS, SpectrumCondition _sC, MScore _m)
    {
        super(_vS, _sC, _m);
        m_bInMsLevel2 = false;
        m_bInPrecursorMz = false;
        m_bInPeaks = false;
        //added this (true by default)
        setbNetworkData(false);
    }


    public void addId(int added)
    {
        m_sId.add(added);
    }

    @Override
    public void startElement(String elx, String localName, String el, Attributes attr)
            throws SAXException
    {
        if ("scan".equals(el)) {
            final String value = attr.getValue("msLevel");
            int level = Integer.parseInt(value);
            setCidLevel(level);
            if (level == 2) {
                m_bInMsLevel2 = true;

                reset();    // Clean up for the next scan

                String val = attr.getValue("num");
                int ival = Integer.parseInt(val);
                setScanNum(ival);
                settId(ival);
                addId(ival);

                val = attr.getValue("peaksCount");
                ival = Integer.parseInt(val);
                setPeaksCount(ival);

                val = attr.getValue("retentionTime");
                setStrRt(val);
            }
            return;
        }
        else if ("peaks".equals(el)) {
            m_bInPeaks = true;
            String val = attr.getValue("precision");
            setbLowPrecision(!"64".equals(val));

            setbCompressed(false);
            val = attr.getValue("compressionType");
            if (val != null && !"none".equals(val)) {
                if (!"zlib".equals(val))
                    throw new UnsupportedOperationException(
                            " Unsupported compression type  " + val);
                setbCompressed(true);
                val = attr.getValue("compressedLen");
                if (val == null)
                    throw new UnsupportedOperationException("Missing compressedLen attribute");
                int ival = Integer.parseInt(val);
                setLenCompressed(ival);
            }
            return;
        }
        else if ("precursorMz".equals(el)) {
            if (getCidLevel() < 3) {
                //don't read precursor data if ms level >= 3
                m_bInPrecursorMz = true;
                String val = attr.getValue("precursorCharge");
                int ival = 0;
                if(val != null)
                    Integer.parseInt(val);
                setPrecursorCharge(ival);

                // test for the mzXML 3.1 possibleCharges attribute
                // ex: "7,11,13"
                String possibleCharges = attr.getValue("possibleCharges");
                if (possibleCharges != null && possibleCharges.length() > 0) {
                    String[] vals = possibleCharges.split(",", -1);
                    for (int i = 0; i < vals.length; i++) {
                        String s = vals[i];
                        m_viPossiblePrecursorCharges.add(Integer.parseInt(s));

                    }
                }

            }
            return;
        }
        return;
    }

    @Override
    public void endElement(String elx, String localName, String el) throws SAXException
    {
        // added slewis
        if ("scan".equals(el)) {
            m_bInMsLevel2 = false;
            // only add a spectrum without charge (which will lead
             // to internal xtandem charge state guessing) if there
             // were no values parsed from *both* "precursorCharge"
             // or "possibleCharges"
             final int precursorCharge = getPrecursorCharge();
             if ((precursorCharge == 0) && (m_viPossiblePrecursorCharges.size() == 0)) {
                 // add spectrum, with precursorMz charge
                 pushSpectrum();
             }

             else {

                 // add the spectrum with the m_precursorCharge value
                 int originalPrecursorMZ = precursorCharge; // do other pushSpectrum calls change this?
                 pushSpectrum(precursorCharge);

                 // are there any multiple precursor charges from mzXML 3.1's
                 // possibleCharges?
                 if (m_viPossiblePrecursorCharges.size() > 0) {
                     int originalId = gettId();
                     for (Integer ir : m_viPossiblePrecursorCharges) {
                         if (ir == originalPrecursorMZ)
                             continue;
                         settId(gettId() + 100000000);
                         pushSpectrum(ir);
                     }
                     settId(originalId);
                 }
                 m_bInMsLevel2 = false;
                 return;
             }

             m_bInMsLevel2 = false;
            return;
        }
        if ("peaks".equals(el)) {
            processData();
            m_bInPeaks = false;
            m_bInMsLevel2 = false;
            return;
        }
        else if ("precursorMz".equals(el)) {
            processData();
            m_bInPrecursorMz = false;
            m_bInMsLevel2 = false;
            return;
        }
     }

    @Override
    public void characters(char[] s, int start, int length) throws SAXException
    {
        if ((m_bInPeaks && getCidLevel() == 2) ||
                (m_bInPrecursorMz)) {
            final String data = getStrData();
            StringBuilder sb = new StringBuilder();
            sb.append(s, start, length);
            String val = sb.toString();
            if (data != null)
                setStrData(data + val);
            else
                setStrData("" + val);
        }
    }

    void processData()
    {
        if (m_bInPeaks && getCidLevel() == 2) {
            pushPeaks(true, true);
        }
        else if (m_bInPrecursorMz) {
            if (getCidLevel() < 3) {
                final String s = getStrData();
                // might be 395.72000000000003 
                float ival = Float.parseFloat(s);
                setPrecursorMz((int)ival);
            }
        }
        setStrData("");

    }

    public Set<Integer> getsId()
    {
        return m_sId;
    }

    public List<Integer> getViPossiblePrecursorCharges()
    {
        return m_viPossiblePrecursorCharges;
    }

    public boolean isbInPeaks()
    {
        return m_bInPeaks;
    }

    public boolean isbInPrecursorMz()
    {
        return m_bInPrecursorMz;
    }

    public boolean isbInMsLevel2()
    {
        return m_bInMsLevel2;
    }

    public static void main(String[] args) throws Exception
    {
        File f = new File(args[0]);
        InputStream inp = new FileInputStream(f);
        List<Spectrum> _vS = new ArrayList<Spectrum>();
        SpectrumCondition _sC = new SpectrumCondition();
        MScore _m = new MScore(IParameterHolder.NULL_PARAMETER_HOLDER);
        SaxMzXMLHandler handlre = new SaxMzXMLHandler(_vS, _sC, _m);
        handlre.parseDocument(inp);
        handlre = null;
    }

}
