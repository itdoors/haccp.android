package com.itdoors.haccp.ui.fragments;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;


import com.itdoors.haccp.Intents;
import com.itdoors.haccp.R;
import com.itdoors.haccp.model.StatististicsItemStatus;
import com.itdoors.haccp.model.rest.retrofit.Characteristic;
import com.itdoors.haccp.model.rest.retrofit.MoreStatistics;
import com.itdoors.haccp.model.rest.retrofit.SpiceRequestInfo;
import com.itdoors.haccp.model.rest.retrofit.Statistic;
import com.itdoors.haccp.rest.robospice_retrofit.GetStatisticsRequest;
import com.itdoors.haccp.rest.robospice_retrofit.GetStatisticsRequest.Builder;
import com.itdoors.haccp.ui.activities.PointDetailsActivity;
import com.itdoors.haccp.ui.interfaces.OnContextMenuItemPressedListener;
import com.itdoors.haccp.ui.interfaces.OnLongStatisticsItemPressedListener;
import com.itdoors.haccp.utils.DateUtils;
import com.itdoors.haccp.utils.LoadActivityUtils;
import com.itdoors.haccp.utils.Logger;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.PendingRequestListener;
import com.octo.android.robospice.request.listener.RequestListener;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
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


public class StatisticsOnlineFragment extends EndlessListFragment {

    protected static final String ARGS_PIAS_URI 			 = "com.itdoord.haccp.fragments.StatisticsOnlineFragment.ARGS_PIAS_URI";
	protected static final String ARGS_PIAS_PARAMS_URI 		 = "com.itdoord.haccp.fragments.StatisticsOnlineFragment.ARGS_PIAS_PARAMS_URI";
	
	protected static final String ARGS_DONT_START 			 = "com.itdoord.haccp.fragments.StatisticsOnlineFragment.ARGS_DONT_START";
	protected static final String ARGS_HAS_PRELOADED_CONTENT = "com.itdoord.haccp.fragments.StatisticsOnlineFragment.ARGS_HAS_PRELOADED_CONTENT";
	protected static final String ARGS_PRELOADED_CONTENT 	 = "com.itdoord.haccp.fragments.StatisticsOnlineFragment.ARGS_PRELOADED_CONTENT";
	protected static final String ARGS_HAS_MORE 			 = "com.itdoord.haccp.fragments.StatisticsOnlineFragment.ARGS_HAS_MORE";
	protected static final String ARGS_ACTION 				 = "com.itdoord.haccp.fragments.StatisticsOnlineFragment.ARGS_ACTION";
	
	// exponential back-off params
	private final static long MIN_RETRY_INTERVAL = 500; 
	private final static long MAX_RETRY_INTERVAL = 5000;
	private final static int MAX_RETRY_COUNT = 5;
	
	public static final int TIME_RANGE_ACTION = 0;
	public static final int REFRESH_ACTION = 1;
	
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
	
	private StatisticListAdapter mStreamAdapter;
	
	private List<Object> stream = new ArrayList<Object>();
	
	private final long  loadMoreRequestDuration = 10 * DurationInMillis.ONE_MINUTE;
	private SpiceRequestInfo<MoreStatistics> lastLoadMoreSpiceRequestInfo;
	
	public static StatisticsOnlineFragment newInstance(int action, ArrayList<Statistic> preloadedContent, boolean hasMore){
		
		StatisticsOnlineFragment f = new StatisticsOnlineFragment();
		
		Bundle args = new Bundle();
		args.putBoolean(ARGS_DONT_START, true);
		args.putInt(ARGS_ACTION, action);
		args.putBoolean(ARGS_HAS_PRELOADED_CONTENT, true);
		args.putSerializable(ARGS_PRELOADED_CONTENT, preloadedContent);
		args.putBoolean(ARGS_HAS_MORE, hasMore);
		f.setArguments(args);
		return f;
		
	}
	
