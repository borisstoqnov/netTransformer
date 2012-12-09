/*
 * iTransformer is an open source tool able to discover IP networks
 * and to perform dynamic data data population into a xml based inventory system.
 * Copyright (C) 2010  http://itransformers.net
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.itransformers.topologyviewer.gui;

import net.itransformers.topologyviewer.config.TopologyViewerConfType;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;
import edu.uci.ics.jung.io.GraphMLMetadata;
import edu.uci.ics.jung.io.GraphMLReader;
import org.apache.commons.collections15.Factory;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.URL;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * Date: 11-11-8
 * Time: 10:46
 * To change this template use File | Settings | File Templates.
 */
public class GraphmlLoader<G extends Graph<String,String>> {
    Factory<G> factory;
    private Set<String> loadedFiles = new HashSet<String>();
    private Map<String, Map<String, GraphMLMetadata<G>>> graphMetadatas = new HashMap<String, Map<String, GraphMLMetadata<G>>>();
    private Map<String, Map<String, GraphMLMetadata<String>>> vertexMetadatas = new HashMap<String, Map<String, GraphMLMetadata<String>>>();
    private Map<String, Map<String, GraphMLMetadata<String>>> edgeMetadatas = new HashMap<String, Map<String, GraphMLMetadata<String>>>();
    private TopologyViewerConfType viewerConfig;
    private G entireGraph;
    private List<GraphmlLoaderListener> listeners = new ArrayList<GraphmlLoaderListener>();

    public GraphmlLoader(TopologyViewerConfType viewerConfig, G entireGraph, Factory<G> factory,Map<String, Map<String, GraphMLMetadata<String>>> vertexMetadatas) {
        this.viewerConfig = viewerConfig;
        this.entireGraph = entireGraph;
        this.factory = factory;
        this.vertexMetadatas = vertexMetadatas;
    }

    List<String> readGraphmlFileNames(URL nodeListUrl) throws IOException {
        List<String> result = new ArrayList<String>();
        BufferedReader in = new BufferedReader(new InputStreamReader(nodeListUrl.openStream()));
        try {
        String s;
        while ((s = in.readLine()) != null){
            result.add(s);
        }
        } finally {
            in.close();
        }
        return result;
    }

    public void loadGraphml(URL urlPath) throws ParserConfigurationException, SAXException, IOException {
//        File file = new File(dir + File.separator + "nodes-file-list.txt");
        URL url2 = new URL(urlPath,"nodes-file-list.txt");
        List<String> allFiles = readGraphmlFileNames(url2);//FileUtils.readLines(file);
        List<String> files = allFiles.subList(loadedFiles.size(),allFiles.size());
        for (String fileName : files){
            if (fileName.startsWith("#")) continue;
            boolean forUpdate = !loadedFiles.add(fileName);
            final G graph = factory.create();
            URL grahmlUrl = new URL(urlPath,fileName);
            GraphMLReader gmlr = loadGraphmlInGraph(grahmlUrl, graph);
            Collection<String> verteces = graph.getVertices();
            for (String vertex :verteces){
                if(!entireGraph.containsVertex(vertex)){
                     entireGraph.addVertex(vertex);
                }             else{
                    System.out.println("Out");
                }
            }
            Collection<String> edges = graph.getEdges();
            for (String edge : edges){
                Pair<String> endpoints = graph.getEndpoints(edge);
                if (!entireGraph.containsEdge(edge)){
                    entireGraph.addEdge(edge,endpoints);
                }
            }
        //    graphMetadatas.put(fileName,gmlr.getGraphMetadata());
            edgeMetadatas.put(fileName,gmlr.getEdgeMetadata());
       //     vertexMetadatas.put(fileName,gmlr.getVertexMetadata());
            notifyListeners(fileName, gmlr.getVertexMetadata(), gmlr.getEdgeMetadata(), graph);
        }
    }

    static <G extends Graph<String,String>> GraphMLReader loadGraphmlInGraph(InputStream is, G graph) throws ParserConfigurationException, SAXException, IOException {
        GraphMLReader gmlr = new GraphMLReader<G, String, String>(null, null);
        BufferedReader in = new BufferedReader(new InputStreamReader(is));
        gmlr.load(in, graph);
        return gmlr;
    }
    static <G extends Graph<String,String>> GraphMLReader loadGraphmlInGraph(URL grahmlUrl, G graph) throws ParserConfigurationException, SAXException, IOException {
        InputStream is = null;
        try {
            is = grahmlUrl.openStream();
            return loadGraphmlInGraph(is,graph);
        } catch (IOException e) {
            System.out.println("Can not load graphml from url :"+grahmlUrl);
            throw e;
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }


    public Map<String, Map<String, GraphMLMetadata<G>>> getGraphMetadatas() {
        return graphMetadatas;
    }

    public Map<String, Map<String, GraphMLMetadata<String>>> getVertexMetadatas() {
        return vertexMetadatas;
    }

    public Map<String, Map<String, GraphMLMetadata<String>>> getEdgeMetadatas() {
        return edgeMetadatas;
    }
    public void addGraphmlLoaderListener(GraphmlLoaderListener listener){
        this.listeners.add(listener);
    }

    private void notifyListeners(String fileName,
                                 Map<String, GraphMLMetadata<String>> vertexMetadata,
                                 Map<String, GraphMLMetadata<String>> edgeMetadata,
                                 G graph){
        for (GraphmlLoaderListener graphmlLoaderListener : listeners) {
            graphmlLoaderListener.graphmlLoaded(fileName, vertexMetadata, edgeMetadata, graph);
        }
    }
}
