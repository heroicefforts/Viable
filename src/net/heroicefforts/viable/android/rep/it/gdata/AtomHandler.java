/*
 *  Copyright 2010 Heroic Efforts, LLC
 *  
 *  This file is part of Viable.
 *
 *  Viable is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Viable is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Viable.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.heroicefforts.viable.android.rep.it.gdata;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Stack;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * This is a common handler implementation for the comment and issue Gdata Atom parser handlers.
 * 
 * @author jevans
 *
 */
public abstract class AtomHandler extends DefaultHandler
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