	private boolean hasPreloadedContent(){
		return getArguments() != null && getArguments().getBoolean(ARGS_HAS_PRELOADED_CONTENT);
	}

	@Override
	protected void loadMoreResults() {
		
		if(mStreamAdapter != null ){
			
			int lastId = -1;
			if(!mStreamAdapter.isEmpty()){
				int position = mStreamAdapter.getCount() - 1;
				Statistic last = (Statistic)mStreamAdapter.getItem(position);
				lastId = last.getId();
			}
			if(mStatisticsListModeHolder != null){
				MODE mode = mStatisticsListModeHolder.getMode();
				switch (mode) {
					case GENERAL:
						loadMore(lastId);
					break;
					case FROM_TIME_RANGE:
						if(mTimeRangeParametersHolder != null){
							
							String fromTime = mTimeRangeParametersHolder.getFromTimeInTimeStamp();
							String toTime = mTimeRangeParametersHolder.getToTimeInTimeStamp();
							
							loadMore(lastId, fromTime, toTime);
						}
					break;
				}
			}
		}
	}
	
	
	
	private RequestListener<MoreStatistics> mLoadMoreStatisctics = new RequestListener<MoreStatistics>() {

		@Override
		public void onRequestFailure(SpiceException exception) {
			tryExponetialBackOff();
		}

		@Override
		public void onRequestSuccess(MoreStatistics statistics) {
			Logger.Loge(getClass(), "from callback");
			onLoadMoreSuccess(statistics);
		}
	};
	
	private PendingRequestListener<MoreStatistics> mLoadMorePendingStatisctics = new PendingRequestListener<MoreStatistics>() {

		@Override
		public void onRequestFailure(SpiceException exception) {
			tryExponetialBackOff();
		}

		@Override
		public void onRequestSuccess(MoreStatistics statistics) {
			Logger.Loge(getClass(), "from pending callback");
			if(getStreamingState() == StreamingState.LOADING){
				onLoadMoreSuccess(statistics);
			}
		}

		@Override
		public void onRequestNotFound() {}
	};
	private RequestListener<MoreStatistics> mLoadMoreFromCacheRequestListener = new RequestListener<MoreStatistics>() {

		@Override
		public void onRequestFailure(SpiceException exception) {
			tryExponetialBackOff();
		}
		@Override
		public void onRequestSuccess(MoreStatistics statistics) {
			Logger.Loge(getClass(), "from cache callback");
			if(getStreamingState() == StreamingState.LOADING){
				onLoadMoreSuccess(statistics);
			}
		}
		
	};
	
	private void onLoadMoreSuccess(MoreStatistics statistics){
		if(statistics != null){
			boolean hasMoreResults = statistics.getMore();
			setState(hasMoreResults ? StreamingState.DONE : StreamingState.COMPLETE);
			onListPackageReady(statistics.getStatistics());
			if(getStreamingState() == StreamingState.COMPLETE)
			  addOrRemoveEmptyView(statistics.getStatistics());
		}
	}
	
	private void loadMore(int lastStatisticsId){
		if(getActivity() != null){
			Bundle extra = getActivity().getIntent().getExtras();
			if(extra != null){
				int pointId = extra.getInt(Intents.Point.UID);
				SpiceManager spiceManager = ((PointDetailsActivity)getActivity()).getSpiceManager();
				if(spiceManager != null){
					Builder builder = new GetStatisticsRequest.Builder().setId(pointId);
					if(lastStatisticsId != -1) 
						builder.setLastId(lastStatisticsId);
					GetStatisticsRequest request = builder.build();
					spiceManager.execute(request, request.getCacheKey() , loadMoreRequestDuration, mLoadMoreStatisctics);
					lastLoadMoreSpiceRequestInfo = new SpiceRequestInfo<MoreStatistics>(request, request.getCacheKey(), loadMoreRequestDuration);
				}
			}
		}
	}
	
