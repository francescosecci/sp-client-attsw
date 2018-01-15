package com.pufose.client.guitest;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.awt.AWTException;
import java.awt.Color;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;

import javax.swing.JComboBox;

import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;

import com.pufose.client.GridFromServer;
import com.pufose.client.IClient;
import com.pufose.client.gui.GUI;
import com.pufose.client.gui.GUIpanel;

@RunWith(JUnit4.class)
public class GUITest  {
	private FrameFixture window;
	private IClient cl;
	private GUI frame;
	@Before
	public void setUp() throws AWTException {
		frame = GuiActionRunner.execute(()-> new GUI());
		frame.mockClient(cl=Mockito.mock(IClient.class));
		window=new FrameFixture(frame);
		window.show();
		
	}
	//NO-INTERNET SCENARIO TESTS START
	@Test
	public void testInitialization() {
		window.requireTitle("Shortest Path Client - ATTSW Project 17-18");
		window.textBox("serverField").requireText("localhost");
		window.textBox("portField").requireText("8080");
		window.textBox("apiField").requireText("/api");
		window.label("lblOutput").requireText("");
		window.textBox("sinkField").requireText("");
		window.textBox("sourceField").requireText("");
		@SuppressWarnings("unchecked")
		JComboBox<String> tgt1=window.comboBox("gridCombo").target();
		@SuppressWarnings("unchecked")
		JComboBox<String> tgt2=window.comboBox("actionsCombo").target();
		String item1=tgt2.getItemAt(0);
		String item2=tgt2.getItemAt(1);
		String item3=tgt2.getItemAt(2);
		assertEquals("Show all grids",item1);
		assertEquals("Request a grid",item2);
		assertEquals("Request shortest path",item3);
		assertEquals(0,tgt1.getItemCount());
		assertEquals(3,tgt2.getItemCount());
	}
	
	private GUIpanel getGuiPanel() {
		return (GUIpanel)(window.panel("guiPanel").target());
	}
	
	//NO-INTERNET SCENARIO TESTS END
	
	
	//RETRIEVE ALL GRIDS SCENARIO TESTS START
	@Test
	public void testRetrieveAllGridsWhenOK() throws IOException {
		int expected=5;
		retrieveAllGrids(expected);
		verify(cl,times(1)).getAllTables();
		@SuppressWarnings("unchecked")
		JComboBox<String> target=window.comboBox("gridCombo").target();
		int size=target.getItemCount();
		assertEquals(5,size);
		window.label("lblOutput").requireText(GUI.OPERATION_OK);
		for(int i=0; i<size;i++) {
			assertEquals(""+i,target.getItemAt(i));
		}
	}
	@Test
	public void testRetrieveAllGridsWhenUserIsNotConnected() throws IOException {
		when(cl.getAllTables()).thenThrow(new NullPointerException());
		window.comboBox("actionsCombo").selectItem(0);
		window.button("btnPerform").click();
		window.label("lblOutput").requireText(GUI.NO_CONNECTOR);
		
		
	}

	
	@Test
	public void testRetrieveAllGridsWhenServerCannotPerformOperation() throws IOException {
		when(cl.getAllTables()).thenThrow(new IOException());
		window.comboBox("actionsCombo").selectItem(0);
		window.button("btnPerform").click();
		window.label("lblOutput").requireText(GUI.SERVER_ERROR);
	}
	
	private void retrieveAllGrids(int expected) throws IOException {
		String[] array=new String[expected];
		for(int i=0; i<expected;i++) {
			array[i]=""+i;
		}
		when(cl.getAllTables()).thenReturn(Arrays.asList(array));
		window.comboBox("actionsCombo").selectItem(0);
		window.button("btnPerform").click();
		
	}
	//RETRIEVE ALL GRIDS SCENARIO TESTS END
	
	//RETRIEVE ONE GRID SCENARIO TESTS START
	@Test
	public void testRequestGridWhenNotRetrievedAll() throws IOException {
		window.comboBox("actionsCombo").selectItem(1);
		window.button("btnPerform").click();
		window.label("lblOutput").requireText("Error, you must retrieve all grids first");
		
	}

