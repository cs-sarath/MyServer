package com.myserver.start;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletContext;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.myserver.server.WebApplicationContext;

class WebXMLParser extends DefaultHandler {

	private String path;

	private WebApplicationContext servletContext;

	private ServletWrapper servletWrapper;
	private boolean isServlet = false;
	private boolean isInitParam = false;
	private boolean isParamName = false;
	private boolean isParamValue = false;
	private boolean isServletName = false;
	private boolean isServletMapping = false;
	private boolean isServletClass = false;
	private boolean isUrlPattern = false;
	private boolean isDisplayName = false;
	
	private String paramName;

	WebXMLParser(File webXML) 
	{
		this.path = webXML.toString();
		this.servletContext = new WebApplicationContext();
	}

	WebXMLParser(String webAppName, String path)
	{
		this.path = path;
		this.servletContext = new WebApplicationContext(webAppName);
	}

	WebApplicationContext parse() throws ParserConfigurationException, SAXException, IOException
	{
		SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();
		saxParser.parse(new File(path), this);
		return servletContext;
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes)
	{
		if (qName.equalsIgnoreCase("servlet")) {
			isServlet = true;
		}
		else if (qName.equalsIgnoreCase("servlet-name")) {
			isServletName = true;
		}
		else if (qName.equalsIgnoreCase("servlet-class")) {
			isServletClass = true;
		}
		else if (qName.equalsIgnoreCase("init-param")) {
			isInitParam = true;
		}
		else if (qName.equalsIgnoreCase("param-name")) {
			isParamName = true;
		}
		else if (qName.equalsIgnoreCase("param-value")) {
			isParamValue = true;
		}
		else if (qName.equalsIgnoreCase("servlet-mapping")) {
			isServletMapping = true;
		}
		else if (qName.equalsIgnoreCase("url-pattern")) {
			isUrlPattern = true;
		}
		else if (qName.equalsIgnoreCase("display-name")) {
			isDisplayName = true;
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException 
	{
		if (qName.equalsIgnoreCase("servlet-name")) {
			servletContext.addServlet(servletWrapper.getServletName(), servletWrapper.getServletClass());
		}
	}

	@Override
	public void characters(char ch[], 
			int start, int length) throws SAXException 
	{
		if (isServlet && isServletName) 
		{
			this.servletWrapper = new ServletWrapper(new String(ch, start, length));
			isServlet = false;
			isServletName = false;
		}
		else if (isServletClass) 
		{
			servletWrapper.setServletClass(new String(ch, start, length));
			isServletClass = false;
		}
		else if (isInitParam && isParamName)
		{
			paramName = new String(ch, start, length);
			isParamName = false;
		}
		else if (isInitParam && isParamValue)
		{
			servletWrapper.setInitParameter(paramName, new String(ch, start, length));
			isInitParam = false;
			isParamValue = false;
		}
		else if (isServletMapping && isServletName)
		{
			isServletMapping = false;
		}
		else if (isServletMapping && isServletName)
		{
			isServletMapping = false;
		} 
		else if (isUrlPattern) 
		{
			isUrlPattern = false;
			servletContext.addServletUrlPattern(new String(ch, start, length), servletWrapper);
		}
		else if (isDisplayName) 
		{
			isDisplayName = false;
			servletContext.setWebAppName(new String(ch, start, length));
		}
	}
}
