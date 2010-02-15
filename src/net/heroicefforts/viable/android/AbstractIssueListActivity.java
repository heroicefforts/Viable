package net.heroicefforts.viable.android;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;

public abstract class AbstractIssueListActivity extends Activity
{
	protected ListView listView;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

        setContentView(R.layout.issue_list);		
		
		Spinner appNameSpinner = (Spinner) findViewById(R.id.AppNameSpinner);
		List<String> appNames = getAppNames();
    	appNameSpinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, appNames));
		appNameSpinner.setOnItemSelectedListener(appChosen);    	
    	
		listView = (ListView) findViewById(R.id.IssueListView);
        listView.setOnCreateContextMenuListener(this);
        listView.setOnItemClickListener(getIssueListClickListener());
	}

	protected abstract List<String> getAppNames();
	protected abstract OnItemClickListener getIssueListClickListener();
	protected abstract SimpleCursorAdapter getIssueCursorAdapter(int position, String appName);

    private OnItemSelectedListener appChosen = new OnItemSelectedListener()
    {
		public void onItemSelected(AdapterView<?> l, View v, int position, long id)
		{
			String appName = (String) l.getItemAtPosition(position);
			SimpleCursorAdapter adapter = getIssueCursorAdapter(position, appName);
			adapter.setViewBinder(new IssuesListViewBinder(AbstractIssueListActivity.this));
			listView.setAdapter(adapter);
		}

		public void onNothingSelected(AdapterView<?> arg0)
		{
			//empty
		}
    	
    };
	
}
