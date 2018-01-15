package com.pufose.client;

import java.io.IOException;
import java.util.List;

import com.google.gson.Gson;
public class Client implements IClient {
	private IRestServiceClient restclient;
	private Gson gson;

	public Client() {
		this.gson = new Gson();
	}

	@SuppressWarnings("unchecked")
	public List<String> getAllTables() throws IOException {
		
		String rcv = (restclient.doGet(1, null));
		return (List<String>) (gson.fromJson(rcv, List.class));
		
	}
	public GridFromServer retrieveGrid(String name) throws IOException {
		return gson.fromJson(restclient.doGet(2, name), GridFromServer.class);
		
	}

	@SuppressWarnings("unchecked")
	public List<String> getShortestPath(String fromName, String toName, String where) throws IOException  {
			String rcv=(restclient.doGet(3, fromName+"TO"+toName+"IN"+where));
			return (List<String>)(gson.fromJson(rcv, List.class));
		
	}

	public void setRestServiceClient(IRestServiceClient restServiceClient) {
		this.restclient = restServiceClient;

	}


}
