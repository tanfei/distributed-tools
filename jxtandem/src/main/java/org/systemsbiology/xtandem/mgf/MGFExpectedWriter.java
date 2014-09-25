package org.systemsbiology.xtandem.mgf;

import org.systemsbiology.xtandem.IMainData;
import org.systemsbiology.xtandem.RawPeptideScan;
import org.systemsbiology.xtandem.scoring.IScoredScan;
import org.systemsbiology.xtandem.scoring.ISpectralMatch;

import java.io.PrintWriter;

/**
 * Created with IntelliJ IDEA.
 * User: attilacsordas
 * Date: 22/03/2013
 * Time: 16:19
 * To change this template use File | Settings | File Templates.
 */
public class MGFExpectedWriter extends MGFWriter {

    private final double m_MaximumExpectValue;

    public MGFExpectedWriter(IMainData pApplication, double MaxExpectValue) {
        super(pApplication, 0);

        m_MaximumExpectValue = MaxExpectValue;
    }


    public double get_MaximumExpectValue() {
        return m_MaximumExpectValue;
    }


    @Override
    public void writeMGFElement(IScoredScan scan, PrintWriter out) {
        ISpectralMatch bestMatch = scan.getBestMatch();
        if(bestMatch == null)
            return;
        double testExpect = scan.getExpectedValue();
        if(  testExpect > get_MaximumExpectValue())
            return;
        if (testExpect == 0)
            return;
        RawPeptideScan raw = scan.getRaw();
        raw.appendAsMGF(out);
    }


}


