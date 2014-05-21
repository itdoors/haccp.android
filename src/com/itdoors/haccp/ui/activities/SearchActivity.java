package com.itdoors.haccp.ui.activities;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.itdoors.haccp.Intents;
import com.itdoors.haccp.R;
import com.itdoors.haccp.model.CompanyObject;
import com.itdoors.haccp.model.Contour;
import com.itdoors.haccp.model.Point;
import com.itdoors.haccp.ui.fragments.PointsSectionesListFragment;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

public class SearchActivity extends SherlockFragmentActivity implements PointsSectionesListFragment.OnPointPressedListener{
	
	private static final String FRAGMENT_TAG = "com.itdoors.haccp.activities.SearchActivity.FRAGMENT_TAG";
	
	private Fragment mFragment;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		requestWindowFeature(com.actionbarsherlock.view.Window.FEATURE_INDETERMINATE_PROGRESS);
		super.onCreate(savedInstanceState);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		setSupportProgressBarIndeterminateVisibility(false);
		setContentView(R.layout.activity_points);
		setTitle(R.string.points);
	}

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        handleIntent(getIntent()); 
    }

    @Override
    public void onNewIntent(Intent intent) {
    	setIntent(intent);
        handleIntent(getIntent()); 
    
    }
    
    private void handleIntent(Intent intent){
    	
    	if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            
	    	
    		String query = intent.getStringExtra(SearchManager.QUERY);
	    	Bundle extra = intent.getBundleExtra(SearchManager.APP_DATA);
	    	
	    	if( extra != null){
	    		
	    		CompanyObject companyObject = (CompanyObject)extra.getSerializable(Intents.CompanyObject.COMPANY_OBJECT);
				Contour contour = (Contour)extra.getSerializable(Intents.Contour.CONTOUR);
				
				mFragment = getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG );
				if(mFragment == null){
						mFragment = PointsSectionesListFragment.newInstance(companyObject, contour, query);
						getSupportFragmentManager()
							.beginTransaction()
							.add(R.id.cpoints_list_frame,mFragment, FRAGMENT_TAG)
							.commit();
				}
	    	}
		}
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
	public void onPointPressed(Point point) {
		onPointPressed(point.getId());
	}

	@Override
	public void onPointPressed(int pointId) {
		Intent intent = PointDetailsActivityV1.newInstance(this, pointId);
		startActivity(intent);	
	}
}