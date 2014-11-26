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

import edu.uci.ics.jung.algorithms.filters.EdgePredicateFilter;
import edu.uci.ics.jung.algorithms.filters.KNeighborhoodFilter;
import edu.uci.ics.jung.algorithms.filters.VertexPredicateFilter;
import edu.uci.ics.jung.algorithms.layout.*;
import edu.uci.ics.jung.algorithms.layout.SpringLayout;
import edu.uci.ics.jung.algorithms.shortestpath.BFSDistanceLabeler;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;
import edu.uci.ics.jung.io.GraphMLMetadata;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.Layer;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.*;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.layout.PersistentLayout;
import edu.uci.ics.jung.visualization.picking.MultiPickedState;
import edu.uci.ics.jung.visualization.picking.PickedState;
import edu.uci.ics.jung.visualization.renderers.DefaultEdgeLabelRenderer;
import edu.uci.ics.jung.visualization.renderers.DefaultVertexLabelRenderer;
import net.itransformers.topologyviewer.config.*;
import net.itransformers.topologyviewer.config.datamatcher.DataMatcher;
import net.itransformers.topologyviewer.rightclick.RightClickInvoker;
import org.apache.commons.collections15.functors.ConstantTransformer;
import org.apache.log4j.Logger;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;


public class GraphViewerPanel<G extends Graph<String, String>> extends JPanel {
    static Logger logger = Logger.getLogger(GraphViewerPanel.class);
    private MyVisualizationViewer vv;
    private TopologyViewerConfType viewerConfig;
    private GraphmlLoader<G> graphmlLoader;
    private G entireGraph;
    private FilterType currentFilter;
    private Integer currentHops;
    private G currentGraph;
    private File currentDir;
    private File deviceXmlPath;
    private File path;
    private File versionDir;
    private File graphmlDir;
    private String initialNode;
    private JFrame parent;
    private String layout;
    private boolean vertexLabel;
    private JComboBox jComboBox = new JComboBox();


    private boolean edgeLabel;
    DefaultModalGraphMouse graphMouse;
    private JPanel controls;


    public GraphViewerPanel(JFrame parent, TopologyViewerConfType viewerConfig,
                            GraphmlLoader<G> graphmlLoader,
                            IconMapLoader iconMapLoader,
                            EdgeStrokeMapLoader edgeStrokeMapLoader,
                            EdgeColorMapLoader edgeColorMapLoader,
                            G entireGraph, File path, File versionDir, File deviceXmlPath, File graphmlDir, String initialNode, String layout) {
        super();
        this.parent = parent;
        this.viewerConfig = viewerConfig;
        this.graphmlLoader = graphmlLoader;
        this.entireGraph = entireGraph;
        this.versionDir = versionDir;
        this.graphmlDir = graphmlDir;
        this.initialNode = initialNode;
        this.path = path;
        this.deviceXmlPath = deviceXmlPath;
        this.layout = layout;
        this.graphMouse = new DefaultModalGraphMouse();
        vv = new MyVisualizationViewer(viewerConfig, entireGraph,
                graphmlLoader.getVertexMetadatas(),
                graphmlLoader.getEdgeMetadatas(),
                iconMapLoader.getIconMap(),
                edgeStrokeMapLoader.getEdgesStrokeMap(),
                edgeColorMapLoader.getEdgesColorMap());
        //   vv.setGraphMouse(graphMouse);
//       vv.setPreferredSize(new Dimension(parentSize.width-50,parentSize.height-150));
//        int vertexCount = entireGraph.getVertexCount();
//        if (vertexCount >= 2000){
//            vv.setPreferredSize(new Dimension(parentSize.width*10,parentSize.height*10));
//        }else if (vertexCount < c999 && vertexCount > 200 ){
//            vv.setPreferredSize(new Dimension(parentSize.width*5,parentSize.height*5));
//        }else {
//           vv.setPreferredSize(new Dimension(parentSize.width+50,parentSize.height-150));
//        }
        createPanel();
    }

    private void createPanel() {
        final GraphZoomScrollPane panel = new GraphZoomScrollPane(vv);
        this.setLayout(new BorderLayout());
        this.add(panel);

        AbstractModalGraphMouse graphMouse = createModalGraphMouse();

        vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller<String>());
        vv.getRenderContext().setVertexLabelRenderer(new MyDefaultVertexLabelRenderer(Color.BLACK, Color.RED));
       // vv.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller<String>());
        vv.getRenderContext().setEdgeLabelTransformer(new ConstantTransformer(null));
        vv.getRenderContext().setEdgeLabelRenderer(new MyDefaultEdgeLabelRenderer(Color.BLACK, Color.RED));

        vv.getRenderContext().setLabelOffset(18);
        setEdgeLabel(false);
        setVertexLabel(true);

//
//            @Override
//            public <T> Component getVertexLabelRendererComponent(JComponent jComponent, Object o, Font font, boolean b, T t) {
//                JLabel jLabel = new JLabel(t.toString());
//                //TOOD has to be a prefference setting
//                Font font1 = new Font(font.getName(), font.getStyle()+Font.BOLD, font.getSize()+2);
//                jLabel.setFont(font1);
//                return jLabel;
//            }
//        });
        vv.setGraphMouse(graphMouse);
        vv.setToolTipText("<html><center>Type 'p' for Pick mode<p>Type 't' for Transform mode");

