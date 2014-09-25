package org.systemsbiology.xtandem.testing;

/**
 * org.systemsbiology.xtandem.testing.ITextEquivalence
 *
 * @author Steve Lewis
 * @date Feb 21, 2011
 */
public interface ITextEquivalence
{
    public static ITextEquivalence[] EMPTY_ARRAY = {};
    public static Class THIS_CLASS = ITextEquivalence.class;

    public static ITextEquivalence DEFAULT = new DefaultTextEquivalence();
    /**
     * return true when the two lines are equivalent
     *
     * @param !null pLine1
     * @param !null pLine2
     * @return as above
     */
    public boolean areLinesEquivalent(String pLine1, String pLine2);

    /**
     * allow short circuit
     * @return
     */
    public boolean isTestOver() ;

    static class DefaultTextEquivalence implements ITextEquivalence
    {
        private DefaultTextEquivalence()
        {
        }

        public boolean areLinesEquivalent(String pLine1, String pLine2)
        {
            if (!pLine1.replace(" ", "").equals(pLine2.replace(" ", "")))
                return false;
            return true;
        }

        public boolean isTestOver() {
            return false;
        }
    }
}
