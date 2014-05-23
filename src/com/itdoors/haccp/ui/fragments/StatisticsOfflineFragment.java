package com.itdoors.haccp.ui.fragments;

import java.text.SimpleDateFormat;
import java.util.Date;

import uk.co.senab.actionbarpulltorefresh.extras.actionbarsherlock.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;


import com.itdoors.haccp.Global;
import com.itdoors.haccp.Intents;
import com.itdoors.haccp.R;
import com.itdoors.haccp.model.StatististicsItemStatus;
import com.itdoors.haccp.provider.HaccpContract;
import com.itdoors.haccp.ui.interfaces.OnContextMenuItemPressedListener;
import com.itdoors.haccp.utils.CalendarUtils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

public class StatisticsOfflineFragment extends ListFragment implements
		LoaderManager.LoaderCallbacks<Cursor>{
	
	private OnRefreshListener mOnRefreshListener;
	private PullToRefreshLayout mPullToRefreshLayout;
	private OnContextMenuItemPressedListener mOnContextMenuItemPressedListener;
	
	private CursorAdapter mStatisticsAdapter;
	 
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try{
			mOnContextMenuItemPressedListener = (OnContextMenuItemPressedListener)activity;
			mOnRefreshListener = (OnRefreshListener)activity;
		}
		catch(ClassCastException e){
			 throw new ClassCastException(activity.toString() + " must implement OnContextMenuItemPressedListener, OnRefreshListener");
		}
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
		setEmptyText(getString(R.string.no_records));
		
		registerForContextMenu(getListView());
		mStatisticsAdapter = new MyStatisticsAdapter(getActivity());
		
		// Adding pullToRefresh 
	    ViewGroup viewGroup = (ViewGroup) view;
        mPullToRefreshLayout = new PullToRefreshLayout(viewGroup.getContext());
        ActionBarPullToRefresh.from(getActivity())
                .insertLayoutInto(viewGroup)
                .theseChildrenArePullable(android.R.id.list)
                .listener(mOnRefreshListener)
                .setup(mPullToRefreshLayout);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setListAdapter(mStatisticsAdapter);
		getLoaderManager().initLoader(0, null, this);
	}
	 
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle data) {
		int pointID  = getActivity().getIntent().getIntExtra(Intents.Point.UID, -1);
		if(pointID == -1)
			return null;
		
		Uri uri = HaccpContract.Statistics.buildUriForPoint(pointID);
		return new CursorLoader(getActivity(), uri, StatisticsQuery.PROJECTION, null, null, null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		mStatisticsAdapter.swapCursor(cursor);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		mStatisticsAdapter.swapCursor(null);
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
	
	public void refreshReshFailed() {
		if(mPullToRefreshLayout != null) 
			mPullToRefreshLayout.setRefreshComplete();
	}
	
	private class MyStatisticsAdapter extends CursorAdapter{

			private Drawable lightDrawable;
			private Drawable darkDrawable;
			
			public MyStatisticsAdapter(Context context) {
	            super(context, null, 0);
	            lightDrawable = context.getResources().getDrawable(R.drawable.abs__ab_solid_light_holo);
	            darkDrawable = context.getResources().getDrawable(R.drawable.abs__ab_solid_shadow_holo);
	        }

	        @Override
	        public View newView(Context context, Cursor cursor, ViewGroup parent) {
	            return LayoutInflater.from(context).inflate(R.layout.list_item_statistics,
	                    parent, false);
	        }
	        
	        @SuppressWarnings("deprecation")
			public View getView(int position, View convertView, ViewGroup parent) {
	        	if (!mDataValid) {
	        		throw new IllegalStateException("this should only be called when the cursor is valid");
	        	
	        	}
	        	if (!mCursor.moveToPosition(position)) {
	        	    throw new IllegalStateException("couldn't move cursor to position " + position);
	        	}
	        	View v;
	        	ViewHolder holder;
	        	
	        	if (convertView == null) {
	        		v = newView(mContext, mCursor, parent);
	        		holder = new ViewHolder();
	        		holder.inspector = (TextView)v.findViewById(R.id.cp_owner);
					holder.status = (TextView)v.findViewById(R.id.cp_status);
					holder.characteristicGroupName = (TextView)v.findViewById(R.id.cp_char_name);
					holder.value = (TextView)v.findViewById(R.id.cp_value);
					holder.date = (TextView)v.findViewById(R.id.cp_date);
					v.setTag(holder);
					
	        	} else {
	        		v = convertView;
	        	}
	        	
	        	bindView(v, mContext, mCursor);
	        	
	        	Drawable backgroundResources = (position % 2 == 0) ? lightDrawable: darkDrawable;
				v.setBackgroundDrawable(backgroundResources);
				
	        	return v;
	        
	        }
	        
	        @Override
	        public View getDropDownView(int position, View convertView, ViewGroup parent) {
		        if (mDataValid) {
		        	return getView(position, convertView, parent);
		        } else {
		        	return null;
		        }
	        }

	        @SuppressLint("SimpleDateFormat")
			@Override
	        public void bindView(View view, Context context, final Cursor cursor) {
	        	
	        	
				StatististicsItemStatus cpStatus = StatististicsItemStatus.APPROVED;
				try{
					
					double value = Double.parseDouble(cursor.getString(StatisticsQuery.VALUE));
					double valueTop = Double.parseDouble(cursor.getString(StatisticsQuery.CHARACTERISTICS_CRITICAL_VALUE_TOP));
					double valueBottom = Double.parseDouble(cursor.getString(StatisticsQuery.CHARACTERISTICS_CRITICAL_VALUE_BOTTOM));
					
					if(value <= valueBottom)
						cpStatus = StatististicsItemStatus.APPROVED;
					else if( value > valueBottom && value < valueTop )
						cpStatus = StatististicsItemStatus.WARNING;
					else if( value >= valueTop)
						cpStatus = StatististicsItemStatus.DANGER;
				}
				catch (Exception e){
					e.printStackTrace();
				}
				
				String warning = getString(StatististicsItemStatus.WARNING.getStringResourceID());
				String danger = getString(StatististicsItemStatus.DANGER.getStringResourceID());
				String approved = getString(StatististicsItemStatus.APPROVED.getStringResourceID());
				
				ViewHolder holder = (ViewHolder)view.getTag();
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
				
				String groupNameStr  = "";
				String unitStr = "";
				String valueStr = "";
				String whoSetStr = "";
				String dateStr = "";
				
				try{
					
					String name = cursor.getString(StatisticsQuery.CHARACTERISTICS_NAME);
					String unit = cursor.getString(StatisticsQuery.CHARACTERISTICS_UNIT);
					int value = (int) Double.parseDouble(cursor.getString(StatisticsQuery.VALUE));
					String dateTimeStamp = cursor.getString(StatisticsQuery.ENTRY_DATE);
					Date date = CalendarUtils.fromTimeStamp(dateTimeStamp);
					
					groupNameStr  = name == null ? "-" : name;
					unitStr = unit == null ? "%" : unit;
					valueStr = Integer.toString(value) + unitStr;
					whoSetStr = "������������";
					dateStr = date== null ? "-" : new SimpleDateFormat(Global.usualDateFromat).format(date) .toString();
					
				}
				catch (Exception e){
					e.printStackTrace();
				}
				
				holder.inspector.setText(whoSetStr);
				holder.characteristicGroupName.setText(groupNameStr);
				holder.value.setText(valueStr);
				holder.date.setText(dateStr);
				
	        }
		
	    	private class ViewHolder{
				TextView inspector;
				TextView status;
				TextView characteristicGroupName;
				TextView value;
				TextView date;
			}
	}
	
	@SuppressWarnings("unused")
	private interface StatisticsQuery{
	 
		String[] PROJECTION = new String[]{
				
				HaccpContract.Statistics._ID,
				HaccpContract.Statistics.UID,
				HaccpContract.Statistics.VALUE,
				HaccpContract.Statistics.ENTRY_DATE,
				HaccpContract.Statistics.GROUP_CHARACTERISTICS_ID_PROJECTION,
				HaccpContract.Statistics.GROUP_CHARACTERISTICS_UID_PROJECTION,
				HaccpContract.GroupCharacterisitcs.NAME,
				HaccpContract.GroupCharacterisitcs.UNIT,
				HaccpContract.GroupCharacterisitcs.CRITICAL_VALUE_BOTTOM,
				HaccpContract.GroupCharacterisitcs.CRITICAL_VALUE_TOP
				
		};
		
		int _ID = 0;
		int UID = 1;
		int VALUE = 2;
		int ENTRY_DATE = 3;
		int CHARACTERISTICS_ID = 4;
		int CHARACTERISTICS_UID = 5;
		int CHARACTERISTICS_NAME = 6;
		int CHARACTERISTICS_UNIT = 7;
		int CHARACTERISTICS_CRITICAL_VALUE_TOP = 8;
		int CHARACTERISTICS_CRITICAL_VALUE_BOTTOM = 9;
	}
	  
	  
}