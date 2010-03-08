package net.heroicefforts.viable.crashy;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class CrashyActivity extends Activity 
{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);  
        
        ((Button) findViewById(R.id.CrashActivityButton)).setOnClickListener(buttonClicked);
        ((Button) findViewById(R.id.CrashServiceButton)).setOnClickListener(buttonClicked);
        ((Button) findViewById(R.id.CrashWorkerButton)).setOnClickListener(buttonClicked);
        ((Button) findViewById(R.id.CrashAsyncButton)).setOnClickListener(buttonClicked);
    } 
    
    private OnClickListener buttonClicked = new OnClickListener()
    {
		public void onClick(View v)
		{
			switch(v.getId())
			{
				case R.id.CrashActivityButton:
					startActivity(new Intent(CrashyActivity.this, CrashingActivity.class));
					break;
					
				case R.id.CrashServiceButton:
					startService(new Intent(CrashyActivity.this, CrashingService.class));
					break;
					
				case R.id.CrashWorkerButton:
					Thread t = new Thread() {
						public void run()
						{
							throw new RuntimeException("Simulated worker thread exception [" + System.currentTimeMillis() + "].");
						}
					};
					t.start();
					break;
					
				case R.id.CrashAsyncButton:
					new AsyncCrashing().execute();
					break;
			}
			
		}
    	
    };
    
    private class AsyncCrashing extends AsyncTask<Void, Void, Void>
    {
		@Override
		protected Void doInBackground(Void... params)
		{
			throw new RuntimeException("Simulated async task exception.");
		}
    	
    }
}