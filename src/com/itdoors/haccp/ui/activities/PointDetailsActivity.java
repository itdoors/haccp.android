package com.itdoors.haccp.ui.activities;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONException;

import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.itdoors.haccp.Global;
import com.itdoors.haccp.Intents;
import com.itdoors.haccp.R;
import com.itdoors.haccp.exceptions.ServerFailedException;
import com.itdoors.haccp.loaders.RESTLoader;
import com.itdoors.haccp.model.Point;
import com.itdoors.haccp.model.StatisticsRecord;
import com.itdoors.haccp.parser.LoadMoreStatisticsParser;
import com.itdoors.haccp.parser.PointStatisticsFromTimeRangeParser;
import com.itdoors.haccp.provider.HaccpContract;
import com.itdoors.haccp.ui.fragments.AttributesFragment;
import com.itdoors.haccp.ui.fragments.StatisticsOnlineFragment;
import com.itdoors.haccp.ui.fragments.StatisticsOfflineFragment;
import com.itdoors.haccp.ui.fragments.TimeRangeDialogFragment;
import com.itdoors.haccp.ui.fragments.StatisticsOnlineFragment.MODE;
import com.itdoors.haccp.ui.interfaces.OnContextMenuItemPressedListener;
import com.itdoors.haccp.ui.interfaces.OnLongStatisticsItemPressedListener;
import com.itdoors.haccp.ui.interfaces.OnTimeRangeChooseListener;
import com.itdoors.haccp.utils.CalendarUtils;
import com.itdoors.haccp.utils.Enviroment;
import com.itdoors.haccp.utils.Logger;
import com.itdoors.haccp.utils.ToastUtil;

