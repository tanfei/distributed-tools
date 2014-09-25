package org.systemsbiology.xtandem.sax;

import org.systemsbiology.xml.*;
import org.systemsbiology.xtandem.*;

import java.util.*;

/**
 * org.systemsbiology.xtandem.sax.AbstractSAXSpectraHandler
 *
 * @author Steve Lewis
 * @date Dec 22, 2010
 */
public abstract class AbstractSAXSpectraHandler extends AbstractSaxParser
{
    public static AbstractSAXSpectraHandler[] EMPTY_ARRAY = {};
    public static Class THIS_CLASS = AbstractSAXSpectraHandler.class;

    private String m_strData;    // For collecting character data.

    private String m_strRt;
    private boolean m_bNetworkData;    // i.e. big endian
    private boolean m_bLowPrecision;    // i.e. 32-bit (v. 64-bit)
    private boolean m_bCompressed;        // true if mzxml compressed peak lists - Brian Pratt for LabKey
    private boolean m_bGaml;            // true if current file is GAML spectra
    private String m_strDesc;        // description from GAML file <note label="Description">.....</note> or filename.scanid.charge
    private double m_dSum;            // from <group> in GAML file sumI="8.41"
    private double m_dMax;            // maxI="1.9242e+007"
    private double m_dFactor;        // fI="192420"
    private double m_dProton;
    private int m_scanNum;    // Scan number
    private int m_cidLevel;    // MS level
    private int m_peaksCount;    // Count of peaks in spectrum
    private int m_lenCompressed; // Length of data compressed
    private int m_precursorCharge;    // Precursor charge
    private double m_precursorMz;    // Precursor M/Z
    private final List<ISpectrumPeak> m_Peaks = new ArrayList<ISpectrumPeak>();

//    private final List<Float> m_vfM = new ArrayList<Float>();
 //   private final List<Float> m_vfI = new ArrayList<Float>();    // Peak list vectors (masses and charges)

    private final List<Spectrum> m_pvSpec = new ArrayList<Spectrum>(); //Doit pointer sur le m_vSpectra de mprocess
    private SpectrumCondition m_pSpecCond; //Doit pointer sur m_specCondition
    private MScore m_pScore; // the object that is used to score sequences and spectra

    private int m_tId; // the id number of a spectrum
    private Spectrum m_specCurrent = new Spectrum();
    //  mspectrum m_specCurrent; //Usage temporaire dans startElement et endElement
    private long m_lLoaded; //if greater than max, output '.' during spectra loading

    public AbstractSAXSpectraHandler(List<Spectrum> _vS, SpectrumCondition _sC, MScore _m)
    {
        m_pvSpec.clear();
        m_pvSpec.addAll(_vS);
        m_pSpecCond = _sC;
        m_pScore = _m;

        m_tId = 1;
        m_lLoaded = 0;
        m_bNetworkData = true;
        m_bLowPrecision = true;
        m_bGaml = false;
        m_dProton = XTandemUtilities.getProtonMass();

        reset();
    }

    void reset()
    {
        m_peaksCount = 0;
        m_precursorCharge = 0;
        m_precursorMz = 0;
    }



    public void pushSpectrum()
    {
        int i = 0;
        if (m_precursorCharge > 0)    // Known charge
        {
            pushSpectrum(m_precursorCharge);
        }
        else     // Not sure about the charge state
        {
            // Guess charge state
            m_precursorCharge = guessCharge();

            if (m_precursorCharge == 1) {
                pushSpectrum(1);
            }
            else    // Multiple charge, most likely 2 or 3
            {
                pushSpectrum(2);
                m_tId += 100000000;
                pushSpectrum(3);
                m_tId -= 100000000;
            }
        }
    }

