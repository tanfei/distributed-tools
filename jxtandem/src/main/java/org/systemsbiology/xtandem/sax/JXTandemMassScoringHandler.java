package org.systemsbiology.xtandem.sax;

import org.systemsbiology.sax.*;
import org.systemsbiology.xml.*;
import org.systemsbiology.xtandem.*;
import org.systemsbiology.xtandem.bioml.sax.*;
import org.systemsbiology.xtandem.testing.*;
import org.xml.sax.*;

import java.io.*;
import java.util.*;

/**
 * org.systemsbiology.xtandem.sax.JXTandemMassScoringHandler
 * User: steven
 * Date: 6/22/11
 */
public class JXTandemMassScoringHandler extends AbstractXTandemElementSaxHandler<ScanScoringReport> implements ITopLevelSaxHandler {
    public static final JXTandemMassScoringHandler[] EMPTY_ARRAY = {};


    public static final String TAG = "JXTandem";

       private Map<Integer,List<ITheoreticalIonsScoring>> m_ScorsByMass = new HashMap<Integer,List<ITheoreticalIonsScoring>>();


    public JXTandemMassScoringHandler() {
        super(TAG, (DelegatingSaxHandler) null);
        setElementObject(new ScanScoringReport(ScoringProcesstype.XTandem));
    }

    @Override
    public void startElement(final String uri, final String localName, final String qName, final Attributes attributes) throws SAXException {
        if ("dot_product".equals(qName)) {
            ScanScoringReport elementObject = getElementObject();
            DotProductScoringHandler handler = new DotProductScoringHandler(this, elementObject);
            getHandler().pushCurrentHandler(handler);
            handler.handleAttributes(uri, localName, qName, attributes);
            return;
        }
        if ("mass".equals(qName)) {
            return;
        }
        super.startElement(uri, localName, qName, attributes);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public void addScorsByMass(Integer key,ITheoreticalIonsScoring added) {
        throw new UnsupportedOperationException("Fix This"); // ToDo
       // m_ScorsByMass.put(key,added);
    }




    public ITheoreticalIonsScoring[]  getScorsByMass(Integer key ) {
        return m_ScorsByMass.get(key).toArray(TheoreticalIonsScoring.EMPTY_ARRAY);
    }


    @Override
    public void endElement(final String elx, final String localName, final String el) throws SAXException {
        if ("dot_product".equals(el)) {
            ISaxHandler handler1 = getHandler().popCurrentHandler();
            if (handler1 instanceof DotProductScoringHandler) {
                DotProductScoringHandler handler = (DotProductScoringHandler) handler1;
                ITheoreticalIonsScoring ti = handler.getElementObject();
                if(true)
                throw new UnsupportedOperationException("Fix This"); // ToDo
                // addScorsByMass( ti);
            }
            return;
        }
        if ("mass".equals(el)) {
            return;
        }
        super.endElement(elx, localName, el);    //To change body of overridden methods use File | Settings | File Templates.
    }

    /**
     * finish handling and set up the enclosed object
     * Usually called when the end tag is seen
     */
    @Override
    public void finishProcessing() {

    }

    public static void main(String[] args) throws Exception {
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            JXTandemMassScoringHandler handler = new JXTandemMassScoringHandler();
            InputStream is = new FileInputStream(arg);
            XTandemUtilities.parseFile(is, handler, arg);
            ScanScoringReport report = handler.getElementObject();

            if(!report.equivalent(report))
                throw new IllegalStateException("problem"); // ToDo change
            int totalScores = report.getTotalScoreCount();
            IScanScoring[] scanScoring = report.getScanScoring();
            for (int j = 0; j < scanScoring.length; j++) {
                IScanScoring scoring = scanScoring[j];
                XMLUtilities.outputLine("Scored " + scoring.getId());
                ITheoreticalScoring[] theoreticalScorings = scoring.getScorings();
                for (int k = 0; k < theoreticalScorings.length; k++) {
                    ITheoreticalScoring ts = theoreticalScorings[k];
                    XMLUtilities.outputLine("Scored   " + ts);
                    ITheoreticalIonsScoring[] inos = ts.getIonScorings();
                    for (int l = 0; l < inos.length; l++) {
                        ITheoreticalIonsScoring ino = inos[l];
                        DebugMatchPeak[] scoringMasses = ino.getScoringMasses();
                               for (int m = 0; m < scoringMasses.length; m++) {
                                   DebugMatchPeak scoringMass = scoringMasses[m];
                                   XMLUtilities.outputLine("Scored one " + scoringMass);
                               }

                    }
                   }
            }

        }

    }
}
