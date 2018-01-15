package com.pufose.client.guitest;

import static org.assertj.swing.fixture.Containers.showInFrame;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.times;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.image.ImageObserver;
import java.text.AttributedCharacterIterator;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.assertj.swing.fixture.FrameFixture;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;


import com.pufose.client.GridFromServer;
import com.pufose.client.gui.GUIpanel;
import com.pufose.client.gui.GraphBuilder;



public class GUIpanelTest {

	private FrameFixture framecontainer;
	private GUIpanel pan;
	private int GRIDSIZE;
	@Before
	public void setup() {
		GRIDSIZE=15;
		pan=new GUIpanel(GRIDSIZE);
		framecontainer=showInFrame(pan);
		framecontainer.show();
	}
	@After
	public void teardown() {
		framecontainer.cleanUp();
	}
	@Test
	public void testInitialization() {
		Color c=pan.getBackground();
		Dimension pref=pan.getPreferredSize();
		Dimension min=pan.getMinimumSize();
		Dimension size=pan.getSize();
		Dimension expected=new Dimension(1024,600);

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
	public void testPaintComponent() {
		Graphics g=Mockito.spy(new GraphicsWrapper(pan.getGraphics()));
		pan.paintComponent(g);
		Mockito.verify(g,times(GRIDSIZE*GRIDSIZE)).setColor(Mockito.any(Color.class));
		Mockito.verify(g,times(GRIDSIZE*GRIDSIZE)).setFont(Mockito.any(Font.class));
		Mockito.verify(g,times(GRIDSIZE*GRIDSIZE)).drawString(Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt());
		Mockito.verify(g,times(GRIDSIZE*GRIDSIZE)).fillOval(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt());
		
		Graphics g2=Mockito.spy(new GraphicsWrapper(pan.getGraphics()));
		pan.enablePoint("test00",0,0,g2);
		pan.enablePoint("test01", 0, 1,g2);
		pan.enablePoint("test10", 1, 0,g2);
		pan.enablePoint("test11", 1, 1,g2);
		int expected_x=pan.getOffsetX();
		int expected_y=pan.getOffsetY();
		int expected_xx=pan.getOffsetX()+pan.getDistance();
		int expected_yy=pan.getOffsetY()+pan.getDistance();
		Mockito.verify(g2,times(4)).drawString("test00", expected_x+4, expected_y-4);
		Mockito.verify(g2,times(1)).drawString("test11", expected_xx+4, expected_yy-4);
		Mockito.verify(g2,times(2)).drawString("test10",expected_x+4,expected_yy-4);
		Mockito.verify(g2,times(3)).drawString("test01", expected_xx+4, expected_y-4);
		Mockito.verify(g2,times(10)).setColor(Color.RED);
		int expected_times_of_background= 
					(GRIDSIZE*GRIDSIZE-4)+(GRIDSIZE*GRIDSIZE-3)+(GRIDSIZE*GRIDSIZE-2)+(GRIDSIZE*GRIDSIZE-1);
		Mockito.verify(g2,times(expected_times_of_background)).setColor(pan.getBackground());
		Mockito.verify(g2,times(4)).fillOval(expected_x-4, expected_y-4, 8, 8);
		Mockito.verify(g2,times(4)).fillOval(expected_xx-4, expected_yy-4, 8, 8);
		Mockito.verify(g2,times(4)).fillOval(expected_x-4, expected_yy-4, 8, 8);
		Mockito.verify(g2,times(4)).fillOval(expected_xx-4, expected_y-4, 8, 8);
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
		GraphBuilder.makeGraph(fixture, pan);
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
		GraphBuilder.makeGraph(fixture, pan);
		
	}
	@Test
	public void testGraphBuilderWhenGraphHasOnlyOneNodeAt0() {
		createGridOfDimensionOneAndAssertCoerentDrawing(0);
		assertColorInPoint(Color.BLACK,0,0);
		assertOtherHiddens(0,0);
	}
	@Test
	public void testGraphBuilderWhenNotSquareMatrixAndRowsGTCol() {
		GraphBuilder.makeGraph(new GridFromServer(createMatrix(3,1),0), pan);
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
	public void testGraphBuilderWhenNotSquareMatrixAndColGTRows() {
		GraphBuilder.makeGraph(new GridFromServer(createMatrix(7,10),0), pan);
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
		GraphBuilder.makeGraph(fixture, pan);
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

class GraphicsWrapper extends Graphics {
	private Graphics wrapped;
	public GraphicsWrapper(Graphics towrap) {
		wrapped=towrap;
	}
	public void clearRect(int arg0, int arg1, int arg2, int arg3) {
		wrapped.clearRect(arg0, arg1, arg2, arg3);
	}
	public void clipRect(int arg0, int arg1, int arg2, int arg3) {
		wrapped.clipRect(arg0, arg1, arg2, arg3);
	}
	public void copyArea(int arg0, int arg1, int arg2, int arg3, int arg4, int arg5) {
		wrapped.copyArea(arg0, arg1, arg2, arg3, arg4, arg5);
	}
	public Graphics create() {
		return wrapped.create();
	}
	public Graphics create(int x, int y, int width, int height) {
		return wrapped.create(x, y, width, height);
	}
	public void dispose() {
		wrapped.dispose();
	}
	public void draw3DRect(int arg0, int arg1, int arg2, int arg3, boolean arg4) {
		wrapped.draw3DRect(arg0, arg1, arg2, arg3, arg4);
	}
	public void drawArc(int arg0, int arg1, int arg2, int arg3, int arg4, int arg5) {
		wrapped.drawArc(arg0, arg1, arg2, arg3, arg4, arg5);
	}
	public void drawBytes(byte[] arg0, int arg1, int arg2, int arg3, int arg4) {
		wrapped.drawBytes(arg0, arg1, arg2, arg3, arg4);
	}
	public void drawChars(char[] arg0, int arg1, int arg2, int arg3, int arg4) {
		wrapped.drawChars(arg0, arg1, arg2, arg3, arg4);
	}
	public boolean drawImage(Image arg0, int arg1, int arg2, Color arg3, ImageObserver arg4) {
		return wrapped.drawImage(arg0, arg1, arg2, arg3, arg4);
	}
	public boolean drawImage(Image arg0, int arg1, int arg2, ImageObserver arg3) {
		return wrapped.drawImage(arg0, arg1, arg2, arg3);
	}
	public boolean drawImage(Image arg0, int arg1, int arg2, int arg3, int arg4, Color arg5, ImageObserver arg6) {
		return wrapped.drawImage(arg0, arg1, arg2, arg3, arg4, arg5, arg6);
	}
	public boolean drawImage(Image arg0, int arg1, int arg2, int arg3, int arg4, ImageObserver arg5) {
		return wrapped.drawImage(arg0, arg1, arg2, arg3, arg4, arg5);
	}
	public boolean drawImage(Image arg0, int arg1, int arg2, int arg3, int arg4, int arg5, int arg6, int arg7, int arg8,
			Color arg9, ImageObserver arg10) {
		return wrapped.drawImage(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10);
	}
	public boolean drawImage(Image arg0, int arg1, int arg2, int arg3, int arg4, int arg5, int arg6, int arg7, int arg8,
			ImageObserver arg9) {
		return wrapped.drawImage(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9);
	}
	public void drawLine(int arg0, int arg1, int arg2, int arg3) {
		wrapped.drawLine(arg0, arg1, arg2, arg3);
	}
	public void drawOval(int arg0, int arg1, int arg2, int arg3) {
		wrapped.drawOval(arg0, arg1, arg2, arg3);
	}
	public void drawPolygon(int[] arg0, int[] arg1, int arg2) {
		wrapped.drawPolygon(arg0, arg1, arg2);
	}
	public void drawPolygon(Polygon p) {
		wrapped.drawPolygon(p);
	}
	public void drawPolyline(int[] arg0, int[] arg1, int arg2) {
		wrapped.drawPolyline(arg0, arg1, arg2);
	}
	public void drawRect(int arg0, int arg1, int arg2, int arg3) {
		wrapped.drawRect(arg0, arg1, arg2, arg3);
	}
	public void drawRoundRect(int arg0, int arg1, int arg2, int arg3, int arg4, int arg5) {
		wrapped.drawRoundRect(arg0, arg1, arg2, arg3, arg4, arg5);
	}
	public void drawString(AttributedCharacterIterator arg0, int arg1, int arg2) {
		wrapped.drawString(arg0, arg1, arg2);
	}
	public void drawString(String arg0, int arg1, int arg2) {
		wrapped.drawString(arg0, arg1, arg2);
	}
	public boolean equals(Object obj) {
		return wrapped.equals(obj);
	}
	public void fill3DRect(int x, int y, int width, int height, boolean raised) {
		wrapped.fill3DRect(x, y, width, height, raised);
	}
	public void fillArc(int arg0, int arg1, int arg2, int arg3, int arg4, int arg5) {
		wrapped.fillArc(arg0, arg1, arg2, arg3, arg4, arg5);
	}
	public void fillOval(int arg0, int arg1, int arg2, int arg3) {
		wrapped.fillOval(arg0, arg1, arg2, arg3);
	}
	public void fillPolygon(int[] arg0, int[] arg1, int arg2) {
		wrapped.fillPolygon(arg0, arg1, arg2);
	}
	public void fillPolygon(Polygon p) {
		wrapped.fillPolygon(p);
	}
	public void fillRect(int arg0, int arg1, int arg2, int arg3) {
		wrapped.fillRect(arg0, arg1, arg2, arg3);
	}
	public void fillRoundRect(int arg0, int arg1, int arg2, int arg3, int arg4, int arg5) {
		wrapped.fillRoundRect(arg0, arg1, arg2, arg3, arg4, arg5);
	}
	public void finalize() {
		wrapped.finalize();
	}
	public Shape getClip() {
		return wrapped.getClip();
	}
	public Rectangle getClipBounds() {
		return wrapped.getClipBounds();
	}
	public Rectangle getClipBounds(Rectangle arg0) {
		return wrapped.getClipBounds(arg0);
	}
	@SuppressWarnings("deprecation")
	public Rectangle getClipRect() {
		return wrapped.getClipRect();
	}
	public Color getColor() {
		return wrapped.getColor();
	}
	public Font getFont() {
		return wrapped.getFont();
	}
	public FontMetrics getFontMetrics() {
		return wrapped.getFontMetrics();
	}
	public FontMetrics getFontMetrics(Font arg0) {
		return wrapped.getFontMetrics(arg0);
	}	
	public int hashCode() {
		return wrapped.hashCode();
	}
	public boolean hitClip(int arg0, int arg1, int arg2, int arg3) {
		return wrapped.hitClip(arg0, arg1, arg2, arg3);
	}
	public void setClip(int arg0, int arg1, int arg2, int arg3) {
		wrapped.setClip(arg0, arg1, arg2, arg3);
	}
	public void setClip(Shape arg0) {
		wrapped.setClip(arg0);
	}
	public void setColor(Color arg0) {
		wrapped.setColor(arg0);
	}
	public void setPaintMode() {
		wrapped.setPaintMode();
	}	
	public void setXORMode(Color arg0) {
		wrapped.setXORMode(arg0);
	}
	public String toString() {
		return wrapped.toString();
	}
	public void translate(int arg0, int arg1) {
		wrapped.translate(arg0, arg1);
	}
	@Override
	public void setFont(Font font) {
		wrapped.setFont(font);
		
	}
}