        final ScalingControl scaler = new CrossoverScalingControl();
        JToggleButton mouseModeButton = createMouseModeButton(graphMouse);
        JButton plus = createZoomInButton(scaler);
        JButton minus = createZoomOutButton(scaler);
        createFilterCombo(jComboBox);

        JComboBox hopsCombo = createHopsCombo();
        JButton update = createUpdateButton();
        JButton reload = createReloadButton();
        JButton saveView = createSaveButton();
        JButton loadView = createLoadButton();
        JButton redraw = createRedrawAroundButton();
        JButton hideEdgeLabels = hideEdgeLabels();
        controls = new JPanel();
        controls.add(saveView);
        controls.add(loadView);
        controls.add(mouseModeButton);
        controls.add(jComboBox);
        controls.add(update);
        controls.add(reload);
        controls.add(plus);
        controls.add(minus);
        controls.add(hopsCombo);
        controls.add(redraw);
        controls.add(createCaptureButton());
        this.add(controls, BorderLayout.SOUTH);

        if (!entireGraph.containsVertex(initialNode)) {
            applyFilter(currentFilter, currentHops);
        } else {
            SetPickedState(initialNode);
            Set<String> vertexes = new HashSet<String>();
            vertexes.add(initialNode);
            applyFilter(currentFilter, currentHops, vertexes);
        }

//        Animator(initialNode);

        //controls.add(hideEdgeLabels);

//        JPanel jp2 = new JPanel();
//		jp2.add(new JLabel("vertex from", SwingConstants.LEFT));
////		jp2.add(getSelectionBox(true));
////		jp2.setBackground(Color.white);
//		JPanel jp3 = new JPanel();
//		jp3.add(new JLabel("vertex to", SwingConstants.LEFT));
////		jp3.add(getSelectionBox(false));
////		jp3.setBackground(Color.white);
//
//        controls.add( jp2 );
//		controls.add( jp3 );


    }
//    private Component getSelectionBox(final boolean from) {
//		ToStringLabeller s1;
//		Set s = new TreeSet();
//
//		for (Iterator iter = entireGraph.getVertices().iterator();
//			iter.hasNext();
//			) {
//			s.add(sl.getLabel((GraphEvent.Vertex) iter.next()));
//		}
//		final JComboBox choices = new JComboBox(s.toArray());
//		choices.setSelectedIndex(-1);
//		choices.setBackground(Color.WHITE);
//		choices.addActionListener(new ActionListener() {
//
//			public void actionPerformed(ActionEvent e) {
//				StringLabeller sl = StringLabeller.getLabeller(entireGraph);
//				GraphEvent.Vertex v = sl.getVertex((String) choices.getSelectedItem());
//				if (from) {
//					mFrom = v;
////					System.out.println("Assigned mFrom!");
//				} else {
//					mTo = v;
////					System.out.println("Assigned mTo!");
//				}
//				drawShortest();
//				repaint();
//			}
//
//		});
//		return choices;
//	}

    static class LayoutFileFilter extends FileFilter {
        @Override
        public boolean accept(File f) {
            return f.isFile() && f.getName().endsWith(".layout");
        }

        @Override
        public String getDescription() {
            return "*.layout";
        }
    }


    private JButton createLoadButton() {
        JButton load = new JButton("Load");
        load.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                PersistentLayout pl = (PersistentLayout) vv.getGraphLayout();
                try {
                    JFileChooser chooser = new JFileChooser(currentDir);
                    chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                    chooser.setMultiSelectionEnabled(false);
                    chooser.setFileFilter(new LayoutFileFilter());
                    int result = chooser.showOpenDialog(GraphViewerPanel.this);
                    if (result == JFileChooser.APPROVE_OPTION) {
                        currentDir = chooser.getCurrentDirectory();
                        String absolutePath = chooser.getSelectedFile().getAbsolutePath();
                        if (!absolutePath.endsWith(".layout")) {
                            absolutePath += ".layout";
                        }
                        pl.restore(absolutePath);
                        vv.repaint();
                    }
                } catch (Exception e1) {
                    e1.printStackTrace();
                    JOptionPane.showMessageDialog(GraphViewerPanel.this, "Error restoring layout: " + e1.getMessage());
                }
            }
        });
        return load;
    }

    //    public void captureScreen(String fileName){
