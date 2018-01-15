package com.pufose.client.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
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

public class GUI extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GUIpanel panel;
	private JPanel NORTH, SOUTH;
	private JTextField server, port, urlToAll, txtsource, txtsink;
	private JButton perform, createConnector, reset;
	private JComboBox<String> actions, comboCity;
	private JLabel lblout;
	private IClient cl;
	private boolean GRID_ENABLED, PATH_ENABLED, CONN_CREATED;
	public static final String OPERATION_OK="Operation successfully done";
	public static final String SERVER_ERROR="Error, server cannot perform operation";
	public static final String NO_CONNECTOR="Error, you must create a connector before";
	public GUI() {
		super("Shortest Path Client - ATTSW Project 17-18");
		initializeGui();
		initializeLocalFields();

	}

	private void initializeLocalFields() {
		cl = new Client();
		GRID_ENABLED = PATH_ENABLED = CONN_CREATED = false;
	}

	private void initializeGui() {
		createWidgets();
		addWidgetsToFrame();
		graphicalAdjustements();
		createEvents();
	}

	private void alert(String message) {
		lblout.setText(message);
	}

	public void resetPane() {
		panel.reset();
		PATH_ENABLED = false;
		comboCity.setEnabled(true);
	}

	private void createConnection() {
		String prefix = "http://" + server.getText() + ":" + port.getText();
		String urltoall = prefix + urlToAll.getText();

		RestServiceClient rsc = (new RestServiceClient(urltoall));
		cl.setRestServiceClient(rsc);
		CONN_CREATED = true;
	}

	private void requestPath() {
		String from = txtsource.getText();
		if (from.length() == 0) {
			alert("Insert source node");
			return;
		}
		String to = txtsink.getText();
		if (to.length() == 0) {
			alert("Insert sink node");
			return;
		}
		try {
			tryToHighlightPath(from, to);
		} catch (IOException e) {
			alert(SERVER_ERROR);
		} catch(NullPointerException e) {
			alert(NO_CONNECTOR);
		}

	}

	private void tryToHighlightPath(String from, String to) throws IOException {
		panel.highlightPath(null);
		List<String> minPath = cl.getShortestPath(from, to, (String) comboCity.getSelectedItem());
		if (minPath.isEmpty()) {
			alert("No paths found from source node to sink");
		} else {
			alert(OPERATION_OK);
			panel.highlightPath(minPath);
		}

	}

	private void requestGrid() {

		panel.reset();
		GridFromServer grid;
		try {
			grid = cl.retrieveGrid((String) comboCity.getSelectedItem());
			GraphBuilder.makeGraph(grid, panel);
			comboCity.setEnabled(false);
			PATH_ENABLED = true;
			alert(OPERATION_OK);
		} catch (IOException e) {
			alert(SERVER_ERROR);
		} catch(NullPointerException e) {
			alert(NO_CONNECTOR);
		}
		
	}

	private void requestAll() {
		
		try {
			tryToGetAllTables();
			alert(OPERATION_OK);
			GRID_ENABLED = true;
		} catch (IOException e) {
			alert(SERVER_ERROR);
		} catch(NullPointerException e) {
			alert(NO_CONNECTOR);
		}
		
	}

	private void tryToGetAllTables() throws IOException {
		List<String> cities = cl.getAllTables();
		comboCity.removeAllItems();
		for (String e : cities) {
			comboCity.addItem(e);
		}
	}

	private void createEvents() {

		reset.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				resetPane();

			}

		});
		createConnector.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				createConnection();

			}

		});
		perform.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				switch (actions.getSelectedIndex()) {
				case 0: {
					requestAll();
					break;
				}
				case 1: {

					if (!GRID_ENABLED && CONN_CREATED) {
						alert("Error, you must retrieve all grids first"); 
						break;
					}
					requestGrid();
					break;
				}
				case 2: {
					if (!PATH_ENABLED && CONN_CREATED) {
						alert("Error, you must retrieve a grid first");
						break;
					}
					requestPath();
					txtsource.setText("");
					txtsink.setText("");

					break;
				}
				}
			}

		});
	}

	private void graphicalAdjustements() {
		setLocation(0,0);
		pack();
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
		initFields();
		lblout.setFont(new Font("", Font.BOLD, 16)); 
		lblout.setForeground(Color.BLUE);

	}

	private void initFields() {
		server.setText("localhost"); 
		port.setText("8080");
		urlToAll.setText("/api");
		
	}

	private void addWidgetsToFrame() {
		addToNorth();
		addToSouth();
		addToContentPane();
	}

	private void addToContentPane() {
		Container c = getContentPane();
		c.add(NORTH, BorderLayout.NORTH);
		c.add(panel, BorderLayout.CENTER);
		c.add(SOUTH, BorderLayout.SOUTH);
	}

	private void addToSouth() {
		SOUTH.add(actions);

		SOUTH.add(comboCity);
		SOUTH.add(new JLabel("Source node")); 
		SOUTH.add(txtsource);
		SOUTH.add(new JLabel("Sink node")); 
		SOUTH.add(txtsink);
		SOUTH.add(perform);
		SOUTH.add(reset);
		SOUTH.add(lblout);
	}

	private void addToNorth() {
		NORTH.add(new JLabel("Hostname")); 
		NORTH.add(server);
		NORTH.add(new JLabel("Port")); 
		NORTH.add(port);
		NORTH.add(new JLabel("Url to RESTful api")); 
		NORTH.add(urlToAll);
		NORTH.add(createConnector);
	}

	private void createWidgets() {

		createPanels();
		createTextFields();
		createComboBoxes();
		createButtons();
	}

	private void createComboBoxes() {
		actions = new JComboBox<String>();
		actions.setName("actionsCombo");
		for (String e : (Arrays.asList("Show all grids", "Request a grid", "Request shortest path"))) { 
			actions.addItem(e);
		}
		comboCity = new JComboBox<String>();
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
		NORTH = new JPanel();
		NORTH.setName("northPanel"); 
		SOUTH = new JPanel();
		SOUTH.setName("southPanel"); 
		panel = new GUIpanel(20);
		panel.setName("guiPanel"); 
	}

	public static GUI createGui() {
		return new GUI();
	}

	public void mockClient(IClient mock) {
		this.cl = mock;
		CONN_CREATED = (mock != null);

	}

}