	@Test
	public void testRequestGridWhenServerCannotPerformOperation() throws IOException {
		retrieveAllGrids(1);
		when(cl.retrieveGrid("0")).thenThrow(new IOException(""));
		window.comboBox("actionsCombo").selectItem(1);
		window.comboBox("gridCombo").selectItem(0);
		window.button("btnPerform").click();
		window.label("lblOutput").requireText(GUI.SERVER_ERROR);
	}
	@Test
	public void testRequestGridWhenNotConnected() throws IOException{
		frame.mockClient(null);
		window.comboBox("actionsCombo").selectItem(1);
		window.button("btnPerform").click();
		window.label("lblOutput").requireText("Error, you must create a connector before");
	}
	@Test
	public void testRequestGridWhenOK() throws  IOException {
		retrieveAllGrids(1);
		window.comboBox("actionsCombo").selectItem(1);
		window.comboBox("gridCombo").selectItem(0);
		when(cl.retrieveGrid("0")).thenReturn(new GridFromServer(new int[][] 
				{{1,0,0},{1,1,0},{1,1,1}},0));
		window.button("btnPerform").click();
		window.label("lblOutput").requireText(GUI.OPERATION_OK);
		GUIpanel pan=getGuiPanel();
		assertEquals(Color.RED, pan.getColorInPoint(0, 0));
		assertEquals(Color.BLACK, pan.getColorInPoint(0, 1));
		assertEquals(Color.BLACK, pan.getColorInPoint(0, 2));
		
		assertEquals(Color.RED, pan.getColorInPoint(1, 0));
		assertEquals(Color.RED, pan.getColorInPoint(1, 1));
		assertEquals(Color.BLACK, pan.getColorInPoint(1, 2));
		
		assertEquals(Color.RED, pan.getColorInPoint(2, 0));
		assertEquals(Color.RED, pan.getColorInPoint(2, 1));
		assertEquals(Color.RED, pan.getColorInPoint(2, 2));
		for(int i=0; i<3;i++) {
			for(int j=0; j<3;j++) {
				String expected_name="";
				if(pan.getColorInPoint(i, j).equals(Color.RED)) 
					expected_name=String.format("%d_%d", i,j);
				assertEquals(expected_name,pan.getPrintedNameIn(i, j));
				
			}
		}
		window.comboBox("gridCombo").requireDisabled();
		verify(cl,times(1)).getAllTables();
	}
	
	private GridFromServer createGridWithId(int id) {
		int[][] matrix=new int[][] {
			{1,0,0},
			{1,1,0},
			{1,1,1},
			
		};
		return new GridFromServer(matrix,id);
	}
	private void requestAGrid(int id) throws IOException {
		retrieveAllGrids(id+1);
		window.comboBox("actionsCombo").selectItem(1);
		window.comboBox("gridCombo").selectItem(id);
		when(cl.retrieveGrid("0")).thenReturn(createGridWithId(id));
		window.button("btnPerform").click();
		
	}
	//RETRIEVE ONE GRID SCENARIO TESTS END
	