//        try {
//    Robot robot = new Robot();
//
//    // Capture a particular area on the screen
//    int x = 100;
//    int y = 100;
//    int width = 200;
//    int height = 200;
//    Rectangle area = new Rectangle(x, y, width, height);
//    BufferedImage bufferedImage = robot.createScreenCapture(area);
//
//    // Capture the whole screen
//    area = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
//    bufferedImage = robot.createScreenCapture(area);
//            try {
//        OutputStream out = new FileOutputStream(fileName);
//           JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
//         encoder.encode(bufferedImage);
//         out.close();
//       } catch (Exception e) {
//         e.printStackTrace();
//      }
//} catch (AWTException e) {
//}
//    }
//    public void captureToFile(String file){
//          vv.setDoubleBuffered( false );
//
//        // capture: create a BufferedImage
//        // create the Graphics2D object that paints to it
//       Layout pl = vv.getGraphLayout();
//       int height = pl.getSize().height;
//       int width = pl.getSize().width;
//        System.out.println("X: "+ width+"Y: "+ height);
//         BufferedImage myImage = new BufferedImage(width,height, BufferedImage.TYPE_INT_RGB);
//         Graphics2D g2 = myImage.createGraphics();
////        vv.repaint();
//        vv.paintComponent(g2);
//         vv.setDoubleBuffered( true );
//       try {
//        OutputStream out = new FileOutputStream(file);
//           JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
//         encoder.encode(myImage);
//         out.close();
//       } catch (Exception e) {
//         e.printStackTrace();
//      }
//    }
//
//
//
//     public void writeJPEGImage(String filename) {
//        PersistentLayout pl = (PersistentLayout) vv.getGraphLayout();
//        int height = pl.getSize().height;
//        int width = pl.getSize().width;
//        Color bg = getBackground();
//
//        BufferedImage bi = new BufferedImage(width,height,BufferedImage.TYPE_3BYTE_BGR);
//        Graphics2D graphics = bi.createGraphics();
//        graphics.setColor(bg);
//        graphics.fillRect(0,0, width, height);
//
//        vv.paintComponent(graphics);
//        try{
//           ImageIO.write(bi, "jpg", new File(filename));
//        }catch(Exception e){e.printStackTrace();}
//
//}
////    public void writeImage(String filename) {
////        PersistentLayout pl = (PersistentLayout) vv.getGraphLayout();
//////        pl.setSize(innerSize);
////     VisualizationImageServer   bvs = new VisualizationImageServer<V,E>(pl);
////        // [...]
////        BufferedImage image = (BufferedImage)bvs.getImage();
////    }
//
    public void writeToImageFile(String imageFileName) throws AWTException {

        final GraphZoomScrollPane panel = new GraphZoomScrollPane(vv);
        PersistentLayout pl = (PersistentLayout) vv.getGraphLayout();
        int height = pl.getSize().height;
        int width = pl.getSize().width;
        panel.setSize(width, height);
        BufferedImage bufImage = ScreenImage.createImage(panel);
        panel.setVisible(false);
        try {
            File outFile = new File(imageFileName);
            ImageIO.write(bufImage, "png", outFile);
            System.out.println("wrote image to " + imageFileName);
        } catch (Exception e) {
            System.out.println("writeToImageFile(): " + e.getMessage());
        }
    }

    private JButton createCaptureButton() {
        JButton capture = new JButton("Capture");
        capture.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                JFileChooser chooser = new JFileChooser(currentDir);
                chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                chooser.setMultiSelectionEnabled(false);
                chooser.setFileFilter(new PngFileFilter());
                int result = chooser.showSaveDialog(GraphViewerPanel.this);
                if (result == JFileChooser.APPROVE_OPTION) {
                    currentDir = chooser.getCurrentDirectory();
                    String absolutePath = chooser.getSelectedFile().getAbsolutePath();
                    if (!absolutePath.endsWith(".png")) {
                        absolutePath += ".png";
                    }
                    try {
                        vv.setDoubleBuffered(false);
                        writeToImageFile(absolutePath);
                        vv.setDoubleBuffered(true);
                    } catch (AWTException e1) {
                        e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
//                        captureToFile(absolutePath);
//                        writeJPEGImage(absolutePath + ".JPEG");
//                        captureScreen(absolutePath);
                }
            }
        });
        return capture;
    }


    //http://www.jarvana.com/jarvana/view/jung/jung/1.7.5/jung-1.7.5-sources.jar!/samples/graph/PersistentLayoutDemo.java?format=ok
    private JButton createSaveButton() {
        JButton save = new JButton("Save");
        save.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                PersistentLayout pl = (PersistentLayout) vv.getGraphLayout();
                try {
                    JFileChooser chooser = new JFileChooser(currentDir);
                    chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                    chooser.setMultiSelectionEnabled(false);
                    chooser.setFileFilter(new LayoutFileFilter());
                    int result = chooser.showSaveDialog(GraphViewerPanel.this);
                    if (result == JFileChooser.APPROVE_OPTION) {
                        currentDir = chooser.getCurrentDirectory();
                        String absolutePath = chooser.getSelectedFile().getAbsolutePath();
                        if (!absolutePath.endsWith(".layout")) {
                            absolutePath += ".layout";
                        }
                        pl.persist(absolutePath);
                    }
                } catch (IOException e1) {
                    e1.printStackTrace();
                    JOptionPane.showMessageDialog(GraphViewerPanel.this, "Error saving layout: " + e1.getMessage());
                }
            }
        });
        return save;
    }

