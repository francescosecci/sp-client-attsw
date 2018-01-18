package com.pufose.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.awt.AWTException;
import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JComboBox;

import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.pufose.client.GridFromServer;
import com.pufose.client.IClient;
import com.pufose.client.RestServiceClient;
import com.pufose.client.gui.GUI;
import com.pufose.client.gui.GUIpanel;

@RunWith(JUnit4.class)
public class GUIUnitTest  {

	private IClient cl;
	private GUIpanel pan;
	private GUI frame;
	@Before
	public void setUp() throws AWTException {
		frame = GUI.createGui(true);
		frame.mockClient(cl=Mockito.mock(IClient.class));
		frame.mockPane(Mockito.spy(new GUIpanel(10)));
		
	}
	
	@Test
	public void testCreateConnector() {

		
		RestServiceClient expected= new RestServiceClient("http://localhost:8080/api/");
		frame.createConnection("localhost","8080");
		ArgumentCaptor<RestServiceClient> captor = ArgumentCaptor.forClass(RestServiceClient.class);
		verify(cl).setRestServiceClient(captor.capture());
		RestServiceClient actual=captor.getValue();
		assertEquals(expected.getUrlToAll(),actual.getUrlToAll());
		assertEquals(expected.getUrlToGrid(),actual.getUrlToGrid());
		assertEquals(expected.getUrlToPath(),actual.getUrlToPath());

		
	}

	@Test
	public void testRetrieveAllGridsWhenEmptyList() throws IOException {
		when(cl.getAllTables()).thenReturn(Arrays.asList());
		List<String> list=frame.requestAll();
		assertEquals(Arrays.asList(),list);
		verify(cl,times(1)).getAllTables();
		
	}
	@Test
	public void testRetrieveAllGridsWhenSingleList() throws IOException {
		when(cl.getAllTables()).thenReturn(Arrays.asList("1"));
		List<String> list=frame.requestAll();
		assertEquals(Arrays.asList("1"),list);
		verify(cl,times(1)).getAllTables();
		
	}
	@Test
	public void testRetrieveAllGridsWhenNormalList() throws IOException {
		when(cl.getAllTables()).thenReturn(Arrays.asList("1","2"));
		List<String> list=frame.requestAll();
		assertEquals(Arrays.asList("1","2"),list);
		verify(cl,times(1)).getAllTables();
		
	}
	@Test
	public void testRetrieveAllGridsWhenNullPointerException() throws IOException{
		throwWhenRetrieveAll(new NullPointerException());
		
	}

	private void throwWhenRetrieveAll(Exception exc) throws IOException {
		when(cl.getAllTables()).thenThrow(exc);
		assertEquals(Arrays.asList(),frame.requestAll());
	}
	@Test
	public void testRetrieveAllGridsWhenIOException() throws IOException{
		throwWhenRetrieveAll(new IOException());
		
	}
	
	@Test
	public void testRetrieveOneGridWhenIOException() throws IOException{
		throwWhenRetrieveGrid(new IOException());
	}
	@Test
	public void testRetrieveOneGridWhenNullPointerException() throws IOException{
		throwWhenRetrieveGrid(new NullPointerException());
		
	}

	@Test
	public void testRetrieveOneGridWhenOk() throws IOException {
		when(cl.retrieveGrid("1")).thenReturn(new GridFromServer(1));
		GridFromServer retrieved=frame.caseRequestGrid("1");
		verify(cl,times(1)).retrieveGrid("1");
		assertEquals(new GridFromServer(1),retrieved);
		
	}
	private void throwWhenRetrieveGrid(Exception exc) throws IOException {
		when(cl.retrieveGrid("1")).thenThrow(exc);
		GridFromServer retrieved=frame.caseRequestGrid("1");
		assertNull(retrieved);
	}
	
	@Test
	public void testGetShortestPathWhenMissingSink() throws IOException {
		List<String> path=frame.caseRequestPath("from",null,"where");
		verify(cl,times(0)).getShortestPath(anyString(),anyString(),anyString());
		assertEquals(Arrays.asList(), path);
	}
	@Test
	public void testGetShortestPathWhenMissingSource() throws IOException {
		List<String> path=frame.caseRequestPath(null,"to","where");
		verify(cl,times(0)).getShortestPath(anyString(),anyString(),anyString());
		assertEquals(Arrays.asList(), path);
	}
	@Test
	public void testGetShortestPathWhenMissingWhere() throws IOException {
		List<String> path=frame.caseRequestPath("from","to",null);
		verify(cl,times(0)).getShortestPath(anyString(),anyString(),anyString());
		assertEquals(Arrays.asList(), path);
	}
	@Test
	public void testGetShortestPathWhenOKEmptyList() throws IOException {
		assertExpectedPath(Arrays.asList(""));
	}
	
	@Test
	public void testGetShortestPathWhenOKSingleList() throws IOException {
		assertExpectedPath(Arrays.asList("1"));
	}
	@Test
	public void testGetShortestPathWhenOKNormalList() throws IOException {
		assertExpectedPath(Arrays.asList("1","2"));
		
	}
	@Test
	public void testGetShortestPathWhenIOException() throws IOException {
		assertExpectedPathException(new IOException());
	}
	@Test
	public void testGetShortestPathWhenNullPointerException() throws IOException {
		assertExpectedPathException(new NullPointerException());
	}
	private void assertExpectedPath(List<String> expected) throws IOException {
		when(cl.getShortestPath("from", "to", "where")).thenReturn(expected);
		List<String> path=frame.caseRequestPath("from","to","where");
		verify(cl,times(1)).getShortestPath("from", "to", "where");
		assertEquals(expected, path);
	}
	private void assertExpectedPathException(Exception e) throws IOException {
		when(cl.getShortestPath("from", "to", "where")).thenThrow(e);
		List<String> path=frame.caseRequestPath("from","to","where");
		assertEquals(Arrays.asList(), path);
	}
	

	
	

}