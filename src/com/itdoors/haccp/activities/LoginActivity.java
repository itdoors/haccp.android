package com.itdoors.haccp.activities;

import com.itdoors.haccp.R;
import com.itdoors.haccp.fragments.LoginFragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;



public class LoginActivity extends BaseSherlockFragmentActivity{

	public static Intent newInstance(Activity activity) {
		Intent intent = new Intent(activity, LoginActivity.class);
		return intent;
	}
	
	@Override
	public void initFragment() {
		setContentFragment(new LoginFragment());
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		requestWindowFeature(com.actionbarsherlock.view.Window.FEATURE_INDETERMINATE_PROGRESS);
		
		super.onCreate(savedInstanceState);
		setSupportProgressBarIndeterminateVisibility(false);
		setTitle(getResources().getString(R.string.authorization));
	}
	
	
	
}