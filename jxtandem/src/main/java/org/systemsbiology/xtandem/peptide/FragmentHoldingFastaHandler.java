package org.systemsbiology.xtandem.peptide;

import org.systemsbiology.xtandem.*;
import org.systemsbiology.xtandem.taxonomy.*;

import java.util.*;

/**
 * org.systemsbiology.xtandem.peptide.FragmentHoldingFastaHandler
 * This class accumulates proteins and peptide fragments - useful primarily for
 * smaller problems and testing
 * User: Steve
 * Date: 8/1/11
 */
public class FragmentHoldingFastaHandler implements IFastaHandler {
    public static final FragmentHoldingFastaHandler[] EMPTY_ARRAY = {};


    private final IMainData m_Tandem;
    private int m_ProteinIndex;
    private int m_FragmentIndex;
    private final FastaParser m_Parser = new FastaParser();
    private String m_Modifications = ""; // TEST_STRING;


    private final Map<String, IProtein> m_Protein = new HashMap<String, IProtein>();
    private final Map<String, IPolypeptide> m_Fragment = new HashMap<String, IPolypeptide>();
    private final Map<Integer, List<IPolypeptide>> m_FragmentsAtMass = new HashMap<Integer, List<IPolypeptide>>();


    public FragmentHoldingFastaHandler(final IMainData pTandem) {
        m_Tandem = pTandem;
        m_Parser.addHandler(this);
    }

    public String getModifications() {
        return m_Modifications;
    }

    public void setModifications(final String pModifications) {
        m_Modifications = pModifications;
    }

    public void addFragment(IPolypeptide added) {

        String key = added.toString();
        if (m_Fragment.containsKey(key))
            return;
        m_Fragment.put(key, added);
    }


    public IPolypeptide[] getFragments() {
        return m_Fragment.values().toArray(new IPolypeptide[0]);
    }


    public IPolypeptide[] getModifiedFragments() {
        List<IModifiedPeptide> holder = new ArrayList<IModifiedPeptide>();
        IPolypeptide[] pps = getFragments();
        for (int i = 0; i < pps.length; i++) {
            IPolypeptide pp = pps[i];
            if (pp instanceof IModifiedPeptide)
                holder.add((IModifiedPeptide) pp);
        }
        IModifiedPeptide[] ret = new IModifiedPeptide[holder.size()];
        holder.toArray(ret);
        return ret;
    }

    public IPolypeptide[] getUnmodifiedFragments() {
        List<IPolypeptide> holder = new ArrayList<IPolypeptide>();
        IPolypeptide[] pps = getFragments();
        for (int i = 0; i < pps.length; i++) {
            IPolypeptide pp = pps[i];
            if (pp instanceof IModifiedPeptide)
                continue;
            holder.add(pp);
        }
        IPolypeptide[] ret = new IPolypeptide[holder.size()];
        holder.toArray(ret);
        return ret;
    }

    public IPolypeptide getFragment(String key) {
        return m_Fragment.get(key);
    }


    public void addProtein(IProtein added) {
        m_Protein.put(added.getId(), added);
    }


    public IProtein[] getProteins() {
        return m_Protein.values().toArray(new IProtein[0]);
    }

    public IProtein getProtein(String key) {
        return m_Protein.get(key);
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


    public IMainData getTandem() {
        return m_Tandem;
    }

    public IPeptideDigester getDigester() {
        return getTandem().getDigester();
    }


    @Override
    public void handleProtein(String annotation, final String sequence) {

        int proteinIndex = getAndIncrementProteinIndex();
        // no duplicates
        if (m_Protein.containsKey(annotation))
            return;
   //     IProtein prot = Protein.getProtein(Protein.getNextId(), annotation, sequence, null);
        IProtein prot = Protein.getProtein( annotation,annotation, sequence, null);
         m_Protein.put(annotation, prot);


        generateProteinFragments(prot);

    }

    public void generateProteinFragments(final IProtein pProt) {
        IPeptideDigester digester = getDigester();

        int startPosition = 0;
        String lastSequence = "";
        IPolypeptide[] pps = digester.digest(pProt);

        for (int i = 0; i < pps.length; i++) {
            IPolypeptide frag = pps[i];
            String seq = frag.getSequence();
            if (XTandemUtilities.ignoreSequence(frag.getSequence()))  // don't do length 1
                continue;

            addFragment(frag);
        }


        boolean semiTryptic = digester.isSemiTryptic();
        if (semiTryptic) {
            IPolypeptide[] semipps = digester.addSemiCleavages(pProt);
            for (int i = 0; i < semipps.length; i++) {
                IPolypeptide semipp = semipps[i];
                if (XTandemUtilities.ignoreSequence(semipp.getSequence()))  // don't do length 1
                    continue;

                String seq = semipp.getSequence();
                if("CKERNLLESVIL".equals(seq))  {
                    double mass = semipp.getMass();
                    double mass2 = semipp.getMatchingMass();
                    XTandemUtilities.breakHere();
                   }

                addFragment(semipp);

            }
        }
        generateModifications();
    }

    public void generateModifications() {
        PeptideModification[] pms = PeptideModification.parsePeptideModifications(getModifications(),PeptideModificationRestriction.Global,false);
  //      if (pms.length == 0)
  //          return;
        IPolypeptide[] fragments = getFragments();
        guaranteeModifications(pms, fragments);
    }

    protected void guaranteeModifications(final PeptideModification[] pPms, IPolypeptide[] fragments) {
        int count = 0;
        for (IPolypeptide peptide : fragments) {
            String sequence = peptide.getSequence();
            if("QSVIGSPEAQSKRV".equals(sequence))
                XTandemUtilities.breakHere();
            IModifiedPeptide[] modifications = ModifiedPolypeptide.buildModifications(peptide, pPms);
            for (int i = 0; i < modifications.length; i++) {
                IModifiedPeptide modification = modifications[i];
                addFragment(modification);
            }
        }
    }

    public synchronized void generateFragmentsAtMass() {
        if (!m_FragmentsAtMass.isEmpty())
            return; // already done (I guess
        IPolypeptide[] fragments = getFragments();
        for (int i = 0; i < fragments.length; i++) {
            IPolypeptide fragment = fragments[i];

            String sequence = fragment.getSequence();
            if("YREERNADSGLC".equals(sequence))
                XTandemUtilities.breakHere();
            if("QSVIGSPEAQSKRV".equals(sequence))
                XTandemUtilities.breakHere();

            double mass = fragment.getMatchingMass();
            double addedMass =   XTandemUtilities.getAddedMass();
            int IMass = new Integer((int) (mass + addedMass ));
            addFragmentAtMass(IMass, fragment);

        }
    }

    protected void addFragmentAtMass(final int pIMass, final IPolypeptide pFragment) {
        List<IPolypeptide> list = m_FragmentsAtMass.get(pIMass);
        if (list == null) {
            list = new ArrayList<IPolypeptide>();
            m_FragmentsAtMass.put(pIMass, list);
        }


        list.add(pFragment);
    }

    public Integer[] getAvailableMasses() {
        Integer[] ret = m_FragmentsAtMass.keySet().toArray(new Integer[0]);
        Arrays.sort(ret);
        return ret;
    }

    public IPolypeptide[] getFragmentsAtMass(final int pIMass) {
        List<IPolypeptide> list = m_FragmentsAtMass.get(pIMass);
        if (list == null) {
            return IPolypeptide.EMPTY_ARRAY;
        }
        return list.toArray(IPolypeptide.EMPTY_ARRAY);
    }

}
