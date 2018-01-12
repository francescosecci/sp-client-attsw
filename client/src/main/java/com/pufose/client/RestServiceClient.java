package com.pufose.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.ProtocolException;
import org.eclipse.jetty.http.HttpTester.Message;

public class RestServiceClient implements IRestServiceClient {

	private String urlToGrid;
	private String urlToPath;
	private String urlToAll;
	private int resp;
	public RestServiceClient(String urlToApi) {
		this.urlToAll = urlToApi;
		this.urlToGrid = urlToApi + "grid";
		this.urlToPath = urlToApi + "path";
		this.resp=0;
	}

	public String doGet(int request, String args) throws IOException, ProtocolException {
		switch (request) {
		case 1: {
			return manageAll();
		}
		case 2: {
			return manage(urlToGrid,args);
		}
		case 3: {
			return null;
		}
		default: {
			throw new IllegalArgumentException("");
		}
		}
	}

	private String manageAll() throws ProtocolException, IOException {
		return manage(urlToAll, "");
	}

	private String manage(String url, String args) throws IOException, ProtocolException {
		HttpURLConnection conn = getConnection(url + args);
		if (conn != null)
			return read(conn);
		return null;

	}
	private String read(HttpURLConnection conn) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(
				(conn.getInputStream())));
		StringBuilder sb=new StringBuilder();
		String line=br.readLine();
		while(line!=null) {
			sb.append(line);
			line=br.readLine();
		}
		return sb.toString();
	}
	
	private HttpURLConnection getConnection(String url) throws IOException, ProtocolException {
		HttpURLConnection connection = createConnection(url);
		this.resp = connection.getResponseCode();
		return connection;
	}
	private HttpURLConnection createConnection(String url)
			throws MalformedURLException, IOException, ProtocolException {
		URL _url = new URL(url);
	    HttpURLConnection connection = (HttpURLConnection)_url.openConnection();
	    connection.setRequestMethod("GET");
		return connection;
	}
	

	public int getLastResponse() {
		return this.resp;
	}

}
