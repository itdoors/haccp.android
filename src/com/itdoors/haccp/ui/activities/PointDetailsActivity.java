package com.itdoors.haccp.ui.activities;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.ActionBar.TabListener;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.itdoors.haccp.Global;
import com.itdoors.haccp.Intents;
import com.itdoors.haccp.R;
import com.itdoors.haccp.model.Point;
import com.itdoors.haccp.model.StatististicsItemStatus;
import com.itdoors.haccp.model.rest.retrofit.MoreStatistics;
import com.itdoors.haccp.model.rest.retrofit.Statistic;
import com.itdoors.haccp.rest.robospice_retrofit.GetStatisticsRequest;
import com.itdoors.haccp.rest.robospice_retrofit.MySpiceService;
import com.itdoors.haccp.ui.fragments.AttributesFragment;
import com.itdoors.haccp.ui.fragments.StatisticsOnlineFragment;
import com.itdoors.haccp.ui.fragments.StatisticsOfflineFragment;
import com.itdoors.haccp.ui.fragments.SwipeRefreshListFragment;
import com.itdoors.haccp.ui.fragments.TimeRangeDialogFragment;
import com.itdoors.haccp.ui.fragments.StatisticsOnlineFragment.MODE;
import com.itdoors.haccp.ui.interfaces.OnContextMenuItemPressedListener;
import com.itdoors.haccp.ui.interfaces.OnLongStatisticsItemPressedListener;
import com.itdoors.haccp.ui.interfaces.OnTimeRangeChooseListener;
import com.itdoors.haccp.utils.CalendarUtils;
import com.itdoors.haccp.utils.Enviroment;
import com.itdoors.haccp.utils.Logger;
import com.itdoors.haccp.utils.ToastUtil;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.PendingRequestListener;
import com.octo.android.robospice.request.listener.RequestListener;

