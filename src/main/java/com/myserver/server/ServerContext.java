package com.myserver.server;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpSession;

import com.myserver.http.Session;

public class ServerContext 
{
	private Map<String, HttpSession> sessions = new ConcurrentHashMap<String, HttpSession>();
	private List<WebApplicationContext> webAppContexts;
	
	private ServerContext()
	{
		webAppContexts = new ArrayList<WebApplicationContext>();
	}
	
	public void addWebApplicationContext(WebApplicationContext webAppContext)
	{
		webAppContexts.add(webAppContext);
	}

	public WebApplicationContext getWebApplicationContext(String webAppName)
	{
		for(WebApplicationContext webAppConetxt : webAppContexts)
		{
			if(webAppConetxt.getWebAppName().equals(webAppName))
			{
				return webAppConetxt;
			}
		}
		return null;
	}
	
	public HttpSession getSession(String sessionId)
	{
		return sessions.get(sessionId);
	}
	
	public void putSession(String sessionId, HttpSession session)
	{
		sessions.put(sessionId, session);
	}
	
	public HttpSession createSession()
	{
		byte[] sessionByte = new byte[16];
		Random random = new Random();
		random.nextBytes(sessionByte);
		SessionBase sessionBase = new SessionBase();
		sessionBase.setSessionId(sessionByte.toString());
		Session session = new Session(sessionBase);
		return session;
	}
	
	static class InstanceHolder
	{
		static final ServerContext INSTANCE = new ServerContext();
	}
	
	public static ServerContext getInstance()
	{
		return InstanceHolder.INSTANCE;
	}
}
