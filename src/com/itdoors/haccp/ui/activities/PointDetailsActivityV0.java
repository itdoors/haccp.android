package com.itdoors.haccp.ui.activities;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import org.json.JSONException;

import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.view.View;
import android.view.ViewGroup;

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
import com.itdoors.haccp.model.PointStatus;
import com.itdoors.haccp.model.StatisticsRecord;
import com.itdoors.haccp.parser.ControlPointParser;
import com.itdoors.haccp.parser.ControlPointParser.Content;
import com.itdoors.haccp.parser.LoadMoreStatisticsParser;
import com.itdoors.haccp.parser.PointStatisticsFromTimeRangeParser;
import com.itdoors.haccp.parser.StaticticsAddInputParser;
import com.itdoors.haccp.parser.StatusesParser;
import com.itdoors.haccp.ui.fragments.PointDetailsFragmentV0;
import com.itdoors.haccp.ui.fragments.StatisticsFragmentV0;
import com.itdoors.haccp.ui.fragments.TimeRangeDialogFragment;
import com.itdoors.haccp.ui.fragments.StatisticsFragmentV0.MODE;
import com.itdoors.haccp.ui.interfaces.OnTimeRangeChooseListener;
import com.itdoors.haccp.utils.CalendarUtils;
import com.itdoors.haccp.utils.LoadActivityUtils;
import com.itdoors.haccp.utils.Logger;
import com.itdoors.haccp.utils.ToastUtil;
import java.util.Date;

