package com.itdoors.haccp.ui.fragments;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.json.JSONException;

import uk.co.senab.actionbarpulltorefresh.extras.actionbarsherlock.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

import com.itdoors.haccp.Global;
import com.itdoors.haccp.R;
import com.itdoors.haccp.exceptions.ServerFailedException;
import com.itdoors.haccp.loaders.RESTLoader;
import com.itdoors.haccp.model.Point;
import com.itdoors.haccp.model.StatisticsRecord;
import com.itdoors.haccp.model.StatististicsItemStatus;
import com.itdoors.haccp.parser.LoadMoreStatisticsParser;
import com.itdoors.haccp.parser.LoadMoreStatisticsParser.Content;
import com.itdoors.haccp.utils.LoadActivityUtils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
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


public class StatisticsFragmentV0 extends EndlessListFragment implements LoaderCallbacks<RESTLoader.RESTResponse>  {

    private static final String POINT_INFO_SAVE = "com.itdoord.haccp.fragments.StatisticsFragment.POINT_INFO_SAVE";
    private static final String STATISTICS_INFO_SAVE = "com.itdoord.haccp.fragments.StatisticsFragment.STATISTICS_INFO_SAVE";
    
	protected static final String ARGS_PIAS_URI = "com.itdoord.haccp.fragments.StatisticsFragment.ARGS_PIAS_URI";
	protected static final String ARGS_PIAS_PARAMS_URI = "com.itdoord.haccp.fragments.StatisticsFragment.ARGS_PIAS_PARAMS_URI";
	
	private final int STATICTIS_MORE_CODE   = 1;
	
	private Point mPoint;
	private List<StatisticsRecord> mStatistics;
	
    public interface OnLongStatisticsItemPressedListener {
		public void onLongStatisticsItemPressed(long id);
	}
    
    public interface OnContextMenuItemPressedListener {
    	public void onEditStaticticsItemContextMenuPressed(int position);
    	public void onDeleteStaticticsItemContextMenuPressed(int position);
    }
    
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
	
