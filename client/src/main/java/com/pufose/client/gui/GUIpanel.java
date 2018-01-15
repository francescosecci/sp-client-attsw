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
	private int offset_x;
	private int offset_y;
	private int distance;
	public static final Color DARKGREEN=Color.decode("#0e7810");
	public GUIpanel(int maxSize) {
		setPreferredSize(new Dimension(1024/2,300));
		setMinimumSize(new Dimension(1024/2,300));
		setSize(new Dimension(1024/2,300));
		gridSize=maxSize;
		this.distance=(int)(8*Math.sqrt(maxSize));
		setBackground(Color.WHITE);
		initGrid(maxSize);
		
		
	}
	public Point getLocationOf(int i, int j) {
		return grid[i][j].getLocation();
	}
	private void initGrid(int MAX_SIZE) {
		grid=new Point[MAX_SIZE][MAX_SIZE];
		gridColor=new Color[MAX_SIZE][MAX_SIZE];
		gridNames=new String[MAX_SIZE][MAX_SIZE];		
		offset_x=getWidth()/2-(MAX_SIZE*distance/4);
		offset_y=getHeight()/2-(MAX_SIZE*distance/4);
		for(int i=0; i<MAX_SIZE;i++) {
			for(int j=0; j<MAX_SIZE;j++)
			{
				grid[j][i]=new Point(offset_x+i*distance,offset_y+j*distance);
				gridColor[j][i]=getBackground();
				gridNames[j][i]="";
			}
		}
		
	}
	
	
	public void reset() {
		for(int i=0; i<gridSize; i++) {
			for(int j=0; j<gridSize;j++) {
				gridColor[i][j]=(getBackground());
				gridNames[i][j]="";
			}
		}
		setVisible(false);
		repaint();
		setVisible(true);
		
	}
	public void enablePoint(String toPrintInPoint, int i, int j) {
		
		
		setColorToPoint(toPrintInPoint, i, j,Color.RED);
		
		
	}
	private void setColorToPoint(String toPrintInPoint, int i, int j,Color col) {
		enablePointProcedure(toPrintInPoint, i, j, col);
		repaint();
	}
	private void enablePointProcedure(String toPrintInPoint, int i, int j, Color col) {
		gridNames[i][j]= toPrintInPoint;
		gridColor[i][j]=(col);
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawRect(0, 0, getWidth()-1, getHeight()-1);
		for(int i=0; i<gridSize;i++)
		{
			for(int j=0; j<gridSize;j++)
			{
				g.setColor(gridColor[i][j]);
				g.fillOval((int)grid[i][j].getX()-8/2, (int)grid[i][j].getY()-8/2, 8, 8);
				g.setFont(g.getFont().deriveFont(Font.BOLD));
				g.drawString(gridNames[i][j], (int)grid[i][j].getX()+8/2, (int)grid[i][j].getY()-8/2);
			}
		}
	
		
		
	}
	public void highlightPath(List<String> path) {
		
		if(path==null) {
			for(int i=0; i<gridSize;i++) {
				for(int j=0; j<gridSize;j++) {
					if(gridColor[i][j].equals(DARKGREEN)) {
						gridColor[i][j]=Color.RED;
					}
				}
			}
		}
		else
		{
			for(String e:path) {
				if(!e.matches("[0-9]\\_[0-9]")) throw new IllegalArgumentException("Check syntax");
				
				int i=e.charAt(0)-48;
				int j=e.charAt(2)-48;
				if(gridColor[i][j].equals(Color.RED))
					gridColor[i][j]=(DARKGREEN);
			}
		}
		setVisible(false);
		repaint();
		setVisible(true);
	}
	public Color getColorInPoint(int i, int j) {
			return gridColor[i][j];
		
	}
	public String getPrintedNameIn(int i, int j) {
		return gridNames[i][j];
		
	}
	public void enableNotHighlightablePoint(String toPrintInPoint, int i, int j) {
		setColorToPoint(toPrintInPoint,i,j,Color.BLACK);
		
	}
	public int getOffsetY() {
		return offset_y;
	}
	public int getOffsetX() {
		return offset_x;
	}
	public int getDistance() {
		return distance;
	}
	public void enablePoint(String string, int i, int j, Graphics g2) {
		enablePointProcedure(string,i,j,Color.RED);
		paintComponent(g2);
	}
	public void enableNotHighlightablePoint(String string, int i, int j, Graphics g2) {
		enablePointProcedure(string,i,j,Color.BLACK);
		paintComponent(g2);
	}
	public Point[][] getAllLocations() {
		return grid.clone();
	}
	

	
		

	
}