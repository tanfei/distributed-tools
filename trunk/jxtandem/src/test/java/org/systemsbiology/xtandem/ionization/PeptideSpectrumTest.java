package org.systemsbiology.xtandem.ionization;

import org.junit.*;
import org.systemsbiology.xtandem.*;
import org.systemsbiology.xtandem.peptide.*;

import java.util.*;

/**
 * org.systemsbiology.xtandem.ionization.PeptideSpectrumTest
 * User: steven
 * Date: Jan 10, 2011
 */
public class PeptideSpectrumTest {
    public static final PeptideSpectrumTest[] EMPTY_ARRAY = {};

    public static final String SEQUENCE1 = "MQKLINSVQNYAWGSKTALTELYGMENPSSQPMAELWMGAHPKSSSRVQNAA" +
            "GDIVSLRDVIESDKSTLLGEAVAKRFGE" +
            "LPFLFKVLCAAQPLSIQVHPNKHNSEIGFAKENAAGIPMDAAERNYKDPNHKPELVFALTPFLAMNAFREFSEIVSLLQP" +
            "VAGAHPAIAHFLQQPDAERLSELFASLLNMQGEEKSRALAILKSALDSQQGEPWQTIRLISEFYPEDSGLFSPLLLNVVK" +
            "LNPGEAMFLFAETPHAYLQGVALEVMANSDNVLRGLTPKYIDIPELVANVKFEAKPANQLLTQPVKQGAELDFPIPVDDF" +
            "AFSLHDLSDKETTISQQSAAILFCVEGDATLWKGSQQLQLKPGESAFIAANESPVTVKGHGRLARVYNKL";


    public static final String[] FRAGMENTS1 = {
            "MQK",
            "LINSVQNYAWGSK",
            "TALTELYGMENPSSQPMAELWMGAHPK",
            "SSSR",
            "VQNAAGDIVSLR",
            "DVIESDK",
            "STLLGEAVAK",
            "R",
            "FGELPFLFK",
            "VLCAAQPLSIQVHPNK",
            "HNSEIGFAK",
            "ENAAGIPMDAAER",
            "NYK",
            "DPNHKPELVFALTPFLAMNAFR",
            "EFSEIVSLLQPVAGAHPAIAHFLQQPDAER",
            "LSELFASLLNMQGEEK",
            "SR",
            "ALAILK",
            "SALDSQQGEPWQTIR",
            "LISEFYPEDSGLFSPLLLNVVK",
            "LNPGEAMFLFAETPHAYLQGVALEVMANSDNVLR",
            "GLTPK",
            "YIDIPELVANVK",
            "FEAKPANQLLTQPVK",
            "QGAELDFPIPVDDFAFSLHDLSDK",
            "ETTISQQSAAILFCVEGDATLWK",
            "GSQQLQLKPGESAFIAANESPVTVK",
            "GHGR",
            "LAR",
            "VYNK",
            "L",

    };

    // simple test peptide fragment where I have hard coded the
    // results from   http://db.systemsbiology.net:8080/proteomicsToolkit/FragIonServlet.html
    //
    public static final String ONE_SEQUENCE = "GLSDGEWQQVLNVWGK";
    public static final String ID = "ABCS";

    // numbers from
    // http://db.systemsbiology.net:8080/proteomicsToolkit/FragIonServlet.html

    public static double[] MASS_B_AND_Y = {
            58.02933, 1815.90301, //  16
            171.11340, 1758.88155, /// 15
            258.14543, 1645.79749, //  14
            373.17237, 1558.76546, //   13
            430.19383, 1443.73851, //   12
            559.23642, 1386.71705, //   11
            745.31574, 1257.67446, //   10
            873.37431, 1071.59515, //    9
            1001.43289, 943.53657, //    8
            1100.50131, 815.47799, //    7
            1213.58537, 716.40958, //    6
            1327.62830, 603.32551, //    5
            1426.69671, 489.28259, //    4
            1612.77602, 390.21417, //    3
            1669.79749, 204.13486, //    2
            1797.89245, 147.11340 //    1
    };

    public static double[] MASS_A_AND_X = {
            30.03442, -1,//        16
            143.11848, 1784.86081,//   15
            230.15051, 1671.77675,//   14
            345.17745, 1584.74472,//   13
            402.19892, 1469.71778,//   12
            531.24151, 1412.69632,//   11
            717.32082, 1283.65372,//   10
            845.37940, 1097.57441,//    9
            973.43798, 969.51583,//    8
            1072.50639, 841.45726,//    7
            1185.59045, 742.38884,//   6
            1299.63338, 629.30478,//    5
            1398.70180, 515.26185,//    4
            1584.78111, 416.19344,//    3
            1641.80257, 230.11413,//   2
            1769.89753, 173.09266//       };
    };


