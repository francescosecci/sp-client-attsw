package com.pufose.client;

import static org.junit.Assert.*;

import org.apache.http.ProtocolException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.junit.Assert.assertEquals;


import java.io.IOException;
public class RestServiceClientTest {

	private RestServiceClient fixture;
	private WireMockServer mockedServer;
	
	
	@Before
	public void setup() {
		this.mockedServer = new WireMockServer(WireMockConfiguration.wireMockConfig().port(8000));
	      WireMock.configureFor("localhost", 8000);
	      mockedServer.start();
	      this.fixture=new RestServiceClient("http://localhost:8000/api/");
	}
	
	@After
	public void teardown() {
		this.mockedServer.stop();
	}
	
	private void stubResponse(String url,String body, int responseCode) {
		 stubFor(get(urlEqualTo(url))
		          .willReturn(aResponse()
		              .withStatus(responseCode)
		              .withHeader("Content-Type", "application/json")
		              .withBody(body)));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testDoGetWhenWrongRequest() throws IOException, ProtocolException {
		fixture.doGet(-1, null);
		
	}
	
	@Test
	public void testDoGetRequestAllOKEmptyList() throws IOException, ProtocolException {
		stubResponse("/api/","[]",200);
		String response=fixture.doGet(1, null);
		assertEquals(200,fixture.getLastResponse());
		assertEquals("[]",response);
	}
	
	@Test
	public void testDoGetRequestAllOKSingleElementList() throws IOException, ProtocolException {
		stubResponse("/api/","[1]",200);
		String response=fixture.doGet(1, null);
		assertEquals(200,fixture.getLastResponse());
		assertEquals("[1]",response);
	}
	
	@Test
	public void testDoGetRequestAllOKRegularList() throws IOException, ProtocolException {
		stubResponse("/api/","[1,2,3,4,5]",200);
		String response=fixture.doGet(1, null);
		assertEquals(200,fixture.getLastResponse());
		assertEquals("[1,2,3,4,5]",response);
	}
	
	@Test(expected=IOException.class)
	public void testDoGetRequestAllWrongWhenServerBecameUnreachable() throws IOException, ProtocolException {
		mockedServer.stop();
		fixture.doGet(1, null);
	}
	
	@Test(expected=IOException.class)
	public void testDoGetRequestAllWrongWhenServerCannotPerformOperation() throws IOException, ProtocolException {
		stubResponse("/api/","An error",500);
		fixture.doGet(1, null);
	}
	
	@Test
	public void testDoGetRequestAGridOK() throws IOException, ProtocolException {
		String jsongrid="{\"n\":5,\"matrix\":[[1,0,1,1,0],[1,1,1,1,1],[1,1,1,0,1],[1,0,1,1,0],[1,1,1,1,1]],\"id\":1}";
		stubResponse("/api/grid1",jsongrid,200);
		String received=fixture.doGet(2,"1");
		assertEquals(200,fixture.getLastResponse());
		assertEquals(jsongrid,received);
	}
	
	@Test(expected=IOException.class)
	public void testDoGetRequestAGridWrongWhenServerBecameUnreachable() throws IOException, ProtocolException {
		mockedServer.stop();
		fixture.doGet(2,"1");
	}
	
	@Test(expected=IOException.class)
	public void testDoGetRequestGridWrongWhenServerCannotPerformOperation() throws IOException, ProtocolException {
		stubResponse("/api/grid2","Error",500);
		fixture.doGet(2, "2");
	}

}
