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

package net.itransformers.topologyviewer.menu;

import net.itransformers.topologyviewer.gui.TopologyManagerFrame;
import net.itransformers.topologyviewer.menu.handlers.*;
import net.itransformers.topologyviewer.menu.handlers.graphFileMenuHandlers.CloseGraphMenuHandler;
import net.itransformers.topologyviewer.menu.handlers.graphFileMenuHandlers.ConfigMenuHandler;
import net.itransformers.topologyviewer.menu.handlers.graphFileMenuHandlers.DiffMenuHandler;
import net.itransformers.topologyviewer.menu.handlers.graphFileMenuHandlers.OpenGraphMenuHandler;
import net.itransformers.topologyviewer.menu.handlers.graphTools.ChangeLayoutMenuHandler;
import net.itransformers.topologyviewer.menu.handlers.graphTools.GraphDistanceStatisticsMenuHandler;
import net.itransformers.topologyviewer.menu.handlers.graphTools.RankingAlgorithms.BetweennessCentralityMenuHandler;
import net.itransformers.topologyviewer.menu.handlers.graphTools.RankingAlgorithms.KMarkovMenuHandler;
import net.itransformers.topologyviewer.menu.handlers.graphTools.RankingAlgorithms.RandomWalkBetweennessCentralityMenuHandler;
import net.itransformers.topologyviewer.menu.handlers.graphTools.searchMenuHandlers.SearchByIpMenuHandler;
import net.itransformers.topologyviewer.menu.handlers.graphTools.searchMenuHandlers.SearchByKeyMenuHandler;
import net.itransformers.topologyviewer.menu.handlers.graphTools.searchMenuHandlers.SearchByNameCurrGraphMenuHandler;
import net.itransformers.topologyviewer.menu.handlers.graphTools.searchMenuHandlers.SearchByNameEntireGraphMenuHandler;
import net.itransformers.topologyviewer.menu.handlers.graphTools.shortherstPathMenuHandlers.DijkstraShortestPathMenuHandler;
import net.itransformers.topologyviewer.menu.handlers.graphTools.shortherstPathMenuHandlers.ShortestPathMenuHandler;
import net.itransformers.topologyviewer.menu.handlers.networkActivation.TemplateEditorMenuHandler;
import net.itransformers.topologyviewer.menu.handlers.projectMenuHandlers.CloseProjectMenuHandler;
import net.itransformers.topologyviewer.menu.handlers.projectMenuHandlers.NewProjectMenuHandler;
import net.itransformers.topologyviewer.menu.handlers.projectMenuHandlers.OpenProjectMenuHandler;
import net.itransformers.topologyviewer.menu.handlers.snmpDiscovery.ConfigureBGPParametersMenuHandler;
import net.itransformers.topologyviewer.menu.handlers.snmpDiscovery.ConfigureParametersMenuHandler;
import net.itransformers.topologyviewer.menu.handlers.snmpDiscovery.EditConfigMenuHandler;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;


public class MenuBuilder {
    public JMenuBar createMenuBar(final TopologyManagerFrame frame) {
        JMenuBar menuBar = new JMenuBar();

        createFileMenu(frame, menuBar);
        createDiscoveryMenu(frame, menuBar);
        createGraphToolsMenu(frame, menuBar);
        createNetworkActivationMenu(frame,menuBar);
        createWindowMenu(frame, menuBar);
        createHelpMenu(frame, menuBar);
        return menuBar;
    }
    public JMenuBar getMenubar(final TopologyManagerFrame frame){
        return frame.getJMenuBar();
    }
    private void createHelpMenu(TopologyManagerFrame frame, JMenuBar menuBar) {
        final JMenu help = new JMenu("Help");
        menuBar.add(help);

        final JMenuItem userGuide = new JMenuItem("Users Guide");
        userGuide.addActionListener(new UsersGuideMenuHandler(frame));
        help.add(userGuide);
        final JMenuItem about = new JMenuItem("About");
        about.addActionListener(new AboutMenuHandler(frame));
        help.add(about);
    }

