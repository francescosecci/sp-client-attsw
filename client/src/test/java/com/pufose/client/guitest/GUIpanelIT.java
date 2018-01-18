package com.pufose.client.guitest;

import static org.mockito.Mockito.times;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.image.ImageObserver;
import java.text.AttributedCharacterIterator;

import org.assertj.swing.fixture.FrameFixture;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.pufose.client.gui.GUIpanel;
import static org.assertj.swing.fixture.Containers.showInFrame;

public class GUIpanelIT {
	private int GRIDSIZE;
	private GUIpanel pan;
	private FrameFixture framecontainer;
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
	@Before
	public void setUp() {
		
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
