package org.systemsbiology.xtandem.taxonomy;

import com.lordjoe.utilities.*;
import org.systemsbiology.xtandem.*;
import org.systemsbiology.xtandem.peptide.*;
import com.lordjoe.utilities.ElapsedTimer;

import java.io.*;
import java.util.*;

/**
 * org.systemsbiology.xtandem.taxonomy.FastaStatistics
 * User: Steve
 * Date: Apr 11, 2011
 */
public class FastaStatistics implements IFastaHandler {
    public static final FastaStatistics[] EMPTY_ARRAY = {};

    public static final int MAX_HANDLED_PROTEIN_LENGTH = 8000;
    public static final int MAX_HANDLED_FRAGMENT_LENGTH = 1000;
    public static final int MAX_HANDLED_FRAGMENT_MASS = 20000;

    private final FastaParser m_Parser = new FastaParser();
    private final CountStatistics m_ProteinLengthStatistics = new CountStatistics(MAX_HANDLED_PROTEIN_LENGTH);
    private final CountStatistics m_FragmentLengthStatistics = new CountStatistics(MAX_HANDLED_FRAGMENT_LENGTH);
    private final CountStatistics m_MonoisotopicMassStatistics = new CountStatistics(MAX_HANDLED_FRAGMENT_MASS);
    private final CountStatistics m_AveragecMassStatistics = new CountStatistics(MAX_HANDLED_FRAGMENT_MASS);
    private final IPeptideDigester m_Digester;

    public FastaStatistics(IPeptideDigester digester) {
        m_Digester = digester;
        m_Parser.addHandler(this);
    }

    public void parse(File inp) {
        try {
            InputStream is = new FileInputStream(inp);
            m_Parser.parseFastaFile(is, inp.getName());
        }
        catch (FileNotFoundException e) {
            throw new RuntimeException(e);

        }
    }

    public void parse(InputStream is) {
        m_Parser.parseFastaFile(is, "");
    }

    public CountStatistics getProteinLengthStatistics() {
        return m_ProteinLengthStatistics;
    }

    public CountStatistics getFragmentLengthStatistics() {
        return m_FragmentLengthStatistics;
    }

    public CountStatistics getMonoisotopicMassStatistics() {
        return m_MonoisotopicMassStatistics;
    }

    public CountStatistics getAveragecMassStatistics() {
        return m_AveragecMassStatistics;
    }

    public IPeptideDigester getDigester() {
        return m_Digester;
    }

    @Override
    public void handleProtein(final String annotation, final String sequence) {
        m_ProteinLengthStatistics.addItem(sequence.length());
        IPeptideDigester digester = getDigester();
        IProtein prot = Protein.getProtein( annotation, annotation, sequence, null);
        IPolypeptide[] pps = digester.digest(prot);
        Arrays.sort(pps);
        //noinspection ForLoopReplaceableByForEach
        for (int i = 0; i < pps.length; i++) {
            IPolypeptide pp = pps[i];
            String psequence = pp.getSequence();

            if (pp.getSequenceLength() < 2)  // don't do length 1
                continue;
            if (pp.getSequenceLength() > 255) // database cannot handle > 255
                continue;
            if (XTandemUtilities.isSequenceAmbiguous(psequence)) //  peptide not well known
                continue;
            m_FragmentLengthStatistics.addItem(pp.getSequenceLength());
            int mass = (int) getAverageMass(pp);
            m_AveragecMassStatistics.addItem(mass);
            mass = (int) getMonoisotopicMass(pp);
            m_MonoisotopicMassStatistics.addItem(mass);
        }

    }

    public double getAverageMass(IPolypeptide pp) {
        MassCalculator calculator = MassCalculator.getCalculator(MassType.average);
        return calculateMass(pp, calculator);

    }

    public double getMonoisotopicMass(IPolypeptide pp) {
        MassCalculator calculator = MassCalculator.getCalculator(MassType.monoisotopic);
        return calculateMass(pp, calculator);

    }

    protected double calculateMass(final IPolypeptide pp, final MassCalculator pCalculator) {
        double ret = pCalculator.getSequenceMass(pp.getSequence()); // s.substring(0,s.length() - 1));
        ret += XTandemUtilities.getCleaveCMass();
        ret += XTandemUtilities.getCleaveNMass();
        ret += XTandemUtilities.getProtonMass();
        return ret;
    }

    protected static void parseFastaFile(final String pArg, final IPeptideDigester pDigester) {
        ElapsedTimer et = new ElapsedTimer();
        FastaStatistics fs = new FastaStatistics(pDigester);
        File item = new File(pArg);
        fs.parse(item);

        et.showElapsed(pArg + " Parsed in ");
        CountStatistics pstat = fs.getProteinLengthStatistics();
        System.out.println("Total Proteins " + pstat.getTotalMembers());

     //   pstat.show(System.out, 25);

        CountStatistics fstat = fs.getFragmentLengthStatistics();
        System.out.println("Total frgaments " + fstat.getTotalMembers());
   //     fstat.show(System.out, 5);

        //noinspection UnusedDeclaration
        CountStatistics astat = fs.getMonoisotopicMassStatistics();
 //       astat.show(System.out, 20);
        //noinspection UnusedDeclaration
        CountStatistics mstat = fs.getAveragecMassStatistics();
 //       mstat.show(System.out, 20);
    }

    public static void main(String[] args) {
        //noinspection ForLoopReplaceableByForEach
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            IPeptideDigester digester = PeptideBondDigester.getDigester("trypsin");
            parseFastaFile(arg, digester);

        }
    }


}
