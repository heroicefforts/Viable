package net.heroicefforts.viable.android;

import java.util.ArrayList;
import java.util.List;

import android.database.AbstractCursor;
import android.database.CursorIndexOutOfBoundsException;
import android.util.Log;

public class RemoteIssueCursor extends AbstractCursor
{
	private static final String TAG = "RemoteIssueCursor";
    public static final String[] PROJECTION = new String[] {
        Issues._ID,
        Issues.APP_NAME,
        Issues.ISSUE_ID,
        Issues.ISSUE_TYPE,
        Issues.ISSUE_PRIORITY,
        Issues.ISSUE_STATE,
        Issues.SUMMARY
    };

	private Repository repository;
	private List<Issue> issueList = new ArrayList<Issue>();
	private int idx = -1;
	private boolean more;
	private SearchParams params;
	private boolean selfChange;
	
	
	public RemoteIssueCursor(Repository remoteRepository)
	{
		this.repository = remoteRepository;
	}

	public void setSearchParams(SearchParams params)
	{
		this.params = params;
		issueList.clear();
		idx = -1;
		loadPage();
	}
	
	public boolean requery()
	{
		if(!selfChange)
		{
			//refresh to index in one big gulp.
			int size = params.getPageSize();
			
			if(issueList.size() > 0)
				params.setPageSize(issueList.size());
			params.setPage(1);
			
			issueList.clear();
			SearchResults results = repository.search(params);
			issueList.addAll(results.getIssues());
			more = results.isMore();
			
			if(idx > issueList.size())
				idx = issueList.size() - 1;
			
			params.setPageSize(size);
			params.setPage(issueList.size() / size + 1);
			
			return super.requery();
		}
		else
		{
			selfChange = false;
			return super.requery();
		}
	}
	
	private void loadPage()
	{
		SearchResults results = repository.search(params);
		issueList.addAll(results.getIssues());
		more = results.isMore();
		params.setPage(params.getPage() + 1);
	}

	@Override
	public String[] getColumnNames()
	{		
		return PROJECTION;
	}

	@Override
	public int getCount()
	{
//		if(more)
//			return Integer.MAX_VALUE;
//		else
//		{
//			Log.d(TAG, "True count:  " + issueList.size());
			return issueList.size();
//		}
	}

	@Override
	public double getDouble(int column)
	{
		return (Double) getValue(column);
	}

	protected void checkPosition()
	{
		if(mPos > issueList.size() && !more)
			throw new CursorIndexOutOfBoundsException(mPos, issueList.size()); 
	}
	
	private Object getValue(int column)
	{
		switch(column)
		{
			case 0:
				return idx;
			case 1:
				return issueList.get(idx).getAppName();
			case 2:
				return issueList.get(idx).getIssueId();
			case 3:
				return issueList.get(idx).getType();
			case 4:
				return issueList.get(idx).getPriority();
			case 5:
				return issueList.get(idx).getState();
			case 6:
				return issueList.get(idx).getSummary();
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
		return (String) getValue(column);
	}

	@Override
	public boolean isNull(int column)
	{
		return getValue(column) == null;
	}

	public boolean onMove(int oldPosition, int newPosition)
	{
		if(newPosition >= issueList.size() - 1 && more)
		{
			while(newPosition >= issueList.size() - 1 && more)
				loadPage();
			selfChange = true;
			onChange(false);
		}
		if(newPosition < issueList.size())
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
