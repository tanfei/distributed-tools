package org.systemsbiology.xtandem.mzml;

import java.io.*;

/**
 * org.systemsbiology.xtandem.mzml.MZMLSplitter
 * User: steven
 * Date: 10/12/11
 */
public class MZMLSplitter {
    public static final MZMLSplitter[] EMPTY_ARRAY = {};

    public static final String MZML_HEADER =
            "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n" +
                    "<indexedmzML xmlns=\"http://psi.hupo.org/ms/mzml\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://psi.hupo.org/ms/mzml http://psidev.info/files/ms/mzML/xsd/mzML1.1.1_idx.xsd\">\n" +
                    "  <mzML xmlns=\"http://psi.hupo.org/ms/mzml\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://psi.hupo.org/ms/mzml http://psidev.info/files/ms/mzML/xsd/mzML1.1.0.xsd\" id=\"VE2_2010_1105_SCX_fr20\" version=\"1.1.0\">\n" +
                    "    <cvList count=\"2\">\n" +
                    "      <cv id=\"MS\" fullName=\"Proteomics Standards Initiative Mass Spectrometry Ontology\" version=\"2.33.1\" URI=\"http://psidev.cvs.sourceforge.net/*checkout*/psidev/psi/psi-ms/mzML/controlledVocabulary/psi-ms.obo\"/>\n" +
                    "      <cv id=\"UO\" fullName=\"Unit Ontology\" version=\"11:02:2010\" URI=\"http://obo.cvs.sourceforge.net/*checkout*/obo/obo/ontology/phenotype/unit.obo\"/>\n" +
                    "    </cvList>\n" +
                    "    <fileDescription>\n" +
                    "      <fileContent>\n" +
                    "        <cvParam cvRef=\"MS\" accession=\"MS:1000128\" name=\"profile spectrum\" value=\"\"/>\n" +
                    "      </fileContent>\n" +
                    "      <sourceFileList count=\"2\">\n" +
                    "        <sourceFile id=\"VE2_2010_1105_SCX_fr20.raw\" name=\"VE2_2010_1105_SCX_fr20.raw\" location=\"file://.\">\n" +
                    "          <cvParam cvRef=\"MS\" accession=\"MS:1000569\" name=\"SHA-1\" value=\"d7590d6f957545b8f9919a57bfd7c052bc232d52\"/>\n" +
                    "          <cvParam cvRef=\"MS\" accession=\"MS:1000563\" name=\"Thermo RAW file\" value=\"\"/>\n" +
                    "          <cvParam cvRef=\"MS\" accession=\"MS:1000768\" name=\"Thermo nativeID format\" value=\"\"/>\n" +
                    "        </sourceFile>\n" +
                    "        <sourceFile id=\"VE2_2010_1105_SCX_fr20.mzXML\" name=\"VE2_2010_1105_SCX_fr20.mzXML\" location=\"file://.\">\n" +
                    "          <cvParam cvRef=\"MS\" accession=\"MS:1000776\" name=\"scan number only nativeID format\" value=\"\"/>\n" +
                    "          <cvParam cvRef=\"MS\" accession=\"MS:1000566\" name=\"ISB mzXML file\" value=\"\"/>\n" +
                    "          <cvParam cvRef=\"MS\" accession=\"MS:1000569\" name=\"SHA-1\" value=\"df88ebc00bb3d6db20a3935f72256d302386467a\"/>\n" +
                    "        </sourceFile>\n" +
                    "      </sourceFileList>\n" +
                    "    </fileDescription>\n" +
                    "    <softwareList count=\"2\">\n" +
                    "      <software id=\"Xcalibur_x0020_software\" version=\"2.6.0\">\n" +
                    "        <cvParam cvRef=\"MS\" accession=\"MS:1000532\" name=\"Xcalibur\" value=\"\"/>\n" +
                    "      </software>\n" +
                    "      <software id=\"pwiz_2.0.1880_x0020__x0028_TPP_x0020_v4.4_x0020_VUVUZELA_x0020_rev_x0020_0_x002c__x0020_Build_x0020_201008171641_x0020__x0028_linux_x0029__x0029_\" version=\"2.0.1880 (TPP v4.4 VUVUZELA rev 0, Build 201008171641 (linux))\">\n" +
                    "        <cvParam cvRef=\"MS\" accession=\"MS:1000615\" name=\"ProteoWizard\" value=\"\"/>\n" +
                    "      </software>\n" +
                    "    </softwareList>\n" +
                    "    <instrumentConfigurationList count=\"2\">\n" +
                    "      <instrumentConfiguration id=\"IC1\">\n" +
                    "        <cvParam cvRef=\"MS\" accession=\"MS:1000855\" name=\"LTQ Velos\" value=\"\"/>\n" +
                    "        <componentList count=\"3\">\n" +
                    "          <source order=\"1\">\n" +
                    "            <cvParam cvRef=\"MS\" accession=\"MS:1000398\" name=\"nanoelectrospray\" value=\"\"/>\n" +
                    "          </source>\n" +
                    "          <analyzer order=\"1\">\n" +
                    "            <cvParam cvRef=\"MS\" accession=\"MS:1000484\" name=\"orbitrap\" value=\"\"/>\n" +
                    "          </analyzer>\n" +
                    "          <detector order=\"1\">\n" +
                    "            <cvParam cvRef=\"MS\" accession=\"MS:1000624\" name=\"inductive detector\" value=\"\"/>\n" +
                    "          </detector>\n" +
                    "        </componentList>\n" +
                    "        <softwareRef ref=\"Xcalibur_x0020_software\"/>\n" +
                    "      </instrumentConfiguration>\n" +
                    "      <instrumentConfiguration id=\"IC2\">\n" +
                    "        <cvParam cvRef=\"MS\" accession=\"MS:1000855\" name=\"LTQ Velos\" value=\"\"/>\n" +
                    "        <componentList count=\"3\">\n" +
                    "          <source order=\"1\">\n" +
                    "            <cvParam cvRef=\"MS\" accession=\"MS:1000398\" name=\"nanoelectrospray\" value=\"\"/>\n" +
                    "          </source>\n" +
                    "          <analyzer order=\"1\">\n" +
                    "            <cvParam cvRef=\"MS\" accession=\"MS:1000083\" name=\"radial ejection linear ion trap\" value=\"\"/>\n" +
                    "          </analyzer>\n" +
                    "          <detector order=\"1\">\n" +
                    "            <cvParam cvRef=\"MS\" accession=\"MS:1000253\" name=\"electron multiplier\" value=\"\"/>\n" +
                    "          </detector>\n" +
                    "        </componentList>\n" +
                    "        <softwareRef ref=\"Xcalibur_x0020_software\"/>\n" +
                    "      </instrumentConfiguration>\n" +
                    "    </instrumentConfigurationList>\n" +
                    "    <dataProcessingList count=\"2\">\n" +
                    "      <dataProcessing id=\"Xcalibur_x0020_software_x0020_processing\">\n" +
                    "        <processingMethod order=\"0\" softwareRef=\"Xcalibur_x0020_software\">\n" +
                    "          <userParam name=\"type\" value=\"unknown software type\"/>\n" +
                    "        </processingMethod>\n" +
                    "      </dataProcessing>\n" +
                    "      <dataProcessing id=\"pwiz_Reader_conversion\">\n" +
                    "        <processingMethod order=\"0\" softwareRef=\"pwiz_2.0.1880_x0020__x0028_TPP_x0020_v4.4_x0020_VUVUZELA_x0020_rev_x0020_0_x002c__x0020_Build_x0020_201008171641_x0020__x0028_linux_x0029__x0029_\">\n" +
                    "          <cvParam cvRef=\"MS\" accession=\"MS:1000544\" name=\"Conversion to mzML\" value=\"\"/>\n" +
                    "        </processingMethod>\n" +
                    "      </dataProcessing>\n" +
                    "    </dataProcessingList>\n" +
                    "    <run id=\"VE2_2010_1105_SCX_fr20\" defaultInstrumentConfigurationRef=\"IC1\">\n" +
                    "      <spectrumList count=\"18431\" defaultDataProcessingRef=\"pwiz_Reader_conversion\">";

