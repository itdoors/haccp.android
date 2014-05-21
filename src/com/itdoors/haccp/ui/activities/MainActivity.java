package com.itdoors.haccp.ui.activities;

import org.json.JSONException;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.WindowManager;

import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.MenuItem;
import com.google.zxing.client.android.CaptureActivity;
import com.itdoors.haccp.R;

import com.itdoors.haccp.model.Company;
import com.itdoors.haccp.model.CompanyObject;
import com.itdoors.haccp.parser.ScanResultParser;
import com.itdoors.haccp.provider.HaccpContract;
import com.itdoors.haccp.sync.SyncUtils;
import com.itdoors.haccp.ui.fragments.CompanyObjectsFragment;
import com.itdoors.haccp.ui.fragments.LoginFragment;
import com.itdoors.haccp.ui.fragments.MenuFragment;
import com.itdoors.haccp.ui.fragments.QRFragment;
import com.itdoors.haccp.ui.interfaces.SetQRCallback;
import com.itdoors.haccp.ui.interfaces.TakeQRCallback;
import com.itdoors.haccp.ui.interfaces.TakeQRListener;
import com.itdoors.haccp.utils.Camera;
import com.itdoors.haccp.utils.Logger;
import com.itdoors.haccp.utils.ToastUtil;
import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.app.SlidingFragmentActivity;

