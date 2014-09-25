package org.systemsbiology.xtandem.hbase;

import org.systemsbiology.xtandem.*;
import org.systemsbiology.xtandem.hadoop.*;
import org.systemsbiology.xtandem.ionization.*;
import org.systemsbiology.xtandem.peptide.*;
import org.systemsbiology.xtandem.scoring.*;
import org.systemsbiology.xtandem.taxonomy.*;

import java.io.*;

/**
 * org.systemsbiology.xtandem.hbase.IntegerTestBuilder
 * User: steven
 * Date: 5/19/11
 */
public class IntegerTestBuilder {
    public static final IntegerTestBuilder[] EMPTY_ARRAY = {};

    private final XTandemMain m_Main;

    public IntegerTestBuilder() {
        m_Main = new XTandemMain(
                XTandemUtilities.getResourceStream("largeSample/tandem.params"),
                "largeSample/tandem.params");
        m_Main.loadScoringTest();
    }

    public XTandemMain getMain() {
        return m_Main;
    }

    public void generateConditionedSpectra(File outFile, InputStream input) {
        SpectrumSaver ss = new SpectrumSaver(outFile);
        LineNumberReader rdr = new LineNumberReader(new InputStreamReader(input));

        try {
            String scanText = getScanString(rdr);
            while (scanText != null) {
                RawPeptideScan scan = XTandemHadoopUtilities.readScan(scanText,null);
                m_Main.addRawScan(scan);
                String id = scan.getId();
                IScoredScan scoring = m_Main.getScoring(id);
                IScoringAlgorithm scorer = m_Main.getScorer();
                SpectrumCondition sp = m_Main.getSpectrumParameters();
                sp.setbUseMinMass(false);
                sp.setbUseMaxMass(false);
                IMeasuredSpectrum cs = scoring.conditionScan(scorer, sp);
                ss.handleSpectrum(cs);
                scanText = getScanString(rdr);

            }
        }
        finally {
            try {
                rdr.close();
            }
            catch (IOException e) {
                throw new RuntimeException(e);

            }
        }

    }

    public void generateTheoreticalSpectra(File outFile, InputStream input) {
        SpectrumSaver ss = new SpectrumSaver(outFile);
        LineNumberReader rdr = new LineNumberReader(new InputStreamReader(input));

        try {
            String scanText = getScanString(rdr);
            while (scanText != null) {
                RawPeptideScan scan = XTandemHadoopUtilities.readScan(scanText,null);
                m_Main.addRawScan(scan);
                String id = scan.getId();
                IScoredScan scoring = m_Main.getScoring(id);
                IScoringAlgorithm scorer = m_Main.getScorer();
                SpectrumCondition sp = m_Main.getSpectrumParameters();
                sp.setbUseMinMass(false);
                sp.setbUseMaxMass(false);
                IMeasuredSpectrum cs = scoring.conditionScan(scorer, sp);
                ss.handleSpectrum(cs);
                scanText = getScanString(rdr);

            }
        }
        finally {
            try {
                rdr.close();
            }
            catch (IOException e) {
                throw new RuntimeException(e);

            }
        }

    }

    protected String getScanString(final LineNumberReader rdr) {
        try {
            StringBuilder sb = new StringBuilder();
            boolean readingScan = false;
            String line = rdr.readLine();
            while (line != null) {
                if (line.contains("<scan ")) {
                    readingScan = true;
                }
                if (readingScan) {
                    sb.append(line);
                    sb.append("\n");
                }
                if (line.contains("</scan>")) {
                    return sb.toString().trim();
                }
                line = rdr.readLine();
            }
            return null;
        }
        catch (IOException e) {
            throw new RuntimeException(e);

        }
    }

    public static class TheoryHandler implements IFastaHandler {
        private final SpectrumSaver m_Saver;
        private final XTandemMain m_Main;

        public TheoryHandler(final File pOutFile, final XTandemMain pMain) {
            m_Saver = new SpectrumSaver(pOutFile);
            m_Main = pMain;
        }

        @Override
        public void handleProtein(final String annotation, final String sequence) {
            IPeptideDigester digester = m_Main.getDigester();
            Scorer runner = m_Main.getScoreRunner();
            IProtein test = Protein.buildProtein("1234",  annotation, sequence, null);
            final IPolypeptide[] pps = digester.digest(test);
            runner.clearSpectra();
            runner.generateTheoreticalSpectra(pps);
            ITheoreticalSpectrumSet[] allSpectra = runner.getAllSpectra();
            for (int i = 0; i < allSpectra.length; i++) {
                ITheoreticalSpectrumSet tss = allSpectra[i];
                for(int charge = 1; charge < 4; charge++) {
                    ITheoreticalSpectrum ts = tss.getSpectrum(charge);
                    m_Saver.handleSpectrum(ts);
                }

            }
        }
    }

    public static void main(String[] args) throws Exception {
        IntegerTestBuilder ib = new IntegerTestBuilder();
        File measuredOut = new File("MeasuredOut.txt");
        File TheoryOut = new File("TheoryOut.txt");


        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg.toLowerCase().endsWith(".mzxml")) {
                FileInputStream input = new FileInputStream(arg);
                ib.generateConditionedSpectra(measuredOut, input);
            }
            if (arg.toLowerCase().endsWith(".fasta")) {
                FileInputStream input = new FileInputStream(arg);
                FastaParser fp = new FastaParser();
                fp.addHandler(new TheoryHandler(TheoryOut, ib.getMain()));
                fp.parseFastaFile(input, "");
            }
        }
    }
}
