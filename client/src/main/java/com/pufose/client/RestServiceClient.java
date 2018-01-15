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
		final int prime = 31;
		int result = 1;
		result = prime * result + resp;
		result = prime * result + ((urlToAll == null) ? 0 : urlToAll.hashCode());
		result = prime * result + ((urlToGrid == null) ? 0 : urlToGrid.hashCode());
		result = prime * result + ((urlToPath == null) ? 0 : urlToPath.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RestServiceClient other = (RestServiceClient) obj;
		if (resp != other.resp)
			return false;
		if (urlToAll == null) {
			if (other.urlToAll != null)
				return false;
		} else if (!urlToAll.equals(other.urlToAll))
			return false;
		if (urlToGrid == null) {
			if (other.urlToGrid != null)
				return false;
		} else if (!urlToGrid.equals(other.urlToGrid))
			return false;
		if (urlToPath == null) {
			if (other.urlToPath != null)
				return false;
		} else if (!urlToPath.equals(other.urlToPath))
			return false;
		return true;
	}

	private HttpURLConnection createConnection(String url) throws IOException {
		URL uurl = new URL(url);
   	    return (HttpURLConnection)uurl.openConnection();

	}
	

	public int getLastResponse() {
		return this.resp;
	}

}