    public void pushPeaks(boolean bM /*= true*/, boolean bI /*= true*/)
    {
//        if (bM)
//            m_vfM.clear();
//        if (bI)
//            m_vfI.clear();

        m_Peaks.clear();
        if (m_bGaml) {
            throw new UnsupportedOperationException("Fix This"); // ToDo
//            int a=0;
//            String pLine =  m_strData ;
//            String pValue = null;
//             pValue = pLine;
//            if(bM){
//                while(*pValue != '\0' && a < m_peaksCount)	{
//                    while(*pValue != '\0' && isspace(*pValue))
//                        pValue++;
//                    if(pValue == '\0')
//                        break;
//                    m_vfM.push_back((float)atof(pValue));
//                    while(*pValue != '\0' && !isspace(*pValue))
//                        pValue++;
//                    a++;
//                }
//            }
//            else{
//                while(*pValue != '\0' && a < m_peaksCount)	{
//                    while(*pValue != '\0' && isspace(*pValue))
//                        pValue++;
//                    if(pValue == '\0')
//                        break;
//                    m_vfI.push_back((float)atof(pValue));
//                    while(*pValue != '\0' && !isspace(*pValue))
//                        pValue++;
//                    a++;
//                }
//            }
//            delete pLine;
        }
        else {
            if (m_bLowPrecision) {
                decode32(bM, bI);
            }
            else {
                decode64(bM, bI);
            }
        }
    }

    public void decode32(boolean bM /*= true*/, boolean bI /*= true*/)
    {
// This code block was revised so that it packs floats correctly
// on both 64 and 32 bit machines, by making use of the uint32_t
// data type. -S. Wiley
        String pData = m_strData;
        int stringSize = m_strData.length();
        int setCount = 0;
        if (bM == true) {
            setCount++;
        }
        if (bI == true) {
            setCount++;
        }
        if(m_bCompressed)
            throw new UnsupportedOperationException("Fix This"); // ToDo
        final ISpectrumPeak[] spectrumPeaks = XTandemUtilities.decodePeaks(pData,
                 MassResolution.Bits32);
        m_Peaks.addAll(Arrays.asList(spectrumPeaks));
//        for (int i = 0; i < spectrumPeaks.length; i++) {
//            SpectrumPeak spectrumPeak = spectrumPeaks[i];
//            m_vfM.add((float)spectrumPeak.getMass());
//            m_vfI.add( spectrumPeak.getPeak());
//
//        }

      }

    /**
     * Converts a 4 byte array of unsigned bytes to an long
     *
     * @param b an array of 4 unsigned bytes
     * @return a long representing the unsigned int
     */
    public static final int bytestoInteger(byte[] b, int start)
    {
        int l = 0;
        l |= b[start] & 0xFF;
        l <<= 8;
        l |= b[start + 1] & 0xFF;
        l <<= 8;
        l |= b[start + 2] & 0xFF;
        l <<= 8;
        l |= b[start + 3] & 0xFF;
        return l;
    }


    void decode64(boolean bM /*= true*/, boolean bI /*= true*/)
    {
// This code block was revised so that it packs floats correctly
// on both 64 and 32 bit machines, by making use of the uint32_t
// data type. -S. Wiley
        String pData = m_strData;
        int stringSize = m_strData.length();
        int setCount = 0;
        if (bM == true) {
            setCount++;
        }
        if (bI == true) {
            setCount++;
        }
        if(m_bCompressed)
            throw new UnsupportedOperationException("Fix This"); // ToDo
        final ISpectrumPeak[] spectrumPeaks = XTandemUtilities.decodePeaks(pData,
                 MassResolution.Bits64);
        m_Peaks.addAll(Arrays.asList(spectrumPeaks));
//        for (int i = 0; i < spectrumPeaks.length; i++) {
//            SpectrumPeak spectrumPeak = spectrumPeaks[i];
//            m_vfM.add((float)spectrumPeak.getMass());
//            m_vfI.add( spectrumPeak.getPeak());
//
//        }
     }

    int dtohl(int l, boolean bNet)
    {
        // mzData allows little-endian data format, so...
        // If it is not network (i.e. big-endian) data, reverse the byte
        // order to make it network format, and then use ntohl (network to host)
        // to get it into the host format.
        //if compiled on OSX the reverse is true
        if (!bNet) {
            l = (l << 24) | ((l << 8) & 0xFF0000) |
                    (l >> 24) | ((l >> 8) & 0x00FF00);
        }
        return l;
    }

