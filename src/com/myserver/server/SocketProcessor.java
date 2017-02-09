package com.myserver.server;


import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.StringTokenizer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import com.myserver.http.BaseRequest;
import com.myserver.http.BaseResponse;
import com.myserver.http.Request;
import com.myserver.http.Response;
import com.myserver.start.ServletWrapper;

public class SocketProcessor 
{
	private ServerContext serverContext;
	
	public SocketProcessor(ServerContext serverContext)
	{
		this.serverContext = serverContext;
	}
	
	public void processSocket(Socket clientSocket) throws Exception
	{		
		System.out.println("SocketProcessor process "+clientSocket.hashCode());
		BufferedReader inputFromClient  = new BufferedReader(new InputStreamReader (clientSocket.getInputStream()));
		DataOutputStream outputToClient = new DataOutputStream(clientSocket.getOutputStream());
		
		String headerLine = inputFromClient.readLine();
		StringTokenizer headTokenizer = new StringTokenizer(headerLine);
		String httpMethod = headTokenizer.nextToken();
		String httpQueryString = headTokenizer.nextToken();
		
		StringTokenizer queryTokenizer = new StringTokenizer(httpQueryString, "/");
		if(queryTokenizer.hasMoreTokens())
		{
			String webAppName = queryTokenizer.nextToken();
			WebApplicationContext webAppContext = serverContext.getWebApplicationContext(webAppName);
			if(queryTokenizer.hasMoreTokens())
			{
				String requestUrl = queryTokenizer.nextToken();
				StringTokenizer urlTokenizer = new StringTokenizer(requestUrl, "?");
				String action = urlTokenizer.nextToken();
				
				ServletWrapper servletWrapper = webAppContext.getServletForUrlPattern("/" + action);
				HttpServlet servlet = servletWrapper.getInstance();
				if(servlet != null)
				{
					BaseRequest baseRequest = prepareBaseRequest(httpMethod);
					Request request = prepareRequest(baseRequest);
					BaseResponse baseResponse = prepareBaseResponse(outputToClient);
					Response response = prepareResponse(baseResponse);
					if(urlTokenizer.hasMoreTokens())
					{
						StringTokenizer parametersTokenizer = new StringTokenizer(urlTokenizer.nextToken(), "&");
						while(parametersTokenizer.hasMoreTokens())
						{
							String[] keyValuePair = parametersTokenizer.nextToken().split("=");
							request.setAttribute(keyValuePair[0], keyValuePair[1]);
						}
					}
					try 
					{
						servlet.service(request, response);						
					} 
					catch (ServletException e)
					{
						e.printStackTrace();
					} 
					catch (IOException e) 
					{
						e.printStackTrace();
					}
					finally
					{
						response.getWriter().close();
					}
				}				
			}
		}	
	}

	private BaseResponse prepareBaseResponse(DataOutputStream outputStream) {
		return new BaseResponse(outputStream);
	}

	private BaseRequest prepareBaseRequest(String method) 
	{
		BaseRequest baseRequest = new BaseRequest(method);
		return baseRequest;
	}
	
	private Response prepareResponse(BaseResponse baseResponse) {
		// TODO Auto-generated method stub
		return new Response(baseResponse);
	}

	private Request prepareRequest(BaseRequest baseRequest) {
		// TODO Auto-generated method stub
		return new Request(baseRequest);
	}
}
