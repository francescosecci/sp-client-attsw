package com.pufose.client.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.pufose.client.Client;
import com.pufose.client.GridFromServer;
import com.pufose.client.IClient;
import com.pufose.client.RestServiceClient;

public class GUI {

	/**
	 * 
	 */
	private JFrame window;
	private GUIpanel panel;
	private JPanel north;
	private JPanel south;
	private JTextField server;
	private JTextField port;
	private JTextField urlToAll;
	private JTextField txtsource;
	private JTextField txtsink;
	private JButton perform;
	private JButton createConnector;
	private JButton reset;
	private JComboBox<String> actions;
	private JComboBox<String> comboCity;
	private JLabel lblout;
	private IClient cl;
	private boolean gridEnabled;
	private boolean pathEnabled;
	private boolean connCreated;
	public static final String OPERATION_OK = "Operation successfully done";
	public static final String SERVER_ERROR = "Error, server cannot perform operation";
	public static final String NO_CONNECTOR = "Error, you must create a connector before";

	private GUI(boolean hidden) {
		window = new JFrame("Shortest Path Client - ATTSW Project 17-18");
		initializeGui(hidden);
		initializeLocalFields();

	}

	private void initializeLocalFields() {
		cl = new Client();
		gridEnabled = pathEnabled = connCreated = false;
	}

	private void initializeGui(boolean hidden) {
		createWidgets();
		addWidgetsToFrame();
		graphicalAdjustements(hidden);
		createEvents();
	}

	private void alert(String message) {
		lblout.setText(message);
		window.pack();
	}

	public void mockPane(GUIpanel pan) {
		this.panel = pan;
	}

	public void resetPane() {
		panel.reset();
		pathEnabled = false;
		comboCity.setEnabled(true);
	}

	public void createConnection(String host, String port) {
		String prefix = "http://" + host + ":" + port;
		String urltoall = prefix + urlToAll.getText();
		RestServiceClient rsc = (new RestServiceClient(urltoall));
		cl.setRestServiceClient(rsc);
		alert(OPERATION_OK);
		connCreated = true;
	}

	private List<String> requestPath(String from, String to, String where) {
		List<String> res = new ArrayList<>();
		if (from.length() == 0) {
			alert("Insert source node");

		}

		else if (to.length() == 0) {
			alert("Insert sink node");

		} else {
			try {
				res = tryToHighlightPath(from, to,where);
			} catch (IOException e) {
				alert(SERVER_ERROR);
			} catch (NullPointerException e) {
				alert(NO_CONNECTOR);
			}
		}
		return res;
	}

	private List<String> tryToHighlightPath(String from, String to, String where) throws IOException {
		panel.highlightPath(null);
		List<String> minPath = cl.getShortestPath(from, to, where);
		
		if (minPath.isEmpty()) {
			alert("No paths found from source node to sink");
		} else {
			alert(OPERATION_OK);
			panel.highlightPath(minPath);
		}
		return minPath;

	}

	private GridFromServer requestGrid(String id) {

		panel.reset();
		GridFromServer grid;
		try {
			grid = cl.retrieveGrid("" + id);
			GraphBuilder.instance.makeGraph(grid, panel);
			comboCity.setEnabled(false);
			pathEnabled = true;
			alert(OPERATION_OK);
			return grid;
		} catch (IOException e) {
			alert(SERVER_ERROR);
			return null;
		} catch (NullPointerException e) {
			alert(NO_CONNECTOR);
			return null;
		}

	}

	public List<String> requestAll() {

		try {
			List<String> all = tryToGetAllTables();
			alert(OPERATION_OK);
			gridEnabled = true;
			return all;
		} catch (IOException e) {
			alert(SERVER_ERROR);
			return new ArrayList<>();
		} catch (NullPointerException e) {
			alert(NO_CONNECTOR);
			return new ArrayList<>();
		}

	}

	private List<String> tryToGetAllTables() throws IOException {
		List<String> cities = cl.getAllTables();
		comboCity.removeAllItems();
		for (String e : cities) {
			comboCity.addItem(e);
		}
		return cities;
	}

