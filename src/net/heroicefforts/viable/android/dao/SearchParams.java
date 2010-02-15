package net.heroicefforts.viable.android.dao;

import java.util.List;

public class SearchParams
{	
	private String hash;
	private List<String> ids;

	private String appName;
	private List<String> affectedVersions;
	
	private int page = 1;
	private int pageSize = 10;
	
	public String getHash()
	{
		return hash;
	}
	public void setHash(String hash)
	{
		this.hash = hash;
	}
	public List<String> getIds()
	{
		return ids;
	}
	public void setIds(List<String> ids)
	{
		this.ids = ids;
	}
	
	public void setProjectAffectedVersions(String appName, List<String> affectedVersions)
	{
		this.appName = appName;
		this.affectedVersions = affectedVersions;
	}
	
	public String getAppName()
	{
		return appName;
	}
	public List<String> getAffectedVersions()
	{
		return affectedVersions;
	}
	public int getPage()
	{
		return page;
	}
	public void setPage(int page)
	{
		this.page = page;
	}
	public int getPageSize()
	{
		return pageSize;
	}
	public void setPageSize(int pageSize)
	{
		this.pageSize = pageSize;
	}
	
	
}
