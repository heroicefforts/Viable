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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;


/**
 * Data class representing comment information.
 * 
 * @author jevans
 *
 */
public class Comment
{
	private static final String TAG = "Comment";

	private Long id;
	private String body;
	private String author;
	private Date createDate;

	public Comment(String body)
	{
		this(-1L, body, null, new Date());
	}

	public Comment(Long id, String body, String author, Date createDate)
	{
		super();
		this.id = id;
		this.body = body;
		this.author = author;
		this.createDate = createDate;
	}

	/**
	 * Instantiate the comment state based upon the JIRA JSON format.
	 * 
	 * @param obj JIRA JSON comment object 
	 * @throws JSONException if there's an error parsing the JSON.
	 */
	public Comment(JSONObject jsonObject) throws JSONException
	{
		this.id = jsonObject.getLong("id");
		this.body = jsonObject.getString("body").replaceAll("\r\n", "\n");
		if (jsonObject.has("author"))
			this.author = jsonObject.getString("author");

		SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		fmt.setLenient(true);
		try
		{
			createDate = fmt.parse(jsonObject.getString("createDate"));
		}
		catch (ParseException e)
		{
			Log.e(TAG, "Error parsing JSON dates.", e);
			throw new JSONException("Error parsing JSON dates.");
		}

	}

	/**
	 * Return the unique identifier of the comment.
	 * @return
	 */
	public Long getId()
	{
		return id;
	}

	/**
	 * Return the content of the comment.
	 * @return
	 */
	public String getBody()
	{
		return body;
	}

	/**
	 * Return the author of the comment.
	 * @return
	 */
	public String getAuthor()
	{
		return author;
	}

	/**
	 * Return the date that the comment was created.
	 * @return
	 */
	public Date getCreateDate()
	{
		return createDate;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Comment other = (Comment) obj;
		if (id == null)
		{
			if (other.id != null)
				return false;
		}
		else if (!id.equals(other.id))
			return false;
		return true;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	public void setCreateDate(Date createDate)
	{
		this.createDate = createDate;
	}

	public void setAuthor(String author)
	{
		this.author = author;
	}

	/**
	 * Copy the state of the supplied comment
	 * @param newComment the source comment
	 */
	public void copy(Comment newComment)
	{
		this.id = newComment.id;
		this.body = newComment.body;
		this.author = newComment.author;
		this.createDate = newComment.createDate;
	}

	public void setBody(String body)
	{
		this.body = body;
	}

}
