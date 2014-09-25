package org.systemsbiology.xtandem.pepxml;

import com.lordjoe.utilities.*;
import org.systemsbiology.xtandem.*;
import org.systemsbiology.xtandem.ionization.*;
import org.systemsbiology.xtandem.peptide.*;
import org.systemsbiology.xtandem.scoring.*;

import java.io.*;
import java.util.*;

/**
 * org.systemsbiology.xtandem.pepxml.PepXMLWriter
 * User: Steve
 * Date: 1/26/12
 */
public class PepXMLWriter {
    public static final PepXMLWriter[] EMPTY_ARRAY = {};

    private static   int gMatchesToPrint = OriginatingScoredScan.MAX_SERIALIZED_MATCHED;

    public static int getMatchesToPrint() {
        return gMatchesToPrint;
    }

    public static void setMatchesToPrint(final int matchesToPrint) {
        gMatchesToPrint = matchesToPrint;
    }

    private final IMainData m_Application;
    private String m_Path = "";

    public PepXMLWriter(final IMainData pApplication) {
        m_Application = pApplication;
    }

    public IMainData getApplication() {
        return m_Application;
    }

    public String getPath() {
        return m_Path;
    }

    public void setPath( String pPath) {
        int extIndex = pPath.lastIndexOf(".");
        if(extIndex > 1)
             pPath = pPath.substring(0,extIndex);
        m_Path = pPath;
    }

    public void writePepXML(IScoredScan scan, File out) {
        OutputStream os = null;
        try {
            System.setProperty("line.separator","\n"); // linux style cr
            os = new FileOutputStream(out);
            writePepXML(scan, out.getAbsolutePath().replace("\\", "/"), os);
        }
        catch (FileNotFoundException e) {
            throw new RuntimeException(e);

        }
    }

    public void writePepXML(IScoredScan scan, String path, OutputStream out) {

        try {
            PrintWriter pw = new PrintWriter(out);
            setPath(path);
            writePepXML(scan, pw);
        }
        finally {
            try {
                out.close();
            }
            catch (IOException e) {   // ignore
            }
        }

    }

    public void writePepXML(IScoredScan scan, PrintWriter out) {
        writeSummaries(scan, out);

    }

    public static final String PEPXML_HEADER =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                    "<?xml-stylesheet type=\"text/xsl\" href=\"pepXML_std.xsl\"?>\n";
//                    "<msms_pipeline_analysis date=\"%DATE%\"" +
//                    " summary_xml=\"%PATH%\"" +
//                    " xmlns=\"http://regis-web.systemsbiology.net/pepXML\"" +
//                    " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"" +
//                    " xsi:schemaLocation=\"http://sashimi.sourceforge.net/schema_revision/pepXML/pepXML_v117.xsd\"" +
//                    ">";

    public static final String TTRYPSIN_XML =
            "      <sample_enzyme name=\"trypsin\">\n" +
                    "         <specificity cut=\"KR\" no_cut=\"P\" sense=\"C\"/>\n" +
                    "      </sample_enzyme>";

    public static final String ANALYSIS_HEADER =
            "<msms_pipeline_analysis date=\"%DATE%\"  " +
                    //             "summary_xml=\"c:\\Inetpub\\wwwroot\\ISB\\data\\HighResMS2\\c:/Inetpub/wwwroot/ISB/data/HighResMS2/noHK-centroid.tandem.pep.xml\" " +
                    "xmlns=\"http://regis-web.systemsbiology.net/pepXML\"" +
                    " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"" +
                    " xsi:schemaLocation=\"http://sashimi.sourceforge.net/schema_revision/pepXML/pepXML_v115.xsd\">\n" +
                    "   <msms_run_summary base_name=\"%PATH%\" search_engine=\"Hydra(%ALGO%)\" msManufacturer=\"Thermo Scientific\" msModel=\"LTQ\" msIonization=\"NSI\" msMassAnalyzer=\"ITMS\" msDetector=\"unknown\" raw_data_type=\"raw\" raw_data=\".mzXML\">" +
                    "";


    public static final String SEARCH_SUMMARY_TEXT =
            "<search_summary base_name=\"%FULL_FILE_PATH%\" search_engine=\"Hydra(%ALGO%)\" precursor_mass_type=\"monoisotopic\" fragment_mass_type=\"monoisotopic\" search_id=\"1\">";

