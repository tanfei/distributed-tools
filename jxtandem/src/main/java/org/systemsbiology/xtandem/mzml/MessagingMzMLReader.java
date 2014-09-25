package org.systemsbiology.xtandem.mzml;

import com.lordjoe.utilities.*;
import org.proteios.io.*;
import org.systemsbiology.xml.*;
import org.systemsbiology.xtandem.*;

import javax.xml.stream.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * org.systemsbiology.xtandem.mzml.MessagingMzMLReader
 * User: steven
 * Date: 4/25/11
 */
public class MessagingMzMLReader extends MzMLFileReader {
    public static final MessagingMzMLReader[] EMPTY_ARRAY = {};

    /**
     * NOTE DO not use for large numbers of scans - this is useful for
     * tests and handling fewer scans
     */
    public static RawPeptideScan[] readAllScans(File readFile) {
        try {
            InputStream inp = new FileInputStream(readFile);
            return readAllScans(inp);
        }
        catch (FileNotFoundException e) {
            throw new RuntimeException(e);

        }

    }

    /**
     * NOTE DO not use for large numbers of scans - this is useful for
     * tests and handling fewer scans
     */
    public static RawPeptideScan[] readAllScans(final InputStream pInp) {
        MessagingMzMLReader rdr = new MessagingMzMLReader();
        rdr.setXMLInputStream(pInp);


        ScanAccumulatingSpectrumHandler handler = new ScanAccumulatingSpectrumHandler();
        rdr.addTagEndListener(handler);
        rdr.processXMLFile();
        return handler.getScans();
    }

    private final List<TagEndListener> m_TagEndListeners;

    public MessagingMzMLReader() {
        m_TagEndListeners = new CopyOnWriteArrayList<TagEndListener>();
        SpectrumImpl.setSpectrumClass(ExtendedSpectrumImpl.class);   // build extended spectra
    }



    /**
     * add a change listener
     * final to make sure this is not duplicated at multiple levels
     *
     * @param added non-null change listener
     */
    public final void addTagEndListener(TagEndListener added) {
        if (!m_TagEndListeners.contains(added))
            m_TagEndListeners.add(added);
    }

    /**
     * remove a change listener
     *
     * @param removed non-null change listener
     */
    public final void removeTagEndListener(TagEndListener removed) {
        while (m_TagEndListeners.contains(removed))
            m_TagEndListeners.remove(removed);
    }


    /**
     * notify any state change listeners - probably should
     * be protected but is in the interface to form an event cluster
     *
     * @param oldState
     * @param newState
     * @param commanded
     */
    public void notifyTagEndListeners(String tag, Object lastGenerated) {
        if (m_TagEndListeners.isEmpty())
            return;
        for (TagEndListener listener : m_TagEndListeners) {
            //noinspection unchecked
            listener.onTagEnd(tag, lastGenerated);
        }
    }

    /**
     * Processes an EndElement event.
     *
     * @param parser XMLStreamReader instance.
     * @throws javax.xml.stream.XMLStreamException
     *          If there is an XML Stream related error
     */
    @Override
    protected void processEndElement(final XMLStreamReader parser) throws XMLStreamException {
        String localName = parser.getLocalName();
        if ("spectrum".equalsIgnoreCase(localName)) {
            String id = getCurrentSpectrumId();
            ExtendedSpectrumImpl spectrum = (ExtendedSpectrumImpl) getSpectrum();
            notifyTagEndListeners(localName, spectrum);
         }
        super.processEndElement(parser);    //To change body of overridden methods use File | Settings | File Templates.
    }

    /**
     * Processes a StartElement event.
     *
     * @param parser XMLStreamReader instance.
     * @throws javax.xml.stream.XMLStreamException
     *          If there is an XML Stream related error
     */
    @Override
    protected void processStartElement(final XMLStreamReader parser) throws XMLStreamException {
        String localName = parser.getLocalName();
        if (localName.equals("spectrum")) {
            /*
                * Get attributes
                */
            String attrValue = XMLImportUtil.seekAttribute("id", parser);
            String scanNumber = XMLImportUtil.seekAttribute("scanNumber", parser);
            if (attrValue != null) {
                String currentSpectrumIdStr = attrValue;
//                /*
//                     * Add spectrum id to list if not already in it.
//                     */
//                if (!getSpectrumIdsFound().contains(currentSpectrumIdStr)) {
//                    getSpectrumIdsFound().add(currentSpectrumIdStr);
//                }
//                /*
//                     * Check if desired spectrum id found.
//                     */
                setInTargetSpectrumBlock(true);

                ExtendedSpectrumImpl currentSpectrum = (ExtendedSpectrumImpl) SpectrumImpl.buildSpectrum();  // changed slewis to allow class to change
                currentSpectrum.setId(currentSpectrumIdStr);
                if (scanNumber != null)
                    currentSpectrum.setScanNumber(Integer.parseInt(scanNumber));
                setSpectrum(currentSpectrum);
                setCurrentSpectrumId(currentSpectrumIdStr);

            }
            return;
        }

        super.processStartElement(parser);    //To change body of overridden methods use File | Settings | File Templates.
    }

    /**
     * development code may be test later
     *
     * @param args
     */
    public static void main(String[] args) throws Exception {
        ElapsedTimer et = new ElapsedTimer();
        File input = new File(args[0]);
        Class cls = MzMLFileReader.class;
        InputStream inp = new FileInputStream(input);
        MessagingMzMLReader rdr = new MessagingMzMLReader();
        rdr.setXMLInputStream(inp);


        CountingSpectrumHandler handler = new CountingSpectrumHandler();
        ScanGeneratingSpectrumHandler handler2 = new ScanGeneratingSpectrumHandler();
        rdr.addTagEndListener(handler);
        rdr.addTagEndListener(handler2);
        rdr.processXMLFile();

        int numberScans = handler.getCount();
        XMLUtilities.outputLine("handled " + numberScans + " scans in " + et.formatElapsed());
    }

}
