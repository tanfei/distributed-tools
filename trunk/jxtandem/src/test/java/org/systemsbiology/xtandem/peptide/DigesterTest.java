package org.systemsbiology.xtandem.peptide;

import org.junit.*;

import java.util.*;

/**
 * org.systemsbiology.xtandem.peptide.DigesterTest
 * User: steven
 * Date: Jan 10, 2011
 */
public class DigesterTest {
    public static final DigesterTest[] EMPTY_ARRAY = {};

    public static final String SEQUENCE1 = "MQKLAWGSKTALTELYGQPMAELWMGAHPKSSSRVQNAA" +
            "GDIVSLRDVIESDKSTLLGEAVAKRFGE" +
            "LPFLFKVLCAAQPLSIQVHPNKHNSEIGFAKENAAGIPMDAAERNYKDPNHKPELVFALTPFLAMNAFREFSEIVSLLQP" +
            "VAGAHPAIAHFLQQPDAERLSELFASLLNMQGEEKSRALAILKSALDSQQGEPWQTIRLISEFYPEDSGLFSPLLLNVVK" +
            "LNPGEAMFLFAETPHAYLQGVALEVMANSDNVLRGLTPKYIDIPELVANVKFEAKPANQLLTQPVKQGAELDFPIPVDDF" +
            "AFSLHDLSDKETTISQQSAAILFCVEGDATLWKGSQQLQLKPGESAFIAANESPVTVKGHGRLARVYNKL";
    public static final String[] FRAGMENTS1 = {
            "LAWGSK",
            "TALTELYGQPMAELWMGAHPK",
            "SSSR",
            "VQNAAGDIVSLR",
            "DVIESDK",
            "STLLGEAVAK",
            "FGELPFLFK",
            "VLCAAQPLSIQVHPNK",
            "HNSEIGFAK",
            "ENAAGIPMDAAER",
            "DPNHKPELVFALTPFLAMNAFR",
            "EFSEIVSLLQPVAGAHPAIAHFLQQPDAER",
            "LSELFASLLNMQGEEK",
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
            "VYNK",

    };

    public static final String ID = "S000002813 ";
    public static final String ANNOTATION = " >REV1_YDR405W MRP20 SGDID:S000002813, Chr IV from 1277637-1278428, Verified ORF, \\\"Mitochondrial ribosomal protein of the large subunit\\ ";
    public static final String PROTEIN_SEQUENCE_FOR_SEMITRYPTIC =
            "TINMAKNFRTTQLNMRFRIQPEKKTMEPWIFPEEMEITMRPEMNDPTTSD";

    public static final String PROTEIN_FOR_SEMI2 =
            "VTIKVKTKFMSVTLANKLVNKQKAGFKPTGRSANLLSSHTMTVHINSTVD" +
                    "YETNVWVVCPTMGIKVIKLQLTERNNTLVDTLSFLCTSAGTYIGLLCSFM" //+
            ;

    public static final Protein PROTEIN1 =
            Protein.buildProtein(ID,  ANNOTATION, PROTEIN_SEQUENCE_FOR_SEMITRYPTIC, "foo");

    public static final String SEQUENCE2 = "MQKPLAWGSKTALTELYGQPMAELWMGAHPKSSSRVQNAA" +
            "GDIVSLRDVIESDKSTLLGEAVAKRFGE" +
            "LPFLFKPVLCAAQPLSIQVHPNKHNSEIGFAKENAAGIPMDAAERNYKDPNHKPELVFALTPFLAMNAFREFSEIVSLLQP" +
            "VAGAHPAIAHFLQQPDAERLSELFASLLNMQGEEKSRALAILKSALDSQQGEPWQTIRPLISEFYPEDSGLFSPLLLNVVK" +
            "LNPGEAMFLFAETPHAYLQGVALEVMANSDNVLRGLTPKYIDIPELVANVKFEAKPANQLLTQPVKQGAELDFPIPVDDF" +
            "AFSLHDLSDKETTISQQSAAILFCVEGDATLWKGSQQLQLKPGESAFIAANESPVTVKGHGRLARPVYNKL";


    public static final Protein PROTEIN2 =
            Protein.buildProtein(ID, ANNOTATION, PROTEIN_FOR_SEMI2, "foo");