    public static double[] MASS_C_AND_Z = {
            75.05588, 1798.87646, //    16
            188.13995, 1741.85500, //    15
            275.17197, 1628.77094, //    14
            390.19892, 1541.73891, //    13
            447.22038, 1426.71197, //    12
            576.26297, 1369.69050, //    11
            762.34229, 1240.64791, //    10
            890.40086, 1054.56860, //     9
            1018.45944, 926.51002, //     8
            1117.52785, 798.45144, //     7
            1230.61192, 699.38303, //     6
            1344.65485, 586.29897, //     5
            1443.72326, 472.25604, //     4
            1629.80257, 373.18762, //     3
            1686.82403, 187.10831, //     2
            -1, 130.08685, //     1
    };


    public static double[] MASS_B_AND_Y_CHARGE2 = {
            29.51860, 908.45544, //   16
            86.06063, 879.94471, //   15
            129.57665, 823.40268, //   14
            187.09012, 779.88666, //   13
            215.60085, 722.37319, //   12
            280.12215, 693.86246, //   11
            373.16180, 629.34116, //   10
            437.19109, 536.30151, //    9
            501.22038, 472.27222, //    8
            550.75459, 408.24293, //    7
            607.29662, 358.70872, //    6
            664.31808, 302.16669, //    5
            713.85229, 245.14523, //    4
            806.89195, 195.61102, //    3
            835.40268, 102.57137, //    2
            899.45016, 74.06063, //    1
    };

    public static double[] MASS_B_AND_Y_CHARGE3 = {
            20.01502, 605.97292, //   16
            57.70971, 586.96576, //   15
            86.72039, 549.27108, //   14
            125.06270, 520.26040, //   13
            144.06986, 481.91808, //   12
            187.08405, 462.91093, //   11
            249.11049, 419.89673, //   10
            291.79668, 357.87030, //    9
            334.48288, 315.18410, //    8
            367.50568, 272.49791, //    7
            405.20037, 239.47511, //    6
            443.21468, 201.78042, //    5
            476.23748, 163.76611, //    4
            538.26392, 130.74330, //    3
            557.27108, 68.71687, //    2
            599.96940, 49.70971  //    1
    };


    public static double getAMass(int index) {
        return MASS_A_AND_X[2 * index];
    }

    public static double getBMass(int index) {
        return MASS_B_AND_Y[2 * index];
    }

    public static double getBMassCharge2(int index) {
        return MASS_B_AND_Y_CHARGE2[2 * index];
    }

    public static double getBMassCharge3(int index) {
        return MASS_B_AND_Y_CHARGE3[2 * index];
    }

    public static double getCMass(int index) {
        return MASS_C_AND_Z[2 * index];
    }


    public static double getXMass(int index) {
        return MASS_A_AND_X[2 * index + 3];
    }

    public static int getYIndex(int index) {
        return (2 * index + 3);
    }

    public static double getYMass(int index) {
        return MASS_B_AND_Y[getYIndex(index)];
    }

    public static double getYMassCharge2(int index) {
        return MASS_B_AND_Y_CHARGE2[getYIndex(index)];
    }

    public static double getYMassCharge3(int index) {
        return MASS_B_AND_Y_CHARGE3[2 * index + 3];
    }

    public static double getZMass(int index) {
        return MASS_C_AND_Z[2 * index + 3];
    }