    private void createWindowMenu(TopologyManagerFrame frame, JMenuBar menuBar) {
        final JMenu tabs = new JMenu("Window");
        tabs.setEnabled(false);
        menuBar.add(tabs);
        final JMenuItem newTab = new JMenuItem("New Tab");
        newTab.addActionListener(new NewTabMenuHandler(frame));
        tabs.add(newTab);
        final JMenuItem close = new JMenuItem("Close Tab");
        close.addActionListener(new CloseTabMenuHandler(frame));
        tabs.add(close);
        final JMenuItem closeOthers = new JMenuItem("Close Others Tab");
        closeOthers.addActionListener(new CloseOthersMenuHandler(frame));
        tabs.add(closeOthers);
        final JMenuItem closeAll = new JMenuItem("Close All Tabs");
        closeAll.addActionListener(new CloseAllMenuHandler(frame));
        tabs.add(closeAll);
    }

    private void createGraphToolsMenu(TopologyManagerFrame frame, JMenuBar menuBar) {
        final JMenu graphTools = new JMenu("Graph Tools");
        graphTools.setEnabled(false);
        menuBar.add(graphTools);

        final JMenu layouts = new JMenu("Graph Layouts");
        graphTools.add(layouts);

        final JMenuItem FRLayout = new JMenuItem("FR Layout");
        FRLayout.addActionListener(new ChangeLayoutMenuHandler(frame,"FRLayout"));
        layouts.add(FRLayout);
        final JMenuItem FRLayout2 = new JMenuItem("FR Layout2");
        FRLayout.addActionListener(new ChangeLayoutMenuHandler(frame,"FRLayout2"));
        layouts.add(FRLayout2);

        final JMenuItem KKLayout = new JMenuItem("KK Layout");
        KKLayout.addActionListener(new ChangeLayoutMenuHandler(frame,"KKLayout"));
        layouts.add(KKLayout);
        final JMenuItem CircleLayout = new JMenuItem("Circle Layout");
        CircleLayout.addActionListener(new ChangeLayoutMenuHandler(frame,"CircleLayout"));
        layouts.add(CircleLayout);
        final JMenuItem SpringLayout = new JMenuItem("Spring Layout");
        SpringLayout.addActionListener(new ChangeLayoutMenuHandler(frame,"SpringLayout"));
        layouts.add(SpringLayout);
        final JMenuItem ISOMLayout = new JMenuItem("ISOM Layout");
        SpringLayout.addActionListener(new ChangeLayoutMenuHandler(frame,"ISOMLayout"));
        layouts.add(ISOMLayout);

//        final JMenuItem iNode = new JMenuItem("Initial Node");
//        iNode.addActionListener(new InitialNodeMenuHandler(frame));
//        graphTools.add(iNode);


        final JMenu search = new JMenu("Node Search");
        graphTools.add(search);

        final JMenu rank = new JMenu("Ranking Algorithms");
        graphTools.add(rank);
        final JMenuItem BetweennessCentrality = new JMenuItem("BetweennessCentrality");
        BetweennessCentrality.addActionListener(new BetweennessCentralityMenuHandler(frame));
        rank.add(BetweennessCentrality);
//        final JMenuItem PageRank = new JMenuItem("PageRank");
//        PageRank.addActionListener(new PageRankerMenuHandler(frame));
//        rank.add(PageRank);
//        final JMenuItem PageRankWithPriors = new JMenuItem("PageRankWithPriors");
//        PageRankWithPriors.addActionListener(new PageRankWithPriorsMenuHandler(frame,"PageRankWithPriors"));
//        rank.add(PageRankWithPriors);
        final JMenuItem KStepMarkov = new JMenuItem("KStepMarkov");
        KStepMarkov.addActionListener(new KMarkovMenuHandler(frame));
        rank.add(KStepMarkov);
        final JMenuItem RandomWalkBetweennessCentralityRanker = new JMenuItem("Random Walk Betweenness");
        RandomWalkBetweennessCentralityRanker.addActionListener(new RandomWalkBetweennessCentralityMenuHandler(frame));
        rank.add(RandomWalkBetweennessCentralityRanker);

        final JMenuItem searchByNameCurrent = new JMenuItem("Search by Name CurrentGraph");
        searchByNameCurrent.addActionListener(new SearchByNameCurrGraphMenuHandler(frame));
        search.add(searchByNameCurrent);
        final JMenuItem searchByNameEntire = new JMenuItem("Search by Name EntireGraph");
        searchByNameEntire.addActionListener(new SearchByNameEntireGraphMenuHandler(frame));
        search.add(searchByNameEntire);
        final JMenuItem searchByKey = new JMenuItem("Search by Key");
        searchByKey.addActionListener(new SearchByKeyMenuHandler(frame));
        search.add(searchByKey);
        final JMenuItem searchByIP = new JMenuItem("Search by IP");
        searchByIP.addActionListener(new SearchByIpMenuHandler(frame));
        search.add(searchByIP);


        final JMenu shorestpath = new JMenu("Path Preview");
        menuBar.add(shorestpath);
        graphTools.add(shorestpath);

        final JMenuItem ShortestPath = new JMenuItem("ShortestPath");
        ShortestPath.addActionListener(new ShortestPathMenuHandler(frame));
        shorestpath.add(ShortestPath);
        final JMenuItem DijkstraShortestPath = new JMenuItem("Dijkstra Shortest Path");
        DijkstraShortestPath.addActionListener(new DijkstraShortestPathMenuHandler(frame));
        shorestpath.add(DijkstraShortestPath);
//        final JMenuItem DijkstraWeightedShortestPath = new JMenuItem("Dijkstra Shortest Path");
//        DijkstraWeightedShortestPath.addActionListener(new DijkstraWeightedShortestPathMenuHandler(frame));
//        shorestpath.add(DijkstraWeightedShortestPath);

        final JMenu graphInfo = new JMenu("Graph Distance Statistics");
        menuBar.add(graphInfo);
        graphTools.add(graphInfo);

        final JMenuItem test = new JMenuItem("Diameter");
        test.addActionListener(new GraphDistanceStatisticsMenuHandler(frame));
        graphInfo.add(test);
      //  graphTools.add(graphInfo);



    }



