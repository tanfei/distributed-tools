package org.systemsbiology.xtandem.mzml;

import org.proteios.io.*;
import org.systemsbiology.sax.*;
import org.systemsbiology.xtandem.*;
import org.systemsbiology.xtandem.sax.*;

import java.util.*;
import java.util.zip.*;

/**
 * org.systemsbiology.xtandem.mzml.MzMlUtilities
 * code for handling mzml including converters from Proteos representation to mine
 * User: Steve
 * Date: Apr 26, 2011
 */
public class MzMlUtilities {
    public static final MzMlUtilities[] EMPTY_ARRAY = {};

    /**
     * convert a proteos representation of a spectrum into mine
     *
     * @param inp !null proteos spectrum
     * @return !null RawPeptideScan
     */
    public static RawPeptideScan buildSpectrum(ExtendedSpectrumImpl inp) {
         
        RawPeptideScan ret = new RawPeptideScan(inp.getId(), null);
        ret.setScanNumber(inp.getScanNumber());
        ret.setMsLevel(inp.getMSLevel());
  //      ret.setScanNumber(inp.);
        double[] masses = inp.listMass();
        double[] intensities = inp.listIntensities();
        setRawScanPeaks(ret, masses, intensities);


        List<StringPairInterface> dataList = inp.getExtraDataList();
        for (StringPairInterface sp : dataList) {
            handleAddedData(ret, sp.getName(), sp.getValue());
        }

        List<SpectrumPrecursor> precursors = inp.getPrecursors();
        if(precursors != null)  {
            SpectrumPrecursor[] pcrs = precursors.toArray(new SpectrumPrecursor[0]);
             SpectrumPrecursor pre = pcrs[0];
             double precursorIntensity = pre.getIntensity() != null ? pre.getIntensity() : ret.getBasePeakIntensity();
             int pPrecursorCharge = pre.getCharge();
             double precursorMassChargeRatio =   pre.getMassToChargeRatio() != null ? pre.getMassToChargeRatio() : getScanPrecursorMZ_2(pre);
             FragmentationMethod method = fromFragmentationType(pre.getFragmentationType());
             IScanPrecursorMZ precursor = new ScanPrecursorMz(precursorIntensity, pPrecursorCharge, precursorMassChargeRatio, method);
             ret.setPrecursorMz(precursor);
            ret.setActivationMethod(fromFragmentationType(pre.getFragmentationType()));
        }
        else {
            double precursorIntensity = ret.getBasePeakIntensity();
            double precursorMassChargeRatio = ret.getPrecursorMassChargeRatio();
            if(precursorMassChargeRatio == 0)
                precursorMassChargeRatio = ret.getBasePeakMz();
            int pPrecursorCharge = 0;
            FragmentationMethod method = FragmentationMethod.CID;
             IScanPrecursorMZ precursor = new ScanPrecursorMz(precursorIntensity, pPrecursorCharge, precursorMassChargeRatio, method);
            ret.setPrecursorMz(precursor);
            ret.setActivationMethod(method);
        }

         return ret;
    }

    protected static double getScanPrecursorMZ_2( SpectrumPrecursor pre)      {
        List<StringPairInterface> ed   = pre.getExtraDataList();
        double mz = 0;
        for(StringPairInterface sp  : ed)  {
            if("m/z".equals(sp.getName())) {
                double ret = Double.parseDouble(sp.getValue());
                pre.setMassToChargeRatio(ret);
                return ret;
            }

        }
        throw new IllegalStateException("no m/z set");
    }

    public static void setRawScanPeaks(final RawPeptideScan pRet, final double[] pMasses, final double[] pIntensity) {
          List<SpectrumPeak> holder = new ArrayList<SpectrumPeak>();

        for (int i = 0; i < pMasses.length; i++) {
            double intensity = pIntensity[i];
            if(intensity <= 0)
                continue; // no need to handle peaks with 0 or negative intensity
            SpectrumPeak peak = new SpectrumPeak(pMasses[i], (float) intensity);
            holder.add(peak);
        }
        SpectrumPeak[] peaks = new SpectrumPeak[holder.size()];
         holder.toArray(peaks);
           pRet.setPeaks(peaks);
    }

    protected static FragmentationMethod fromFragmentationType(SpectrumPrecursor.FragmentationType inp) {
        switch (inp) {
            case CID:
                return FragmentationMethod.CID;
            case ECD:
                return FragmentationMethod.ECD;
            case ETD:
                return FragmentationMethod.ETD;
            case HCD:
                return FragmentationMethod.HCD;
            case PQD:
                return FragmentationMethod.PQD;
            case PSD:
                return FragmentationMethod.PSD;
            case UNKNOWN:   // todo fix
                return FragmentationMethod.CID;
        }
        throw new UnsupportedOperationException("FragmentationType " + inp + " Unhandled");
    }