    public static final String[] FRAGMENTS2 = {
            "MQKPLAWGSK",
            "TALTELYGQPMAELWMGAHPK",
            "SSSR",
            "VQNAAGDIVSLR",
            "DVIESDK",
            "STLLGEAVAK",
            "FGELPFLFKPVLCAAQPLSIQVHPNK",
            "HNSEIGFAK",
            "ENAAGIPMDAAER",
            "DPNHKPELVFALTPFLAMNAFR",
            "EFSEIVSLLQPVAGAHPAIAHFLQQPDAER",
            "LSELFASLLNMQGEEK",
            "ALAILK",
            "SALDSQQGEPWQTIRPLISEFYPEDSGLFSPLLLNVVK",
            "LNPGEAMFLFAETPHAYLQGVALEVMANSDNVLR",
            "GLTPK",
            "YIDIPELVANVK",
            "FEAKPANQLLTQPVK",
            "QGAELDFPIPVDDFAFSLHDLSDK",
            "ETTISQQSAAILFCVEGDATLWK",
            "GSQQLQLKPGESAFIAANESPVTVK",
            "GHGR",
            "LARPVYNK",

    };

    public static final String PROTEIN_FOR_TWO_MISSED
            = "MTLRRLLAGVAGGTAATAAANRLVRADEQPPDPLPGTRTTTRWRGFDVSYTTAGDPDDPDLVLLHGVHAAASSRAFAGVVDTLA" +
            "EDYHVLAPDLPGFGHTDRPSIAYTSALYEAFVASFIGDVADDPAVVASSLTGAWAAMAAADTSVSALALVCPIADTGQRRPAVRRL" +
            "LRTPVVGTAVFNALVSRRGLRCFTNRHAFYQSANVPAGLLDYQHRTSHQPNARLAPASFAGGMLDPAVDLVDAVQSVPAPVTLVWG" +
            "REARITPLADGRALADAADVRLTVLDDAGAVPHVEHPASFCDALGAALPQLEHH";

    // missed Cleavages
    public static final String[] SEQUENCES_WITH_TWO_MISSED = {
            "ADEQPPDPLPGTR",
            "ADEQPPDPLPGTRTTTR",
            "ADEQPPDPLPGTRTTTRWR",
            "ALADAADVR",
            "CFTNR",
            "CFTNRHAFYQSANVPAGLLDYQHR",
            "CFTNRHAFYQSANVPAGLLDYQHRTSHQPNAR",
            "EARITPLADGR",
            "EARITPLADGRALADAADVR",
            "GFDVSYTTAGDPDDPDLVLLHGVHAAASSR",
            "GLRCFTNR",
            "GLRCFTNRHAFYQSANVPAGLLDYQHR",
            "HAFYQSANVPAGLLDYQHR",
            "HAFYQSANVPAGLLDYQHRTSHQPNAR",
            "ITPLADGR",
            "ITPLADGRALADAADVR",
            "LAPASFAGGMLDPAVDLVDAVQSVPAPVTLVWGR",
            "LAPASFAGGMLDPAVDLVDAVQSVPAPVTLVWGREAR",
            "LLAGVAGGTAATAAANR",
            "LLAGVAGGTAATAAANRLVR",
            "LLAGVAGGTAATAAANRLVRADEQPPDPLPGTR",
            "LLRTPVVGTAVFNALVSR",
            "LLRTPVVGTAVFNALVSRR",
            "LTVLDDAGAVPHVEHPASFCDALGAALPQLEHH",
            "LVRADEQPPDPLPGTR",
            "LVRADEQPPDPLPGTRTTTR",
            "MTLR",
            "MTLRR",
            "MTLRRLLAGVAGGTAATAAANR",
            "RGLR",
            "RGLRCFTNR",
            "RLLAGVAGGTAATAAANR",
            "RLLAGVAGGTAATAAANRLVR",
            "RLLR",
            "RLLRTPVVGTAVFNALVSR",
            "RPAVR",
            "RPAVRR",
            "RPAVRRLLR",
            "TPVVGTAVFNALVSR",
            "TPVVGTAVFNALVSRR",
            "TPVVGTAVFNALVSRRGLR",
            "TSHQPNAR",
            "TTTR",
            "TTTRWR",
            "TTTRWRGFDVSYTTAGDPDDPDLVLLHGVHAAASSR",
            "WRGFDVSYTTAGDPDDPDLVLLHGVHAAASSR",

    };

