package org.systemsbiology.xtandem.taxonomy;

/**
 * org.systemsbiology.xtandem.taxonomy.SequenceTree
 * User: Steve
 * Date: Apr 7, 2011
 */
public class SequenceTree {
    public static final SequenceTree[] EMPTY_ARRAY = {};
    public static final int NUMBER_NODES = 'Z' - 'A' + 1;

    private SequenceNode m_Start = new SequenceNode();

    public SequenceTree() {
    }

    public void clear() {
        m_Start.m_ChildNodes = null;
        m_Start.setPresent(false);
    }
    public boolean isPresent(String sequence) {
        if(sequence.length() == 0)
              throw new IllegalStateException("Sequence must be non-zero length");
        sequence = sequence.toUpperCase();
        return isPresent(sequence, 0, m_Start);
    }

    /**
     * add to tree
     * @param sequence the sequence
     * @return  true if already there
     */
    public boolean guaranteePresent(String sequence) {
        if(sequence.length() == 0)
             throw new IllegalStateException("Sequence must be non-zero length");
        sequence = sequence.toUpperCase();
        return guaranteePresent(sequence, 0, m_Start);
    }

    protected boolean isPresent(String sequence, int index, SequenceNode current) {
        if (sequence.length() == index)
            return current.isPresent();
        int c = sequence.charAt(index);
        SequenceNode sn = current.getChildNode(c, false);
        if (sn == null)
            return false;
        return isPresent(  sequence,   index + 1,sn);
    }


    protected boolean guaranteePresent(String sequence, int index, SequenceNode current) {
        if (sequence.length() == index) {
            if(current.isPresent())
                return true;
            current.setPresent(true);
            return false;

        }
        int c = sequence.charAt(index);
        boolean ret = false;
        SequenceNode sn = current.getChildNode(c, false);
        if (sn == null)  {
            ret = false;
            sn = current.getChildNode(c, true);
            guaranteePresent(  sequence,   index + 1,sn);
            return ret;
        }
        return guaranteePresent(  sequence,   index + 1,sn);
    }


    private static class SequenceNode {
        private boolean m_Present; // true if the node terminates a sequence
        private SequenceNode[] m_ChildNodes; // todo is a Map better

        public boolean isPresent() {
            return m_Present;
        }

        public boolean isChildNodesPresent() {
            return m_ChildNodes != null;
        }

        public void setPresent(final boolean pPresent) {
            m_Present = pPresent;
        }

        public SequenceNode[] getChildNodes() {
            return m_ChildNodes;
        }

        public SequenceNode getChildNode(int index, boolean create) {
            if (index < 'A' || index > 'Z')
                throw new IllegalArgumentException("index should be 'A' .. 'Z");
            int realIndex = index - 'A';
            if (create) {
                if (!isChildNodesPresent()) {
                    m_ChildNodes = new SequenceNode[NUMBER_NODES];
                }
                  if (m_ChildNodes[realIndex] == null)
                    m_ChildNodes[realIndex] = new SequenceNode();
                return m_ChildNodes[realIndex];
            }
            else {
                if (!isChildNodesPresent())
                    return null;
                return m_ChildNodes[realIndex];

            }
        }
    }

}
