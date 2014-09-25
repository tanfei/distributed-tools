package org.systemsbiology.xtandem.taxonomy;

import com.lordjoe.utilities.*;
import org.junit.*;
//import org.springframework.jdbc.core.simple.*;
import org.systemsbiology.xtandem.*;
import org.systemsbiology.xtandem.hadoop.*;
import org.systemsbiology.xtandem.peptide.*;
import org.systemsbiology.xtandem.scoring.*;

import java.io.*;
import java.util.*;

/**
 * org.systemsbiology.xtandem.taxonomy.JDBCTaxonomyTests
 * User: Steve
 * Date: Apr 7, 2011
 */
public class JDBCTaxonomyTests {
    public static final JDBCTaxonomyTests[] EMPTY_ARRAY = {};

//    // Propably this test will blow memory
//   // @Test
////    public void testFastaRead() {
////        IMainData main = buildMain();
////        JDBCTaxonomy tax = new JDBCTaxonomy(main);
////        IPeptideDigester peptideDigester = PeptideBondDigester.getDigester("trypsin");
////        ((PeptideBondDigester) peptideDigester).setNumberMissedCleavages(2);
////
////
////        tax.setDigester(peptideDigester);
////        tax.readFasta(XTandemUtilities.getResourceStream("largeSample/Halobacterium-20080205_TargDecoy.fasta"), "foo");
////    }
//
//    // I am
////    @Test
//    public void testXMLParse() {
//        IMainData main = buildMain();
//        TopLevelScanScoreHandler handler = new TopLevelScanScoreHandler(main, null);
//        String text = FileUtilities.readInResource(XTandemUtilities.class, "largeSample/parseError.xml");
//        IScoredScan scoredScan = XTandemHadoopUtilities.parseXMLString(text, handler);
//        Assert.assertNotNull(scoredScan);
//    }
//
//
//
//    /**
//     * this only works when pointed at the user.dir is the data directory
//     */
////    @Test
//    public void testGetPeptidesOfMass() {
//
//        InputStream stream = XTandemUtilities.getResourceStream("largeSample/tandem_database.params");
//        XTandemMain main = new XTandemMain(stream, "tandem_database.params");
//
//        main.loadScoringTest();
//
//        final ITaxonomy taxonomy = main.getTaxonomy();
//        final Scorer scorer = main.getScoreRunner();
//        scorer.digest();
//        double mass = 2189;
//        IPolypeptide[] peptides = scorer.getPeptidesOfMass(mass);
//        Arrays.sort(peptides);
//        Assert.assertEquals(1, peptides.length);
//        Assert.assertEquals("HAFYQSANVPAGLLDYQHR", peptides[0].getSequence());
//
//        // we built a database taxonomy
//        Assert.assertEquals(JDBCTaxonomy.class, taxonomy.getClass());
//
//        IPolypeptide[] dbpeptides = taxonomy.getPeptidesOfMass(mass);
//        Arrays.sort(dbpeptides);
//        Assert.assertEquals("HAFYQSANVPAGLLDYQHR", dbpeptides[0].getSequence());
//        Assert.assertArrayEquals(peptides, dbpeptides);
//
//
//    }
//
//
// //   @Test
//    public void testGuaranteeDatabase() {
//        SpringJDBCUtilities.guaranteeDatabase("localhost", "halobacterium");
//
//        SpringJDBCUtilities.guaranteeDatabase("localhost", "foo");
//        SpringJDBCUtilities.dropDatabase("localhost", "foo");
//
//    }
//
//    /**
//     * Test that we get the same peptides
//     */
//  //  @Test  Fix by rebuilding database
//    public void testGetPeptidesOfMassLarge() {
//
//        ElapsedTimer et = new ElapsedTimer();
//        InputStream stream = XTandemUtilities.getResourceStream("largeSample/tandem_database_medium.params");
//        XTandemMain main = new XTandemMain(stream, "tandem_database_medium.params");
//        if(!JXTandemTestConfiguration.isDatabaseAccessible(main))
//            return;
//
//        Set<IPolypeptide> notFoundByDb = new HashSet<IPolypeptide>();
//        Set<IPolypeptide> notFoundByDigester = new HashSet<IPolypeptide>();
//
//        main.loadScoringTest();
//
//        final ITaxonomy taxonomy = main.getTaxonomy();
//        final Scorer scorer = main.getScoreRunner();
//        scorer.digest();
//
//        int maxMass = 4000;
//        int minMass = 130;
//        int gotSameMasses = 0;
//        int gotSameEmptyMasses = 0;
//        int foundSameMass = 0;
//        int notFoundSameMass = 0;
//
//        int numberCommon = 0;
//        int numberScoreronly = 0;
//        int numberDBronly = 0;
//
//        Assert.assertEquals(JDBCTaxonomy.class, taxonomy.getClass());
//        int mass = minMass;
//        for (; mass < maxMass; mass++) {
//            IPolypeptide[] peptides = scorer.getPeptidesOfMass(mass);
//            Set<IPolypeptide> scoreerFound = new HashSet<IPolypeptide>(Arrays.asList(peptides));
//            Arrays.sort(peptides);
//            IPolypeptide[] dbpeptides = taxonomy.getPeptidesOfMass(mass);
//            Arrays.sort(dbpeptides);
//            Set<IPolypeptide> dbFound = new HashSet<IPolypeptide>(Arrays.asList(dbpeptides));
//            if (dbFound.size() == 0 && scoreerFound.size() == 0)
//                continue;
//
//            Set<IPolypeptide> common = new HashSet<IPolypeptide>(scoreerFound);
//            common.retainAll(dbFound);
//            numberCommon += common.size();
//
//            Set<IPolypeptide> scorerOnly = new HashSet<IPolypeptide>(scoreerFound);
//            scorerOnly.removeAll(dbFound);
//            numberScoreronly += scorerOnly.size();
//            notFoundByDb.addAll(scorerOnly);
//
//            Set<IPolypeptide> dbOnly = new HashSet<IPolypeptide>(dbFound);
//            dbOnly.removeAll(scoreerFound);
//            numberDBronly += dbOnly.size();
//            notFoundByDigester.addAll(dbOnly);
//
//
//            if (dbpeptides.length == 0 && peptides.length == 0)
//                gotSameEmptyMasses++;
//
//        }
//
//        et.showElapsed("Done with 4000 masses");
//        int baddigests = notFoundByDb.size();
//        int badLookups = notFoundByDigester.size();
//
//
//    }
//
//  //  @Test
//    public void testHumanFastaRead() {
//        try {
//            IMainData main = buildMain();
//            if(!JXTandemTestConfiguration.isDatabaseAccessible(main))
//                return;
//            JDBCTaxonomy tax = new JDBCTaxonomy(main);
//
//            IPeptideDigester peptideDigester = PeptideBondDigester.getDigester("trypsin");
//            ((PeptideBondDigester) peptideDigester).setNumberMissedCleavages(2);
//
//
//            tax.setDigester(peptideDigester);
//            tax.readFasta(XTandemUtilities.getResourceStream("fasta/uniprothum.fasta"), "uniprothum");
//        }
//        catch (CannotAccessDatabaseException e) {
//             // this means you cannot read the database
//
//        }
//    }
//
//    @Test
//    public void testJDBCAccess() {
//        try {
//      //      Protein.clearProteinCache();
//            IMainData main = buildMain();
//            if(!JXTandemTestConfiguration.isDatabaseAccessible(main))
//                return;
//            JDBCTaxonomy tax = new JDBCTaxonomy(main);
//
//            SimpleJdbcTemplate template = tax.getTemplate();
//
//            int protein_count = template.queryForInt("select count(*) FROM   proteins ");
//
//            Assert.assertTrue(protein_count > 100000);
//
//            List<IProtein> answer = template.query("select * from proteins LIMIT 1000;", SpringJDBCUtilities.PROTEIN_MAPPER);
//            Assert.assertTrue(answer.size() == 1000 );
//        }
//         catch (CannotAccessDatabaseException e) {
//             // this means teh database is not available
//
//         }
//
//    }
//
//    @Test
//    public void testGuaranteeTable() {
//        try {
//            IMainData main = buildMain();
//            if(!JXTandemTestConfiguration.isDatabaseAccessible(main))
//                return;
//            JDBCTaxonomy tax = new JDBCTaxonomy(main);
//
//            SimpleJdbcTemplate template = tax.getTemplate();
//
//            TaxonomyDatabase db = new TaxonomyDatabase(template);
//
//            db.guaranteeTable("proteins");
//        }
//        catch (CannotAccessDatabaseException e) {
//            // this means teh database is not available
//
//        }
//
//    }
//
//    /**
//     * NOTE - this is VERY DANGEROUS - NOT REALLY to be used except in development
//     * NEW NOTE - this is innocuous - just guarantees my tables
//     */
//    @Test
//    public void testProteomicsCreate() {
//        try {
//            IMainData main = buildMain();
//            if(!JXTandemTestConfiguration.isDatabaseAccessible(main))
//                return;
//            JDBCTaxonomy tax = new JDBCTaxonomy(main);
//
//            SimpleJdbcTemplate template = tax.getTemplate();
//
//            TaxonomyDatabase db = new TaxonomyDatabase(template);
//
//            db.createDatabase();
//
//            db.guaranteeTable("proteins");
//        }
//         catch (CannotAccessDatabaseException e) {
//             // this means teh database is not available
//
//         }
//      }
//
//    /**
//     *
//     */
//    @Test
//    public void testDatabaseAccess() {
//        try {
//            IMainData main = buildMain();
//            if(!JXTandemTestConfiguration.isDatabaseAccessible(main))
//                return;
//
//            JDBCTaxonomy tax = new JDBCTaxonomy(main);
//
//            SimpleJdbcTemplate template = tax.getTemplate();
//
//            TaxonomyDatabase db = new TaxonomyDatabase(template);
//
//            //    db.createDatabase( );
//
//            db.guaranteeTable("proteins");
//        }
//         catch (CannotAccessDatabaseException e) {
//             // this means teh database is not available
//
//         }
//      }
//
//    protected static TestMain buildMain() {
//        TestMain main = new TestMain();
//
//        main.setParameter(SpringJDBCUtilities.DATA_HOST_PARAMETER, "cook");
//        main.setParameter(SpringJDBCUtilities.DATA_DATABASE_PARAMETER, "uniprothum");
//        main.setParameter(SpringJDBCUtilities.DATA_USER_PARAMETER, "proteomics");
//        main.setParameter(SpringJDBCUtilities.DATA_PASSWORD_PARAMETER, "tandem");
//        main.setParameter(SpringJDBCUtilities.DATA_DRIVER_CLASS_PARAMETER, "com.mysql.jdbc.Driver");
//        return main;
//    }

}