public class PointDetailsActivity extends SherlockFragmentActivity implements 
			
		ViewPager.OnPageChangeListener,
		TabHost.OnTabChangeListener,
		OnContextMenuItemPressedListener,
		OnLongStatisticsItemPressedListener,
		StatisticsOnlineFragment.TimeRangeParametersHolder,																			  
		StatisticsOnlineFragment.StatisticsListModeHolder,
		OnTimeRangeChooseListener,
		OnRefreshListener,
		LoaderCallbacks<RESTLoader.RESTResponse>
{
	
	protected static final String CHOOSE_TIME_RANGE_TYPE_DIALOG 	= "com.itdoors.haccp.activities.PointDetailsActivity.CHOOSE_TIME_RANGE_TYPE_DIALOG";
	protected static final String CHOOSE_CUSTOM_TIME_RANGE_DIALOG 	= "com.itdoors.haccp.activities.PointDetailsActivity.CHOOSE_CUSTOM_TIME_RANGE_DIALOG";
	public static final String STATISTICS_MODE_SAVE_KEY 			= "com.itdoors.haccp.activities.PointDetailsActivity.STATISTICS_MODE_SAVE_KEY";
	public static final String STATISTICS_FROM_TIME_SAVE_KEY 		= "com.itdoors.haccp.activities.PointDetailsActivity.STATISTICS_FROM_TIME_SAVE_KEY";
	public static final String STATISTICS_TO_TIME_SAVE_KEY 			= "com.itdoors.haccp.activities.PointDetailsActivity.STATISTICS_TO_TIME_SAVE_KEY";
	public static final String TITLE_SAVE_KEY 						= "com.itdoors.haccp.activities.PointDetailsActivity.TITLE_SAVE_KEY";
	public static final String CONTROL_POINT_STATISTICS_TAG 		= "com.itdoors.haccp.activities.PointDetailsActivity.CONTROL_POINT_STATISTICS_TAG";
	public static final String CONTROL_POINT_ATTRIBUTES_TAG			= "com.itdoors.haccp.activities.PointDetailsActivity.CONTROL_POINT_ATTRIBUTES_TAG";
	protected static final String ARGS_PIAS_URI 					= "com.itdoors.haccp.activities.PointDetailsActivity.ARGS_PIAS_URI";
	protected static final String ARGS_PIAS_PARAMS_URI 				= "com.itdoors.haccp.activities.PointDetailsActivity.ARGS_PIAS_PARAMS_URI";
	 
	protected static final int TIME_RANGE_REQUEST = 0x0abc;
	protected static final int POINT_STATICTIS_FROM_TIME_RANGE_CODE = 1;
	protected static final int REFRESH_STATICTIS_CODE = 2;

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
	
	private TabHost mTabHost;
    private ViewPager  mViewPager;
    
    private Fragment mStatisticsFragment;
    
	private StatisticsOnlineFragment.MODE mStatisticFragmentMode;
	private String fromTimeStatisticsTimeStamp;
	private String toTimeStatisticsTimeStamp;
    
	@SuppressWarnings("unused")
	private ActionMode mActionMode;
	
	private Uri statisticsUri;
	private Mode networkMode;
	
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
		setContentView(R.layout.activity_control_point_v1);
		
		mViewPager = (ViewPager)findViewById(R.id.cp_pager);
        
		if(mViewPager != null){
        	
			mTabHost = (TabHost)findViewById(android.R.id.tabhost);
		    mTabHost.setup();
	    	
		    LayoutInflater inflater = getLayoutInflater();
			View cpStatisticsView = createTabView(inflater, getResources().getString(R.string.statistics));
		    View cpAttributesView = createTabView(inflater, getResources().getString(R.string.attributes));
		    
		    TabSpec mStatisticsTabSpec = mTabHost.newTabSpec(CONTROL_POINT_STATISTICS_TAG).setContent(new DummyTabFactory(this)).setIndicator(cpStatisticsView);
		    TabSpec mAttributesTabSpec = mTabHost.newTabSpec(CONTROL_POINT_ATTRIBUTES_TAG).setContent(new DummyTabFactory(this)).setIndicator(cpAttributesView);
		    
		    mTabHost.addTab(mStatisticsTabSpec);
		    mTabHost.addTab(mAttributesTabSpec);
		    
		    mTabHost.setOnTabChangedListener(this);
		    
		    mViewPager.setAdapter( new PointInfoTabsAdapter(getSupportFragmentManager()));
		    mViewPager.setOnPageChangeListener(this);
           
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
	
		int pointId = getIntent().getExtras().getInt(Intents.Point.UID);
		statisticsUri = HaccpContract.Statistics.buildUriForPoint(pointId);
	}
   
	@Override
	protected void onPause() {
		super.onPause();
		getContentResolver().registerContentObserver(statisticsUri, true, mStatiscticsObserver);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		getContentResolver().unregisterContentObserver(mStatiscticsObserver);
	}
	
	private ContentObserver mStatiscticsObserver = new ContentObserver(new Handler()) {
		public void onChange(boolean selfChange) {
			if(networkMode == Mode.ONLINE && Enviroment.isNetworkAvaliable(PointDetailsActivity.this))
				beginRefreshStatistics();
		};
	};
	
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
										? new ReplaceableFragment( new StatisticsOnlineFragment() ) 
										: new ReplaceableFragment( new StatisticsOfflineFragment() ));
				case 1:	return new AttributesFragment();
			};
			return new Fragment();
		}
		
		@Override
		public int getCount() {
			return 2;
		}
	
	}
	
	@SuppressLint("ValidFragment")
	private static class ReplaceableFragment extends Fragment{
		
		private Fragment mInsideFragment;
		
		public ReplaceableFragment(Fragment insideFragment) {
			mInsideFragment = insideFragment;
		}
		
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setRetainInstance(true);
		}
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View view = inflater.inflate(R.layout.fragment_replaceable, container, false);
			if(mInsideFragment != null){
				replaceInsidefragment(mInsideFragment);
			}
			return view;
		}
		
		public void replaceInsidefragment(Fragment fragment){
			mInsideFragment = fragment;
			FragmentTransaction transaction = getChildFragmentManager()
					.beginTransaction();
			transaction.replace(R.id.root_frame, fragment);
			transaction.commit();
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
				ToastUtil.ToastLong(this, "Params");
				return true;
			
			case R.id.cp_bp_calendar_item:
			{
				if(Enviroment.isNetworkAvaliable(this)){
					
					TimeRangeDialogFragment dialog = new TimeRangeDialogFragment();
					dialog.show(getSupportFragmentManager(), CHOOSE_TIME_RANGE_TYPE_DIALOG );
					return true;
					
				}
				else{
					ToastUtil.ToastLong(this, getString(R.string.not_avalieble_without_any_interent_connection));
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
  
    private static View createTabView(LayoutInflater inflater, final String text) {
        View view = inflater.inflate(R.layout.tabs_view, null);
        TextView tv = (TextView) view.findViewById(R.id.tabsText);
        tv.setText(text);
        return view;
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
	    				
	    		ToastUtil.ToastLong(this, getString(R.string.from) + " : " + fromTimeStampStr + ", " + getString(R.string.to) + " : " + toTimeStampStr);
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
            
        	ToastUtil.ToastLong(PointDetailsActivity.this, "Got click: " + item);
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
		ToastUtil.ToastLong(this, getString(R.string.edit) + ":" + position);
	}

	@Override
	public void onDeleteStaticticsItemContextMenuPressed(int position) {
		ToastUtil.ToastLong(this, getString(R.string.delete) + ":" + position);
	}	

	@Override
	public void onRefreshStarted(View view) {
		if(networkMode == Mode.ONLINE){
			beginRefreshStatistics();
		}
		else{
			if(Enviroment.isNetworkAvaliable(this)){
				beginRefreshStatistics();
			}
			else{
				ToastUtil.ToastLong(this, getString(R.string.not_avalieble_without_any_interent_connection));
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
	    		
	    		ToastUtil.ToastLong(this, toastMess);
	    		
	    		beginStatisticsFromTimeStampLoading(fromUnixTimeStamp, toUnixTimeStamp);
				
			}
			else{
				ToastUtil.ToastLong(this, getString(R.string.not_avalieble_without_any_interent_connection));
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
		mTabHost.setCurrentTab(position);
    }

	@Override
	public void onTabChanged(String tabId) {
		  int position = mTabHost.getCurrentTab();
		  mViewPager.setCurrentItem(position);
	}
	
	
	@Override
	public Loader<RESTLoader.RESTResponse> onCreateLoader(int id, Bundle args) {
		switch (id) {
			case POINT_STATICTIS_FROM_TIME_RANGE_CODE:
			case REFRESH_STATICTIS_CODE:
				if (args != null && args.containsKey(ARGS_PIAS_URI) && args.containsKey(ARGS_PIAS_PARAMS_URI)) {
		            
					Uri    action = args.getParcelable(ARGS_PIAS_URI);
		            Bundle params = args.getParcelable(ARGS_PIAS_PARAMS_URI);
		            return new RESTLoader(this, RESTLoader.HTTPVerb.GET, action, params);
		        }
				else{
					throw new IllegalArgumentException("params did't contain " + ARGS_PIAS_URI +" or " +  ARGS_PIAS_PARAMS_URI);
				}
				
			default:
				throw new IllegalArgumentException("unknown loader id : " + id);
		}
	}

	@Override
	public void onLoadFinished(Loader<RESTLoader.RESTResponse> loader, RESTLoader.RESTResponse data) {
		    switch (loader.getId()) {
		    	case POINT_STATICTIS_FROM_TIME_RANGE_CODE:
	        		onStaticsFromTimeRangeLoadFinished(data);
	        		break;
	        	case REFRESH_STATICTIS_CODE:
	        		onRefreshFinished(data);
	        		break;
	    	}
	}
	
	@Override
	public void onLoaderReset(Loader<RESTLoader.RESTResponse> loader) {
	}
	
	protected void showProgress(){
		setSupportProgressBarIndeterminateVisibility(true);
	}
	protected void hideProgress(){
		setSupportProgressBarIndeterminateVisibility(false);
	}
	
	private void beginRefreshStatistics(){
		
		if( getIntent().getExtras() != null ){
			
			int pointId = getIntent().getExtras().getInt(Intents.Point.UID);
			
			String url = Global.API_URL +"/point/" + Integer.toString(pointId) + "/statistics";
			Uri loadPointInfoAndStatistic = Uri.parse(url);
			
		    Bundle args = new Bundle();
	        args.putParcelable(ARGS_PIAS_URI, loadPointInfoAndStatistic);
	        args.putParcelable(ARGS_PIAS_PARAMS_URI, null);
	        
	        
	        Loader<Object> loader = getSupportLoaderManager().getLoader(REFRESH_STATICTIS_CODE);
			if(  loader == null )
				 getSupportLoaderManager().initLoader(REFRESH_STATICTIS_CODE, args, this);
			else getSupportLoaderManager().restartLoader(REFRESH_STATICTIS_CODE, args, this);
	      
		}
	}

	private void beginStatisticsFromTimeStampLoading(String fromUnixTimeStamp, String toUnixTimeStamp){
		
		this.fromTimeStatisticsTimeStamp = fromUnixTimeStamp;
		this.toTimeStatisticsTimeStamp = toUnixTimeStamp;
		
		if(getIntent().getExtras() == null) 
			return;
		
		int pointId = getIntent().getExtras().getInt(Intents.Point.UID);
		String url = Global.API_URL + "/point/" + Integer.toString(pointId) + 
									  "/statistics/" + fromUnixTimeStamp +"/" + toUnixTimeStamp;
		
		Uri loadStatistic = Uri.parse(url);
		
	    Bundle args = new Bundle();
        args.putParcelable(ARGS_PIAS_URI, loadStatistic);
        args.putParcelable(ARGS_PIAS_PARAMS_URI, null);
        
        showProgress();
        
        Loader<Object> loader = getSupportLoaderManager().getLoader(POINT_STATICTIS_FROM_TIME_RANGE_CODE);
		if(  loader == null )
			 getSupportLoaderManager().initLoader(POINT_STATICTIS_FROM_TIME_RANGE_CODE, args, this);
		else getSupportLoaderManager().restartLoader(POINT_STATICTIS_FROM_TIME_RANGE_CODE, args, this);
	
	}
	
	private void onStaticsFromTimeRangeLoadFinished (RESTLoader.RESTResponse data){
		
		int    code = data.getCode();
        String json = data.getData();
        hideProgress();
        if (code == 200) {
        	PointStatisticsFromTimeRangeParser parser = new PointStatisticsFromTimeRangeParser();
        	try {
				final Object content = parser.parse(json);
				final com.itdoors.haccp.parser.PointStatisticsFromTimeRangeParser.Content loadedContent = (com.itdoors.haccp.parser.PointStatisticsFromTimeRangeParser.Content)content;
	    		this.mStatisticFragmentMode = MODE.FROM_TIME_RANGE;
	    		//Show result in short and details fragments
	    		updateStatisticsAfterTimeRangeLoadSuccess(loadedContent);
        	}
        	catch (JSONException e) {
        		ToastUtil.ToastLong(this, "JSONException.");
				e.printStackTrace();
			}
        	catch (ServerFailedException e) {
        		ToastUtil.ToastLong(this, "ServerFailedException.");
    	        e.printStackTrace();
    		}
        }
        else {
        	Logger.Logi(getClass(), "code: " + code +"; json: " + json);
        	ToastUtil.ToastLong(this, getString(R.string.failed_to_load_data));
        }
	}
	
	private void onRefreshFinished(RESTLoader.RESTResponse data){
		
		int    code = data.getCode();
        String json = data.getData();
        
        boolean failed = false;
        if (code == 200) {
        	LoadMoreStatisticsParser parser = new LoadMoreStatisticsParser();
        	try {
				final Object content = parser.parse(json);
				com.itdoors.haccp.parser.LoadMoreStatisticsParser.Content loadedContent = (com.itdoors.haccp.parser.LoadMoreStatisticsParser.Content)content;
				
	    		this.mStatisticFragmentMode = MODE.GENERAL;
	    		updateStatisticsAfterRefreshSuccess(loadedContent);
        	}
        	catch (JSONException e) {
				failed = true;
        		ToastUtil.ToastLong(this, "JSONException.");
				e.printStackTrace();
			}
        	catch (ServerFailedException e) {
        		failed = true;
        		ToastUtil.ToastLong(this, "ServerFailedException.");
    	        e.printStackTrace();
    		}
       }
       else {
    	    failed = true;
       		Logger.Logi(getClass(), "code: " + code +"; json: " + json);
       		ToastUtil.ToastLong(this, getString(R.string.failed_to_load_data));
       }
       if(failed){
    	   refreshFailed();
       }
	}
	
	protected void updateStatisticsAfterTimeRangeLoadSuccess(com.itdoors.haccp.parser.PointStatisticsFromTimeRangeParser.Content content){
		if(mStatisticsFragment != null){
			if(networkMode == Mode.ONLINE){
				StatisticsOnlineFragment fragment = (StatisticsOnlineFragment) ((ReplaceableFragment)mStatisticsFragment).getFragment();
				fragment.updateStatisticsAfterTimeRangeLoadSuccess(content.records, content.hasMoreStatiscticItems);
			}
			else{
				ReplaceableFragment fragment = (ReplaceableFragment)mStatisticsFragment;
    			StatisticsOnlineFragment newFragment = StatisticsOnlineFragment.newInstance(
    					
    					StatisticsOnlineFragment.ACTION_UPDATE_AFTER_TIME_RANGE_WITH_PRELOADED_CONTENT_CODE, 
    					(ArrayList<StatisticsRecord>)content.records, 
    					content.hasMoreStatiscticItems
    			);
    			
    			fragment.replaceInsidefragment(newFragment);
    			networkMode = Mode.ONLINE;
			}
		}
	}
	
	protected void updateStatisticsAfterRefreshSuccess(com.itdoors.haccp.parser.LoadMoreStatisticsParser.Content content){
			if(mStatisticsFragment != null){
				if(networkMode == Mode.ONLINE){
					
					StatisticsOnlineFragment fragment = (StatisticsOnlineFragment) ((ReplaceableFragment)mStatisticsFragment).getFragment();
					(fragment).updateStatisticsAfterRefreshSuccess(content.records, content.hasMoreStatiscticItems);
				
				}
				else{
					ReplaceableFragment fragment = (ReplaceableFragment)mStatisticsFragment;
	    			StatisticsOnlineFragment newFragment = StatisticsOnlineFragment.newInstance(
	    					
	    					StatisticsOnlineFragment.ACTION_UPDATE_AFTER_TIME_RANGE_WITH_PRELOADED_CONTENT_CODE, 
	    					(ArrayList<StatisticsRecord>)content.records, 
	    					content.hasMoreStatiscticItems
	    			);
	    			
	    			fragment.replaceInsidefragment(newFragment);
	    			networkMode = Mode.ONLINE;	
				}
			}
			
	}
	
	protected void refreshFailed(){
		if(mStatisticsFragment != null){
			Fragment statisticsFragment = ((ReplaceableFragment)mStatisticsFragment).getFragment();
			if(networkMode == Mode.ONLINE){
				StatisticsOnlineFragment fragment = (StatisticsOnlineFragment)statisticsFragment;
				fragment.refreshReshFailed();
			}
			else{
				StatisticsOfflineFragment fragment = (StatisticsOfflineFragment)statisticsFragment;
				fragment.refreshReshFailed();
			}
		}
	}
	
}
