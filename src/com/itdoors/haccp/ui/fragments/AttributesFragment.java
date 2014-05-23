package com.itdoors.haccp.ui.fragments;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.itdoors.haccp.Global;
import com.itdoors.haccp.Intents;
import com.itdoors.haccp.R;
import com.itdoors.haccp.provider.HaccpContract;
import com.itdoors.haccp.utils.Logger;

import android.annotation.SuppressLint;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class AttributesFragment extends Fragment implements  LoaderCallbacks<Cursor>{
	
	private ContentObserver mObserver = new ContentObserver(new Handler()) {
	        @Override
	        public void onChange(boolean selfChange) {
	            if (getActivity() == null) {
	                return;
	            }
	            getLoaderManager().restartLoader(PointQuery._TOKEN, null, AttributesFragment.this);
	        }
    };

    @Override
    public void onAttach(android.app.Activity activity) {
    	super.onAttach(activity);
    		if(activity.getIntent().getExtras() != null ){
    			int point_id = activity.getIntent().getIntExtra(Intents.Point.UID, -1);
    			activity.getContentResolver().registerContentObserver(
    				HaccpContract.Points.buildPointUri(point_id), false, mObserver);
    		}
    };

    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_point_attributes, container, false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getLoaderManager().initLoader(PointQuery._TOKEN, null, this);
		
	}
	@Override
    public void onDetach() {
        super.onDetach();
        getActivity().getContentResolver().unregisterContentObserver(mObserver);

    }
	
	@SuppressWarnings("unused")
	private interface PointQuery{
	 
		int _TOKEN = 0;
		String[] PROJECTION = new String[]{
				HaccpContract.Points._ID,
				HaccpContract.Points.UID,
				HaccpContract.Points.NAME,
				HaccpContract.Points.INSTALATION_DATE,
				HaccpContract.Points.PLANS_UID_PROJECTION,
				HaccpContract.Points.PLANS_NAME_PROJECTION,
				HaccpContract.Points.CONTOUR_UID_PROJECTION,
				HaccpContract.Points.CONTOUR_NAME_PROJECTION,
				HaccpContract.Points.CONTOUR_SLUG_PROJECTION,
				HaccpContract.Points.STATUS_UID_PROJECTION,
				HaccpContract.Points.STATUS_NAME_PROJECTION,
				HaccpContract.Points.STATUS_SLUG_PROJECTION,
				HaccpContract.Points.GROUP_UID_PROJECTION,
				HaccpContract.Points.GROUP_NAME_PROJECTION
		};
	 	
		int _ID = 0;
		int UID = 1;
		int NAME = 2;
		int INSTALATION_DATE = 3;
		int PLANS_UID = 4;
		int PLANS_NAME = 5;
		int CONTOUR_UID = 6;
		int CONTOUR_NAME = 7;
		int CONTOUR_SLUG = 8;
		int STATUS_UID = 9;
		int STATUS_NAME = 10;
		int STATUS_SLUG = 11;
		int GROUP_UID = 12;
		int GROUP_NAME = 13;
	}
	
	@SuppressLint("SimpleDateFormat")
	private void fillViews(final Cursor cursor){
		
		if(cursor != null){
			cursor.moveToFirst();
			String timeStamp = cursor.getString(PointQuery.INSTALATION_DATE);
			Date date = null;
			try{
				date = new java.util.Date(Long.valueOf(timeStamp)*1000);
			}catch(Exception e){}
			
			String number = Integer.toString(cursor.getInt(PointQuery.NAME));
			String instDate = date == null ? "-" : new SimpleDateFormat(Global.usualDateFromat).format(date) .toString();;
			String owner = "Михайличенко";
			String type = cursor.getString(PointQuery.GROUP_NAME) == null ? "-" : cursor.getString(PointQuery.GROUP_NAME);
			String multiBurrierLevel = cursor.getString(PointQuery.CONTOUR_NAME) == null ? "-" : cursor.getString(PointQuery.CONTOUR_NAME);
			String monitoringObject = cursor.getString(PointQuery.PLANS_NAME) == null ? "-" : cursor.getString(PointQuery.PLANS_NAME);
			String status = cursor.getString(PointQuery.STATUS_NAME) == null ? "-" : cursor.getString(PointQuery.STATUS_NAME);
			
			Logger.Loge(getClass(), "status: " + (status == null ? "null" : status ));
			
			((TextView)getView().findViewById(R.id.cp_attr_point_number)).setText(number);
			((TextView)getView().findViewById(R.id.cp_attr_inst_date)).setText(instDate);
			((TextView)getView().findViewById(R.id.cp_attr_who_set)).setText(owner);
			((TextView)getView().findViewById(R.id.cp_attr_point_type)).setText(type);
			((TextView)getView().findViewById(R.id.cp_attr_mlevel)).setText(multiBurrierLevel);
			((TextView)getView().findViewById(R.id.cp_attr_object)).setText(monitoringObject);
			((TextView)getView().findViewById(R.id.cp_attr_status)).setText(status);
			
			getActivity().setTitle(type);

		}
		else{
			
			String empty = "";
			((TextView)getView().findViewById(R.id.cp_attr_point_number)).setText(empty);
			((TextView)getView().findViewById(R.id.cp_attr_inst_date)).setText(empty);
			((TextView)getView().findViewById(R.id.cp_attr_who_set)).setText(empty);
			((TextView)getView().findViewById(R.id.cp_attr_point_type)).setText(empty);
			((TextView)getView().findViewById(R.id.cp_attr_mlevel)).setText(empty);
			((TextView)getView().findViewById(R.id.cp_attr_object)).setText(empty);
			((TextView)getView().findViewById(R.id.cp_attr_status)).setText(empty);
			
		}
	}
	
	
	@Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		if(id == PointQuery._TOKEN)
			return new CursorLoader(getActivity(), 
        						HaccpContract.Points.buildPointUri(getActivity().getIntent().getIntExtra(Intents.Point.UID, -1)), 
        						PointQuery.PROJECTION, 
        						null, 
        						null, 
        						null );
		else throw new IllegalArgumentException("Unknown loader id: " + id);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (getActivity() == null) {
            return;
        }
        if (cursor != null && cursor.getCount() > 0) {
          fillViews(cursor);
        } else {
          fillViews((Cursor)null);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {}
}
