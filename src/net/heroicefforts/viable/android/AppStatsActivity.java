package net.heroicefforts.viable.android;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import net.heroicefforts.viable.android.dao.ProjectDetail;
import net.heroicefforts.viable.android.dao.VersionDetail;
import net.heroicefforts.viable.android.rep.CreateException;
import net.heroicefforts.viable.android.rep.RepositoryFactory;
import net.heroicefforts.viable.android.rep.ServiceException;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

/**
 * Activity for displaying the summary info and vital issue tracking statistics of a project.
 * 
 * @author jevans
 *
 */
public class AppStatsActivity extends Activity
{
	private static final String TAG = "AppStatsActivity";
	
	private RepositoryFactory factory;

	private ProgressBar progressBar;
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.app_stats);

        progressBar = (ProgressBar) findViewById(R.id.ProgressBar);
        factory = new RepositoryFactory(this);
        
    	Spinner appNameSpinner = (Spinner) findViewById(R.id.AppNameSpinner);
		
		List<String> appNames = new ArrayList<String>(factory.getApplicationNames());
    	appNameSpinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, appNames.toArray(new String[appNames.size()])));
		appNameSpinner.setOnItemSelectedListener(appChosen);    	
        
    }
    
    /**
     * The event handler for the application spinner.  It reloads the display information when a new application is selected.
     */
    private OnItemSelectedListener appChosen = new OnItemSelectedListener()
    {
		public void onItemSelected(AdapterView<?> l, View v, int position, long id)
		{
			String appName = (String) l.getItemAtPosition(position);
			new LoadAppStatsTask().execute(appName);
		}

		public void onNothingSelected(AdapterView<?> arg0)
		{
			//empty
		}
    	
    };
    
    /**
     * Asynchronous task for asynchronously requesting application information and later displaying it.
     * 
     * @author jevans
     *
     */
    private class LoadAppStatsTask extends AsyncTask<String, Void, ProjectDetail> 
    {
    	private String appName;

    	
    	public void onPreExecute()
    	{
    		progressBar.setVisibility(View.VISIBLE);
    	}
    	
    	protected ProjectDetail doInBackground(String...appNames)
    	{
    		this.appName = appNames[0];
    		try {
    			return factory.getRepository(appName).getApplicationStats();
    		}
			catch (CreateException e)
			{
				Error.handle(AppStatsActivity.this, e);
				return null;
			}
			catch (ServiceException e)
			{
				Error.handle(AppStatsActivity.this, e);
				return null;
			}
    		
    	}
    	
    	protected void onPostExecute(final ProjectDetail pd)
    	{
			showProjectDetails(pd, appName);
			progressBar.setVisibility(View.GONE);			
		}
		
	}

    /**
     * Set the project details to the view.
     * 
     * @param pd the project details
     * @param appName the application name of the project supplied
     */
	private void showProjectDetails(final ProjectDetail pd, String appName)
	{
		if(pd == null)
		{
			Log.e(TAG, "Error retrieving app stats for '" + appName + "'.");
			return;
		}
		
		if(!TextUtils.isEmpty(pd.getDescription()))
			((TextView) findViewById(R.id.DescriptionTextView)).setText(pd.getDescription());
		else
			((TextView) findViewById(R.id.DescriptionTextView)).setText("");
		
		if(!TextUtils.isEmpty(pd.getLead()))
			((TextView) findViewById(R.id.LeadTextView)).setText(getString(R.string.maintainer) + pd.getLead());
		else
			((TextView) findViewById(R.id.LeadTextView)).setText("");
		
		if(!TextUtils.isEmpty(pd.getUrl()))
		{
			SpannableStringBuilder urlSpan = new SpannableStringBuilder();
			urlSpan.append(getString(R.string.website));
			urlSpan.setSpan(new URLSpan(pd.getUrl()), 0, urlSpan.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			((TextView) findViewById(R.id.URLTextView)).setText(urlSpan);
			((TextView) findViewById(R.id.URLTextView)).setOnClickListener(new OnClickListener() {

				public void onClick(View v)
				{
					Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(pd.getUrl()));		
					AppStatsActivity.this.startActivity(intent);
				}
				
			});
			Linkify.addLinks((TextView) findViewById(R.id.URLTextView), Linkify.ALL);
		}
		else
			((TextView) findViewById(R.id.URLTextView)).setText("");

		setIssueCounts(R.id.BugsUnresTextView, R.id.BugsResTextView, pd.getUnfixedBugs(), pd.getFixedBugs());
		((TextView) findViewById(R.id.ImprsUnresTextView)).setText(String.valueOf(pd.getUnfixedImprovements()));
		((TextView) findViewById(R.id.ImprsResTextView)).setText(String.valueOf(pd.getFixedImprovements()));
		((TextView) findViewById(R.id.FeaturesUnresTextView)).setText(String.valueOf(pd.getUnfixedFeatures()));
		((TextView) findViewById(R.id.FeaturesResTextView)).setText(String.valueOf(pd.getFixedFeatures()));
		
		TableLayout vTable = (TableLayout) findViewById(R.id.VersionTableLayout);
		
		if(vTable.getChildCount() > 1)
			vTable.removeViews(1, vTable.getChildCount() - 1);
		DateFormat fmt = SimpleDateFormat.getDateInstance();
		for(VersionDetail version : pd.getVersions())
		{
			TableRow tr = new TableRow(AppStatsActivity.this);
			
			TextView name = new TextView(AppStatsActivity.this);
			SpannableStringBuilder nameStr = new SpannableStringBuilder();
			nameStr.append(version.getName());
			if(factory.getApplicationVersion(appName).equals(version.getName()))
				nameStr.setSpan(new ForegroundColorSpan(Color.RED), 0, nameStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			name.setText(nameStr);			
			tr.addView(name);
			
			TextView date = new TextView(AppStatsActivity.this);
			//date.setLayoutParams(lp);
			if(version.getReleaseDate() != null)
				date.setText(fmt.format(version.getReleaseDate()));
			else
			{
				SpannableStringBuilder unreleased = new SpannableStringBuilder();
				unreleased.append(getString(R.string.unreleased));
				unreleased.setSpan(new ForegroundColorSpan(Color.BLUE), 0, unreleased.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);				
				date.setText(unreleased);
			}
			tr.addView(date);

			vTable.addView(tr);
			vTable.requestLayout();
		}		
	}
    
	/**
	 * Sets the vital bug counts for a row in the table view.
	 * 
	 * @param unresRes resource id for the unresolved text view 
	 * @param resRes resource id for the resolved text view
	 * @param unfixedCnt the unresolved issue count
	 * @param fixedCnt the fixed issue count
	 */
	private void setIssueCounts(int unresRes, int resRes, long unfixedCnt, long fixedCnt)
	{
		float pctFixed = ((float) fixedCnt) / unfixedCnt;

		SpannableStringBuilder unfixedSpan = new SpannableStringBuilder();
		unfixedSpan.append(String.valueOf(unfixedCnt));
		if(pctFixed < .25)
			unfixedSpan.setSpan(new ForegroundColorSpan(Color.RED), 0, unfixedSpan.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		else if(pctFixed < .5)
			unfixedSpan.setSpan(new ForegroundColorSpan(Color.YELLOW), 0, unfixedSpan.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);		
		else if(pctFixed < .75)
			unfixedSpan.setSpan(new ForegroundColorSpan(Color.WHITE), 0, unfixedSpan.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		
		((TextView) findViewById(unresRes)).setText(unfixedSpan);
		((TextView) findViewById(resRes)).setText(String.valueOf(fixedCnt));
	}
    
}