    public void writePepXMLHeader(String path,String algo, PrintWriter out) {
        String now = XTandemUtilities.xTandemNow();
        String header = PEPXML_HEADER.replace("%PATH%", path);
        header = header.replace("%DATE%", now);
        out.println(header);

        header = ANALYSIS_HEADER.replace("%PATH%", path);
        header = header.replace("%DATE%", now);
        header = header.replace("%ALGO%", algo);
          out.println(header);

        out.println(TTRYPSIN_XML);  // tod stop hard coding

        String ss = SEARCH_SUMMARY_TEXT.replace("%FULL_FILE_PATH%",path);
        ss = ss.replace("%ALGO%", algo);
        out.println(ss);  // tod stop hard coding

        showDatabase(out);
        showEnzyme(out);
        showModifications(out);
        showParameters(out);
         out.println("      </search_summary>");
    }

    protected void showModifications(PrintWriter out) {
        IMainData application = getApplication();
        ScoringModifications scoringMods = application.getScoringMods();
        PeptideModification[] modifications = scoringMods.getModifications();
        Arrays.sort(modifications);
        for(PeptideModification pm : modifications)     {
            showModification(pm,out);
        }
        PeptideModification.getTerminalModifications();
        Arrays.sort(modifications);
        for(PeptideModification pm : PeptideModification.getTerminalModifications())     {
             showModification(pm,out);
         }
    }

    /*
       <aminoacid_modification aminoacid="C" massdiff="57.0215" mass="160.0306" variable="N" />
         <aminoacid_modification aminoacid="C" massdiff="-17.0265" mass="143.0041" variable="Y" symbol="^" /><!--X! Tandem n-terminal AA variable modification-->
         <aminoacid_modification aminoacid="E" massdiff="-18.0106" mass="111.0320" variable="Y" symbol="^" /><!--X! Tandem n-terminal AA variable modification-->
         <aminoacid_modification aminoacid="K" massdiff="8.0142" mass="136.1092" variable="Y" />
         <aminoacid_modification aminoacid="M" massdiff="15.9949" mass="147.0354" variable="Y" />
         <aminoacid_modification aminoacid="Q" massdiff="-17.0265" mass="111.0321" variable="Y" symbol="^" /><!--X! Tandem n-terminal AA variable modification-->
         <aminoacid_modification aminoacid="R" massdiff="10.0083" mass="166.1094" variable="Y" />

     */
    protected void showModification(PeptideModification pm, PrintWriter out) {
        double massChange = pm.getMassChange();
        double pepideMass = pm.getPepideMass();
        String variable = "Y";
        if(pm.isFixed())
            variable = "N";

        out.print(" <aminoacid_modification");
        out.print(" aminoacid=\"" +  pm.getAminoAcid() + "\"");
        out.print(" massdiff=\"" + String.format("%10.4f",massChange).trim() + "\"");
        out.print(" mass=\"" + String.format("%10.4f",pepideMass).trim() + "\"");
        out.print("variable=\"" + variable + "\"");
         out.print(" />");
        if(pm.getRestriction() == PeptideModificationRestriction.CTerminal)
              out.print("<!--X! Tandem c-terminal AA variable modification-->");
        if(pm.getRestriction() == PeptideModificationRestriction.NTerminal)
              out.print("<!--X! Tandem n-terminal AA variable modification-->");

        out.println();
    }


    protected void showEnzyme(PrintWriter out) {
        IMainData application = getApplication();

        out.println("        <enzymatic_search_constraint enzyme=\"trypsin\" max_num_internal_cleavages=\"" +
                application.getDigester().getNumberMissedCleavages() +
                "\" />"
        );
    }

    protected void showDatabase(PrintWriter out) {
        IMainData application = getApplication();
        String[] parameterKeys = application.getParameterKeys();
        out.println("         <search_database local_path=\"" +
                application.getDatabaseName() +
                "\" type=\"AA\"" +
                 " />"
        );
    }

