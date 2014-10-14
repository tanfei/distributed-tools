package org.systemsbiology.peptide;

import org.junit.*;
import org.systemsbiology.xtandem.peptide.*;

/**
 * org.systemsbiology.peptide.HeaderParserTests
 * User: Steve
 * Date: 10/14/2014
 */
public class HeaderParserTests {
    public static final String[] HEADERS = {
            ">sp|Q6GZX4|001R_FRG3G Putative transcription factor 001R OS=Frog virus 3 (isolate Goorha) GN=FV3-001R PE=4 SV=1",
            ">sp|Q6GZX3|002L_FRG3G Uncharacterized protein 002L OS=Frog virus 3 (isolate Goorha) GN=FV3-002L PE=4 SV=1",
            ">sp|Q197F8|002R_IIV3 Uncharacterized protein 002R OS=Invertebrate iridescent virus 3 GN=IIV3-002R PE=4 SV=1",
            ">sp|Q197F7|003L_IIV3 Uncharacterized protein 003L OS=Invertebrate iridescent virus 3 GN=IIV3-003L PE=4 SV=1",
            ">sp|Q6GZX2|003R_FRG3G Uncharacterized protein 3R OS=Frog virus 3 (isolate Goorha) GN=FV3-003R PE=4 SV=1",
            ">sp|Q6GZX1|004R_FRG3G Uncharacterized protein 004R OS=Frog virus 3 (isolate Goorha) GN=FV3-004R PE=4 SV=1",
            ">sp|Q6GZX0|005R_FRG3G Uncharacterized protein 005R OS=Frog virus 3 (isolate Goorha) GN=FV3-005R PE=4 SV=1",
            ">sp|Q91G88|006L_IIV6 Putative KilA-N domain-containing protein 006L OS=Invertebrate iridescent virus 6 GN=IIV6-006L PE=3 SV=1",
            ">sp|Q6GZW9|006R_FRG3G Uncharacterized protein 006R OS=Frog virus 3 (isolate Goorha) GN=FV3-006R PE=4 SV=1",
            ">sp|Q6GZW8|007R_FRG3G Uncharacterized protein 007R OS=Frog virus 3 (isolate Goorha) GN=FV3-007R PE=4 SV=1",
            ">sp|Q197F3|007R_IIV3 Uncharacterized protein 007R OS=Invertebrate iridescent virus 3 GN=IIV3-007R PE=4 SV=1",
            ">sp|Q197F2|008L_IIV3 Uncharacterized protein 008L OS=Invertebrate iridescent virus 3 GN=IIV3-008L PE=4 SV=1",
            ">sp|Q6GZW6|009L_FRG3G Putative helicase 009L OS=Frog virus 3 (isolate Goorha) GN=FV3-009L PE=4 SV=1"
    };
    public static final String[] IDS = {
            "Q6GZX4", //1R_FRG3G Putative transcription factor 001R OS=Frog virus 3 (isolate Goorha) GN=FV3-001R PE=4 SV=1",
            "Q6GZX3", //2L_FRG3G Uncharacterized protein 002L OS=Frog virus 3 (isolate Goorha) GN=FV3-002L PE=4 SV=1",
            "Q197F8", //2R_IIV3 Uncharacterized protein 002R OS=Invertebrate iridescent virus 3 GN=IIV3-002R PE=4 SV=1",
            "Q197F7", //3L_IIV3 Uncharacterized protein 003L OS=Invertebrate iridescent virus 3 GN=IIV3-003L PE=4 SV=1",
            "Q6GZX2", //3R_FRG3G Uncharacterized protein 3R OS=Frog virus 3 (isolate Goorha) GN=FV3-003R PE=4 SV=1",
            "Q6GZX1", //4R_FRG3G Uncharacterized protein 004R OS=Frog virus 3 (isolate Goorha) GN=FV3-004R PE=4 SV=1",
            "Q6GZX0", //5R_FRG3G Uncharacterized protein 005R OS=Frog virus 3 (isolate Goorha) GN=FV3-005R PE=4 SV=1",
            "Q91G88", //6L_IIV6 Putative KilA-N domain-containing protein 006L OS=Invertebrate iridescent virus 6 GN=IIV6-006L PE=3 SV=1",
            "Q6GZW9", //6R_FRG3G Uncharacterized protein 006R OS=Frog virus 3 (isolate Goorha) GN=FV3-006R PE=4 SV=1",
            "Q6GZW8", //7R_FRG3G Uncharacterized protein 007R OS=Frog virus 3 (isolate Goorha) GN=FV3-007R PE=4 SV=1",
            "Q197F3", //7R_IIV3 Uncharacterized protein 007R OS=Invertebrate iridescent virus 3 GN=IIV3-007R PE=4 SV=1",
            "Q197F2", //8L_IIV3 Uncharacterized protein 008L OS=Invertebrate iridescent virus 3 GN=IIV3-008L PE=4 SV=1",
            "Q6GZW6", //9L_FRG3G Putative helicase 009L OS=Frog virus 3 (isolate Goorha) GN=FV3-009L PE=4 SV=1"
    };

    @Test
    public void testHeaderParse() throws Exception {
        for (int i = 0; i < HEADERS.length; i++) {
            String header = HEADERS[i];
            String id = IDS[i];
            String actual = Protein.idFromAnnotation(header);
            Assert.assertEquals(id, actual);
          }

    }
}
