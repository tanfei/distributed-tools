package org.systemsbiology.peptide;

import org.junit.*;
import org.systemsbiology.xtandem.*;
import org.systemsbiology.xtandem.peptide.*;

import java.util.*;

/**
 * org.systemsbiology.peptide.NakedPeptideMassTest
 * User: steven
 * Date: 1/3/13
 */
public class NakedPeptideMassTest {
    public static final NakedPeptideMassTest[] EMPTY_ARRAY = {};

    //
    //
    public static final String PROTEIN_SEQUENCE =
            "MLGFVGRVAAAPASGALRRLTPSASLPPAQLLLRAAPTAVHPVRDYAAQTSPSPKAGAAT" +
                    "GRIVAVIGAVVDVQFDEGLPPILNALEVQGRETRLVLEVAQHLGESTVRTIAMDGTEGLV" +
                    "RGQKVLDSGAPIKIPVGPETLGRIMNVIGEPIDERGPIKTKQFAPIHAEAPEFMEMSVEQ" +
                    "EILVTGIKVVDLLAPYAKGGKIGLFGGAGVGKTVLIMELINNVAKAHGGYSVFAGVGERT" +
                    "REGNDLYHEMIESGVINLKDATSKVALVYGQMNEPPGARARVALTGLTVAEYFRDQEGQD" +
                    "VLLFIDVSALLGRIPSAVGYQPTLATDMGTMQERITTTKKGSITSVQAIYVPADDLTDPA" +
                    "PATTFAHLDATTVLSRAIAELGIYPAVDPLDSTSRIMDPNIVGSEHYDVARGVQKILQDY" +
                    "KSLQDIIAILGMDELSEEDKLTVSRARKIQRFLSQPFQVAEVFTGHMGKLVPLKETIKGF" +
                    "QQILAGEYDHLPEQAFYMVGPIEEAVAKADKLAEEHSS";

    public static final String PROTEIN_ID = "F8VPV9";

    public static final String FOUND_SEQUENCES_TAB_DELIMITED =
            "[M+H]+\tsequence\tposition in protein\n" +
                    "779.4232\tMLGFVGR\t1-7\n" +
                    "983.5632\tVAAAPASGALR\t8-18\n" +
                    "175.1189\tR\t19-19\n" +
                    "1576.9421\tLTPSASLPPAQLLLR\t20-34\n" +
                    "1018.5792\tAAPTAVHPVR\t35-44\n" +
                    "1164.5531\tDYAAQTSPSPK\t45-55\n" +
                    "603.3209\tAGAATGR\t56-62\n" +
                    "3031.6825\tIVAVIGAVVDVQFDEGLPPILNALEVQGR\t63-91\n" +
                    "405.2092\tETR\t92-94\n" +
                    "1650.9173\tLVLEVAQHLGESTVR\t95-109\n" +
                    "1262.6409\tTIAMDGTEGLVR\t110-121\n" +
                    "332.1928\tGQK\t122-124\n" +
                    "899.5196\tVLDSGAPIK\t125-133\n" +
                    "1038.5942\tIPVGPETLGR\t134-143\n" +
                    "1385.7093\tIMNVIGEPIDER\t144-155\n" +
                    "414.2711\tGPIK\t156-159\n" +
                    "248.1605\tTK\t160-161\n" +
                    "3044.5107\tQFAPIHAEAPEFMEMSVEQEILVTGIK\t162-188\n" +
                    "1088.635\tVVDLLAPYAK\t189-198\n" +
                    "261.1557\tGGK\t199-201\n" +
                    "975.5621\tIGLFGGAGVGK\t202-212\n" +
                    "1457.8396\tTVLIMELINNVAK\t213-225\n" +
                    "1406.6811\tAHGGYSVFAGVGER\t226-239\n" +
                    "276.1666\tTR\t240-241\n" +
                    "2060.9957\tEGNDLYHEMIESGVINLK\t242-259\n" +
                    "521.2565\tDATSK\t260-264\n" +
                    "1601.8104\tVALVYGQMNEPPGAR\t265-279\n" +
                    "246.156\tAR\t280-281\n" +
                    "1439.7892\tVALTGLTVAEYFR\t282-294\n" +
                    "2088.0971\tDQEGQDVLLFIDVSALLGR\t295-313\n" +
                    "2266.0842\tIPSAVGYQPTLATDMGTMQER\t314-334\n" +
                    "563.3399\tITTTK\t335-339\n" +
                    "147.1128\tK\t340-340\n" +
                    "3714.886\tGSITSVQAIYVPADDLTDPAPATTFAHLDATTVLSR\t341-376\n" +
                    "1988.0334\tAIAELGIYPAVDPLDSTSR\t377-395\n" +
                    "1815.8694\tIMDPNIVGSEHYDVAR\t396-411\n" +
                    "431.2612\tGVQK\t412-415\n" +
                    "779.4298\tILQDYK\t416-421\n" +
                    "2119.0474\tSLQDIIAILGMDELSEEDK\t422-440\n" +
                    "575.3511\tLTVSR\t441-445\n" +
                    "246.156\tAR\t446-447\n" +
                    "147.1128\tK\t448-448\n" +
                    "416.2616\tIQR\t449-451\n" +
                    "2023.0105\tFLSQPFQVAEVFTGHMGK\t452-469\n" +
                    "569.4021\tLVPLK\t470-474\n" +
                    "490.2871\tETIK\t475-478\n" +
                    "3350.6401\tGFQQILAGEYDHLPEQAFYMVGPIEEAVAK\t479-508\n" +
                    "333.1768\tADK\t509-511\n" +
                    "772.3471\tLAEEHSS\t512-518\n";

