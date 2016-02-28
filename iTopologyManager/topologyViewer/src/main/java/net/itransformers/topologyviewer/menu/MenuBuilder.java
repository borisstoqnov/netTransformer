/*
 * MenuBuilder.java
 *
 * This work is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation; either version 2 of the License,
 * or (at your option) any later version.
 *
 * This work is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 *
 * Copyright (c) 2010-2016 iTransformers Labs. All rights reserved.
 */

package net.itransformers.topologyviewer.menu;

import net.itransformers.topologyviewer.gui.TopologyManagerFrame;
import net.itransformers.topologyviewer.menu.handlers.*;
import net.itransformers.topologyviewer.menu.handlers.graphFileMenuHandlers.*;
import net.itransformers.topologyviewer.menu.handlers.graphTools.*;
import net.itransformers.topologyviewer.menu.handlers.graphTools.RankingAlgorithms.BetweennessCentralityMenuHandler;
import net.itransformers.topologyviewer.menu.handlers.graphTools.RankingAlgorithms.KMarkovMenuHandler;
import net.itransformers.topologyviewer.menu.handlers.graphTools.RankingAlgorithms.RandomWalkBetweennessCentralityMenuHandler;
import net.itransformers.topologyviewer.menu.handlers.graphTools.searchMenuHandlers.*;
import net.itransformers.topologyviewer.menu.handlers.graphTools.shortherstPathMenuHandlers.DijkstraShortestPathMenuHandler;
import net.itransformers.topologyviewer.menu.handlers.graphTools.shortherstPathMenuHandlers.ShortestPathMenuHandler;
import net.itransformers.topologyviewer.menu.handlers.networkActivation.TemplateEditorMenuHandler;
import net.itransformers.topologyviewer.menu.handlers.projectMenuHandlers.CloseProjectMenuHandler;
import net.itransformers.topologyviewer.menu.handlers.projectMenuHandlers.NewProjectMenuHandler;
import net.itransformers.topologyviewer.menu.handlers.projectMenuHandlers.OpenProjectMenuHandler;
import net.itransformers.topologyviewer.menu.handlers.snmpDiscovery.ConfigureBGPParametersMenuHandler;
import net.itransformers.topologyviewer.menu.handlers.snmpDiscovery.ConfigureParametersMenuHandler;
import net.itransformers.topologyviewer.menu.handlers.snmpDiscovery.EditConfigMenuHandler;
import net.itransformers.topologyviewer.menu.handlers.snmpDiscovery.StartDiscoveryWizardMenuHandler;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;


public class MenuBuilder {
    public JMenuBar createMenuBar(final TopologyManagerFrame frame) {
        JMenuBar menuBar = new JMenuBar();
        //Main menu items
        // File - 0, Discovery -1, Layouts -2 ,Graph - 3, Search - 4, Window - 5, Preferences 6, Help - 7
        createFileMenu(frame, menuBar);
        createDiscoveryMenu(frame, menuBar);
        createModelsMenu(frame,menuBar);
        createLayoutsMenu(frame,menuBar);
        createGraphToolsMenu(frame, menuBar);
        createSearchMenu(frame, menuBar);

//        createNetworkActivationMenu(frame, menuBar);
        createWindowMenu(frame, menuBar);
        createPreferencesMenu(frame, menuBar);
        createHelpMenu(frame, menuBar);
        return menuBar;
    }

    public JMenuBar getMenubar(final TopologyManagerFrame frame) {
        return frame.getJMenuBar();
    }

    private void createHelpMenu(TopologyManagerFrame frame, JMenuBar menuBar) {
        final JMenu help = new JMenu("Help");
        help.setName("Help");
        menuBar.add(help);
        final JMenuItem about = new JMenuItem("About");
        about.setName("About");
        about.addActionListener(new AboutMenuHandler(frame));
        help.add(about);
        final JMenuItem userGuide = new JMenuItem("User Guide");
        userGuide.setName("User Guide");
        userGuide.addActionListener(new UsersGuideMenuHandler(frame));
        help.add(userGuide);
        final JMenuItem forum = new JMenuItem("Forum");
        forum.setName("Forum");
        forum.addActionListener(new ForumMenuHandler(frame));
        help.add(forum);
    }

