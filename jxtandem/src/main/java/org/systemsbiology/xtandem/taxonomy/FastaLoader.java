package org.systemsbiology.xtandem.taxonomy;

//import org.springframework.dao.*;
//import org.springframework.jdbc.core.simple.*;
import org.apache.hadoop.fs.*;
import org.systemsbiology.common.*;
import org.systemsbiology.hadoop.*;
import org.systemsbiology.xtandem.*;
import org.systemsbiology.xtandem.peptide.*;
import org.systemsbiology.xtandem.scoring.*;

import java.io.*;
import java.util.*;

/**
 * org.systemsbiology.xtandem.taxonomy.FastaLoader
 * Writes bulk load files for protein database
 * NOTE this code is NOT thread safe
 * User: Steve
 * Date: Apr 11, 2011
 */
public class FastaLoader implements IFastaHandler {
    public static final FastaLoader[] EMPTY_ARRAY = {};

    // sometimes annotation gets REALLY long this limits length
    public static final int MAX_ANNOTATION_LENGTH = 767;

    public static final int NUMBER_MISSED_CLEAVAGES = 2;
    public static final int MAX_CHARGE = 3;

    public static final String DROP_MONO_INDEX_STRING =
            "DROP index load_fragments_imono_idx on   %TABLE%  ";
    public static final String CREATE_MONO_INDEX_STRING =
            "create index load_fragments_imono_idx on   %TABLE% (imono_mass,sequence)";
    public static final String DISABLE_LOGGING =
            "set sql_log_bin=0";
    public static final String TEST_STRING =
            "15.994915@M,8.014199@K,10.008269@R";


    public static final PeptideModification[] APPLIED_MODIFICATIONS =
            {
                    //      new PeptideModification("8.014199@K"),
                    //     new PeptideModification("15.994915@M"),
                    //     new PeptideModification("10.008269@R"),
            };


    private final FastaParser m_Parser = new FastaParser();

    private final IMainData m_Application;
    private final IPeptideDigester m_Digester;
    private final File m_ProteinLoadFile;
    private final File m_FragmentLoadFile;
    private final File m_SemiFragmentLoadFile;
    private PrintWriter m_ProteinWriter;
    private PrintWriter m_FragmentWriter;
    private PrintWriter m_SemiFragmentWriter;
    private int m_ProteinIndex;
    private int m_FragmentIndex;
    private Set<String> m_Annotations = new HashSet<String>();

    public FastaLoader(IMainData application, File proteins, File fragments, File semifragments) {
        m_Application = application;
        m_Digester = application.getDigester();
        m_Parser.addHandler(this);
        m_ProteinLoadFile = proteins;
        m_FragmentLoadFile = fragments;
        m_SemiFragmentLoadFile = semifragments;

        // hard code modifications of cystein
        MassCalculator monoCalc = MassCalculator.getCalculator(MassType.monoisotopic);
        MassCalculator averageCalc = MassCalculator.getCalculator(MassType.average);
    }


    public void parse(File inp) {
        try {
            InputStream is = new FileInputStream(inp);
            parse(is, inp.getName());
        }
        catch (FileNotFoundException e) {
            throw new RuntimeException(e);

        }
    }

    public void parse(InputStream is, String url) {
        m_ProteinIndex = 1;
        try {
            m_ProteinWriter = new PrintWriter(new FileWriter(m_ProteinLoadFile));
            m_FragmentWriter = new PrintWriter(new FileWriter(m_FragmentLoadFile));
            m_SemiFragmentWriter = new PrintWriter(new FileWriter(m_SemiFragmentLoadFile));
            m_Parser.parseFastaFile(is, url);
        }
        catch (IOException e) {
            throw new RuntimeException(e);

        }
        finally {
            if (getProteinWriter() != null)
                getProteinWriter().close();
            if (getFragmentWriter() != null)
                getFragmentWriter().close();
            if (getSemiFragmentWriter() != null)
                getSemiFragmentWriter().close();
        }
    }

    public IMainData getApplication() {
        return m_Application;
    }

    public PeptideModification[] getModifications() {
        ScoringModifications scoringMods = getApplication().getScoringMods();
        return scoringMods.getModifications();
    }


    public static PrintWriter buildRemoteWriter(IHDFSFileSystem fs, String remoteDirectory, File localFile) {
        String name = remoteDirectory + "/" + localFile.getName();
        Path path = new Path(name);
        OutputStream os = fs.openFileForWrite(path);
        return new PrintWriter(new OutputStreamWriter(os));
    }

