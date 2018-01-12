package com.pufose.client;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.http.ProtocolException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.gson.Gson;

public class ClientTest {
	private Client client;
	private IRestServiceClient service;
	private static final int REQUEST_ALL = 1;
	private static final int REQUEST_GRID = 2;
	private static final int REQUEST_PATH = 3;
	
	@Before
	public void setUp() {
		client=new Client();
		client.setRestServiceClient(service=Mockito.mock(IRestServiceClient.class));
	}
	
	@Test(expected=NullPointerException.class)
	public void testGetAllGridsWhenNoServiceProvided() throws  IOException {
		client.setRestServiceClient(null);
		client.getAllTables();
	}
	@Test
	public void testGetAllTablesOK() throws IOException {
		Mockito.doReturn(new Gson().toJson(Arrays.asList("1","2","3"))).when(service).doGet(REQUEST_ALL, null);
		List<String> tables=client.getAllTables();
		verify(service,times(1)).doGet(REQUEST_ALL, null);
		assertEquals(Arrays.asList("1","2","3"),tables);
	}
	@Test
	public void testGetAllTablesOKWhenEmptyList() throws IOException {
		Mockito.doReturn(new Gson().toJson(Arrays.asList())).when(service).doGet(REQUEST_ALL, null);
		List<String> tables=client.getAllTables();
		verify(service,times(1)).doGet(REQUEST_ALL, null);
		assertEquals(Arrays.asList(),tables);
	}
	@Test
	public void testGetAllTablesOKWhenSingleElementList() throws IOException {
		Mockito.doReturn(new Gson().toJson(Arrays.asList("1"))).when(service).doGet(REQUEST_ALL, null);
		List<String> tables=client.getAllTables();
		verify(service,times(1)).doGet(REQUEST_ALL, null);
		assertEquals(Arrays.asList("1"),tables);
	}
	@Test(expected=IOException.class)
	public void testGetAllTablesFailServerUnreacheable() throws IOException {
		Mockito.doThrow(new IOException()).when(service).doGet(REQUEST_ALL, null);
		client.getAllTables();
	}
	
	@Test(expected=IOException.class)
	public void testGetAllTablesFailServerCannotSendObjectToClient() throws IOException {
		when(service.doGet(REQUEST_ALL, null)).thenThrow(new IOException());
		client.getAllTables();

	}
	@Test
	public void testGetATableOK() throws IOException {
		int[][] matrix = new int[][] {
			{1,0},
			{0,1}
		};
		GridFromServer fixture=new GridFromServer(matrix,0);
		String json=new Gson().toJson(fixture);
		Mockito.doReturn(json).when(service).doGet(REQUEST_GRID, "0");
		GridFromServer rcv=client.retrieveGrid("0");
		verify(service,times(1)).doGet(REQUEST_GRID, "0");
		assertEquals(fixture,rcv);
	}
	
	@Test(expected=IOException.class)
	public void testGetATableFailServerUnreacheable() throws IOException {
		Mockito.doThrow(new IOException()).when(service).doGet(REQUEST_GRID, "0");
		client.retrieveGrid("0");
		
	}
	
	@Test(expected=IOException.class)
	public void testGetATableFailServerCannotSendObjectToClient() throws IOException {
		when(service.doGet(REQUEST_GRID, "0")).thenThrow(new IOException());
		client.retrieveGrid("0");
	}
	@Test
	public void testGetPathOK() throws IOException, ProtocolException {
		String fromName="node1";
		String toName="node2";
		Mockito.doReturn(new Gson().toJson(Arrays.asList(fromName,toName))).when(service).doGet(REQUEST_PATH,"node1TOnode2INwhere");
		List<String> path=client.getShortestPath(fromName,toName,"where");
		verify(service,times(1)).doGet(REQUEST_PATH, fromName+"TO"+toName+"INwhere");
		assertEquals(Arrays.asList(fromName,toName),path);
	
	}
	@Test(expected=IOException.class)
	public void testGetPathFailWhenServerUnreacheable() throws  IOException {
		String fromName="node1";
		String toName="node2";
		String in="grid";
		Mockito.doThrow(new IOException()).when(service).doGet(REQUEST_PATH, fromName+"TO"+toName+"IN"+in);
	    client.getShortestPath(fromName, toName, in);
		
		
	}
	@Test(expected=IOException.class)
	public void testGetPathFailWhenServerCannotSendObjectToClient() throws  IOException {
		String fromName="node1";
		String toName="node2";
		String in="grid";
		when(service.doGet(REQUEST_PATH, fromName+"TO"+toName+"IN"+in)).thenThrow(new IOException());
		client.getShortestPath(fromName, toName, in);
		
	}
	
	
	
}
