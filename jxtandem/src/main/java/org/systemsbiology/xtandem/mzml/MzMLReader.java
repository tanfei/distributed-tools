package org.systemsbiology.xtandem.mzml;

import com.lordjoe.utilities.*;
import org.proteios.io.*;
import org.systemsbiology.xml.*;
import org.systemsbiology.xtandem.*;

import java.io.*;
import java.util.*;

/**
 * org.systemsbiology.xtandem.mzml.MzMLReader
 * User: steven
 * Date: 4/25/11
 */
public class MzMLReader implements TagEndListener<SpectrumInterface> {
    public static final MzMLReader[] EMPTY_ARRAY = {};


    public static final String FRAGMENT_PREFIX =
            "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n" +
                    "<!-- edited with XMLSPY v5 rel. 4 U (http://www.xmlspy.com) by Twana Johnson (Institute for Systems Biology) -->\n" +
                    "<mzML xmlns=\"http://psi.hupo.org/schema_revision/mzML_0.99.1\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://psi.hupo.org/schema_revision/mzML_0.99.1 mzML0.99.1.xsd\" accession=\"\" id=\"urn:lsid:psidev.info:mzML.instanceDocuments.tiny1\" version=\"0.99.1\">\n" +
                    "<run id=\"Exp01\"      >\n" +
                    " <sourceFileRefList count=\"1\">";

    public static final String FRAGMENT_POSTFIX =
            " </spectrum>\n" +
                    " </spectrumList>\n" +
                    " </run>\n" +
                    "</mzML>";


    /**
     * turn an XML fragment corresponding to s spectrum into a SpectrumInterface
     *
     * @param spectrumFragment !numm fragment
     * @return !null spectrum
     */
    public static RawPeptideScan scanFromFragment(String spectrumFragment) {

        String url = urlFromFragment(spectrumFragment);
        ExtendedSpectrumImpl  spectrum = (ExtendedSpectrumImpl)spectrumFromFragment(spectrumFragment);
        RawPeptideScan ret = MzMlUtilities.buildSpectrum(spectrum);
        if(url != null && ret.getUrl() == null)
              ret.setUrl(url);
        return ret;
    }

    public static final String URL_PARAM = "<userParam  name=\"url\" value=\"";
    protected static String urlFromFragment(final String spectrumFragment) {
        int index = spectrumFragment.indexOf(URL_PARAM);
        if(index == -1)
            return null;
        index += URL_PARAM.length();
        int index2 = spectrumFragment.indexOf("\"",index);
        String url = spectrumFragment.substring(index,index2);
        return url;
    }

    /**
     * turn an XML fragment corresponding to s spectrum into a SpectrumInterface
     *
     * @param spectrumFragment !numm fragment
     * @return !null spectrum
     */
    public static ExtendedSpectrumImpl spectrumFromFragment(String spectrumFragment) {
     //   SpectrumImpl.setSpectrumClass(ExtendedSpectrumImpl.class); // force the generation of out spectrum
     //   SpectrumPrecursor.setSpectrumPrecursorClass(ExtendedSpectrumPrecursor.class); // force the generation of out spectrum

        StringBuilder sb = new StringBuilder(FRAGMENT_PREFIX);
        sb.append(spectrumFragment);
        sb.append(FRAGMENT_POSTFIX);

        String id = XMLUtilities.extractTag("id", spectrumFragment);
        String mslevel = XMLUtilities.maybeExtractTag("mslevel", spectrumFragment);
        if(mslevel == null) {
            // ok may be
            mslevel = XMLUtilities.maybeExtractTag("name=\"ms level\" value=", spectrumFragment);
        }
        int level  = Integer.parseInt(mslevel);

        InputStream inp = XMLUtilities.stringToInputStream(sb.toString());
        MzMLReader rdr = new MzMLReader(inp);
        ExtendedSpectrumImpl  spectrum = (ExtendedSpectrumImpl)rdr.getSpectrumWithId(id);
        spectrum.setMSLevel(level);
        return spectrum;
    }

    private final MessagingMzMLReader m_Reader = new MessagingMzMLReader();
        private List<SpectrumInterface> m_Spectrum = new ArrayList<SpectrumInterface>();


    public MzMLReader(String theFileName) {
        this(FileUtilities.getInputStream(theFileName));
    }


    public MzMLReader(File theFile) {
        this(FileUtilities.getInputStream(theFile));
    }


    public MzMLReader(InputStream is) {
        m_Reader.setXMLInputStream(is);
        m_Reader.addTagEndListener(this);
    }

    public MzMLFileReader getReader() {
        return m_Reader;
    }


    public void addSpectrum(SpectrumInterface added) {
        m_Spectrum.add(added);
    }


    public void removeSpectrum(SpectrumInterface removed) {
        m_Spectrum.remove(removed);
    }

    public SpectrumInterface[] getSpectrums( ) {
        return m_Spectrum.toArray(new SpectrumInterface[0]);
    }

    @Override
    public Class getDesiredClass() {
        return SpectrumInterface.class;
    }

    @Override
    public void onTagEnd(final String tag, final SpectrumInterface lastGenerated) {
      addSpectrum(  lastGenerated);
    }

    public SpectrumInterface[] getSpectra() {
        MzMLFileReader rdr = getReader();
        List<String> ids = rdr.getSpectrumIdList();
//        List<SpectrumInterface> holder = new ArrayList<SpectrumInterface>();
//        for (String id : ids) {
//            SpectrumInterface spectrum = rdr.getSpectrum(id);
//            holder.add(spectrum);
//        }
//
//        SpectrumInterface[] ret = new SpectrumInterface[holder.size()];
//        holder.toArray(ret);
        return getSpectrums( );
    }

    public String[] getSpectraIds() {
        MzMLFileReader rdr = getReader();
        List<String> ids = rdr.getSpectrumIdList();
        return ids.toArray(new String[0]) ;
    }

    public SpectrumInterface getSpectrumWithId(String id) {
        MzMLFileReader rdr = getReader();
        SpectrumInterface spectrum = rdr.getSpectrum(id);
        return spectrum;
    }

    public SpectrumInterface[] getSpectraWithIds(String... id) {
        MzMLFileReader rdr = getReader();
        Iterator<SpectrumInterface> itr = rdr.getSpectrum(Arrays.asList(id));

        List<SpectrumInterface> holder = new ArrayList<SpectrumInterface>();
         while (itr.hasNext()) {
              holder.add(itr.next());
         }

         SpectrumInterface[] ret = new SpectrumInterface[holder.size()];
         holder.toArray(ret);
         return ret;
     }


}