	@Override
	protected void loadMoreResults() {
		
		if(mStream != null && !mStream.isEmpty()){
			int position = mStream.size() - 1;
			StatisticsRecord last = (StatisticsRecord)mStream.get(position);
			int lastId = last.getId();
		
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
		
		int pointId = mPoint.getId();
		
		String url = Global.API_URL +	"/point/" + Integer.toString(pointId) + 
										"/statistics/" + lastStatisticsId;
		
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
		
		int pointId = mPoint.getId();
		
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
	
	@SuppressWarnings("unchecked")
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		mStreamAdapter =  new StatisticListAdapter(getActivity());
		setListAdapter(mStreamAdapter);
		
		Point point = null;
		ArrayList<StatisticsRecord> statistics = null;
		
		if(getArguments() != null ){
			point = (Point)getArguments().getSerializable(PointDetailsFragmentV0.CONTROL_POINT_INFO_TAG);
			statistics = (ArrayList<StatisticsRecord>)getArguments().getSerializable(PointDetailsFragmentV0.STATISTICS_INFO_TAG);
			
		}
		if(savedInstanceState != null){
			point = (Point)savedInstanceState.getSerializable(POINT_INFO_SAVE);
			statistics = (ArrayList<StatisticsRecord>)savedInstanceState.getSerializable(STATISTICS_INFO_SAVE);
		}
		if(point != null && statistics != null){
			restoreStatistics(point, statistics);
		}
		
		registerForContextMenu(getListView());
	
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
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if(mPoint != null && mStatistics != null){
			outState.putSerializable(POINT_INFO_SAVE, mPoint);
			outState.putSerializable(STATISTICS_INFO_SAVE, (ArrayList<StatisticsRecord>)mStatistics);
		}
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		
		view.setBackgroundResource(R.drawable.abs__ab_solid_shadow_holo);
		
		/*
		getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				if(mOnLongStatisticsItemPressedListener != null ){
					mOnLongStatisticsItemPressedListener.onLongStatisticsItemPressed(id);
					return true;
				}
				return false;
			}
		});
		*/
		// Adding pullToRefresh 
		
		  ViewGroup viewGroup = (ViewGroup) view;

          // As we're using a ListFragment we create a PullToRefreshLayout manually
          mPullToRefreshLayout = new PullToRefreshLayout(viewGroup.getContext());

          // We can now setup the PullToRefreshLayout
          ActionBarPullToRefresh.from(getActivity())
                  // We need to insert the PullToRefreshLayout into the Fragment's ViewGroup
                  .insertLayoutInto(viewGroup)
                  // Here we mark just the ListView and it's Empty View as pullable
                  .theseChildrenArePullable(android.R.id.list)
                  .listener(mOnRefreshListener)
                  .setup(mPullToRefreshLayout);
          
          
		
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
				
				convertView = mLayoutInflater.inflate(R.layout.list_item_statistics, null);
				
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
					String whoSetStr = "Михайличенко";
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
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		if(!mStream.isEmpty()){
		
		}
	}
	
	public void restoreStatistics(Point point, List<StatisticsRecord> records){
		
		this.mPoint = point;
		this.mStatistics = records;
		
		
		mStream.addAll(records);
		mStreamAdapter.notifyDataSetChanged();
		
		if(records != null && !records.isEmpty())
			LoadActivityUtils.removeEmptyViewIfExist(this);
		else
			LoadActivityUtils.addEmptyViewIfNotExist(this, getString(R.string.no_statistic_items));
	
	}
	
	public void fillStatistics(Point point, List<StatisticsRecord> records, boolean hasMoreItems) {
		
		this.mPoint = point;
		this.mStatistics = records;
		
		
		mStream.addAll(records);
		mStreamAdapter.notifyDataSetChanged();
	
		setState(hasMoreItems ? StreamingState.LOADING : StreamingState.COMPLETE);
		
		if(getStreamingState() == StreamingState.LOADING)
			load();
		
		if(records == null || records.isEmpty())
			LoadActivityUtils.addEmptyViewIfNotExist(this, getString(R.string.no_statistic_items));
		
	}
	
	public void updateStatisticsAfterAddRequestSuccess(Point point,	List<StatisticsRecord> records, Boolean hasMoreStatiscticItems) {
		
		this.mPoint = point;
		this.mStatistics = records;
		
		mStream.clear();
		mStream.addAll(records);
		mStreamAdapter.notifyDataSetChanged();
		
		if(records != null && !records.isEmpty())
			LoadActivityUtils.removeEmptyViewIfExist(this);
		else
			LoadActivityUtils.addEmptyViewIfNotExist(this, getString(R.string.no_statistic_items));
		
	}

	public void updateStatisticsAfterTimeRangeLoadSuccess(Point point,	List<StatisticsRecord> records, Boolean hasMoreStatiscticItems) {
		
		this.mPoint = point;
		this.mStatistics = records;
		
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
	
	public void updateStatisticsAfterRefreshSuccess(Point point,
			List<StatisticsRecord> records, Boolean hasMoreStatiscticItems) {
		this.mPoint = point;
		this.mStatistics = records;
		
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
		
		this.mPoint = null;
		this.mStatistics = null;
		
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
	            
	            if(getActivity() != null)
	            	return new RESTLoader(getActivity(), RESTLoader.HTTPVerb.GET, action, params);
	        }
	        
	        return null;
	}

	
	@Override
	public void onLoadFinished(Loader<RESTLoader.RESTResponse> loader, RESTLoader.RESTResponse data) {
		  	
		    switch (loader.getId()) {
				
	        	case STATICTIS_MORE_CODE:
	        		onLoadMoreCompleted(data);
	        	default:
	        		break;
			}
	        
	   
	}
	
	@Override
	public void onLoaderReset(Loader<RESTLoader.RESTResponse> loader) {
		
		switch (loader.getId()) {
			
	      	case STATICTIS_MORE_CODE:
	    		break;
	      	default:
	      		break;
		}
		
	}
	
	private void onLoadMoreCompleted( RESTLoader.RESTResponse data ){
		
		int    code = data.getCode();
        String json = data.getData();
        
        
        boolean failed  = false;
        
        if (code == 200 && !json.equals("")) {
        	
        	LoadMoreStatisticsParser parser = new LoadMoreStatisticsParser();
        	try {
        		
				final Content content = (Content) parser.parse(json);
				boolean hasMoreResults = content.hasMoreStatiscticItems;
				setState(hasMoreResults ? StreamingState.DONE : StreamingState.COMPLETE);
		
				mStatistics.addAll(content.records);
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
        	if(getActivity() != null)
        	failed = true;
        }
       
        if(failed){
        	// remove loading view and add retry view
        	onError();
        }
	}

	
	
}
