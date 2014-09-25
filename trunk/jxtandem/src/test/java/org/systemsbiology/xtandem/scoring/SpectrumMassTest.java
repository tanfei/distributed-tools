package org.systemsbiology.xtandem.scoring;

import org.junit.*;
import org.systemsbiology.xtandem.*;
import org.systemsbiology.xtandem.peptide.*;

/**
 * org.systemsbiology.xtandem.scoring.SpectrumMassTest
 *
 * @author Steve Lewis
 * @date Feb 25, 2011
 */
public class SpectrumMassTest {
    public static SpectrumMassTest[] EMPTY_ARRAY = {};
    public static Class THIS_CLASS = SpectrumMassTest.class;

    public static final double[] MASSES = {
            /* handling spectrum 7858 mass */ 2192.25
            , /* handling spectrum 100007858 mass */ 3287.88
            , /* handling spectrum 7860 mass */ 1667.57
            , /* handling spectrum 100007860 mass */ 2500.86
            , /* handling spectrum 7861 mass */ 1584.27
            , /* handling spectrum 100007861 mass */ 2375.91
            , /* handling spectrum 7862 mass */ 2018.23
            , /* handling spectrum 100007862 mass */ 3026.85
            , /* handling spectrum 7863 mass */ 1968.25
            , /* handling spectrum 100007863 mass */ 2951.88
            , /* handling spectrum 7864 mass */ 2252.99
            , /* handling spectrum 100007864 mass */ 3378.99
            , /* handling spectrum 7866 mass */ 1700.41
            , /* handling spectrum 100007866 mass */ 2550.12
            , /* handling spectrum 7867 mass */ 2166.97
            , /* handling spectrum 100007867 mass */ 3249.96
            , /* handling spectrum 7868 mass */ 1460.23
            , /* handling spectrum 100007868 mass */ 2189.85
    };

    public static final String[] IDS = {
            /*  handling spectrum  */ "7858", // mass 2192.25
            /*  handling spectrum  */ "100007858", // mass 3287.88
            /*  handling spectrum  */ "7860", // mass 1667.57
            /*  handling spectrum  */ "100007860", // mass 2500.86
            /*  handling spectrum  */ "7861", // mass 1584.27
            /*  handling spectrum  */ "100007861", // mass 2375.91
            /*  handling spectrum  */ "7862", // mass 2018.23
            /*  handling spectrum  */ "100007862", // mass 3026.85
            /*  handling spectrum  */ "7863", // mass 1968.25
            /*  handling spectrum  */ "100007863", // mass 2951.88
            /*  handling spectrum  */ "7864", // mass 2252.99
            /*  handling spectrum  */ "100007864", // mass 3378.99
            /*  handling spectrum  */ "7866", // mass 1700.41
            /*  handling spectrum  */ "100007866", // mass 2550.12
            /*  handling spectrum  */ "7867", // mass 2166.97
            /*  handling spectrum  */ "100007867", // mass 3249.96
            /*  handling spectrum  */ "7868", // mass 1460.23
            /*  handling spectrum  */ "100007868", // mass 2189.85
    };

    public static final String[] SEQUENCES = {
            /*matched sequence  */ "LLAGVAGGTAATAAANRLV",   //  at mass 1696.97
            /*matched sequence  */ "LLAGVAGGTAATAAANRLVRADE",   //  at mass 2168.18
            /*matched sequence  */ "EQPPDPLPGTRTTTR",   //  at mass 1665.86
            /*matched sequence  */ "ADEQPPDPLPGTRTTTRWR",   //  at mass 2194.1
            /*matched sequence  */ "WRGFDVSYTTAGDPDDPDLVLLHGVHAA",   //  at mass 3024.45

    };

    public static final double[] SEQUENCE_MASSES = {
            /*  matched sequence LLAGVAGGTAATAAANRLV at mass */1696.97
            , /*  matched sequence LLAGVAGGTAATAAANRLVRADE at mass */2168.18
            , /*  matched sequence EQPPDPLPGTRTTTR at mass */1665.86
            , /*  matched sequence ADEQPPDPLPGTRTTTRWR at mass */2194.1
            , /*  matched sequence WRGFDVSYTTAGDPDDPDLVLLHGVHAA at mass */3024.45

    };