    protected void showParameters(PrintWriter out) {
        IMainData application = getApplication();
        String[] parameterKeys = application.getParameterKeys();
        out.println("        <!-- Input parameters -->");
        for (int i = 0; i < parameterKeys.length; i++) {
            String parameterKey = parameterKeys[i];
            String value = application.getParameter(parameterKey);
            out.println("        <parameter name=\"" +
                    parameterKey + "\"" +
                    " value=\"" +
                    value +
                    "\" />"
            );
        }
    }

    public void writePepXMLFooter(PrintWriter out) {
        out.println("     </msms_run_summary>");
        out.println("</msms_pipeline_analysis>");
    }

    protected void writeSummaries(IScoredScan scan, PrintWriter out) {

        String algorithm = scan.getAlgorithm();
        ISpectralMatch[] spectralMatches = scan.getSpectralMatches();
        if (!"KScore".equals(algorithm))
            XTandemUtilities.breakHere();

        if (spectralMatches.length == 0)
            return;
        writeScanHeader(scan, out);
        switch (spectralMatches.length) {
            case 0:
                return;
            case 1:
                printMatch(scan, spectralMatches[0], null,  out);
                break;

            default:
                printLimitedMatches(scan, out, spectralMatches, getMatchesToPrint() );
        }
        out.println("      </spectrum_query>");
    }

    private void printLimitedMatches(final IScoredScan scan, final PrintWriter out, final ISpectralMatch[] pSpectralMatches, int matchesToPrint) {
        out.println("         <search_result>");
        for (int i = 0; i < Math.min(matchesToPrint, pSpectralMatches.length); i++) {
            ISpectralMatch match = pSpectralMatches[i];
            int rank = i + 1;
            ISpectralMatch next = null;
            if (i < pSpectralMatches.length - 2)
                next = pSpectralMatches[i + 1];
            internalPrintMatch(scan, match, next, rank, out);
        }
          out.println("         </search_result>");
    }

    protected void writeScanHeader(IScoredScan scan, PrintWriter out) {
        String id = scan.getId();
        String idString = XTandemUtilities.asAlphabeticalId(id);   // will be safew for parsing as int
        int charge = scan.getCharge();

        out.print("      <spectrum_query ");
        double precursorMass = scan.getRaw().getPrecursorMass() - XTandemUtilities.getProtonMass();
        String path = getPath().replace("\\","/");
        int dirIndex = path.lastIndexOf("/");
        if(dirIndex > -1)
            path = path.substring(dirIndex + 1);
         out.print(" spectrum=\"" + path +  "." + idString +"." + idString + "." + charge + "\"");
        out.print(" start_scan=\""   + id + "\" end_scan=\""   + id + "\" ");
        out.print(" precursor_neutral_mass=\"" + String.format("%10.4f", precursorMass).trim() + "\"");
        double rt = scan.getRetentionTime();
        if(rt != 0)
            out.print(" retention_time_sec=\"" + String.format("%10.3f",rt).trim() + "\"");
        out.print(" assumed_charge=\"" + scan.getCharge() + "\"");
        out.println(" >");
    }


    protected void printMatch(IScoredScan scan, ISpectralMatch match, ISpectralMatch nextmatch,  PrintWriter out) {
        out.println("         <search_result>");
        internalPrintMatch(scan, match, nextmatch, 1, out);
        out.println("         </search_result>");
     }

