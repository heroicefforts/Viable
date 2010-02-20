package net.heroicefforts.viable.android.content;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.heroicefforts.viable.android.dao.Comment;
import net.heroicefforts.viable.android.dao.CommentSet;
import net.heroicefforts.viable.android.rep.Repository;
import net.heroicefforts.viable.android.rep.ServiceException;

import android.database.AbstractCursor;
import android.database.CursorIndexOutOfBoundsException;
import android.util.Log;

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
			Log.d(TAG, "Can't move to position " + newPosition);
			return false;
		}
	}

}
