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
import com.itdoors.haccp.model.Point;
import com.itdoors.haccp.model.PointStatus;
import com.itdoors.haccp.rest.AsyncSQLiteOperations;
import com.itdoors.haccp.ui.fragments.AddStatisticsFragmentV1;
import com.itdoors.haccp.utils.ToastUtil;

public class AddStatisticsActivityV1 extends SherlockFragmentActivity implements AddStatisticsFragmentV1.OnAddPressedListener{
	
	private static final String STATUSES_TO_CHANGE_SAVE_TAG = "com.itdoors.haccp.activities.AddStatisticsActivity.STATUSES_TO_CHANGE_SAVE_TAG";
	private static final String ADD_STATICTICS_FRAGMENT_TAG = "com.itdoors.haccp.activities.AddStatisticsActivity.ADD_STATICTICS_FRAGMENT_TAG";
	
	private Fragment mFragment;
	private PointStatus statusToChange;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		requestWindowFeature(com.actionbarsherlock.view.Window.FEATURE_INDETERMINATE_PROGRESS);
		super.onCreate(savedInstanceState);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		setSupportProgressBarIndeterminateVisibility(false);
		setContentView(R.layout.activity_add_statictics);
		setTitle(R.string.add_statistic_record);
		
		initFragment();
		
		if(savedInstanceState != null){
			statusToChange = (PointStatus) savedInstanceState.getSerializable(STATUSES_TO_CHANGE_SAVE_TAG);
		}
	}
	
	protected void initFragment(){
		
		mFragment = getSupportFragmentManager().findFragmentByTag(ADD_STATICTICS_FRAGMENT_TAG );
		if(mFragment == null){
				mFragment = new AddStatisticsFragmentV1();
				getSupportFragmentManager()
					.beginTransaction()
					.add(R.id.add_statictics_frame,mFragment, ADD_STATICTICS_FRAGMENT_TAG)
					.commit();
		}
	}
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if(statusToChange != null)
			outState.putSerializable(STATUSES_TO_CHANGE_SAVE_TAG, statusToChange);
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
			break;
		}
		return super.onOptionsItemSelected(item);
	
	}
	
	protected void showProgress(){
		setSupportProgressBarIndeterminateVisibility(true);
	}
	protected void hideProgress(){
		setSupportProgressBarIndeterminateVisibility(false);
	}
	
	@Override
	public void onAddPressed(HashMap<GroupCharacteristic, Double> values) {
		
		int pointId = getIntent().getIntExtra(Intents.Point.UID, -1);
		if(pointId == -1)
			return;
		
		Iterator<Entry<GroupCharacteristic, Double>> iterator = values.entrySet().iterator();
		Entry<GroupCharacteristic, Double> entry = null;
		
		if(iterator.hasNext())		entry = iterator.next();
		if(entry == null) return;
		
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
