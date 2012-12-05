/*
 * iTransformer is an open source tool able to discover and transform
 *  IP network infrastructures.
 *  Copyright (C) 2012  http://itransformers.net
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import net.itransformers.idiscover.networkmodel.DiscoveredDeviceData;
import net.itransformers.idiscover.networkmodel.ObjectType;
import net.itransformers.idiscover.networkmodel.ParameterType;
import net.itransformers.idiscover.networkmodel.ParametersType;
import net.itransformers.idiscover.util.JaxbMarshalar;
import net.itransformers.ws.upload.Node;
import net.itransformers.ws.upload.TreeImporterImplService;

import javax.ws.rs.core.UriBuilder;
import javax.xml.namespace.QName;
import java.io.*;
import java.lang.Exception;
import java.net.URL;
import java.util.List;

public class Neo4JWsDataImporter {
    public static int nodeCounter = 0;
    private static net.itransformers.ws.upload.TreeImporter treeImporter;


    public static void main(String[] args) throws java.lang.Exception {
        treeImporter = new TreeImporterImplService(new URL("http://localhost:8080/wsitransformer/upload"),
                new QName("http://upload.ws.itransformers.net/","TreeImporterImplService")).getTreeImporterImplPort();
//        System.out.println(port.importNode(5L, new net.itransformers.ws.upload.Node()));
        doImport();
    }

    private static void doImport() throws java.lang.Exception {
        File dir = new File("//Users//niau//svn//4_very_big");

        Long rootId = createRootNetworkNode();
        File[] files = dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.startsWith("device-") && name.endsWith(".xml");
            }
        });

        long start = System.currentTimeMillis();
//        importData(rootId, new File(dir, "device-data-MAG-112-1.xml"));
        for (File file : files) {

            System.out.println("importing file: " + file.getName());
            importData(rootId, file);

        }
        System.out.println("Created nodes: "+nodeCounter);
        System.out.println("Imported ... "+(System.currentTimeMillis()-start)/1000 + " seconds");
    }

    public static Long createRootNetworkNode() throws java.lang.Exception {
        Node root = new Node();
        Node.Attributes.Entry entry = new Node.Attributes.Entry();
        entry.setKey("name");entry.setValue("network");
        Node.Attributes value = new Node.Attributes();
        root.setAttributes(value);
        root.getAttributes().getEntry().add(entry);
        return treeImporter.importNode(null, root);
    }

    public static void importData(Long rootId, File file) throws java.lang.Exception {
        FileInputStream is = new FileInputStream(file);
        DiscoveredDeviceData discoveryManagerType = null;
        try {
            discoveryManagerType = JaxbMarshalar.unmarshal(DiscoveredDeviceData.class, is);
        } finally {
            is.close();
        }
        Node node = importDiscoveredDeviceData(discoveryManagerType);
        treeImporter.importNode(rootId, node);
    }
    public static Node importDiscoveredDeviceData(DiscoveredDeviceData discoveryManagerType) throws java.lang.Exception {
        Node node= new Node();
        nodeCounter++;
        System.out.println("Created node count= "+nodeCounter);
        Node.Attributes.Entry entry = new Node.Attributes.Entry();

        entry.setKey("name");entry.setValue(discoveryManagerType.getName());

        node.setAttributes(new Node.Attributes());

        node.getAttributes().getEntry().add(entry);

        entry = new Node.Attributes.Entry();
        entry.setKey("objectType");entry.setValue("Device");// Hardcoded because the DiscoveredDeviceData is not natural data type
        node.getAttributes().getEntry().add(entry);

        ParametersType parameters = discoveryManagerType.getParameters();
        for (ParameterType param : parameters.getParameter()) {
            String nameURI = UriBuilder.fromPath(param.getName()).build("").toString();
            entry = new Node.Attributes.Entry();
            entry.setKey(nameURI);entry.setValue(param.getValue());
            node.getAttributes().getEntry().add(entry);

        }

        List<ObjectType> objectTypeList = discoveryManagerType.getObject();
        for (ObjectType objectType : objectTypeList) {
            Node child = importObjectType(objectType);
            node.getChildren().add(child);
        }
        return node;
    }

    public static Node importObjectType(ObjectType objectType) throws Exception {
        Node node= new Node();
        nodeCounter++;
        System.out.println("Created node count= "+nodeCounter);
        Node.Attributes.Entry entry;
        node.setAttributes(new Node.Attributes());

        if (objectType.getName() != null) {
            entry = new Node.Attributes.Entry();
            entry.setKey("name");entry.setValue(objectType.getName());
            node.getAttributes().getEntry().add(entry);

        }
        entry = new Node.Attributes.Entry();
        entry.setKey("objectType");entry.setValue(objectType.getObjectType());
        node.getAttributes().getEntry().add(entry);

        ParametersType parameters = objectType.getParameters();
        for (ParameterType param : parameters.getParameter()) {
            String nameURI = UriBuilder.fromPath(param.getName()).build("").toString();
            entry = new Node.Attributes.Entry();
            entry.setKey(nameURI);entry.setValue(param.getValue());
            node.getAttributes().getEntry().add(entry);
        }
        List<ObjectType> objectTypeList = objectType.getObject();
        for (ObjectType childObjectType : objectTypeList) {
            Node child = importObjectType(childObjectType);
            node.getChildren().add(child);
        }
        return node;
    }

}
