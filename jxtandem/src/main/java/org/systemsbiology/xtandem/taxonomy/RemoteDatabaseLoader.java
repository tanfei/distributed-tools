package org.systemsbiology.xtandem.taxonomy;

//import org.springframework.jdbc.core.simple.*;
import org.systemsbiology.common.*;
import org.systemsbiology.remotecontrol.*;
import org.systemsbiology.xtandem.*;

import java.io.*;

/**
 * org.systemsbiology.xtandem.taxonomy.RemoteDatabaseLoader
 * User: steven
 * Date: 4/20/11
 */
public class RemoteDatabaseLoader
{
    public static final RemoteDatabaseLoader[] EMPTY_ARRAY = {};

//    public static void loadRemoteDatabase(SimpleJdbcTemplate db, String remoteDirectory,
//                                          String fileNameBase)
//    {
//
////        BasicDataSource ds = (BasicDataSource) XTandemUtilities.getDataSource();
////
////        if (ds == null)
////            throw new IllegalStateException("You have not created a data source");
//        //boolean oldAutoCommit = ds.getDefaultAutoCommit();
//        try {
////            ds.setDefaultAutoCommit(false);
//
//            ElapsedTimer et = new ElapsedTimer();
//            String load_Table = "proteins";
//            int numberProteins = db.queryForInt("select count(*) from " + load_Table);
//            if (numberProteins == 0) {
//                SpringJDBCUtilities.loadRemoteTable(db, remoteDirectory, fileNameBase.replace(".fasta", "proteins.sql"), load_Table);
//                et.showElapsed("Loaded Proteins in ");
//            }
//
//            load_Table = "load_fragments";
//            int numberFragments = db.queryForInt("select count(*) from " + load_Table);
//            if (numberFragments == 0) {
//                et.reset();
//                SpringJDBCUtilities.loadRemoteTable(db, remoteDirectory, fileNameBase.replace(".fasta", "fragments.sql"), load_Table);
//                et.showElapsed("Loaded Fragments in ");
//            }
//
//            int numberMono = db.queryForInt("select count(*) from mono_mz_to_fragments");
//            if (numberMono == 0) {
//                et.reset();
//                db.update("insert into mono_mz_to_fragments (select  imono_mass, sequence,max(mono_mass),missed_cleavages from  " + load_Table + "  where imono_mass  is not null   group by imono_mass, sequence )");
//                et.showElapsed("Loaded Mono in ");
//
//                et.reset();
//                db.update("insert into average_mz_to_fragments (select  iaverage_mass, sequence,max(average_mass),missed_cleavages from   " + load_Table + " where iaverage_mass  is not null   group by iaverage_mass, sequence )");
//                et.showElapsed("Loaded average in ");
//            }
//
//
//        }
//        finally {
//            //          ds.setDefaultAutoCommit(oldAutoCommit);
//
//        }
//    }
//
//    public static void loadRemoteDatabase2(SimpleJdbcTemplate db, String remoteDirectory,
//                                           String fileNameBase)
//    {
//
//        File inp = new File(fileNameBase.replace(".fasta", "proteins.sql"));
//
////        BasicDataSource ds = (BasicDataSource) XTandemUtilities.getDataSource();
////
////        if (ds == null)
////            throw new IllegalStateException("You have not created a data source");
//        //boolean oldAutoCommit = ds.getDefaultAutoCommit();
//        try {
////            ds.setDefaultAutoCommit(false);
//
//            ElapsedTimer et = new ElapsedTimer();
//            String load_Table = "proteins";
//            int numberProteins = db.queryForInt("select count(*) from " + load_Table);
//            if (numberProteins == 0) {
//                SpringJDBCUtilities.loadRemoteTable(db, remoteDirectory, fileNameBase.replace(".fasta", "proteins.sql"), load_Table);
//                et.showElapsed("Loaded Proteins in ");
//            }
//
//            load_Table = "load_fragments";
//            int numberFragments = db.queryForInt("select count(*) from " + load_Table);
//            if (numberFragments == 0) {
//                et.reset();
//                SpringJDBCUtilities.loadRemoteTable(db, remoteDirectory, fileNameBase.replace(".fasta", "fragments.sql"), load_Table);
//                // we have some problems when the inpt file is broken
//                db.update("delete from " + load_Table + " where imono_mass is  null  iaverage_mass is  null  ");
//                 et.showElapsed("Loaded Fragments in ");
//            }
//
//            int numberMono = db.queryForInt("select count(*) from mono_mz_to_fragments");
//            if (numberMono == 0) {
//                et.reset();
//                db.update("insert into mono_mz_to_fragments (select  imono_mass, sequence,max(mono_mass),missed_cleavages from  " + load_Table + " where imono_mass is not null  group by imono_mass, sequence )");
//                et.showElapsed("Loaded Mono in ");
//
//                et.reset();
//                db.update("insert into average_mz_to_fragments (select  iaverage_mass, sequence,max(average_mass),missed_cleavages from   " + load_Table + "  where iaverage_mass is not null group by iaverage_mass, sequence )");
//                et.showElapsed("Loaded average in ");
//            }
//
//            load_Table = "semi_load_fragments";
//            int numberSemiFragments = db.queryForInt("select count(*) from " + load_Table);
//            if (numberSemiFragments == 0) {
//                et.reset();
//                SpringJDBCUtilities.loadRemoteTable(db, remoteDirectory, fileNameBase.replace(".fasta", "semifragments.sql"), load_Table);
//                et.showElapsed("Loaded Semi Fragments in ");
//            }
//            numberMono = db.queryForInt("select count(*) from semi_mono_mz_to_fragments");
//            if (numberMono == 0) {
//                et.reset();
//                db.update("insert into semi_mono_mz_to_fragments (select  imono_mass, sequence,max(mono_mass),missed_cleavages from  " + load_Table + " where imono_mass is not null group by imono_mass, sequence  ) ");
//                et.showElapsed("Loaded mono in ");
//
//                et.reset();
//                db.update("insert into semi_average_mz_to_fragments (select  iaverage_mass, sequence,max(average_mass),missed_cleavages from   " + load_Table + " where iaverage_mass is not null group by iaverage_mass, sequence ) ");
//                et.showElapsed("Loaded average in ");
//            }
//
//        }
//        finally {
//            //          ds.setDefaultAutoCommit(oldAutoCommit);
//
//        }
//    }
//
//    public static void main(String[] args)
//    {
//        String pFileName = args[0];
//        String host = args[1];
//        String database = args[2];
//        String remoteDirectory = args[3];
//
//        /**
//         * specify clean to reload the database
//         */
//        if (true)
//            SpringJDBCUtilities.dropDatabase(host, database);
//
//        // we are having security issues and need not to check this
//        SpringJDBCUtilities.guaranteeDatabase(host, database);
//
//
//
//        IMainData main = FastaLoader.buildMain(host, database);
//
//        JDBCTaxonomy tax = new JDBCTaxonomy(main);
//        File proteins = new File(pFileName.replace(".fasta", "proteins.sql"));
//        File fragments = new File(pFileName.replace(".fasta", "fragments.sql"));
//        File semifragments = new File(pFileName.replace(".fasta", "semifragments.sql"));
//
//        FastaLoader fs = new FastaLoader(main, proteins, fragments, semifragments);
//        String user = RemoteUtilities.getUser();
//        String password = RemoteUtilities.getPassword();
//        IFileSystem rfs = new FTPWrapper(user, password, host);
//
//        IFileSystem local = new LocalFileSystem();
//        InputStream is = local.openFileForRead(pFileName);
//        ElapsedTimer et = new ElapsedTimer();
//    //    if (false)
//            fs.parseToRemote(is, rfs, remoteDirectory, pFileName);
//        et.showElapsed("Parsed " + pFileName);
//        et.reset();
//        SimpleJdbcTemplate template = tax.getTemplate();
//        TaxonomyDatabase tdb = new TaxonomyDatabase(template);
//        tdb.createDatabase();
//        //  fs.parse(new File(pFileName));
//
//        loadRemoteDatabase2(template, remoteDirectory, pFileName);
//        et.showElapsed(System.out);
//
//
//    }
//

}