//    private JMenu createModeMenu(AbstractModalGraphMouse graphMouse) {
//        JMenu modeMenu = graphMouse.getModeMenu(); // Obtain mode menu from the mouse
//        modeMenu.setText("Mouse Mode");
//        modeMenu.setIcon(null); // I'm using this in a main menu
//        modeMenu.setPreferredSize(new Dimension(80,20)); // Change the size         menuBar.add(modeMenu);
//        return modeMenu;
//    }

    private JToggleButton createMouseModeButton(final AbstractModalGraphMouse graphMouse) {
        final String selectText = "Move graph";
        final JToggleButton button = new JToggleButton(selectText);
        button.setToolTipText("If toggled then you can move the entire graph.");
        graphMouse.setMode(ModalGraphMouse.Mode.PICKING);
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (button.isSelected()) {
                    graphMouse.setMode(ModalGraphMouse.Mode.TRANSFORMING);
                } else {
                    graphMouse.setMode(ModalGraphMouse.Mode.PICKING);
                }
            }
        });

        return button;
    }

    private AbstractModalGraphMouse createModalGraphMouse() {
        final AbstractModalGraphMouse graphMouse = new DefaultModalGraphMouse<String, String>();
        graphMouse.add(new AbstractPopupGraphMousePlugin() {
            @Override
            protected void handlePopup(final MouseEvent e) {
                GraphElementAccessor<String, String> pickSupport = vv.getPickSupport();
//                final String v = pickSupport.getVertex(vv.getGraphLayout(), e.getX(), e.getY());
                Collection<String> picked = new HashSet(vv.getPickedVertexState().getPicked());
                final String[] varr = picked.toArray(new String[picked.size()]);
                final java.util.List<RightClickItemType> rightClickItem = GraphViewerPanel.this.viewerConfig.getRightClickItem();
                JPopupMenu popup = new JPopupMenu();
                fillRightClickMenu(varr, rightClickItem, popup);
                JMenuItem remove = new JMenuItem("remove");
                popup.add(remove);
                remove.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent event) {
                        for (String v : varr) {
                            currentGraph.removeVertex(v);
                        }
                        vv.repaint();
                    }
                });
                popup.show(vv, e.getX(), e.getY());
            }
        });
        return graphMouse;

    }

    private <T extends JComponent> void fillRightClickMenu(final String[] varr, java.util.List<RightClickItemType> rightClickItem, T popup) {
        for (final RightClickItemType rcItemType : rightClickItem) {
            JMenuItem sendCmd = new JMenuItem(rcItemType.getName());
            sendCmd.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e1) {
                    try {
                        final Map<String, GraphMLMetadata<String>> vertexMetadatas = graphmlLoader.getVertexMetadatas();
                        for (String v : varr) {
                            GraphViewerPanel.this.Animator(v.toString());
                            RightClickInvoker.invokeRightClickHandler(GraphViewerPanel.this.parent, v, rcItemType, vertexMetadatas, path, versionDir);
                        }
                    } catch (Exception e2) {
                        e2.printStackTrace();
                        JOptionPane.showMessageDialog(GraphViewerPanel.this, "Error while calling right click: " + e2.getMessage());
                    }
                }
            });
            popup.add(sendCmd);
            final java.util.List<SubMenuType> submenu1 = rcItemType.getSubmenu();
            for (SubMenuType subMenuType : submenu1) {
                JMenu submenu = new JMenu(subMenuType.getName());
                java.util.List<RightClickItemType> submenuItems = subMenuType.getRightClickItem();
                popup.add(submenu);
                fillRightClickMenu(varr, submenuItems, submenu);
            }
        }
    }

    private JButton createUpdateButton() {
        JButton update = new JButton("Refresh");
        update.setToolTipText("Refresh");
        update.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Set<String> vertexes = new HashSet<String>(currentGraph.getVertices());
                applyFilter(currentFilter, currentHops, vertexes);
                refreshComboFilters();
//                vv.repaint();
            }
        });
        return update;
    }

    private JButton createReloadButton() {
        JButton reload = new JButton("Reload");
        reload.setToolTipText("Reload ");
        reload.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    graphmlLoader.loadGraphml();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                Set<String> vertexes = new HashSet<String>(currentGraph.getVertices());
                applyFilter(currentFilter, currentHops, vertexes);
                refreshComboFilters();
                vv.repaint();
            }
        });
        return reload;
    }

    private JButton createRedrawAroundButton() {
        JButton redraw = new JButton("Redraw Around");
        redraw.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Set<String> pickedVertexes = vv.getPickedVertexState().getPicked();
                applyFilter(currentFilter, currentHops, pickedVertexes);
                if (pickedVertexes.iterator().next() != null) {
                    Animator(pickedVertexes.iterator().next());
                }
                refreshComboFilters();


            }
        });
        return redraw;
    }

    //TODO
    private JButton hideEdgeLabels() {
        JButton hideEdgeLabels = new JButton("Hide Edge Labels");
        hideEdgeLabels.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (isEdgeLabel()) {
                    vv.getRenderContext().setEdgeLabelTransformer(new ConstantTransformer(null));
                    vv.repaint();
                    setEdgeLabel(false);
                } else {
                    vv.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller<String>());
                    vv.repaint();
                    setEdgeLabel(true);

                }
            }
        });
        return hideEdgeLabels;
    }


    private JButton createZoomOutButton(final ScalingControl scaler) {
        JButton minus = new JButton("-");
        minus.setToolTipText("Zoom Out");
        minus.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                scaler.scale(vv, 1 / 1.1f, vv.getCenter());
            }
        });
        return minus;
    }

    private JButton createZoomInButton(final ScalingControl scaler) {
        JButton plus = new JButton("+");
        plus.setToolTipText("Zoom In");
        plus.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                scaler.scale(vv, 1.1f, vv.getCenter());
            }
        });
        return plus;
    }

    private JComboBox createHopsCombo() {
        HopsType hopsType = viewerConfig.getHops();
        JComboBox hopsCombo = new JComboBox();
        if (hopsType == null) return hopsCombo;
        String[] hopsArr = hopsType.getValue().split(",");
        for (String hops : hopsArr) {
            final int hopsInt = Integer.parseInt(hops);
            hopsCombo.addItem(hopsInt);
        }
        hopsCombo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Integer hops = (Integer) ((JComboBox) e.getSource()).getSelectedItem();
                currentHops = hops;
            }
        });
        currentHops = Integer.valueOf(hopsType.getSelected());
        hopsCombo.setSelectedItem(currentHops);
        return hopsCombo;
    }

    private void refreshComboFilters(){
        FiltersType filtersType = viewerConfig.getFilters();
        if (filtersType == null) ;
        jComboBox.removeAllItems();
        java.util.List<FilterType> filters = filtersType.getFilter();
        final Map<String, FilterType> filterName2FilterType = new HashMap<String, FilterType>();

        Map<String, DataMatcher> matcherMap = new HashMap<String, DataMatcher>();

        List<DataMatcherType> matcherList = viewerConfig.getDataMatcher();
        for (DataMatcherType dataMatcherType : matcherList) {
            String className = dataMatcherType.getClazz();
            Class clazz = null;
            try {
                clazz = Class.forName(className);
            } catch (ClassNotFoundException e) {
                logger.error("Can not find class: "+className, e);
            }
            try {
                DataMatcher dataMatcher = (DataMatcher) clazz.newInstance();
                matcherMap.put(dataMatcherType.getName(), dataMatcher);
            } catch (InstantiationException e) {
                logger.error("Can not instantiate class: " + className, e);
            } catch (IllegalAccessException e) {
                logger.error("Can not access constructor class: " + className, e);
            }
        }

//        for (FilterType filter : filters) {
//            G graph = transformCurrentGraph(filter,matcherMap, Integer.valueOf(viewerConfig.getHops().getSelected()), null);
//            int size = graph.getVertices().size();
//            jComboBox.addItem(filter.getName()+ " ("+size+")");
//            filterName2FilterType.put(filter.getName()+ " ("+size+")", filter);
//
//        }
        for (FilterType filter : filters) {
          //  G graph = transformCurrentGraph(filter,matcherMap, Integer.valueOf(viewerConfig.getHops().getSelected()), null);
          //  int size = graph.getVertices().size();
            jComboBox.addItem(filter.getName());
            filterName2FilterType.put(filter.getName(), filter);

        }
    }
    private JComboBox createFilterCombo(JComboBox filtersCombo) {
        FiltersType filtersType = viewerConfig.getFilters();
       // filtersCombo = new JComboBox();
        if (filtersType == null) return filtersCombo;
        java.util.List<FilterType> filters = filtersType.getFilter();
        final Map<String, FilterType> filterName2FilterType = new HashMap<String, FilterType>();

//        for (FilterType filter : filters) {
//            G graph = transformGraph(filter, Integer.valueOf(viewerConfig.getHops().getSelected()), null);
//            int size = graph.getVertices().size();
//            filtersCombo.addItem(filter.getName()+ " ("+size+")");
//            filterName2FilterType.put(filter.getName()+ " ("+size+")", filter);
//        }
        for (FilterType filter : filters) {
       //     G graph = transformGraph(filter, Integer.valueOf(viewerConfig.getHops().getSelected()), null);
       //     int size = graph.getVertices().size();
            filtersCombo.addItem(filter.getName());
            filterName2FilterType.put(filter.getName(), filter);
        }
        filtersCombo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String filterName = (String) ((JComboBox) e.getSource()).getSelectedItem();
                currentFilter = filterName2FilterType.get(filterName);
                vv.setCurrentFilter(currentFilter);
//                Set<String> vertexes = new HashSet<String>(currentGraph.getVertices());
                Set<String> pickedVertexes = vv.getPickedVertexState().getPicked();
                if (pickedVertexes != null) {
                    applyFilter(currentFilter, currentHops, pickedVertexes);
                } else {
                    applyFilter(currentFilter, currentHops);
                }
            }
        });
        final String filterName = (String) filtersCombo.getSelectedItem();
        currentFilter = filterName2FilterType.get(filterName);
        vv.setCurrentFilter(currentFilter);
        return filtersCombo;
    }

    private void applyFilter(final FilterType filter, Integer hops) {//, Set<String> selectedVertexes) {
        applyFilter(filter, hops, null);
    }

    private G transformGraph(final FilterType filter, final Integer hops, final Set<String> pickedVertexes){
        final Map<String, GraphMLMetadata<String>> edgeMetadatas1 = graphmlLoader.getEdgeMetadatas();

        Map<String, DataMatcher> matcherMap = new HashMap<String, DataMatcher>();

        List<DataMatcherType> matcherList = viewerConfig.getDataMatcher();
        for (DataMatcherType dataMatcherType : matcherList) {
            String className = dataMatcherType.getClazz();
            Class clazz = null;
            try {
                clazz = Class.forName(className);
            } catch (ClassNotFoundException e) {
                logger.error("Can not find class: "+className, e);
            }
            try {
                DataMatcher dataMatcher = (DataMatcher) clazz.newInstance();
                matcherMap.put(dataMatcherType.getName(), dataMatcher);
            } catch (InstantiationException e) {
                logger.error("Can not instantiate class: " + className, e);
            } catch (IllegalAccessException e) {
                logger.error("Can not access constructor class: " + className, e);
            }
        }
        EdgePredicateFilter<String, String> edgeFilter = EdgeFilterFactory.createEdgeFilter(filter, matcherMap,edgeMetadatas1);
        final Graph<String, String> graph1 = edgeFilter.transform(entireGraph);


        VertexPredicateFilter<String, String> filterV = VertexFilterFactory.createVertexFilter(filter,matcherMap, graphmlLoader.getVertexMetadatas(), graph1);

        G graph2 = (G) filterV.transform(graph1);
        HashSet<String> set = new HashSet<String>(graph2.getVertices());
        if (pickedVertexes != null) {
            if (pickedVertexes.size() != 0) {
                set.retainAll(pickedVertexes);
            }
        }
        KNeighborhoodFilter<String, String> f = new KNeighborhoodFilter<String, String>(set, hops, KNeighborhoodFilter.EdgeType.IN_OUT);
        return (G) f.transform(graph2);
    }
    private G transformCurrentGraph(final FilterType filter, Map<String, DataMatcher> matcherMap, final Integer hops, final Set<String> pickedVertexes){
        final Map<String, GraphMLMetadata<String>> edgeMetadatas1 = graphmlLoader.getEdgeMetadatas();
        EdgePredicateFilter<String, String> edgeFilter = EdgeFilterFactory.createEdgeFilter(filter, matcherMap,edgeMetadatas1);
        final Graph<String, String> graph1 = edgeFilter.transform(currentGraph);

        VertexPredicateFilter<String, String> filterV = VertexFilterFactory.createVertexFilter(filter,matcherMap ,graphmlLoader.getVertexMetadatas(), graph1);

        G graph2 = (G) filterV.transform(graph1);
        HashSet<String> set = new HashSet<String>(graph2.getVertices());
        if (pickedVertexes != null) {
            if (pickedVertexes.size() != 0) {
                set.retainAll(pickedVertexes);
            }
        }
        KNeighborhoodFilter<String, String> f = new KNeighborhoodFilter<String, String>(set, hops, KNeighborhoodFilter.EdgeType.IN_OUT);
        return (G) f.transform(graph2);
    }

    private void applyFilter(final FilterType filter, Integer hops, final Set<String> pickedVertexes) {
        hops = hops == null ? 0 : hops;
        currentGraph = transformGraph(filter, hops, pickedVertexes);

        vv.setGraphLayout(createLayout(currentGraph, layout));

    }

    public void changeLayout() {
        vv.setGraphLayout(createLayout(currentGraph, layout));
        vv.repaint();

    }

    private Layout createLayout(G graph, String layout) {
        PersistentLayout test = null;
        if (layout.equals("CircleLayout")) {
            test = new MyPersistentLayoutImpl(new CircleLayout<String, String>(graph));
        } else if (layout.equals("KKLayout")) {
            test = new MyPersistentLayoutImpl(new KKLayout<String, String>(graph));
        } else if (layout.equals("SpringLayout")) {
            test = new MyPersistentLayoutImpl(new SpringLayout<String, String>(graph));
        } else if (layout.equals("SpringLayout2")) {
            test = new MyPersistentLayoutImpl(new SpringLayout2<String, String>(graph));
        } else if (layout.equals("ISOMLayout")) {
            test = new MyPersistentLayoutImpl(new ISOMLayout<String, String>(graph));
        } else if (layout.equals("FRLayout2")) {
            test = new MyPersistentLayoutImpl(new FRLayout2<String, String>(graph));
//        }   else if (layout.equals("DAGLayout")){
//            DAGLayout<String,String> abx =    new DAGLayout<String,String>(graph);
//             abx.setRoot("R1");
//            test =  new PersistentLayoutImpl(abx);
        }
//        } else if(layout.equals("TreeLayout")){
//            TreeLayout<String,String> abx = new TreeLayout<String, String>(graph);
//        }
        else {
            test = new MyPersistentLayoutImpl(new FRLayout<String, String>(graph));
        }

        int vertexCount = currentGraph.getVertexCount();
//        final ScalingControl scaler = new CrossoverScalingControl();
        final double nodeDensity = 0.001;
        Dimension parent1 = parent.getSize();
         int  x=parent.getWidth();
         int  y= (int) (parent.getHeight() - 150);
//
//        int x = (int) (Math.sqrt(vertexCount / nodeDensity));
//        int y = (int) (Math.sqrt(vertexCount / nodeDensity));
//        if (x < parent1.width) {
//            x = parent1.width - 50;
//        } else {
//            x = x * (parent1.width - 50) / 1000;
//        }
//
//        if (y < parent1.height) {
//            y = parent1.height - 150;
//        } else {
//            y = y * (parent1.height - 150) / 1000;
//        }

        System.out.println("X: " + x + "Y: " + y + "width: " + parent1.width + "heigth: " + parent1.height);
        test.setSize(new Dimension(new Dimension(x, y)));

//        test.setSize(new Dimension(x,y));
        return test;
    }

    public VisualizationViewer getVisualizationViewer() {
        return vv;
    }


    public G getCurrentGraph() {
        return currentGraph;
    }

    public G getEntireGraph() {
        return entireGraph;
    }

    public void SetPickedState(String vertex) {
        PickedState<String> ps = new MultiPickedState<String>();
        ps.pick(vertex, true);
        vv.setPickedVertexState(ps);
    }

    public void SetEdgePickedState(String edge) {
        PickedState<String> ps = new MultiPickedState<String>();
        ps.pick(edge, true);
        vv.setPickedEdgeState(ps);
    }

    public void SetPickedStates(Set<String> vertexes) {
        PickedState<String> ps = new MultiPickedState<String>();
        Iterator it = vertexes.iterator();
        while (it.hasNext()) {
            Object element = it.next();
            ps.pick(element.toString(), true);
        }
        vv.setPickedVertexState(ps);
    }

    public void Animator(String vertex) {
        Layout<String, String> layout = vv.getGraphLayout();
        Point2D q = layout.transform(vertex);
        Point2D lvc = vv.getRenderContext().getMultiLayerTransformer().inverseTransform(vv.getCenter());
        final double dx = (lvc.getX() - q.getX()) / 10;
        final double dy = (lvc.getY() - q.getY()) / 10;
        Runnable animator = new Runnable() {

            public void run() {
                for (int i = 0; i < 10; i++) {
                    vv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.LAYOUT).translate(dx, dy);

                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ex) {
                    }
                }
            }
        };
        Thread thread = new Thread(animator);
        thread.start();
