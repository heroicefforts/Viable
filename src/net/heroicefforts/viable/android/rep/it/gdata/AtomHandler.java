package net.heroicefforts.viable.android.rep.it.gdata;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Stack;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class AtomHandler extends DefaultHandler
{

	protected static final String EOL = System.getProperty("line.separator");
	protected Stack<String> tags = new Stack<String>();
	protected StringBuilder value = new StringBuilder();
	protected SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException
	{
		value.append(ch, start, length);
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException
	{
		value.setLength(0);
		tags.push(localName);
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException
	{
		tags.pop();
	}
	
	protected final Date parseDate(StringBuilder dateValue)
	{
		try
		{
			return sdf.parse(value.toString());
		}
		catch (ParseException e)
		{
			return null;
		}
	}

}
