package org.systemsbiology.xtandem.peptide;

import org.systemsbiology.xtandem.*;

/**
 * org.systemsbiology.xtandem.peptide.DummyPeptide
 * User: Steve
 * Date: Apr 5, 2011
 */
public class DummyPeptide implements IPolypeptide {
    public static final DummyPeptide[] EMPTY_ARRAY = {};


    private final String m_Id;

    public DummyPeptide(final String pId) {
        m_Id = pId;
    }


    @Override
    public boolean isProtein() {
        return false;
    }

    /**
     * true is the polypaptide is known to be a decoy
     *
     * @return
     */
    @Override
    public boolean isDecoy() {
        return false;
    }


    @Override
      public IPolypeptide asDecoy() {
          throw new UnsupportedOperationException("Fix This"); // ToDo
      }

    /**
     * return a list of contained proteins
     *
     * @return !null array
     */
    @Override
    public IProteinPosition[] getProteinPositions() {
        return IProteinPosition.EMPTY_ARRAY;
    }


    @Override
    public IPolypeptide getUnModified() {
        return this;
    }

    /**
     * !null validity may be unknown
     *
     * @return
     */
    public PeptideValidity getValidity() {
        return PeptideValidity.Unknown;
    }

    /**
     * true if the peptide is SewmiTryptic but may
     * miss instance where K or R is followed by aP which
     * are semitryptic
     *
     * @return
     */
    public boolean isProbablySemiTryptic() {
        String sequence = getSequence();
        char c = sequence.charAt(sequence.length() - 1);
        switch (c) {
            case 'r':
            case 'R':
            case 'k':
            case 'K':
                return false; // tryptic unless followed by a P
            default:
                return false;
        }
    }


    /**
     * true if there is at least one modification
     *
     * @return
     */
    @Override
    public boolean isModified() {
        return false;
    }

    /**
     * count the occurrance of an amino acid in the sequence
     *
     * @param aa !null amino acid
     * @return count of presence
     */
    @Override
    public int getAminoAcidCount(final FastaAminoAcid aa) {
        return 0;
    }

    @Override
    public boolean hasAminoAcid(FastaAminoAcid aa) {
        return false;
    }

    /**
     * return the N Terminal amino acid
     *
     * @return
     */
    @Override
    public FastaAminoAcid getNTerminal() {
        return null;
    }

    /**
     * return the C Terminal amino acid
     *
     * @return
     */
    @Override
    public FastaAminoAcid getCTerminal() {
        return null;
    }

    @Override
    public boolean hasUnmodifiedAminoAcid(FastaAminoAcid aa) {
        return false;
    }

    /**
     * count the occurrance of an amino acid in the sequence
     *
     * @param aa !null amino acid  letter
     * @return count of presence
     */
    @Override
    public int getAminoAcidCount(final String aa) {
        return 0;
    }

    /**
     * weak test for equality
     *
     * @param test !null test
     * @return true if equivalent
     */
    @Override
    public boolean equivalent(final IPolypeptide test) {
        return false;
    }

    @Override
    public String getId() {
        return m_Id;
    }

    /**
     * check fo r common errors like * in AA seqience
     *
     * @return
     */
    @Override
    public boolean isValid() {
        return false;
    }

    /**
     * check for ambiguous peptides like *
     *
     * @return
     */
    @Override
    public boolean isUnambiguous() {
        return false;
    }

    /**
     * return the mass calculated with the default calculator
     * monoisotopic or average
     *
     * @return as above
     */
    @Override
    public double getMass() {
        //    if(true)
        //       throw new UnsupportedOperationException("Dummy peptides cannot get this");
        return 0;
    }

    /**
     * mass used to see if scoring rowks
     *
     * @return
     */
    @Override
    public double getMatchingMass() {
        //  if(true)
        //       throw new UnsupportedOperationException("Dummy peptides cannot get this");
        return 0;
    }


    /**
     * return the length of the sequence
     *
     * @return !null String
     */
    @Override
    public int getSequenceLength() {
        return XTandemUtilities.peptideKeyToLength(getId());
    }

    /**
     * return the sequence as a set of characters
     *
     * @return !null String
     */
    @Override
    public String getSequence() {
        //    if(true)
        //         throw new UnsupportedOperationException("Dummy peptides cannot get this");
        return "";
    }


    /**
     * return the number of bionds in the sequence
     *
     * @return as above
     */
    @Override
    public int getNumberPeptideBonds() {
        return getSequenceLength() - 1;
    }

    /**
     * return the amino acids as chars on the N and C sides of the bond
     *
     * @param bond
     * @return
     */
    @Override
    public char[] getBondPeptideChars(final int bond) {
        if (true)
            throw new UnsupportedOperationException("Dummy peptides cannot get this");
        return new char[0];
    }

    /**
     * build a ploypeptide by putting the two peptides together
     *
     * @param added !null added sequence
     * @return !null merged peptide
     */
    @Override
    public IPolypeptide concat(final IPolypeptide added) {
        if (true)
            throw new UnsupportedOperationException("Dummy peptides cannot get this");
        return null;
    }

    /**
     * delibrately hide the manner a peptide is cleaved to
     * support the possibility of the sequence pointing to the protein as
     * Java substring does
     *
     * @param bond non-negative bond
     * @return !null array of polypeptides
     * @throws IndexOutOfBoundsException on bad bond
     */
    @Override
    public IPolypeptide[] cleave(final int bond) throws IndexOutOfBoundsException {
        if (true)
            throw new UnsupportedOperationException("Dummy peptides cannot get this");
        return new IPolypeptide[0];
    }

    /**
     * deibbrately hide the manner a peptide is cleaved to
     * support the possibility of the sequence pointing to the protein as
     * Java substring does
     *
     * @param bond non-negative bond
     * @return !null array of polypeptides
     * @throws IndexOutOfBoundsException on bad bond
     */
    @Override
    public IPolypeptide subsequence(final int start, final int end) throws IndexOutOfBoundsException {
        if (true)
            throw new UnsupportedOperationException("Dummy peptides cannot get this");
        return null;
    }

    /**
     * return the number of missed cleavages
     *
     * @return as above
     */
    @Override
    public int getMissedCleavages() {
        //   if(true)
        //        throw new UnsupportedOperationException("Dummy peptides cannot get this");
        return 0;
    }

    /**
     * get the number of modified peptides
     *
     * @return
     */
    public int getNumberModifications()
    {
        return 0;
    }


    @Override
    public double getRetentionTime() {
        return 0;
    }

    @Override
    public void setRetentionTime(final double pRetentionTime) {
       throw new UnsupportedOperationException("Cannot Do");
    }

    @Override
    public String toString() {
        return getId();    //To change body of overridden methods use File | Settings | File Templates.
    }
}
