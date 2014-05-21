package com.itdoors.haccp.ui.fragments;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.itdoors.haccp.R;
import com.itdoors.haccp.model.Point;
import com.itdoors.haccp.model.PointStatus;
import com.itdoors.haccp.model.StatisticsRecord;
import com.itdoors.haccp.parser.ControlPointParser.Content;
import com.itdoors.haccp.utils.TabsAdapter;

public class PointDetailsFragmentV0 extends SherlockFragment {

	TabHost mTabHost;
    ViewPager  mViewPager;
    TabsAdapter mTabsAdapter;
    
    public static final String CONTROL_POINT_STATISTICS_TAG = "com.itdoors.haccp.fragments.ControlPointDetailsFragment.CONTROL_POINT_STATISTICS_TAG";
    public static final String CONTROL_POINT_ATTRIBUTES_TAG = "com.itdoors.haccp.fragments.ControlPointDetailsFragment.CONTROL_POINT_ATTRIBUTES_TAG";
    public static final String TAB_SAVE_TAG = "com.itdoors.haccp.fragments.ControlPointDetailsFragment.TAB_SAVE_TAG";
    
    public static final String CONTROL_POINT_INFO_TAG = "com.itdoors.haccp.fragments.ControlPointDetailsFragment.CONTROL_POINT_INFO_TAG";
    public static final String STATISTICS_INFO_TAG = "com.itdoors.haccp.fragments.ControlPointDetailsFragment.STATISTICS_INFO_TAG";
    
 	public static final String CONTROL_POINT_SAVE_KEY = "com.itdoors.haccp.fragments.ControlPointDetailsFragment.CONTROL_POINT_SAVE_KEY";
	public static final String STATISTICS_SAVE_TAG = "com.itdoors.haccp.fragments.ControlPointDetailsFragment.STATISTICS_INFO_TAG";
    
	public static PointDetailsFragmentV0 newInstance(Point point, ArrayList<StatisticsRecord> records){
		
		PointDetailsFragmentV0 f = new PointDetailsFragmentV0();
		
		Bundle bundle = new Bundle();
		bundle.putSerializable(CONTROL_POINT_INFO_TAG, point);
		bundle.putSerializable(STATISTICS_INFO_TAG, records);
		f.setArguments(bundle);
		
		return f;
	}
    
	public void showTabs(Point point, List<StatisticsRecord> statistics){
		 
		 LayoutInflater inflater = getLayoutInflater(null);
		 View cpStatisticsView = createTabView(inflater, getResources().getString(R.string.statistics));
	     View cpAttributesView = createTabView(inflater, getResources().getString(R.string.attributes));
	        
	     Bundle pointAndStatisticsInfo = new Bundle();
	     
	     if(point != null)
	    	 pointAndStatisticsInfo.putSerializable(CONTROL_POINT_INFO_TAG, point);
	     if(statistics != null && !statistics.isEmpty())	
	    	 pointAndStatisticsInfo.putSerializable(STATISTICS_INFO_TAG, (ArrayList<StatisticsRecord>)statistics);
	     if(point != null && statistics != null){
	    	
	    	 mTabsAdapter = new TabsAdapter(getActivity(), mTabHost, mViewPager);
	     	 mTabsAdapter.addTab(mTabHost.newTabSpec(CONTROL_POINT_STATISTICS_TAG).setIndicator(cpStatisticsView),
	    		 StatisticsFragmentV0.class, pointAndStatisticsInfo);
	     	 mTabsAdapter.addTab(mTabHost.newTabSpec(CONTROL_POINT_ATTRIBUTES_TAG).setIndicator(cpAttributesView),
	    		 AttributesFragmentV0.class, pointAndStatisticsInfo);
	    		
	     }
	     
	}
	public void showEmptyTabs(){
		
		 LayoutInflater inflater = getLayoutInflater(null);
		 View cpStatisticsView = createTabView(inflater, getResources().getString(R.string.statistics));
	     View cpAttributesView = createTabView(inflater, getResources().getString(R.string.attributes));
	        
	   	 mTabsAdapter = new TabsAdapter(getActivity(), mTabHost, mViewPager);
	   	 mTabsAdapter.addTab(mTabHost.newTabSpec(CONTROL_POINT_STATISTICS_TAG).setIndicator(cpStatisticsView),
	   			 StatisticsFragmentV0.class, null);
		 mTabsAdapter.addTab(mTabHost.newTabSpec(CONTROL_POINT_ATTRIBUTES_TAG).setIndicator(cpAttributesView),
	    		 AttributesFragmentV0.class, null);
	    
	
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
			
			View v = inflater.inflate(R.layout.fragment_control_point_details_v0, null);
			
			mTabHost = (TabHost)v.findViewById(android.R.id.tabhost);
		    mTabHost.setup();
	        mViewPager = (ViewPager)v.findViewById(R.id.cp_pager);
	        
			if(getArguments() != null){
			
				Point point = (Point)getArguments().getSerializable(CONTROL_POINT_INFO_TAG);
				
				@SuppressWarnings("unchecked")
				ArrayList<StatisticsRecord> statistics = (ArrayList<StatisticsRecord>)getArguments().getSerializable(STATISTICS_SAVE_TAG);
				showTabs(point, statistics);
			
			}
			
			else showEmptyTabs();
			
	        if (savedInstanceState != null)
	            mTabHost.setCurrentTabByTag(savedInstanceState.getString(TAB_SAVE_TAG));
	        
		return v;
	
	}
	