public class PointDetailsActivity extends SherlockFragmentActivity implements 
			
		ViewPager.OnPageChangeListener,
		OnContextMenuItemPressedListener,
		OnLongStatisticsItemPressedListener,
		StatisticsOnlineFragment.TimeRangeParametersHolder,																			  
		StatisticsOnlineFragment.StatisticsListModeHolder,
		OnTimeRangeChooseListener,
		OnRefreshListener,
		TabListener
{
	
	private static final String CHOOSE_TIME_RANGE_TYPE_DIALOG = "com.itdoors.haccp.activities.PointDetailsActivity.CHOOSE_TIME_RANGE_TYPE_DIALOG";
	private static final String STATISTICS_MODE_SAVE_KEY 	  = "com.itdoors.haccp.activities.PointDetailsActivity.STATISTICS_MODE_SAVE_KEY";
	private static final String STATISTICS_FROM_TIME_SAVE_KEY = "com.itdoors.haccp.activities.PointDetailsActivity.STATISTICS_FROM_TIME_SAVE_KEY";
	private static final String STATISTICS_TO_TIME_SAVE_KEY   = "com.itdoors.haccp.activities.PointDetailsActivity.STATISTICS_TO_TIME_SAVE_KEY";
	private static final String TITLE_SAVE_KEY 				  = "com.itdoors.haccp.activities.PointDetailsActivity.TITLE_SAVE_KEY";
	 
	private static final int TIME_RANGE_REQUEST = 0x0abc;
	
	protected static enum Mode {
		ONLINE, OFFLINE;
	}
	
	public static Intent newIntent(Activity activity, Point point) {
		Intent intent = new Intent(activity, PointDetailsActivity.class);
		intent.putExtra(Intents.Point.POINT, point);
		return intent;
	}
		
	public static Intent newIntent(Activity activity, int id) {
		Intent intent = new Intent(activity,PointDetailsActivity.class);
		intent.putExtra(Intents.Point.UID,id);
		return intent;
	}
	
	private SpiceManager spiceManager = new SpiceManager(MySpiceService.class);
	
    private ViewPager  mViewPager;
    
    private Fragment mStatisticsFragment;
    
	private StatisticsOnlineFragment.MODE mStatisticFragmentMode;
	private String fromTimeStatisticsTimeStamp;
	private String toTimeStatisticsTimeStamp;
    
	@SuppressWarnings("unused")
	private ActionMode mActionMode;
	
	private Mode networkMode;
	
	RequestListener<MoreStatistics> mStatisticsRefreshRequestListener = new RequestListener<MoreStatistics>() {
		@Override
		public void onRequestFailure(SpiceException exception) {
			ToastUtil.ToastLong(getApplicationContext(), getString(R.string.failed_to_load_data));
			refreshFailed();
		}
		@Override
		public void onRequestSuccess(MoreStatistics statistics) {
		
			PointDetailsActivity.this.mStatisticFragmentMode = MODE.GENERAL;
    		updateStatisticsAfterRefreshSuccess(statistics);
    		//NotificationDialogFragment.newInstance("Responce:", statistics.toString()).show(getSupportFragmentManager(), "responce");
			//refreshSuccess();
		}
	};
	
	PendingRequestListener<MoreStatistics> mStatisticsRefreshPendingRequestListener = new PendingRequestListener<MoreStatistics>() {

		@Override
		public void onRequestFailure(SpiceException exception) {
			ToastUtil.ToastLong(getApplicationContext(), getString(R.string.failed_to_load_data));
			refreshFailed();
		}

		@Override
		public void onRequestSuccess(MoreStatistics statistics) {
			PointDetailsActivity.this.mStatisticFragmentMode = MODE.GENERAL;
    		updateStatisticsAfterRefreshSuccess(statistics);
			//NotificationDialogFragment.newInstance("Responce:", statistics.toString()).show(getSupportFragmentManager(), "responce");
			refreshSuccess();
		}

		@Override
		public void onRequestNotFound() {
		}
	};
	
	RequestListener<MoreStatistics> mStatisticsFromTimeRangeRequestListener = new RequestListener<MoreStatistics>() {
		@Override
		public void onRequestFailure(SpiceException exception) {
			ToastUtil.ToastLong(getApplicationContext(), getString(R.string.failed_to_load_data));
			refreshFailed();
		}
		@Override
		public void onRequestSuccess(MoreStatistics statistics) {
		
			PointDetailsActivity.this.mStatisticFragmentMode = MODE.FROM_TIME_RANGE;
    		updateStatisticsAfterTimeRangeLoadSuccess(statistics);
			//NotificationDialogFragment.newInstance("Responce:", statistics.toString()).show(getSupportFragmentManager(), "responce");
			refreshSuccess();
		}
	};
	
	PendingRequestListener<MoreStatistics> mStatisticsFromTimeRangePendingRequestListener = new PendingRequestListener<MoreStatistics>() {

		@Override
		public void onRequestFailure(SpiceException exception) {
			ToastUtil.ToastLong(getApplicationContext(), getString(R.string.failed_to_load_data));
			refreshFailed();
		}

		@Override
		public void onRequestSuccess(MoreStatistics statistics) {
			PointDetailsActivity.this.mStatisticFragmentMode = MODE.FROM_TIME_RANGE;
    		updateStatisticsAfterTimeRangeLoadSuccess(statistics);
			//NotificationDialogFragment.newInstance("Responce:", statistics.toString()).show(getSupportFragmentManager(), "responce");
			refreshSuccess();
		}

		@Override
		public void onRequestNotFound() {
		}
	};
	
	
	
	
	@Override
	protected void onStart() {
		spiceManager.start(this);
		super.onStart();
	}
	@Override
	protected void onResume() {
		super.onResume();
		//getContentResolver().unregisterContentObserver(mStatiscticsObserver);
		
		if(getIntent().getExtras() != null){
			int id = getIntent().getExtras().getInt(Intents.Point.UID);
			spiceManager.addListenerIfPending(MoreStatistics.class, getRefreshCacheKey(id), mStatisticsRefreshPendingRequestListener);
			spiceManager.addListenerIfPending(MoreStatistics.class, getFromTimeCacheKey(id, fromTimeStatisticsTimeStamp, toTimeStatisticsTimeStamp), mStatisticsFromTimeRangePendingRequestListener);
		}
		
		
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		//getContentResolver().registerContentObserver(statisticsUri, true, mStatiscticsObserver);
	}
	
	
	@Override
	protected void onStop() {
		spiceManager.shouldStop();
		super.onStop();
	}
	
	public SpiceManager getSpiceManager(){
		return spiceManager;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		requestWindowFeature(com.actionbarsherlock.view.Window.FEATURE_INDETERMINATE_PROGRESS);
		super.onCreate(savedInstanceState);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		setSupportProgressBarIndeterminateVisibility(false);
		
		mStatisticFragmentMode = MODE.GENERAL;
		String title = getResources().getString(R.string.control_point);
		
		networkMode = Enviroment.isNetworkAvaliable(this) ? Mode.ONLINE : Mode.OFFLINE;
		
		if (savedInstanceState != null){
			
			mStatisticFragmentMode = (MODE)savedInstanceState.getSerializable(STATISTICS_MODE_SAVE_KEY);
			fromTimeStatisticsTimeStamp = savedInstanceState.getString(STATISTICS_FROM_TIME_SAVE_KEY);
			toTimeStatisticsTimeStamp = savedInstanceState.getString(STATISTICS_TO_TIME_SAVE_KEY);
			title = savedInstanceState.getString(TITLE_SAVE_KEY);
			
			if (mStatisticsFragment == null) {
			 	if(networkMode == Mode.ONLINE)
			 		mStatisticsFragment = (ReplaceableFragment) getSupportFragmentManager()
		                  .getFragment(savedInstanceState, "statistics_stream_fragment");
			 	else
			 		mStatisticsFragment = (ReplaceableFragment) getSupportFragmentManager()
			 			.getFragment(savedInstanceState, "statistics_database_fragment");
			}
		}
		
		setTitle(title);
		setContentView(R.layout.activity_control_point);
		
		mViewPager = (ViewPager)findViewById(R.id.cp_pager);
        
		if(mViewPager != null){
     		
		    mViewPager.setAdapter( new PointInfoTabsAdapter(getSupportFragmentManager()));
		    mViewPager.setOnPageChangeListener(this);
		    mViewPager.setPageMarginDrawable(R.drawable.grey_border_inset_lr);
            mViewPager.setPageMargin(getResources()
                    .getDimensionPixelSize(R.dimen.page_margin_width));
		    
		    final ActionBar actionBar = getSupportActionBar();
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
            actionBar.addTab(actionBar.newTab()
                    .setText(R.string.statistics)
                    .setTabListener(this));
            actionBar.addTab(actionBar.newTab()
                    .setText(R.string.attributes)
                    .setTabListener(this));
           
		   // mViewPager.setPageMarginDrawable(R.drawable.grey_border_inset_lr);
           // mViewPager.setPageMargin(getResources()
           //         .getDimensionPixelSize(R.dimen.page_margin_width));
		   	
        }
		
		ViewGroup bottomPanel = (ViewGroup)findViewById(R.id.cp_bottom_panel);
		View.OnClickListener mOnBottomPanelClickListener = new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				onBottomPanelPressed(v);
			}
		};
		
		bottomPanel.findViewById(R.id.cp_bp_add_item).setOnClickListener(mOnBottomPanelClickListener);
		bottomPanel.findViewById(R.id.cp_bp_params_item).setOnClickListener(mOnBottomPanelClickListener);
		bottomPanel.findViewById(R.id.cp_bp_calendar_item).setOnClickListener(mOnBottomPanelClickListener);
	
	}
   
	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		 mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
	}
	
	
	/*
	private ContentObserver mStatiscticsObserver = new ContentObserver(null) {
		public void onChange(boolean selfChange) {
			if(networkMode == Mode.ONLINE && Enviroment.isNetworkAvaliable(PointDetailsActivity.this))
				beginRefreshStatistics();
		};
	};
	*/
	
	static class DummyTabFactory implements TabHost.TabContentFactory {
		private final Context mContext;
	
		public DummyTabFactory(Context context) {
			mContext = context;
		}
	
		@Override
		public View createTabContent(String tag) {
			View v = new View(mContext);
			v.setMinimumWidth(0);
			v.setMinimumHeight(0);
			return v;
		}
	}
	
	private class PointInfoTabsAdapter extends FragmentPagerAdapter{
		
		public PointInfoTabsAdapter(FragmentManager fm) {
	       super(fm);
		}
		
		@Override
		public Fragment getItem(int position) {
			switch (position) {
			
				case 0: return ( mStatisticsFragment = 
									(networkMode == Mode.ONLINE) 
										? ReplaceableFragment.newInstance(StatisticsOnlineFragment.class)
										: ReplaceableFragment.newInstance(StatisticsOfflineFragment.class));
			
				case 1:	return new AttributesFragment();
			};
			return new Fragment();
		}
		
		@Override
		public int getCount() {
			return 2;
		}
	
	}
	
	public static class ReplaceableFragment extends Fragment{
	
		private Fragment mInsideFragment;
		
		public static ReplaceableFragment newInstance(Class<?> _class){
			
			ReplaceableFragment f = new ReplaceableFragment();
			Bundle args = new Bundle();
			args. putSerializable("class", _class);
			f.setArguments(args);
			return f;
		
		}
		
		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);
			
			FragmentManager fm = getChildFragmentManager();
			mInsideFragment = fm.findFragmentByTag("insideFragment");
			if(mInsideFragment == null && getArguments() != null){
				Class<?> clazz = (Class<?>)getArguments().getSerializable("class");
				mInsideFragment = Fragment.instantiate(getActivity(), clazz.getName());
				fm.beginTransaction()
					.add(R.id.root_frame, mInsideFragment, "insideFragment")
					.commit();
			}
		}
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View view = inflater.inflate(R.layout.fragment_replaceable, container, false);
			//setRetainInstance(true);
			Logger.Logi(getClass(), "onCreateView");
			return view;
		}
	
		public void replaceInside(Fragment fragment){
			mInsideFragment = fragment;
			getChildFragmentManager()
					.beginTransaction()
					.replace(R.id.root_frame, fragment, "insideFragment")
					.commit();
		}
		
		public Fragment getFragment(){
			return mInsideFragment;
		}
		
	}
    
    public boolean onBottomPanelPressed(View item){
		
		switch (item.getId()) {
		
			case R.id.cp_bp_add_item:
				
				int pointId = getIntent().getExtras().getInt(Intents.Point.UID);
				Intent intent = new Intent(this, AddStatisticsActivity.class);
				intent.putExtra(Intents.Point.UID, pointId);
				startActivity(intent);
				return true;
			
			case R.id.cp_bp_params_item:
				ToastUtil.ToastLong(getApplicationContext(), "Params");
				return true;
			
			case R.id.cp_bp_calendar_item:
			{
				if(Enviroment.isNetworkAvaliable(this)){
					
					TimeRangeDialogFragment dialog = new TimeRangeDialogFragment();
					dialog.show(getSupportFragmentManager(), CHOOSE_TIME_RANGE_TYPE_DIALOG );
					return true;
					
				}
				else{
					ToastUtil.ToastLong(getApplicationContext(), getString(R.string.not_avalieble_without_any_interent_connection));
					return false;
				}
			}
			default:
				return false;
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
	public void onSaveInstanceState(Bundle outState) {
		
		super.onSaveInstanceState(outState);
		outState.putSerializable(STATISTICS_MODE_SAVE_KEY, mStatisticFragmentMode);
		outState.putString(STATISTICS_FROM_TIME_SAVE_KEY, fromTimeStatisticsTimeStamp);
		outState.putString(STATISTICS_TO_TIME_SAVE_KEY, toTimeStatisticsTimeStamp);
		outState.putString(TITLE_SAVE_KEY, getTitle().toString());
		
		if (mStatisticsFragment != null) {
			if(networkMode == Mode.ONLINE)
				 getSupportFragmentManager().putFragment(outState, "statistics_stream_fragment",
						mStatisticsFragment);
			else getSupportFragmentManager().putFragment(outState, "statistics_database_fragment", 
						mStatisticsFragment);
	    }
	}

    @SuppressLint("SimpleDateFormat")
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    if(resultCode == Activity.RESULT_OK){
	    	if(requestCode == TIME_RANGE_REQUEST){
	    		
	    		String fromTimeStamp = data.getStringExtra(Intents.CalendarTimeRange.FROM_TIME_STAMP);
	    		String toTimeStamp = data.getStringExtra(Intents.CalendarTimeRange.TO_TIME_STAMP);
	    		
	    		String fromTimeStampStr = CalendarUtils.inUsualDateFromat(fromTimeStamp);
	    		String toTimeStampStr = CalendarUtils.inUsualDateFromat(toTimeStamp);
	    				
	    		ToastUtil.ToastLong(getApplicationContext(), getString(R.string.from) + " : " + fromTimeStampStr + ", " + getString(R.string.to) + " : " + toTimeStampStr);
	    		beginStatisticsFromTimeStampLoading(fromTimeStamp, toTimeStamp);
	    	
	    	}
	    }
	}
    
    @Override
	public MODE getMode() {
		return mStatisticFragmentMode;
	}

	@Override
	public String getFromTimeInTimeStamp() {
		return fromTimeStatisticsTimeStamp;
	}

	@Override
	public String getToTimeInTimeStamp() {
		return toTimeStatisticsTimeStamp;
	}

	private final class StatisticsActionMode implements ActionMode.Callback {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            
        	menu.add(getResources().getString(R.string.edit)).setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
            menu.add(getResources().getString(R.string.delete)).setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
            
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            
        	ToastUtil.ToastLong(PointDetailsActivity.this.getApplicationContext(), "Got click: " + item);
            mode.finish();
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
        }
    }
	
	@Override
	public void onLongStatisticsItemPressed(long itemId) {
		mActionMode = startActionMode(new StatisticsActionMode());	
	}

	@Override
	public void onEditStaticticsItemContextMenuPressed(int position) {
		ToastUtil.ToastLong(getApplicationContext(), getString(R.string.edit) + ":" + position);
	}

	@Override
	public void onDeleteStaticticsItemContextMenuPressed(int position) {
		ToastUtil.ToastLong(getApplicationContext(), getString(R.string.delete) + ":" + position);
	}	

	// Swipe refresh support.v4 rev.19.1;
	@Override
	public void onRefresh() {
		if(networkMode == Mode.ONLINE){
			beginRefreshStatistics();
		}
		else{
			if(Enviroment.isNetworkAvaliable(this)){
				beginRefreshStatistics();
			}
			else{
				ToastUtil.ToastLong(getApplicationContext(), getString(R.string.not_avalieble_without_any_interent_connection));
				refreshFailed();
			}
		}
		
	}

	@SuppressLint("SimpleDateFormat")
	@Override
	public void onTimeRagneClicked(int item_type) {
		
		boolean directLoad = true;
		
		boolean today = false;
		boolean yesterday = false;
		
		String fromUnixTimeStamp = null, toUnixTimeStamp = null;
		
		final Calendar toDateCalendar = Calendar.getInstance();
		Date toDate = CalendarUtils.getEndOfDay(toDateCalendar.getTime());
		
		final Calendar fromDateCalendar = Calendar.getInstance();
		Date  fromDate = null;
		
		switch (item_type) {
			
			case 0:
				//Today
				fromDate = CalendarUtils.getStartOfDay(fromDateCalendar.getTime());
				today = true;
			break;
			case 1:
				//Yesterday
				fromDateCalendar.add(Calendar.DATE, -1);
				fromDate = CalendarUtils.getStartOfDay(fromDateCalendar.getTime());
				toDate = CalendarUtils.getEndOfDay(fromDate);
				yesterday = true;
			break;
			case 2:
				//LastWeak = last 7 days
				fromDateCalendar.add(Calendar.DATE, -7 + 1);
				fromDate = CalendarUtils.getStartOfDay(fromDateCalendar.getTime());
			break;
			case 3:
				//Last 30 days
				fromDateCalendar.add(Calendar.DATE, -30 + 1);
				fromDate = CalendarUtils.getStartOfDay(fromDateCalendar.getTime());
			break;
			case 4:
				//This month
				fromDateCalendar.set(Calendar.DATE, 1);
				fromDate = CalendarUtils.getStartOfDay(fromDateCalendar.getTime());
			break;
			case 5:
				directLoad = false;
				Intent intent = new Intent(this, CalendarActivity.class);
				startActivityForResult(intent, TIME_RANGE_REQUEST);
			break;
			default:
				directLoad = false;
			break;
		}
		
		if(directLoad){
			if(Enviroment.isNetworkAvaliable(this)){
				
				fromUnixTimeStamp = Long.toString(fromDate.getTime() / 1000);
				toUnixTimeStamp =  Long.toString(toDate.getTime() / 1000);
				
				String fromTimeStampStr = new SimpleDateFormat(Global.usualDateFromat).format(new Date(Long.valueOf(fromUnixTimeStamp)*1000)).toString();
	    		String toTimeStampStr = new SimpleDateFormat(Global.usualDateFromat).format(new Date(Long.valueOf(toUnixTimeStamp)*1000)).toString();
				String toastMess = getString(R.string.from) + " : " + fromTimeStampStr + " , " + getString(R.string.to) +" : " + toTimeStampStr;
	        	
				if( today )		toastMess = getString(R.string.today) + " : " + fromTimeStampStr;
	    		if( yesterday )	toastMess = getString(R.string.yesterday) + " : " + fromTimeStampStr;
	    		
	    		ToastUtil.ToastLong(getApplicationContext(), toastMess);
	    		
	    		beginStatisticsFromTimeStampLoading(fromUnixTimeStamp, toUnixTimeStamp);
				
			}
			else{
				ToastUtil.ToastLong(getApplicationContext(), getString(R.string.not_avalieble_without_any_interent_connection));
			}
		}
	}

	@Override
	public void onPageScrollStateChanged(int i) {
	}

	@Override
    public void onPageScrolled(int i, float v, int i1) {
    }

	@Override
    public void onPageSelected(int position) {
		 getSupportActionBar().setSelectedNavigationItem(position);
    }

	

	
	
	protected void showProgress(){
		setSupportProgressBarIndeterminateVisibility(true);
	}
	protected void hideProgress(){
		setSupportProgressBarIndeterminateVisibility(false);
	}
	
	private static final String STATISTICS_REFRESH_CACHE_KEY = "statistics_refresh";
	private static final String STATISTICS_FROM_TIME_RANGE_CACHE_KEY = "statistics_from_time_range";
	
	private static String getRefreshCacheKey(int id){
		return STATISTICS_REFRESH_CACHE_KEY +"_" + id;
	}
	
	private static String getFromTimeCacheKey(int id, String from, String to){
		return STATISTICS_FROM_TIME_RANGE_CACHE_KEY + "_" + id + "_" + from + "_" + to;
	}
	
	
	private void beginRefreshStatistics(){
		// RoboSpice request
		Bundle extras = getIntent().getExtras();
		if(  extras!= null ){
			int pointId = extras.getInt(Intents.Point.UID);
			GetStatisticsRequest request = new GetStatisticsRequest.Builder().setId(pointId).build();
			spiceManager.execute(request, getRefreshCacheKey(pointId), DurationInMillis.ONE_MINUTE, mStatisticsRefreshRequestListener);
		}
	}
	
	private void beginStatisticsFromTimeStampLoading(String fromUnixTimeStamp, String toUnixTimeStamp){
		
		this.fromTimeStatisticsTimeStamp = fromUnixTimeStamp;
		this.toTimeStatisticsTimeStamp = toUnixTimeStamp;
		
		Bundle extras = getIntent().getExtras();
		if( extras != null){ 
			int pointId = extras.getInt(Intents.Point.UID);
			GetStatisticsRequest request = new GetStatisticsRequest.Builder().setId(pointId).setStartDate(fromUnixTimeStamp).setEndDate(toUnixTimeStamp).build();
			spiceManager.execute(request, getFromTimeCacheKey(pointId, fromUnixTimeStamp, toUnixTimeStamp), DurationInMillis.ONE_MINUTE, mStatisticsFromTimeRangeRequestListener);
		}
	}
	
	protected void updateStatisticsAfterTimeRangeLoadSuccess(MoreStatistics content){
		if(mStatisticsFragment != null){
			if(networkMode == Mode.ONLINE){
				StatisticsOnlineFragment fragment = (StatisticsOnlineFragment) ((ReplaceableFragment)mStatisticsFragment).getFragment();
				fragment.updateStatisticsAfterTimeRangeLoadSuccess(content.getStatistics(), content.getMore());
			}
			else{
				ReplaceableFragment fragment = (ReplaceableFragment)mStatisticsFragment;
    			StatisticsOnlineFragment newFragment = StatisticsOnlineFragment.newInstance(
    					StatisticsOnlineFragment.ACTION_UPDATE_AFTER_TIME_RANGE_WITH_PRELOADED_CONTENT_CODE, 
    					(ArrayList<Statistic>)content.getStatistics(), 
    					content.getMore()
    			);
    			
    			fragment.replaceInside(newFragment);
    			networkMode = Mode.ONLINE;
			}
		}
	}
	
	protected void updateStatisticsAfterRefreshSuccess(MoreStatistics content){
		if(mStatisticsFragment != null){
			if(networkMode == Mode.ONLINE){
				
				StatisticsOnlineFragment fragment = (StatisticsOnlineFragment) ((ReplaceableFragment)mStatisticsFragment).getFragment();
				(fragment).updateStatisticsAfterRefreshSuccess(content.getStatistics(), content.getMore());
				refreshSuccess();
			}
			else{
				
				ReplaceableFragment fragment = (ReplaceableFragment)mStatisticsFragment;
    			StatisticsOnlineFragment newFragment = StatisticsOnlineFragment.newInstance(
    					StatisticsOnlineFragment.ACTION_UPDATE_AFTER_TIME_RANGE_WITH_PRELOADED_CONTENT_CODE, 
    					(ArrayList<Statistic>)content.getStatistics(), 
    					content.getMore()
    			);
    			
    			fragment.replaceInside(newFragment);
    			networkMode = Mode.ONLINE;	
			}
		}
		
}
	
	
	
	protected void refreshFailed(){
		if(mStatisticsFragment != null){
			Fragment frg = ((ReplaceableFragment)mStatisticsFragment).getFragment();
			SwipeRefreshListFragment swipeFrg = (SwipeRefreshListFragment)frg;
			swipeFrg.setRefreshing(false);
		}
	}
	protected void refreshSuccess(){
		if(mStatisticsFragment != null){
			Fragment frg = ((ReplaceableFragment)mStatisticsFragment).getFragment();
			SwipeRefreshListFragment swipeFrg = (SwipeRefreshListFragment)frg;
			swipeFrg.setRefreshing(false);
		}
	}

	public static void setUpStatusView(StatististicsItemStatus status, TextView tv, Map<StatististicsItemStatus, String> statuses){
		switch (status) {
			case WARNING:
				tv.setText(statuses.get(StatististicsItemStatus.WARNING));
				tv.setBackgroundResource(R.color.status_warning);
			break;
			case DANGER:
				tv.setText(statuses.get(StatististicsItemStatus.DANGER));
				tv.setBackgroundResource(R.color.status_danger);
			break;
			default:
				tv.setText(statuses.get(StatististicsItemStatus.APPROVED));
				tv.setBackgroundResource(R.color.status_approved);
			break;
		}
	}
	
	public static HashMap<StatististicsItemStatus, String> getStatusesMap (Context context){
		
		String approved = context.getString(R.string.cp_statistics_type_approved);
		String warning = context.getString(R.string.cp_statistics_type_warning);
		String danger = context.getString(R.string.cp_statistics_type_danger);
		
		HashMap<StatististicsItemStatus, String> map = new HashMap<StatististicsItemStatus, String>();
		map.put(StatististicsItemStatus.APPROVED, approved);
		map.put(StatististicsItemStatus.WARNING, warning);
		map.put(StatististicsItemStatus.DANGER, danger);
		
		return map;
	}
	
	public static StatististicsItemStatus getStatus(double value, double top, double bottom){
		return ( value <= bottom ) ? StatististicsItemStatus.APPROVED : ( 
			   (value > bottom && value <= top) ? StatististicsItemStatus.WARNING : StatististicsItemStatus.DANGER );
		
	}

	public static StatististicsItemStatus getStatus(String v, String t, String b){
		
		try{
			
			Double value = Double.parseDouble(v);
			Double top = Double.parseDouble(t);
			Double bottom = Double.parseDouble(b);
			
			return getStatus(value, top, bottom);
				
		}
		catch (NumberFormatException e){
			e.printStackTrace();
			return null;
		
		}
		
		
	}
	
}
