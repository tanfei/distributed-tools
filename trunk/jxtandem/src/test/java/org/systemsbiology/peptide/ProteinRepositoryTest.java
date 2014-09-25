package org.systemsbiology.peptide;

import org.junit.*;
import org.systemsbiology.xtandem.*;
import org.systemsbiology.xtandem.peptide.*;

import java.io.*;

/**
 * org.systemsbiology.peptide.ProteinRepositoryTest
 * test the protein repository code
 * User: steven
 * Date: 9/16/11
 */
public class ProteinRepositoryTest {
    public static final ProteinRepositoryTest[] EMPTY_ARRAY = {};

    public static final String[] EXPECTED_ANNOTATIONS =
            {
                    "18proteinMixShit_Mannose-6-phosphate isomerase - Escherichia coli.",
                    "18proteinMixShit_CASB_BOVIN BETA CASEIN PRECURSOR - Bos taurus (Bovine).",
                    "18proteinMixShit_CAH2_BOVIN CARBONIC ANHYDRASE II - Bos taurus (Bovine).",
                    "18proteinMixShit_CYC_BOVIN Cytochrome c OS=Bos taurus GN=CYCS PE=1 SV=2",
                    "18proteinMixShit_LACB_BOVIN BETA-LACTOGLOBULIN PRECURSOR (BETA-LG) (ALLERGEN BOS D 5) - Bos taurus (Bovine).",
                    "18proteinMixShit_LCA_BOVIN ALPHA-LACTALBUMIN PRECURSOR (LACTOSE SYNTHASE B PROTEIN) (ALLERGEN BOS D 4) - Bos taurus (Bovine).",
                    "18proteinMixShit_MYG_HORSE MYOGLOBIN - Equus caballus (Horse), and Equus burchelli (Plains zebra) (Equus quagga).",
                    "18proteinMixShit_ALBU_BOVIN SERUM ALBUMIN PRECURSOR (ALLERGEN BOS D 6) - Bos taurus (Bovine).",
                    "18proteinMixShit_OVAL_CHICK OVALBUMIN (PLAKALBUMIN) (ALLERGEN GAL D 2) (GAL D II) - Gallus gallus (Chicken).",
                    "18proteinMixShit_TRFE_BOVIN SEROTRANSFERRIN PRECURSOR (SIDEROPHILIN) (BETA-1-METAL BINDING GLOBULIN) - Bos taurus (Bovine).",
                    "18proteinMixShit_AMY_BACLI Alpha-amylase OS=Bacillus licheniformis GN=amyS PE=1 SV=1",
                    "18proteinMixShit_G3P_RABIT GLYCERALDEHYDE 3-PHOSPHATE DEHYDROGENASE (EC 1.2.1.12) (GAPDH) - Oryctolagus cuniculus (Rabbit).",
                    "18proteinMixShit_PHS2_RABIT GLYCOGEN PHOSPHORYLASE, MUSCLE FORM (EC 2.4.1.1) (MYOPHOSPHORYLASE) - Oryctolagus cuniculus (Rabbit).",
                    "18proteinMixShit_BGAL_ECOLI BETA-GALACTOSIDASE (EC 3.2.1.23) (LACTASE) - Escherichia coli.",
                    "18proteinMixShit_ACTA_BOVIN Actin, aortic smooth muscle OS=Bos taurus GN=ACTA2 PE=1 SV=1",
                    "18proteinMixShit_CATA_BOVIN CATALASE (EC 1.11.1.6) - Bos taurus (Bovine).",
                    "18proteinMixShit_MLE1_RABIT MYOSIN LIGHT CHAIN 1, SKELETAL MUSCLE ISOFORM (MLC1F) (A1 CATALYTIC) (ALKALI) - Oryctolagus cuniculus (Rabbit).",
                    "18proteinMixShit_PPB_ECOLI ALKALINE PHOSPHATASE PRECURSOR (EC 3.1.3.1) (APASE) - Escherichia coli.",

            };

    public static final String TEST_ANNOTTION1 = "tr|F8VPV9|F8VPV9_HUMAN ATP synthase subunit beta OS=Homo sapiens GN=ATP5B PE=3 SV=1";
    public static final String TEST_ANNOTTION2 = ">tr|F8VPV9|F8VPV9_HUMAN ATP synthase subunit beta OS=Homo sapiens GN=ATP5B PE=3 SV=1";
    public static final String TEST_ANNOTTION3 = "  >tr|F8VPV9|F8VPV9_HUMAN ATP synthase subunit beta OS=Homo sapiens GN=ATP5B PE=3 SV=1";
    public static final String TEST_ANNOTTION4 = "";
    public static final String TEST_ANNOTTION5 = "%";
    public static final String TEST_ANNOTTION6 = "&^*(^$&%$#";
    public static final String TEST_ID1 = "TR_F8VPV9_F8VPV9_HUMAN";

    @Test
    public void testProteinId() {
        String id = Protein.annotationToId(TEST_ANNOTTION1);
        Assert.assertEquals(TEST_ID1, id);

        id = Protein.annotationToId(TEST_ANNOTTION2);
        Assert.assertEquals(TEST_ID1, id);

        id = Protein.annotationToId(TEST_ANNOTTION3);
        Assert.assertEquals(TEST_ID1, id);

        // blank should be id + int
        id = Protein.annotationToId(TEST_ANNOTTION4);
        testEmptyId(id);

        id = Protein.annotationToId(TEST_ANNOTTION5);
          testEmptyId(id);

        id = Protein.annotationToId(TEST_ANNOTTION6);
          testEmptyId(id);

    }