    public static final String DEFAULT_CV_LABEL = "MS";
    public static final String CV_PARAM_TAG = "cvParam";
    public static final String USER_PARAM_TAG = "userParam";

    /**
      * special adder for cvParam does this
      *     <cvParam cvLabel=\"MS\" accession=\"MS:1000127\" name=\"centroid mass spectrum\" value=\"\"/>\n

      * @param accession
      * @param name
      * @param value
      * @param cvLabel
      */
     public static void appendCVParam(IXMLAppender appender, String accession, String name, String value, String cvLabel) {
         appender.openTag(CV_PARAM_TAG);
         appender.appendAttribute("cvLabel", cvLabel);
         appender.appendAttribute("accession", accession);
         appender.appendAttribute("name", name);
         if(value == null)
             value = "";
         appender.appendAttribute("value", value);
         appender.closeTag(CV_PARAM_TAG);
       //  appender.cr();
     }
    /**
      * special adder for cvParam does this
      *     <cvParam cvLabel=\"MS\" accession=\"MS:1000127\" name=\"centroid mass spectrum\" value=\"\"/>\n

      * @param accession
      * @param name
      * @param value
      * @param cvLabel
      */
     public static void appendUserParam(IXMLAppender appender,   String name, String value ) {
         appender.openTag(USER_PARAM_TAG);
          appender.appendAttribute("name", name);
         if(value == null)
             value = "";
         appender.appendAttribute("value", value);
         appender.closeTag(USER_PARAM_TAG);
       //  appender.cr();
     }


    /**
      * special
      *
      * @param accession
      * @param name
      * @param value
      * @param cvLabel
      */
     public static void appendCVParam(IXMLAppender appender, String accession, String name, String value) {
         appendCVParam(appender, accession, name, value, DEFAULT_CV_LABEL);
     }

    /**
      * append a param with no value - this is pretty common
      * @param accession
      * @param name
       */
     public static void appendCVParam(IXMLAppender appender, String accession, String name) {
         appendCVParam(appender, accession, name, "");
     }

     public static String encode2(double[] data)
    {
        String ret = XTandemUtilities.encodeData64(data);
        return ret;
    }

    public static  String filterBase64(String dataBase64Raw)
    {
        if(dataBase64Raw == null)
            return null;
	/*
		 * Remove any line break characters from
		 * the base64-encoded block.
		 */
		StringBuffer dataBase64RawStrBuf = new StringBuffer(dataBase64Raw);
		StringBuffer dataBase64StrBuf = new StringBuffer("");
		int nChars = 0;
		int nLines = 0;
		int lineLength = 0;
		boolean newLineFlag = true;
		if (dataBase64Raw != null) {
			if (dataBase64Raw.length() > 0) {
				for (int i = 0; i < dataBase64RawStrBuf.length(); i++) {
					char c = dataBase64RawStrBuf.charAt(i);
					if (c == '\r' || c == '\n') {
						newLineFlag = true;
					} else {
						dataBase64StrBuf.append(c);
						nChars++;
						if (newLineFlag) {
							nLines++;
						}
						if (nLines == 1) {
							lineLength++;
						}
						newLineFlag = false;
					}
				}
			}
		}
		String ret = dataBase64StrBuf.toString();
        return ret;
    }


    public static double[] decodeDataString(final String pDataString,boolean is32Bit) {
        int len = pDataString.length();
        boolean isMultipleOf4 = (len % 4) == 0;
        boolean doublePrecision = !is32Bit;
        boolean bigEndian = false;
        List<Double> dataList = Base64Util.decode(doublePrecision, bigEndian, pDataString);
        int size = dataList.size();
        double[] realData = new double[size];
        for (int i = 0; i < size; i++) {
            realData[i] = dataList.get(i);
            //XTandemUtilities.outputLine("" + realData[i] + ",");
        }
        return realData;
    }

    public static double[] decodeDataString2(final String pDataString) {
        boolean doublePrecision = true;
        boolean bigEndian = false;
        List<Double> dataList = Base64Util.decode(doublePrecision, bigEndian, pDataString);
        int size = dataList.size();
        double[] realData = new double[size];
        for (int i = 0; i < size; i++) {
            realData[i] = dataList.get(i);
            //XTandemUtilities.outputLine("" + realData[i] + ",");
        }
        return realData;
    }

    public static String encode(double[] data) {
        List<Double> dataList = new ArrayList<Double>();
        for (int q = 0; q < data.length; q++) {
            double v = data[q];
            dataList.add(v);
        }
        boolean doublePrecision = true;
        boolean bigEndian = false;
        return Base64Util.encode(doublePrecision, bigEndian, dataList);

    }

