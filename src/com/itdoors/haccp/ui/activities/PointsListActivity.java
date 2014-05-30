package com.itdoors.haccp.ui.activities;

import android.annotation.TargetApi;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.SearchView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.itdoors.haccp.Intents;
import com.itdoors.haccp.R;

import com.itdoors.haccp.model.CompanyObject;
import com.itdoors.haccp.model.Contour;
import com.itdoors.haccp.model.Point;
import com.itdoors.haccp.ui.fragments.PointsSectionesListFragment;
import com.itdoors.haccp.utils.ApiLevel;

public class PointsListActivity extends SherlockFragmentActivity implements PointsSectionesListFragment.OnPointPressedListener{
	
	private static final String FRAGMENT_TAG = "com.itdoors.haccp.activities.PointsListActivity.FRAGMENT_TAG";
	
	
	private Fragment mFragment;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		requestWindowFeature(com.actionbarsherlock.view.Window.FEATURE_INDETERMINATE_PROGRESS);
		super.onCreate(savedInstanceState);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		setSupportProgressBarIndeterminateVisibility(false);
		setContentView(R.layout.activity_points);
		setTitle(R.string.points);
		
		initFragment();
	
	}
	
	public static Intent newIntentInstance(Context context, CompanyObject companyobject, Contour contour){
		
		Intent intent = new Intent(context, PointsListActivity.class);
		intent.putExtra(Intents.CompanyObject.COMPANY_OBJECT, companyobject);
		intent.putExtra(Intents.Contour.CONTOUR, contour);
		return intent;
	}
	
	
	protected void initFragment(){
		
		if(getIntent().getExtras() != null ){
			
			mFragment = getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG );
			if(mFragment == null){
					
					CompanyObject companyObject = (CompanyObject)getIntent().getExtras().getSerializable(Intents.CompanyObject.COMPANY_OBJECT);
					Contour contour = (Contour)getIntent().getExtras().getSerializable(Intents.Contour.CONTOUR);
				
					mFragment = PointsSectionesListFragment.newInstance(companyObject, contour);
					getSupportFragmentManager()
						.beginTransaction()
						.add(R.id.cpoints_list_frame,mFragment, FRAGMENT_TAG)
						.commit();
			}
			
		}
	}
	
	@Override
	public void startActivity(Intent intent) {
		
		if(intent.getAction() != null && intent.getAction().equals(Intent.ACTION_SEARCH)){
			Bundle bundle = getExtraSearchInfo();
			if(bundle != null && ( ApiLevel.hasHoneycomb() || intent.getBundleExtra(SearchManager.APP_DATA) == null))
					intent.putExtra(SearchManager.APP_DATA, bundle);
		}
		super.startActivity(intent);
	}
	
	 @TargetApi(Build.VERSION_CODES.HONEYCOMB)
	 @Override
	 public boolean onCreateOptionsMenu(Menu menu) {
	        
	        MenuItem  searchItem = menu.add(Menu.NONE, 0 , Menu.NONE, getString(R.string.search_point_label));
	        searchItem.setIcon( R.drawable.ic_search);
	        
	        if (ApiLevel.hasHoneycomb()) {
	        	SearchView searchView = new SearchView(this);
	        	searchItem.setActionView(searchView);
	        	if (searchView != null) {
	                SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
	                searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
	                
	                //Applies white color on searchview text
	                int id = searchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
	                TextView textView = (TextView) searchView.findViewById(id);
	                textView.setTextColor(Color.WHITE);

	                searchView.setQueryRefinementEnabled(true);
	            }
	       }
	       
	        searchItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
	  	    return true;
	  }
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				finish();
				return true;
			case 0:
				
				if (!ApiLevel.hasHoneycomb()) {
					
					Bundle searchQueryinfo = getExtraSearchInfo();
					startSearch(null, false, searchQueryinfo, false);
	                
					return true;
	           }
		}
		
		return super.onOptionsItemSelected(item);
	
	}
	
	private Bundle getExtraSearchInfo(){
		
		if(getIntent() != null && getIntent().getExtras() != null){
			
			CompanyObject companyObject = (CompanyObject)getIntent().getExtras().getSerializable(Intents.CompanyObject.COMPANY_OBJECT);
			Contour contour = (Contour)getIntent().getExtras().getSerializable(Intents.Contour.CONTOUR);
			
			Bundle searchQueryinfo = new Bundle();
			searchQueryinfo.putSerializable(Intents.CompanyObject.COMPANY_OBJECT, companyObject);
			searchQueryinfo.putSerializable(Intents.Contour.CONTOUR, contour);
			
			return searchQueryinfo;
		}
		return null;
	}

	@Override
	public void onPointPressed(Point point) {
		onPointPressed(point.getId());
	}

	@Override
	public void onPointPressed(int pointId) {
		Intent intent = PointDetailsActivity.newIntent(this, pointId);
		startActivity(intent);	
	}

}