    private void createFileMenu(TopologyManagerFrame frame, JMenuBar menuBar) {
        final JMenu file = new JMenu("File");
        menuBar.add(file);

        final JMenuItem newProject = new JMenuItem("New Project");
        newProject.addActionListener(new NewProjectMenuHandler(frame));
        file.add(newProject);
        final JMenuItem open = new JMenuItem("Open Project");
        open.addActionListener(new OpenProjectMenuHandler(frame));
        file.add(open);
        final JMenuItem close = new JMenuItem("Close Project");
        close.addActionListener(new CloseProjectMenuHandler(frame));
        file.add(close);

        file.addSeparator();

        final JMenuItem openGraph = new JMenuItem("Open Graph");
        openGraph.addActionListener(new OpenGraphMenuHandler(frame));
        openGraph.setEnabled(false);
        file.add(openGraph);
//        final JMenuItem openDirectedGraph = new JMenuItem("Open Directed Graph");
//        openDirectedGraph.addActionListener(new OpenGraphMenuHandler(frame, GraphType.DIRECTED));
//        file.add(openDirectedGraph);
        final JMenuItem closeGraph = new JMenuItem("Close Graph");
        closeGraph.addActionListener(new CloseGraphMenuHandler(frame));
        closeGraph.setEnabled(false);

        file.add(closeGraph);

        final JMenuItem diff = new JMenuItem("Diff Graph");
        diff.addActionListener(new DiffMenuHandler(frame));
        diff.setEnabled(false);
        file.add(diff);

        file.addSeparator();

        final JMenuItem config = new JMenuItem("Viewer Settings");
        config.addActionListener(new ConfigMenuHandler(frame));
        config.setEnabled(false);
        file.add(config);

        final JMenu capture = new JMenu("Export to ...");
        capture.setEnabled(false);
        //file.add(capture);

        JMenuItem captureTpPNGMenuItem = new JMenuItem("Export to PNG");
        captureTpPNGMenuItem.addActionListener(new CaptureToPNGMenuHandler(frame));
        capture.add(captureTpPNGMenuItem);
        JMenuItem captureTpEPSMenuItem = new JMenuItem("Export to EPS");
        captureTpEPSMenuItem.addActionListener(new CaptureToEPSMenuHandler(frame));
        capture.add(captureTpEPSMenuItem);
        file.add(capture);

        file.addSeparator();

        final JMenuItem exit = new JMenuItem("Exit");
        exit.addActionListener(new ExitMenuHandler(frame));
        file.add(exit);
    }