    public static final String[] SEMI_TRYPTIC_FRAGMENTS = {
            "AKNFR",
            "AKNFRTTQLNMR",
            "DPTTSD",
            "EEMEITMRPEMNDPTTSD",
            "EITMRPEMNDPTTSD",
            "EKKTMEPWIFPEEMEITMRPEMNDPTTSD",
            "EMEITMRPEMNDPTTSD",
            "EMNDPTTSD",
            "EPWIFPEEMEITMRPEMNDPTTSD",
            "FPEEMEITMRPEMNDPTTSD",
            "FRIQ",
            "FRIQP",
            "FRIQPE",
            "FRIQPEK",
            "FRIQPEKK",
            "FRTTQLNMR",
            "FRTTQLNMRFR",
            "IFPEEMEITMRPEMNDPTTSD",
            "INMAK",
            "INMAKNFR",
            "INMAKNFRTTQLNMR",
            "IQPE",
            "IQPEK",
            "IQPEKK",
            "IQPEKKT",
            "IQPEKKTM",
            "IQPEKKTME",
            "IQPEKKTMEP",
            "IQPEKKTMEPW",
            "IQPEKKTMEPWI",
            "IQPEKKTMEPWIF",
            "IQPEKKTMEPWIFP",
            "IQPEKKTMEPWIFPE",
            "IQPEKKTMEPWIFPEE",
            "IQPEKKTMEPWIFPEEM",
            "IQPEKKTMEPWIFPEEME",
            "IQPEKKTMEPWIFPEEMEI",
            "IQPEKKTMEPWIFPEEMEIT",
            "IQPEKKTMEPWIFPEEMEITM",
            "IQPEKKTMEPWIFPEEMEITMR",
            "IQPEKKTMEPWIFPEEMEITMRP",
            "IQPEKKTMEPWIFPEEMEITMRPE",
            "IQPEKKTMEPWIFPEEMEITMRPEM",
            "IQPEKKTMEPWIFPEEMEITMRPEMN",
            "IQPEKKTMEPWIFPEEMEITMRPEMND",
            "IQPEKKTMEPWIFPEEMEITMRPEMNDP",
            "IQPEKKTMEPWIFPEEMEITMRPEMNDPT",
            "IQPEKKTMEPWIFPEEMEITMRPEMNDPTT",
            "IQPEKKTMEPWIFPEEMEITMRPEMNDPTTS",
            "IQPEKKTMEPWIFPEEMEITMRPEMNDPTTSD",
            "ITMRPEMNDPTTSD",
            "KKTMEPWIFPEEMEITMRPEMNDPTTSD",
            "KNFR",
            "KNFRTTQLNMR",
            "KTME",
            "KTMEP",
            "KTMEPW",
            "KTMEPWI",
            "KTMEPWIF",
            "KTMEPWIFP",
            "KTMEPWIFPE",
            "KTMEPWIFPEE",
            "KTMEPWIFPEEM",
            "KTMEPWIFPEEME",
            "KTMEPWIFPEEMEI",
            "KTMEPWIFPEEMEIT",
            "KTMEPWIFPEEMEITM",
            "KTMEPWIFPEEMEITMR",
            "KTMEPWIFPEEMEITMRP",
            "KTMEPWIFPEEMEITMRPE",
            "KTMEPWIFPEEMEITMRPEM",
            "KTMEPWIFPEEMEITMRPEMN",
            "KTMEPWIFPEEMEITMRPEMND",
            "KTMEPWIFPEEMEITMRPEMNDP",
            "KTMEPWIFPEEMEITMRPEMNDPT",
            "KTMEPWIFPEEMEITMRPEMNDPTT",
            "KTMEPWIFPEEMEITMRPEMNDPTTS",
            "KTMEPWIFPEEMEITMRPEMNDPTTSD",
            "LNMR",
            "LNMRFR",
            "LNMRFRIQPEK",
            "MAKNFR",
            "MAKNFRTTQLNMR",
            "MEITMRPEMNDPTTSD",
            "MEPWIFPEEMEITMRPEMNDPTTSD",
            "MNDPTTSD",
            "MRFR",
            "MRFRIQPEK",
            "MRPEMNDPTTSD",
            "NDPTTSD",
            "NFRT",
            "NFRTT",
            "NFRTTQ",
            "NFRTTQL",
            "NFRTTQLN",
            "NFRTTQLNM",
            "NFRTTQLNMR",
            "NFRTTQLNMRF",
            "NFRTTQLNMRFR",
            "NMAK",
            "NMAKNFR",
            "NMAKNFRTTQLNMR",
            "NMRFR",
            "NMRFRIQPEK",
            "PEEMEITMRPEMNDPTTSD",
            "PEKK",
            "PEKKTMEPWIFPEEMEITMRPEMNDPTTSD",
            "PEMNDPTTSD",
            "PTTSD",
            "PWIFPEEMEITMRPEMNDPTTSD",
            "QLNMR",
            "QLNMRFR",
            "QLNMRFRIQPEK",
            "QPEK",
            "QPEKK",
            "QPEKKTMEPWIFPEEMEITMRPEMNDPTTSD",
            "RFRIQPEK",
            "RIQPEK",
            "RIQPEKK",
            "RPEMNDPTTSD",
            "RTTQLNMR",
            "RTTQLNMRFR",
            "TINM",
            "TINMA",
            "TINMAK",
            "TINMAKN",
            "TINMAKNF",
            "TINMAKNFR",
            "TINMAKNFRT",
            "TINMAKNFRTT",
            "TINMAKNFRTTQ",
            "TINMAKNFRTTQL",
            "TINMAKNFRTTQLN",
            "TINMAKNFRTTQLNM",
            "TINMAKNFRTTQLNMR",
            "TMEP",
            "TMEPW",
            "TMEPWI",
            "TMEPWIF",
            "TMEPWIFP",
            "TMEPWIFPE",
            "TMEPWIFPEE",
            "TMEPWIFPEEM",
            "TMEPWIFPEEME",
            "TMEPWIFPEEMEI",
            "TMEPWIFPEEMEIT",
            "TMEPWIFPEEMEITM",
            "TMEPWIFPEEMEITMR",
            "TMEPWIFPEEMEITMRP",
            "TMEPWIFPEEMEITMRPE",
            "TMEPWIFPEEMEITMRPEM",
            "TMEPWIFPEEMEITMRPEMN",
            "TMEPWIFPEEMEITMRPEMND",
            "TMEPWIFPEEMEITMRPEMNDP",
            "TMEPWIFPEEMEITMRPEMNDPT",
            "TMEPWIFPEEMEITMRPEMNDPTT",
            "TMEPWIFPEEMEITMRPEMNDPTTS",
            "TMEPWIFPEEMEITMRPEMNDPTTSD",
            "TMRPEMNDPTTSD",
            "TQLNMR",
            "TQLNMRFR",
            "TQLNMRFRIQPEK",
            "TTQL",
            "TTQLN",
            "TTQLNM",
            "TTQLNMR",
            "TTQLNMRF",
            "TTQLNMRFR",
            "TTQLNMRFRI",
            "TTQLNMRFRIQ",
            "TTQLNMRFRIQP",
            "TTQLNMRFRIQPE",
            "TTQLNMRFRIQPEK",
            "TTSD",
            "WIFPEEMEITMRPEMNDPTTSD",
    };

