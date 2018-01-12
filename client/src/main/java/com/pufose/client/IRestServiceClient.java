package com.pufose.client;

import java.io.IOException;

import org.apache.http.ProtocolException;

public interface IRestServiceClient {
	public String doGet(int request, String args) throws IOException, ProtocolException;
	public int getLastResponse();

}