    private void createDiscoveryMenu(TopologyManagerFrame frame, JMenuBar menuBar) {
        final JMenu discovery = new JMenu("Discoverers");
        discovery.setEnabled(false);
        menuBar.add(discovery);
        final JMenu snmpNetworkDiscovery = new JMenu("SNMP Network Discovery");
        final JMenu bgpPeeringNetworkDiscovery = new JMenu("BGP Peering Network Discovery");

        final JMenuItem startDiscovery = new JMenuItem("Start Discovery");
        startDiscovery.addActionListener(new StartDiscoveryMenuHandler(frame));
        snmpNetworkDiscovery.add(startDiscovery);
        final JMenuItem configureResource = new JMenuItem("Configure Resource");
        configureResource.addActionListener(new EditConfigMenuHandler(frame, "iDiscover/conf/xml/discoveryResource.xml"));
        snmpNetworkDiscovery.add(configureResource);
        final JMenuItem configureParameters = new JMenuItem("Configure Parameters");
        configureParameters.addActionListener(new ConfigureParametersMenuHandler(frame));
        snmpNetworkDiscovery.add(configureParameters);


        final JMenuItem startBGPDiscovery = new JMenuItem("Start Discovery");
        startBGPDiscovery.addActionListener(new StartBGPDiscoveryMenuHandler(frame));
        bgpPeeringNetworkDiscovery.add(startBGPDiscovery);
        final JMenuItem configureBGPParameters = new JMenuItem("Configure Parameters");
        configureBGPParameters.addActionListener(new ConfigureBGPParametersMenuHandler(frame));
        bgpPeeringNetworkDiscovery.add(configureBGPParameters);


        discovery.add(snmpNetworkDiscovery);
        discovery.add(bgpPeeringNetworkDiscovery);


    }

    private void createNetworkActivationMenu(TopologyManagerFrame frame, JMenuBar menuBar) {
        final JMenu networkActvation = new JMenu("Network Activation");
        menuBar.add(networkActvation);
        networkActvation.setEnabled(false);
        final JMenuItem ConfigureParamFactoryParameters = new JMenuItem("Configure Parameters");
        ConfigureParamFactoryParameters.addActionListener(new EditConfigMenuHandler(frame, "iTopologyManager/parameterFactory/conf/xml/param-factory.xml"));
        networkActvation.add(ConfigureParamFactoryParameters);
        final JMenuItem configureResources = new JMenuItem("Configure Resources");
        configureResources.addActionListener(new EditConfigMenuHandler(frame, "resourceManager/conf/xml/resource.xml"));
        networkActvation.add(configureResources);

        final JMenuItem configureFulfillmentFactory = new JMenuItem("Configure Bindings");
        configureFulfillmentFactory.addActionListener(new EditConfigMenuHandler(frame, "iTopologyManager/fulfilmentFactory/conf/xml/fulfilment-factory.xml"));
        networkActvation.add(configureFulfillmentFactory);

        final JMenuItem configureTemplates = new JMenuItem("Configure Templates");
        configureTemplates.addActionListener(new TemplateEditorMenuHandler(frame, "iTopologyManager/fulfilmentFactory/conf/templ"));
        networkActvation.add(configureTemplates);

    }



    private ActionListener createMenuHandler(TopologyManagerFrame frame, String handlerClassName) {
        ActionListener handler = null;
        try {
            Class handlerClass = Class.forName(handlerClassName);
            Constructor constructor = handlerClass.getConstructor(new Class<?>[]{TopologyManagerFrame.class});
            return (ActionListener) constructor.newInstance(frame);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return handler;
    }

}
