package org.systemsbiology.xtandem;

import java.util.*;

/**
 * org.systemsbiology.xtandem.ProtienDomain
 *
 * @author Steve Lewis
 * @date Dec 21, 2010
 */
/*
* maps to momain
 * mdomain objects contain information about an identified peptide sequence. each
 * identified peptide is refered to by a single mdomain object
 */
public class ProteinDomain
{
    public static ProteinDomain[] EMPTY_ARRAY = {};
    public static Class THIS_CLASS = ProteinDomain.class;

    private int m_lS; // the start position of the peptide in the protein sequence (N-terminus = 0)
    private int m_lE; // the end position of the peptide in the protein sequence
    private long m_lMissedCleaves; // missed cleavages
    private float m_fScore; // the convolution score for the peptide
    private float m_fHyper; // the hyper score for the peptide
    private double m_dMH; // the mass of the peptide + a proton
    // double m_dDelta replaces float m_fDelta, starting with version 2006.02.01
    // because of an issue with the accuracy of this value
    private double m_dDelta; // the mass difference between the mass of the peptide and the measured mass
    private boolean m_bUn;

    private Map<Long, Long> m_mapCount; // a map of the number of ions detected for each ion type
    private Map<Long, Float> m_mapScore; // a map of the convolution scores for each ion type
    private List<ModifiedAminoAcid> m_vAa; // vector of modified amino acids

    public int getlS() {
        return m_lS;
    }

    public void setlS(final int pLS) {
        m_lS = pLS;
    }

    public int getlE() {
        return m_lE;
    }

    public void setlE(final int pLE) {
        m_lE = pLE;
    }

    public long getlMissedCleaves() {
        return m_lMissedCleaves;
    }

    public void setlMissedCleaves(final long pLMissedCleaves) {
        m_lMissedCleaves = pLMissedCleaves;
    }

    public float getfScore() {
        return m_fScore;
    }

    public void setfScore(final float pFScore) {
        m_fScore = pFScore;
    }

    public float getfHyper() {
        return m_fHyper;
    }

    public void setfHyper(final float pFHyper) {
        m_fHyper = pFHyper;
    }

    public double getdMH() {
        return m_dMH;
    }

    public void setdMH(final double pDMH) {
        m_dMH = pDMH;
    }

    public double getdDelta() {
        return m_dDelta;
    }

    public void setdDelta(final double pDDelta) {
        m_dDelta = pDDelta;
    }

    public boolean isbUn() {
        return m_bUn;
    }

    public void setbUn(final boolean pBUn) {
        m_bUn = pBUn;
    }

    public Map<Long, Long> getMapCount() {
        return m_mapCount;
    }

    public void setMapCount(final Map<Long, Long> pMapCount) {
        m_mapCount = pMapCount;
    }

    public Map<Long, Float> getMapScore() {
        return m_mapScore;
    }

    public void setMapScore(final Map<Long, Float> pMapScore) {
        m_mapScore = pMapScore;
    }

    public List<ModifiedAminoAcid> getvAa() {
        return m_vAa;
    }

    public void setvAa(final List<ModifiedAminoAcid> pVAa) {
        m_vAa = pVAa;
    }
}
