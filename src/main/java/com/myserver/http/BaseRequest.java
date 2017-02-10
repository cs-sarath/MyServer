package com.myserver.http;

public class BaseRequest 
{
	private String method;
	
	public BaseRequest(String method)
	{
		this.method = method;
	}
	
	public String getMethod()
	{
		return method;
	}
}
