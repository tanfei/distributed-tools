package org.systemsbiology.xtandem.hadoop;

import org.apache.hadoop.conf.*;
import org.systemsbiology.xtandem.*;
import org.systemsbiology.xtandem.scoring.*;

import java.io.*;

/**
 * org.systemsbiology.xtandem.hadoop.HadoopTandemMain
 *  modified XTandemMain for Hadoop
 * @author Steve Lewis
 * @date Mar 8, 2011
 */
public class HadoopTandemMain  extends XTandemMain

{
    public static HadoopTandemMain[] EMPTY_ARRAY = {};
    public static Class THIS_CLASS = HadoopTandemMain.class;

    private static HadoopTandemMain gInstance;

    /**
     * call to find if we need to build one
     * @return  possibly null instance
     */
    public static synchronized HadoopTandemMain getInstance( )
   {
        return gInstance;
    }

    /**
     * guarantee this is a singleton
     * @param is
     * @param url
     * @param ctx
     * @return
     */
     public static synchronized HadoopTandemMain getInstance(InputStream is, String url,Configuration ctx)
    {
        if(gInstance == null)
             gInstance = new  HadoopTandemMain(is,url,ctx);
        return gInstance;
     }


    private final Configuration m_Context;

//    public HadoopTandemMain(Configuration ctx )
//    {
//        super( );
//        m_Context = ctx;
//      }

//    public HadoopTandemMain(File pTaskFile,Configuration ctx)
//    {
//        super(pTaskFile);
//        m_Context = ctx;
//      }

    private HadoopTandemMain(InputStream is, String url,Configuration ctx)
    {
        super(is, url);
        m_Context = ctx;
     }

    public Configuration getContext() {
        return m_Context;
    }

    public void handleInputs(final InputStream is)
     {
         super.handleInputs(is,"no File");
     }

    @Override
    public void addRawScan(final RawPeptideScan added) {
        throw new UnsupportedOperationException("Not in Map/Reduce"); // better not happen
    }

    @Override
    public RawPeptideScan getRawScan(final String key) {
        throw new UnsupportedOperationException("Not in Map/Reduce"); // better not happen
     }

    @Override
    public void addScoring(final IScoredScan added) {
        throw new UnsupportedOperationException("Not in Map/Reduce"); // better not happen
     }
}
