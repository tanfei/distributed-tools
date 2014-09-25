package org.systemsbiology.xtandem;

import com.lordjoe.utilities.*;
import org.junit.*;

import java.io.*;

/**
 * org.systemsbiology.xtandem.JXtandemDeployerTests
 *
 * @author Steve Lewis
 * @date 5/14/13
 */
public class JXtandemDeployerTests {
    public static JXtandemDeployerTests[] EMPTY_ARRAY = {};
    public static Class THIS_CLASS = JXtandemDeployerTests.class;

    @Test
    public void buildTargetTest()
    {
        File Target = new File("Target.jar");
        JarUtilities ju = new JarUtilities(Target);
        ju.jarClassPath();
        Assert.assertTrue(Target.exists());
    }
}