public class MainActivity extends SlidingFragmentActivity implements	SetQRCallback, 
								TakeQRListener, MenuFragment.OnProfilePressedListener, 
								MenuFragment.OnPointsPressedListener, CompanyObjectsFragment.OnCompanyObjectItemPressedListener {

	public static final int GET_RECOGNIZED_TEXT_REQUEST_CODE = 111;

	public enum MenuActionMode {
		USER_PROFILE, SCANNER, POINTS, ABOUT;
	}

	private static MenuActionMode mActionMode;
	public static int THEME = R.style.Theme_Sherlock;

	private Fragment mContent;
	private Fragment mMenu;

	@SuppressWarnings("unused")
	private ActionMode mMode;
	private TakeQRCallback mTakeCodeFromCameraCallback;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		Logger.Logi(getClass(), "getIntent()" + ((getIntent() == null) ? "null" : getIntent().toString()));
		if(!SyncUtils.cheakSync(this, getIntent()))
			finish();
		 
		requestWindowFeature(com.actionbarsherlock.view.Window.FEATURE_INDETERMINATE_PROGRESS);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		super.onCreate(savedInstanceState);

		
		
		setTitle(R.string.app_name_haccp);
		setContentView(R.layout.activity_main);
		setSupportProgressBarIndeterminateVisibility(false);
		setSlidingActionBarEnabled(false);

		// check if the content frame contains the menu frame
		if (findViewById(R.id.menu_frame) == null) {
			// ///
			setBehindContentView(R.layout.menu_frame);
			getSlidingMenu().setSlidingEnabled(true);
			getSlidingMenu()
					.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);

		} else {
			// add a dummy view
			View v = new View(this);
			setBehindContentView(v);
			getSlidingMenu().setSlidingEnabled(false);
			getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
		}

		// set the Above View Fragment
		if (savedInstanceState != null) {
			mContent = getSupportFragmentManager().getFragment(
					savedInstanceState, "mContent");
			mMenu = getSupportFragmentManager().getFragment(savedInstanceState,
					"mMenu");
			mActionMode = (MenuActionMode) savedInstanceState
					.getSerializable("actionBarMode");
			setTitle(savedInstanceState.getString("title"));

		}

		if (mContent == null) {
			setTitle(getResources().getString(R.string.scanner));
			mContent = new QRFragment();
			mActionMode = MenuActionMode.SCANNER;

			getSupportFragmentManager().beginTransaction()
					.add(R.id.main_content_frame, mContent).commit();

		}

		if (mMenu == null) {
			// set the Behind View Fragment
			mMenu = new MenuFragment();
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.menu_frame, mMenu).commit();

		}

		// customize the SlidingMenu
		SlidingMenu sm = getSlidingMenu();

		sm.setSelected(true);
		sm.setSelectorDrawable(R.drawable.arrow_left);
		sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		sm.setShadowWidthRes(R.dimen.shadow_width);
		sm.setShadowDrawable(R.drawable.shadow);
		sm.setBehindScrollScale(0.25f);
		sm.setFadeDegree(0.25f);

		// show up-back navigation
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setLogo(R.drawable.mobile_menu1);
		getSupportActionBar().setIcon(R.drawable.mobile_menu);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case android.R.id.home:
			toggle();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == GET_RECOGNIZED_TEXT_REQUEST_CODE) {
				mTakeCodeFromCameraCallback.codeFromCameraCallback(data);
				try {
					Integer id = (Integer) (new ScanResultParser()).parse(data
							.getStringExtra("SCAN_RESULT"));
					// Intent intent = ControlPointActivity.newInstance(this,
					// id.intValue());
					Intent intent = PointDetailsActivityV1.newInstance(this,
							id.intValue());
					startActivity(intent);

				} catch (JSONException e) {
					e.printStackTrace();
					ToastUtil
							.ToastLong(this, getString(R.string.scanner_error));
				}
			}
		} else if (resultCode == Activity.RESULT_CANCELED) {
			ToastUtil.ToastLong(this, "QR scanning canceled");
		}
	}

	@Override
	public void takeQR() {
		if (Camera.checkCameraHardware(this)) {

			Intent intent = new Intent(this, CaptureActivity.class);

			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

			intent.setAction("com.itdoors.haccp.SCAN");
			// for Qr code, its “QR_CODE_MODE” instead of “PRODUCT_MODE”
			intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
			// this stops saving ur barcode in barcode scanner app’s history
			intent.putExtra("SAVE_HISTORY", false);
			startActivityForResult(intent, GET_RECOGNIZED_TEXT_REQUEST_CODE);
		} else {
			ToastUtil.ToastLong(this,
					getResources()
							.getString(R.string.your_device_has_no_camera));
		}

	}

	@Override
	public void onSaveInstanceState(Bundle outState) {

		super.onSaveInstanceState(outState);

		getSupportFragmentManager().putFragment(outState, "mContent", mContent);
		getSupportFragmentManager().putFragment(outState, "mMenu", mMenu);
		outState.putSerializable("actionBarMode", mActionMode);
		outState.putString("title", getTitle().toString());

	}

	public void switchContent(final Fragment fragment, String title,
			View selectedView, MenuActionMode actionMode) {

		mContent = fragment;

		getSupportFragmentManager().beginTransaction()
				.replace(R.id.main_content_frame, fragment).commit();

		Handler h = new Handler();
		h.postDelayed(new Runnable() {
			public void run() {
				getSlidingMenu().showContent();
			}
		}, 50);

		mActionMode = actionMode;
		// invalidateOptionsMenu();
		setTitle(title);
		getSlidingMenu().setSelectedView(selectedView);
	}

	@Override
	public void setTakeCodeFromCameraCallBack(TakeQRCallback callback) {
		this.mTakeCodeFromCameraCallback = callback;
	}

	@Override
	public void onProfilePressed() {
		showProfile();
	}

	private void showProfile() {
		String title = getResources().getString(R.string.user_profile);
		// Fragment newContent = new ProfileFragment();
		Fragment newContent = new LoginFragment();
		switchContent(newContent, title, getMenuView(MenuFragment.profile_id),
				MenuActionMode.USER_PROFILE);

	}

	private View getMenuView(int id) {
		return getSlidingMenu().getMenu().findViewById(id);
	}
	
	
	public void addFragmentToStack(Fragment newFragment){
		
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
	    ft.replace(R.id.main_content_frame, newFragment);
	    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
	    ft.addToBackStack(null);
	    ft.commit();
	}
	
	public void switchContentToBackstack(final Fragment fragment, String title,
			View selectedView, MenuActionMode actionMode) {

		mContent = fragment;

		addFragmentToStack(fragment);

		Handler h = new Handler();
		h.postDelayed(new Runnable() {
			public void run() {
				getSlidingMenu().showContent();
			}
		}, 50);

		mActionMode = actionMode;
		// invalidateOptionsMenu();
		setTitle(title);
		getSlidingMenu().setSelectedView(selectedView);
	}

	@Override
	public void onPointsPressed() {
		
		 
		 Company company;
		 	
		 Cursor companiesCursor = getContentResolver().query(HaccpContract.Companies.CONTENT_URI, ComapiesQuery.PROJECTION, null, null, HaccpContract.Companies.DEFAULT_SORT);
		 companiesCursor.moveToFirst();
		 company = new Company(companiesCursor.getInt(ComapiesQuery.UID), companiesCursor.getString(ComapiesQuery.NAME));
		 companiesCursor.close();
		 
		 CompanyObjectsFragment fragment = CompanyObjectsFragment.newInstance(company);
		 switchContent( fragment, 
				 		getString(R.string.points), 
				 		getMenuView(MenuFragment.points_id),
				 		MenuActionMode.POINTS);
	
	}

	public interface ComapiesQuery{
	
		String[] PROJECTION = {
			    	HaccpContract.Companies._ID,
	                HaccpContract.Companies.NAME,
	                HaccpContract.Companies.UID,
	    };
		int _ID = 0;
		int NAME = 1;
		int UID = 2;
	
	}

	@Override
	public void onCompanyObjectPressedListener(CompanyObject companyObject) {
		
		Intent intent = ServicesAndContoursActivity.newIntentInstance(this, companyObject);
		startActivity(intent);
		
	}
	
	

	
}
