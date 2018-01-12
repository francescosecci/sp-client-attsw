package com.pufose.client;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.http.ProtocolException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;




public class ClientRestIT {
	private WireMockServer mockedServer;
	private Client client;
	private RestServiceClient rsc;
	
	@Before
	public void setup() {
		  mockedServer = new WireMockServer(WireMockConfiguration.wireMockConfig().port(9000));
	      WireMock.configureFor("localhost", 9000);
		  mockedServer.start();
	      this.rsc=new RestServiceClient("http://localhost:9000/api/");
	      this.client=new Client();
	      client.setRestServiceClient(rsc);
	}
	@After
	public void teardown() {
		mockedServer.stop();
		
	}
	
	private void stubResponse(String url,String body, int responseCode) {
		 stubFor(get(urlEqualTo(url))
		          .willReturn(aResponse()
		              .withStatus(responseCode)
		              .withHeader("Content-Type", "application/json")
		              .withBody(body)));
	}
	
	@Test
	public void testGetPathOK() throws IOException, ProtocolException {
		String expected="[(A),(B)]";
		stubResponse("/api/pathATOBINgrid2",expected,200);
		List<String> expectedList= Arrays.asList("(A)","(B)");
		List<String> receivedList= client.getShortestPath("A", "B", "grid2");
		assertEquals(200,rsc.getLastResponse());
		assertEquals(expectedList,receivedList);
	
	}
	@Test(expected=IOException.class)
	public void testGetPathFailWhenServerUnreacheable() throws IOException {

		mockedServer.stop();
		client.getShortestPath("","","");
	
	}
	@Test(expected=IOException.class)
	public void testGetPathFailWhenServerCannotSendObjectToClient() throws IOException {
		stubResponse("/api/pathATOBINgrid2","not json",500);
		client.getShortestPath("A","B","grid2");
	}
	
	@Test
	public void testGetAllTablesOK() throws IOException {
		stubResponse("/api/","[\"1\",\"2\",\"3\"]",200);
		List<String> tables=client.getAllTables();
		assertEquals(200,rsc.getLastResponse());
		assertEquals(Arrays.asList("1","2","3"),tables);
	}
	@Test
	public void testGetAllTablesOKWhenEmptyList() throws IOException {
		stubResponse("/api/","[]",200);
		List<String> tables=client.getAllTables();
		assertEquals(200,rsc.getLastResponse());
		assertEquals(Arrays.asList(),tables);
	}
	@Test
	public void testGetAllTablesOKWhenSingleElementList() throws IOException {
		stubResponse("/api/","[\"1\"]",200);
		List<String> tables=client.getAllTables();
		assertEquals(200,rsc.getLastResponse());
		assertEquals(Arrays.asList("1"),tables);
	}
	@Test(expected=IOException.class)
	public void testGetAllTablesFailServerUnreacheable() throws IOException {
		mockedServer.stop();
		client.getAllTables();
		
	}
	
	@Test(expected=IOException.class)
	public void testGetAllTablesFailServerCannotSendObjectToClient() throws IOException {
		stubResponse("/api/","Not json",500);
		client.getAllTables();
		
	}
	@Test
	public void testGetATableOK() throws IOException {
		String jsongrid="{\"n\":5,\"matrix\":[[1,0,1,1,0],[1,1,1,1,1],[1,1,1,0,1],[1,0,1,1,0],[1,1,1,1,1]],\"id\":1}";
		stubResponse("/api/grid1",jsongrid,200);
		int[][] matrix = new int[][] {
			{1,0,1,1,0},
			{1,1,1,1,1},
			{1,1,1,0,1},
			{1,0,1,1,0},
			{1,1,1,1,1}
		};
		GridFromServer expectedGrid=new GridFromServer(matrix,1);
		GridFromServer rcv=client.retrieveGrid("1");
		assertEquals(expectedGrid,rcv);
		assertEquals(200,rsc.getLastResponse());
	}
	
	@Test(expected=IOException.class)
	public void testGetATableFailServerUnreacheable() throws IOException {
		mockedServer.stop();
		client.retrieveGrid("0");
	
	}
	
	@Test(expected=IOException.class)
	public void testGetATableFailServerCannotSendObjectToClient() throws IOException {
		stubResponse("/api/grid0","not json",500);
		client.retrieveGrid("0");
		
	}
	

}