    /**
     */
    @Test
    public void oneSequenceBYTest() {
        XTandemMain main = new XTandemMain(XTandemUtilities.getResourceStream("input3.xml"),"input2.xml");
        main.loadScoringTest();
        main.loadSpectra();
          IProtein test = Protein.getProtein(ID,  null, ONE_SEQUENCE, null);
        SequenceUtilities su = main.getSequenceUtilities();
        double wholeMass = su.getSequenceMass(test);
        double delWhole = 1814.89519 - wholeMass;

        double massPlusH = test.getMass()  + XTandemUtilities.getProtonMass() +  XTandemUtilities.getCleaveCMass() +  XTandemUtilities.getCleaveNMass();

        ITheoreticalSpectrumSet spectrumSet = new TheoreticalSpectrumSet(3,massPlusH,test);

        // save work print the results
        PeptideSpectrum ps = new PeptideSpectrum(spectrumSet,1, IonType.B_ION_TYPES, su);
        PeptideIon[] spectrum = ps.getSpectrum();
//             Assert.assertEquals(spectrum.length,MASS_B_AND_Y.length);
        for (int j = 0; j < spectrum.length; j++) {
            PeptideIon peptideIon = spectrum[j];
            double ionMass = peptideIon.getMass(su, 1);

            if (peptideIon.getTerminalType() == AminoTerminalType.N) {
                double actual = getBMass(j / 2);
                double del = actual - ionMass;
                //  System.out.println(peptideIon.toString() + " " + actual + " " + ionMass + " " + del);
                Assert.assertEquals(ionMass, actual, 0.001);

            }
            else {
                IPolypeptide pp = peptideIon.getPeptide();
                double actual = getYMass(j / 2);
                double del = actual - ionMass;
                //  System.out.println(peptideIon.toString() + " " + actual + " " + ionMass + " " + del);
                Assert.assertEquals(ionMass, actual, 0.001);
            }
        }
    }


    /**
     */
    @Test
    public void oneSequenceCharge2BYTest() {
        XTandemMain main = new XTandemMain(XTandemUtilities.getResourceStream("input3.xml"),"input2.xml");
        main.loadScoringTest();
        main.loadSpectra();
        IProtein test = Protein.getProtein( ID, null, ONE_SEQUENCE, null);
        SequenceUtilities su = main.getSequenceUtilities();
        double wholeMass = su.getSequenceMass(test);
        double delWhole = 1814.89519 - wholeMass;
        double massPlusH = test.getMass()  + XTandemUtilities.getProtonMass() +  XTandemUtilities.getCleaveCMass() +  XTandemUtilities.getCleaveNMass();

          ITheoreticalSpectrumSet spectrumSet = new TheoreticalSpectrumSet(3,massPlusH,test);

        // save work print the results
        PeptideSpectrum ps = new PeptideSpectrum(spectrumSet,1, IonType.B_ION_TYPES, su);
        PeptideIon[] spectrum = ps.getSpectrum();
//             Assert.assertEquals(spectrum.length,MASS_B_AND_Y.length);
        for (int j = 0; j < spectrum.length; j++) {
            PeptideIon peptideIon = spectrum[j];
            double ionMass = peptideIon.getMassChargeRatio(su, 2);

            if (peptideIon.getTerminalType() == AminoTerminalType.N) {
                double actual = getBMassCharge2(j / 2);
                double del = actual - ionMass;
                //   System.out.println(peptideIon.toString() + " " + actual + " " + ionMass + " " + del);
                Assert.assertEquals(ionMass, actual, 0.001);

            }
            else {
                IPolypeptide pp = peptideIon.getPeptide();
                double actual = getYMassCharge2(j / 2);
                double del = actual - ionMass;
                //   System.out.println(peptideIon.toString() + " " + actual + " " + ionMass + " " + del);
                Assert.assertEquals(ionMass, actual, 0.001);
            }
        }
    }

    /**
     */
    @Test
    public void oneSequenceCharge3BYTest() {
        XTandemMain main = new XTandemMain(XTandemUtilities.getResourceStream("input3.xml"),"input2.xml");
        main.loadScoringTest();
        main.loadSpectra();
        IProtein test = Protein.getProtein( ID, null, ONE_SEQUENCE, null);
        SequenceUtilities su = main.getSequenceUtilities();
        double wholeMass = su.getSequenceMass(test);
        double delWhole = 1814.89519 - wholeMass;
        double massPlusH = test.getMass() + XTandemUtilities.getProtonMass() +  XTandemUtilities.getCleaveCMass() +  XTandemUtilities.getCleaveNMass();

          ITheoreticalSpectrumSet spectrumSet = new TheoreticalSpectrumSet(3,massPlusH,test);

        // save work print the results
        PeptideSpectrum ps = new PeptideSpectrum(spectrumSet,1, IonType.B_ION_TYPES, su);
        PeptideIon[] spectrum = ps.getSpectrum();
//             Assert.assertEquals(spectrum.length,MASS_B_AND_Y.length);
        for (int j = 0; j < spectrum.length; j++) {
            PeptideIon peptideIon = spectrum[j];
            double ionMass = peptideIon.getMassChargeRatio(su, 3);

            if (peptideIon.getTerminalType() == AminoTerminalType.N) {
                double actual = getBMassCharge3(j / 2);
                double del = actual - ionMass;
                //   System.out.println(peptideIon.toString() + " " + actual + " " + ionMass + " " + del);
                Assert.assertEquals(ionMass, actual, 0.001);

            }
            else {
                IPolypeptide pp = peptideIon.getPeptide();
                double actual = getYMassCharge3(j / 2);
                double del = actual - ionMass;
                //   System.out.println(peptideIon.toString() + " " + actual + " " + ionMass + " " + del);
                Assert.assertEquals(ionMass, actual, 0.001);
            }
        }
    }

