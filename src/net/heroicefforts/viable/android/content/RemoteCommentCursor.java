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
package net.heroicefforts.viable.android.content;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.heroicefforts.viable.android.Config;
import net.heroicefforts.viable.android.dao.Comment;
import net.heroicefforts.viable.android.dao.CommentSet;
import net.heroicefforts.viable.android.rep.Repository;
import net.heroicefforts.viable.android.rep.ServiceException;
import android.database.AbstractCursor;
import android.database.CursorIndexOutOfBoundsException;
import android.util.Log;

/**
 * A cursor that provides access to an issue's comments in a remote repository.
 * 
 * @author jevans
 *
 */
public class RemoteCommentCursor extends AbstractCursor
{
	private static final String TAG = "RemoteIssueCursor";

	private SimpleDateFormat fmt = new SimpleDateFormat();	
	
	private Repository repository;
	private List<Comment> commentList = new ArrayList<Comment>();
	private int idx = -1;
	private boolean more;
	private int page = 1;
	private int pageSize = 10;
	private boolean selfChange;
	private String issueId;
	
	
	/**
	 * Instantiate the cursor.
	 * 
	 * @param remoteRepository the remote repository.
	 * @param issueId the id of the issue owning the comments.
	 * 
	 * @throws ServiceException if there's an error contacting the repository.
	 */
	public RemoteCommentCursor(Repository remoteRepository, String issueId) 
		throws ServiceException
	{
		this.repository = remoteRepository;
		this.issueId = issueId;
		loadPage();
	}

	public boolean requery()
	{
		if(!selfChange)
		{			
			try
			{
				CommentSet results = repository.findCommentsForIssue(issueId, 1, commentList.size());
				commentList.clear();
				commentList.addAll(results.getComments());
				more = results.isMore();
				
				if(idx > commentList.size())
					idx = commentList.size() - 1;
				
				page = commentList.size() / pageSize + 1;
				
				return super.requery();
			}
			catch (ServiceException e)
			{
				Log.e(TAG, "Error requerying remote resource.", e);
				return false;
			}
		}
		else
		{
			selfChange = false;
			return super.requery();
		}
	}
	
	private void loadPage() 
		throws ServiceException
	{
		CommentSet results = repository.findCommentsForIssue(issueId, page++, pageSize);
		commentList.addAll(results.getComments());
		more = results.isMore();
	}

	@Override
	public String[] getColumnNames()
	{		
		return Comments.PROJECTION;
	}

	@Override
	public int getCount()
	{
		return commentList.size();
	}

	@Override
	public double getDouble(int column)
	{
		return (Double) getValue(column);
	}

	protected void checkPosition()
	{
		if(mPos > commentList.size() && !more)
			throw new CursorIndexOutOfBoundsException(mPos, commentList.size()); 
	}
	
	private Object getValue(int column)
	{
		switch(column)
		{
			case 0:
				return idx;
			case 1:
				return commentList.get(idx).getAuthor();
			case 2:
				return commentList.get(idx).getBody();
			case 3:
				return commentList.get(idx).getCreateDate();
			default:
				throw new CursorIndexOutOfBoundsException("No column index of:  " + column);
		}
	}
	
	@Override
	public float getFloat(int column)
	{
		return (Float) getValue(column);
	}

	@Override
	public int getInt(int column)
	{
		return (Integer) getValue(column);
	}

	@Override
	public long getLong(int column)
	{
		Number num = (Number) getValue(column);
		return num.longValue();
	}

	@Override
	public short getShort(int column)
	{
		return (Short) getValue(column);
	}

	@Override
	public String getString(int column)
	{
		Object value = getValue(column);
		if(value instanceof Date)
		{
			return fmt.format((Date) value);
		}
		else
			return (String) value;
	}

	@Override
	public boolean isNull(int column)
	{
		return getValue(column) == null;
	}

	public boolean onMove(int oldPosition, int newPosition)
	{
		if(newPosition >= commentList.size() - 1 && more)
		{
			while(newPosition >= commentList.size() - 1 && more)
			{
				try
				{
					loadPage();
				}
				catch (ServiceException e)
				{
					Log.e(TAG, "Error advancing remote cursor.", e);
					return false;
				}
			}
			selfChange = true;
			onChange(false);
		}
		if(newPosition < commentList.size())
		{
			idx = newPosition;
			return true;
		}
		else
		{
			if(Config.LOGD) 
				Log.d(TAG, "Can't move to position " + newPosition);
			return false;
		}
	}

}
