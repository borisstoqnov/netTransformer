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
import net.itransformers.topologyviewer.menu.handlers.RankingAlgorithms.BetweennessCentralityMenuHandler;
import net.itransformers.topologyviewer.menu.handlers.RankingAlgorithms.KMarkovMenuHandler;
import net.itransformers.topologyviewer.menu.handlers.RankingAlgorithms.RandomWalkBetweennessCentralityMenuHandler;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by IntelliJ IDEA.
 * Date: 12-4-27
 * Time: 23:11
 * To change this template use File | Settings | File Templates.
 */
public class MenuBuilder {
    public JMenuBar createMenuBar(final TopologyManagerFrame frame) {
        JMenuBar menuBar = new JMenuBar();

        createFileMenu(frame, menuBar);
        createDiscoveryMenu(frame, menuBar);
        createGraphToolsMenu(frame, menuBar);
        createWindowMenu(frame, menuBar);
        createHelpMenu(frame, menuBar);
        return menuBar;
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

        final JMenuItem iNode = new JMenuItem("Initial Node");
        iNode.addActionListener(new InitialNodeMenuHandler(frame));
        graphTools.add(iNode);


        final JMenu search = new JMenu("Node Search");
        graphTools.add(search);

        final JMenu rank = new JMenu("Ranking Algorithms");
        graphTools.add(rank);
        final JMenuItem BetweennessCentrality = new JMenuItem("BetweennessCentrality");
        BetweennessCentrality.addActionListener(new BetweennessCentralityMenuHandler(frame));
        rank.add(BetweennessCentrality);
        final JMenuItem PageRank = new JMenuItem("PageRank");
        PageRank.addActionListener(new PageRankerMenuHandler(frame));
        rank.add(PageRank);
        final JMenuItem PageRankWithPriors = new JMenuItem("PageRankWithPriors");
        PageRankWithPriors.addActionListener(new ChangeLayoutMenuHandler(frame,"PageRankWithPriors"));
        rank.add(PageRankWithPriors);
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
        final JMenuItem DijkstraWeightedShortestPath = new JMenuItem("Dijkstra Shortest Path");
        DijkstraWeightedShortestPath.addActionListener(new DijkstraWeightedShortestPathMenuHandler(frame));
        shorestpath.add(DijkstraWeightedShortestPath);

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
        file.add(openGraph);
//        final JMenuItem openDirectedGraph = new JMenuItem("Open Directed Graph");
//        openDirectedGraph.addActionListener(new OpenGraphMenuHandler(frame, GraphType.DIRECTED));
//        file.add(openDirectedGraph);
        final JMenuItem closeGraph = new JMenuItem("Close Graph");
        closeGraph.addActionListener(new CloseGraphMenuHandler(frame));
        file.add(closeGraph);

        final JMenuItem diff = new JMenuItem("Diff Graph");
        diff.addActionListener(new DiffMenuHandler(frame));
        file.add(diff);

        file.addSeparator();

//        final JMenuItem openRemote = new JMenuItem("Open Remote");
//        openRemote.addActionListener(new OpenRemoteMenuHandler(frame));
//        file.add(openRemote);
        final JMenuItem config = new JMenuItem("Settings");
        config.addActionListener(new ConfigMenuHandler(frame));
        file.add(config);
//        final JMenuItem remoteConfig = new JMenuItem("RemoteConfig");
//        remoteConfig.addActionListener(new RemoteConfigMenuHandler(frame));
//        file.add(remoteConfig);

        final JMenu capture = new JMenu("Export to ...");
        file.add(capture);

        JMenuItem captureTpPNGMenuItem = new JMenuItem("Export to PNG");
        captureTpPNGMenuItem.addActionListener(new CaptureToPNGMenuHandler(frame));
        capture.add(captureTpPNGMenuItem);
        JMenuItem captureTpEPSMenuItem = new JMenuItem("Export to EPS");
        captureTpEPSMenuItem.addActionListener(new CaptureToEPSMenuHandler(frame));
        capture.add(captureTpEPSMenuItem);

        file.addSeparator();

        final JMenuItem exit = new JMenuItem("Exit");
        exit.addActionListener(new ExitMenuHandler(frame));
        file.add(exit);
    }

    private void createDiscoveryMenu(TopologyManagerFrame frame, JMenuBar menuBar) {
        final JMenu discovery = new JMenu("Discovery");
        menuBar.add(discovery);

        final JMenuItem startDiscovery = new JMenuItem("Start Discovery");
        startDiscovery.addActionListener(new StartDiscoveryMenuHandler(frame));
        discovery.add(startDiscovery);
        final JMenuItem configureResource = new JMenuItem("Configure Resource");
        configureResource.addActionListener(new ConfigureResourceMenuHandler(frame));
        discovery.add(configureResource);
        final JMenuItem configureParameters = new JMenuItem("Configure Parameters");
        configureParameters.addActionListener(new ConfigureParametersMenuHandler(frame));
        discovery.add(configureParameters);
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