    @Test
    public void constructedDigesterTest() {
        IPeptideDigester digester = PeptideBondDigester.getDigester("[KR]|{P}");
        IProtein test = Protein.getProtein(ID,  null, SEQUENCE1, null);
        IPolypeptide[] polypeptides = digester.digest(test);
        polypeptides = PeptideBondDigester.filterIgnoredPeptides(polypeptides);

        // save work print the results
        for (int i = 0; i < polypeptides.length; i++) {
            IPolypeptide polypeptide = polypeptides[i];
            if (!FRAGMENTS1[i].equals(polypeptide.getSequence()))
                System.out.println("\"" + polypeptide.getSequence() + "\" not  \"" + FRAGMENTS1[i] + "\",");
        }

        Assert.assertEquals(polypeptides.length, FRAGMENTS1.length);
        for (int i = 0; i < polypeptides.length; i++) {
            IPolypeptide polypeptide = polypeptides[i];
            Assert.assertEquals(FRAGMENTS1[i], polypeptide.getSequence());
        }
    }

    /**
     * in this version we use a bitarray for logic
     * test the general [KR]|{P}  loic
     */
    @Test
    public void constructedDigesterTestWithExclude() {
        IPeptideDigester digester = PeptideBondDigester.getDigester("[KR]|{P}");
        IProtein test = Protein.getProtein(ID, null, SEQUENCE2, null);
        IPolypeptide[] polypeptides = digester.digest(test);

//        for (int i = 0; i < polypeptides.length; i++) {
//            IPolypeptide polypeptide = polypeptides[i];
//            System.out.println("\"" + polypeptide.getSequence() + "\",");
//        }
//
        Assert.assertEquals(polypeptides.length, FRAGMENTS2.length);
        for (int i = 0; i < polypeptides.length; i++) {
            IPolypeptide polypeptide = polypeptides[i];
            Assert.assertEquals(FRAGMENTS2[i], polypeptide.getSequence());
        }
    }

