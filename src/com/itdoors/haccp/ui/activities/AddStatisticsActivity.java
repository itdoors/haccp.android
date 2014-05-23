package com.itdoors.haccp.ui.activities;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.itdoors.haccp.Intents;
import com.itdoors.haccp.R;
import com.itdoors.haccp.model.GroupCharacteristic;
import com.itdoors.haccp.model.PointStatus;
import com.itdoors.haccp.rest.AsyncSQLiteOperations;
import com.itdoors.haccp.ui.fragments.AddStatisticsFragment;
import com.itdoors.haccp.ui.fragments.AddStatisticsFragment.Action;
import com.itdoors.haccp.utils.ToastUtil;

public class AddStatisticsActivity extends SherlockFragmentActivity implements AddStatisticsFragment.OnAddPressedListener{
	
	private static final String ADD_STATICTICS_FRAGMENT_TAG = "com.itdoors.haccp.activities.AddStatisticsActivity.ADD_STATICTICS_FRAGMENT_TAG";
	
	private Fragment mFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		requestWindowFeature(com.actionbarsherlock.view.Window.FEATURE_INDETERMINATE_PROGRESS);
		super.onCreate(savedInstanceState);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		setSupportProgressBarIndeterminateVisibility(false);
		setContentView(R.layout.activity_add_statictics);
		setTitle(R.string.add_statistic_record);
		
		mFragment = getSupportFragmentManager().findFragmentByTag(ADD_STATICTICS_FRAGMENT_TAG );
		if(mFragment == null){
				mFragment = new AddStatisticsFragment();
				getSupportFragmentManager()
					.beginTransaction()
					.add(R.id.add_statictics_frame,mFragment, ADD_STATICTICS_FRAGMENT_TAG)
					.commit();
		}
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    menu.add(Menu.NONE, 0 , Menu.NONE, getString(R.string.done))
	        .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				setResult(RESULT_CANCELED);
				finish();
				return true;
			case 0 :
				onDonePressed();
				return true;
			
		}
		return super.onOptionsItemSelected(item);
	
	}

	public void onDonePressed(){
		
		if(mFragment != null && mFragment.isAdded()){
			AddStatisticsFragment fragment = (AddStatisticsFragment)mFragment;
			Action action = fragment.getActionType();
			switch (action) {
				case CHANGE_STATUS:
					changeStatusPressed(fragment.getStatus());
					break;
				case ADD_STATISTICS:
					onAddPressed(fragment.getValues());
					break;
			}
			
		}
	}
	
	@Override
	public void onAddPressed(HashMap<GroupCharacteristic, Double> values) {
		
		int pointId = getIntent().getIntExtra(Intents.Point.UID, -1);
		if(pointId == -1)
			return;
		
		Iterator<Entry<GroupCharacteristic, Double>> iterator = values.entrySet().iterator();
		Entry<GroupCharacteristic, Double> entry = null;
		
		if(iterator.hasNext())
			entry = iterator.next();
		if(entry == null) 
			return;
		
		
		GroupCharacteristic characteristic = entry.getKey();
		Double value = entry.getValue();
		String date = Long.toString(Calendar.getInstance().getTime().getTime() / 1000);;
		
		AsyncSQLiteOperations.startInsertStatistics(getContentResolver(), pointId, characteristic.getId(), date, date, Integer.toString(value.intValue()));
		ToastUtil.ToastLong(this, getString(R.string.data_will_be_entered_on_the_server));
		finish();
	}

	@Override
	public void changeStatusPressed(PointStatus status) {
		
		int pointId = getIntent().getIntExtra(Intents.Point.UID, -1);
		if(pointId == -1)
			return;
		
		AsyncSQLiteOperations.startUpdatePointStatus(getContentResolver(), pointId, status.getId());
		ToastUtil.ToastLong(this, getString(R.string.data_will_be_entered_on_the_server));
		finish();
	}
}
