

package net.itransformers.idiscover.v2.core.listeners;

import groovy.lang.Binding;
import groovy.util.GroovyScriptEngine;
import groovy.util.XmlSlurper;
import groovy.util.slurpersupport.GPathResult;
import net.itransformers.idiscover.networkmodel.DiscoveredDeviceData;
import net.itransformers.idiscover.util.JaxbMarshalar;
import net.itransformers.idiscover.v2.core.NodeDiscoveryListener;
import net.itransformers.idiscover.v2.core.NodeDiscoveryResult;
import net.itransformers.utils.XmlFormatter;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;

public class GraphmlFileLogGroovyDiscoveryListener implements NodeDiscoveryListener {
    static Logger logger = Logger.getLogger(GraphmlFileLogGroovyDiscoveryListener.class);
    String labelDirName;
    String graphmDirName;
    String rawData2GraphmlGroovyTransformer;
    String projectPath;
    public GraphmlFileLogGroovyDiscoveryListener() {
        rawData2GraphmlGroovyTransformer = "net.itransformers.idiscover.v2.core.listeners.groovy.RawData2GraphmlTransformer";
    }

    @Override
    public void nodeDiscovered(NodeDiscoveryResult discoveryResult) {
        File baseDir = new File(projectPath,labelDirName);
        File graphmlDir = new File(baseDir,graphmDirName);
        if (!graphmlDir.exists()) graphmlDir.mkdir();

        String deviceName = discoveryResult.getNodeId();
        DiscoveredDeviceData discoveredDeviceData = (DiscoveredDeviceData) discoveryResult.getDiscoveredData("deviceData");

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            JaxbMarshalar.marshal(discoveredDeviceData, out, "DiscoveredDevice");
        } catch (JAXBException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        StringReader reader = new StringReader(out.toString());

        StringWriter graphmlWriter = new StringWriter();
        transformRawDataToGraphml(reader, graphmlWriter);


        try {
            final String fileName = "node-" + deviceName + ".graphml";
//            String fullFileName = path + File.separator + fileName;
            final File nodeFile = new File(graphmlDir,fileName);
            System.out.println(new String(graphmlWriter.toString()));
            String graphml;

            graphml = new XmlFormatter().format(graphmlWriter.toString());

            FileUtils.writeStringToFile(nodeFile, graphml);
            File undirectedGraphmls = new File(graphmlDir.getParent(),"undirected"+".graphmls");
            if (!undirectedGraphmls.exists()){
                undirectedGraphmls.createNewFile();
            }
                FileWriter writer = new FileWriter(undirectedGraphmls,true);

                writer.append(String.valueOf(fileName)).append("\n");
                writer.close();


        } catch (IOException e) {
            logger.debug("Unformated xml is not in correct format: \n"+graphmlWriter.toString());
            e.printStackTrace();
        } catch (ParserConfigurationException e) {

            logger.debug("Unformated xml is not in correct format: \n"+graphmlWriter.toString());

            e.printStackTrace();
        } catch (SAXException e) {
            logger.debug("Unformated xml is not in correct format: \n"+graphmlWriter.toString());
            e.printStackTrace();
        }


    }

    void transformRawDataToGraphml(Reader in, Writer out) {
        try {

            GPathResult response = new XmlSlurper().parse(new InputSource(in));
            Binding binding = new Binding();
            binding.setProperty("input", response);
            binding.setProperty("output", out);

           // binding = new Binding();
           // Expect4Groovy.createBindings(connection, binding, true);
           // binding.setProperty("params", params);

           // String gseRoots = new File().toURI().toURL().toString();
            String[] roots =  new String[]{projectPath+"/iDiscover/conf/groovy/"+File.separator};
            GroovyScriptEngine gse = new GroovyScriptEngine(roots);
            gse.run("RawData2GraphmlTransformer.groovy", binding);

//            Class clazz = Class.forName(new File(projectPath,rawData2GraphmlGroovyTransformer).toURI().toURL().toString());
//            Constructor constructor = clazz.getDeclaredConstructor(Binding.class);
//            Script script = (Script) constructor.newInstance(binding);
//            script.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getLabelDirName() {
        return labelDirName;
    }

    public void setLabelDirName(String labelDirName) {
        this.labelDirName = labelDirName;
    }

    public String getGraphmDirName() {
        return graphmDirName;
    }

    public void setGraphmDirName(String graphmDirName) {
        this.graphmDirName = graphmDirName;
    }

    public String getRawData2GraphmlGroovyTransformer() {
        return rawData2GraphmlGroovyTransformer;
    }

    public void setRawData2GraphmlGroovyTransformer(String rawData2GraphmlGroovyTransformer) {
        this.rawData2GraphmlGroovyTransformer = rawData2GraphmlGroovyTransformer;
    }

    public String getProjectPath() {
        return projectPath;
    }

    public void setProjectPath(String projectPath) {
        this.projectPath = projectPath;
    }
}