    /**
     */
    @Test
    public void oneSequenceAXTest() {
        XTandemMain main = new XTandemMain(XTandemUtilities.getResourceStream("input3.xml"),"input2.xml");
        main.loadScoringTest();
        main.loadSpectra();
         IProtein test = Protein.getProtein(ID,  null, ONE_SEQUENCE, null);
        SequenceUtilities su = main.getSequenceUtilities();
        double wholeMass = su.getSequenceMass(test);
        double delWhole = 1814.89519 - wholeMass;
        double massPlusH = test.getMass()  + XTandemUtilities.getProtonMass() +  XTandemUtilities.getCleaveCMass() +  XTandemUtilities.getCleaveNMass();

          ITheoreticalSpectrumSet spectrumSet = new TheoreticalSpectrumSet(3,massPlusH,test);

        // save work print the results
        PeptideSpectrum ps = new PeptideSpectrum(spectrumSet,1, IonType.A_ION_TYPES, su);
        PeptideIon[] spectrum = ps.getSpectrum();
//             Assert.assertEquals(spectrum.length,MASS_B_AND_Y.length);
        for (int j = 0; j < spectrum.length; j++) {
            PeptideIon peptideIon = spectrum[j];
            double ionMass = peptideIon.getMass(su, 1);

            if (peptideIon.getTerminalType() == AminoTerminalType.N) {
                double actual = getBMass(j / 2);
                double del = actual - ionMass;
                //       System.out.println(peptideIon.toString() + " " + actual + " " + ionMass + " " + del);
                Assert.assertEquals(ionMass, actual, 0.001);

            }
            else {
                IPolypeptide pp = peptideIon.getPeptide();
                double actual = getYMass(j / 2);
                double del = actual - ionMass;
                //        System.out.println(peptideIon.toString() + " " + actual + " " + ionMass + " " + del);
                Assert.assertEquals(ionMass, actual, 0.001);
            }
        }
    }

    /**
     */
    @Test
    public void oneSequenceCZTest() {
        XTandemMain main = new XTandemMain(XTandemUtilities.getResourceStream("input3.xml"),"input2.xml");
        main.loadScoringTest();
        main.loadSpectra();
         IProtein test = Protein.getProtein(ID,  null, ONE_SEQUENCE, null);
        SequenceUtilities su = main.getSequenceUtilities();
        double wholeMass = su.getSequenceMass(test);
        double delWhole = 1814.89519 - wholeMass;
        double massPlusH = test.getMass()  + XTandemUtilities.getProtonMass() +  XTandemUtilities.getCleaveCMass() +  XTandemUtilities.getCleaveNMass();

          ITheoreticalSpectrumSet spectrumSet = new TheoreticalSpectrumSet(3,massPlusH,test);

        // save work print the results
        PeptideSpectrum ps = new PeptideSpectrum(spectrumSet,1, IonType.C_ION_TYPES, su);
        PeptideIon[] spectrum = ps.getSpectrum();
//             Assert.assertEquals(spectrum.length,MASS_B_AND_Y.length);
        for (int j = 0; j < spectrum.length; j++) {
            PeptideIon peptideIon = spectrum[j];
            double ionMass = peptideIon.getMass(su, 1);

            if (peptideIon.getTerminalType() == AminoTerminalType.N) {
                double actual = getBMass(j / 2);
                double del = actual - ionMass;
                //       System.out.println(peptideIon.toString() + " " + actual + " " + ionMass + " " + del);
                Assert.assertEquals(ionMass, actual, 0.001);

            }
            else {
                IPolypeptide pp = peptideIon.getPeptide();
                double actual = getYMass(j / 2);
                double del = actual - ionMass;
                //      System.out.println(peptideIon.toString() + " " + actual + " " + ionMass + " " + del);
                Assert.assertEquals(ionMass, actual, 0.001);
            }
        }
    }

