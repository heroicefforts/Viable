package net.heroicefforts.viable.android;

import java.text.DateFormat;
import java.util.List;

import net.heroicefforts.viable.android.content.Comments;
import net.heroicefforts.viable.android.content.IssueContentAdapter;
import net.heroicefforts.viable.android.content.Issues;
import net.heroicefforts.viable.android.content.RemoteCommentCursor;
import net.heroicefforts.viable.android.dao.Issue;
import net.heroicefforts.viable.android.rep.RepositoryFactory;
import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class IssueViewActivity extends Activity
{
	private static final String TAG = "IssueViewActivity";
	public static final String EXTRA_APP_NAME = Issues.APP_NAME;
	public static String EXTRA_ISSUE_ID = Issues.ISSUE_ID;
	
	private String appName;
	private RepositoryFactory factory;
	
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.issue_view);
        
		factory = new RepositoryFactory(this);        
        
        if(getIntent().getData() != null)
        {
        	Cursor cursor = managedQuery(getIntent().getData(), Issues.ISSUE_PROJECTION, null, null, Issues.DEFAULT_SORT_ORDER);
            if(cursor.moveToFirst())
            {
            	Issue issue = new IssueContentAdapter(cursor).toIssue();
            	appName = issue.getAppName();
            	presentIssue(issue);
            }
            else
            {
            	Log.e(TAG, "Invalid intent data.  Closing activity.");
            	finish();
            }
        }
        else if(getIntent().getStringExtra(EXTRA_ISSUE_ID) != null)
        {
        	String issueId = getIntent().getStringExtra(EXTRA_ISSUE_ID);
        	appName = getIntent().getStringExtra(EXTRA_APP_NAME);
        	Issue issue = factory.getRepository(appName).findById(issueId);
        	if(issue != null)
        		presentIssue(issue);
        	else
        	{
        		Log.e(TAG, "Issue '" + issueId + "' does not exist.  Closing activity.");
        		finish();
        	}
        }
        else
        {
        	Log.e(TAG, "No intent data.  Closing activity.");
        	finish();        	
        }
        
        ImageButton refreshButton = (ImageButton) findViewById(R.id.RefreshButton);
        refreshButton.setOnClickListener(refreshClicked);
        refreshButton.setImageResource(android.R.drawable.ic_popup_sync);
    }

    private OnClickListener refreshClicked = new OnClickListener()
    {
		public void onClick(View v)
		{
			String issueId = ((TextView) findViewById(R.id.IssueIdTextView)).getText().toString();
			Issue issue = factory.getRepository(appName).findById(issueId);
			if(issue != null)
			{
				presentIssue(issue);			
				getContentResolver().update(Issues.CONTENT_URI, new IssueContentAdapter(issue).toContentValues(), Issues.ISSUE_ID + " = ?", new String[] { issueId });
			}
		}

    	
    };
    
    private void presentIssue(Issue issue)
	{
		IssueState issueState = IssueState.getState(issue);
		TextView issueView = (TextView) findViewById(R.id.IssueIdTextView);
		issueView.setText(issue.getIssueId());
		issueView.setTextColor(Color.WHITE);
		((ImageView) findViewById(R.id.TypeImageView)).setImageResource(issueState.getIconRes());
		TextView typeView = (TextView) findViewById(R.id.TypeTextView);
		String type = issue.getType();
		type = Character.toUpperCase(type.charAt(0)) + type.substring(1);
		typeView.setText(type);
		typeView.setTextColor(IssueState.getTypeColor(issue));
		TextView priorityView = (TextView) findViewById(R.id.PriorityTextView);
		String priority = issue.getPriority();
		priority = Character.toUpperCase(priority.charAt(0)) + priority.substring(1);
		priorityView.setText(priority);
		priorityView.setTextColor(IssueState.getPriorityColor(issue));
		TextView stateView = (TextView) findViewById(R.id.StateTextView);
		String state = issue.getState();
		state = Character.toUpperCase(state.charAt(0)) + state.substring(1);
		stateView.setText(state);
		stateView.setTextColor(IssueState.getStateColor(issue));
		
		SpannableStringBuilder affectedVersions = new SpannableStringBuilder();
		affectedVersions.append(getString(R.string.affected_versions_label));
		String currVersion = getCurrVersion(issue);
		Log.d(TAG, "Current version:  " + currVersion);
		for(String version : issue.getAffectedVersions())
		{
			if(version.equals(currVersion))
			{
				int idxStart = affectedVersions.length();
				affectedVersions.append(version);
				affectedVersions.setSpan(new ForegroundColorSpan(Color.RED), idxStart, affectedVersions.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				affectedVersions.append(", ");
			}
			else
				affectedVersions.append(version).append(", ");
		}
		affectedVersions.delete(affectedVersions.length() - 2, affectedVersions.length() - 1);
		
		((TextView) findViewById(R.id.AffectedVersionsTextView)).setText(affectedVersions);

		DateFormat fmt = DateFormat.getDateInstance();
		((TextView) findViewById(R.id.CreatedDateTextView)).setText(getString(R.string.created_date_label) + fmt.format(issue.getCreateDate()));
		((TextView) findViewById(R.id.ModifiedDateTextView)).setText(getString(R.string.modified_date_label) + fmt.format(issue.getModifiedDate()));
		((TextView) findViewById(R.id.SummaryTextView)).setText(issue.getSummary());
		((TextView) findViewById(R.id.DescriptonTextView)).setText(issue.getDescription());
		
		RemoteCommentCursor cursor = new RemoteCommentCursor(factory.getRepository(issue.getAppName()), issue.getIssueId());
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.comments_list_item, cursor,
		        new String[] { Comments.AUTHOR, Comments.BODY, Comments.CREATED_DATE }, 
		        new int[] { R.id.AuthorTextView, R.id.BodyTextView, R.id.CreateDateTextView });
		//adapter.setViewBinder(new IssuesListViewBinder(this));
		((ListView) findViewById(R.id.CommentsListView)).setAdapter(adapter);
		
		
	}

	private String getCurrVersion(Issue issue)
	{
		PackageManager pkgMgr = getPackageManager();
        List<ApplicationInfo> apps = pkgMgr.getInstalledApplications(PackageManager.GET_META_DATA);
        for(ApplicationInfo appInfo : apps)
        {
        	try
			{
				if(pkgMgr.getApplicationLabel(appInfo).equals(issue.getAppName()))
					return pkgMgr.getPackageInfo(appInfo.packageName, PackageManager.GET_META_DATA).versionName;
			}
			catch (NameNotFoundException e)
			{
				return null;
			}
        }
        
        return null;
	}    

}
