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

package net.itransformers.topologyviewer.rightclick.impl;

import net.itransformers.topologyviewer.rightclick.impl.telnet.JTelnetTty;
import com.wittams.gritty.RequestOrigin;
import com.wittams.gritty.ResizePanelDelegate;
import com.wittams.gritty.swing.BufferPanel;
import com.wittams.gritty.swing.GrittyTerminal;
import com.wittams.gritty.swing.TermPanel;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Map;


public class Terminal {
	

	public static final Logger logger = Logger.getLogger(Terminal.class);
	JFrame bufferFrame;

	private TermPanel termPanel;

	private GrittyTerminal terminal;

	private AbstractAction openAction = new AbstractAction("Open SHELL Session..."){
		public void actionPerformed(final ActionEvent e) {
			openSession(null);
		}
	};
	
	private AbstractAction showBuffersAction = new AbstractAction("Show buffers") {
		public void actionPerformed(final ActionEvent e) {
			if(bufferFrame == null)
				showBuffers();
		}
	};
	
	private AbstractAction resetDamage = new AbstractAction("Reset damage") {
		public void actionPerformed(final ActionEvent e) {
			if(termPanel != null)
				termPanel.getBackBuffer().resetDamage();
		}
	};
	
	private AbstractAction drawDamage = new AbstractAction("Draw from damage") {
		public void actionPerformed(final ActionEvent e) {
			if(termPanel != null)
				termPanel.redrawFromDamage();
		}
	};
	
	private final JMenuBar getJMenuBar() {
		final JMenuBar mb = new JMenuBar();
		final JMenu m = new JMenu("File");
		
		m.add(openAction);
		mb.add(m);
		final JMenu dm = new JMenu("Debug");
		
		dm.add(showBuffersAction);
		dm.add(resetDamage);
		dm.add(drawDamage);
		mb.add(dm);

		return mb;
	}

	public void openSession(Map<String, String> connParams) {
		if(!terminal.isSessionRunning()){
//			terminal.setTty(new JSchTty(connParams.get("ManagementIPAddress"), connParams.get("username"), connParams.get("password")));
            final int timeout = 3000; // todo config default
            try {
                Integer.parseInt(connParams.get("timeout"));
            } catch (RuntimeException rte){
                logger.info("can not parse timeout:"+connParams.get("timeout"));
            }
            terminal.setTty(new JTelnetTty(connParams.get("ManagementIPAddress"),
                    connParams.get("username"),
                    connParams.get("password"),
                    timeout));
			terminal.start();
		}
	}

	public Terminal() {
		terminal = new GrittyTerminal();
		termPanel = terminal.getTermPanel();
		final JFrame frame = new JFrame("Gritty");

		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(final WindowEvent e) {
				frame.dispose();
			}
		});

		final JMenuBar mb = getJMenuBar();
		//frame.setJMenuBar(mb);
		sizeFrameForTerm(frame);
		frame.getContentPane().add("Center", terminal);

		frame.pack();
		termPanel.setVisible(true);
		frame.setVisible(true);

		frame.setResizable(true);

		termPanel.setResizePanelDelegate(new ResizePanelDelegate(){
			public void resizedPanel(final Dimension pixelDimension, final RequestOrigin origin) {
				if(origin == RequestOrigin.Remote)
					sizeFrameForTerm(frame);
			}
		});

	}

	private void sizeFrameForTerm(final JFrame frame) {
		Dimension d = terminal.getPreferredSize();
		
		d.width += frame.getWidth() - frame.getContentPane().getWidth();
		d.height += frame.getHeight() - frame.getContentPane().getHeight(); 
		frame.setSize(d);
	}

	public static void main(final String[] arg) {
		BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(Level.INFO);
        Terminal term = new Terminal();
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("ManagementIPAddress","10.10.10.10");
        params.put("username","user");
        params.put("password","pass123!");
        term.openSession(params);

	}
	
	private void showBuffers() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				bufferFrame = new JFrame("buffers");
				final JPanel panel = new BufferPanel(terminal);
				
				bufferFrame.getContentPane().add(panel);
				bufferFrame.pack();
				bufferFrame.setVisible(true);
				bufferFrame.setSize(800, 600);
				
				bufferFrame.addWindowListener(new WindowAdapter(){
					@Override
					public void windowClosing(final WindowEvent e) {
						bufferFrame = null;
					}
				});
			}
		});
	}
}
