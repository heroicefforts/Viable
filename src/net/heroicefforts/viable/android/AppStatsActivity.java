package net.heroicefforts.viable.android;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import net.heroicefforts.viable.android.dao.ProjectDetail;
import net.heroicefforts.viable.android.dao.VersionDetail;
import net.heroicefforts.viable.android.rep.RepositoryFactory;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
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
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

public class AppStatsActivity extends Activity
{
	private static final String TAG = "AppStatsActivity";
	
	private RepositoryFactory factory;
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.app_stats);

        factory = new RepositoryFactory(this);
        
    	Spinner appNameSpinner = (Spinner) findViewById(R.id.AppNameSpinner);
		
		List<String> appNames = new ArrayList<String>(factory.getApplicationNames());
    	appNameSpinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, appNames.toArray(new String[appNames.size()])));
		appNameSpinner.setOnItemSelectedListener(appChosen);    	
        
    }
    
    private OnItemSelectedListener appChosen = new OnItemSelectedListener()
    {
		public void onItemSelected(AdapterView<?> l, View v, int position, long id)
		{
			String appName = (String) l.getItemAtPosition(position);
			loadStats(appName);
		}

		public void onNothingSelected(AdapterView<?> arg0)
		{
			//empty
		}
    	
    };
    
	private void loadStats(String appName)
	{
		final ProjectDetail pd = factory.getRepository(appName).getApplicationStats(appName);
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

		setCounts(R.id.BugsUnresTextView, R.id.BugsResTextView, pd.getUnfixedBugs(), pd.getFixedBugs());
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
			TableRow tr = new TableRow(this);
			
			TextView name = new TextView(this);
			SpannableStringBuilder nameStr = new SpannableStringBuilder();
			nameStr.append(version.getName());
			if(factory.getApplicationVersion(appName).equals(version.getName()))
				nameStr.setSpan(new ForegroundColorSpan(Color.RED), 0, nameStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			name.setText(nameStr);			
			tr.addView(name);
			
			TextView date = new TextView(this);
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

	private void setCounts(int unresRes, int resRes, long unfixedCnt, long fixedCnt)
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