    public void parseToRemote(InputStream is, IHDFSFileSystem fs, String remoteDirectory, String url) {
        m_ProteinIndex = 1;
        try {
            m_ProteinWriter = buildRemoteWriter(fs, remoteDirectory, m_ProteinLoadFile);
            m_FragmentWriter = buildRemoteWriter(fs, remoteDirectory, m_FragmentLoadFile);
            m_SemiFragmentWriter = buildRemoteWriter(fs, remoteDirectory, m_SemiFragmentLoadFile);
            m_Parser.parseFastaFile(is, url);
        }
        finally {
            if (getProteinWriter() != null)
                getProteinWriter().close();
            if (getFragmentWriter() != null)
                getFragmentWriter().close();
            if (getSemiFragmentWriter() != null)
                getSemiFragmentWriter().close();
        }
    }

    public IPeptideDigester getDigester() {
        return m_Digester;
    }

    public File getProteinLoadFile() {
        return m_ProteinLoadFile;
    }

    public File getFragmentLoadFile() {
        return m_FragmentLoadFile;
    }

    public File getSemiFragmentLoadFile() {
        return m_SemiFragmentLoadFile;
    }

    public PrintWriter getSemiFragmentWriter() {
        return m_SemiFragmentWriter;
    }

    public PrintWriter getProteinWriter() {
        return m_ProteinWriter;
    }

    public PrintWriter getFragmentWriter() {
        return m_FragmentWriter;
    }

    public int getProteinIndex() {
        return m_ProteinIndex;
    }

    public int getAndIncrementProteinIndex() {
        return m_ProteinIndex++;
    }

    public int getFragmentIndex() {
        return m_FragmentIndex;
    }

    public int getAndIncrementFragmentIndex() {
        return m_FragmentIndex++;
    }

    @Override
    public void handleProtein(String annotation, final String sequence) {

        // sometimes annotation gets REALLY long this limits length
        if (annotation.length() > MAX_ANNOTATION_LENGTH)
            annotation = annotation.substring(0, MAX_ANNOTATION_LENGTH);
        int proteinIndex = getAndIncrementProteinIndex();
        // no duplicates
        if (m_Annotations.contains(annotation))
            return;
        m_Annotations.add(annotation);

        getProteinWriter().println(Integer.toString(proteinIndex) + "\t" + annotation + "\t" + sequence);

        IPeptideDigester digester = getDigester();
        digester.setNumberMissedCleavages(NUMBER_MISSED_CLEAVAGES);

        IProtein prot = Protein.getProtein(  annotation,  annotation, sequence, null);
        int startPosition = 0;
        String lastSequence = "";
        IPolypeptide[] pps = digester.digest(prot);
        PrintWriter writer = getFragmentWriter();
        writeFragments(writer, sequence, proteinIndex, startPosition, lastSequence, pps);

        for (int i = 0; i < pps.length; i++) {
            IPolypeptide pp = pps[i];
            String seq = pp.getSequence();

            // debugging case slewis
            if (seq.contains("AIQFLEI"))
                XTandemUtilities.breakHere();
            // debugging case slewis
            if ("FRTTQLNMRFR".equalsIgnoreCase(seq)) {
                XTandemUtilities.breakHere();

            }
            // debugging case slewis
            //        if (XTandemUtilities.hasMissedCleavages(seq, NUMBER_MISSED_CLEAVAGES + 1)) {
            //         XTandemUtilities.outputLine(seq);
            //     }
        }


        boolean semiTryptic = digester.isSemiTryptic();
        if (semiTryptic) {
            IPolypeptide[] semipps = digester.addSemiCleavages(prot);
            PrintWriter semiwriter = getSemiFragmentWriter();
            writeFragments(semiwriter, sequence, proteinIndex, startPosition, lastSequence, semipps);
        }

    }

