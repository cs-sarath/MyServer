package com.myserver.server;


import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSession;

import com.myserver.http.BaseRequest;
import com.myserver.http.BaseResponse;
import com.myserver.http.Request;
import com.myserver.http.Response;
import com.myserver.start.ServletWrapper;

public class SocketProcessor 
{
	private ServerContext serverContext;
	private Map<String, String> headersFromRequest = new HashMap<String, String>();
	
	public SocketProcessor(ServerContext serverContext)
	{
		this.serverContext = serverContext;
	}
	
	DataOutputStream outputToClient = null;
	
	public void processSocket(Socket clientSocket) throws Exception
	{		
		System.out.println("SocketProcessor process "+clientSocket.hashCode());
		BufferedReader inputFromClient  = new BufferedReader(new InputStreamReader (clientSocket.getInputStream()));
		outputToClient = new DataOutputStream(clientSocket.getOutputStream());
		
		String headerLine = inputFromClient.readLine();
		if(headerLine == null)
		{
			return;
		}
		while(inputFromClient.ready())
		{
			String readLine = inputFromClient.readLine();
			StringTokenizer headerTokenizer = new StringTokenizer(readLine, ": ");
			if(headerTokenizer.hasMoreTokens())
			{
				String headerKey = headerTokenizer.nextToken();
				if(headerTokenizer.hasMoreTokens())
				{
					String headerValue = headerTokenizer.nextToken();
					System.out.println(headerKey+ "="+ headerValue);
					headersFromRequest.put(headerKey, headerValue);
				}
			}
		}
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
				if(servletWrapper == null)
				{
					outputToClient.writeBytes("HTTP/1.1 404 OK\r\n");
					outputToClient.writeBytes("\r\n");
					outputToClient.close();
					return;
				}
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
	
	private Response prepareResponse(BaseResponse baseResponse) throws IOException {
		Response response = new Response(baseResponse);
//		response.getWriter().write("HTTP/1.1 200 OK");
//		response.addHeader("Connection", "keep-alive");
//		response.addHeader("Content-Length", "10000");
//		String cookieFromRequest = null;
//		String sessionId = "";
//		if((cookieFromRequest = headersFromRequest.get("Cookie")) == null)
//		{
//			HttpSession session = serverContext.createSession();
//			if(session != null)
//			{
//				sessionId = session.getId();
//			}
//		}
//		else
//		{
//			StringTokenizer stringTokenizer = new StringTokenizer(cookieFromRequest, "=");
//			stringTokenizer.nextToken();
//			if(stringTokenizer.hasMoreTokens())
//			{
//				sessionId = stringTokenizer.nextToken();
//			}
//		}
//		Cookie cookie = new Cookie("JSESSIONID", sessionId);
//		response.addCookie(cookie);
		return response;
	}

	private Request prepareRequest(BaseRequest baseRequest) {
		return new Request(baseRequest);
	}
}
