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
		if(request==1) {
			return manageAll();
		}
		else if(request==2) {
			return manage(urlToGrid,args);
		}
		else if(request==3) {
			return manage(urlToPath,args);
		}
		else {
			throw new IllegalArgumentException("");
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
		connection.setConnectTimeout(2000);
		this.resp = connection.getResponseCode();
		return connection;
		
	}
	@Override
	public int hashCode() {

		final int prime = 31;// $COVERAGE-IGNORE$
		int result = 1;// $COVERAGE-IGNORE$
		result = prime * result + resp;// $COVERAGE-IGNORE$
		result = prime * result + ((urlToAll == null) ? 0 : urlToAll.hashCode());// $COVERAGE-IGNORE$
		result = prime * result + ((urlToGrid == null) ? 0 : urlToGrid.hashCode());// $COVERAGE-IGNORE$
		result = prime * result + ((urlToPath == null) ? 0 : urlToPath.hashCode());// $COVERAGE-IGNORE$
		return result;// $COVERAGE-IGNORE$
	}

	
	@Override
	public boolean equals(Object obj) {
	
		if (this == obj) // $COVERAGE-IGNORE$
			return true; // $COVERAGE-IGNORE$
		if (obj == null) // $COVERAGE-IGNORE$
			return false; // $COVERAGE-IGNORE$
		if (getClass() != obj.getClass()) // $COVERAGE-IGNORE$
			return false; // $COVERAGE-IGNORE$
		RestServiceClient other = (RestServiceClient) obj; // $COVERAGE-IGNORE$
		if (resp != other.resp) // $COVERAGE-IGNORE$
			return false; // $COVERAGE-IGNORE$
		if (urlToAll == null) { // $COVERAGE-IGNORE$
			if (other.urlToAll != null) // $COVERAGE-IGNORE$
				return false; // $COVERAGE-IGNORE$
		} else if (!urlToAll.equals(other.urlToAll)) // $COVERAGE-IGNORE$
			return false; // $COVERAGE-IGNORE$
		if (urlToGrid == null) { // $COVERAGE-IGNORE$
			if (other.urlToGrid != null) // $COVERAGE-IGNORE$
				return false;// $COVERAGE-IGNORE$
		} else if (!urlToGrid.equals(other.urlToGrid))// $COVERAGE-IGNORE$
			return false;// $COVERAGE-IGNORE$
		if (urlToPath == null) {// $COVERAGE-IGNORE$
			if (other.urlToPath != null)// $COVERAGE-IGNORE$
				return false;// $COVERAGE-IGNORE$
		} else if (!urlToPath.equals(other.urlToPath))// $COVERAGE-IGNORE$
			return false;// $COVERAGE-IGNORE$
		return true;// $COVERAGE-IGNORE$
	}

	private HttpURLConnection createConnection(String url) throws IOException {
		URL uurl = new URL(url);
   	    return (HttpURLConnection)uurl.openConnection();

	}
	

	public int getLastResponse() {
		return this.resp;
	}


}
