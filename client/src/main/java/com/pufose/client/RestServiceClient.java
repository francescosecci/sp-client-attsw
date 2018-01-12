package com.pufose.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

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

	public String doGet(int request, String args) throws IOException {
		switch (request) {
		case 1: {
			return manageAll();
		}
		case 2: {
			return manage(urlToGrid,args);
		}
		case 3: {
			return manage(urlToPath,args);
		}
		default: {
			throw new IllegalArgumentException("");
		}
		}
	}

	private String manageAll() throws IOException  {
		return manage(urlToAll, "");
	}

	private String manage(String url, String args) throws IOException  {
		return read(getConnection(url + args));

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
	
	private HttpURLConnection getConnection(String url) throws IOException {
		HttpURLConnection connection;
		connection = createConnection(url);
		this.resp = connection.getResponseCode();
		return connection;
		
	}
	private HttpURLConnection createConnection(String url) throws IOException {
		URL uurl = new URL(url);
	    HttpURLConnection connection = (HttpURLConnection)uurl.openConnection();
		return connection;
	}
	

	public int getLastResponse() {
		return this.resp;
	}

}