	private void loadMore(int lastStatisticsId, String fromUnixTimeStamp, String toUnixTimeStamp){
		if(getActivity() != null){
			Bundle extra = getActivity().getIntent().getExtras();
			if(extra != null){
				int pointId = extra.getInt(Intents.Point.UID);
				SpiceManager spiceManager = ((PointDetailsActivity)getActivity()).getSpiceManager();
				if(spiceManager != null){
					 Builder builder = new GetStatisticsRequest.Builder()
						.setId(pointId)
						.setStartDate(fromUnixTimeStamp)
						.setEndDate(toUnixTimeStamp);
					 if(lastStatisticsId != -1)
						 builder.setLastId(lastStatisticsId);
					GetStatisticsRequest request = builder.build();
					spiceManager.execute(request, request.getCacheKey() , loadMoreRequestDuration, mLoadMoreStatisctics);
					lastLoadMoreSpiceRequestInfo = new SpiceRequestInfo<MoreStatistics>(request, request.getCacheKey(), loadMoreRequestDuration);
				}
			}
		}
   }
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try{
			mOnLongStatisticsItemPressedListener = (OnLongStatisticsItemPressedListener) activity;
			mTimeRangeParametersHolder = (TimeRangeParametersHolder) activity;
			mStatisticsListModeHolder = (StatisticsListModeHolder) activity;
			mOnContextMenuItemPressedListener = (OnContextMenuItemPressedListener)activity;
			mOnRefreshListener = (OnRefreshListener)activity;
		}
		catch(ClassCastException e){
			 throw new ClassCastException(activity.toString() + " must implement OnLongStatisticsItemPressedListener, " +
			 		"TimeRangeParametersHolder, StatisticsListModeHolder, OnRefreshListener, OnContextMenuItemPressedListener");
		}
		Logger.Loge(getClass(), "onAttach()");
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Logger.Loge(getClass(), "onCreate()");
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
		
		Logger.Loge(getClass(), "onCreateView()");
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
		
		registerForContextMenu(getListView());
		
		setOnRefreshListener(mOnRefreshListener);
		setColorScheme(R.color.swipe_first, 
				R.color.swipe_second, 
				R.color.swipe_third, 
				R.color.swipe_fourth);
		
		mStreamAdapter = new StatisticListAdapter(getActivity(), stream);
		
		setListAdapter(mStreamAdapter);
	
        if(hasPreloadedContent()){
        	int action = getArguments().getInt(ARGS_ACTION);
        	boolean hasMore = getArguments().getBoolean(ARGS_HAS_MORE);
			@SuppressWarnings("unchecked")
			ArrayList<Statistic> records = (ArrayList<Statistic>)getArguments().getSerializable(ARGS_PRELOADED_CONTENT);
			switch (action) {
				case TIME_RANGE_ACTION:
				  updateAfterFromTimeRangeLoad(records, hasMore);
				  break;
				case REFRESH_ACTION:
				  updateAfterRefresh(records, hasMore);
				  break;
			}
        	setState(StreamingState.LOADING);
        }
  
 
        
        Logger.Loge(getClass(), "onViewCreated()");
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setRetainInstance(true);
		
