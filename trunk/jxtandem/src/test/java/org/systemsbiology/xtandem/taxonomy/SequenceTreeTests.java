package org.systemsbiology.xtandem.taxonomy;

import org.junit.*;

/**
 * org.systemsbiology.xtandem.taxonomy.SequenceTreeTests
 * User: Steve
 * Date: Apr 7, 2011
 */
public class SequenceTreeTests {
    public static final SequenceTreeTests[] EMPTY_ARRAY = {};


    public static final String[] ITEMS = {
            "A",
            "ABB",
            "AC",
            "ACBC",
            "ACCA",
            "AAA",
    };

    @Test
    public void testSimpleSequenceTree()
    {
        SequenceTree tree = new SequenceTree();
        for (int i = 0; i < ITEMS.length; i++) {
            String item = ITEMS[i];
            Assert.assertFalse(tree.isPresent(item));
            Assert.assertFalse(tree.guaranteePresent(item));
            Assert.assertTrue(tree.isPresent(item));
            Assert.assertTrue(tree.guaranteePresent(item));
        }

    }

    @Test
    public void testLargeSequenceTree()
    {
        SequenceTree tree = new SequenceTree();
        for (int i = 0; i < ITEMS.length; i++) {
            String item = ITEMS[i];
            Assert.assertFalse(tree.isPresent(item));
            Assert.assertFalse(tree.guaranteePresent(item));
            Assert.assertTrue(tree.isPresent(item));
            Assert.assertTrue(tree.guaranteePresent(item));
        }

    }

}
