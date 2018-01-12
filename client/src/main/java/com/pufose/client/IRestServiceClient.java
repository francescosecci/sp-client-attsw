package com.pufose.client;

import java.io.IOException;

public interface IRestServiceClient {
	public String doGet(int request, String args) throws IOException;
	public void doLogin() throws RuntimeException,IOException;
	public int getLastResponse();

}