    public static final String MZML_FOOTER =
            "       </spectrumList>\n" +
                    "   </run>\n" +
                    "  </mzML>\n" +
                    " </indexedmzML>\n";

    public static final int SPLIT_SIZE = 50 * 1000 * 1000;
    public static final int MAX_SPLITS = 0;

    private static void handleMZMLSplit(String fileName, final LineNumberReader pRdr, final int pSplitSize, final int pMaxSplits) {
        int Split_number = 1;
        String outFile = fileName.replace(".mzML", Integer.toString(Split_number++) + ".mzML");
        boolean done = buildMZMLSplit(outFile, pRdr, pSplitSize);
        while (!done && pMaxSplits == 0 || Split_number < pMaxSplits) {
            outFile = fileName.replace(".mzML", Integer.toString(Split_number++) + ".mzML");
            done = buildMZMLSplit(outFile, pRdr, pSplitSize);
        }

    }

    private static void handleFASTASplit(String fileName, final LineNumberReader pRdr, final int pSplitSize, final int pMaxSplits) {
        int Split_number = 1;
        String outFile = fileName.replace(".fasta", Integer.toString(Split_number++) + ".fasta");
        boolean done = buildFASTASplit(outFile, pRdr, pSplitSize);
        while (!done && pMaxSplits == 0 || Split_number < pMaxSplits) {
            outFile = fileName.replace(".fasta", Integer.toString(Split_number++) + ".fasta");
            done = buildFASTASplit(outFile, pRdr, pSplitSize);
        }

    }

