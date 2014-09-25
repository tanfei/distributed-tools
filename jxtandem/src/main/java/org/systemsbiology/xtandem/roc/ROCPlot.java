package org.systemsbiology.xtandem.roc;


import com.lordjoe.utilities.*;
import org.jfree.chart.*;
import org.jfree.chart.plot.*;
import org.jfree.data.xy.*;
import org.jfree.ui.*;

import java.util.*;

/**
 * org.systemsbiology.xtandem.roc.ROCPlot
 * User: steven
 * Date: 3/8/12
 */
public class ROCPlot extends ApplicationFrame {
    public static final ROCPlot[] EMPTY_ARRAY = {};

    /**
     * A simple demo showing a dataset created using the {@link XYSeriesCollection} class.
     *
     */

    /**
     * A demonstration application showing an XY series containing a null value.
     *
     * @param title the frame title.
     */
    public ROCPlot(final String title, ROCData... rdata) {

        super(title);
        XYSeries series = fromROCData(rdata[0]);
        final XYSeriesCollection data = new XYSeriesCollection(series);
        for (int i = 1; i < rdata.length; i++) {
            ROCData rocData = rdata[i];
            series = fromROCData(rdata[i]);
            data.addSeries(series);
        }
        final JFreeChart chart = ChartFactory.createXYLineChart(
                "ROC",
                "Q Score",
                "Number",
                data,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        setContentPane(chartPanel);

    }


    public static XYSeries fromROCData(ROCData data) {
        final XYSeries series = new XYSeries(data.getName());
        Point[] data1 = data.getData();
        for (int i = 0; i < data1.length; i++) {
            Point point = data1[i];
            series.add(point.getX(), point.getY());
        }


        return series;

    }

    // ****************************************************************************
    // * JFREECHART DEVELOPER GUIDE                                               *
    // * The JFreeChart Developer Guide, written by David Gilbert, is available   *
    // * to purchase from Object Refinery Limited:                                *
    // *                                                                          *
    // * http://www.object-refinery.com/jfreechart/guide.html                     *
    // *                                                                          *
    // * Sales are used to provide funding for the JFreeChart project - please    *
    // * support us so that we can continue developing free software.             *
    // ****************************************************************************

    /**
     * Starting point for the demonstration application.
     *
     * @param args ignored.
     */
    public static void main(final String[] args) {
        List<ROCData> holder = new ArrayList<ROCData>();

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            String fileName = arg;
            ROCData data = ROCData.buildROCData(fileName, null);
            holder.add(data);
        }
        ROCData[] data = new ROCData[holder.size()];
        holder.toArray(data);

        final ROCPlot demo = new ROCPlot("ROC Plot", data);
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);

    }

}