//        try {
//            thread.sleep(1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//        }

    }

    public void EdgeAnimator(Pair edge) {

        Layout<String, String> layout = vv.getGraphLayout();
        String first = (String) edge.getFirst();
        String second = (String) edge.getSecond();
        Point2D q = layout.transform(first);
        Point2D y = layout.transform(second);
        Line2D line = new Line2D.Double(q.getX(), q.getY(), y.getX(), y.getY());

        Point2D lvc = vv.getRenderContext().getMultiLayerTransformer().inverseTransform(vv.getCenter());
        final double dx = (lvc.getX() - line.getBounds().getCenterX()) / 10;
        final double dy = (lvc.getY() - line.getBounds().getCenterY()) / 10;
        Runnable edgeAnimator = new Runnable() {

            public void run() {
                for (int i = 0; i < 10; i++) {
                    vv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.LAYOUT).translate(dx, dy);

                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ex) {
                    }
                }
            }
        };
        Thread thread = new Thread(edgeAnimator);
        thread.start();
//        try {
//            thread.sleep(1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//        }

    }

    public Set<String> findShortest(String aFrom, String aTo, Graph<String, String> aGraph) {
        final Set<String> mOrderedPred = new LinkedHashSet<String>();
        final LinkedList<String> mPred = new LinkedList<String>();
        if (aFrom == null || aTo == null) {
            return mOrderedPred;
        }
        BFSDistanceLabeler<String, String> bdl = new BFSDistanceLabeler<String, String>();
        bdl.labelDistances(aGraph, aFrom);
        // grab a predecessor
        String v = aTo;
        Set<String> prd = bdl.getPredecessors(v);
        mPred.add(aTo);
        while (prd != null && prd.size() > 0) {
            v = prd.iterator().next();
            System.out.println("V: " + v);
            mPred.add(v);
            if (v.equals(aFrom)) {
                final Iterator<String> stringIterator = mPred.descendingIterator();
                while (stringIterator.hasNext()) {
                    String next = stringIterator.next();
                    mOrderedPred.add(next);
                }
                return mOrderedPred;
            }
            prd = bdl.getPredecessors(v);
        }
        // Reorder the set of nodes

        final Iterator<String> stringIterator = mPred.descendingIterator();
        while (stringIterator.hasNext()) {
            String next = stringIterator.next();
            mOrderedPred.add(next);
        }
        return mOrderedPred;
    }

    public Object getEdgeKeyValue(String key, String edgeId) {
        Map<String, String> edgeParams = new HashMap<String, String>();
        edgeParams = getEdgeParams(edgeId);

        return edgeParams.get(key);
    }

    public Set<String> FindNodeByKey(String key, Object value) {
        Map<String, GraphMLMetadata<String>> vertexMetadata = graphmlLoader.getVertexMetadatas();
        Set<String> foundVertexes = new HashSet<String>();
        outer:
        for (String element : currentGraph.getVertices()) {
            //Iterate around for each vertexKey
            logger.info("Key:" + key);
            // If the vertexKey is equal to the key get the value
            GraphMLMetadata<String> graphMLMetadata = vertexMetadata.get(key);
            if (graphMLMetadata != null) {
                String nodeDataValue = graphMLMetadata.transformer.transform(element);
                logger.info("value: " + value + " Value: " + nodeDataValue);
                if (nodeDataValue.toUpperCase().contains(value.toString().toUpperCase())) {
                    foundVertexes.add(element);
                    continue outer;
                }
            }
        }
        return foundVertexes;
    }

    public Set<String> FindEdgeByKey(String key, Object value) {

        Map<String, GraphMLMetadata<String>> vertexMetadata = graphmlLoader.getEdgeMetadatas();
        Set<String> foundEdges = new HashSet<String>();
        outer:
        for (String element : currentGraph.getEdges()) {
            //Iterate around for each vertexKey
            logger.info("Key:" + key);
            // If the vertexKey is equal to the key get the value
            GraphMLMetadata<String> graphMLMetadata = vertexMetadata.get(key);
            if (graphMLMetadata != null) {
                String edgeDataValue = graphMLMetadata.transformer.transform(element);
                logger.info("value: " + value + " Value: " + edgeDataValue);
                if (edgeDataValue.toUpperCase().contains(value.toString().toUpperCase())) {
                    foundEdges.add(element);
                    continue outer;
                }
            }
        }
        return foundEdges;
    }


    public <G> Map<String, String> getVertexParams(String v) {
        Map<String, GraphMLMetadata<String>> vertexMetadata = graphmlLoader.getVertexMetadatas();
        HashMap<String, String> params = new HashMap<String, String>();
        for (String key : vertexMetadata.keySet()) {
            String value = vertexMetadata.get(key).transformer.transform(v);
            if (value == null) continue;
            if (!params.containsKey(key)) {
                params.put(key, value);
            } else {
                value = value.concat(", ").concat(params.get(key));
                params.put(key, value);
            }
        }
        return params;
    }

    public <G> Map<String, String> getEdgeParams(String v) {
        HashMap<String, String> params = new HashMap<String, String>();
        Map<String, GraphMLMetadata<String>> edgeMetadata = graphmlLoader.getEdgeMetadatas();
        for (String key : edgeMetadata.keySet()) {
            String value = edgeMetadata.get(key).transformer.transform(v);
            if (value == null) continue;
            if (!params.containsKey(key)) {
                params.put(key, value);
            } else {
                value = value.concat(", ").concat(params.get(key));
                params.put(key, value);
            }
        }
        return params;
    }

    public boolean FindNodeByIDCurrentGraph(String name) {
        if (name != null && !name.isEmpty()) {
            if (currentGraph != null)
                return currentGraph.containsVertex(name);
            else
                return false;
        } else {
            return false;
        }

    }

    public boolean FindEdgeByIDCurrentGraph(String name) {
        if (name != null && !name.isEmpty()) {
            if (currentGraph != null)
                return currentGraph.containsEdge(name);
            else
                return false;
        } else {
            return false;
        }

    }

    public boolean FindNodeByIDEntireGraph(String name) {
        if (name != null && !name.isEmpty()) {
            if (entireGraph != null)
                return entireGraph.containsVertex(name);
            else
                return false;

        } else {
            return false;
        }
    }

    public boolean FindEdgeByIDEntireGraph(String name) {
        if (name != null && !name.isEmpty()) {
            if (entireGraph != null)
                return entireGraph.containsEdge(name);
            else
                return false;

        } else {
            return false;
        }
    }

    public Pair<String> getEdgeVertexes(String name) {
        Pair<String> params = entireGraph.getEndpoints(name);
        return params;
    }

    public File getGraphmlDir() {
        return graphmlDir;
    }

    public File getVersionDir() {
        return versionDir;
    }

    public void setLayout(String layout) {
        this.layout = layout;

    }

    class MyDefaultVertexLabelRenderer extends DefaultVertexLabelRenderer {
        protected Color unpickedVertexLabelColor = Color.BLACK;

        public MyDefaultVertexLabelRenderer(Color unpickedVertexLabelColor, Color pickedVertexLabelColor) {
            super(pickedVertexLabelColor);
            this.unpickedVertexLabelColor = unpickedVertexLabelColor;
        }

        public <V> Component getVertexLabelRendererComponent(JComponent vv, Object value, Font font, boolean isSelected, V vertex) {
            super.setForeground(unpickedVertexLabelColor);
            if (isSelected) setForeground(pickedVertexLabelColor);
            super.setBackground(vv.getBackground());
            if (font != null) {
                Font font1 = new Font(font.getName(), font.getStyle() + Font.BOLD, font.getSize() + 2);
                setFont(font1);
            } else {
                Font font1 = new Font(vv.getFont().getName(), vv.getFont().getStyle() + Font.BOLD, vv.getFont().getSize() + 2);
                setFont(font1);
            }
            setIcon(null);
            setBorder(noFocusBorder);
            setValue(value);

            return this;
        }
    }

    class MyDefaultEdgeLabelRenderer extends DefaultEdgeLabelRenderer {

        protected Color unpickedEdgeLabelColor = Color.BLACK;

        public MyDefaultEdgeLabelRenderer(Color unpickedEdgeLabelColor, Color pickedEdgeLabelColor) {
            super(pickedEdgeLabelColor);
            this.unpickedEdgeLabelColor = unpickedEdgeLabelColor;
        }

        public <V> Component getEdgeLabelRendererComponent(JComponent vv, Object value, Font font, boolean isSelected, V vertex) {
            super.setForeground(unpickedEdgeLabelColor);
            if (isSelected) setForeground(pickedEdgeLabelColor);
            super.setBackground(vv.getBackground());
            if (font != null) {
                Font font1 = new Font(font.getName(), font.getStyle() + Font.ITALIC, font.getSize());
                setFont(font1);
            } else {
                Font font1 = new Font(vv.getFont().getName(), vv.getFont().getStyle() + Font.ITALIC, vv.getFont().getSize());
                setFont(font1);
            }
            setIcon(null);
            setBorder(noFocusBorder);
            setValue(value);

            return this;
        }
    }

    public boolean isVertexLabel() {
        return vertexLabel;
    }

    public void setVertexLabel(boolean vertexLabel) {
        this.vertexLabel = vertexLabel;
    }

    public boolean isEdgeLabel() {
        return edgeLabel;
    }

    public void setEdgeLabel(boolean edgeLabel) {
        this.edgeLabel = edgeLabel;
    }

    public GraphmlLoader<G> getGraphmlLoader() {
        return graphmlLoader;
    }
}