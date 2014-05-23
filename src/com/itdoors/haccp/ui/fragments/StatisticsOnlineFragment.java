package com.itdoors.haccp.ui.fragments;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONException;

import uk.co.senab.actionbarpulltorefresh.extras.actionbarsherlock.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

import com.itdoors.haccp.Global;
import com.itdoors.haccp.Intents;
import com.itdoors.haccp.R;
import com.itdoors.haccp.exceptions.ServerFailedException;
import com.itdoors.haccp.loaders.RESTLoader;
import com.itdoors.haccp.model.StatisticsRecord;
import com.itdoors.haccp.model.StatististicsItemStatus;
import com.itdoors.haccp.parser.LoadMoreStatisticsParser;
import com.itdoors.haccp.parser.LoadMoreStatisticsParser.Content;
import com.itdoors.haccp.ui.interfaces.OnContextMenuItemPressedListener;
import com.itdoors.haccp.ui.interfaces.OnLongStatisticsItemPressedListener;
import com.itdoors.haccp.utils.LoadActivityUtils;
import com.itdoors.haccp.utils.Logger;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class StatisticsOnlineFragment extends EndlessListFragment implements LoaderCallbacks<RESTLoader.RESTResponse>  {

    protected static final String ARGS_PIAS_URI 			 = "com.itdoord.haccp.fragments.StatisticsFragment.ARGS_PIAS_URI";
	protected static final String ARGS_PIAS_PARAMS_URI 		 = "com.itdoord.haccp.fragments.StatisticsFragment.ARGS_PIAS_PARAMS_URI";
	
	protected static final String ARGS_DONT_START 			 = "com.itdoord.haccp.fragments.StatisticsFragment.ARGS_DONT_START";
	protected static final String ARGS_HAS_PRELOADED_CONTENT = "com.itdoord.haccp.fragments.StatisticsFragment.ARGS_HAS_PRELOADED_CONTENT";
	protected static final String ARGS_PRELOADED_CONTENT 	 = "com.itdoord.haccp.fragments.StatisticsFragment.ARGS_PRELOADED_CONTENT";
	protected static final String ARGS_HAS_MORE 			 = "com.itdoord.haccp.fragments.StatisticsFragment.ARGS_HAS_MORE";
	protected static final String ARGS_ACTION 				 = "com.itdoord.haccp.fragments.StatisticsFragment.ARGS_ACTION";
	
	private static final String SAVE_PRELOADED_CONTENT_SET   = "com.itdoord.haccp.fragments.StatisticsFragment.SAVE_PRELOADED_CONTENT_SET";
	
	private static final int STATICTIS_MORE_CODE   = 1;
	
	public static final int ACTION_UPDATE_AFTER_TIME_RANGE_WITH_PRELOADED_CONTENT_CODE = 0;
    public static final int ACTION_REFRESH_WITH_PRELOADED_CONTENT = 1;
    
	// exponential back-off params
	private final static long MIN_RETRY_INTERVAL = 500; 
	private final static long MAX_RETRY_INTERVAL = 5000;
	private final static int MAX_RETRY_COUNT = 5;
	
	private int retryCount = 0;
	private long retryInterval = MIN_RETRY_INTERVAL;
    
    public interface TimeRangeParametersHolder{
    	public String getFromTimeInTimeStamp();
    	public String getToTimeInTimeStamp();
    }
    
    public interface StatisticsListModeHolder{
    	public MODE getMode();
    }
    
    public static enum MODE {
		GENERAL, FROM_TIME_RANGE;
		
	}
    
	@SuppressWarnings("unused")
	private OnLongStatisticsItemPressedListener mOnLongStatisticsItemPressedListener;
	private TimeRangeParametersHolder mTimeRangeParametersHolder;
	private StatisticsListModeHolder mStatisticsListModeHolder;
	private OnContextMenuItemPressedListener mOnContextMenuItemPressedListener;
	private OnRefreshListener mOnRefreshListener;
	
	private PullToRefreshLayout mPullToRefreshLayout;

	private boolean isPreLoadedContentSet = false;
	
	public static StatisticsOnlineFragment newInstance(boolean dontStart) {
		StatisticsOnlineFragment f = new StatisticsOnlineFragment();
		Bundle args = new Bundle();
		args.putBoolean(ARGS_DONT_START, dontStart);
		f.setArguments(args);
		return f;
	}
	
	public static StatisticsOnlineFragment newInstance(int action, ArrayList<StatisticsRecord> preloadedContent, boolean hasMore){
		
		StatisticsOnlineFragment f = new StatisticsOnlineFragment();
		
		Bundle args = new Bundle();
		args.putBoolean(ARGS_DONT_START, true);
		args.putBoolean(ARGS_HAS_PRELOADED_CONTENT, true);
		args.putInt(ARGS_ACTION, action);
		args.putSerializable(ARGS_PRELOADED_CONTENT, preloadedContent);
		args.putBoolean(ARGS_HAS_MORE, hasMore);
		f.setArguments(args);
		return f;
		
	}
	
	private boolean isDontStartInArgs(){
		return getArguments() != null && getArguments().getBoolean(ARGS_DONT_START);
	}
	private boolean hasPreloadedContentInArgs(){
		return getArguments() != null && getArguments().getBoolean(ARGS_HAS_PRELOADED_CONTENT);
	}
	

	@Override
	protected void loadMoreResults() {
		
		if(mStream != null ){
			
			int lastId = -1;
			if(!mStream.isEmpty()){
				int position = mStream.size() - 1;
				StatisticsRecord last = (StatisticsRecord)mStream.get(position);
				lastId = last.getId();
			}
			if(mStatisticsListModeHolder != null){
				MODE mode = mStatisticsListModeHolder.getMode();
				switch (mode) {
					case GENERAL:
						beginLoadMore(lastId);
					break;
					case FROM_TIME_RANGE:
						if(mTimeRangeParametersHolder != null){
							
							String fromTime = mTimeRangeParametersHolder.getFromTimeInTimeStamp();
							String toTime = mTimeRangeParametersHolder.getToTimeInTimeStamp();
							
							beginLoadMore(lastId, fromTime, toTime);
						}
					break;
				}
			}
		}
	}
	
	
	
	private void beginLoadMore(int lastStatisticsId){
		if(getActivity() == null)
		return;

		int pointId = getActivity().getIntent().getIntExtra(Intents.Point.UID, -1);
		if(pointId == -1)
		return;
		
		String url;
		if(lastStatisticsId > -1)
			 url = Global.API_URL +	"/point/" + Integer.toString(pointId) + 
										"/statistics/" + lastStatisticsId;
		else
			 url = Global.API_URL +	"/point/" + Integer.toString(pointId) + 
				"/statistics";
		
		Uri loadStatistic = Uri.parse(url);
		
	    Bundle args = new Bundle();
        args.putParcelable(ARGS_PIAS_URI, loadStatistic);
        args.putParcelable(ARGS_PIAS_PARAMS_URI, null);
        
        if(getActivity() != null){
        	
        	Loader<Object> loader = getActivity().getSupportLoaderManager().getLoader(STATICTIS_MORE_CODE);
			if(  loader == null )
				 getActivity().getSupportLoaderManager().initLoader(STATICTIS_MORE_CODE, args, this);
			else getActivity().getSupportLoaderManager().restartLoader(STATICTIS_MORE_CODE, args, this);
        
        }
	}
	
	private void beginLoadMore(int lastStatisticsId, String fromUnixTimeStamp, String toUnixTimeStamp){
		if(getActivity() == null)
		return;
		
		int pointId = getActivity().getIntent().getIntExtra(Intents.Point.UID, -1);
		if(pointId == -1)
		return;
		
		String url = Global.API_URL + "/point/" + Integer.toString(pointId) + 
									  "/statistics/" + fromUnixTimeStamp +"/" + toUnixTimeStamp + "/" + lastStatisticsId;
		
		Uri loadStatistic = Uri.parse(url);
		
	    Bundle args = new Bundle();
        args.putParcelable(ARGS_PIAS_URI, loadStatistic);
        args.putParcelable(ARGS_PIAS_PARAMS_URI, null);
        
        if(getActivity() != null){
        	
        	Loader<Object> loader = getActivity().getSupportLoaderManager().getLoader(STATICTIS_MORE_CODE);
			if(  loader == null )
				 getActivity().getSupportLoaderManager().initLoader(STATICTIS_MORE_CODE, args, this);
			else getActivity().getSupportLoaderManager().restartLoader(STATICTIS_MORE_CODE, args, this);
        
        }
	}
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean(SAVE_PRELOADED_CONTENT_SET, isPreLoadedContentSet);
	}
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try{
			mOnLongStatisticsItemPressedListener = (OnLongStatisticsItemPressedListener) activity;
			mTimeRangeParametersHolder = (TimeRangeParametersHolder) activity;
			mStatisticsListModeHolder = (StatisticsListModeHolder) activity;
			mOnRefreshListener = (OnRefreshListener)activity;
			mOnContextMenuItemPressedListener = (OnContextMenuItemPressedListener)activity;
		}
		catch(ClassCastException e){
			 throw new ClassCastException(activity.toString() + " must implement OnLongStatisticsItemPressedListener, " +
			 		"TimeRangeParametersHolder, StatisticsListModeHolder, OnRefreshListener, OnContextMenuItemPressedListener");
		}
	}
	
	private static final int EDIT_ID = Menu.FIRST + 3;
	private static final int DELETE_ID = Menu.FIRST + 4;
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		
		menu.setHeaderTitle(R.string.action);
		menu.add(Menu.NONE, EDIT_ID, Menu.NONE, R.string.edit);
		menu.add(Menu.NONE, DELETE_ID, Menu.NONE, R.string.delete);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		int position = ((AdapterView.AdapterContextMenuInfo)item.getMenuInfo()).position;
		int id = item.getItemId();
		
		switch (id) {
			case EDIT_ID:
				if(mOnContextMenuItemPressedListener != null)
					mOnContextMenuItemPressedListener.onEditStaticticsItemContextMenuPressed(position);
				return true;
			case DELETE_ID:
				if(mOnContextMenuItemPressedListener != null)
					mOnContextMenuItemPressedListener.onDeleteStaticticsItemContextMenuPressed(position);
				return true;
		}
		return super.onContextItemSelected(item);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View root = super.onCreateView(inflater, container, savedInstanceState);
		root.setBackgroundColor(Color.WHITE);
	    return root;
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		
		final ListView listView = getListView();
		listView.setSelector(R.drawable.abs__tab_indicator_ab_holo);
	    listView.setCacheColorHint(Color.WHITE);
		listView.setDrawSelectorOnTop(true);
		listView.setTranscriptMode(ListView.TRANSCRIPT_MODE_DISABLED);
		
		mStreamAdapter =  new StatisticListAdapter(getActivity());
		setListAdapter(mStreamAdapter);
		registerForContextMenu(getListView());
		
		if(mStream != null && mStream.isEmpty() && !isDontStartInArgs())
			fillStatistics();
		
		  // Adding pullToRefresh 
		  ViewGroup viewGroup = (ViewGroup) view;
          mPullToRefreshLayout = new PullToRefreshLayout(viewGroup.getContext());
          ActionBarPullToRefresh.from(getActivity())
                  .insertLayoutInto(viewGroup)
                  .theseChildrenArePullable(android.R.id.list)
                  .listener(mOnRefreshListener)
                  .setup(mPullToRefreshLayout);
        
        if(savedInstanceState != null){
        	isPreLoadedContentSet = savedInstanceState.getBoolean(SAVE_PRELOADED_CONTENT_SET);
        }
        
        if(hasPreloadedContentInArgs() && !isPreLoadedContentSet){
        	
        	int action = getArguments().getInt(ARGS_ACTION);
        	boolean hasMore = getArguments().getBoolean(ARGS_HAS_MORE);
			@SuppressWarnings("unchecked")
			ArrayList<StatisticsRecord> records = 
				(ArrayList<StatisticsRecord>)getArguments().getSerializable(ARGS_PRELOADED_CONTENT);
		
        	switch (action) {
				case ACTION_UPDATE_AFTER_TIME_RANGE_WITH_PRELOADED_CONTENT_CODE:
					updateStatisticsAfterTimeRangeLoadSuccess(records, hasMore);
				break;
				case ACTION_REFRESH_WITH_PRELOADED_CONTENT:
					updateStatisticsAfterRefreshSuccess(records, hasMore);
				break;
			}
        	
        	setState(StreamingState.LOADING);
        	isPreLoadedContentSet = true;
        }
      	
	}
	
	private class StatisticListAdapter extends BaseAdapter{
			 
		private LayoutInflater mLayoutInflater;
		
		public StatisticListAdapter(Context context) {
			mLayoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		@Override
		public int getCount() {
			return mStream.size();
		}
		@Override
		public Object getItem(int position) {
			return mStream.get(position);
		}
		@Override
		public long getItemId(int position) {
			return position;
		}
		
		@SuppressLint("SimpleDateFormat")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			ViewHolder holder;
			if(convertView==null){
				
				convertView = mLayoutInflater.inflate(R.layout.list_item_statistics, parent, false);
				
				holder = new ViewHolder();
				holder.inspector = (TextView)convertView.findViewById(R.id.cp_owner);
				holder.status = (TextView)convertView.findViewById(R.id.cp_status);
				holder.characteristicGroupName = (TextView)convertView.findViewById(R.id.cp_char_name);
				holder.value = (TextView)convertView.findViewById(R.id.cp_value);
				holder.date = (TextView)convertView.findViewById(R.id.cp_date);
				
				convertView.setTag(holder);
			}
			else{
				holder = (ViewHolder)convertView.getTag();
			}
			
			if(!mStream.isEmpty()){
				
				StatisticsRecord statistics = (StatisticsRecord)mStream.get(position);
				if(statistics == null)	return convertView;
				
				double value = statistics.getValue();
				double valueTop = statistics.getGroupCharacteristics() == null ? 100 : statistics.getGroupCharacteristics().getCriticalTopValue();
				double valueBottom = statistics.getGroupCharacteristics() == null ? 0 : statistics.getGroupCharacteristics().getCriticalBottomValue();
				
				
				StatististicsItemStatus cpStatus = StatististicsItemStatus.APPROVED;
				
				if(statistics.getGroupCharacteristics() != null){
					
					if(value <= valueBottom)
						cpStatus = StatististicsItemStatus.APPROVED;
					else if( value > valueBottom && value < valueTop )
						cpStatus = StatististicsItemStatus.WARNING;
					else if( value >= valueTop)
						cpStatus = StatististicsItemStatus.DANGER;
				
				}
				
				if(isAdded()){
					
					String warning = getString(StatististicsItemStatus.WARNING.getStringResourceID());
					String danger = getString(StatististicsItemStatus.DANGER.getStringResourceID());
					String approved = getString(StatististicsItemStatus.APPROVED.getStringResourceID());
					
					switch (cpStatus) {
						case WARNING:
							holder.status.setText(warning);
							holder.status.setBackgroundResource(R.color.status_warning);
						break;
						case DANGER:
							holder.status.setText(danger);
							holder.status.setBackgroundResource(R.color.status_danger);
						break;
						default:
							holder.status.setText(approved);
							holder.status.setBackgroundResource(R.color.status_approved);
						break;
					}
					
					String groupNameStr  = statistics.getGroupCharacteristics() == null ? "-" : statistics.getGroupCharacteristics().getName();
					String unit = statistics.getGroupCharacteristics() == null ? "%" : statistics.getGroupCharacteristics().getUnit();
					String valueStr = Integer.toString((int)statistics.getValue()) + unit;
					String whoSetStr = "������������";
					Date date = statistics.getEntryDate();
					
					String dateStr = date== null ? "-" : new SimpleDateFormat(Global.usualDateFromat).format(date) .toString();
					
					holder.inspector.setText(whoSetStr);
					holder.characteristicGroupName.setText(groupNameStr);
					holder.value.setText(valueStr);
					holder.date.setText(dateStr);
					
				}
					int backgroundResources = (position % 2 == 0) ? R.drawable.abs__ab_solid_light_holo : R.drawable.abs__ab_solid_shadow_holo;
					((RelativeLayout)convertView).setBackgroundResource(backgroundResources);
			}
			return convertView;
		}
		
		private class ViewHolder{
			TextView inspector;
			TextView status;
			TextView characteristicGroupName;
			TextView value;
			TextView date;
		}
		
	  }
	
	public void restoreStatistics(List<StatisticsRecord> records){
	
		mStream.addAll(records);
		mStreamAdapter.notifyDataSetChanged();
		
		if(records != null && !records.isEmpty())
			LoadActivityUtils.removeEmptyViewIfExist(this);
		else
			LoadActivityUtils.addEmptyViewIfNotExist(this, getString(R.string.no_statistic_items));
	}
	
	public void fillStatistics(){
		setState(StreamingState.LOADING );
		load();
	}
	
	public void fillStatistics(List<StatisticsRecord> records, boolean hasMoreItems) {
		
		mStream.addAll(records);
		mStreamAdapter.notifyDataSetChanged();
	
		setState(hasMoreItems ? StreamingState.LOADING : StreamingState.COMPLETE);
		
		if(getStreamingState() == StreamingState.LOADING)
			load();
		
		if(records == null || records.isEmpty())
			LoadActivityUtils.addEmptyViewIfNotExist(this, getString(R.string.no_statistic_items));
		
	}
	
	public void updateStatisticsAfterAddRequestSuccess( List<StatisticsRecord> records, Boolean hasMoreStatiscticItems) {
		
		mStream.clear();
		mStream.addAll(records);
		mStreamAdapter.notifyDataSetChanged();
		
		if(records != null && !records.isEmpty())
			LoadActivityUtils.removeEmptyViewIfExist(this);
		else
			LoadActivityUtils.addEmptyViewIfNotExist(this, getString(R.string.no_statistic_items));
	}

	public void updateStatisticsAfterTimeRangeLoadSuccess(List<StatisticsRecord> records, Boolean hasMoreStatiscticItems) {
		
		mStream.clear();
		mStream.addAll(records);
		mStreamAdapter.notifyDataSetChanged();
		
		setState(hasMoreStatiscticItems ? StreamingState.LOADING : StreamingState.COMPLETE);
		
		if(getStreamingState() == StreamingState.LOADING)
			load();
		
		if(records != null && !records.isEmpty())
			LoadActivityUtils.removeEmptyViewIfExist(this);
		else
			LoadActivityUtils.addEmptyViewIfNotExist(this, getString(R.string.no_statistic_items));
	}
	
	public void updateStatisticsAfterRefreshSuccess(List<StatisticsRecord> records, Boolean hasMoreStatiscticItems) {
		
		mStream.clear();
		mStream.addAll(records);
		mStreamAdapter.notifyDataSetChanged();
		
		setState(hasMoreStatiscticItems ? StreamingState.LOADING : StreamingState.COMPLETE);
		
		if(getStreamingState() == StreamingState.LOADING)
			load();
		
		if(records != null && !records.isEmpty())
			LoadActivityUtils.removeEmptyViewIfExist(this);
		else
			LoadActivityUtils.addEmptyViewIfNotExist(this, getString(R.string.no_statistic_items));
		
		if(mPullToRefreshLayout != null) 
			mPullToRefreshLayout.setRefreshComplete();
	}
	
	public void refreshReshFailed() {
		if(mPullToRefreshLayout != null) 
			mPullToRefreshLayout.setRefreshComplete();
	}
	
	public void clearStatistics() {
		setState(StreamingState.INIT);
		mStream.clear();
		mStreamAdapter.notifyDataSetChanged();
		LoadActivityUtils.addEmptyViewIfNotExist(this, getString(R.string.no_statistic_items));
	}
	
	public void scrollListToBottom() {
		if(mStreamAdapter != null & getListView() != null)
	    getListView().post(new Runnable() {
	        @Override
	        public void run() {
	            // Select the last row so it will scroll into view...
	            getListView().setSelection(mStreamAdapter.getCount() - 1);
	        }
	    });
	}

	public void scrollListToTop() {
		if(mStreamAdapter != null & getListView() != null)
		    getListView().post(new Runnable() {
		        @Override
		        public void run() {
		            getListView().setSelection(0);
		        }
		    });
	}

	@Override
	public Loader<RESTLoader.RESTResponse> onCreateLoader(int id, Bundle args) {
		  
			if (args != null && args.containsKey(ARGS_PIAS_URI) && args.containsKey(ARGS_PIAS_PARAMS_URI)) {
	        	Uri    action = args.getParcelable(ARGS_PIAS_URI);
	            Bundle params = args.getParcelable(ARGS_PIAS_PARAMS_URI);
	            return new RESTLoader(getActivity(), RESTLoader.HTTPVerb.GET, action, params);
	        }
	        return null;
	}
	@Override
	public void onLoadFinished(Loader<RESTLoader.RESTResponse> loader, RESTLoader.RESTResponse data) {
		if(loader.getId() == STATICTIS_MORE_CODE) {
			onLoadMoreCompleted(data);
	   }
	}
	@Override
	public void onLoaderReset(Loader<RESTLoader.RESTResponse> loader) {}
	
	private void onLoadMoreCompleted( RESTLoader.RESTResponse data ){
		
		int    code = data.getCode();
        String json = data.getData();
        
        boolean failed  = false;
        if (code == 200) {
        	LoadMoreStatisticsParser parser = new LoadMoreStatisticsParser();
        	try {
        		final Content content = (Content) parser.parse(json);
				boolean hasMoreResults = content.hasMoreStatiscticItems;
				setState(hasMoreResults ? StreamingState.DONE : StreamingState.COMPLETE);
				onListPackageReady(content.records);
			}
        	catch (JSONException e) {
	    		if(getActivity() != null)
        		 e.printStackTrace();
				failed = true;
			}
        	catch (ServerFailedException e) {
        		if(getActivity() != null)
        		e.printStackTrace();
    	        failed = true;
			}
        }
        else {
        	if(getActivity() != null){
        		failed = true;
        	}
        }
        if(failed){
        
        	// simple exponential back-off
        	retryInterval = 2 * retryInterval < MAX_RETRY_INTERVAL ? 2 * retryInterval : MAX_RETRY_INTERVAL;
        	if(retryCount < MAX_RETRY_COUNT){
        		Handler handler = new Handler();
        		handler.postDelayed( new Runnable() {
					@Override
					public void run() {
						Logger.Logi(getClass(), "retry : " + "count: "  + retryCount + ", interval:" + retryInterval);
		        		setState(StreamingState.REPEAT);
		        		loadMoreResults();
						retryCount++;
					}
				}, retryInterval);
        	}
        	else{
        		retryCount = 0;
        		retryInterval = MIN_RETRY_INTERVAL;
        		// remove loading view and add retry view
        		onError();
        	}
        }
	}

	
	
	
	
}