    long dtohl(long l, boolean bNet)
    {
        // mzData allows little-endian data format, so...
        // If it is not network (i.e. big-endian) data, reverse the byte
        // order to make it network format, and then use ntohl (network to host)
        // to get it into the host format.
        //if compiled on OSX the reverse is true
        if (!bNet) {
            l = (l << 56) | ((l << 40) & 0x00FF000000000000L) | ((l << 24) & 0x0000FF0000000000L) | ((l << 8) & 0x000000FF00000000L) |
                    (l >> 56) | ((l >> 40) & 0x000000000000FF00L) | ((l >> 24) & 0x0000000000FF0000L) | ((l >> 8) & 0x00000000FF000000L);
        }
        return l;
    }

    void pushSpectrum(int charge)
    {
        int lLimit = 2000;
        /*
        * create a temporary mspectrum object
        */
        m_specCurrent.settId(m_tId);
        m_specCurrent.setStrRt(m_strRt);
        if (m_bGaml) {
            m_specCurrent.setdMH(m_precursorMz);
            m_specCurrent.clearVDStats();
            m_specCurrent.addVDStat(m_dSum);
            m_specCurrent.addVDStat(m_dMax);
            m_specCurrent.addVDStat(m_dFactor);
        }
        else {
            m_specCurrent.setdMH((m_precursorMz - m_dProton) * (float) charge + m_dProton);
        }
        m_specCurrent.setfZ((float) charge);

//        SpectrumMI miCurrent = new SpectrumMI();
        m_specCurrent.clear_intensity_values();

        for(ISpectrumPeak peak : m_Peaks)  {
           if (peak.getMassChargeRatio() > 0 && peak.getPeak() > 0)
                 m_specCurrent.addVMI(peak);
        }
//        for (int i = 0; i < m_vfM.size(); i++) {
//            miCurrent.setfM(m_vfM.get(i));
//            miCurrent.setfI(m_vfI.get(i));
//            // Only push if both mass and intensity are non-zero.
//            if (miCurrent.getfM() != 0 && miCurrent.getfI() != 0)
//                m_specCurrent.addVMI(miCurrent);
//        }


        if (m_bGaml && m_strDesc != "") {
            m_specCurrent.setStrDescription(m_strDesc);
        }
        else {
            setDescription();
        }

        //Ajout du spectre a m_vSpectra
        if (m_pSpecCond.condition(m_specCurrent, m_pScore)) { // || true to force
            addSpectrum(m_specCurrent);
            m_lLoaded++;
            if (m_lLoaded == lLimit) {
                XMLUtilities.outputText(".");
                m_lLoaded = 0;
            }
        }
    }

    public void addSpectrum(Spectrum s)
    {
        m_pvSpec.add(s);
    }

    int guessCharge()
    {
        // All this small routine does is trying to guess the charge state of the precursor
        // from the ratio of the integrals of the intensities below and above m_precursorMz
        float intBelow = 0;
        float intTotal = 0;

        for(ISpectrumPeak peak  : m_Peaks) {
            intTotal += peak.getPeak();
            if (peak.getMassChargeRatio() < m_precursorMz)
                intBelow +=  peak.getPeak();

        }
//        int length = m_Peaks.size();
//        for (int i = 0; i < length; i++) {
//            intTotal += m_vfI.get(i);
//            if (m_vfM.get(i) < m_precursorMz)
//                intBelow += m_vfI.get(i);
//        }

        // There is no particular reason for the 0.95. It's there just
        // to compensate for the noise.... 
        if (intTotal == 0.0 || intBelow / intTotal > 0.95)
            return 1;
        else
            return 2;
    }

    void setDescription()
    {
        String fileName = getStrFileName();
        if(fileName == null)
            return;

         int pos = fileName.lastIndexOf("/");
         String desc = fileName.substring(pos + 1);
        desc += " scan ";
        desc += m_scanNum;
        desc += " (charge ";
        desc += (int)m_specCurrent.getfZ();
        desc += ")";
        m_specCurrent.setStrDescription(desc);

//            m_specCurrent.m_strDescription.clear();
//        int iPos = 0;
//        int iSlash = m_strFileName.rfind('/');
//        if (iSlash != string::npos && iSlash > iPos)
//            iPos = iSlash + 1;
//        int iBack = m_strFileName.rfind('\\');
//        if (iBack != string::npos && iBack > iPos)
//            iPos = iBack + 1;
//        m_specCurrent.m_strDescription += m_strFileName.substr(iPos);
//        m_specCurrent.m_strDescription += " scan ";
//        char buffer[20];
//        sprintf(buffer, "%d", m_scanNum);
//        m_specCurrent.m_strDescription += buffer;
//        m_specCurrent.m_strDescription += " (charge ";
//        sprintf(buffer, "%d", (int)m_specCurrent.m_fZ);
//        m_specCurrent.m_strDescription += buffer;
//        m_specCurrent.m_strDescription += ")";
    }


