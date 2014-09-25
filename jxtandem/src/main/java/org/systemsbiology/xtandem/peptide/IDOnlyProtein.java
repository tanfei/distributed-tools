package org.systemsbiology.xtandem.peptide;

import org.systemsbiology.xtandem.*;

import java.util.*;

/**
 * org.systemsbiology.xtandem.peptide.IDOnlyProtein
 *  This is a place holder for a protein where only the id is known
 * User: Steve
 * Date: 1/12/12
 */
public class IDOnlyProtein implements IProtein {
    public static final IDOnlyProtein[] EMPTY_ARRAY = {};

    private static  Map<String,IDOnlyProtein>  gUsedProteins = new HashMap<String,IDOnlyProtein>();

    /**
     * clear a list usually used by a reducer to keep these from filling memory
     * this dectroys the uniqueness of these values but is OK when they are not to be reused
     */
    public static void clearUsedProteins() {
        synchronized (gUsedProteins) {
            gUsedProteins.clear();
        }
    }



    /**
     * convert an id to a protein
     * @param id   !null id
     * @return  !null protein
     */
    public static IDOnlyProtein getUsedProtein(String id) {
        synchronized (gUsedProteins) {
            IDOnlyProtein idOnlyProtein = gUsedProteins.get(id);
            if(idOnlyProtein == null)  {
               idOnlyProtein = new  IDOnlyProtein(id);
               gUsedProteins.put(id,idOnlyProtein);
            }
            return idOnlyProtein;
         }
     }

    /**
     * convert an id to a protein
     * @param id   !null id
     * @return  !null protein
     */
    public static IProtein[] getUsedProteins(String[] ids) {
        List<IProtein> holder = new ArrayList<IProtein>();
        for (int i = 0; i < ids.length; i++) {
            String id = ids[i];
             holder.add(getUsedProtein(ids[i]));
        }
        IProtein[] ret = new IProtein[holder.size()];
        holder.toArray(ret);
        return ret;
     }

    private final String m_Id;
    private IDOnlyProtein(String id) {
         m_Id = id;
    }

    @Override
    public String toString() {
        return   m_Id ;
    }

    /**
      * !null validity may be unknown
      * @return
      */
     public PeptideValidity getValidity()
     {
        return  PeptideValidity.fromString(getId()) ;
      }



    /**
     * convert position to id
     *
     * @param start
     * @param length
     * @return
     */
    @Override
    public String getSequenceId(final int start, final int length) {
        return null;
    }


    /**
     * return a list of contained proteins
     *
     * @return !null array
     */
    @Override
    public IProteinPosition[] getProteinPositions() {
        IProteinPosition[] ret = { new ProteinPosition(this)};
        return ret;
    }


    @Override
    public String getId() {
        return m_Id;
    }

    /**
     * source file
     *
     * @return
     */
    @Override
    public String getURL() {
        return null;
    }

    @Override
    public String getAnnotation() {
        return m_Id;
    }

    @Override
    public double getExpectationFactor() {
        return 0;
    }


    @Override
    public int compareTo(final IPolypeptide o) {
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
        return test == this;
    }

    /**
     * true is the polypaptide is known to be a protein
     *
     * @return
     */
    @Override
    public boolean isProtein() {
        return true;
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
     * true if the peptide is SewmiTryptic but may
     * miss instance where K or R is followed by aP which
     * are semitryptic
     *
     * @return
     */
    @Override
    public boolean isProbablySemiTryptic() {
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

    @Override
    public boolean hasUnmodifiedAminoAcid(FastaAminoAcid aa) {
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
     * true if there is at least one modification
     *
     * @return
     */
    @Override
    public boolean isModified() {
        return false;
    }

    /**
     * check fo r common errors like * in AA seqience
     *
     * @return
     */
    @Override
    public boolean isValid() {
        return true;
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
        return 0;
    }

    /**
     * mass used to see if scoring rowks
     *
     * @return
     */
    @Override
    public double getMatchingMass() {
        return 0;
    }

    /**
     * return the length of the sequence
     *
     * @return !null String
     */
    @Override
    public int getSequenceLength() {
        return 0;
    }

    /**
     * return the sequence as a set of characters
     *
     * @return !null String
     */
    @Override
    public String getSequence() {
        return null;
    }

    @Override
    public IPolypeptide getUnModified() {
        return this;
    }

    /**
     * return the number of bionds in the sequence
     *
     * @return as above
     */
    @Override
    public int getNumberPeptideBonds() {
        return 0;
    }

    /**
     * return the amino acids as chars on the N and C sides of the bond
     *
     * @param bond
     * @return
     */
    @Override
    public char[] getBondPeptideChars(final int bond) {
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
        return null;
    }

    /**
     * return the number of missed cleavages
     *
     * @return as above
     */
    @Override
    public int getMissedCleavages() {
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


}