    public static final int NUMBER_DIGESTED_PEPTIDES = 47;   // note there is one duplicate

    public static class IdentifiedPeptide {
        private final double m_DesiredMass;
        private final IPolypeptide m_Peptide;
        private final int m_Start;
        private final int m_End;

        public IdentifiedPeptide(String s) {
            String[] items = s.split("\t");
            m_DesiredMass = Double.parseDouble(items[0]);
            m_Peptide = Polypeptide.fromString(items[1]);
            String[] StartEnd = items[2].split("-");
            m_Start = Integer.parseInt(StartEnd[0]);
            m_End = Integer.parseInt(StartEnd[1]);
        }

        public double getDesiredMass() {
            return m_DesiredMass;
        }

        public IPolypeptide getPeptide() {
            return m_Peptide;
        }

        public int getStart() {
            return m_Start;
        }

        public int getEnd() {
            return m_End;
        }
    }

    public static final Map<IPolypeptide, IdentifiedPeptide> buildPeptidesMap() {
        String[] strings = FOUND_SEQUENCES_TAB_DELIMITED.split("\n");
        Map<IPolypeptide, IdentifiedPeptide> ret = new HashMap<IPolypeptide, IdentifiedPeptide>();
        for (int i = 1; i < strings.length; i++) {
            String string = strings[i];
            IdentifiedPeptide ip = new IdentifiedPeptide(string);
            ret.put(ip.getPeptide(), ip);
        }
        return ret;
    }

    @Test
    public void testGetIdentifiedPeptide() {
        Map<IPolypeptide, IdentifiedPeptide> map = buildPeptidesMap();
        Assert.assertEquals(map.size(), NUMBER_DIGESTED_PEPTIDES);
    }


    @Test
    public void testDigestion() {
        PeptideBondDigester dig = PeptideBondDigester.TRYPSIN;
        Protein p = Protein.getProtein(PROTEIN_ID, "", PROTEIN_SEQUENCE, "");

        Map<IPolypeptide, IdentifiedPeptide> map = buildPeptidesMap();
        Map<IPolypeptide, IdentifiedPeptide> map2 = new HashMap<IPolypeptide, IdentifiedPeptide>();
        IPolypeptide[] digest = dig.digest(p);
        for (int i = 0; i < digest.length; i++) {
            IPolypeptide pp = digest[i];
            map2.put(pp, map.get(pp));
        }
        Assert.assertEquals(digest.length, map2.size());
        for (int i = 0; i < digest.length; i++) {
            IPolypeptide pp = digest[i];
            IdentifiedPeptide testpp = map2.get(pp);

            // steps to calculate mass
    //        MassCalculator calculator = MassCalculator.getDefaultCalculator();
   //         String sequence = pp.getSequence();
   //         double mass = calculator.getSequenceMass(pp);

            double pMass = pp.getMatchingMass();

            double desiredMass = testpp.getDesiredMass();
            double diff = pMass - desiredMass;

            Assert.assertEquals(pMass, desiredMass, 0.01);
        }

    }

    @Test
    public void testSimgleAminoAcidMass() {
        IPolypeptide pp = Polypeptide.fromString("K");
        // steps to calculate mass
        MassCalculator calculator = MassCalculator.getDefaultCalculator();

        double water = calculator.calcMass("HHO");
        double proton = calculator.calcMass("H");
        double waterAndProton = water + proton;

        String sequence = pp.getSequence();
        double mass = calculator.getSequenceMass(pp);


        double pMass = pp.getMass();
        Assert.assertEquals(pMass, mass, 0.01);

        pMass = pp.getMatchingMass();     // this is the real calculation

        double desiredMass = 147.1128;
        double diff = pMass - desiredMass;

        Assert.assertEquals(pMass, desiredMass, 0.01);

    }


}