    /**
     * habdle name value pairs for added data - NOTE look for todo for required fixes
     * Will throw UnsupportedOperationException
     *
     * @param raw    data to set
     * @param pName
     * @param pValue
     */
    protected static void handleAddedData(final RawPeptideScan raw, final String pName, final String pValue) {

        raw.addAddedValue(pName,pValue);
        String tag = "ms level";
        if (tag.equalsIgnoreCase(pName)) {
            raw.setMsLevel(Integer.parseInt(pValue));
            return;
        }
        tag = "MSn spectrum";   // todo Fix
        if (tag.equalsIgnoreCase(pName)) {
            if ("".equals(pValue))
                return;
            throw new UnsupportedOperationException("Cannot understand " + tag);
        }
        tag = "positive scan";
        if (tag.equalsIgnoreCase(pName)) {
            raw.setPolarity(ScanPolarity.plus);
            return;
        }
        tag = "negative scan";
        if (tag.equalsIgnoreCase(pName)) {
            raw.setPolarity(ScanPolarity.minus);
            return;
        }
        tag = "centroid spectrum";  // todo Fix
        if (tag.equalsIgnoreCase(pName)) {
            if ("".equals(pValue))
                return;
            throw new UnsupportedOperationException("Cannot understand " + tag);
        }
        tag = "base peak m/z";
        if (tag.equalsIgnoreCase(pName)) {
            raw.setBasePeakMz(Double.parseDouble(pValue));
              return;
        }
        tag = "base peak intensity";
        if (tag.equalsIgnoreCase(pName)) {
            raw.setBasePeakIntensity(Double.parseDouble(pValue));
            return;
        }
        tag = "total ion current";
        if (tag.equalsIgnoreCase(pName)) {
            raw.setTotIonCurrent(Double.parseDouble(pValue));
            return;
        }
        tag = "lowest observed m/z";
        if (tag.equalsIgnoreCase(pName)) {
            raw.setLowMz(Double.parseDouble(pValue));
            return;
        }
        tag = "highest observed m/z";
        if (tag.equalsIgnoreCase(pName)) {
            raw.setHighMz(Double.parseDouble(pValue));
            return;
        }
        tag = "no combination";        // todo Fix
        if (tag.equalsIgnoreCase(pName)) {
            if ("".equals(pValue))
                return;
            throw new UnsupportedOperationException("Cannot understand " + tag);
        }
        tag = "filter string";
        if (tag.equalsIgnoreCase(pName)) {
            raw.setFilterLine(pValue);
            return;
        }
        tag = "preset scan configuration";     // todo Fix
        if (tag.equalsIgnoreCase(pName)) {
            if ("".equals(pValue))
                return;
            int value = Integer.parseInt(pValue);
            return;
        }
        tag = "ion injection time";
        if (tag.equalsIgnoreCase(pName)) {
            raw.setIonInjectionTime(Double.parseDouble(pValue));
            return;
        }
        tag = "scan window lower limit";
        if (tag.equalsIgnoreCase(pName)) {
            raw.setScanWindowLowerLimit(Double.parseDouble(pValue));
            return;
        }
        tag = "scan window upper limit";
        if (tag.equalsIgnoreCase(pName)) {
            raw.setScanWindowUpperLimit(Double.parseDouble(pValue));
            return;
        }
        tag = "profile spectrum";   // todo Fix
        if (tag.equalsIgnoreCase(pName)) {
            if ("".equals(pValue))
                return;
            throw new UnsupportedOperationException("Cannot understand " + tag);
        }
        tag = "centroid mass spectrum";   // todo Fix
         if (tag.equalsIgnoreCase(pName)) {
             if ("".equals(pValue))
                 return;
             throw new UnsupportedOperationException("Cannot understand " + tag);
         }
        tag = "MS1 spectrum";   // todo Fix
         if (tag.equalsIgnoreCase(pName)) {
             if ("".equals(pValue))
                 return;
          }
        tag = "lowest m/z value";
        if (tag.equalsIgnoreCase(pName)) {
            raw.setLowMz((Double.parseDouble(pValue)));
            return;
        }
        tag = "highest m/z value";
          if (tag.equalsIgnoreCase(pName)) {
              raw.setHighMz((Double.parseDouble(pValue)));
              return;
          }

        tag = "scan m/z lower limit";
           if (tag.equalsIgnoreCase(pName)) {
               raw.setLowMz((Double.parseDouble(pValue)));
               return;
           }
        tag = "scan m/z upper limit";
            if (tag.equalsIgnoreCase(pName)) {
                raw.setHighMz((Double.parseDouble(pValue)));
                return;
            }

       throw new UnsupportedOperationException("Cannot handle data of type " + pName);
    }


}
