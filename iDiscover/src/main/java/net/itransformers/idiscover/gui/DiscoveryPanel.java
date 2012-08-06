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

package net.itransformers.idiscover.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * Date: 11-11-9
 * Time: 1:46
 * To change this template use File | Settings | File Templates.
 */
public class DiscoveryPanel extends JPanel{
    private Properties preferences;
    private String prefsFileName;

    public DiscoveryPanel(Properties preferences, String prefsFileName) {
        this.preferences = preferences;
        this.prefsFileName = prefsFileName;
        init();
    }

    private void savePrefs(){
        try {
            preferences.store(new FileOutputStream(prefsFileName),"");
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Can not store prefs", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void init() {
        JPanel confPanel = new JPanel();
        confPanel.setLayout(new GridLayout(6,2, 0, 0));
        confPanel.add(new JLabel("Address:"));
        final JTextField addrField = new JTextField(preferences.getProperty("Address"),10);

        final FocusListener focusListener = new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {}
            @Override
            public void focusLost(FocusEvent e) {
                JTextField field = (JTextField) e.getSource();
                savePrefs();
            }
        };
        addrField.addFocusListener(focusListener);
        confPanel.add(addrField);
        confPanel.add(new JLabel("Port:"));
        final JTextField portField = new JTextField(preferences.getProperty("Port"),10);
        portField.addFocusListener(focusListener);
        confPanel.add(portField);
        confPanel.add(new JLabel("Mode:"));
        final JTextField modeField = new JTextField(preferences.getProperty("Mode"),10);
        modeField.addFocusListener(focusListener);
        confPanel.add(modeField);
        confPanel.add(new JLabel("Community:"));
        confPanel.add(new JTextField(preferences.getProperty("Community"),10));
        confPanel.add(new JLabel("Community2:"));
        confPanel.add(new JTextField(preferences.getProperty("Community2"),10));
        confPanel.add(new JLabel("Params xml:"));
        confPanel.add(new JTextField(preferences.getProperty("ParamsXml"),10));

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(confPanel, BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(new JButton("Start Discovery"));

        this.setLayout(new BorderLayout());
        this.add(mainPanel, BorderLayout.NORTH);
        this.add(buttonPanel, BorderLayout.CENTER);
        preferences.setProperty("Address",addrField.getText());
    }

    private static JFrame createFrame(){
        // create a frome to hold the graph
        final JFrame frame = new JFrame("Network Topology Viewer");

//        frame.setBackground(Color.black);
        frame.setExtendedState(Frame.MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.setVisible(true);
        frame.setMinimumSize(new Dimension(200,200));
        return frame;
    }

    public static void main(String[] args) throws IOException {
        String prefsFileName = "prefs.properties";
        File prefsFile = new File(prefsFileName);
        if (!prefsFile.exists()){
            prefsFile.createNewFile();
        }
        Properties prefs = new Properties();
        prefs.load(new FileInputStream(prefsFileName));
        JFrame frame = createFrame();
        DiscoveryPanel discoveryPanel = new DiscoveryPanel(prefs,prefsFileName);
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(discoveryPanel, BorderLayout.NORTH);
        frame.setVisible(true);
        frame.pack();
    }
}