    /**
     * Test digestion on a protein without excslusion used
     */

    @Test
    public void simpleDigesterTest() {
        IPeptideDigester digester = PeptideBondDigester.getDigester("Trypsin");
        IProtein test = Protein.getProtein( ID, null, SEQUENCE1, null);
        int missed = digester.getNumberMissedCleavages();

        IPolypeptide[] polypeptides = digester.digest(test);
        Arrays.sort(polypeptides);

        String[] answer = FRAGMENTS1;
        Arrays.sort(answer);
//        // save work print the results
//        for (int i = 0; i < polypeptides.length; i++) {
//            IPolypeptide polypeptide = polypeptides[i];
//            System.out.println("\"" + polypeptide.getSequence() + "\",");
//        }

        Assert.assertEquals(polypeptides.length, FRAGMENTS1.length);
        for (int i = 0; i < polypeptides.length; i++) {
            IPolypeptide polypeptide = polypeptides[i];
            Assert.assertEquals(FRAGMENTS1[i], polypeptide.getSequence());
            IProteinPosition[] proteinPositions = polypeptide.getProteinPositions();
            Assert.assertEquals(1,proteinPositions.length);
            Assert.assertEquals(polypeptide,proteinPositions[0].getPeptide());
            Assert.assertEquals(test,proteinPositions[0].getProtein());

        }
    }

    /**
     * Test digestion on a protein with  excslusion used
     */
    @Test
    public void digesterTestWithExclude() {
        IPeptideDigester digester = PeptideBondDigester.getDigester("Trypsin");
        IProtein test = Protein.getProtein( ID, null, SEQUENCE2, null);
        IPolypeptide[] polypeptides = digester.digest(test);
        String[] sequences = new String[polypeptides.length];
        for (int i = 0; i < polypeptides.length; i++) {
            sequences[i] = polypeptides[i].getSequence();

        }
        Arrays.sort(sequences);
//         for (int i = 0; i < polypeptides.length; i++) {
//             IPolypeptide polypeptide = polypeptides[i];
//             System.out.println("\"" + polypeptide.getSequence() + "\",");
//         }

        Assert.assertEquals(polypeptides.length, FRAGMENTS2.length);
        for (int i = 0; i < polypeptides.length; i++) {
            IPolypeptide polypeptide = polypeptides[i];
            Assert.assertEquals(FRAGMENTS2[i], polypeptide.getSequence());
        }
    }

    /**
     * Test for proper output with two missed cleavages - here we assume fragments are
     * distinct if there is a different start point or a different sequence
     */
    @Test
    public void constructedDigesterTestWithMixedCleavages() {
        PeptideBondDigester digester = (PeptideBondDigester) PeptideBondDigester.getDigester("[KR]|{P}");
        digester.setNumberMissedCleavages(2);
        IProtein test = Protein.getProtein( ID, null, PROTEIN_FOR_TWO_MISSED, null);
        IPolypeptide[] polypeptides = digester.digest(test);


//        for (int i = 0; i < polypeptides.length; i++) {
//            IPolypeptide polypeptide = polypeptides[i];
//            System.out.println("\"" + polypeptide.getSequence() + "\",");
//        }

        int length = SEQUENCES_WITH_TWO_MISSED.length;
        for (int i = 0; i < Math.min(polypeptides.length, length); i++) {
            IPolypeptide polypeptide = polypeptides[i];
            Assert.assertEquals(SEQUENCES_WITH_TWO_MISSED[i], polypeptide.getSequence());
        }
        Assert.assertEquals(polypeptides.length, length);
    }


