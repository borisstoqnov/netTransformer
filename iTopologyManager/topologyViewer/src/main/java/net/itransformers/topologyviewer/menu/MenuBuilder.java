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

import net.itransformers.topologyviewer.gui.TopologyViewer;
import net.itransformers.topologyviewer.menu.handlers.*;

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
    public JMenuBar createMenuBar(final TopologyViewer frame) {
        JMenuBar menuBar = new JMenuBar();
//        TopologyViewerConfType viewerConfig = frame.getViewerConfig();
//        List<MenuItemType> menuItems = viewerConfig.getMenuItem();
//        for (MenuItemType menuItem : menuItems) {
//            String menuItemName = menuItem.getName();
//            final String handlerClassName = menuItem.getHandlerClass();
//            ActionListener handler = createMenuHandler(frame, handlerClassName);
//
//        }


        final JMenu file = new JMenu("File");
        final JMenu search = new JMenu("Node Search");
        final JMenu shorestpath = new JMenu("Path Preview");
        final JMenu tabs = new JMenu("Tabs");
        final JMenu help = new JMenu("Help");
        final JMenu capture = new JMenu("Capture");

        menuBar.add(file);
        menuBar.add(tabs);
        menuBar.add(search);
        menuBar.add(shorestpath);
        menuBar.add(capture);
        menuBar.add(help);
        JMenuItem captureTpPNGMenuItem = new JMenuItem("Capture to PNG");
        captureTpPNGMenuItem.addActionListener(new CaptureToPNGMenuHandler(frame));
        capture.add(captureTpPNGMenuItem);
        JMenuItem captureTpEPSMenuItem = new JMenuItem("Capture to EPS");
        captureTpEPSMenuItem.addActionListener(new CaptureToEPSMenuHandler(frame));
        capture.add(captureTpEPSMenuItem);
        final JMenuItem open = new JMenuItem("Open");
        open.addActionListener(new OpenMenuHandler(frame));
        file.add(open);
        final JMenuItem openRemote = new JMenuItem("Open Remote");
        openRemote.addActionListener(new OpenRemoteMenuHandler(frame));
        file.add(openRemote);
        final JMenuItem config = new JMenuItem("Config");
        config.addActionListener(new ConfigMenuHandler(frame));
        file.add(config);
        final JMenuItem diff = new JMenuItem("Diff");
        diff.addActionListener(new DiffMenuHandler(frame));
        file.add(diff);
        final JMenuItem remoteConfig = new JMenuItem("RemoteConfig");
        remoteConfig.addActionListener(new RemoteConfigMenuHandler(frame));
        file.add(remoteConfig);
        final JMenuItem iNode = new JMenuItem("Initial Node");
        iNode.addActionListener(new InitialNodeMenuHandler(frame));
        file.add(iNode);
        final JMenuItem exit = new JMenuItem("Exit");
        exit.addActionListener(new ExitMenuHandler(frame));
        file.add(exit);
        final JMenuItem ShortestPath = new JMenuItem("ShortestPath");
        ShortestPath.addActionListener(new ShortestPathMenuHandler(frame));
        shorestpath.add(ShortestPath);
        final JMenuItem WeightedShortestPath = new JMenuItem("Weighted Shortest Path");
        WeightedShortestPath.addActionListener(new WeightedShortestPathMenuHandler(frame));
        shorestpath.add(WeightedShortestPath);

        final JMenuItem searchByNameCurrent = new JMenuItem("Search by Name CurrentGraph");
        searchByNameCurrent.addActionListener(new SearchByNameCurrGraphMenuHandler(frame));
        search.add(searchByNameCurrent);
        final JMenuItem searchByNameEntire = new JMenuItem("Search by Name EntireGraph");
        searchByNameEntire.addActionListener(new SearchByNameEntireGraphMenuHandler(frame));
        search.add(searchByNameEntire);
        final JMenuItem searchByKey = new JMenuItem("Search by Key");
        searchByKey.addActionListener(new SearchByKeyMenuHandler(frame));
        search.add(searchByKey);

        final JMenuItem newTab = new JMenuItem("New Tab");
        newTab.addActionListener(new NewTabMenuHandler(frame));
        tabs.add(newTab);
        final JMenuItem close = new JMenuItem("Close");
        close.addActionListener(new CloseMenuHandler(frame));
        tabs.add(close);
        final JMenuItem closeAll = new JMenuItem("Close All");
        closeAll.addActionListener(new CloseAllMenuHandler(frame));
        tabs.add(closeAll);
        final JMenuItem userGuide = new JMenuItem("Users Guide");
        userGuide.addActionListener(new UsersGuideMenuHandler(frame));
        help.add(userGuide);
        final JMenuItem about = new JMenuItem("About");
        about.addActionListener(new AboutMenuHandler(frame));
        help.add(about);

        return menuBar;
    }

    private ActionListener createMenuHandler(TopologyViewer frame, String handlerClassName) {
        ActionListener handler = null;
        try {
            Class handlerClass = Class.forName(handlerClassName);
            Constructor constructor = handlerClass.getConstructor(new Class<?>[]{TopologyViewer.class});
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
