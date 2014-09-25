package org.systemsbiology.xtandem.sax;

import org.systemsbiology.sax.*;
import org.systemsbiology.xtandem.*;
import org.systemsbiology.xtandem.bioml.sax.*;
import org.systemsbiology.xtandem.scoring.*;
import org.xml.sax.*;

/**
 * org.systemsbiology.xtandem.sax.SaxMzXMLHandler
 *
 * @author Steve Lewis
 * @date Dec 23, 2010
 */
public class MultiScoreHandler extends AbstractXTandemElementSaxHandler<MultiScorer> implements IMainDataHolder {
    public static MultiScoreHandler[] EMPTY_ARRAY = {};
    public static Class THIS_CLASS = MultiScoreHandler.class;

    public static final String TAG = MultiScorer.TAG;


    public MultiScoreHandler(IMainData main, DelegatingSaxHandler parent) {
        super(TAG, parent);
        setElementObject(new MultiScorer());
     }


    public MultiScoreHandler(IElementHandler parent) {
        super(TAG, parent);
     }


    @Override
    public void handleAttributes(String elx, String localName, String el, Attributes attr)
            throws SAXException {

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
        if (TAG.equals(el)) {
            handleAttributes(uri, localName, el, attributes);
            return;
        }
        if (ScoredScan.TAG.equals(el)) {
            ScanScoreHandler handler = new ScanScoreHandler(this);
            getHandler().pushCurrentHandler(handler);
            handler.handleAttributes(uri, localName, el, attributes);
            return;
        }
        super.startElement(uri, localName, el, attributes);

    }

    @Override
    public void endElement(String elx, String localName, String el) throws SAXException {
        if (TAG.equals(el)) {
            finishProcessing();
            final IElementHandler parent = getParent();
            if (parent != null)
                parent.endElement(elx, localName, el);
            return;
        }
        if (ScoredScan.TAG.equals(el)) {
            ScanScoreHandler handler = (ScanScoreHandler) getHandler().popCurrentHandler();
            ScoredScan scan = handler.getElementObject();
            getElementObject().addAlgorithm(scan);
            return;
        }

         super.endElement(elx, localName, el);
    }


    /**
     * finish handling and set up the enclosed object
     * Usually called when the end tag is seen
     */
    @Override
    public void finishProcessing() {
     }


}