    private void createWindowMenu(TopologyManagerFrame frame, JMenuBar menuBar) {
        final JMenu tabs = new JMenu("Window");
        tabs.setName("Window");
        tabs.setEnabled(false);
        menuBar.add(tabs);
        final JMenuItem newTab = new JMenuItem("New Tab");
        newTab.addActionListener(new NewTabMenuHandler(frame));
        tabs.add(newTab);
        final JMenuItem close = new JMenuItem("Close Tab");
        close.addActionListener(new CloseTabMenuHandler(frame));
        tabs.add(close);
        final JMenuItem closeOthers = new JMenuItem("Close Other Tabs");
        closeOthers.addActionListener(new CloseOthersMenuHandler(frame));
        tabs.add(closeOthers);
        final JMenuItem closeAll = new JMenuItem("Close All Tabs");
        closeAll.addActionListener(new CloseAllMenuHandler(frame));
        tabs.add(closeAll);
    }
    private void createSearchMenu(TopologyManagerFrame frame, JMenuBar menuBar) {
        final JMenu search = new JMenu("Search");
        search.setName("Search");
        search.setEnabled(false);
        menuBar.add(search);
        final JMenuItem searchNodeByNameCurrent = new JMenuItem("Search Node by Name CurrentGraph");
        searchNodeByNameCurrent.addActionListener(new SearchByNameCurrGraphMenuHandler(frame));
        search.add(searchNodeByNameCurrent);
        final JMenuItem searchNodeByNameEntire = new JMenuItem("Search Node by Name EntireGraph");
        searchNodeByNameEntire.addActionListener(new SearchByNameEntireGraphMenuHandler(frame));
        search.add(searchNodeByNameEntire);
        final JMenuItem searchNodeByKey = new JMenuItem("Search Node by Key");
        searchNodeByKey.addActionListener(new SearchByKeyMenuHandler(frame));
        search.add(searchNodeByKey);
        final JMenuItem searchNodeByIP = new JMenuItem("Search Node by IP");
        searchNodeByIP.addActionListener(new SearchByIpMenuHandler(frame));
        search.add(searchNodeByIP);
        search.addSeparator();

        final JMenuItem searchEdgeByNameCurrent = new JMenuItem("Search Edge by Name CurrentGraph");
        searchEdgeByNameCurrent.addActionListener(new SearchEdgeByNameCurrGraphMenuHandler(frame));
        search.add(searchEdgeByNameCurrent);
        final JMenuItem searchEdgeByNameEntire = new JMenuItem("Search Edge by Name EntireGraph");
        searchEdgeByNameEntire.addActionListener(new SearchEdgeByNameEntireGraphMenuHandler(frame));
        search.add(searchEdgeByNameEntire);
        final JMenuItem searchEdgeByKey = new JMenuItem("Search Edge by Key");
        searchEdgeByKey.addActionListener(new SearchByKeyMenuHandler(frame));
        search.add(searchEdgeByKey);


    }
    private void createLayoutsMenu(TopologyManagerFrame frame, JMenuBar menuBar) {
        final JMenu layouts = new JMenu("Layouts");
        layouts.setName("Layouts");
        layouts.setEnabled(false);
        menuBar.add(layouts);

        final JMenuItem FRLayout = new JMenuItem("FR Layout");



        FRLayout.addActionListener(new ChangeLayoutMenuHandler(frame, "FRLayout"));


//        FRLayout.add(FRLayout);


        layouts.add(FRLayout);
        final JMenuItem KKLayout = new JMenuItem("KK Layout");
        KKLayout.addActionListener(new ChangeLayoutMenuHandler(frame, "KKLayout"));
        layouts.add(KKLayout);
        final JMenuItem CircleLayout = new JMenuItem("Circle Layout");
        CircleLayout.addActionListener(new ChangeLayoutMenuHandler(frame, "CircleLayout"));
        layouts.add(CircleLayout);
        final JMenuItem SpringLayout = new JMenuItem("Spring Layout");
        SpringLayout.addActionListener(new ChangeLayoutMenuHandler(frame, "SpringLayout"));
        layouts.add(SpringLayout);
        final JMenuItem ISOMLayout = new JMenuItem("ISOM Layout");
        SpringLayout.addActionListener(new ChangeLayoutMenuHandler(frame, "ISOMLayout"));
        layouts.add(ISOMLayout);
        final JMenuItem DAGLayout = new JMenuItem("DAG Layout");
        SpringLayout.addActionListener(new ChangeLayoutMenuHandler(frame, "DAGLayout"));
        layouts.add(DAGLayout);
    }

