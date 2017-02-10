package com.myserver.start;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;

public class ServletWrapper implements ServletConfig 
{
	private String servletName;
	private String servletClass;
	private HttpServlet instance;
	private Map<String,String> initParameters = new HashMap<String, String>();

	ServletWrapper(String servletName)
	{
		this.servletName = servletName;
	}

	@Override
	public String getInitParameter(String arg0) {
		return initParameters.get(arg0);
	}

	@Override
	public Enumeration<String> getInitParameterNames() {
		// TODO Auto-generated method stub
		return null;
	}

	void setInitParameter(String name, String value) {
		initParameters.put(name, value);
	}
	
	@Override
	public ServletContext getServletContext() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getServletName() {
		return servletName;
	}
	
	public void setServletClass(String servletClass) {
		this.servletClass = servletClass;
	}
	
	public String getServletClass() {
		return servletClass;
	}

	public HttpServlet getInstance() {
		if(instance == null)
		{
			try
			{
				Class servletClazz = Class.forName(servletClass);
				instance = (HttpServlet)servletClazz.getConstructor().newInstance();
				instance.init(this);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return instance;
	}
	
}
