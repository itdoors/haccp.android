package com.itdoors.haccp.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.itdoors.haccp.Intents;
import com.itdoors.haccp.R;
import com.itdoors.haccp.model.CompanyObject;
import com.itdoors.haccp.model.Contour;
import com.itdoors.haccp.ui.fragments.ServicesAndContoursFragment;


public class ServicesAndContoursActivity extends SherlockFragmentActivity implements ServicesAndContoursFragment.OnContourPressedListener{
	
	private static final String FRAGMENT_TAG = "com.itdoors.haccp.activities.ServicesAndContoursActivity.FRAGMENT_TAG";
	
	private Fragment mFragment;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setSupportProgressBarIndeterminateVisibility(false);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		setContentView(R.layout.activity_services_and_contours);
		setTitle(R.string.contours);
		
		initFragment();
		
	}
	
	public static Intent newIntentInstance(Context context, CompanyObject companyobject){
		
		Intent intent = new Intent(context, ServicesAndContoursActivity.class);
		intent.putExtra(Intents.CompanyObject.COMPANY_OBJECT, companyobject);
		return intent;
	}
	
	
	protected void initFragment(){
		
		if(getIntent().getExtras() != null ){
			
			mFragment = getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG );
			if(mFragment == null){
					mFragment = new ServicesAndContoursFragment();
					getSupportFragmentManager()
						.beginTransaction()
						.add(R.id.serv_and_cont_list_frame,mFragment, FRAGMENT_TAG)
						.commit();
			}
			
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    return true;
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

	@Override
	public void onContourPressed(Contour contour) {
		
		if(getIntent().getExtras() != null){
		
			CompanyObject companyobject = (CompanyObject)getIntent().getExtras().get(Intents.CompanyObject.COMPANY_OBJECT);
			Intent intent = PointsListActivity.newIntentInstance(this, companyobject, contour);
			startActivity(intent);
		
		}
		
	}

}