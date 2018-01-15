package com.pufose.client.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.util.List;

import javax.swing.JPanel;

public class GUIpanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private Point[][] grid;
	private Color[][] gridColor;
	private String[][] gridNames;
	private int gridSize;
	private int offsetX;
	private int offsetY;
	private int distance;
	public static final Color DARKGREEN = Color.decode("#0e7810");

	public GUIpanel(int maxSize) {
		setSizes(maxSize);
		this.distance = (int) (8 * Math.sqrt(maxSize));
		setBackground(Color.WHITE);
		initGrid(maxSize);
	}

	private void setSizes(int maxSize) {
		setPreferredSize(new Dimension(512, 512));
		setMinimumSize(new Dimension(512, 512));
		setSize(new Dimension(512, 512));
		gridSize = maxSize;
	}

	public Point getLocationOf(int i, int j) {
		return grid[i][j].getLocation();
	}

	private void initGrid(int maxSize) {
		initDrawingVariables(maxSize);
		doInitGrid(maxSize);

	}

	private void doInitGrid(int maxSize) {
		for (int i = 0; i < maxSize; i++) {
			for (int j = 0; j < maxSize; j++) {
				initOne(i, j);
			}
		}
	}

	private void initOne(int i, int j) {
		grid[j][i] = new Point(offsetX + i * distance, offsetY + j * distance);
		gridColor[j][i] = getBackground();
		gridNames[j][i] = "";
	}

	private void initDrawingVariables(int maxSize) {
		grid = new Point[maxSize][maxSize];
		gridColor = new Color[maxSize][maxSize];
		gridNames = new String[maxSize][maxSize];
		offsetX = getWidth() / 2 - (maxSize * distance / 4);
		offsetY = getHeight() / 2 - (maxSize * distance / 4);
	}

	public void reset() {
		doReset();
		refreshView();

	}

	private void refreshView() {
		setVisible(false);
		repaint();
		setVisible(true);
	}

	private void doReset() {
		for (int i = 0; i < gridSize; i++) {
			for (int j = 0; j < gridSize; j++) {
				resetOne(i, j);
			}
		}
	}

	private void resetOne(int i, int j) {
		gridColor[i][j] = (getBackground());
		gridNames[i][j] = "";
	}

	public void enablePoint(String toPrintInPoint, int i, int j) {

		setColorToPoint(toPrintInPoint, i, j, Color.RED);

	}

	private void setColorToPoint(String toPrintInPoint, int i, int j, Color col) {
		enablePointProcedure(toPrintInPoint, i, j, col);
		repaint();
	}

	private void enablePointProcedure(String toPrintInPoint, int i, int j, Color col) {
		gridNames[i][j] = toPrintInPoint;
		gridColor[i][j] = (col);
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
		for (int i = 0; i < gridSize; i++) {
			for (int j = 0; j < gridSize; j++) {
				drawOne(g, i, j);
			}
		}

	}

	private void drawOne(Graphics g, int i, int j) {
		g.setColor(gridColor[i][j]);
		g.fillOval((int) grid[i][j].getX() - 8 / 2, (int) grid[i][j].getY() - 8 / 2, 8, 8);
		g.setFont(g.getFont().deriveFont(Font.BOLD));
		g.drawString(gridNames[i][j], (int) grid[i][j].getX() + 8 / 2, (int) grid[i][j].getY() - 8 / 2);
	}

	public void highlightPath(List<String> path) {

		if (path == null) {
			unhighlight();
		} else {
			doHighlightPath(path);
		}
		refreshView();
	}

	private void doHighlightPath(List<String> path) {
		for (String e : path) {
			highlightOne(e);
		}
	}

	private void highlightOne(String e) {
		if (!e.matches("[0-9]\\_[0-9]"))
			throw new IllegalArgumentException("Check syntax");

		int i = e.charAt(0) - 48;
		int j = e.charAt(2) - 48;
		if (gridColor[i][j].equals(Color.RED))
			gridColor[i][j] = (DARKGREEN);
	}

	private void unhighlight() {
		for (int i = 0; i < gridSize; i++) {
			for (int j = 0; j < gridSize; j++) {
				if (gridColor[i][j].equals(DARKGREEN)) {
					gridColor[i][j] = Color.RED;
				}
			}
		}
	}

	public Color getColorInPoint(int i, int j) {
		return gridColor[i][j];

	}

	public String getPrintedNameIn(int i, int j) {
		return gridNames[i][j];

	}

	public void enableNotHighlightablePoint(String toPrintInPoint, int i, int j) {
		setColorToPoint(toPrintInPoint, i, j, Color.BLACK);

	}

	public int getOffsetY() {
		return offsetY;
	}

	public int getOffsetX() {
		return offsetX;
	}

	public int getDistance() {
		return distance;
	}

	public void enablePoint(String string, int i, int j, Graphics g2) {
		enablePointProcedure(string, i, j, Color.RED);
		paintComponent(g2);
	}

	public void enableNotHighlightablePoint(String string, int i, int j, Graphics g2) {
		enablePointProcedure(string, i, j, Color.BLACK);
		paintComponent(g2);
	}

	public Point[][] getAllLocations() {
		return grid.clone();
	}

}