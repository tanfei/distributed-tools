package org.systemsbiology.xtandem;

import java.io.*;

/**
 * org.systemsbiology.xtandem.XTamdemProcessLog
 *   A simple logger for use in the algorithm - allows
 *   writing the log to a file for later testing
 * @author Steve Lewis
  */
public class XTamdemProcessLog
{
    public static XTamdemProcessLog[] EMPTY_ARRAY = {};
    public static Class THIS_CLASS = XTamdemProcessLog.class;

    private PrintWriter m_Logger;

    public boolean open(String _s)
    {
        try {
            m_Logger = new PrintWriter(new FileWriter(_s));
            return true;
        }
        catch (IOException e) {
            return false;
        }
    }

    public boolean log(String _m)
    {
        if (m_Logger == null) {
            return false;
        }
//        time_t tValue;
//        time( & tValue);
//        struct tm*tmValue = localtime( & tValue);
//        char pLine[
//        256];
//        strftime(pLine, 255, "%Y-%m-%d %H:%M:%S", tmValue);
//        m_Logger << pLine << "\t" << _m.c_str() << "\n";
//        m_Logger.flush();
        m_Logger.println(_m);
        return true;
    }

    public boolean close()
    {
        if (m_Logger == null) {
            return false;
        }
        m_Logger.close();
        return true;
    }

}

