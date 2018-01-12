package com.pufose.client;

import java.io.IOException;
import java.util.List;

import org.apache.http.ProtocolException;


public interface IClient {
	public List<String> getAllTables() throws  IOException, ProtocolException;
	public GridFromServer retrieveGrid(String name) throws  IOException, ProtocolException;
	public List<String> getShortestPath(String fromName, String toName, String where)throws IOException, ProtocolException;
	public void setRestServiceClient(IRestServiceClient restServiceClient);
}
