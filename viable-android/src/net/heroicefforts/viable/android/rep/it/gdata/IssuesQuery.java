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

import java.net.URL;

/**
 * Represents an issue query.
 * 
 * @author jevans
 *
 */
public class IssuesQuery
{
	private URL feedUrl;
	private String label;

	public IssuesQuery(URL feedUrl)
	{
		this.feedUrl = feedUrl;
	}

	public void setLabel(String label)
	{
		this.label = label;
	}

	public String toUrl()
	{
		StringBuilder url = new StringBuilder(feedUrl.toString());
		if(url.indexOf("?") == -1)
			url.append("?");
		else
			url.append("&");
		if(feedUrl != null && label != null)
		{
			url.append("label=").append(label).append("&");
		}
		url.append("alt=json");
		
		return url.toString();
	}

	
}
