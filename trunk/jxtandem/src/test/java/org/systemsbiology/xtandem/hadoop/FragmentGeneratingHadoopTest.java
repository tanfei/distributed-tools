package org.systemsbiology.xtandem.hadoop;

import org.junit.*;

/**
 * org.systemsbiology.xtandem.hadoop.FragmentGeneratingHadoopTest
 *  This tests runs a local version of hadoop generating fragments on the fly
 *  and comparees a small run against an XTandem run of the same data
 * User: Steve
 * Date: 9/13/11
 */
public class FragmentGeneratingHadoopTest {
    public static final FragmentGeneratingHadoopTest[] EMPTY_ARRAY = {};

   // too hard to set up properly SLewis
  //@Test
    public void testLocalRun() throws Exception {
         String input = "params=res://tandem_params.xml";
        String[] args = { "config=res://TestLauncherLocal.properties" , input };
        JXTandemLauncher.main(args);
    }
}
