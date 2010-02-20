package net.heroicefforts.viable.android.rep.it.gdata;

import java.net.URL;

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