    private void testEmptyId(String id) {
        Assert.assertTrue(id.startsWith("Prot"));

        Integer.parseInt(id.replace("Prot",""));
    }

    /**
     * test load from one file
     *
     * @throws Exception
     */
    @Test
    public void test18Proteins() throws Exception {
        ProteinRepository r1 = new ProteinRepository(null);
        InputStream is = XTandemUtilities.getDescribedStream("res://18ProteinMix.fasta");
        r1.populate(is);

        int numberProteins = r1.getNumberProteins();
        Assert.assertEquals(EXPECTED_ANNOTATIONS.length, numberProteins);
        for (int i = 0; i < numberProteins; i++) {
            int realId = i + 1; // 0 not allowed
            String annotation = r1.getAnnotation(realId);
            Assert.assertEquals(EXPECTED_ANNOTATIONS[i], annotation);
            Integer id = r1.getId(EXPECTED_ANNOTATIONS[i]);
            Assert.assertEquals(realId, (int) id);

        }
    }

    public static final String[] ADDED_ANNOTATIONS = {
            "REV1_YDR405W MRP20 SGDID:S000002813, Chr IV from 1277637-1278428, Verified ORF, \"Mitochondrial ribosomal protein of the large subunit\"",
            "REV1_YLR045C STU2 SGDID:S000004035, Chr XII from 237704-235038, reverse complement, Verified ORF, \"Microtubule-associated protein (MAP) of the XMAP215/Dis1 family; regulates microtubule dynamics during spindle orientation and metaphase chromosome alignment; interacts with spindle pole body component Spc72p\"",
            "REV1_YGR097W ASK10 SGDID:S000003329, Chr VII from 678699-682139, Verified ORF, \"Component of the RNA polymerase II holoenzyme, phosphorylated in response to oxidative stress; has a role in destruction of Ssn8p, which relieves repression of stress-response genes\"",
            "REV1_YFL034W YFL034W SGDID:S000001860, Chr VI from 65475-68696, Uncharacterized ORF, \"Putative integral membrane protein that interacts with Rpp0p, which is a component of the ribosomal stalk\"",
            "REV1_YOR093C YOR093C SGDID:S000005619, Chr XV from 502453-497507, reverse complement, Uncharacterized ORF, \"Hypothetical protein\"",
            "YDR356W SPC110 SGDID:S000002764, Chr IV from 1186099-1188933, Verified ORF, \"Inner plaque spindle pole body (SPB) component, ortholog of human kendrin; involved in connecting nuclear microtubules to SPB; interacts with Tub4p-complex and calmodulin; phosphorylated by Mps1p in cell cycle-dependent manner\"",
            "YLR244C MAP1 SGDID:S000004234, Chr XII from 626333-625170, reverse complement, Verified ORF, \"Methionine aminopeptidase, catalyzes the cotranslational removal of N-terminal methionine from nascent polypeptides; function is partially redundant with that of Map2p\"",
            "REV1_YJL076W NET1 SGDID:S000003612, Chr X from 295162-298731, Verified ORF, \"Core subunit of the RENT complex, which is a complex involved in nucleolar silencing and telophase exit; stimulates transcription by RNA polymerase I and regulates nucleolar structure\"",
            "YJL077W-A YJL077W-A SGDID:S000028661, Chr X from 294716-294802, Dubious ORF, \"Identified by gene-trapping, microarray-based expression analysis, and genome-wide homology searching\"",
            "REV1_YJL077W-A YJL077W-A SGDID:S000028661, Chr X from 294716-294802, Dubious ORF, \"Identified by gene-trapping, microarray-based expression analysis, and genome-wide homology searching\"",

    };

    /**
     * testload from two fasta files - if we ever have to do that
     *
     * @throws Exception
     */
    @Test
    public void testMultiLoad() throws Exception {
        ProteinRepository r1 = new ProteinRepository(null);
        InputStream is = XTandemUtilities.getDescribedStream("res://18ProteinMix.fasta");
        r1.populate(is);
        is = XTandemUtilities.getDescribedStream("res://yeast_orfs_pruned.fasta");
        r1.populate(is);

        int numberProteins = r1.getNumberProteins();
        Assert.assertEquals(EXPECTED_ANNOTATIONS.length + ADDED_ANNOTATIONS.length, numberProteins);
        for (int i = 0; i < EXPECTED_ANNOTATIONS.length; i++) {
            int realId = i + 1; // 0 not allowed
            String annotation = r1.getAnnotation(realId);
            Assert.assertEquals(EXPECTED_ANNOTATIONS[i], annotation);
            Integer id = r1.getId(EXPECTED_ANNOTATIONS[i]);
            Assert.assertEquals(realId, (int) id);

        }
        for (int i = EXPECTED_ANNOTATIONS.length; i < r1.getNumberProteins(); i++) {
            int realId = i + 1; // 0 not allowed
            String annotation = r1.getAnnotation(realId);
            //    System.out.println("\"" + annotation + "\"," );
            Assert.assertEquals(ADDED_ANNOTATIONS[i - EXPECTED_ANNOTATIONS.length], annotation);
            Integer id = r1.getId(ADDED_ANNOTATIONS[i - EXPECTED_ANNOTATIONS.length]);
            Assert.assertEquals(realId, (int) id);

        }
    }
}