    /**
     * in this version we use a bitarray for logic
     * test the general [KR]|{P}  loic
     */
    @Test
    public void testWithSemiTryptic() {
        IPeptideDigester digester = PeptideBondDigester.getDigester("[KR]|{P}");
        digester.setNumberMissedCleavages(2);
        digester.setSemiTryptic(true);
        IProtein test = PROTEIN1;
        IPolypeptide[] polypeptides = digester.addSemiCleavages(test);

//        for (int i = 0; i < polypeptides.length; i++) {
//            IPolypeptide polypeptide = polypeptides[i];
//            System.out.println("\"" + polypeptide.getSequence() + "\",");
//        }

        //      Assert.assertEquals(polypeptides.length, SEMI_TRYPTIC_FRAGMENTS.length);
        for (int i = 0; i < polypeptides.length; i++) {
            IPolypeptide polypeptide = polypeptides[i];
            if (!SEMI_TRYPTIC_FRAGMENTS[i].equals(polypeptide.getSequence()))
                Assert.assertEquals(SEMI_TRYPTIC_FRAGMENTS[i], polypeptide.getSequence());
        }
    }

    /**
     * in this version we use a bitarray for logic
     * test the general [KR]|{P}  loic
     */
    @Test
    public void testWithSemiTryptic2() {
        IPeptideDigester digester = PeptideBondDigester.getDigester("[KR]|{P}");
        digester.setNumberMissedCleavages(2);
        digester.setSemiTryptic(true);
        IProtein test = PROTEIN2;
        IPolypeptide[] polypeptides = digester.addSemiCleavages(test);
        Arrays.sort(polypeptides);
        List<String> holder = new ArrayList<String>();
        for (int i = 0; i < polypeptides.length; i++) {
            IPolypeptide polypeptide = polypeptides[i];
            holder.add(polypeptide.getSequence());
        }


//        for (int i = 0; i < polypeptides.length; i++) {
//            IPolypeptide polypeptide = polypeptides[i];
//            System.out.println("\"" + polypeptide.getSequence() + "\",");
//        }

        Assert.assertTrue(holder.contains("QKAGFKPTGRSANL"));
    }

    /*
        This sample is hand build to test
     */

    public static final String SEQUENCE3 = "MQKPLAWGSKTALTELYGQRPMAELWMGAHPKSSSRVQNAA";


    public static final Protein PROTEIN3 =
            Protein.buildProtein( ID, ANNOTATION, SEQUENCE3, "foo");

    public static final String[] MISSED_CLEAVAGE_3 = {
            // tryptic
            "MQKPLAWGSK",
            "TALTELYGQPMAELWMGAHPK",
            "SSSR",
            "VQNAA",
            // missed 1
            "MQKPLAWGSK" + "TALTELYGQPMAELWMGAHPK",
            "TALTELYGQPMAELWMGAHPK" + "SSSR",
            "SSSR" + "VQNAA",

            // missed 2
            "MQKPLAWGSK" + "TALTELYGQPMAELWMGAHPK" + "SSSR",
            "TALTELYGQPMAELWMGAHPK" + "SSSR" + "VQNAA",


    };

