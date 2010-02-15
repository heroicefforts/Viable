package net.heroicefforts.viable.android.dao;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class Comment
{
	private static final String TAG = "Comment";
	
	private Long id;
	private String body;
	private String author;
	private Date createDate;

	
	public Comment(JSONObject jsonObject)
		throws JSONException
	{
		this.id = jsonObject.getLong("id");
		this.body = jsonObject.getString("body").replaceAll("\r\n", "\n");
		if(jsonObject.has("author"))
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


	public Long getId()
	{
		return id;
	}


	public String getBody()
	{
		return body;
	}


	public String getAuthor()
	{
		return author;
	}


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

}