@SuppressLint("SimpleDateFormat")
public class PointDetailsActivityV0 extends SherlockFragmentActivity implements StatisticsFragmentV0.OnContextMenuItemPressedListener,
																			  StatisticsFragmentV0.OnLongStatisticsItemPressedListener,
																			  StatisticsFragmentV0.TimeRangeParametersHolder,																			  
																			  StatisticsFragmentV0.StatisticsListModeHolder,
																			  OnTimeRangeChooseListener,
																			  OnRefreshListener,
																			  LoaderCallbacks<RESTLoader.RESTResponse>{
	
	public static final String POINT_KEY = "com.itdoors.haccp.activities.PointDetailsActivity.POINT_KEY";
	public static final String POINT_ID_KEY = "com.itdoors.haccp.activities.PointDetailsActivity.POINT_ID_KEY";
	
	public static final String DETAILS_SAVE_KEY = "com.itdoors.haccp.activities.PointDetailsActivity.DETAILS_SAVE_KEY";
	public static final String SHORT_INFO_SAVE_KEY = "com.itdoors.haccp.activities.PointDetailsActivity.SHORT_INFO_SAVE_KEY";
	public static final String POINT_CONTENT_SAVE_KEY = "com.itdoors.haccp.activities.PointDetailsActivity.POINT_CONTENT_SAVE_KEY";
	public static final String STATISTICS_CONTENT_SAVE_KEY = "com.itdoors.haccp.activities.PointDetailsActivity.STATISTICS_CONTENT_SAVE_KEY";
	public static final String STATISTICS_MODE_SAVE_KEY = "com.itdoors.haccp.activities.PointDetailsActivity.STATISTICS_MODE_SAVE_KEY";
	public static final String STATISTICS_FROM_TIME_SAVE_KEY = "com.itdoors.haccp.activities.PointDetailsActivity.STATISTICS_FROM_TIME_SAVE_KEY";
	public static final String STATISTICS_TO_TIME_SAVE_KEY = "com.itdoors.haccp.activities.PointDetailsActivity.STATISTICS_TO_TIME_SAVE_KEY";
	public static final String STATUSES_SAVE_KEY = "com.itdoors.haccp.activities.PointDetailsActivity.STATUSES_SAVE_KEY";
	public static final String TITLE_SAVE_KEY = "com.itdoors.haccp.activities.PointDetailsActivity.TITLE_SAVE_KEY";
	
	
	protected static final String IS_LOADING_FAILED = "com.itdoors.haccp.activities.PointDetailsActivity.IS_LOADING_FAILED";
	protected static final String CHOOSE_TIME_RANGE_TYPE_DIALOG = "com.itdoors.haccp.activities.PointDetailsActivity.CHOOSE_TIME_RANGE_TYPE_DIALOG";
	protected static final String CHOOSE_CUSTOM_TIME_RANGE_DIALOG = "com.itdoors.haccp.activities.PointDetailsActivity.CHOOSE_CUSTOM_TIME_RANGE_DIALOG";
	
	protected final static int TIME_RANGE_REQUEST = 0x0abc;
	protected final static int ADD_STATISTICS_REQUEST = 0x0abd;
	
	protected static final int POINT_INFO_AND_STATICTIS_CODE = 1;
	protected static final int POINT_STATICTIS_FROM_TIME_RANGE_CODE = 2;
	protected static final int POINT_GROUP_CHARACTERIISTICS_CODE = 3;
	protected static final int REFRESH_STATICTIS_CODE = 4;
	protected static final int POINT_STATUSES_CODE = 5;
	
	protected static final String ARGS_PIAS_URI = "com.itdoors.haccp.activities.PointDetailsActivity.ARGS_PIAS_URI";
	protected static final String ARGS_PIAS_PARAMS_URI = "com.itdoors.haccp.activities.PointDetailsActivity.ARGS_PIAS_PARAMS_URI";
	
	
	protected String DETAILS_FRAGMENT_TAG = "com.itdoors.haccp.activities.PointDetailsActivity.DETAILS_FRAGMENT_TAG";
	protected String SHORT_FRAGMENT_TAG = "com.itdoors.haccp.activities.PointDetailsActivity.SHORT_FRAGMENT_TAG";
	
	protected Fragment mControlPoinDetailsFragment;
	
	protected ActionMode mActionMode;
	
	//Resulting content taken from REST AsyncTaskLoader
	protected Object mContent;
	
	// is Loading was failed;
	protected boolean loadingFailed = false;
	
	
	private StatisticsFragmentV0.MODE mStatisticFragmentMode;
	private String fromTimeStatisticsTimeStamp;
	private String toTimeStatisticsTimeStamp;
	
	
	private ArrayList<PointStatus> mStatusesList;
	
	public static Intent newInstance(Activity activity, Point point) {
		
		Intent intent = new Intent(activity, PointDetailsActivityV0.class);
		intent.putExtra(POINT_KEY, point);
		return intent;
	
	}
	
	public static Intent newInstance(Activity activity, int id) {
		Intent intent = new Intent(activity,PointDetailsActivityV0.class);
		intent.putExtra(POINT_ID_KEY,id);
		return intent;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		requestWindowFeature(com.actionbarsherlock.view.Window.FEATURE_INDETERMINATE_PROGRESS);
		super.onCreate(savedInstanceState);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		setSupportProgressBarIndeterminateVisibility(false);
		
		setContentView(R.layout.activity_control_point);
		mStatisticFragmentMode = MODE.GENERAL;
		String title = getResources().getString(R.string.control_point);
		
		if (savedInstanceState != null){
			
			restoreContent(savedInstanceState);
			loadingFailed = savedInstanceState.getBoolean(IS_LOADING_FAILED);
			mStatisticFragmentMode = (MODE)savedInstanceState.getSerializable(STATISTICS_MODE_SAVE_KEY);
			fromTimeStatisticsTimeStamp = savedInstanceState.getString(STATISTICS_FROM_TIME_SAVE_KEY);
			toTimeStatisticsTimeStamp = savedInstanceState.getString(STATISTICS_TO_TIME_SAVE_KEY);
			mStatusesList = (ArrayList<PointStatus>)savedInstanceState.getSerializable(STATUSES_SAVE_KEY);
			title = savedInstanceState.getString(TITLE_SAVE_KEY);
		}
		setTitle(title);
		
		Logger.Logi(getClass(), "previously loading failed: " + loadingFailed);

		
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
		
		//create clear empty ui fragments
		initFragments();
		
		//add loading or error view to screen
		initLoadingUI();
		
		//init rest content loader for loading ui content
		prepareLoading();
		
	}
	
	private void initLoadingUI(){
		
		if(mContent == null){
			if(loadingFailed) 
				 LoadActivityUtils.addErrorView(this, retryList);
			else LoadActivityUtils.addLoadingView(this);
		}
		
	}
	
	private final View.OnClickListener retryList = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			
			LoadActivityUtils.removeErrorView(PointDetailsActivityV0.this);
			LoadActivityUtils.addLoadingView(PointDetailsActivityV0.this);
			
			restartLoading();
		}
	};
	
	protected void restartLoading(){
		Logger.Logi(getClass(), "restartLoading");
		if( getIntent().getExtras() != null ){
			
			this.mContent = null;
			loadingFailed = false;
			
			int pointId = ((Integer)getIntent().getExtras().getSerializable(POINT_ID_KEY)).intValue();
			String url = Global.API_URL + "/point/" + Integer.toString(pointId);
			Uri loadPointInfoAndStatistic = Uri.parse(url);
			
		
		    Bundle args = new Bundle();
	        args.putParcelable(ARGS_PIAS_URI, loadPointInfoAndStatistic);
	        args.putParcelable(ARGS_PIAS_PARAMS_URI, null);
	        
	        getSupportLoaderManager().restartLoader(POINT_INFO_AND_STATICTIS_CODE, args, this);
	      
		}
	}
		
	
	
	
	
	
	protected void initFragments(){
		
			mControlPoinDetailsFragment = getSupportFragmentManager().findFragmentByTag(DETAILS_FRAGMENT_TAG);
			
			
			/*
			mControlpointShortInfoFragment = getSupportFragmentManager().findFragmentByTag(SHORT_FRAGMENT_TAG);
			
			FragmentTransaction mFragmentTransaction = null;
			if(mControlPoinDetailsFragment == null || mControlpointShortInfoFragment == null){
				mFragmentTransaction = getSupportFragmentManager().beginTransaction();
			}
			if(mControlPoinDetailsFragment == null){
				
				mControlPoinDetailsFragment = new PointDetailsFragment();
				mFragmentTransaction.add(R.id.cp_details, mControlPoinDetailsFragment, DETAILS_FRAGMENT_TAG);
				
			}
			if(mControlpointShortInfoFragment == null){
				mControlpointShortInfoFragment = new PointShortInfoFragment();
				mFragmentTransaction.add(R.id.cp_short, mControlpointShortInfoFragment, SHORT_FRAGMENT_TAG);
			}
			*/
			FragmentTransaction mFragmentTransaction = null;
			if(mControlPoinDetailsFragment == null){
				mFragmentTransaction = getSupportFragmentManager().beginTransaction();
			}
			if(mControlPoinDetailsFragment == null){
				mControlPoinDetailsFragment = new PointDetailsFragmentV0();
				mFragmentTransaction.add(R.id.cp_details, mControlPoinDetailsFragment, DETAILS_FRAGMENT_TAG);
			}
		
			if(mFragmentTransaction != null)
				mFragmentTransaction.commit();	
			
				
	}
	
	protected void fillFragments(){
		
		if(mContent != null){
			
			final Content content = (Content) mContent;
			
			if(content.point != null){
				if(content.point.getGroup() != null)
					setTitle(content.point.getGroup().getName());
			}
			
			if(mControlPoinDetailsFragment != null)
				((PointDetailsFragmentV0) mControlPoinDetailsFragment).fillUI(content);
			//if(mControlpointShortInfoFragment != null)
			//	((PointShortInfoFragment)mControlpointShortInfoFragment).fillUI(content);
		
		}
	}
	
	protected void changeStatusOnAttributes(PointStatus status){
		if(mControlPoinDetailsFragment != null){
			((PointDetailsFragmentV0) mControlPoinDetailsFragment).changeStatusOnAttributes(status);
		}
	}
	protected void updateStatisticsAfterAddRequestSuccess(){
		if(mContent != null){
			final Content content = (Content) mContent;
			if(mControlPoinDetailsFragment != null){
				((PointDetailsFragmentV0) mControlPoinDetailsFragment).updateStatisticsAfterAddRequestSuccess(content);
			}
		}
	}
	
	protected void updateStatisticsAfterTimeRangeLoadSuccess(){
		if(mContent != null){
			final Content content = (Content) mContent;
			if(mControlPoinDetailsFragment != null){
				((PointDetailsFragmentV0) mControlPoinDetailsFragment).updateStatisticsAfterTimeRangeLoadSuccess(content);
			}
		}
	}
	
	protected void updateStatisticsAfterRefreshSuccess(){
		if(mContent != null){
			final Content content = (Content) mContent;
			if(mControlPoinDetailsFragment != null){
				((PointDetailsFragmentV0) mControlPoinDetailsFragment).updateStatisticsAfterRefreshSuccess(content);
			}
		}
	}
	protected void refreshReshFailed(){
		if(mControlPoinDetailsFragment != null){
			((PointDetailsFragmentV0) mControlPoinDetailsFragment).refreshReshFailed();
		}
	}
	
	protected void scrollStatisticsToBottom(){
		if(mControlPoinDetailsFragment != null){
			((PointDetailsFragmentV0) mControlPoinDetailsFragment).scroolStatisticsToBottom();
		}
	}
	protected void scrollStatisticsToTop(){
		if(mControlPoinDetailsFragment != null){
			((PointDetailsFragmentV0) mControlPoinDetailsFragment).scroolStatisticsToTop();
		}
	}
	
	private void showAddStatisticActivity(StaticticsAddInputParser.Content content){
		if(content != null){
			
			Intent intent = new Intent(this, AddStatisticsActivity.class);
			intent.putExtra(AddStatisticsActivity.CHARACTERISTICS_TAG, content);
			intent.putExtra(AddStatisticsActivity.POINT_TAG, ((Content)mContent).point);
			intent.putExtra(AddStatisticsActivity.STATUSES_TAG, mStatusesList);
			startActivityForResult(intent, ADD_STATISTICS_REQUEST);
		
		}
			
	}
	
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		
		super.onSaveInstanceState(outState);
		saveContent(outState);
		outState.putBoolean(IS_LOADING_FAILED, loadingFailed);
		outState.putSerializable(STATISTICS_MODE_SAVE_KEY, mStatisticFragmentMode);
		outState.putString(STATISTICS_FROM_TIME_SAVE_KEY, fromTimeStatisticsTimeStamp);
		outState.putString(STATISTICS_TO_TIME_SAVE_KEY, toTimeStatisticsTimeStamp);
		outState.putSerializable(STATUSES_SAVE_KEY, mStatusesList);
		outState.putString(TITLE_SAVE_KEY, getTitle().toString());
		
	}
	
	public void saveContent(Bundle outState){
		
		if(mContent != null){
			
			final ControlPointParser.Content content = (ControlPointParser.Content)mContent;
			outState.putSerializable(POINT_CONTENT_SAVE_KEY, content.point);
			outState.putSerializable(STATISTICS_CONTENT_SAVE_KEY, (ArrayList<StatisticsRecord>)content.records);
		
		}
	}
	
	@SuppressWarnings("unchecked")
	public void restoreContent(Bundle savedInstanceState){
		
		final ControlPointParser.Content content =  new ControlPointParser.Content();
		content.point = (Point)savedInstanceState.getSerializable(POINT_CONTENT_SAVE_KEY);
		content.records = (ArrayList<StatisticsRecord>)savedInstanceState.getSerializable(STATISTICS_CONTENT_SAVE_KEY);
		
		if(content.point != null && content.records != null)
			mContent = content;
	
	}
	
	@SuppressLint("SimpleDateFormat")
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    if(resultCode == Activity.RESULT_OK){
	    	if(requestCode == TIME_RANGE_REQUEST){
	    		
	    		String fromTimeStamp = data.getStringExtra(Intents.CalendarTimeRange.FROM_TIME_STAMP);
	    		String toTimeStamp = data.getStringExtra(Intents.CalendarTimeRange.TO_TIME_STAMP);
	    		
	    		String fromTimeStampStr = new SimpleDateFormat(Global.usualDateFromat).format(new Date(Long.valueOf(fromTimeStamp)*1000)) .toString();
	    		String toTimeStampStr = new SimpleDateFormat(Global.usualDateFromat).format(new Date(Long.valueOf(toTimeStamp)*1000)) .toString();
		    	
	    		ToastUtil.ToastLong(this, getString(R.string.from) + " : " + fromTimeStampStr + ", " + getString(R.string.to) + " : " + toTimeStampStr);
	    		
	    		beginStatisticsFromTimeStampLoading(fromTimeStamp, toTimeStamp);
	    	
	    	}
	    	else if(requestCode == ADD_STATISTICS_REQUEST){
	    		
	    		StatisticsRecord record = (StatisticsRecord)data.getSerializableExtra(Intents.Statistic.STATISTIC_RECORD);
	    		
	    		if(record != null){
	    			
	    			record.setPoint(((Content)mContent).point);
	    			((Content)mContent).records.add(0,record);
	    			updateStatisticsAfterAddRequestSuccess();
	    			scrollStatisticsToTop();
	    		}
	    		
	    		PointStatus status = (PointStatus)data.getSerializableExtra(Intents.Status.CHANGED_STATUS);
	    		if(status != null){
	    			(((Content)mContent).point).setStatus(status);
	    			changeStatusOnAttributes(status);
	    		}
	    		
	    	}
	    }
	}
	
	private void beginStatisticsFromTimeStampLoading(String fromUnixTimeStamp, String toUnixTimeStamp){
		

		
		this.fromTimeStatisticsTimeStamp = fromUnixTimeStamp;
		this.toTimeStatisticsTimeStamp = toUnixTimeStamp;
		
		
		int pointId = ((Integer)getIntent().getExtras().getSerializable(POINT_ID_KEY)).intValue();
		
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
	
	
	private void beginRefreshStatistics(){
		
		if( getIntent().getExtras() != null ){
			
			int pointId = ((Integer)getIntent().getExtras().getSerializable(POINT_ID_KEY)).intValue();
			
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
	
	
	private void beginChacteristicsLoading(int groupId){
		
		String url = Global.API_URL + "/point.group/" + groupId +"/characteristics";
	    
		
		Uri loadCharacteristics = Uri.parse(url);
		
	    Bundle args = new Bundle();
        args.putParcelable(ARGS_PIAS_URI, loadCharacteristics);
        args.putParcelable(ARGS_PIAS_PARAMS_URI, null);
		showProgress();
	    
	    Loader<Object> loader = getSupportLoaderManager().getLoader(POINT_GROUP_CHARACTERIISTICS_CODE);
			if(  loader == null )
				 getSupportLoaderManager().initLoader(POINT_GROUP_CHARACTERIISTICS_CODE, args, this);
			else getSupportLoaderManager().restartLoader(POINT_GROUP_CHARACTERIISTICS_CODE, args, this);
		     
	}
	
	private void beginStatusesLoading(){
		
		
		String url = Global.API_URL + "/point.status";
	    
		Uri loadCharacteristics = Uri.parse(url);
		Bundle args = new Bundle();
        args.putParcelable(ARGS_PIAS_URI, loadCharacteristics);
        args.putParcelable(ARGS_PIAS_PARAMS_URI, null);
		showProgress();
	    
	    Loader<Object> loader = getSupportLoaderManager().getLoader(POINT_STATUSES_CODE);
			if(  loader == null )
				 getSupportLoaderManager().initLoader(POINT_STATUSES_CODE, args, this);
			else getSupportLoaderManager().restartLoader(POINT_STATUSES_CODE, args, this);
		
		
	}
	
	
	
	protected void showProgress(){
		setSupportProgressBarIndeterminateVisibility(true);
	}
	protected void hideProgress(){
		setSupportProgressBarIndeterminateVisibility(false);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		}
		switch (item.getItemId()) {
		
		}
		return super.onOptionsItemSelected(item);
	}

	
	public boolean onBottomPanelPressed(View item){
		
		switch (item.getId()) {
		
			case R.id.cp_bp_add_item:
				
				if(mStatusesList != null){
					
					int groupId = ((Content)mContent).point.getGroup().getId();
					beginChacteristicsLoading(groupId);
				
				}
				else{
					
					beginStatusesLoading();
				
				}
				
				return true;
			case R.id.cp_bp_params_item:
				ToastUtil.ToastLong(this, "Params");
				return true;
			case R.id.cp_bp_calendar_item:
				TimeRangeDialogFragment dialog = new TimeRangeDialogFragment();
				dialog.show(getSupportFragmentManager(), CHOOSE_TIME_RANGE_TYPE_DIALOG );
				return true;
				
			default:
				return false;
				
		}
		
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
            
        	ToastUtil.ToastLong(PointDetailsActivityV0.this, "Got click: " + item);
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
	

	
	public void prepareLoading(){
		
		if( getIntent().getExtras() != null && mContent == null){
			
			loadingFailed = false;
			
			int pointId = ((Integer)getIntent().getExtras().getSerializable(POINT_ID_KEY)).intValue();
			String url = Global.API_URL + "/point/" + Integer.toString(pointId);
			Uri loadPointInfoAndStatistic = Uri.parse(url);
			
		
		    Bundle args = new Bundle();
	        args.putParcelable(ARGS_PIAS_URI, loadPointInfoAndStatistic);
	        args.putParcelable(ARGS_PIAS_PARAMS_URI, null);
	        
	        getSupportLoaderManager().initLoader(POINT_INFO_AND_STATICTIS_CODE, args, this);
	      
		}
	}
		
	
	

	@Override
	public Loader<RESTLoader.RESTResponse> onCreateLoader(int id, Bundle args) {
		  
			if (args != null && args.containsKey(ARGS_PIAS_URI) && args.containsKey(ARGS_PIAS_PARAMS_URI)) {
	            
				Uri    action = args.getParcelable(ARGS_PIAS_URI);
	            Bundle params = args.getParcelable(ARGS_PIAS_PARAMS_URI);
	            
	            return new RESTLoader(this, RESTLoader.HTTPVerb.GET, action, params);
	        }
	        
	        return null;
	}

	@Override
	public void onLoadFinished(Loader<RESTLoader.RESTResponse> loader, RESTLoader.RESTResponse data) {
		  	
		    switch (loader.getId()) {
				
	        	case POINT_INFO_AND_STATICTIS_CODE:
	        		onInitLoadFinished(data);
	        		break;
	        	case POINT_STATICTIS_FROM_TIME_RANGE_CODE:
	        		onStaticsFromTimeRangeLoadFinished(data);
	        		break;
	        	case POINT_GROUP_CHARACTERIISTICS_CODE:
	        		onCharacteristicsLoadFinished(data);
	        		break;
	        	case POINT_STATUSES_CODE:
	        		onStatusesLoadFinished(data);
	        		
	        		Logger.Logi(getClass(), "Statuses loading finished...");
	        		break;
	        	case REFRESH_STATICTIS_CODE:
	        		onRefreshFinished(data);
	        		break;
	        	
	        	default:
	        		break;
			}
	        
	   
	}
	
	@Override
	public void onLoaderReset(Loader<RESTLoader.RESTResponse> loader) {
		
		switch (loader.getId()) {
			
	      	case POINT_INFO_AND_STATICTIS_CODE:
	      		break;
	      	case POINT_STATICTIS_FROM_TIME_RANGE_CODE:
	    		//clearFragments();
	      		break;
	      	case POINT_GROUP_CHARACTERIISTICS_CODE:
	      		break;
	      	case REFRESH_STATICTIS_CODE:
	      		break;
      	  	default:
	      		break;
		}
		
	}
	
	
	private void onRefreshFinished(RESTLoader.RESTResponse data){
		
		int    code = data.getCode();
        String json = data.getData();
        
        boolean failed = false;
        
        if (code == 200 && !json.equals("")) {
        	
        	LoadMoreStatisticsParser parser = new LoadMoreStatisticsParser();
        	try {
        		
				final Object content = parser.parse(json);
			
			
				com.itdoors.haccp.parser.LoadMoreStatisticsParser.Content loadedContent = (com.itdoors.haccp.parser.LoadMoreStatisticsParser.Content)content;
				
				((Content)mContent).hasMoreStatiscticItems = loadedContent.hasMoreStatiscticItems; 
				((Content)mContent).records = loadedContent.records;
				
				Logger.Logi(getClass(), "onStaticsFromTimeRangeLoadFinished: content: " + content.toString());
				
				
	    		this.mStatisticFragmentMode = MODE.GENERAL;
	    		
				//Show result in short and details fragments
				updateStatisticsAfterRefreshSuccess();
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
        	refreshReshFailed();
        }
	}
	
	private void onStatusesLoadFinished(RESTLoader.RESTResponse data){
		
		int    code = data.getCode();
        String json = data.getData();
    	
        if (code == 200 && !json.equals("")) {
        	
        	StatusesParser parser = new StatusesParser();
        	try {
        		
				final Object content = parser.parse(json);
				Logger.Logi(getClass(), "onStatusesLoadFinished, content: " + content.toString());
				
				mStatusesList = (ArrayList<PointStatus>)((StatusesParser.Content)content).records;
				
				int groupId = ((Content)mContent).point.getGroup().getId();
				beginChacteristicsLoading(groupId);
				
        	}
        	catch (JSONException e) {
				
        		hideProgress();
        		ToastUtil.ToastLong(this, "JSONException.");
				e.printStackTrace();
			
			}
        	catch (ServerFailedException e) {
        		hideProgress();
        		ToastUtil.ToastLong(this, "ServerFailedException.");
    	        e.printStackTrace();
    		}
        }
        else {
        	hideProgress();
        	Logger.Logi(getClass(), "code: " + code +"; json: " + json);
        	ToastUtil.ToastLong(this, getString(R.string.failed_to_load_data));
        }
	}
	
	private void onCharacteristicsLoadFinished(RESTLoader.RESTResponse data){
		int    code = data.getCode();
        String json = data.getData();
    	
        hideProgress();
        
        if (code == 200 && !json.equals("")) {
        	
        	StaticticsAddInputParser parser = new StaticticsAddInputParser();
        	try {
        		
				final Object content = parser.parse(json);
				Logger.Logi(getClass(), "onStaticsFromTimeRangeLoadFinished, content: " + content.toString());
				showAddStatisticActivity((StaticticsAddInputParser.Content)content);
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
	
	private void onStaticsFromTimeRangeLoadFinished (RESTLoader.RESTResponse data){
		
		int    code = data.getCode();
        String json = data.getData();
    	
        hideProgress();
        
        if (code == 200 && !json.equals("")) {
        	
        	PointStatisticsFromTimeRangeParser parser = new PointStatisticsFromTimeRangeParser();
        	try {
        		
				final Object content = parser.parse(json);
				//set the resulted parsed content into local variable
				com.itdoors.haccp.parser.PointStatisticsFromTimeRangeParser.Content loadedContent = (com.itdoors.haccp.parser.PointStatisticsFromTimeRangeParser.Content)content;
				
				((Content)mContent).hasMoreStatiscticItems = loadedContent.hasMoreStatiscticItems; 
				((Content)mContent).records = loadedContent.records;
				
				Logger.Logi(getClass(), "onStaticsFromTimeRangeLoadFinished: content: " + content.toString());
				
				
	    		this.mStatisticFragmentMode = MODE.FROM_TIME_RANGE;
	    		
				//Show result in short and details fragments
				updateStatisticsAfterTimeRangeLoadSuccess();
				
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
	private void onInitLoadFinished( RESTLoader.RESTResponse data){
		
		int    code = data.getCode();
        String json = data.getData();
        
        
        boolean failed  = false;
        
        if (code == 200 && !json.equals("")) {
        	
        	ControlPointParser parser = new ControlPointParser();
        	try {
        		
				final Object content = parser.parse(json);
				//set the resulted parsed content into local variable
				mContent = content;
				//remove loading view
				LoadActivityUtils.removeLoadingView(PointDetailsActivityV0.this);
				//Show result in short and details fragments
				fillFragments();
				
        	}
        	catch (JSONException e) {
				
        		ToastUtil.ToastLong(this, "JSONException.");
				e.printStackTrace();
				failed = true;
			
			}
        	catch (ServerFailedException e) {
        		ToastUtil.ToastLong(this, "ServerFailedException.");
    	        e.printStackTrace();
    	        failed = true;
			}
        	
     
        }
        else {
        	ToastUtil.ToastLong(this, getString(R.string.failed_to_load_data));
        	failed = true;
        }
       
        if(failed){
        	loadingFailed = true;
        	// remove loading view and add retry view
        	LoadActivityUtils.removeLoadingView(PointDetailsActivityV0.this);
        	LoadActivityUtils.addErrorView(PointDetailsActivityV0.this, retryList);
        	
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
			
			fromUnixTimeStamp = Long.toString(fromDate.getTime() / 1000);
			toUnixTimeStamp =  Long.toString(toDate.getTime() / 1000);
			
			String fromTimeStampStr = new SimpleDateFormat(Global.usualDateFromat).format(new Date(Long.valueOf(fromUnixTimeStamp)*1000)) .toString();
    		String toTimeStampStr = new SimpleDateFormat(Global.usualDateFromat).format(new Date(Long.valueOf(toUnixTimeStamp)*1000)) .toString();
			
    		String toastMess = getString(R.string.from) + " : " + fromTimeStampStr + " , " + getString(R.string.to) +" : " + toTimeStampStr;
        	if( today )		toastMess = getString(R.string.today) + " : " + fromTimeStampStr;
    		if( yesterday )	toastMess = getString(R.string.yesterday) + " : " + fromTimeStampStr;
    		
    		ToastUtil.ToastLong(this, toastMess);
    		
			beginStatisticsFromTimeStampLoading(fromUnixTimeStamp, toUnixTimeStamp);
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

	@Override
	public void onRefreshStarted(View view) {
		beginRefreshStatistics();
	}

	@Override
	public void onEditStaticticsItemContextMenuPressed(int position) {
		ToastUtil.ToastLong(this, getString(R.string.edit) + ":" + position);
	}

	@Override
	public void onDeleteStaticticsItemContextMenuPressed(int position) {
		ToastUtil.ToastLong(this, getString(R.string.delete) + ":" + position);
	}	
}
