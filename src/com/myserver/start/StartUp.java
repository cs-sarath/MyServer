package com.myserver.start;

import java.io.IOException;

import com.myserver.server.HttpServer;
import com.myserver.server.ServerContext;

public class StartUp {
	
	public void start()
	{
		//get the jars and add to classpath
		new Crawler(ServerContext.getInstance()).addAll();
		
		//all ready then start the server
		try 
		{
			new HttpServer().start();
		} 
		catch (IOException e) 
		{
			System.err.println("Unable to start server");
			e.printStackTrace();
		}
	}
	
	public void stop()
	{
		
	}

	public static void main(String[] args)
	{
		new StartUp().start();
	}
}