    public static final String[] SEMI_FRAGMENTS3 = {
            "AELWMGAHPK",
            "AELWMGAHPKSSSR",
            "AELWMGAHPKSSSRVQNAA",
            "AHPK",
            "AHPKSSSR",
            "AHPKSSSRVQNAA",
            "ALTELYGQRPMAELWMGAHPK",
            "ALTELYGQRPMAELWMGAHPKSSSR",
            "ALTELYGQRPMAELWMGAHPKSSSRVQNAA",
            "AWGSK",
            "AWGSKTALTELYGQRPMAELWMGAHPK",
            "AWGSKTALTELYGQRPMAELWMGAHPKSSSR",
            "ELWMGAHPK",
            "ELWMGAHPKSSSR",
            "ELWMGAHPKSSSRVQNAA",
            "ELYGQRPMAELWMGAHPK",
            "ELYGQRPMAELWMGAHPKSSSR",
            "ELYGQRPMAELWMGAHPKSSSRVQNAA",
            "GAHPK",
            "GAHPKSSSR",
            "GAHPKSSSRVQNAA",
            "GQRPMAELWMGAHPK",
            "GQRPMAELWMGAHPKSSSR",
            "GQRPMAELWMGAHPKSSSRVQNAA",
            "GSKTALTELYGQRPMAELWMGAHPK",
            "GSKTALTELYGQRPMAELWMGAHPKSSSR",
            "HPKSSSR",
            "HPKSSSRVQNAA",
            "KPLAWGSK",
            "KPLAWGSKTALTELYGQRPMAELWMGAHPK",
            "KPLAWGSKTALTELYGQRPMAELWMGAHPKSSSR",
            "KSSSR",
            "KSSSRVQNAA",
            "KTALTELYGQRPMAELWMGAHPK",
            "KTALTELYGQRPMAELWMGAHPKSSSR",
            "LAWGSK",
            "LAWGSKTALTELYGQRPMAELWMGAHPK",
            "LAWGSKTALTELYGQRPMAELWMGAHPKSSSR",
            "LTELYGQRPMAELWMGAHPK",
            "LTELYGQRPMAELWMGAHPKSSSR",
            "LTELYGQRPMAELWMGAHPKSSSRVQNAA",
            "LWMGAHPK",
            "LWMGAHPKSSSR",
            "LWMGAHPKSSSRVQNAA",
            "LYGQRPMAELWMGAHPK",
            "LYGQRPMAELWMGAHPKSSSR",
            "LYGQRPMAELWMGAHPKSSSRVQNAA",
            "MAELWMGAHPK",
            "MAELWMGAHPKSSSR",
            "MAELWMGAHPKSSSRVQNAA",
            "MGAHPK",
            "MGAHPKSSSR",
            "MGAHPKSSSRVQNAA",
            "MQKP",
            "MQKPL",
            "MQKPLA",
            "MQKPLAW",
            "MQKPLAWG",
            "MQKPLAWGS",
            "MQKPLAWGSK",
            "MQKPLAWGSKT",
            "MQKPLAWGSKTA",
            "MQKPLAWGSKTAL",
            "MQKPLAWGSKTALT",
            "MQKPLAWGSKTALTE",
            "MQKPLAWGSKTALTEL",
            "MQKPLAWGSKTALTELY",
            "MQKPLAWGSKTALTELYG",
            "MQKPLAWGSKTALTELYGQ",
            "MQKPLAWGSKTALTELYGQR",
            "MQKPLAWGSKTALTELYGQRP",
            "MQKPLAWGSKTALTELYGQRPM",
            "MQKPLAWGSKTALTELYGQRPMA",
            "MQKPLAWGSKTALTELYGQRPMAE",
            "MQKPLAWGSKTALTELYGQRPMAEL",
            "MQKPLAWGSKTALTELYGQRPMAELW",
            "MQKPLAWGSKTALTELYGQRPMAELWM",
            "MQKPLAWGSKTALTELYGQRPMAELWMG",
            "MQKPLAWGSKTALTELYGQRPMAELWMGA",
            "MQKPLAWGSKTALTELYGQRPMAELWMGAH",
            "MQKPLAWGSKTALTELYGQRPMAELWMGAHP",
            "MQKPLAWGSKTALTELYGQRPMAELWMGAHPK",
            "MQKPLAWGSKTALTELYGQRPMAELWMGAHPKS",
            "MQKPLAWGSKTALTELYGQRPMAELWMGAHPKSS",
            "MQKPLAWGSKTALTELYGQRPMAELWMGAHPKSSS",
            "MQKPLAWGSKTALTELYGQRPMAELWMGAHPKSSSR",
            "PKSSSR",
            "PKSSSRVQNAA",
            "PLAWGSK",
            "PLAWGSKTALTELYGQRPMAELWMGAHPK",
            "PLAWGSKTALTELYGQRPMAELWMGAHPKSSSR",
            "PMAELWMGAHPK",
            "PMAELWMGAHPKSSSR",
            "PMAELWMGAHPKSSSRVQNAA",
            "QKPLAWGSK",
            "QKPLAWGSKTALTELYGQRPMAELWMGAHPK",
            "QKPLAWGSKTALTELYGQRPMAELWMGAHPKSSSR",
            "QNAA",
            "QRPMAELWMGAHPK",
            "QRPMAELWMGAHPKSSSR",
            "QRPMAELWMGAHPKSSSRVQNAA",
            "RPMAELWMGAHPK",
            "RPMAELWMGAHPKSSSR",
            "RPMAELWMGAHPKSSSRVQNAA",
            "RVQNAA",
            "SKTALTELYGQRPMAELWMGAHPK",
            "SKTALTELYGQRPMAELWMGAHPKSSSR",
            "SRVQNAA",
            "SSRVQNAA",
            "SSSR",
            "SSSRV",
            "SSSRVQ",
            "SSSRVQN",
            "SSSRVQNA",
            "SSSRVQNAA",
            "TALT",
            "TALTE",
            "TALTEL",
            "TALTELY",
            "TALTELYG",
            "TALTELYGQ",
            "TALTELYGQR",
            "TALTELYGQRP",
            "TALTELYGQRPM",
            "TALTELYGQRPMA",
            "TALTELYGQRPMAE",
            "TALTELYGQRPMAEL",
            "TALTELYGQRPMAELW",
            "TALTELYGQRPMAELWM",
            "TALTELYGQRPMAELWMG",
            "TALTELYGQRPMAELWMGA",
            "TALTELYGQRPMAELWMGAH",
            "TALTELYGQRPMAELWMGAHP",
            "TALTELYGQRPMAELWMGAHPK",
            "TALTELYGQRPMAELWMGAHPKS",
            "TALTELYGQRPMAELWMGAHPKSS",
            "TALTELYGQRPMAELWMGAHPKSSS",
            "TALTELYGQRPMAELWMGAHPKSSSR",
            "TALTELYGQRPMAELWMGAHPKSSSRV",
            "TALTELYGQRPMAELWMGAHPKSSSRVQ",
            "TALTELYGQRPMAELWMGAHPKSSSRVQN",
            "TALTELYGQRPMAELWMGAHPKSSSRVQNA",
            "TALTELYGQRPMAELWMGAHPKSSSRVQNAA",
            "TELYGQRPMAELWMGAHPK",
            "TELYGQRPMAELWMGAHPKSSSR",
            "TELYGQRPMAELWMGAHPKSSSRVQNAA",
            "VQNA",
            "VQNAA",
            "WGSK",
            "WGSKTALTELYGQRPMAELWMGAHPK",
            "WGSKTALTELYGQRPMAELWMGAHPKSSSR",
            "WMGAHPK",
            "WMGAHPKSSSR",
            "WMGAHPKSSSRVQNAA",
            "YGQRPMAELWMGAHPK",
            "YGQRPMAELWMGAHPKSSSR",
            "YGQRPMAELWMGAHPKSSSRVQNAA",
     };

