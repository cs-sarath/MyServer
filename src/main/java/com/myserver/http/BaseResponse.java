package com.myserver.http;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

public class BaseResponse 
{
	private PrintWriter writer;
	
	public BaseResponse(DataOutputStream outputStream)
	{
		writer = new PrintWriter(outputStream);
	}
	
	public PrintWriter getWriter()
	{
		return writer;
	}
	
	public void close() throws IOException
	{
		writer.close();
	}
}
