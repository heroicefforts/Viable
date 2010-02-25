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
package net.heroicefforts.viable.android.dao;

import java.util.ArrayList;
import java.util.List;

/**
 * This class holds search parameters.
 * 
 * @author jevans
 *
 */
public class SearchParams
{	
	private String hash;
	private List<String> ids = new ArrayList<String>();

	private String appName;
	private List<String> affectedVersions = new ArrayList<String>();
	
	private int page = 1;
	private int pageSize = 10;
	
	public String getHash()
	{
		return hash;
	}
	
	/**
	 * Search for an issue matching the specified checksum.
	 * 
	 * @param hash the checksum value.
	 */
	public void setHash(String hash)
	{
		this.hash = hash;
	}
	
	public List<String> getIds()
	{
		return ids;
	}
	
	/**
	 * Search for a specific list of issues.
	 * @param ids a non-null list of issue ids.
	 */
	public void setIds(List<String> ids)
	{
		this.ids = ids;
	}
	
	/**
	 * Search for issues matching these specific application versions.
	 * @param appName the application name
	 * @param affectedVersions the version names to match.
	 */
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
	
	/**
	 * Set the starting page of the search.
	 * @param page the page count, 1-based index.
	 */
	public void setPage(int page)
	{
		this.page = page;
	}
	
	public int getPageSize()
	{
		return pageSize;
	}
	
	/**
	 * Set the size of the page to be returned.
	 * @param pageSize
	 */
	public void setPageSize(int pageSize)
	{
		this.pageSize = pageSize;
	}
	
	
}
