package com.pufose.client;

import java.io.IOException;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class Client implements IClient {
	private IRestServiceClient restclient;
	private Gson gson;

	public Client() {
		this.gson = new Gson();
	}

	public Client(IRestServiceClient restclient) {
		this.gson = new Gson();
		this.restclient = restclient;
	}

	@SuppressWarnings("unchecked")
	public List<String> getAllTables() throws JsonSyntaxException, IOException {
		try {
			String rcv = (restclient.doGet(1, null));
			return (List<String>) (gson.fromJson(rcv, List.class));
		} catch (IOException IOe) {
			manageException(IOe);
			return null;
		}
	}

	private void manageException(IOException IOe) throws IOException {
		if (restclient.getLastResponse() >= 500) {
			throw new JsonSyntaxException("");
		} else {
			throw IOe;
		}
	}

	public GridFromServer retrieveGrid(String name) throws JsonSyntaxException, IOException {
		try {
			GridFromServer retrieved = gson.fromJson(restclient.doGet(2, name), GridFromServer.class);
			return retrieved;
		} catch (IOException IOe) {
			manageException(IOe);
			return null;
		}

	}

	@SuppressWarnings("unchecked")
	public List<String> getShortestPath(String fromName, String toName, String where) throws JsonSyntaxException, IOException  {
		try {
			String rcv=(restclient.doGet(3, fromName+"TO"+toName+"IN"+where));
			return (List<String>)(gson.fromJson(rcv, List.class));
		}catch(IOException IOe) {
			manageException(IOe);
			return null;
		}
	}

	public void setRestServiceClient(IRestServiceClient restServiceClient) {
		this.restclient = restServiceClient;

	}

}
