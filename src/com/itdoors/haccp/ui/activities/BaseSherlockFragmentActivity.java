package com.itdoors.haccp.ui.activities;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.itdoors.haccp.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;


public abstract class BaseSherlockFragmentActivity extends SherlockFragmentActivity{
	private Fragment mContentFragment;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(com.actionbarsherlock.view.Window.FEATURE_INDETERMINATE_PROGRESS);
		super.onCreate(savedInstanceState);
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.activity_main);
		setSupportProgressBarIndeterminateVisibility(false);
		
		// set the Above View Fragment
		if (savedInstanceState != null){
				mContentFragment = getSupportFragmentManager().getFragment(savedInstanceState, "mContent");
		}
				
		if (mContentFragment == null){
			//mContentFragment = new ProductsFragment();
			initFragment();
			getSupportFragmentManager()
			.beginTransaction()
			.add(R.id.main_content_frame, mContentFragment)
			.commit();

		}
		
	}
	
	public abstract void initFragment();
	
	
	
	public void addNewItemFragmentToStack(Fragment newFragment){
		
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
	    ft.replace(R.id.main_content_frame, newFragment);
	    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
	    ft.addToBackStack(null);
	    ft.commit();
	}

	public Fragment getContentFragment() {
		return mContentFragment;
	}
	protected void setContentFragment(Fragment fragment){
		mContentFragment = fragment;
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		getSupportFragmentManager().putFragment(outState, "mContent", mContentFragment);
	}
	
}