		Logger.Loge(getClass(), "onActivityCreated()");
	}
	@Override
	public void onStart() {
		super.onStart();
		Logger.Loge(getClass(), "onStart()");
	}
	@Override
	public void onResume() {
		super.onResume();
		SpiceManager spiceManager = ((PointDetailsActivity)getActivity()).getSpiceManager();
		if(spiceManager != null && lastLoadMoreSpiceRequestInfo != null && getStreamingState() == StreamingState.LOADING){
			spiceManager.addListenerIfPending(MoreStatistics.class, lastLoadMoreSpiceRequestInfo.getCachekey(), mLoadMorePendingStatisctics);
			spiceManager.getFromCacheAndLoadFromNetworkIfExpired(
							lastLoadMoreSpiceRequestInfo.getSpiceRequest(), 
							lastLoadMoreSpiceRequestInfo.getCachekey(), 
							lastLoadMoreSpiceRequestInfo.getDurration(), 
							mLoadMoreFromCacheRequestListener);
		}
	   
		if(mStreamAdapter.isEmpty() ){
			if(getStreamingState() != StreamingState.COMPLETE && getStreamingState() != StreamingState.ERROR){
				Logger.Loge(getClass(), "onResume(), fillStatistics()");
		    	fill();
	    	}
			else{
	    		LoadActivityUtils.addEmptyViewIfNotExist(this, getString(R.string.no_statistic_items));
	    	}
		}
		Logger.Loge(getClass(), "onResume()");
	}
	@Override
	public void onPause() {
		super.onPause();
		Logger.Loge(getClass(), "onPause()");
	}
	@Override
	public void onStop() {
		super.onStop();
		Logger.Loge(getClass(), "onPause()");
	}
	@Override
	public void onDestroyView() {
		LoadActivityUtils.removeEmptyViewIfExist(this);
		super.onDestroyView();
		setListAdapter(null);
		mStreamAdapter = null;
		Logger.Loge(getClass(), "onDestroyView()");
	}
	
	@Override
	public void onDetach() {
		super.onDetach();
		mOnLongStatisticsItemPressedListener = null;
		mTimeRangeParametersHolder = null;
		mStatisticsListModeHolder = null;
		mOnContextMenuItemPressedListener = null;
		mOnRefreshListener = null;
		
		Logger.Loge(getClass(), "onDetach()");
	}
	private static class StatisticListAdapter extends BaseAdapter{
			 
		private LayoutInflater mLayoutInflater;
		private Map<StatististicsItemStatus, String> statusesMap;
		private List<Object> items;
		
		private final Object mLock = new Object();
		
		public StatisticListAdapter(Context context, List<Object> stream) {
			mLayoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			statusesMap = PointDetailsActivity.getStatusesMap(context);
			items = stream;
		}
		
		@Override
		public int getCount() {
			return items.size();
		}
		@Override
		public Object getItem(int position) {
			return items.get(position);
		}
		@Override
		public long getItemId(int position) {
			return position;
		}
		public void clear(){
			synchronized (mLock) {
				items.clear();	
			}
		}
		
		public void addAll(Collection<? extends Object> collection){
			synchronized (mLock) {
				for(Object object : collection){
					if(!items.contains(object))
						items.add(object);
				}
				//items.addAll(collection);
			}
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
				
				Statistic statistics = (Statistic)items.get(position);
				if(statistics == null)	return convertView;
				
				String value = statistics.getValue();
				Characteristic characteristic = statistics.getCharacteristic();
				
				String valueTop = characteristic == null ? "100" : characteristic.getCriticalValueTop();
				String valueBottom = characteristic == null ? "0" : characteristic.getCriticalValueBottom();
				
				StatististicsItemStatus status = PointDetailsActivity.getStatus(value, valueTop, valueBottom);
				if(status != null)
					PointDetailsActivity.setUpStatusView(status, holder.status, statusesMap);
					
				String groupNameStr  = characteristic == null ? "-" : characteristic.getName();
				String unit = characteristic == null ? "%" : characteristic.getUnit();
				String valueStr = statistics.getValue() + unit;
				String whoSetStr = "Михайличенко";
				
				Date recordEntrydate  = DateUtils.getDate(statistics.getEntryDate());
				String dateFormat = DateUtils.inUsualFormat(recordEntrydate);
					
				holder.inspector.setText(whoSetStr);
				holder.characteristicGroupName.setText(groupNameStr);
				holder.value.setText(valueStr);
				holder.date.setText(dateFormat);
				
				
				int backgroundResources = (position % 2 == 0) ? R.drawable.abs__ab_solid_light_holo : R.drawable.abs__ab_solid_shadow_holo;
				((RelativeLayout)convertView).setBackgroundResource(backgroundResources);
			
			return convertView;
		}
		
		private static class ViewHolder{
			TextView inspector;
			TextView status;
			TextView characteristicGroupName;
			TextView value;
			TextView date;
		}
		
	  }
	
	
	public void restore(List<Statistic> records){
	
		mStreamAdapter.clear();
		mStreamAdapter.addAll(records);
		mStreamAdapter.notifyDataSetChanged();
		
		addOrRemoveEmptyView(records);
	}
	
	public void fill(){
		setState(StreamingState.LOADING );
		load();
	}
	
	
	public void fill(List<Statistic> records, boolean hasMoreItems) {
		
		mStreamAdapter.clear();
		mStreamAdapter.addAll(records);
		mStreamAdapter.notifyDataSetChanged();
		
		setState(hasMoreItems ? StreamingState.DONE : StreamingState.COMPLETE);
		addOrRemoveEmptyView(records);
		
	}
	
	public void updateAfterFromTimeRangeLoad(List<Statistic> records, Boolean hasMore) {
		
		mStreamAdapter.clear();
		mStreamAdapter.addAll(records);
		mStreamAdapter.notifyDataSetChanged();
		
		setState(hasMore ? StreamingState.DONE : StreamingState.COMPLETE);
		addOrRemoveEmptyView(records);
	}
	
	public void updateAfterRefresh(List<Statistic> records, Boolean hasMore) {
		
		mStreamAdapter.clear();
		mStreamAdapter.addAll(records);
		mStreamAdapter.notifyDataSetChanged();
		
		setState(hasMore ? StreamingState.DONE : StreamingState.COMPLETE);
		addOrRemoveEmptyView(records);
	}
	
	private void addOrRemoveEmptyView(List<? extends Object> records){
		if(records != null && !records.isEmpty())
			 LoadActivityUtils.removeEmptyViewIfExist(this);
		else LoadActivityUtils.addEmptyViewIfNotExist(this, getString(R.string.no_statistic_items));
	}

	public void clearStatistics() {
		setState(StreamingState.INIT);
		mStreamAdapter.clear();
		mStreamAdapter.notifyDataSetChanged();
		
		LoadActivityUtils.addEmptyViewIfNotExist(this, getString(R.string.no_statistic_items));
	}
	
	public void scrollListToBottom() {
		if(mStreamAdapter != null & getListView() != null)
	    getListView().post(new Runnable() {
	        @Override
	        public void run() {
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
	
	protected void onListPackageReady(Collection<? extends Object> collection){
    	if(collection != null){
    		mStreamAdapter.addAll(collection);
     	   	mStreamAdapter.notifyDataSetChanged();
     	}
	}
	private void tryExponetialBackOff(){
		// simple exponential back-off
    	retryInterval = 2 * retryInterval < MAX_RETRY_INTERVAL ? 2 * retryInterval : MAX_RETRY_INTERVAL;
    	if(retryCount < MAX_RETRY_COUNT){
    		Handler handler = new Handler();
    		handler.postDelayed( 
    				new MyBackOffRunnable(this), retryInterval);
    	}
    	else{
    		retryCount = 0;
    		retryInterval = MIN_RETRY_INTERVAL;
    		// remove loading view and add retry view
    		onError();
    	}
	}

	private static class MyBackOffRunnable implements Runnable {

		private WeakReference<StatisticsOnlineFragment> fragmentRef;
		
		public MyBackOffRunnable(StatisticsOnlineFragment fragment){
			fragmentRef = new WeakReference<StatisticsOnlineFragment>(fragment);
		}
		@Override
		public void run() {
			StatisticsOnlineFragment fragment = fragmentRef.get();
			if(fragment != null && fragment.isAdded()){
				Logger.Logi(getClass(), "retry : " + "count: "  + fragment.retryCount + ", interval:" + fragment.retryInterval);
        		fragment.setState(StreamingState.REPEAT);
        		fragment.loadMoreResults();
				fragment.retryCount++;
			}
		}
		
	};

	
	
	
	
}
