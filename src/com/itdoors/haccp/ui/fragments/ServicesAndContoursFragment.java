package com.itdoors.haccp.ui.fragments;

import java.util.ArrayList;
import java.util.List;

import com.itdoors.haccp.R;
import com.itdoors.haccp.model.Contour;
import com.itdoors.haccp.model.Service;
import com.itdoors.haccp.provider.HaccpContract;
import com.itdoors.haccp.ui.adapters.SectionedListAdapter;
import com.itdoors.haccp.utils.Logger;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.widget.ListView;

public class ServicesAndContoursFragment extends ListFragment implements
		LoaderManager.LoaderCallbacks<Cursor> {

	public interface OnContourPressedListener{
		public void onContourPressed(Contour contour);
	}
	
	private SectionedListAdapter mSectionedListAdapter;
	private SimpleCursorAdapter mServiceSimpleCursorAdapter;
	private SimpleCursorAdapter mContourSimpleCursorAdapter;
	
	private OnContourPressedListener mOnContourPressedListener;

	private static final String[] SERVICE_FROM_COLUMNS = new String[] { HaccpContract.Services.NAME};
	private static final int[] SERVICE_TO_FIELDS = new int[] { R.id.serv_item_name };

	private static final String[] CONTOUR_FROM_COLUMNS = new String[] { HaccpContract.Contours.NAME };
	private static final int[] CONTOUR_TO_FIELDS = new int[] { R.id.cont_item_name };

	
	@SuppressWarnings("unused")
	private interface ServicesQuery{
		 
		int _TOKEN = 0;
    	String[] PROJECTION = new String[]{
    			HaccpContract.Services._ID,
    			HaccpContract.Services.UID,
	            HaccpContract.Services.NAME,
	    
	    };
    	
        
		int SERVICE_ID = 0;
    	int SERVICE_UID = 1;
 	    int SERVICE_NAME = 2;
 	    
	}
	
	@SuppressWarnings("unused")
	private interface ContoursByServicesQuery{
		 
		int _TOKEN = 1;
    	String[] PROJECTION = new String[]{
    			
    			HaccpContract.Contours._ID,
    			HaccpContract.Contours.UID,
    			HaccpContract.Contours.NAME,
    			HaccpContract.Contours.SERVICE_ID,
    			HaccpContract.Contours.COLOR,
    			
    			HaccpContract.Contours.SERVICE_UID_PROJECTION,
    			HaccpContract.Contours.SERVICE_NAME_PROJECTION
	    };
    	
    	int CONTOUR_ID = 0;
        int CONTOUR_UID = 1;
        int CONTOUR_NAME = 2;
        int CONTOUR_SERVICE_ID = 3;
   	    int CONTOUR_COLOR = 4;
   	    int SERVICE_UID = 5;
   	    int SERVICE_NAME = 6;
 	    
	}
	
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mOnContourPressedListener = (OnContourPressedListener) activity;
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		final ListView mListView = getListView();
		mListView.setBackgroundResource(R.drawable.abs__ab_solid_light_holo);
		mListView.setSelector(R.drawable.abs__tab_indicator_ab_holo);
		mListView.setCacheColorHint(Color.TRANSPARENT);
		mListView.setDrawSelectorOnTop(true);
		//mListView.setEmptyView(ContextUtils.getEmptyListView(getActivity()));

		
		mServiceSimpleCursorAdapter = new SimpleCursorAdapter(
				getActivity(), 
				R.layout.list_item_service, 
				null,
				SERVICE_FROM_COLUMNS, 
				SERVICE_TO_FIELDS, 
				0);
		mContourSimpleCursorAdapter = new SimpleCursorAdapter(
				getActivity(),
				R.layout.list_item_contour, 
				null, 
				CONTOUR_FROM_COLUMNS,
				CONTOUR_TO_FIELDS, 
				0);
		
		//setEmptyText(getText(R.string.loading));
		mSectionedListAdapter = new SectionedListAdapter(getActivity(), mServiceSimpleCursorAdapter, mContourSimpleCursorAdapter);
	}

	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setListAdapter(mSectionedListAdapter);
		
		getLoaderManager().initLoader(ServicesQuery._TOKEN, null, this);
		getLoaderManager().initLoader(ContoursByServicesQuery._TOKEN, null, this);
		
	}
	
	private void onServicesLoadFinished(Cursor cursor){
		if(mServiceSimpleCursorAdapter!=null && cursor!=null) {
			 mServiceSimpleCursorAdapter.swapCursor(cursor); //swap the new cursor in.
        }
	}
    private void onContoursLoadfinished(Cursor cursor){
    	if(mContourSimpleCursorAdapter!=null && cursor!=null) {
    	        
    		    List<SectionedListAdapter.Section> sections =
    	                new ArrayList<SectionedListAdapter.Section>();
    	        cursor.moveToFirst();
    	        long previousServiceId = -1;
    	        long serviceId;
    	        while (!cursor.isAfterLast()) {
    	            serviceId = cursor.getInt(ContoursByServicesQuery.CONTOUR_SERVICE_ID);
    	        	if (serviceId != previousServiceId) {
    	        		int position = cursor.getPosition();
    	        		Logger.Logi(getClass(), "sectionPostion:" + Integer.toString(position)); 
    	        		sections.add(new SectionedListAdapter.Section(position));
    	                
    	        	}
    	            previousServiceId = serviceId;
    	            cursor.moveToNext();
    	        }
    	    
    	        mContourSimpleCursorAdapter.swapCursor(cursor); //swap the new cursor in.

    	        SectionedListAdapter.Section[] dummy =
    	                new SectionedListAdapter.Section[sections.size()];
    	        mSectionedListAdapter.setSections(sections.toArray(dummy));
    	   
    	}
    }
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle data) {
		if(id == ContoursByServicesQuery._TOKEN){
			return new CursorLoader(getActivity(), HaccpContract.Contours.CONTENT_URI, ContoursByServicesQuery.PROJECTION, null, null, HaccpContract.Contours.SERVICE_SORT);
		}
		else if(id == ServicesQuery._TOKEN){
			return new CursorLoader(getActivity(), HaccpContract.Services.CONTENT_URI, ServicesQuery.PROJECTION, null, null, HaccpContract.Services.DEFAULT_SORT);
		}
		throw new IllegalArgumentException("unknown loader id: " + id);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
	        if (loader.getId() == ContoursByServicesQuery._TOKEN) {
	        	onContoursLoadfinished(cursor);
	        }else if (loader.getId() == ServicesQuery._TOKEN) {
	        	onServicesLoadFinished(cursor);
	        }
	        else throw new IllegalArgumentException("unknown loader id: " + loader.getId());
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		// TODO Auto-generated method stub
		switch (loader.getId()) {
			case ContoursByServicesQuery._TOKEN:
				 if(mContourSimpleCursorAdapter!=null) 
					 mContourSimpleCursorAdapter.swapCursor(null);
		    break;
			case ServicesQuery._TOKEN:
				 if(mServiceSimpleCursorAdapter!=null) 
					 mServiceSimpleCursorAdapter.swapCursor(null);
		    break;
			default:
				throw new IllegalArgumentException("unknown loader id: " + loader.getId());
			
		}

	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		if(!mSectionedListAdapter.isSectionHeaderPosition(position)){
			
			Contour contour = null;
			Service service = null;
			
			Cursor cursor = (Cursor)mSectionedListAdapter.getItem(position);
			
			int uid = cursor.getInt(ContoursByServicesQuery.CONTOUR_UID);
			int color = cursor.getInt(ContoursByServicesQuery.CONTOUR_COLOR);
			String name = cursor.getString(ContoursByServicesQuery.CONTOUR_NAME);
			int serviceId = cursor.getInt(ContoursByServicesQuery.CONTOUR_SERVICE_ID);
			String serviceName = cursor.getString(ContoursByServicesQuery.SERVICE_NAME);
				
			service = new Service(serviceId, serviceName);
			contour = new Contour(uid, name, color, service);
			
			if(mOnContourPressedListener != null)
				mOnContourPressedListener.onContourPressed(contour);
			
			
		}
	}

}
