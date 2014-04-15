package com.itdoors.haccp.activities;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.itdoors.haccp.R;
import com.itdoors.haccp.fragments.PointsListFragment;
import com.itdoors.haccp.model.Point;

public class PointsListActivity extends SherlockFragmentActivity implements PointsListFragment.OnPointPressedListener{
	
	private static final String POINTS_LIST_FRAGMENT_TAG = "com.itdoors.haccp.activities.AddStatisticsActivity.POINTS_LIST_FRAGMENT_TAG";
	
	private static final String POINTS_LIST_FRAGMENT_COUNT_TAG = "com.itdoors.haccp.activities.AddStatisticsActivity.POINTS_LIST_FRAGMENT_COUNT_TAG";
	private static final String POINTS_LIST_FRAGMENT_DELTA_TAG = "com.itdoors.haccp.activities.AddStatisticsActivity.POINTS_LIST_FRAGMENT_DELTA_TAG";
	
	
	private Fragment mFragment;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		requestWindowFeature(com.actionbarsherlock.view.Window.FEATURE_INDETERMINATE_PROGRESS);
		super.onCreate(savedInstanceState);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		setSupportProgressBarIndeterminateVisibility(false);
		setContentView(R.layout.points_list_frame);
		setTitle(R.string.point_list);
		
		initFragment();
		if(savedInstanceState != null){
		
		}
	}
	
	public static Intent newInstance(Activity activity, int count, int delta) {
		
		Intent intent = new Intent(activity, PointsListActivity.class);
		intent.putExtra(POINTS_LIST_FRAGMENT_COUNT_TAG, count);
		intent.putExtra(POINTS_LIST_FRAGMENT_DELTA_TAG, delta);
		
		return intent;
	
	}
	
	protected void initFragment(){
		
		if(getIntent().getExtras() != null ){
			
			int count = getIntent().getExtras().getInt(POINTS_LIST_FRAGMENT_COUNT_TAG);
			int delta = getIntent().getExtras().getInt(POINTS_LIST_FRAGMENT_DELTA_TAG);
			
		
			mFragment = getSupportFragmentManager().findFragmentByTag(POINTS_LIST_FRAGMENT_TAG );
			if(mFragment == null){
					mFragment = PointsListFragment.newInstance(count, delta);
					getSupportFragmentManager()
						.beginTransaction()
						.add(R.id.cpoints_list_frame,mFragment, POINTS_LIST_FRAGMENT_TAG)
						.commit();
			}
			
		}
	}
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}
	
	protected void showProgress(){
		setSupportProgressBarIndeterminateVisibility(true);
	}
	protected void hideProgress(){
		setSupportProgressBarIndeterminateVisibility(false);
	}

	@Override
	public void onPointPressed(int pointId) {
		Intent intent = PointDetailsActivity.newInstance(this, pointId);
		startActivity(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				setResult(RESULT_CANCELED);
				finish();
				return true;
		}
		return super.onOptionsItemSelected(item);
	
	}
}