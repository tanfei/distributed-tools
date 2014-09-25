package org.systemsbiology.xtandem.peptide;

/**
 * org.systemsbiology.xtandem.peptide.DummyProtein
 * User: Steve
 * Date: Apr 5, 2011
 */
public class DummyProtein extends DummyPeptide implements IProtein {
    public static final DummyProtein[] EMPTY_ARRAY = {};

    private static int gIndex = 1;

    public synchronized static int getIndex() {
        return gIndex++;
    }

    private int m_UUID;

    public DummyProtein(final String pId) {
        super(pId);
        m_UUID = getIndex();
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
     * return a list of contained proteins
     *
     * @return !null array
     */
    @Override
    public IProteinPosition[] getProteinPositions() {
        IProteinPosition[] ret = {new ProteinPosition(this)};
        return ret;
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
        if (true)
            throw new UnsupportedOperationException("Dummy proteins cannot get this");
        return null;
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


    @Override
    public boolean isDecoy() {
        return false;
    }

    @Override
    public boolean isProtein() {
        return true;
    }

    @Override
      public IPolypeptide asDecoy() {
          throw new UnsupportedOperationException("Fix This"); // ToDo
      }


    /**
     * source file
     *
     * @return
     */
    @Override
    public String getURL() {
        if (true)
            return "unknown";
        // throw new UnsupportedOperationException("Dummy proteins cannot get this");
        return null;
    }

    @Override
    public String getAnnotation() {
        return getId();
    }

    @Override
    public double getExpectationFactor() {
        if (true)
            throw new UnsupportedOperationException("Dummy proteins cannot get this");
        return 0;
    }

    @Override
    public int compareTo(final IPolypeptide o) {
        if (true)
            throw new UnsupportedOperationException("Dummy proteins cannot get this");
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