    private void internalPrintMatch(IScoredScan scan, ISpectralMatch match, ISpectralMatch nextmatch, int hitNum, PrintWriter out) {
        out.print("            <search_hit hit_rank=\"" +
                hitNum +
                "\" peptide=\"");
        IPolypeptide peptide = match.getPeptide();
        out.print(peptide.getSequence());
        out.print("\"");
        IMeasuredSpectrum conditionedScan = scan.getConditionedScan();
        int totalPeaks = conditionedScan.getPeaks().length;
        RawPeptideScan raw = scan.getRaw();
        double precursorMass = raw.getPrecursorMass(scan.getCharge());
        double pepMass = peptide.getMatchingMass();
        double delMass = precursorMass - pepMass;
        int numberMatchedPeaks = match.getNumberMatchedPeaks();
        //     out.print(" peptide_prev_aa=\"K\" ");
        //     out.print("peptide_next_aa=\"I\" " );
        // Let Refresh parser analyze for now
        IProteinPosition[] proteinPositions = peptide.getProteinPositions();
        if (proteinPositions.length > 0) {
            IProteinPosition pp = proteinPositions[0];
            showProteinPosition( pp,out);
        }
        out.print("");
        out.print(" num_tot_proteins=\"" + proteinPositions.length + "\" ");

        out.print(" num_matched_ions=\"" + numberMatchedPeaks + "\"");
        out.print(" tot_num_ions=\"" + totalPeaks + "\"");
        out.print(" calc_neutral_pep_mass=\"" + totalPeaks + "\" ");
        out.print(" massdiff=\"" + String.format("%10.4f", delMass).trim() + "\" ");
        ////      out.print("num_tol_term=\"2\" ");
        int missed_cleavages = peptide.getMissedCleavages();
        out.print("num_missed_cleavages=\"" + missed_cleavages + "\" ");
        //     out.print("is_rejected=\"0\">\n");
        out.println(" >");

        for (int i = 1; i < proteinPositions.length; i++) {
            showAlternateiveProtein(proteinPositions[i], out);

        }

        if (peptide.isModified()) {
            showModificationInfo((IModifiedPeptide) peptide, out);

        }

        double value = 0;
        value = match.getHyperScore();
        out.println("             <search_score name=\"hyperscore\" value=\"" +
                String.format("%10.4f", value).trim() + "\"/>");

        if (nextmatch != null) {
            value = nextmatch.getHyperScore();
            out.println("             <search_score name=\"nextscore\" value=\"" +
                    String.format("%10.4f", value).trim() + "\"/>");
        }
        double bvalue = match.getScore(IonType.B);
        out.println("             <search_score name=\"bscore\" value=\"" +
                String.format("%10.4f", bvalue).trim() + "\"/>");

        double yvalue = match.getScore(IonType.Y);
        out.println("             <search_score name=\"yscore\" value=\"" +
                String.format("%10.4f", yvalue).trim() + "\"/>");

        HyperScoreStatistics hyperScores = scan.getHyperScores();
        double expected = hyperScores.getExpectedValue(match.getScore());
        out.println("             <search_score name=\"expect\" value=\"" +
                String.format("%10.4f", expected).trim() + "\"/>");
        out.println("              </search_hit>");
    }

    private void showProteinPosition( final IProteinPosition pPp,final PrintWriter out) {
        out.println(" protein=\"" + getId(pPp.getProtein()) + "\"");
        out.println("                       protein_descr=\"" + Util.xmlEscape(pPp.getProtein()) + "\"");
        out.print("                        ");
          FastaAminoAcid before = pPp.getBefore();
        if (before != null)
            out.print(" peptide_prev_aa=\"" + before + "\"");
        else
            out.print(" peptide_prev_aa=\"-\"");
        FastaAminoAcid after = pPp.getAfter();
        if (after != null)
            out.print(" peptide_next_aa=\"" + after + "\"");
        else
             out.print(" peptide_next_aa=\"-\"");
        out.println();
        out.print("                                     ");
    }

    protected void showModificationInfo(final IModifiedPeptide peptide, final PrintWriter out) {
        String totalModifiedSequence = peptide.getModifiedSequence();
        out.println("             <modification_info modified_peptide=\"" + totalModifiedSequence + "\" >");
        PeptideModification[] modifications = peptide.getModifications();
        for (int i = 0; i < modifications.length; i++) {
            PeptideModification modification = modifications[i];
            if (modification != null) {
                showModification(modification, i, out);
            }
        }
        out.println("             </modification_info>");

    }

    protected void showModification(final PeptideModification pModification, final int index, final PrintWriter out) {
        out.println("             <mod_aminoacid_mass position=\"" + (index + 1) + "\" mass=\"" + String.format("%10.3f", pModification.getPepideMass()).trim() + "\" />");
    }

    protected void showAlternateiveProtein(final IProteinPosition pp, final PrintWriter out) {
        out.print("             <alternative_protein ");
        showProteinPosition(pp, out);
        out.println(" />");
    }

    public static String getId(String descr)
    {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < descr.length(); i++) {
             char ch = descr.charAt(i);
            if(ch == ' ')
                break;
             if(Character.isJavaIdentifierPart(ch))
                 sb.append(ch);

         }

        return sb.toString();

    }


}
