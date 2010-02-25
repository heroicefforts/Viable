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

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import net.heroicefforts.viable.android.dao.Comment;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * This class extends comment to add handling of the Gdata Atom protocol.
 * 
 * @author jevans
 *
 */
public class AtomComment extends Comment
{

	public AtomComment(Comment issue)
	{
		super("");
		copy(issue);
	}

	public AtomComment(String atomEntry)
		throws SAXException, IOException, ParserConfigurationException
	{
		super("");
		parse(atomEntry);		
	}

	protected void parse(String atomEntry)
		throws SAXException, IOException, ParserConfigurationException
	{
		System.out.println("Parsing:  " + atomEntry);
		SAXParserFactory f = SAXParserFactory.newInstance();
		SAXParser p = f.newSAXParser();
		p.parse(new InputSource(new StringReader(atomEntry)), new CommentContentHandler(this));
	}
	
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("<?xml version='1.0' encoding='UTF-8'?>");
		builder.append("<entry xmlns='http://www.w3.org/2005/Atom' xmlns:issues='http://schemas.google.com/projecthosting/issues/2009'>");
		builder.append("<content type='html'>").append(getBody()).append("</content>");
		builder.append("<author><name>").append(getAuthor()).append("</name></author>");
		builder.append("</entry>");
		return builder.toString();
	}
	
	private static class CommentContentHandler extends AtomHandler
	{
		private Comment comment;
		private String title;
		private String content;
		
		public CommentContentHandler(Comment comment)
		{
			this.comment = comment;
		}
		
		@Override
		public void endElement(String uri, String localName, String qName) throws SAXException
		{
			super.endElement(uri, localName, qName);
			
			if("id".equals(localName))
			{
				comment.setId(Long.parseLong(value.substring(value.lastIndexOf("/") + 1)));
			}
			else if("title".equals(localName))
			{
				title = value.toString();
			}
			else if("content".equals(localName))
			{
				content = value.toString().replaceAll("<p>", "").replaceAll("</p>", EOL);
			}
			else if("published".equals(localName))
			{
				comment.setCreateDate(parseDate(value));
			}
		}
	
		@Override
		public void endDocument() throws SAXException
		{
			comment.setBody(title + EOL + EOL + content);
		}	
	}
	
}