    /**
     * manually build all the sequences
     */
    @Test
    public void testWithMultipleMissed() {
        IPeptideDigester digester = PeptideBondDigester.getDigester("[KR]|{P}");
        digester.setNumberMissedCleavages(2);
        // digester.setSemiTryptic(true);
        IProtein test = PROTEIN3;
        IPolypeptide[] polypeptides = digester.digest(test);
        Arrays.sort(polypeptides);
        List<String> holder = new ArrayList<String>();
        for (int i = 0; i < polypeptides.length; i++) {
            IPolypeptide polypeptide = polypeptides[i];
            holder.add(polypeptide.getSequence());
        }


        Assert.assertEquals(MISSED_CLEAVAGE_3.length, polypeptides.length);
        for (int i = 0; i < MISSED_CLEAVAGE_3.length; i++) {
            IPolypeptide pp = polypeptides[i];
            Assert.assertTrue(holder.contains(pp.getSequence()));

        }
    }

    /**
     * manually build all the sequences
     */
    @Test
    public void testWithSemiMultipleMissed() {
        IPeptideDigester digester = PeptideBondDigester.getDigester("[KR]|{P}");
        digester.setNumberMissedCleavages(2);
        digester.setSemiTryptic(true);
        IProtein test = PROTEIN3;
        IPolypeptide[] polypeptides = digester.addSemiCleavages(test);
        Arrays.sort(polypeptides);
        List<String> holder = new ArrayList<String>();
        for (int i = 0; i < polypeptides.length; i++) {
            IPolypeptide polypeptide = polypeptides[i];
            holder.add(polypeptide.getSequence());
        }

        for (int i = 0; i < polypeptides.length; i++) {
            IPolypeptide polypeptide = polypeptides[i];
            System.out.println("\"" + polypeptide.getSequence() + "\",");
        }


        Assert.assertEquals(SEMI_FRAGMENTS3.length, polypeptides.length);
        for (int i = 0; i < SEMI_FRAGMENTS3.length; i++) {
            IPolypeptide pp = polypeptides[i];
            Assert.assertTrue(holder.contains(pp.getSequence()));

        }
    }


}
