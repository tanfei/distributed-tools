package org.systemsbiology.xtandem.taxonomy;

import com.lordjoe.utilities.*;
import org.systemsbiology.xtandem.*;
import org.systemsbiology.xtandem.peptide.*;

import javax.sql.*;
import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.prefs.*;

//import org.apache.commons.dbcp.*;
//import org.springframework.jdbc.*;
//import org.springframework.jdbc.core.simple.*;
///**
// * org.systemsbiology.xtandem.taxonomy.SpringJDBCUtilities
// * User: Steve
// * Date: Apr 7, 2011
// */
//public class SpringJDBCUtilities {
//    public static final SpringJDBCUtilities[] EMPTY_ARRAY = {};
//
//    public static final ParameterizedRowMapper<String> STRING_MAPPER = new StringMapper();
//    public static final ParameterizedRowMapper<IProtein> PROTEIN_MAPPER = new ProteinMapper();
//    public static final ParameterizedRowMapper<IPolypeptide> PEPTIDE_MAPPER = new PeptideFragmentMapper();
//    public static final ParameterizedRowMapper<IModifiedPeptide> MODIFIED_PEPTIDE_MAPPER = new ModifiedPeptideFragmentMapper();
//    public static final ParameterizedRowMapper<FieldDescription> FIELD_MAPPER = new FieldDescriptionMapper();
//    private static boolean gUsingMySQL = false;
//
//    public static boolean isUsingMySQL() {
//        return gUsingMySQL;
//    }
//
//    public static void setUsingMySQL(final boolean pUsingMySQL) {
//        gUsingMySQL = pUsingMySQL;
//    }
//
//    private static final Map<Class<?>, ParameterizedRowMapper> gMappers = new HashMap<Class<?>, ParameterizedRowMapper>();
//
//    public static <T> ParameterizedRowMapper<T> getMapper(Class<T> cls) {
//        return (ParameterizedRowMapper<T>) gMappers.get(cls);
//    }
//
//    /**
//     * create a table if it dies not exist
//     *
//     * @param template  !null dbaccessor
//     * @param tableName !null table name
//     * @param creator   !null statement to create table
//     */
//    public static void guaranteeTable(SimpleJdbcTemplate template, String tableName, String creator) {
//        try {
//            List<SpringJDBCUtilities.FieldDescription> fields = template.query("describe " + tableName, SpringJDBCUtilities.FIELD_MAPPER);
//            if (fields.size() > 0)
//                return;
//            template.update(creator);
//        }
//        catch (BadSqlGrammarException ex) {
//            template.update(creator);
//        }
//
//    }
//
//
//    public static void guaranteeDatabase(String host, String database) {
//        if (isDatabaseExistant(host, database))
//            return;
//        authorizeUser(host, database);
//    }
//
//
//    public static boolean isDatabaseExistant(String host, String database) {
//        SimpleJdbcTemplate template = getRootTemplate(host);
//        List<String> found = template.query("SELECT SCHEMA_NAME FROM INFORMATION_SCHEMA.SCHEMATA WHERE SCHEMA_NAME = ?",
//                STRING_MAPPER, database);
//        if (found.size() == 1 && database.equalsIgnoreCase(found.get(0)))
//            return true;
//        return false;
//    }
//
//
//    public static void dropDatabase(String host, String database) {
//        SimpleJdbcTemplate template = getRootTemplate(host);
//        template.update("DROP DATABASE IF EXISTS " + database);
//    }
//
//    public static void authorizeUser(String host, String database) {
//        SimpleJdbcTemplate tp = SpringJDBCUtilities.getRootTemplate(host);
//        tp.update("CREATE DATABASE IF NOT EXISTS " + database);
//        tp.update("grant all on " + database + ".* TO proteomics@'%' identified by \'tandem\'");
//        // this is needed to load a file
//        tp.update("grant file on " + "*.* TO proteomics@'%' identified by \'tandem\'");
//    }
//
//
//    /**
//     * return true if a table exists
//     *
//     * @param template  !null dbaccessor
//     * @param tableName !null table name
//     * @return true of the table exists
//     */
//    public boolean isTablePresent(SimpleJdbcTemplate template, String tableName) {
//        String[] tables = listTables(template);
//        for (int i = 0; i < tables.length; i++) {
//            String table = tables[i];
//            if (table.equalsIgnoreCase(tableName))
//                return true;
//        }
//        return false;
//    }
//
//
//    /**
//     * return a list of tables in the default database
//     *
//     * @param template !null active template
//     * @return !null list of table names
//     */
//    public static String[] listTables(SimpleJdbcTemplate template) {
//        String query = "show Tables";
//        return queryForStrings(template, query);
//    }
//
//    /**
//     * make a query which returns a single column usually a string
//     *
//     * @param template !null active template
//     * @param pQuery   !null query
//     * @return !null array of results
//     */
//    public static String[] queryForStrings(final SimpleJdbcTemplate template, final String pQuery, Object... data) {
//        List<String> fields = template.query(pQuery, STRING_MAPPER, data);
//        return fields.toArray(new String[0]);
//    }
//
//
//    public static <T> void registerMapper(Class<T> cls, ParameterizedRowMapper<T> mapper) {
//        gMappers.put(cls, mapper);
//    }
//
////    private static DataSource gDataSource;
////
////    public static synchronized DataSource getDataSource() {
////        if (gDataSource == null) {
////            DataSource ds = null; //new DataSource()
////            throw new UnsupportedOperationException("Fix This"); // ToDo
////        }
////        return gDataSource;
////    }
////
//
//    /**
//     * version passing in the class to lookup
//     *
//     * @param holder template holder
//     * @param sql    sql statement - expecting one parameter
//     * @param id     as an Object
//     * @param cls    !null target class
//     * @param <T>    target type
//     * @return possibly null target
//     */
//    public static <T> T getItemWithId(ITemplateHolder holder, String sql, Object id, ParameterizedRowMapper<T> mapper) {
//        SimpleJdbcTemplate template = holder.getTemplate();
//
//        return template.queryForObject(sql, mapper, id);
//    }
//
//    /**
//     * version passing in the class to lookup
//     *
//     * @param holder template holder
//     * @param sql    sql statement - expecting one parameter
//     * @param id     as an Object
//     * @param cls    !null target class
//     * @param <T>    target type
//     * @return possibly null target
//     */
//    public static <T> T getItemWithId(ITemplateHolder holder, String sql, Object id, Class<T> cls) {
//        return getItemWithId(holder, sql, id, getMapper(cls));
//    }
//
//
//    /**
//     * version passing in the class to lookup
//     *
//     * @param holder template holder
//     * @param sql    sql statement - expecting one parameter
//     * @param id     as an Object
//     * @param cls    !null target class
//     * @param <T>    target type
//     * @return possibly null target
//     */
//    public static <T> List<T> getObjectsSatisfying(ITemplateHolder holder, String sql, ParameterizedRowMapper<T> mapper, Object... data) {
//        SimpleJdbcTemplate template = holder.getTemplate();
//
//        return template.query(sql, mapper, data);
//    }
//
//    /**
//     * version passing in the class to lookup
//     *
//     * @param holder template holder
//     * @param sql    sql statement - expecting one parameter
//     * @param id     as an Object
//     * @param cls    !null target class
//     * @param <T>    target type
//     * @return possibly null target
//     */
//    public static <T> List<T> getObjectsSatisfying(ITemplateHolder holder, String sql, Class<T> cls, Object... data) {
//        return getObjectsSatisfying(holder, sql, getMapper(cls), data);
//    }
//
//    public static final String DATA_HOST_PARAMETER = "org.systemsbiology.xtandem.Datasource.Host";
//    public static final String DATA_DATABASE_PARAMETER = "org.systemsbiology.xtandem.Datasource.Database";
//    public static final String DATA_USER_PARAMETER = "org.systemsbiology.xtandem.Datasource.User";
//    public static final String DATA_PASSWORD_PARAMETER = "org.systemsbiology.xtandem.Datasource.Password";
//    public static final String DATA_DRIVER_CLASS_PARAMETER = "org.systemsbiology.xtandem.Datasource.DriverClass";
//
//
//    public static String buildConnectionString(final IParameterHolder holder) {
//        String host = holder.getParameter(DATA_HOST_PARAMETER);
//        String database = holder.getParameter(DATA_DATABASE_PARAMETER);
//        String connString = "jdbc:mysql://" + host + "/";
//        if (database != null)
//            connString += database;
//        return connString;
//    }
//
//    public static DataSource buildDataSource(final IParameterHolder holder) {
//        String user = holder.getParameter(DATA_USER_PARAMETER);
//        String password = holder.getParameter(DATA_PASSWORD_PARAMETER);
//        String driverclass = holder.getParameter(DATA_DRIVER_CLASS_PARAMETER);
//        BasicDataSource ret = new BasicDataSource();
//
//        String connString = buildConnectionString(holder);
//
//        ret.setUrl(connString);
//        ret.setDriverClassName(driverclass);
//        ret.setUsername(user);
//        ret.setPassword(password);
//
//        try {
//            Connection connection = ret.getConnection();
//            connection.close();
//        }
//        catch (SQLException e) {
//            throw new CannotAccessDatabaseException("cannot use connection " + connString + " as user " + user);
//
//        }
//        return ret;
//    }
//
//    public static class ProteinMapper implements ParameterizedRowMapper<IProtein> {
//        private ProteinMapper() {
//        }
//
//        @Override
//        public IProtein mapRow(final ResultSet rs, final int i) throws SQLException {
//            int id = rs.getInt("id");
//            String sequence = rs.getString("sequence");
//            sequence = cleanSequence(sequence);
//            String annotation = rs.getString("annotation");
//            IProtein ret = Protein.getProtein(  annotation, sequence, "");
//
//            return ret;
//        }
//
//    }
//
//    /**
//     * we should not need this but I suppose it is safe
//     *
//     * @param pSequence
//     * @return
//     */
//    public static String cleanSequence(final String pSequence) {
//        StringBuilder sb = new StringBuilder();
//
//        for (int i = 0; i < pSequence.length(); i++) {
//            char c = Character.toUpperCase(pSequence.charAt(i));
//            if (!Character.isWhitespace(c))
//                sb.append(c);
//
//        }
//        return sb.toString();
//    }
//
//
//    /**
//     * handle a result which is a single String
//     */
//    public static class StringMapper implements ParameterizedRowMapper<String> {
//        private StringMapper() {
//        }
//
//        @Override
//        public String mapRow(final ResultSet rs, final int i) throws SQLException {
//            return rs.getString(1);
//        }
//    }
//
//
//    public static class FieldDescriptionMapper implements ParameterizedRowMapper<FieldDescription> {
//        private FieldDescriptionMapper() {
//        }
//
//        @Override
//        public FieldDescription mapRow(final ResultSet rs, final int i) throws SQLException {
//            FieldDescription ret = new FieldDescription();
//            ret.setField(rs.getString("field"));
//            ret.setType(rs.getString("type"));
//            ret.setKey(rs.getString("key"));
//            return ret;
//        }
//    }
//
//    public static class FieldDescription {
//        private String m_Field;
//        private String m_Type;
//        private String m_Null;
//        private String m_Key;
//        private String m_Default;
//        private String m_Extra;
//
//        public String getField() {
//            return m_Field;
//        }
//
//        public void setField(final String pField) {
//            m_Field = pField;
//        }
//
//        public String getType() {
//            return m_Type;
//        }
//
//        public void setType(final String pType) {
//            m_Type = pType;
//        }
//
//        public String getNull() {
//            return m_Null;
//        }
//
//        public void setNull(final String pNull) {
//            m_Null = pNull;
//        }
//
//        public String getKey() {
//            return m_Key;
//        }
//
//        public void setKey(final String pKey) {
//            m_Key = pKey;
//        }
//
//        public String getDefault() {
//            return m_Default;
//        }
//
//        public void setDefault(final String pDefault) {
//            m_Default = pDefault;
//        }
//
//        public String getExtra() {
//            return m_Extra;
//        }
//
//        public void setExtra(final String pExtra) {
//            m_Extra = pExtra;
//        }
//    }
//
//    public static class PeptideFragmentMapper implements ParameterizedRowMapper<IPolypeptide> {
//        private PeptideFragmentMapper() {
//        }
//
//        @Override
//        public IPolypeptide mapRow(final ResultSet rs, final int i) throws SQLException {
//            int id = rs.getInt("mz");
//            String sequence = rs.getString("sequence");
//            double realMass = rs.getDouble("real_mass");
//            int missedCleavages = rs.getInt("missed_cleavages");
//            sequence = cleanSequence(sequence);
//            Polypeptide ret;
//            if (sequence.contains("[")) {
//                  ret = (Polypeptide)ModifiedPolypeptide.fromModifiedString(sequence, missedCleavages);
//            }
//            else {
//                 ret = new Polypeptide(sequence, missedCleavages);
//            }
//            ret.setMatchingMass(realMass);
//            ret.setMissedCleavages(missedCleavages);
//            return ret;
//        }
//    }
//
//    public static class ModifiedPeptideFragmentMapper implements ParameterizedRowMapper<IModifiedPeptide> {
//        private ModifiedPeptideFragmentMapper() {
//        }
//
//        @Override
//        public IModifiedPeptide mapRow(final ResultSet rs, final int i) throws SQLException {
//            int id = rs.getInt("mz");
//            String sequence = rs.getString("sequence");
//            String modified_sequence = rs.getString("modified_sequence");
//            String modification = rs.getString("modification");
//            double realMass = rs.getDouble("real_mass");
//            int missedCleavages = rs.getInt("missed_cleavages");
//            sequence = cleanSequence(sequence);
//            PeptideModification[] mods = PeptideModification.fromModificationString(sequence, modification);
//            ModifiedPolypeptide ret = new ModifiedPolypeptide(sequence, 0, mods); // todo get missed cleavages
//            ret.setMatchingMass(realMass);
//            ret.setMissedCleavages(missedCleavages);
//            return ret;
//        }
//    }
//
//
//    /**
//     * return the last id for the table
//     *
//     * @param holder template holder
//     * @param table  !null existing table
//     * @return last id
//     */
//    public static int getLastId(ITemplateHolder holder, String table) {
//        SimpleJdbcTemplate template = holder.getTemplate();
//        if (isUsingMySQL())
//            return template.queryForInt("SELECT LAST_INSERT_ID()");
//
//        throw new UnsupportedOperationException("getLastId unlo works for myslql");
//    }
//
//
//    public static String getRootPassword(String host) {
//        if ("asterix".equalsIgnoreCase(host))
//            return "admin";
//        if ("auburn".equalsIgnoreCase(host))
//            return "admin";
//        if ("cook".equalsIgnoreCase(host))
//            return "alaron"; // this uses a lordjoe account
//        //return "j4m35c0ok";
//        Preferences prefs = Preferences.userNodeForPackage(SpringJDBCUtilities.class);
//        String hostRootPassword = host + "_root_password";
//        String def = "<none>";
//        String ret = prefs.get(hostRootPassword, def);
//        if (def.equals(ret))
//            throw new UnsupportedOperationException("Unknown host " + host + " run SpringJDBCUtilities.setPassword(host,password");
//        return Encrypt.decryptString(ret);
//    }
//
//    public static void setHostPassword(String host, String passwordInClear) {
//        Preferences prefs = Preferences.userNodeForPackage(SpringJDBCUtilities.class);
//        String hostRootPassword = host + "_root_password";
//        prefs.put(hostRootPassword, Encrypt.encryptString(passwordInClear));
//    }
//
//    public static DataSource getRootDataSource(String host) {
//        AbstractParameterHolder holder = new AbstractParameterHolder();
//        holder.setParameter(DATA_HOST_PARAMETER, host);
//        holder.setParameter(DATA_USER_PARAMETER, "root");
//        // hack to get root privilegse remotely
//        if ("cook".equals(host))
//            holder.setParameter(DATA_USER_PARAMETER, "lordjoe");
//        String pw = getRootPassword(host);
//        holder.setParameter(DATA_PASSWORD_PARAMETER, pw);
//        holder.setParameter(DATA_DRIVER_CLASS_PARAMETER, "com.mysql.jdbc.Driver");
//        return SpringJDBCUtilities.buildDataSource(holder);
//    }
//
//
//    public static SimpleJdbcTemplate getRootTemplate(String host) {
//        DataSource ds = getRootDataSource(host);
//        return new SimpleJdbcTemplate(ds);
//    }
//
//
//    // at 20 * 1000 * 1000  this kicks in alot at 2000 * 1000 * 1000 almost never
//    public static final long MAX_SINGLE_LOAD = 2000 * 1000 * 1000;
//    //  http://kevin.vanzonneveld.net/techblog/article/improve_mysql_insert_performance/
//    public static final long MAX_SINGLE_LOAD_LINES = 5 * 1000;
//
//    public static void loadTable(SimpleJdbcTemplate db, File filename, String table) {
//        if (filename.length() < MAX_SINGLE_LOAD) {
//            String adjFileName = filename.getAbsolutePath().replace("\\", "/");
//            db.update("LOAD Data LOCAL INFILE \'" + adjFileName + "\' into table " + table);
//        }
//        else {
//            File[] split = splitFile(filename, (long) (MAX_SINGLE_LOAD * 0.8));
//            ElapsedTimer et = new ElapsedTimer();
//            db.update("ALTER TABLE " + table + " DISABLE KEYS;");
//            for (int i = 0; i < split.length; i++) {
//                File file = split[i];
//                String adjFileName = file.getAbsolutePath().replace("\\", "/");
//                db.update(
//                        //      "UNIQUE_CHECKS=0;" +
//                        "LOAD Data LOCAL INFILE \'" + adjFileName + "\' into table " + table
//                        //     + "UNIQUE_CHECKS=1;"
//                );
//                if (i % 50 == 0)
//                    et.showElapsed("loaded part " + i);
//                et.reset();
//            }
//            db.update("ALTER TABLE " + table + " ENABLE KEYS;");
//        }
//    }
//
//    public static void loadRemoteTable(SimpleJdbcTemplate db, String remoteDirectory, String filename, String table) {
//        if (filename.length() < MAX_SINGLE_LOAD) {
//            String adjFileName = remoteDirectory + "/" + filename;
//            String command = "LOAD Data   INFILE \'" + adjFileName + "\' into table " + table;
//            db.update(command);
//        }
//    }
//
//    public static File[] splitFile(final File file, final long maxLength) {
//        long readLength = file.length();
//        if (readLength < maxLength) {
//            File[] ret = {file};
//            return ret;
//        }
//        List<File> holder = new ArrayList<File>();
//        int inputLines = 0;
//        try {
//            LineNumberReader inp = new LineNumberReader(new FileReader(file));
//            int index = 1;
//            // Create temp file.
//            File temp = File.createTempFile("tmp" + index++, null);
//            PrintWriter out = new PrintWriter(new FileWriter(temp));
//            holder.add(temp);
//            // Delete temp file when program exits.
//            temp.deleteOnExit();
//
//            String line = inp.readLine();
//            while (line != null) {
//                out.println(line);
//                inputLines++;
//                if (inputLines > MAX_SINGLE_LOAD_LINES) {
//                    out.close();
//                    inputLines = 0;
//                    temp = File.createTempFile("tmp" + index++, null);
//                    out = new PrintWriter(new FileWriter(temp));
//                    holder.add(temp);
//                    // Delete temp file when program exits.
//                    temp.deleteOnExit();
//                }
//                line = inp.readLine();
//            }
//            out.close();
//            if (inputLines == 0) {
//                holder.remove(temp);
//            }
//
//
//            File[] ret = new File[holder.size()];
//            holder.toArray(ret);
//            return ret;
//        }
//        catch (IOException e) {
//            throw new RuntimeException(e);
//
//        }
//
//
//    }
//
//    public static void main(String[] args) {
////        setHostPassword("asterix","admin");
////        setHostPassword("auburn","admin");
////        setHostPassword("localhost","admin");
//
//        String host = args[0];
//        String password = args[1];
//        setHostPassword(host, password);
//    }
//
//}