    public String getStrData()
    {
        return m_strData;
    }

    public void setStrData(String pStrData)
    {
        m_strData = pStrData;
    }

    public long getlLoaded()
    {
        return m_lLoaded;
    }

    public void setlLoaded(long pLLoaded)
    {
        m_lLoaded = pLLoaded;
    }

    public int gettId()
    {
        return m_tId;
    }

    public void settId(int pTId)
    {
        m_tId = pTId;
    }

    public MScore getpScore()
    {
        return m_pScore;
    }

    public void setpScore(MScore pPScore)
    {
        m_pScore = pPScore;
    }

    public SpectrumCondition getpSpecCond()
    {
        return m_pSpecCond;
    }

    public void setpSpecCond(SpectrumCondition pPSpecCond)
    {
        m_pSpecCond = pPSpecCond;
    }

    public double getPrecursorMz()
    {
        return m_precursorMz;
    }

    public void setPrecursorMz(double pPrecursorMz)
    {
        m_precursorMz = pPrecursorMz;
    }

    public int getPrecursorCharge()
    {
        return m_precursorCharge;
    }

    public void setPrecursorCharge(int pPrecursorCharge)
    {
        m_precursorCharge = pPrecursorCharge;
    }

    public int getLenCompressed()
    {
        return m_lenCompressed;
    }

    public void setLenCompressed(int pLenCompressed)
    {
        m_lenCompressed = pLenCompressed;
    }

    public int getPeaksCount()
    {
        return m_peaksCount;
    }

    public void setPeaksCount(int pPeaksCount)
    {
        m_peaksCount = pPeaksCount;
    }

    public int getCidLevel()
    {
        return m_cidLevel;
    }

    public void setCidLevel(int pCidLevel)
    {
        m_cidLevel = pCidLevel;
    }

    public int getScanNum()
    {
        return m_scanNum;
    }

    public void setScanNum(int pScanNum)
    {
        m_scanNum = pScanNum;
    }

    public double getdFactor()
    {
        return m_dFactor;
    }

    public void setdFactor(double pDFactor)
    {
        m_dFactor = pDFactor;
    }

    public double getdProton()
    {
        return m_dProton;
    }

    public void setdProton(double pDProton)
    {
        m_dProton = pDProton;
    }

    public double getdMax()
    {
        return m_dMax;
    }

    public void setdMax(double pDMax)
    {
        m_dMax = pDMax;
    }

    public double getdSum()
    {
        return m_dSum;
    }

    public void setdSum(double pDSum)
    {
        m_dSum = pDSum;
    }

    public String getStrDesc()
    {
        return m_strDesc;
    }

    public void setStrDesc(String pStrDesc)
    {
        m_strDesc = pStrDesc;
    }

    public boolean isbGaml()
    {
        return m_bGaml;
    }

    public void setbGaml(boolean pBGaml)
    {
        m_bGaml = pBGaml;
    }

    public boolean isbCompressed()
    {
        return m_bCompressed;
    }

    public void setbCompressed(boolean pBCompressed)
    {
        m_bCompressed = pBCompressed;
    }

    public boolean isbLowPrecision()
    {
        return m_bLowPrecision;
    }

    public void setbLowPrecision(boolean pBLowPrecision)
    {
        m_bLowPrecision = pBLowPrecision;
    }

    public boolean isbNetworkData()
    {
        return m_bNetworkData;
    }

    public void setbNetworkData(boolean pBNetworkData)
    {
        m_bNetworkData = pBNetworkData;
    }

    public String getStrRt()
    {
        return m_strRt;
    }

    public void setStrRt(String pStrRt)
    {
        m_strRt = pStrRt;
    }
}