    public static final int NUMBER_PEPTIDES = 65;

    /**
     * this test makes sure that the mass a spectrum exposes for scoring is exactly that used by XTandem
     *
     * @throws Exception
     */
    @Test // todo fix
    public void testSpectrumMass() throws Exception {
        XTandemMain main = new XTandemMain(
                XTandemUtilities.getResourceStream("largeSample/tandem.params"),
                "largeSample/tandem.params");
        main.loadScoringTest();
        main.loadSpectra();
        for (int i = 0; i < IDS.length; i++) {
            String id = IDS[i];
            int idValue = Integer.parseInt(id);
            if (idValue < XTandemUtilities.ID_OFFSET) {
                final RawPeptideScan ps = main.getRawScan(id);
                Assert.assertEquals(ps.getId(), id);
                final int charge = ps.getPrecursorCharge();
                final IScanPrecursorMZ mz = ps.getPrecursorMz();
                final double mzr = mz.getMassChargeRatio();
                final double measuredMass = mz.getPrecursorMass();

                double desiredMass = MASSES[i];

            }
            else {   // handle higher charges i.e. charge 3
                id = Integer.toString(idValue - XTandemUtilities.ID_OFFSET);
                final RawPeptideScan ps = main.getRawScan(id);
                Assert.assertEquals(ps.getId(), id);
                int charge = ps.getPrecursorCharge();
                charge = 3;
                final IScanPrecursorMZ mz = ps.getPrecursorMz();
                final double mzr = mz.getMassChargeRatio();
                final double measuredMass = (mzr - XTandemUtilities.getProtonMass()) * charge + XTandemUtilities.getProtonMass();

                double desiredMass = MASSES[i];
                Assert.assertEquals(measuredMass, desiredMass, 0.01);

            }


        }

        final Scorer sa = main.getScoreRunner();
        sa.digest();
        final IPolypeptide[] pps = sa.getPeptides();
        Assert.assertEquals(pps.length,
                NUMBER_PEPTIDES);  // bullshit number but fail until paptide count is right
        for (int i = 0; i < pps.length; i++) {
            IPolypeptide pp = pps[i];
            System.out.println(pp);
            if (false) {
                for (int j = 0; j < SEQUENCES.length; j++) {
                    String sequence = SEQUENCES[j];
                    final String testSequence = pp.getSequence();
                    if (testSequence.equals(sequence)) {
                        double desiredMass = SEQUENCE_MASSES[j];
                        double peptideMass = pp.getMass();
                        Assert.assertEquals(peptideMass, desiredMass, 0.01);

                    }
                    else {
                        if (testSequence.startsWith(sequence)) {
                            double desiredMass = SEQUENCE_MASSES[j];
                            double peptideMass = pp.getMass();
                            double mass = MassCalculator.getDefaultCalculator().getSequenceMass(
                                    sequence);
                            mass += XTandemUtilities.getCleaveCMass();
                            mass += XTandemUtilities.getCleaveNMass();
                            mass += XTandemUtilities.getProtonMass();

                            //   peptideMass = pp.getMass();
                            Assert.assertEquals(mass, desiredMass, 0.01);

                        }

                    }
                }
            }
        }

    }

    public static final String PROTEIN_1 = " ";

    public static final int NUMBER_TRYPTIC_PEPTIDES = 14;

    public static final String[] TRYPTIC_SEQUENCES = {
            "ADEQPPDPLPGTR",
            "ALADAADVR",
            "CFTNR",
            "GFDVSYTTAGDPDDPDLVLLHGVHAAASSR",
            "HAFYQSANVPAGLLDYQHR",
            "ITPLADGR",
            "LAPASFAGGMLDPAVDLVDAVQSVPAPVTLVWGR",
            "LLAGVAGGTAATAAANR",
            "LTVLDDAGAVPHVEHPASFCDALGAALPQLEHH",
            "MTLR",
            "RPAVR",
            "TPVVGTAVFNALVSR",
            "TSHQPNAR",
            "TTTR"
    };