    private void createModelsMenu(TopologyManagerFrame frame, JMenuBar menuBar) {
        final JMenu models = new JMenu("Network Models");
        models.setEnabled(false);
        models.setName("Network Models");
        menuBar.add(models);
        final JMenuItem reviewNetworkCentricModel = new JMenuItem("Tree viewer");
        reviewNetworkCentricModel.addActionListener(new XmlTreeNetworkGraphmlMenuHandler(frame));
        models.add(reviewNetworkCentricModel);

        final JMenuItem editNetworkCentricModel = new JMenuItem("Data viewer");
        editNetworkCentricModel.addActionListener(new EditNetworkGraphmlMenuHandler(frame));
        models.add(editNetworkCentricModel);


    }
    private void createGraphToolsMenu(TopologyManagerFrame frame, JMenuBar menuBar) {
        final JMenu graphTools = new JMenu("Graph Tools");
        graphTools.setName("Graph Tools");
        graphTools.setEnabled(false);
        menuBar.add(graphTools);

//            final JMenuItem iNode = new JMenuItem("Initial Node");
//            iNode.addActionListener(new InitialNodeMenuHandler(frame));
//            graphTools.add(iNode);
        final JMenuItem statistics = new JMenuItem("Graph Statistics");
        statistics.addActionListener(new GraphDistanceStatisticsMenuHandler(frame));
        graphTools.add(statistics);
        final JMenuItem bgstatistics = new JMenuItem("BG Peering Statistics");
        bgstatistics.addActionListener(new BGMapStatisticsMenuHandler(frame));
        graphTools.add(bgstatistics);
        final JMenuItem diffStatistics = new JMenuItem("Diff Statistics");
        diffStatistics.addActionListener(new DiffReportMenuHandler(frame));
        graphTools.add(diffStatistics);


        final JMenuItem ShortestPath = new JMenuItem("ShortestPath");
        ShortestPath.addActionListener(new ShortestPathMenuHandler(frame));
        graphTools.add(ShortestPath);
        final JMenuItem DijkstraShortestPath = new JMenuItem("Dijkstra Shortest Path");
        DijkstraShortestPath.addActionListener(new DijkstraShortestPathMenuHandler(frame));
        graphTools.add(DijkstraShortestPath);

        graphTools.addSeparator();

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

    }


    private void createFileMenu(TopologyManagerFrame frame, JMenuBar menuBar) {
        final JMenu file = new JMenu("File");
        file.setName("File");
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

        final JMenuItem addGraph = new JMenuItem("Add Graph");
        addGraph.addActionListener(new AddGraphMenuHandler(frame));
        addGraph.setEnabled(false);
        file.add(addGraph);


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

//        final JMenuItem config = new JMenuItem("Viewer Settings");
//        config.addActionListener(new ConfigMenuHandler(frame));
//        config.setEnabled(false);
//        file.add(config);

        final JMenu capture = new JMenu("Export Graph to ...");
        capture.setEnabled(false);
        //file.add(capture);

        JMenuItem captureTpPNGMenuItem = new JMenuItem("Export to PNG");
        captureTpPNGMenuItem.addActionListener(new CaptureToPNGMenuHandler(frame));
        capture.add(captureTpPNGMenuItem);
        JMenuItem captureTpEPSMenuItem = new JMenuItem("Export to EPS");
        captureTpEPSMenuItem.addActionListener(new CaptureToEPSMenuHandler(frame));
        capture.add(captureTpEPSMenuItem);
        file.add(capture);

        final JMenuItem saveGraph = new JMenuItem("Export Current Graph to Graphml");
        saveGraph.addActionListener(new SaveCurrentGraphMenuHandler(frame));
        saveGraph.setEnabled(true);
        capture.add(saveGraph);


        file.addSeparator();

        final JMenuItem exit = new JMenuItem("Exit");
        exit.addActionListener(new ExitMenuHandler(frame));
        file.add(exit);
    }

