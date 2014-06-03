package com.itdoors.haccp.ui.activities;

import android.os.Bundle;

import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.MenuItem;
import com.itdoors.haccp.R;

public class SettingsActivity extends SherlockPreferenceActivity{
	
	 	@SuppressWarnings("deprecation")
		@Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	        addPreferencesFromResource(R.xml.preferences);
	    }
	 	
	 	@Override
	 	public boolean onOptionsItemSelected(MenuItem item) {
	 		switch (item.getItemId()) {
	 			case android.R.id.home:
	 			finish();
	 			return true;
	 		}
	 		return super.onOptionsItemSelected(item);
	 	}	
}
