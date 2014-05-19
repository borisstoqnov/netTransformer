/*
 * netTransformer is an open source tool able to discover IP networks
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

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;
import edu.uci.ics.jung.io.GraphMLMetadata;
import edu.uci.ics.jung.io.GraphMLReader;
import org.apache.commons.collections15.Factory;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * Date: 11-11-8
 * Time: 10:46
 * To change this template use File | Settings | File Templates.
 */
public class FileSystemGraphmlLoader<G extends Graph<String,String>> implements GraphmlLoader<G> {
    private File urlPath;
    private Factory<G> factory;
    private Map<String, GraphMLMetadata<G>> graphMetadatas;
    private Map<String, GraphMLMetadata<String>> vertexMetadatas;
    private Map<String, GraphMLMetadata<String>> edgeMetadatas;
    private G entireGraph;
    private List<GraphmlLoaderListener> listeners = new ArrayList<GraphmlLoaderListener>();

    public FileSystemGraphmlLoader(G entireGraph, Factory<G> factory, File urlPath) {
        this.entireGraph = entireGraph;
        this.factory = factory;
        this.urlPath = urlPath;
    }
    @Override
    public void loadGraphml() throws Exception {
        final G graph = factory.create();
        GraphMLReader gmlr = loadGraphmlInGraph(urlPath, graph);
        Collection<String> verteces = graph.getVertices();
        for (String vertex :verteces){
            if(!entireGraph.containsVertex(vertex)){
                entireGraph.addVertex(vertex);
            }
        }
        Collection<String> edges = graph.getEdges();
        for (String edge : edges){
            Pair<String> endpoints = graph.getEndpoints(edge);
            if (!entireGraph.containsEdge(edge)){
                entireGraph.addEdge(edge,endpoints);
            }
        }
        graphMetadatas = gmlr.getGraphMetadata();
        edgeMetadatas = gmlr.getEdgeMetadata();
        vertexMetadatas = gmlr.getVertexMetadata();
        notifyListeners(gmlr.getVertexMetadata(), gmlr.getEdgeMetadata(), graph);
    }

    static <G extends Graph<String,String>> GraphMLReader loadGraphmlInGraph(InputStream is, G graph) throws ParserConfigurationException, SAXException, IOException {
        GraphMLReader gmlr = new GraphMLReader<G, String, String>(null, null);
        BufferedReader in = new BufferedReader(new InputStreamReader(is));
        gmlr.load(in, graph);
        return gmlr;
    }
    static <G extends Graph<String,String>> GraphMLReader loadGraphmlInGraph(File grahmlUrl, G graph) throws ParserConfigurationException, SAXException, IOException {
        InputStream is = null;
        try {
            is = new FileInputStream(grahmlUrl);
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

    @Override
    public Map<String, GraphMLMetadata<G>> getGraphMetadatas() {
        return graphMetadatas;
    }
    @Override
    public Map<String, GraphMLMetadata<String>> getVertexMetadatas() {
        return vertexMetadatas;
    }
    @Override
    public Map<String, GraphMLMetadata<String>> getEdgeMetadatas() {
        return edgeMetadatas;
    }
    @Override
    public void addGraphmlLoaderListener(GraphmlLoaderListener listener){
        this.listeners.add(listener);
    }

    @Override
    public void removeGraphmlLoaderListener(GraphmlLoaderListener listener) {
        this.listeners.remove(listener);
    }

    private void notifyListeners(Map<String, GraphMLMetadata<String>> vertexMetadata,
                                 Map<String, GraphMLMetadata<String>> edgeMetadata,
                                 G graph){
        for (GraphmlLoaderListener graphmlLoaderListener : listeners) {
            graphmlLoaderListener.graphmlLoaded(vertexMetadata, edgeMetadata, graph);
        }
    }
}