    private void writeFragments(PrintWriter writer, final String sequence, final int pProteinIndex, int pStartPosition, String pLastSequence, final IPolypeptide[] pPps) {
        PeptideModification[] modifications = getModifications();
        Arrays.sort(pPps);
        for (int i = 0; i < pPps.length; i++) {
            IPolypeptide pp = pPps[i];
            String psequence = pp.getSequence();

            if (XTandemUtilities.ignoreSequence(psequence))  // don't do length 1
                continue;
            if (pLastSequence.equals(psequence)) {
                // this is a repeat
            }
            else {
                pStartPosition = 0;
                pLastSequence = psequence;
            }
            pStartPosition = addPeptideAtCharge(writer, 1, sequence, pProteinIndex, pStartPosition, pp, psequence);
            if (modifications.length > 0) {
                IModifiedPeptide[] iModifiedPeptides = ModifiedPolypeptide.buildAllModifications(pp, modifications);
                for (int j = 0; j < iModifiedPeptides.length; j++) {
                    IModifiedPeptide iModifiedPeptide = iModifiedPeptides[j];
                    addPeptideAtCharge(writer, 1, sequence, pProteinIndex, pStartPosition, iModifiedPeptide, psequence);
                }
            }
            //  for (int charge = 1; charge <= MAX_CHARGE ;charge++) {
            //      pStartPosition = addPeptideAtCharge(writer,charge, sequence, pProteinIndex, pStartPosition, pp, psequence);
            //   }
            //     if (numberFragments < 100)
            //         XTandemUtilities.outputLine(fragment);
        }
    }

    protected int addPeptideAtCharge(final PrintWriter writer, int charge, final String sequence, final int pProteinIndex, int pStartPosition, final IPolypeptide pPp, final String pPsequence) {
        pStartPosition = sequence.indexOf(pPsequence, pStartPosition);
        double aMass = XTandemUtilities.getAverageMass(pPp) / charge;
        int averagemass = XTandemUtilities.getDefaultConverter().asInteger(aMass);
        double mMass = XTandemUtilities.getMonoisotopicMass(pPp) / charge;
        int monomass = XTandemUtilities.getDefaultConverter().asInteger(aMass);

        if (Math.abs(aMass - mMass) > Math.max(100, aMass) / 800) {
            mMass = XTandemUtilities.getMonoisotopicMass(pPp) / charge;
            aMass = XTandemUtilities.getAverageMass(pPp) / charge;
            throw new UnsupportedOperationException("Bad average or mono mass");
        }

        int numberFragments = getAndIncrementFragmentIndex();
        String fragment =
                (numberFragments * MAX_CHARGE + charge) + "\t" +   // use only in no_key
                        pPp.getSequence() + "\t" +
                        Integer.toString(pProteinIndex) + "\t" +
                        pStartPosition + "\t" +
                        aMass + "\t" +
                        averagemass + "\t" +
                        mMass + "\t" +
                        monomass + "\t" +
                        pPp.getMissedCleavages();

        pStartPosition++; // mzake sure we do not find current fragment on next search
        writer.println(fragment);
        return pStartPosition;
    }

//    private static void parseFile(final String pFileName) {
//        IPeptideDigester digester = PeptideBondDigester.getDigester("trypsin");
//        digester.setNumberMissedCleavages(2);
//        digester.setSemiTryptic(true);
//        ElapsedTimer et = new ElapsedTimer();
//
//        File item = new File(pFileName);
//        File proteins = new File(pFileName.replace(".fasta", "proteins.sql"));
//        File fragments = new File(pFileName.replace(".fasta", "fragments.sql"));
//        File semifragments = new File(pFileName.replace(".fasta", "semifragments.sql"));
//
//        FastaLoader fs = new FastaLoader(digester, proteins, fragments, semifragments);
//        fs.parse(item);
//
//        XTandemUtilities.outputLine("Parsed " + fs.getProteinIndex() + " and " + fs.getFragmentIndex() + " fragments ");
//        et.showElapsed("Parsed in ");
//    }
//    private void handleModifiedPeptides3(final IModifiedPeptide pModification[], final SimpleJdbcTemplate db) {
//        String query = buildModifiedInsertQuery(3);
//        IModifiedPeptide mod = pModification[0];
//        IModifiedPeptide mod2 = pModification[1];
//        IModifiedPeptide mod3 = pModification[2];
//        db.update(query,
//                new Integer((int) (mod.getMass() + XTandemUtilities.getAddedMass() + ADDED_MODIFIED_MASS)),
//                mod.getModificationString(),
//                mod.getSequence(),
//                mod.getModifiedSequence(),
//                new Double(mod.getMass() + XTandemUtilities.getAddedMass()),
//                new Integer(mod.getMissedCleavages()),
//
//                new Integer((int) (mod2.getMass() + XTandemUtilities.getAddedMass() + ADDED_MODIFIED_MASS)),
//                mod2.getModificationString(),
//                mod2.getSequence(),
//                mod2.getModifiedSequence(),
//                new Double(mod2.getMass() + XTandemUtilities.getAddedMass()),
//                new Integer(mod2.getMissedCleavages()),
//
//                new Integer((int) (mod3.getMass() + XTandemUtilities.getAddedMass() + ADDED_MODIFIED_MASS)),
//                mod3.getModificationString(),
//                mod3.getSequence(),
//                mod3.getModifiedSequence(),
//                new Double(mod3.getMass() + XTandemUtilities.getAddedMass()),
//                new Integer(mod3.getMissedCleavages())
//        );
//
//    }

//
//    protected static TestMain buildMain(String host, String database) {
//        TestMain main = new TestMain();
//
//        main.setParameter(SpringJDBCUtilities.DATA_HOST_PARAMETER, host);
//        main.setParameter(SpringJDBCUtilities.DATA_DATABASE_PARAMETER, database);
//        main.setParameter(SpringJDBCUtilities.DATA_USER_PARAMETER, "proteomics");
//        main.setParameter(SpringJDBCUtilities.DATA_PASSWORD_PARAMETER, "tandem");
//        main.setParameter(SpringJDBCUtilities.DATA_DRIVER_CLASS_PARAMETER, "com.mysql.jdbc.Driver");
//        return main;
//    }
//
    /*
    select count(*) FROM  proteomics.proteins

LOAD Data LOCAL INFILE 'E:/Concurrent/JXTandem/fasta/uniprothumproteins.sql' into table proteomics.proteins

     */

