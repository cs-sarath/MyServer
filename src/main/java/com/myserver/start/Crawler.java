package com.myserver.start;

import java.io.File;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import com.myserver.server.ServerContext;

public class Crawler {
	
	private ServerContext serverContext;
	
	public Crawler(ServerContext serverContext)
	{
		this.serverContext = serverContext;
	}
	
	public void addAll(){
		File directory = new File("webapps");
		
		//get all files from webapps directory
		File[] files = directory.listFiles();
		for(File file : files)
		{
			if(file.isDirectory())
			{
				try
				{
					//process deployed webapp, first step web.xml
					File webXML = new File(file.toString()+ File.separator+ "WEB-INF"+ File.separator+ "web.xml");
					if(webXML.exists())
					{
						serverContext.addWebApplicationContext(new WebXMLParser(webXML).parse());
						
						//add all libs and classes to classpath
						addWebAppDirectory(new File(file.toString()+ File.separator+ "WEB-INF"));
					}
				}
				catch(Exception e)
				{
					System.err.println("Couldn't add webapp");
					e.printStackTrace();
				}
			}
		}
	}

	private void addWebAppDirectory(File webAppDirectory) 
	{
		File[] fileList = webAppDirectory.listFiles();
		for(File file: fileList)
		{
			if(file.toString().endsWith("WEB-INF"+ File.separator+ "classes") ||
					file.toString().endsWith("WEB-INF"+ File.separator+ "lib"))
			{
				try 
				{
					addUrl(file.toURI().toURL());
				}
				catch (MalformedURLException e) 
				{
					e.printStackTrace();
				}
			}
		}
		
	}

	private void addUrl(URL url) 
	{
		try
		{
			URLClassLoader systemClassLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
			Class urlClass = URLClassLoader.class;
			Method method = urlClass.getDeclaredMethod("addURL", new Class[]{URL.class});
			method.setAccessible(true);
			method.invoke(systemClassLoader, new Object[]{url});
		}
		catch (Exception e)
		{
			System.err.println("failed to add " +url.toString() + " to classpath");
			e.printStackTrace();
		}
	}
}
