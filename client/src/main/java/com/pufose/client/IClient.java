package com.pufose.client;

import java.io.IOException;
import java.util.List;


public interface IClient {
	public List<String> getAllTables() throws  IOException;
	public GridFromServer retrieveGrid(String name) throws  IOException;
	public List<String> getShortestPath(String fromName, String toName, String where)throws IOException;
	public void setRestServiceClient(IRestServiceClient restServiceClient);
}