    public static void main(String[] args) {
        String paramsFileName = args[0];
        boolean isCompleteLoad = args.length > 1 && "clean".equals(args[1]);
        throw new UnsupportedOperationException("Dropped database stuff for now"); // Dropped database stuff for now
//
//
//        File paramsFile = new File(paramsFileName);
//        if (!paramsFile.exists() || !paramsFile.canRead() || !paramsFile.isFile())
//            throw new IllegalStateException("bad params file " + paramsFile);
//        IMainData main = new XTandemMain(paramsFile);
//
//        String taxonomyFile = main.getParameter("list path, taxonomy information"); //, "taxonomy.xml");
//        String taxonomyName = main.getParameter("protein, taxon");
//
//        Taxonomy oldTax = new Taxonomy(main, taxonomyName, taxonomyFile);
//
//        String host = main.getParameter("org.systemsbiology.xtandem.Datasource.Host");
//        String database = main.getParameter("org.systemsbiology.xtandem.Datasource.Database");
//
//        JDBCTaxonomy tax = new JDBCTaxonomy(main);
//        SimpleJdbcTemplate template = tax.getTemplate();
//        try {
//            int count = template.queryForInt("select count(*) from  proteins");
//            if (count == 0)
//                isCompleteLoad = true; // better rebuild all
//        }
//        catch (DataAccessException e) {
//            isCompleteLoad = true; // better rebuild all
//
//        }
//
//        /**
//         * specify clean to reload the database
//         */
//        if (isCompleteLoad)
//            SpringJDBCUtilities.dropDatabase(host, database);
//
//        IPeptideDigester digester = main.getDigester();
//
//        String taxonomyBase = database;
//        if (taxonomyBase == null || taxonomyBase.isEmpty())
//            taxonomyBase = taxonomyFile.replace(".fasta", "");
//        File proteins = new File(taxonomyBase + ".proteins.sql");
//        File fragments = new File(taxonomyBase + ".fragments.sql");
//        File semifragments = new File(taxonomyBase + ".semifragments.sql");
//        String path = semifragments.getAbsolutePath();
//
//        if (isCompleteLoad) {
//            proteins.delete();
//            fragments.delete();
//            semifragments.delete();
//            if (proteins.exists() || fragments.exists() || semifragments.exists())
//                throw new IllegalStateException("cannot clean files ");
//        }
//
//        FastaLoader fs = new FastaLoader(main, proteins, fragments, semifragments);
//
//        SpringJDBCUtilities.guaranteeDatabase(host, database);
//        TaxonomyDatabase tdb = new TaxonomyDatabase(template);
//        tdb.createDatabase();
//        ElapsedTimer et = new ElapsedTimer();
//        String[] taxomonyFiles = oldTax.getTaxomonyFiles();
//        File theFile = new File(taxomonyFiles[0]);
//        if (!theFile.exists())
//            throw new IllegalArgumentException("file " + theFile + " does not exist");
//
//        if (!fragments.exists()) {
//            fs.parse(theFile);
//            et.showElapsed(System.out);
//            et.reset();
//        }
//
//        if (isCompleteLoad) {
//            fs.loadDatabase(template);
//        }
//        else {
//            PeptideModification[] pms = fs.getModifications();
//            if (pms.length > 0)
//                fs.guaranteeModifications(template, pms);
//        }
//
//        et.showElapsed(System.out);
//
//
    }


}
