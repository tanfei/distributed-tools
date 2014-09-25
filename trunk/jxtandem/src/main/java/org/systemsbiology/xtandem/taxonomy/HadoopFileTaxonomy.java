package org.systemsbiology.xtandem.taxonomy;

import org.apache.hadoop.conf.*;
import org.apache.hadoop.fs.*;
import org.systemsbiology.xtandem.*;
import org.systemsbiology.xtandem.hadoop.*;
import org.systemsbiology.xtandem.peptide.*;
import org.systemsbiology.xtandem.sax.*;
import org.systemsbiology.xtandem.scoring.*;

import java.io.*;
import java.util.*;

/**
 * org.systemsbiology.xtandem.taxonomy.Taxonomy
 *
 * @author Steve Lewis
 * @date Jan 7, 2011
 */
public class HadoopFileTaxonomy implements ITaxonomy {
    public static ITaxonomy[] EMPTY_ARRAY = {};
    public static Class THIS_CLASS = HadoopFileTaxonomy.class;

    private final String m_Organism;
    private final Configuration m_Conf;
    private IPeptideDigester m_Digester;
    //    private final List<IProtein> m_Proteins = new ArrayList<IProtein>();
    private Map<String, IProtein> m_IdToProtein = new HashMap<String, IProtein>();

    private final IMainData m_Tandem;

    public HadoopFileTaxonomy(IMainData tandem, String pOrganism, Configuration conf) {
        m_Tandem = tandem;
        m_Organism = pOrganism;
        m_Conf = conf;
        TaxonHandler taxonHandler = new TaxonHandler(null, "peptide", pOrganism);
        // might be null in test code
//        if (m_DescriptiveFile != null) {
//            InputStream is = tandem.open(m_DescriptiveFile);
//            String[] files = XTandemUtilities.parseFile(is, taxonHandler, m_DescriptiveFile);
//            setTaxomonyFiles(files);
//
//        }
//        else {
//            String[] files = { pOrganism };
//            setTaxomonyFiles(files);
//        }
    }
//
//
//    public void addProtein(IProtein p) {
//        m_Proteins.add(p);
//        String id = p.getId();
//        // do we want this
//        m_IdToProtein.put(id, p);
//    }


    @Override
    public void setDigester(final IPeptideDigester digester) {
        m_Digester = digester;
    }

    @Override
    public IPeptideDigester getDigester() {
        if (m_Digester != null)
            return m_Digester;
        return PeptideBondDigester.getDefaultDigester();
    }


    public IMainData getTandem() {
        return m_Tandem;
    }


    @Override
    public IPolypeptide[] getPeptidesOfMass(final double scanmass) {
        return getPeptidesOfMass(scanmass, false);
    }

    /**
     * retrieve all peptides matching a specific mass
     *
     * @param scanmass
     * @param isSemi   if true get semitryptic masses
     * @return
     */
    @Override
    public IPolypeptide[] getPeptidesIntegerOfMZ(final int scanmass, final boolean isSemi) {
        IPolypeptide[] pps = getPeptidesOfMass(scanmass, isSemi);
        List<IPolypeptide> holder = new ArrayList<IPolypeptide>();
        for (int i = 0; i < pps.length; i++) {
            IPolypeptide pp = pps[i];
            if ((int) pp.getMatchingMass() == scanmass)
                holder.add(pp);
            // todo handle other charge states
        }
        IPolypeptide[] ret = new IPolypeptide[holder.size()];
        holder.toArray(ret);
        return ret;
    }

    /**
     * given an id get the corresponding protein
     *
     * @param key
     * @return
     */
    @Override
    public IProtein getProteinById(final String key) {
        if (true)
            throw new UnsupportedOperationException("Fix This"); // ToDo
        return null;
    }

    /**
     * find the first protein with this sequence and return the corresponding id
     *
     * @param sequence
     * @return
     */
    @Override
    public String seqenceToID(final String sequence) {
        if (true)
            throw new UnsupportedOperationException("Fix This"); // ToDo
        return null;
    }

    /**
     * retrieve all peptides matching a specific mass
     *
     * @param scanmass
     * @return
     */
    @Override
    public IPolypeptide[] getPeptidesOfMass(final double scanmass, boolean isSemi) {

        //here for backward compatability
        if (getTandem() instanceof XTandemMain) {
            Scorer scorer = ((XTandemMain) getTandem()).getScoreRunner();
            return scorer.getPeptidesOfMass(scanmass);
        }
        throw new UnsupportedOperationException("Bad State");
    }


    public Configuration getConf() {
        return m_Conf;
    }

    /**
     * retrieve all peptides matching a specific mass
     *
     * @param scanmass
     * @param isSemi   if true get semitryptic masses
     * @return
     */
    @Override
    public IPolypeptide[] getPeptidesOfExactMass(final int scanmass, final boolean isSemi) {

        Path path = XTandemHadoopUtilities.buildPathFromMass(scanmass, getTandem());
        String[] items = XTandemHadoopUtilities.readTextLines(path, getConf());
        if (items.length == 0)
            return IPolypeptide.EMPTY_ARRAY;

        List<IPolypeptide> holder = new ArrayList<IPolypeptide>();

        for (int i = 0; i < items.length; i++) {
            IPolypeptide pp = buildPeptideFromDatabaseString(items[i]);

            holder.add(pp);
        }
        IPolypeptide[] ret = new IPolypeptide[holder.size()];
        holder.toArray(ret);
        return ret;
    }