    private void createDiscoveryMenu(TopologyManagerFrame frame, JMenuBar menuBar) {
        final JMenu discovery = new JMenu("Discover");
        discovery.setName("Discover");
        discovery.setEnabled(false);
        menuBar.add(discovery);

        final JMenuItem startDiscoveryWizard = new JMenuItem("Discovery Wizard");
        startDiscoveryWizard.addActionListener(new StartDiscoveryWizardMenuHandler(frame));
        discovery.add(startDiscoveryWizard);

        final JMenuItem startDiscovery = new JMenuItem("Discover Now!");
        startDiscovery.addActionListener(new StartDiscoveryMenuHandler(frame));
        discovery.add(startDiscovery);


//        final JMenuItem startBGPDiscovery = new JMenuItem("Internet BGP Peering");
//        startBGPDiscovery.addActionListener(new StartBGPDiscoveryMenuHandler(frame));
//        discovery.add(startBGPDiscovery);

    }
    private void createPreferencesMenu(TopologyManagerFrame frame, JMenuBar menuBar) {
        final JMenu configuration = new JMenu("Configuration");
        configuration.setName("Configuration");
        menuBar.add(configuration);

        configuration.setEnabled(false);


//        final JMenu labels = new JMenu("Show/Hide Labels");
        final JMenuItem nodeLabels = new JMenuItem("Show/Hide Node Labels");
        nodeLabels.addActionListener(new ShowHideNodeLabelsMenuHandler(frame));
        configuration.add(nodeLabels);

        final JMenuItem edgeLabels = new JMenuItem("Show/Hide Edge Labels");
        edgeLabels.addActionListener(new ShowHideEdgeLabelsMenuHandler(frame));
        configuration.add(edgeLabels);
        configuration.addSeparator();

        final JMenu snmpDiscoverer = new JMenu("IP Network Discovery");
        snmpDiscoverer.setName("SNMP Discoverer");
        snmpDiscoverer.setEnabled(false);
        configuration.add(snmpDiscoverer);

        final JMenuItem resources = new JMenuItem("Resource editor");
        resources.addActionListener(new EditConfigMenuHandler(frame, "resourceManager/conf/xml/resource.xml"));
        snmpDiscoverer.add(resources);

        final JMenuItem configureManager = new JMenuItem("Discovery entry points editor");
        configureManager.addActionListener(new EditConfigMenuHandler(frame, "iDiscover/conf/txt/connection-details.txt"));
        snmpDiscoverer.add(configureManager);

        final JMenuItem configureParameters = new JMenuItem("SNMP Discovery Parameters editor");
        configureParameters.addActionListener(new ConfigureParametersMenuHandler(frame));
        snmpDiscoverer.add(configureParameters);

        final JMenuItem viewerSettingsItem = new JMenuItem("Viewer configuration editor");

        viewerSettingsItem.addActionListener(new ConfigMenuHandler(frame));
        snmpDiscoverer.add(viewerSettingsItem);

        final JMenu diffSettings = new JMenu("Diff Ignored keys editor");
        final JMenuItem ignoredNodeKeys = new JMenuItem("ignoredNodeKeys");
        ignoredNodeKeys.addActionListener(new EditConfigMenuHandler(frame, "iTopologyManager/topologyViewer/conf/xml/ignored_node_keys.xml"));
        diffSettings.add(ignoredNodeKeys);

        final JMenuItem ignoredEdgeKeys = new JMenuItem("ignoredEdgeKeys");
        ignoredEdgeKeys.addActionListener(new EditConfigMenuHandler(frame, "iTopologyManager/topologyViewer/conf/xml/ignored_edge_keys.xml"));
        diffSettings.add(ignoredEdgeKeys);

        snmpDiscoverer.add(diffSettings);
        //configuration.addSeparator();



        final JMenu networkActvation = new JMenu("CLI configuration settings");
        snmpDiscoverer.add(networkActvation);


        final JMenuItem ConfigureParamFactoryParameters = new JMenuItem("Configure Parameters");
        ConfigureParamFactoryParameters.addActionListener(new TemplateEditorMenuHandler(frame, "iTopologyManager/parameterFactory/conf/xml", ".xml"));
        networkActvation.add(ConfigureParamFactoryParameters);

        final JMenuItem configureFulfillmentFactory = new JMenuItem("Configure Bindings");
        configureFulfillmentFactory.addActionListener(new TemplateEditorMenuHandler(frame, "iTopologyManager/fulfilmentFactory/conf/xml/", ".xml"));
        networkActvation.add(configureFulfillmentFactory);

        final JMenuItem configureTemplates = new JMenuItem("Configure Templates");
        configureTemplates.addActionListener(new TemplateEditorMenuHandler(frame, "iTopologyManager/fulfilmentFactory/conf/templ", ".templ"));
        networkActvation.add(configureTemplates);

        final JMenuItem configureGroovyTemplates = new JMenuItem("Configure Groovy Templates");
        configureGroovyTemplates.addActionListener(new TemplateEditorMenuHandler(frame, "postDiscoverer/conf/groovy", ".groovy"));
        networkActvation.add(configureGroovyTemplates);


        final JMenuItem postDiscovery = new JMenuItem("POST discovery settings");
        postDiscovery.addActionListener(new EditConfigMenuHandler(frame, "postDiscoverer/conf/xml/reportGenerator.xml"));
        snmpDiscoverer.add(postDiscovery);


        final JMenu bgpPeeringDiscovery = new JMenu("Internet BGP Peering");
        bgpPeeringDiscovery.setName("Internet BGP Peering");
        bgpPeeringDiscovery.setEnabled(false);

        final JMenuItem configureBGPParameters = new JMenuItem("BGP peering Map Parameters");
        configureBGPParameters.addActionListener(new ConfigureBGPParametersMenuHandler(frame));
        bgpPeeringDiscovery.add(configureBGPParameters);
        final JMenuItem bgpViewerSettingsItem = new JMenuItem("Viewer Configuration Editor");

        bgpViewerSettingsItem.addActionListener(new ConfigMenuHandler(frame));
        bgpPeeringDiscovery.add(bgpViewerSettingsItem);

        final JMenu bgpDiffSettings = new JMenu("Diff Ignored keys Editor");
        final JMenuItem bgpIgnoredNodeKeys = new JMenuItem("ignoredNodeKeys");

        bgpIgnoredNodeKeys.addActionListener(new EditConfigMenuHandler(frame, "iTopologyManager/topologyViewer/conf/xml/bgpPeeringMap/ignored_node_keys.xml"));
        bgpDiffSettings.add(bgpIgnoredNodeKeys);

        final JMenuItem bgpIgnoredEdgeKeys = new JMenuItem("ignoredEdgeKeys");

        bgpIgnoredEdgeKeys.addActionListener(new EditConfigMenuHandler(frame, "iTopologyManager/topologyViewer/conf/xml/bgpPeeringMap/ignored_edge_keys.xml"));
        bgpDiffSettings.add(bgpIgnoredEdgeKeys);

        bgpPeeringDiscovery.add(bgpDiffSettings);

        //configuration.add(discovery);
        configuration.add(bgpPeeringDiscovery);



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