	//REQUEST SHORTEST PATH SCENARIO TESTS START
	@Test
	public void testRequestShortestPathWhenNotConnected() throws IOException  {
		frame.mockClient(null);
		window.comboBox("actionsCombo").selectItem(2);
		window.textBox("sourceField").setText("test");
		window.textBox("sinkField").setText("test");
		when(cl.getShortestPath(Mockito.anyString(),Mockito.anyString() ,Mockito.anyString())).thenThrow(new NullPointerException());
		window.button("btnPerform").click();
		window.label("lblOutput").requireText(GUI.NO_CONNECTOR);
		verify(cl,times(0)).getShortestPath(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
		
	}
	
	@Test
	public void testRequestShortestPathWhenServerUnreacheable() throws IOException  {
		requestAGrid(0);
		window.textBox("sourceField").setText("test");
		window.textBox("sinkField").setText("test");
		window.comboBox("actionsCombo").selectItem(2);
		when(cl.getShortestPath(Mockito.anyString(),Mockito.anyString() ,Mockito.anyString())).thenThrow(new IOException());
		window.button("btnPerform").click();
		window.label("lblOutput").requireText(GUI.SERVER_ERROR);
		
		
	}
	
	@Test
	public void testRequestShortestPathWhenNotRetrievedAllGrids() throws IOException {
		window.comboBox("actionsCombo").selectItem(2);
		window.button("btnPerform").click();
		window.label("lblOutput").requireText("Error, you must retrieve a grid first");
		verify(cl,times(0)).getShortestPath(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
	}
	@Test
	public void testRequestShortestPathWhenNotRetrievedTargetGridButOnlyAllGrids() throws IOException{
		retrieveAllGrids(1);
		window.comboBox("actionsCombo").selectItem(2);
		window.button("btnPerform").click();
		window.label("lblOutput").requireText("Error, you must retrieve a grid first");
		verify(cl,times(0)).getShortestPath(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
	}
	@Test
	public void testRequestShortestPathWhenNotSpecifiedSource() throws IOException {
		requestAGrid(0);
		window.comboBox("actionsCombo").selectItem(2);
		window.button("btnPerform").click();
		window.label("lblOutput").requireText("Insert source node");
		verify(cl,times(0)).getShortestPath(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
	}
	
	@Test
	public void testRequestShortestPathWhenNotSpecifiedSink() throws IOException {
		requestAGrid(0);
		window.comboBox("actionsCombo").selectItem(2);
		window.textBox("sourceField").setText("test");
		window.button("btnPerform").click();
		window.label("lblOutput").requireText("Insert sink node");
		verify(cl,times(0)).getShortestPath(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
	}
	@Test
	public void testRequestShortestPathWhenServerCannotFindSinkNode() throws IOException {
		requestAGrid(0);
		window.comboBox("actionsCombo").selectItem(2);
		window.textBox("sourceField").setText("0_0");
		window.textBox("sinkField").setText("test");
		when(cl.getShortestPath(Mockito.eq("test"),Mockito.eq("0_0"), Mockito.anyString())).thenReturn(new LinkedList<String>());
		window.button("btnPerform").click();
		window.label("lblOutput").requireText("No paths found from source node to sink");
		verify(cl,times(1)).getShortestPath(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
	}
	@Test
	public void requestShortestPathWhenServerCannotFindSourceNode() throws IOException {
		requestAGrid(0);
		window.comboBox("actionsCombo").selectItem(2);
		window.textBox("sourceField").setText("test");
		window.textBox("sinkField").setText("0_0");
		when(cl.getShortestPath(Mockito.eq("test"),Mockito.eq("0_0") , Mockito.anyString())).thenReturn(new LinkedList<String>());
		window.button("btnPerform").click();
		window.label("lblOutput").requireText("No paths found from source node to sink");
		verify(cl,times(1)).getShortestPath(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
		
	}
	
	@Test
	public void requestShortestPathOK() throws  IOException {
		shortestPathOkScenario();
		window.label("lblOutput").requireText(GUI.OPERATION_OK);
		verify(cl,times(1)).getShortestPath(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
		GUIpanel pan=getGuiPanel();
		assertEquals(GUIpanel.DARKGREEN,pan.getColorInPoint(0, 0));
		assertEquals(Color.BLACK,pan.getColorInPoint(0, 1));
		assertEquals(Color.BLACK,pan.getColorInPoint(0, 2));
		
		assertEquals(GUIpanel.DARKGREEN,pan.getColorInPoint(1, 0));
		assertEquals(Color.RED,pan.getColorInPoint(1, 1));
		assertEquals(Color.BLACK,pan.getColorInPoint(1, 2));
		
		assertEquals(GUIpanel.DARKGREEN,pan.getColorInPoint(2, 0));
		assertEquals(Color.RED,pan.getColorInPoint(2, 1));
		assertEquals(Color.RED,pan.getColorInPoint(2, 2));
		
		
	}
	
	@Test
	public void testReset() throws IOException {
		shortestPathOkScenario();
		GUIpanel pan=getGuiPanel();
		window.button("btnReset").click();
		for(int i=0; i<3;i++) {
			for(int j=0; j<3;j++) {
				assertEquals(pan.getBackground(),pan.getColorInPoint(i, j));
			}
		}
		window.comboBox("gridCombo").requireEnabled();
	}
	
	private void shortestPathOkScenario() throws IOException {
		requestAGrid(0);
		window.comboBox("actionsCombo").selectItem(2);
		window.textBox("sourceField").setText("0_0");
		window.textBox("sinkField").setText("2_0");
		when(cl.getShortestPath(Mockito.eq("0_0"),Mockito.eq("2_0") , Mockito.anyString())).thenReturn(Arrays.asList("0_0","1_0","2_0"));
		window.button("btnPerform").click();
		
		
	}
	
	//REQUEST SHORTEST PATH SCENARIO TESTS END
	@After
	public void tearDown() {
		window.cleanUp();
		
	}
	
	

}