	private static View createTabView(LayoutInflater inflater, final String text) {
        View view = inflater.inflate(R.layout.tabs_view, null);
        TextView tv = (TextView) view.findViewById(R.id.tabsText);
        tv.setText(text);
        return view;
    }
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString(TAB_SAVE_TAG, mTabHost.getCurrentTabTag());
	}

	public void fillUI(Content content) {
		
		if(getActivity () != null) {
		
			final StatisticsFragmentV0 mStatisticsFragment = getStatisticsFragment();
			final AttributesFragmentV0 mAttrFragment = getAttributesFragment();
			
			if(content != null ){
			
				mStatisticsFragment.fillStatistics(content.point, content.records, content.hasMoreStatiscticItems);
				mAttrFragment.fillViews(content.point);
			
			}
			else{
				mStatisticsFragment.clearStatistics();
				mAttrFragment.fillViews(null);
			}
		}
	}
	
	public void scroolStatisticsToBottom(){
		if(getActivity () != null) {
			final StatisticsFragmentV0 mStatisticsFragment = getStatisticsFragment();
			mStatisticsFragment.scrollListToBottom();
		}
	}
	
	public void scroolStatisticsToTop() {
		if(getActivity () != null) {
			final StatisticsFragmentV0 mStatisticsFragment = getStatisticsFragment();
			mStatisticsFragment.scrollListToTop();
		}
	}
	
	private StatisticsFragmentV0 getStatisticsFragment(){
		return getActivity() == null ? null : (StatisticsFragmentV0) getActivity().getSupportFragmentManager().findFragmentByTag(mTabsAdapter.getFragmentTag(0));
	}
	
	private AttributesFragmentV0 getAttributesFragment(){
		return getActivity() == null ? null : (AttributesFragmentV0) getActivity().getSupportFragmentManager().findFragmentByTag(mTabsAdapter.getFragmentTag(1));
	}

	public void updateStatisticsAfterAddRequestSuccess(Content content) {
		if(getActivity () != null) {
			final StatisticsFragmentV0 mStatisticsFragment = getStatisticsFragment();
			if(content != null){
				mStatisticsFragment.updateStatisticsAfterAddRequestSuccess(content.point, content.records, content.hasMoreStatiscticItems);
			}
		}
		
	}

	public void updateStatisticsAfterTimeRangeLoadSuccess(Content content) {
		if(getActivity () != null) {
			final StatisticsFragmentV0 mStatisticsFragment = getStatisticsFragment();
			if(content != null){
				mStatisticsFragment.updateStatisticsAfterTimeRangeLoadSuccess(content.point, content.records, content.hasMoreStatiscticItems);
			}
		}
		
	}

	public void updateStatisticsAfterRefreshSuccess(Content content) {
		if(getActivity () != null) {
			final StatisticsFragmentV0 mStatisticsFragment = getStatisticsFragment();
			if(content != null){
				mStatisticsFragment.updateStatisticsAfterRefreshSuccess(content.point, content.records, content.hasMoreStatiscticItems);
			}
		}
		
	}

	public void refreshReshFailed() {
		if(getActivity () != null) {
			final StatisticsFragmentV0 mStatisticsFragment = getStatisticsFragment();
				mStatisticsFragment.refreshReshFailed();
		}
	}

	public void changeStatusOnAttributes(PointStatus status) {
		if(getActivity () != null) {
			final AttributesFragmentV0 mAttributesFragment = getAttributesFragment();
			if(status != null){
				mAttributesFragment.changeStatusOnAttributes(status);
			}
		}
		
	}

	
	

}