    public  IPolypeptide buildPeptideFromDatabaseString(final String pItem ) {
        String item = pItem;
        String[] values = item.split(",");
        String sequence = values[0];
        if (sequence.contains("-"))
            XTandemUtilities.breakHere();

        Polypeptide pp = (Polypeptide) Polypeptide.fromString(sequence);
        int missedCleavages = getDigester().probableNumberMissedCleavages(pp);
         pp.setMissedCleavages(missedCleavages);
        double mass = Double.parseDouble(values[1]);
        pp.setMatchingMass(mass);
        String[] proteinIds = values[3].split(";");
        IProteinPosition[] positions = new IProteinPosition[proteinIds.length];
        for (int i = 0; i < positions.length; i++) {
            positions[i] = new ProteinPosition(pp, proteinIds[i]);

        }
        pp.setContainedInProteins(positions);
        return pp;
    }


    /**
     * retrieve all peptides matching a specific mass
     *
     * @param scanmass
     * @return
     */
    @Override
    public IPolypeptide[] getPeptidesOfExactMass(MassPeptideInterval interval, boolean isSemi) {
        int scanmass = interval.getMass();
        if (interval.isUnlimited())
            return getPeptidesOfExactMass(scanmass, isSemi);
        Path path = XTandemHadoopUtilities.buildPathFromMass(scanmass, getTandem());
        LineNumberReader reader = null;
        try {
            reader = XTandemHadoopUtilities.openTextLines(path, getConf());
            if (reader == null)
                return IPolypeptide.EMPTY_ARRAY;
            IPolypeptide[] peptidesOfExactMass = buildReadPeptides(reader, interval, isSemi);

            return peptidesOfExactMass;
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        finally {
            if (reader != null)
                try {
                    reader.close();
                }
                catch (IOException e1) {
                    // throw new RuntimeException(e1);

                }
        }
    }

    public  IPolypeptide[] buildReadPeptides(final LineNumberReader pReader, final MassPeptideInterval pInterval,  final boolean pSemi) {
        List<IPolypeptide> holder = new ArrayList<IPolypeptide>();
        int numberLines = -1;
        try {
            boolean done = false;
            int start = pInterval.getStart();
            int end = pInterval.getEnd();
            String line = null;
            if (start > 0) {
                while (!done && numberLines++ < start) {
                    line = pReader.readLine();
                    if (line == null)
                        if (line == null) {
                            done = true;
                            break;
                        }
                }
            }
            while (!done && numberLines++ < end) {
                line = pReader.readLine();
                if (line == null) {
                    done = true;
                    break;
                }
                holder.add(buildPeptideFromDatabaseString(line ));
            }
            // at end of file
            if (line == null)
                XTandemUtilities.breakHere();

            IPolypeptide[] ret = new IPolypeptide[holder.size()];
            holder.toArray(ret);
            return ret;
        }
        catch (IOException e) {
            throw new RuntimeException(e);

        }
    }


    /**
     * given an id get the corresponding IPolypeptide
     *
     * @param key
     * @return
     */
    @Override
    public IPolypeptide getPeptideById(String key) {
        return Polypeptide.fromString(key);
    }


    @Override
    public IProtein[] getProteins() {
//        if (true)
        throw new UnsupportedOperationException("Fix This"); // ToDo
//        if(m_Proteins.isEmpty())
//             loadTaxonomyFiles();
//
//        return m_Proteins.toArray(IProtein.EMPTY_ARRAY);
    }

    @Override
    public IProtein[] getValidProteins() {
        IProtein[] prots = getProteins();
        List<IProtein> holder = new ArrayList<IProtein>();
        for (int i = 0; i < prots.length; i++) {
            IProtein prot = prots[i];
            if (!prot.isValid())
                continue;
            holder.add(prot);
        }
        IProtein[] ret = new IProtein[holder.size()];
        holder.toArray(ret);
        return ret;
    }

    @Override
    public String getOrganism() {
        return m_Organism;
    }
//
//    public void loadTaxonomyFiles() {
//        String[] taxomonyFiles = getTaxomonyFiles();
//        if(taxomonyFiles == null)
//            return; // nothing to do
//        for (String f : taxomonyFiles) {
//            readFasta(f);
//        }
//    }
//
//    public void readFasta(String is) {
//        IParameterHolder tandem = getTandem();
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
//        LineNumberReader inp = new LineNumberReader(new InputStreamReader(is));
//        StringBuilder sb = new StringBuilder();
//        String annotation = null;
//        int proteinIndex = 0;
//        try {
//            String line = inp.readLine();
//            if (line.startsWith("xbang-pro-fasta-format")) {
//                readFastaPro(inp);
//            }
//            while (line != null) {
//                if (line.startsWith(">")) {
//                    annotation = line.substring(1); // annotation is the rest o fthe line
//                    buildAndAddProtein(annotation, sb, url);
//                }
//                else {
//                    sb.append(line.trim()); // add to sequence
//                }
//                line = inp.readLine();
//            }
//        }
//        catch (IOException e) {
//            throw new RuntimeException(e);
//        }
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

    public void readFastaPro(LineNumberReader inp) {
        try {
            String line = inp.readLine();
            while (line != null) {
                buildAndAddFastaProProtein(line);
                line = inp.readLine();
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        finally {
            try {
                inp.close();
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }

    protected void buildAndAddFastaProProtein(String line) {
        if (line.length() == 0)
            return;
        String[] items = line.split("\t");
        for (int i = 0; i < items.length; i++) {
            String item = items[i];
            item = null;
        }
        //  addProtein(new Protein(this, annotation, sb.toString()));
    }


//    protected void buildAndAddProtein(String annotation, StringBuilder sb, String url) {
//        if (sb.length() == 0)
//            return;
//        addProtein(Protein.getProtein(annotation, sb.toString(), url));
//        sb.setLength(0);  // clear sequence
//    }

}
