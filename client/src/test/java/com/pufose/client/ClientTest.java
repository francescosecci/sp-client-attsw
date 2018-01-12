package com.pufose.client;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class ClientTest {
	private Client client;
	private IRestServiceClient service;
	private static final int REQUEST_ALL=1;
	private static final int REQUEST_PATH=2;
	private static final int REQUEST_GRID = 3;
	@Before
	public void setUp() {
		client=new Client();
		client.setRestServiceClient(service=Mockito.mock(IRestServiceClient.class));
	}
	
	@Test(expected=NullPointerException.class)
	public void testGetAllGridsWhenNoServiceProvided() throws JsonSyntaxException, IOException {
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
	
	@Test(expected=JsonSyntaxException.class)
	public void testGetAllTablesFailServerCannotSendObjectToClient() throws IOException {
		when(service.doGet(REQUEST_ALL, null)).thenThrow(new IOException());
		when(service.getLastResponse()).thenReturn(500);
		client.getAllTables();

	}
	
}
