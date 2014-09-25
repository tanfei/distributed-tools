package org.systemsbiology.xtandem;

/**
 * org.systemsbiology.xtandem.ModifiedAminoAcid
 *
 * @author Steve Lewis
 * @date Dec 21, 2010
 */
public class ModifiedAminoAcid
{
    public static ModifiedAminoAcid[] EMPTY_ARRAY = {};
    public static Class THIS_CLASS = ModifiedAminoAcid.class;

    private long m_lPos; // the sequence position of the residue (N-terminal = 0)
    private double m_dMod; // mass of the modification
    private char m_cRes; // single letter abbreviation for the amino acid
    private char m_cMut; // single letter abbreviation for a discovered point mutation
    private String m_strId; // character string representing an external accession number for a mutation/modification
    private double m_dPrompt; // prompt loss from modification mass

}
