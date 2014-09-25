package org.systemsbiology.xtandem.taxonomy;

//import org.springframework.jdbc.core.simple.*;
//import org.systemsbiology.xtandem.*;
//import org.systemsbiology.xtandem.peptide.*;
//import org.systemsbiology.xtandem.scoring.*;
//
//import java.io.*;
//import java.util.*;
//
///**
// * org.systemsbiology.xtandem.taxonomy.JDBCTaxonomy
// * gets protiens and fragments from a database
// * User: Steve
// * Date: Apr 7, 2011
// */
//public class JDBCTaxonomy implements ITaxonomy, ITemplateHolder {
//    public static final JDBCTaxonomy[] EMPTY_ARRAY = {};
//
//    public static final String PROTEINS_TABLE = "proteins";
//    public static final String PEPTIDES_TABLE = "peptides";
//    public static final String MASS_TO_PEPTIDES_TABLE = "mono_mz_to_fragments";
//    public static final String PEPTIDE_TO_PROTEIN_TABLE = "mono_mz_to_fragments";
//
//    // todo support average as well
//    public static final String MASS_TO_SEMI_PEPTIDES_TABLE = "semi_mono_mz_to_fragments";
//    public static final String SEMIPEPTIDE_TO_PROTEIN_TABLE = "semi_mono_mz_to_fragments";
//    public static final String MODIFIED_PEPTIDE_TO_PROTEIN_TABLE = "semi_mono_modified_mz_to_fragments";
//
//    public static final String GET_ALL_PROTEINS_STATEMENT = "select * from " + PROTEINS_TABLE;
//    public static final String GET_PROTEINS_STATEMENT = "select * from " + PROTEINS_TABLE + " WHERE  id = ?";
//    public static final String GET_PEPTIDES_STATEMENT = "select * from " + PEPTIDES_TABLE + " WHERE  id = ?";
//    public static final String GET_MASS_TO_PEPTIDES_STATEMENT = "select * from " + MASS_TO_PEPTIDES_TABLE + " WHERE mz  = ?";
//    public static final String GET_TO_PROTEIN_STATEMENT = "select * from " + PEPTIDE_TO_PROTEIN_TABLE + " WHERE  sequence  = ?";
//
//    public static final String GET_MASS_TO_SSEMIPEPTIDES_STATEMENT = "select * from " + MASS_TO_SEMI_PEPTIDES_TABLE + " WHERE mz  = ?";
//    public static final String GET_TO_SEMI_PROTEIN_STATEMENT = "select * from " + SEMIPEPTIDE_TO_PROTEIN_TABLE + " WHERE  sequence  = ?";
//
//    public static final String GET_MASS_TO_MODIFICATIONS_STATEMENT = "select * from " + MODIFIED_PEPTIDE_TO_PROTEIN_TABLE + " WHERE mz  = ?";
//
//    private final IMainData m_MainData;
//    private final SimpleJdbcTemplate m_Template;
//    private IPeptideDigester m_Digester;
//    private final int m_MaxMissedCleavages;
//    private double m_LastMeasuredMass;
//    private Map<Integer, IPolypeptide[]> m_CachedMasses = new HashMap<Integer, IPolypeptide[]>();
//
//    public JDBCTaxonomy(IMainData parent) {
//        m_MainData = parent;
//        m_Template = XTandemUtilities.templateFromParameters(parent);
//        m_MaxMissedCleavages = parent.getIntParameter("scoring, maximum missed cleavage sites",
//                0);
//    }
//
//    public int getMaxMissedCleavages() {
//        return m_MaxMissedCleavages;
//    }
//
//    public IMainData getMainData() {
//        return m_MainData;
//    }
//
//
//    /**
//     * return the associated template
//     *
//     * @return
//     */
//    public SimpleJdbcTemplate getTemplate() {
//        return m_Template;
//    }
//
//
//    /**
//     * given an id get the corresponding protein
//     *
//     * @param key
//     * @return
//     */
//    @Override
//    public IProtein getProteinById(final String key) {
//
//        Object[] args = {key};
//        SimpleJdbcTemplate template = getTemplate();
//        return template.queryForObject("SELECT * FROM PROTEINS WHERE id = ?", SpringJDBCUtilities.PROTEIN_MAPPER, args);
//    }
//
//    /**
//     * find the first protein with this sequence and return the corresponding id
//     *
//     * @param sequence
//     * @return
//     */
//    @Override
//    public String seqenceToID(final String sequence) {
//        return null;
//    }
//
//    @Override
//    public void setDigester(final IPeptideDigester digester) {
//        m_Digester = digester;
//    }
//
//    @Override
//    public IPeptideDigester getDigester() {
//        return m_Digester;
//    }
//
//    @Override
//    public IPolypeptide[] getPeptidesOfMass(final double scanmass) {
//        return getPeptidesOfMass(scanmass, false);
//    }
//
//    /**
//     * retrieve all peptides matching a specific mass
//     *
//     * @param scanmass
//     * @param isSemi   if true get semitryptic masses
//     * @return
//     */
//    @Override
//    public IPolypeptide[] getPeptidesIntegerOfMZ(final int scanmass, final boolean isSemi) {
//        IPolypeptide[] items = getPeptidesOfMassIndex(scanmass, isSemi);
//        return items;
//    }
//
//    /**
//     * retrieve all peptides matching a specific mass
//     *
//     * @param scanmass
//     * @return
//     */
//    @Override
//    public IPolypeptide[] getPeptidesOfMass(final double scanmass, boolean isSemi) {
//        if (m_LastMeasuredMass > scanmass)
//            throw new IllegalStateException("masses must be presented in order");
//        int maxMissed = getMaxMissedCleavages();
//        int[] masses = getScannedMasses(scanmass);
//        List<IPolypeptide> holder = new ArrayList<IPolypeptide>();
//        for (int i = 0; i < masses.length; i++) {
//            int mass = masses[i];
//            IPolypeptide[] items = getPeptidesOfMassIndex(mass, isSemi);
//            for (int q = 0; q < items.length; q++) {
//                IPolypeptide item = items[q];
//                if (item.getMissedCleavages() <= maxMissed)
//                    holder.add(item);
//            }
//            m_LastMeasuredMass = i;
//        }
//
//        IPolypeptide[] ret = new IPolypeptide[holder.size()];
//        holder.toArray(ret);
//        return ret;
//    }
//
//    /**
//     * retrieve all peptides matching a specific mass
//     *
//     * @param scanmass
//     * @return
//     */
//    @Override
//    public IPolypeptide[] getPeptidesOfExactMass(final int mass, boolean isSemi) {
//        if (m_LastMeasuredMass > mass)
//            throw new IllegalStateException("masses must be presented in order");
//        int maxMissed = getMaxMissedCleavages();
//        List<IPolypeptide> holder = new ArrayList<IPolypeptide>();
//        IPolypeptide[] items = getPeptidesOfMassIndex(mass, isSemi);
//        for (int q = 0; q < items.length; q++) {
//            IPolypeptide item = items[q];
//            if (item.getMissedCleavages() <= maxMissed)
//                holder.add(item);
//        }
//        m_LastMeasuredMass = mass;
//
//        IPolypeptide[] ret = new IPolypeptide[holder.size()];
//        holder.toArray(ret);
//        return ret;
//    }
//
//    /**
//     * retrieve all peptides matching a specific mass
//     *
//     * @param scanmass
//     * @return
//     */
//    @Override
//    public IPolypeptide[] getPeptidesOfExactMass(MassPeptideInterval interval, boolean isSemi) {
//        IPolypeptide[] peptidesOfExactMass = getPeptidesOfExactMass(interval.getMass(), isSemi);
//        if (interval.isUnlimited())
//            return peptidesOfExactMass;
//        List<IPolypeptide> holder = new ArrayList<IPolypeptide>();
//        for (int i = interval.getStart(); i < Math.min(peptidesOfExactMass.length, interval.getEnd()); i++) {
//            IPolypeptide peptide = peptidesOfExactMass[i];
//            holder.add(peptide);
//        }
//        IPolypeptide[] ret = new IPolypeptide[holder.size()];
//        holder.toArray(ret);
//        return ret;
//    }
//
//    /**
//     * retrieve all peptides matching a specific mass
//     *
//     * @param scanmass
//     * @return
//     */
//    public IPolypeptide[] getPeptidesOfMassIndex(final int scanmass, boolean isSemi) {
//        IPolypeptide[] retx = m_CachedMasses.get(scanmass);
//        if (retx != null)
//            return retx; // already cached
//        IPolypeptide[] ret = findPeptidesOfMassIndex(scanmass, isSemi);
//        m_CachedMasses.put(scanmass, ret); // cache this
//        return ret;
//    }
//
//
//    public IPolypeptide[] findPeptidesOfMassIndexWithoutModifications(final int scanmass, final boolean isSemi) {
//        int maxMissed = getMaxMissedCleavages();
//        SimpleJdbcTemplate template = getTemplate();
//        String query = null;
//        if (isSemi)
//            query = GET_MASS_TO_SSEMIPEPTIDES_STATEMENT;
//        else
//            query = GET_MASS_TO_PEPTIDES_STATEMENT;
//        Integer mass = scanmass; // convert to an object
//        List<IPolypeptide> plist = template.query(query, SpringJDBCUtilities.PEPTIDE_MAPPER, mass);
//        List<IPolypeptide> holder = new ArrayList<IPolypeptide>();
//        for (IPolypeptide pp : plist) {
//            if (pp.getMissedCleavages() <= maxMissed)
//                holder.add(pp);
//
//        }
//        IPolypeptide[] ret = new IPolypeptide[holder.size()];
//        holder.toArray(ret);
//        return ret;
//    }
//
//    public IPolypeptide[] findPeptidesOfMassIndex(final int scanmass, final boolean isSemi) {
//        int maxMissed = getMaxMissedCleavages();
//        Integer mass = scanmass; // convert to an object
//        SimpleJdbcTemplate template = getTemplate();
//        IPolypeptide[] unModified = findPeptidesOfMassIndexWithoutModifications(scanmass, isSemi);
//        List<IPolypeptide> holder = new ArrayList<IPolypeptide>(Arrays.asList(unModified));
//        String query = GET_MASS_TO_MODIFICATIONS_STATEMENT;
//        List<IModifiedPeptide> plist2 = template.query(query, SpringJDBCUtilities.MODIFIED_PEPTIDE_MAPPER, mass);
//        for (IPolypeptide pp : plist2) {
//            if (!isSemi && pp.hasMissedCleavages())
//                continue;
//            if (pp.getMissedCleavages() <= maxMissed)
//                holder.add(pp);
//
//        }
//
//        IPolypeptide[] ret = new IPolypeptide[holder.size()];
//        holder.toArray(ret);
//        return ret;
//    }
//
//    public IPolypeptide[] findPeptidesOfMassIndex(final MassPeptideInterval interval, final boolean isSemi) {
//        int maxMissed = getMaxMissedCleavages();
//        Integer mass = interval.getMass(); // convert to an object
//        SimpleJdbcTemplate template = getTemplate();
//        IPolypeptide[] unModified = findPeptidesOfMassIndexWithoutModifications(mass, isSemi);
//        List<IPolypeptide> holder = new ArrayList<IPolypeptide>(Arrays.asList(unModified));
//        String query = GET_MASS_TO_MODIFICATIONS_STATEMENT;
//        List<IModifiedPeptide> plist2 = template.query(query, SpringJDBCUtilities.MODIFIED_PEPTIDE_MAPPER, mass);
//        for (IPolypeptide pp : plist2) {
//            if (!isSemi && pp.hasMissedCleavages())
//                continue;
//            if (pp.getMissedCleavages() <= maxMissed)
//                holder.add(pp);
//
//        }
//
//        IPolypeptide[] peptidesOfMass = new IPolypeptide[holder.size()];
//        holder.toArray(peptidesOfMass);
//        if (interval.isUnlimited())
//            return peptidesOfMass;
//        holder.clear();
//        for (int i = interval.getStart(); i < Math.min(peptidesOfMass.length, interval.getEnd()); i++) {
//            IPolypeptide peptide = peptidesOfMass[i];
//            holder.add(peptide);
//        }
//        IPolypeptide[] ret = new IPolypeptide[holder.size()];
//        holder.toArray(ret);
//        return ret;
//    }
//
//
//    protected int[] getScannedMasses(final double scanmass) {
//        ITandemScoringAlgorithm scorer = getMainData().getScorer();
//        int[] limits = scorer.allSearchedMasses(scanmass);
//        cleanCacheddMasses(limits);
//        return limits;
//    }
//
//    protected void cleanCacheddMasses(int[] newCache) {
//        Integer[] current = m_CachedMasses.keySet().toArray(new Integer[0]);
//        for (int i = 0; i < current.length; i++) {
//            int test = current[i];
//            boolean used = false;
//            for (int j = 0; j < newCache.length; j++) {
//                if (test == newCache[j]) {
//                    used = true;
//                    break;
//                }
//
//            }
//            if (!used)
//                m_CachedMasses.remove(test); // not using this key
//        }
//    }
//
//    /**
//     * given an id get the corresponding IPolypeptide
//     *
//     * @param key
//     * @return
//     */
//    @Override
//    public IPolypeptide getPeptideById(final String key) {
//        return null;
//    }
//
//    @Override
//    public IProtein[] getProteins() {
//        SimpleJdbcTemplate template = getTemplate();
//        String query = GET_ALL_PROTEINS_STATEMENT;
//        List<IProtein> plist = template.query(query, SpringJDBCUtilities.PROTEIN_MAPPER);
//        IProtein[] ret = new IProtein[plist.size()];
//        plist.toArray(ret);
//        return ret;
//    }
//
//    @Override
//    public IProtein[] getValidProteins() {
//        return getProteins();
//    }
//
//    @Override
//    public String getOrganism() {
//        return null;
//    }
//
//    public void readFasta(String is) {
//        IParameterHolder tandem = getMainData();
//        InputStream describedStream = tandem.open(is);
//        // might list lots of data we do not have
//        if (describedStream != null) {
//            readFasta(describedStream, is);
//        }
//        else {
//            throw new IllegalArgumentException("bad Fasta file " + is);
//        }
//    }
//
//    public void readFasta(InputStream is, String url) {
//        System.gc();
//        long before = Runtime.getRuntime().freeMemory() / 1000;
//        SequenceTree peptides = new SequenceTree();
//        LineNumberReader inp = new LineNumberReader(new InputStreamReader(is));
//        StringBuilder sb = new StringBuilder();
//        String annotation = null;
//        int proteinIndex = 0;
//        int numberProteins = 0;
//        int numberPeptides = 0;
//        try {
//            String line = inp.readLine();
//            while (line != null) {
//                if (line.startsWith(">")) {
//                    annotation = line.substring(1); // annotation is the rest o fthe line
//                    numberProteins++;
//                    numberPeptides = handleProtein(annotation, sb, url, peptides, numberPeptides);
//                }
//
//                else {
//                    sb.append(line.trim()); // add to sequence
//                }
//                line = inp.readLine();
//
//                numberProteins++;
//                numberPeptides = handleProtein(annotation, sb, url, peptides, numberPeptides);
//            }
//            System.gc();
//            long after = Runtime.getRuntime().freeMemory() / 1000;
//            long used = before - after;
//            XTandemUtilities.outputLine("number Peptides = " + numberPeptides + " number Proteins " + numberProteins);
//            XTandemUtilities.outputLine("memory before = " + before + " after " + after + " used " + used);
//        }
//        catch (IOException ex) {
//            throw new RuntimeException(ex);
//        }
//
//        finally {
//            try {
//                is.close();
//            }
//            catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        }
//
//    }
//
//    protected int handleProtein(String annotation, StringBuilder sb, String url, SequenceTree peptides, int numberPeptides) {
//        if (sb.length() == 0)
//            return numberPeptides;
//        IProtein protein = Protein.getProtein(annotation, sb.toString(), url);
//        sb.setLength(0);  // clear sequence
//        IPeptideDigester digester = getDigester();
//        if (digester != null) {
//            IPolypeptide[] fragments = digester.digest(protein);
//            for (int i = 0; i < fragments.length; i++) {
//                IPolypeptide fragment = fragments[i];
//                String sequence = fragment.getSequence();
//                if (!sequence.contains("*")) {
//                    if (!peptides.guaranteePresent(sequence))
//                        numberPeptides++;
//                }
//
//            }
//
//        }
//        return numberPeptides;
//    }
//
//
//}
