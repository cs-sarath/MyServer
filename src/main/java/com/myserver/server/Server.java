package com.myserver.server;

import java.io.IOException;
import java.net.UnknownHostException;

public interface Server {
	
	public void setPort();
	
	public void start() throws UnknownHostException, IOException;
	
	public void stop();
}
