package org.systemsbiology.xtandem.roc;

import com.lordjoe.utilities.*;

import java.io.*;
import java.util.*;

/**
 * org.systemsbiology.xtandem.roc.ROCData
 * User: steven
 * Date: 3/8/12
 */
public class ROCData {
    public static final ROCData[] EMPTY_ARRAY = {};
    public static final double DEFAULT_DEL = 0.001;


    public static ROCData buildROCData(final String pFileName,String dataName) {
        if(dataName == null)
            dataName = pFileName.replace(".xml","");
        File f = new File(pFileName);
        String[] lines = FileUtilities.readInLines(f);

        ROCData data = new ROCData(dataName);
        for (int i = 1; i < lines.length; i++) {
            String line = lines[i];
            String[] items = line.split("\t");
            if (items.length == 3) {
                double score = Double.parseDouble(items[2]);
                data.addScore(score);
            }
        }
        return data;
    }


    private final String m_Name;
    private double m_MaxQScore = 0.05;
    private List<Double> m_QScores = new ArrayList<Double>();
    private boolean m_Normalized;

    public ROCData(final String pName) {
        m_Name = pName;
    }

    public Point[] getData() {
        return getData(DEFAULT_DEL);
    }

    public String getName() {
        return m_Name;
    }

    public double getMaxQScore() {
        return m_MaxQScore;
    }

    public boolean isNormalized() {
        return m_Normalized;
    }

    public void setNormalized(final boolean pNormalized) {
        m_Normalized = pNormalized;
    }

    public void clear() {
        m_QScores.clear();
        setNormalized(true);
    }

    public synchronized void addScore(double score) {
        m_QScores.add(score);
        setNormalized(false);
    }

    public void guaranteeNormalized() {
        if (isNormalized())
            return;
        Collections.sort(m_QScores);
        setNormalized(true);
    }

    public Point[] getData(double del) {
        guaranteeNormalized();
        List<Point> holder = new ArrayList<Point>();
        holder.add(Point.ZERO);

        double next = del;
        double end = getMaxQScore();
        int n = 0;
        while (next < end) {
            double test = m_QScores.get(n);
            if (test >= next) {
                holder.add(new Point(test, n));
                next += del;
                if (next >= end)
                    break;
            }
            else {
                n++;
                if (n >= m_QScores.size())
                    break;
            }
        }


        Point[] ret = new Point[holder.size()];
        holder.toArray(ret);
        return ret;
    }

    public static void main(String[] args) {
        String fileName = args[0];
        ROCData data = buildROCData(fileName,null);

        Point[] data1 = data.getData();
        for (int i = 0; i < data1.length; i++) {
            Point point = data1[i];
            System.out.println(point);
        }
    }

}