    private static boolean buildFASTASplit(String fileName, final LineNumberReader pRdr, final int pSplitSize) {
         PrintWriter out = null;
         try {
             String line = pRdr.readLine();
             if (line == null)
                 return true; // done
             out = new PrintWriter(new FileWriter(fileName));

             long fileSize = 0;
             while (line != null) {
                 if (line.startsWith(">")) {
                     break;
                 }
                 line = pRdr.readLine();
             }
             while (line != null) {
                 fileSize += line.length() + 1;
                 if (line.startsWith(">")) {
                     if (fileSize > pSplitSize)
                         break;
                 }
                 out.println(line);
                 line = pRdr.readLine();
             }
              return line == null;
         }
         catch (IOException e) {
             throw new RuntimeException(e);

         }
         finally {
             if (out != null)
                 out.close();
         }
     }


    private static void handleMGFSplit(String fileName, final LineNumberReader pRdr, final int pSplitSize, final int pMaxSplits) {
        int Split_number = 1;
        String outFile = fileName.replace(".mgf", Integer.toString(Split_number++) + ".mgf");
        boolean done = buildMGFSplit(outFile, pRdr, pSplitSize);
        while (!done && pMaxSplits == 0 || Split_number < pMaxSplits) {
            outFile = fileName.replace(".mgf", Integer.toString(Split_number++) + ".mgf");
            done = buildMGFSplit(outFile, pRdr, pSplitSize);
        }

    }

    private static boolean buildMGFSplit(String fileName, final LineNumberReader pRdr, final int pSplitSize) {
         PrintWriter out = null;
         try {
             String line = pRdr.readLine();
             if (line == null)
                 return true; // done
             out = new PrintWriter(new FileWriter(fileName));

             long fileSize = 0;
             while (line != null) {
                 if (line.startsWith("BEGIN IONS")) {
                     break;
                 }
                 line = pRdr.readLine();
             }
             while (line != null) {
                 fileSize += line.length() + 1;
                 if (line.startsWith("BEGIN IONS")) {
                     if (fileSize > pSplitSize)
                         break;
                 }
                 out.println(line);
                 line = pRdr.readLine();
             }
              return line == null;
         }
         catch (IOException e) {
             throw new RuntimeException(e);

         }
         finally {
             if (out != null)
                 out.close();
         }
     }



    private static boolean buildMZMLSplit(String fileName, final LineNumberReader pRdr, final int pSplitSize) {
        PrintWriter out = null;
        try {
            String line = pRdr.readLine();
            if (line == null)
                return true; // done
            out = new PrintWriter(new FileWriter(fileName));
            out.println(MZML_HEADER);

            long fileSize = 0;
            while (line != null) {
                if (line.contains("<spectrum")) {
                    break;
                }
                line = pRdr.readLine();
            }
            while (line != null) {
                out.println(line);
                fileSize += line.length() + 1;
                if (line.contains("</spectrum")) {
                    if (fileSize > pSplitSize)
                        break;
                }
                if (line.contains("</spectrumList")) {   // at end of file
                    while (line != null)
                        line = pRdr.readLine(); // finish reading
                    break;
                }
                line = pRdr.readLine();
            }
            out.println(MZML_FOOTER);
            return line == null;
        }
        catch (IOException e) {
            throw new RuntimeException(e);

        }
        finally {
            if (out != null)
                out.close();
        }
    }


    public static void main(String[] args) throws Exception {
        String fileName = args[0];
        int megs = 50;
        if(args.length > 1)
            megs = Integer.parseInt(args[1]);

        FileReader fs = new FileReader(fileName);
        LineNumberReader rdr = new LineNumberReader(fs);
        //       handleSplit(args[0], rdr, SPLIT_SIZE, MAX_SPLITS);
        int oneMeg = 1024 * 1024;
        int oneGig = 1024 * oneMeg;
        if (fileName.toLowerCase().endsWith(".mzml")) {
            handleMZMLSplit(fileName, rdr, megs * oneMeg, 1);
            return;
        }
        if (fileName.toLowerCase().endsWith(".fasta")) {
            handleFASTASplit(fileName, rdr, megs * oneMeg, 0);
            return;
        }
        if (fileName.toLowerCase().endsWith(".mgf")) {
            handleMGFSplit(fileName, rdr, megs * oneMeg, 0);
            return;
        }


    }
}