    public static final double[] TRYPTIC_SEQUENCE_MASSES = {
            1392.6753, //            "ADEQPPDPLPGTR",
            901.4737, //             "ALADAADVR",
            640.2871, //             "CFTNR",
            3083.4704,//             "GFDVSYTTAGDPDDPDLVLLHGVHAAASSR",
            2187.0730, //             "HAFYQSANVPAGLLDYQHR",
            842.4730,//             "ITPLADGR",
            3419.8031, //             "LAPASFAGGMLDPAVDLVDAVQSVPAPVTLVWGR",
            1484.8179,//             "LLAGVAGGTAATAAANR",
            3430.6847,    //             "LTVLDDAGAVPHVEHPASFCDALGAALPQLEHH",
            520.2912, //             "MTLR",
            598.3783, //             "RPAVR",
            1530.8638,//             "TPVVGTAVFNALVSR",
            910.4489,//             "TSHQPNAR",
//             "TTTR"

    };


    /**
     * this test makes sure that the mass a spectrum exposes for scoring is exactly that used by XTandem
     *
     * @throws Exception
     */
    @Test
    public void testUnmodifiedMass() throws Exception {
        boolean wasModified = PeptideModification.isHardCodeModifications();
        try {
          //  PeptideModification.setHardCodeModifications(false);
            XTandemMain main = new XTandemMain(
                    XTandemUtilities.getResourceStream("largeSample/tandem.params"),
                    "largeSample/tandem.params");
            IPeptideDigester digester = main.getDigester();
            digester.setNumberMissedCleavages(0);

            main.loadScoringTest();
            main.loadSpectra();
            final Scorer sa = main.getScoreRunner();

            sa.digest();
            for (int i = 0; i < IDS.length; i++) {
                String id = IDS[i];
                int idValue = Integer.parseInt(id);
                if (idValue < XTandemUtilities.ID_OFFSET) {
                    final RawPeptideScan ps = main.getRawScan(id);
                    Assert.assertEquals(ps.getId(), id);
                    final int charge = ps.getPrecursorCharge();
                    final IScanPrecursorMZ mz = ps.getPrecursorMz();
                    final double mzr = mz.getMassChargeRatio();
                    final double measuredMass = mz.getPrecursorMass();

                    double desiredMass = MASSES[i];

                }
                else {   // handle higher charges i.e. charge 3
                    id = Integer.toString(idValue - XTandemUtilities.ID_OFFSET);
                    final RawPeptideScan ps = main.getRawScan(id);
                    Assert.assertEquals(ps.getId(), id);
                    int charge = ps.getPrecursorCharge();
                    charge = 3;
                    final IScanPrecursorMZ mz = ps.getPrecursorMz();
                    final double mzr = mz.getMassChargeRatio();
                    final double measuredMass = (mzr - XTandemUtilities.getProtonMass()) * charge + XTandemUtilities.getProtonMass();

                    double desiredMass = MASSES[i];

                    Assert.assertEquals(measuredMass, desiredMass, 0.01);

                }


            }

            final IPolypeptide[] pps = sa.getPeptides();
            Assert.assertEquals(pps.length, NUMBER_TRYPTIC_PEPTIDES);
            for (int i = 0; i < pps.length; i++) {
                IPolypeptide pp = pps[i];
                System.out.println(pp);
                for (int j = 0; j < TRYPTIC_SEQUENCES.length; j++) {
                    String sequence = TRYPTIC_SEQUENCES[j];
                    final String testSequence = pp.getSequence();
                    if (testSequence.equals(sequence)) {
                        double desiredMass = TRYPTIC_SEQUENCE_MASSES[j];
                        double peptideMass = pp.getMass();
                        System.out.println("desired " + desiredMass + " measured " + peptideMass + " peptide " + sequence);
                       // Assert.assertEquals( desiredMass,peptideMass, 0.01);

                    }
                    else {
                        if (testSequence.startsWith(sequence)) {
                            double desiredMass = TRYPTIC_SEQUENCE_MASSES[j];
                            double peptideMass = pp.getMass();
                            double mass = MassCalculator.getDefaultCalculator().getSequenceMass(
                                    sequence);
                            mass += XTandemUtilities.getCleaveCMass();
                            mass += XTandemUtilities.getCleaveNMass();
                            mass += XTandemUtilities.getProtonMass();

                            //   peptideMass = pp.getMass();
                            Assert.assertEquals(mass, desiredMass, 0.01);

                        }

                    }
                }
            }
        }
        finally {
            PeptideModification.setHardCodeModifications(wasModified);

        }

    }


}