    /**
     * in this version we use a bitarray for logic
     * test the general [KR]|{P}  loic
     */
    @Test
    public void constructedIonizerTest() {
        IPeptideDigester digester = PeptideBondDigester.getDigester("[KR]|{P}");
        IProtein test = Protein.getProtein( ID, null, SEQUENCE1, null);
        IPolypeptide[] polypeptides = digester.digest(test);
        XTandemMain main = new XTandemMain(XTandemUtilities.getResourceStream("input3.xml"),"input2.xml");
        main.loadScoringTest();
        main.loadSpectra();
        SequenceUtilities su = main.getSequenceUtilities();

        // save work print the results
        for (int i = 0; i < polypeptides.length; i++) {
            IPolypeptide pp = polypeptides[i];
            double massPlusH = pp.getMass()  + XTandemUtilities.getProtonMass() +  XTandemUtilities.getCleaveCMass() +  XTandemUtilities.getCleaveNMass();

              ITheoreticalSpectrumSet spectrumSet = new TheoreticalSpectrumSet(3,massPlusH,test);
              PeptideSpectrum ps = new PeptideSpectrum(spectrumSet, 1,IonType.B_ION_TYPES, su);
            PeptideIon[] spectrum = ps.getSpectrum();
            for (int j = 0; j < spectrum.length; j++) {
                PeptideIon peptideIon = spectrum[j];

            }
        }


    }

    public static final Integer[] BMASSES = {
            100, //.07628   2190.11955   20
            197, //.12905   2091.05113   19
            326, //.17164   1993.99837   18
            427, //.21932   1864.95578   17
            528, //.26700   1763.90810   16
            684, //.36811   1662.86042   15
            797, //.45217   1506.75931   14
            911, //.49510   1393.67525   13
            1074, //.55843   1279.63232   12
            1173, //.62684   1116.56899   11
            1230, //.64830   1017.50058   10
            1359, //.69090    960.47911    9
            1456, //.74366    831.43652    8
            1557, //.79134    734.38376    7
            1614, //.81280    633.33608    6
            1800, //.89211    576.31462    5
            1899, //.96053    390.23530    4
            1986, //.99256    291.16689    3
            2044, //.01402    204.13486    2
            2172, //.10898    147.11340    1
    };

    public static final Integer[] YMASSES = {
            2190, //.11955   20
            2091, //.05113   19
            1993, //.99837   18
            1864, //.95578   17
            1763, //.90810   16
            1662, //.86042   15
            1506, //.75931   14
            1393, //.67525   13
            1279, //.63232   12
            1116, //.56899   11
            1017, //.50058   10
            960, //.47911    9
            831, //.43652    8
            734, //.38376    7
            633, //.33608    6
            576, //.31462    5
            390, //.23530    4
            291, //.16689    3
            204, //.13486    2
            147  //.11340    1
    };

    public static final Set<Integer>  B_MASSES = new HashSet<Integer>(Arrays.asList(BMASSES));
    public static final Set<Integer>  Y_MASSES = new HashSet<Integer>(Arrays.asList(YMASSES));

    private void validateMasses(final SequenceUtilities pSu, final PeptideIon[] pSpectrum) {
        for (int j = 0; j < pSpectrum.length; j++) {
            PeptideIon peptideIon = pSpectrum[j];
            int ionMass = (int) peptideIon.getMass(pSu, 1);
            if (peptideIon.getType() == IonType.B)
                  Assert.assertTrue(B_MASSES.contains(ionMass));
            else
                Assert.assertTrue(Y_MASSES.contains(ionMass));
         }
    }


    /**
     */
    @Test
    public void oneSequenceBYTest2() {
        XTandemMain main = new XTandemMain(XTandemUtilities.getResourceStream("input3.xml"),"input2.xml");
        main.loadScoringTest();
        main.loadSpectra();
        IProtein test = Protein.getProtein( ID, null, "VPETTRINYVGEPTGWVSGK", null);
        SequenceUtilities su = main.getSequenceUtilities();
        double wholeMass = su.getSequenceMass(test);
        double delWhole = 1814.89519 - wholeMass;
        double massPlusH = test.getMass()  + XTandemUtilities.getProtonMass() +  XTandemUtilities.getCleaveCMass() +  XTandemUtilities.getCleaveNMass();

        ITheoreticalSpectrumSet spectrumSet = new TheoreticalSpectrumSet(3,massPlusH,test);
    
        // save work print the results
        PeptideSpectrum ps = new PeptideSpectrum(spectrumSet,1, IonType.B_ION_TYPES, su);
        PeptideIon[] spectrum = ps.getSpectrum();
         //             Assert.assertEquals(spectrum.length,MASS_B_AND_Y.length);
        validateMasses(su, spectrum);
    }

}
