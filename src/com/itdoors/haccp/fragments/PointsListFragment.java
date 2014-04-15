package com.itdoors.haccp.fragments;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockListFragment;
import com.itdoors.haccp.R;

public class PointsListFragment extends SherlockListFragment{
	
	private static final String POINTS_LIST_FRAGMENT_COUNT_TAG = "com.itdoors.haccp.activities.PointsListFragment.POINTS_LIST_FRAGMENT_COUNT_TAG";
	private static final String POINTS_LIST_FRAGMENT_DELTA_TAG = "com.itdoors.haccp.activities.PointsListFragment.POINTS_LIST_FRAGMENT_DELTA_TAG";
	
	public interface OnPointPressedListener{
		public void onPointPressed(int pointId);
	}
	
	private OnPointPressedListener mOnPointPressedListener;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mOnPointPressedListener = (OnPointPressedListener)activity;
	
	}
	
	public static PointsListFragment newInstance(int count, int delta){
		
		Bundle args = new Bundle();
		args.putSerializable(POINTS_LIST_FRAGMENT_COUNT_TAG, count);
		args.putSerializable(POINTS_LIST_FRAGMENT_DELTA_TAG, delta);
		
		PointsListFragment f = new PointsListFragment();
		f.setArguments(args);
		
		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
	}
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		final ListView mListView = getListView();
		mListView.setSelector(R.drawable.abs__tab_indicator_ab_holo);
		mListView.setCacheColorHint(Color.TRANSPARENT);
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if(getArguments() != null){
			int count = getArguments().getInt(POINTS_LIST_FRAGMENT_COUNT_TAG);
			
			String[] items = new String [count];
	        for(int i = 0; i < items.length; i++){
	        	items [i] = "Точка № " + (i + 1);
	        }
	        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, items);
	        setListAdapter(adapter);
	        
		}
        
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		
		if(getArguments() != null){
		int delta = getArguments().getInt(POINTS_LIST_FRAGMENT_DELTA_TAG);
			if(mOnPointPressedListener != null){
				mOnPointPressedListener.onPointPressed( delta + position );
			}
		}
	}
}
