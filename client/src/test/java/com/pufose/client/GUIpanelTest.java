package com.pufose.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.pufose.client.gui.GUIpanel;
import com.pufose.client.gui.GraphBuilder;



public class GUIpanelTest {


	private GUIpanel pan;
	private int GRIDSIZE;
	@Before
	public void setup() {
		GRIDSIZE=15;
		pan=new GUIpanel(GRIDSIZE);

	}

	@Test
	public void testInitialization() {
		Color c=pan.getBackground();
		Dimension pref=pan.getPreferredSize();
		Dimension min=pan.getMinimumSize();
		Dimension size=pan.getSize();
		Dimension expected=new Dimension(512,512);

		assertEquals(pan.getWidth()/2-(GRIDSIZE*pan.getDistance()/4),pan.getOffsetX());
		assertEquals(pan.getHeight()/2-(GRIDSIZE*pan.getDistance()/4),pan.getOffsetY());
		assertEquals(Color.WHITE,c);
		assertEquals(expected,pref);
		assertEquals(expected,min);
		assertEquals(expected,size);
		assertEquals((int)(8*Math.sqrt(GRIDSIZE)),pan.getDistance());
		for(int i=0; i<GRIDSIZE;i++) {
			for(int j=0; j<GRIDSIZE;j++) {
				Point Expected=new Point(pan.getOffsetX()+i*pan.getDistance(),pan.getOffsetY()+j*pan.getDistance());
				assertEquals(Expected,pan.getLocationOf(j, i));
			}
		}
	}
	
	
	
	@Test
	public void testAllHidden() {
		allHiddenProcedure(0,0);
	}
	private void allHiddenProcedure(int start_i, int start_j) {
		for(int i=start_i; i<GRIDSIZE;i++) {
			for(int j=start_j; j<GRIDSIZE;j++) {
				assertHidden(i,j);
			}
		}
	}
	private void assertColorInPoint(Color expected, int i, int j) {
		assertEquals(expected,pan.getColorInPoint(i, j));
	}
	@Test
	public void testAccessingColorOfExistingPoint() {
		assertNotNull(pan.getColorInPoint(0, 0));
	}
	@Test(expected=ArrayIndexOutOfBoundsException.class)
	public void testAccessingColorOfUnexistingPoint() {
		pan.getColorInPoint(GRIDSIZE,0);
		
	}
	
