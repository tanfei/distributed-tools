package org.systemsbiology.xtandem.hadoop;

import org.systemsbiology.xtandem.*;

import java.io.*;

/**
 * org.systemsbiology.xtandem.hadoop.SpectrumSaver
 * quick class to write data for Josh Patterson
 * User: steven
 * Date: 5/18/11
 */
public class SpectrumSaver {
    public static final SpectrumSaver[] EMPTY_ARRAY = {};
    public static final int NUMBER_PEAKS = 64;

    private final File m_OutFile;

    public SpectrumSaver(final File pOutFile) {
        m_OutFile = pOutFile;
    }

    public synchronized void handleSpectrum(ISpectrum spec)
    {
        PrintWriter out = buildWriter();
        ISpectrumPeak[] peaks = spec.getPeaks();

        int numberWritten = 0;
        int lastWrittem = 0;
        for ( int i = 0; i < Math.min(peaks.length,NUMBER_PEAKS); i++) {
            ISpectrumPeak peak = peaks[i];
            int value = (int)peak.getMassChargeRatio();
            if(value <= lastWrittem)
                continue;
            out.print(value + " ");
             lastWrittem = value;
             numberWritten++;
        }
        for ( int i = numberWritten; i < NUMBER_PEAKS; i++) {
            out.print("0 ");
          }
        out.println();
        out.close();
    }

    private PrintWriter buildWriter() {
        try {
            boolean append = true;
            FileWriter fw = new FileWriter(m_OutFile,append);
            return new PrintWriter(fw);
        }
        catch (IOException e) {
            throw new RuntimeException(e);

        }
    }
}
