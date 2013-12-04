package net.itransformers.utils.DynamicDataGrapher;

import net.itransformers.snmptoolkit.CmdOptions;
import net.itransformers.snmptoolkit.CmdParser;
import net.itransformers.snmptoolkit.Get;
import net.itransformers.snmptoolkit.messagedispacher.DefaultMessageDispatcherFactory;
import net.itransformers.snmptoolkit.transport.UdpTransportMappingFactory;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Map;


/**
 * A demonstration application showing a time series chart where you can dynamically add
 * (random) data by clicking on a button.
 *
 */
public class BgpPeeringUpdateGrapher extends ApplicationFrame implements ActionListener {

    /** The time series data. */
    private static TimeSeries series;

    /** The most recent value added. */
    private static long lastValue = 0;
    private static Get snmpGet;



    private static int deltaInterval;
    /**
     * Constructs a new demonstration application.
     *
     * @param title  the frame title.
     */
    public BgpPeeringUpdateGrapher(final String title,String series) {

        super(title);
        this.series = new TimeSeries(series, Millisecond.class);
        final TimeSeriesCollection dataset = new TimeSeriesCollection(this.series);

        final JFreeChart chart = createChart(dataset, title);

        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(1000, 1000));
        chartPanel.setMouseWheelEnabled(true);
        chartPanel.setMouseZoomable(true);

        final JPanel content = new JPanel(new BorderLayout());
        content.add(chartPanel);
        setContentPane(content);

    }


    private JFreeChart createChart(final XYDataset dataset, String chartTitle) {
        final JFreeChart result = ChartFactory.createTimeSeriesChart(
                chartTitle,
                "Time",
                "Value",
                dataset,
                true,
                true,
                false
        );
        final XYPlot plot = result.getXYPlot();
        ValueAxis axis = plot.getDomainAxis();
        axis.setAutoRange(true);
        axis.setFixedAutoRange(60000.0);  // 60 seconds
        axis = plot.getRangeAxis();
        axis.setAutoRangeMinimumSize(1000.0);

        return result;
    }


    public void actionPerformed(final ActionEvent e) {
        if (e.getActionCommand().equals("ADD_DATA")) {
           Long bgpInUpdate = null;
            try {
                bgpInUpdate = Long.parseLong(snmpGet.getSNMPValue());
                System.out.println(bgpInUpdate);
            } catch (IOException e1) {
                e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

            long delta = bgpInUpdate - lastValue;
            lastValue = bgpInUpdate;
            System.out.println(delta);
            final Millisecond now = new Millisecond();
            System.out.println("Now = " + now.toString());
            this.series.add(new Millisecond(), delta);
        }
    }

//    private static Map<String, String> loadProperties(File file) throws IOException {
//        Properties props = new Properties();
//        props.load(new FileInputStream(file));
//        HashMap<String, String> settings = new HashMap<String, String>();
//        for (Object key : props.keySet()) {
//            settings.put((String)key,(String)props.get(key));
//        }
//        return settings;
//    }
//    private static void printUsage(String param){
//        System.out.println("Usage:   java net.itransformers.net.itransformers.utils.DynamicDataGrapher.BgpPeeringUpdateGrapher -s <Path to bgpUpdates.properties>");
//        System.out.println("Example [Windows]: java net.itransformers.utils.DynamicDataGrapher.BgpPeeringUpdateGrapher -s utils\\conf\\bgpUpdates.properties");
//        System.out.println("Example [Unix]: java net.itransformers.utils.DynamicDataGrapher.BgpPeeringUpdateGrapher -s utils/conf/bgpUpdates.properties");
//        System.out.println("Missing parameter: "+param);
//    }

    public static void main(final String[] args) throws IOException {
        Map<CmdOptions,String> opts;

        try {
            opts = CmdParser.parseCmd(args);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            CmdParser.printGetUsage();
            return;
        }
        String oid = opts.get(CmdOptions.OIDS);
        if (oid == null) {
            System.out.println("Missing option \"-"+ CmdOptions.OIDS.getName()+"\"");
            CmdParser.printGetUsage();
        }
        String address = opts.get(CmdOptions.ADDRESS);
        if (address == null) {
            System.out.println("Missing option \"-"+ CmdOptions.ADDRESS.getName()+"\"");
            CmdParser.printGetUsage();
        }
        String community = opts.get(CmdOptions.COMMUNITY);
        if (community == null) {
            System.out.println("Missing option \"-"+ CmdOptions.COMMUNITY.getName()+"\"");
            CmdParser.printGetUsage();
        }
        int versionInt;
        try {
            String version = opts.get(CmdOptions.VERSION);
            versionInt = Integer.parseInt(version);
        } catch (NumberFormatException nfe) {
            System.out.println("Invalid parameter value for \"-"+ CmdOptions.VERSION + "\", int value is required");
            CmdParser.printGetUsage();
            return;
        }
        int retriesInt;
        try {
            String retries = opts.get(CmdOptions.RETRIES);
            retriesInt = Integer.parseInt(retries);
        } catch (NumberFormatException nfe) {
            System.out.println("Invalid parameter value for \"-"+ CmdOptions.RETRIES + "\", int value is required");
            CmdParser.printGetUsage();
            return;
        }
        int timeoutInt;
        try {
            String timeout = opts.get(CmdOptions.TIMEOUT);
            timeoutInt = Integer.parseInt(timeout);
        } catch (NumberFormatException nfe) {
            System.out.println("Invalid parameter value for \"-"+ CmdOptions.TIMEOUT + "\", int value is required");
            CmdParser.printGetUsage();
            return;
        }
        try {
            String delta = opts.get(CmdOptions.DELTA);
            deltaInterval = Integer.parseInt(delta);
            if (deltaInterval < 600000){
                System.out.println("Delta interval "+ deltaInterval + " is too small. It should be a number higher than 600000 ms (10 minutes)!");
                return;
            }
        } catch (NumberFormatException nfe) {
            System.out.println("Invalid parameter value for \"-"+ CmdOptions.DELTA + "\", int value is required");
            CmdParser.printGetUsage();
            return;
        }
        snmpGet = new Get(oid, address, versionInt, retriesInt, timeoutInt, community, new UdpTransportMappingFactory(), new DefaultMessageDispatcherFactory());
        String response =   snmpGet.getSNMPValue();
        System.out.println(response);
        lastValue = Long.parseLong(response);
        final BgpPeeringUpdateGrapher demo = new BgpPeeringUpdateGrapher("BgpPeeringUpdate Line Chart",oid);
        demo.pack();
        gen myGen = new gen();
        new Thread(myGen).start();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);

    }
    static class gen implements Runnable {

        public void run() {
            while(true) {
                Long update = null;

                try {
                    update = Long.parseLong(snmpGet.getSNMPValue());
                } catch (IOException e1) {
                    e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }

                long delta = update - lastValue;
                System.out.println("Update: "+ update);

                System.out.println("previous Update: "+ lastValue);
                System.out.println("delta: "+ delta);

                lastValue = update;


                series.addOrUpdate(new Millisecond(), delta);
                try {
                    Thread.sleep(deltaInterval);
                } catch (InterruptedException ex) {
                    System.out.println(ex);
                }
            }
        }
    }

}