	private void assertStringInPoint(String expected, int i, int j) {
		assertEquals(expected,pan.getPrintedNameIn(0,0));
	}
	@Test
	public void testEnableOnePoint() {
		assertEnablingOnePoint(0,0,"test",Color.RED);
	}
	private void assertEnablingOnePoint(int i, int j, String expectedname, Color expectedcol) {
		pan.enablePoint(expectedname, 0, 0);
		assertStringInPoint(expectedname,0,0);
		assertColorInPoint(expectedcol,0,0);
	}
	@Test(expected=ArrayIndexOutOfBoundsException.class)
	public void assertEnableUnexistingPoint() {
		pan.enablePoint("", GRIDSIZE, GRIDSIZE);
	}
	@Test
	public void testHighlightEmptyPath() {
		pan.highlightPath(new LinkedList<String>());
		for(int i=0; i<GRIDSIZE;i++) {
			for(int j=0; j<GRIDSIZE;j++) {
				assertNotEquals(GUIpanel.DARKGREEN,pan.getColorInPoint(i, j));
			}
		}
	}
	@Test
	public void testHighlightSinglePath() {
		pan.enablePoint("", 0, 1);
		pan.highlightPath(Arrays.asList("0_1"));
		assertColorInPoint(GUIpanel.DARKGREEN,0,1);
		for(int i=0; i<GRIDSIZE; i++) {
			for(int j=0; j<GRIDSIZE; j++) {
				if(i==0 && j==1) continue;
				assertNotEquals(GUIpanel.DARKGREEN,pan.getColorInPoint(i, j));
			}
		}
	}
	@Test
	public void testHighlightPath() {
		pan.enablePoint("", 0, 0);
		pan.enablePoint("", 0, 1);
		List<String> path=Arrays.asList("0_0","0_1");
		pan.highlightPath(path);
		for(int i=0;i<2;i++) {
			assertColorInPoint(GUIpanel.DARKGREEN,0,i);
		}
	}
	@Test(expected=IllegalArgumentException.class)
	public void testWrongPath() {
		List<String> path=Arrays.asList("10","1-1","0_1","abc");
		pan.highlightPath(path);
	}
	@Test
	public void testResetHighlight() {
		pan.enablePoint("", 0, 0);
		pan.enablePoint("", 0, 1);
		pan.enablePoint("", 0, 2);
		
		List<String> path=Arrays.asList("0_0","0_1","0_2");
		pan.highlightPath(path);
		for(int i=0; i<3;i++) {
			assertColorInPoint(GUIpanel.DARKGREEN,0,i);
		}
		pan.highlightPath(null);
		for(int i=0; i<3;i++) {
			assertColorInPoint(Color.RED,0,i);
		}
	}
	@Test
	public void testEnablePointNotHighlightable() {
		pan.enableNotHighlightablePoint("", 0, 0);
		assertColorInPoint(Color.BLACK,0,0);
		
	}
	@Test
	public void testReset() {
		pan.enablePoint("test", 0, 0);
		pan.reset();
		allHiddenProcedure(0,0);
	}
	private int[][] createMatrix(int row, int col) {
		return new int[row][col];
	}
	@Test
	public void testGraphBuilderWhenGraphIsEmpty() {
		int[][] matrix=createMatrix(0,0);
		GridFromServer fixture=new GridFromServer(matrix,0);
		GraphBuilder.instance.makeGraph(fixture, pan);
		allHiddenProcedure(0,0);
	}
	@Test 
	public void testGraphBuilderWhenGraphHasOnlyOneNodeAt1() {
		createGridOfDimensionOneAndAssertCoerentDrawing(1);
		assertColorInPoint(Color.RED,0,0);
	}
	private void createGridOfDimensionOneAndAssertCoerentDrawing(int value_in_matrix ) {
		int[][]matrix=createMatrix(1,1);
		matrix[0][0]=value_in_matrix;
		GridFromServer fixture=new GridFromServer(matrix,0);
		GraphBuilder.instance.makeGraph(fixture, pan);
		
	}
	@Test
	public void testGraphBuilderWhenGraphHasOnlyOneNodeAt0() {
		createGridOfDimensionOneAndAssertCoerentDrawing(0);
		assertColorInPoint(Color.BLACK,0,0);
		assertOtherHiddens(0,0);
	}
	@Test
	public void testGraphBuilderWhenNotSquareMatrixAndRowsGTCol() {
		GraphBuilder.instance.makeGraph(new GridFromServer(createMatrix(3,1),0), pan);
		for(int i=0; i<2;i++) {
			for(int j=0; j<2;j++) {
				assertColorInPoint(Color.BLACK,i,j);
			}
		}
		assertOtherHiddens(2,2);
	}
	private void assertOtherHiddens(int i, int j) {
		allHiddenProcedure(0,j+1);
		allHiddenProcedure(i+1,0);
		
	}
	private void assertHidden(int i, int j) {
		assertEquals(pan.getBackground(),pan.getColorInPoint(i,j));
		assertEquals("",pan.getPrintedNameIn(i, j));
	}
	@Test
	public void testClonedLocations() {
		Point[][] loc=pan.getAllLocations();
		Point[][] loc2=pan.getAllLocations();
		assertTrue(Arrays.deepEquals(loc, loc2));
		assertTrue(loc!=loc2);
	}
	@Test
	public void testGraphBuilderWhenNotSquareMatrixAndColGTRows() {
		GraphBuilder.instance.makeGraph(new GridFromServer(createMatrix(7,10),0), pan);
		for(int i=0; i<7;i++) {
			for(int j=0; j<7;j++) {
				assertColorInPoint(Color.BLACK,i,j);
			}
		}
		assertOtherHiddens(7,7);
	}
	@Test
	public void testGraphBuilderInACommonScenario() {
		
		int[][] matrix= new int[][] {
			{1,0,1,1},
			{1,1,0,0},
			{1,1,1,1},
			{1,1,0,1}
		};
		GridFromServer fixture=new GridFromServer(matrix,0);
		GraphBuilder.instance.makeGraph(fixture, pan);
		for(int i=0; i<4;i++) {
			for(int j=0; j<4;j++) {
				if(matrix[i][j]==1) {
					assertColorInPoint(Color.RED,i,j);
				}
				else
				{
					assertColorInPoint(Color.BLACK,i,j);
				}
			}
		}
		assertOtherHiddens(4,4);
	}
	
	
	

}