	private void createEvents() {

		reset.addActionListener((ActionEvent arg0) -> resetPane());
		createConnector.addActionListener((ActionEvent arg0) -> createConnection(server.getText(), port.getText()));
		perform.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (!connCreated) {
					alert(NO_CONNECTOR);
					return;
				}
				if (actions.getSelectedIndex() == 0) {
					requestAll();
				} else if (actions.getSelectedIndex() == 1) {
					caseRequestGrid((String) comboCity.getSelectedItem());
				} else if (actions.getSelectedIndex() == 2) {
					caseRequestPath(txtsource.getText(), txtsink.getText(), (String) comboCity.getSelectedItem());
				}

			}

		});
	}

	public List<String> caseRequestPath(String from, String to, String where) {
		if (!pathEnabled) {
			alert("Error, you must retrieve a grid first");
			return new ArrayList<>();
		} else {
			txtsource.setText("");
			txtsink.setText("");
			return requestPath(from, to, where);

		}
	}

	public GridFromServer caseRequestGrid(String id) {
		if (!gridEnabled) {
			alert("Error, you must retrieve all grids first");
			return null;
		} else {
			return requestGrid(id);
		}
	}

	private void graphicalAdjustements(boolean hidden) {
		window.setLocation(0, 0);
		window.pack();
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setVisible(!hidden);
		initFields();
		lblout.setFont(new Font("", Font.BOLD, 16));
		lblout.setForeground(Color.BLUE);

	}

	private void initFields() {
		server.setText("localhost");
		port.setText("8080");
		urlToAll.setText("/api/");

	}

	private void addWidgetsToFrame() {
		addToNorth();
		addToSouth();
		addToContentPane();
	}

	private void addToContentPane() {
		Container c = window.getContentPane();
		c.add(north, BorderLayout.NORTH);
		c.add(panel, BorderLayout.CENTER);
		c.add(south, BorderLayout.SOUTH);
	}

	private void addToSouth() {
		south.add(actions);

		south.add(comboCity);
		south.add(new JLabel("Source node"));
		south.add(txtsource);
		south.add(new JLabel("Sink node"));
		south.add(txtsink);
		south.add(perform);
		south.add(reset);
		south.add(lblout);
	}

	private void addToNorth() {
		north.add(new JLabel("Hostname"));
		north.add(server);
		north.add(new JLabel("Port"));
		north.add(port);
		north.add(new JLabel("Url to RESTful api"));
		north.add(urlToAll);
		north.add(createConnector);
	}

	private void createWidgets() {

		createPanels();
		createTextFields();
		createComboBoxes();
		createButtons();
	}

	private void createComboBoxes() {
		actions = new JComboBox<>();
		actions.setName("actionsCombo");
		for (String e : (Arrays.asList("Show all grids", "Request a grid", "Request shortest path"))) {
			actions.addItem(e);
		}
		comboCity = new JComboBox<>();
		comboCity.setName("gridCombo");
	}

	private void createButtons() {
		perform = new JButton("Perform");
		perform.setName("btnPerform");
		createConnector = new JButton("Create connector");
		createConnector.setName("btnCreateConn");
		reset = new JButton("Reset");
		reset.setName("btnReset");
	}

	private void createTextFields() {
		server = new JTextField(10);
		server.setName("serverField");
		port = new JTextField(6);
		port.setName("portField");
		urlToAll = new JTextField(10);
		urlToAll.setName("apiField");
		lblout = new JLabel();
		lblout.setName("lblOutput");
		txtsource = new JTextField(4);
		txtsink = new JTextField(4);
		txtsource.setName("sourceField");
		txtsink.setName("sinkField");
	}

	private void createPanels() {
		north = new JPanel();
		north.setName("northPanel");
		south = new JPanel();
		south.setName("southPanel");
		panel = new GUIpanel(20);
		panel.setName("guiPanel");
	}

	public static GUI createGui(boolean hidden) {
		return new GUI(hidden);
	}

	public void mockClient(IClient mock) {
		this.cl = mock;
		connCreated = (mock != null);

	}

	public IClient getClient() {
		return this.cl;
	}

	public JFrame getFrame() {
		return this.window;
	}

	public void setGridEnabled(boolean b) {
		this.gridEnabled=b;
	}

	public void setPathEnabled(boolean b) {
		this.pathEnabled=b;
		
	}

}