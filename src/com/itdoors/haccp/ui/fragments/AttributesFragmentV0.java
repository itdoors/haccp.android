package com.itdoors.haccp.ui.fragments;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import com.itdoors.haccp.Global;
import com.itdoors.haccp.R;
import com.itdoors.haccp.model.Point;
import com.itdoors.haccp.model.PointStatus;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class AttributesFragmentV0 extends Fragment{
	
	private static final String POINT_INFO_SAVE = "com.itdoord.haccp.fragments.AttributesFragment.POINT_INFO_SAVE";
	
	private Point mControlPoint;
	
	private final String[] viewsTags = {
		
		"number",
		"installation_date",
		"who_set",
		"type",
		"level_of_multi-barrier",
		"monitoring_object",
		"status"
		
	};

	private HashMap<String, View> contentViews;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
		
    }
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.fragment_point_attributes, container, false);
		contentViews = new HashMap<String, View>();
		
		contentViews = new HashMap<String, View>();
		
		contentViews.put(viewsTags[0], view.findViewById(R.id.cp_attr_point_number));
		contentViews.put(viewsTags[1], view.findViewById(R.id.cp_attr_inst_date));
		contentViews.put(viewsTags[2], view.findViewById(R.id.cp_attr_who_set));
		contentViews.put(viewsTags[3], view.findViewById(R.id.cp_attr_point_type));
		contentViews.put(viewsTags[4], view.findViewById(R.id.cp_attr_mlevel));
		contentViews.put(viewsTags[5], view.findViewById(R.id.cp_attr_object));
		contentViews.put(viewsTags[6], view.findViewById(R.id.cp_attr_status));
		
		Point point = null;
		if(getArguments() != null ){
			point = (Point)getArguments().getSerializable(PointDetailsFragmentV0.CONTROL_POINT_INFO_TAG);
			fillViews(mControlPoint);
		}
		if(savedInstanceState != null){
			point = (Point)savedInstanceState.getSerializable(POINT_INFO_SAVE);
		}
		if(point != null)
			fillViews(point);
		
		return view;
	}
	
	@SuppressLint("SimpleDateFormat")
	public void fillViews(final Point point){
		
		this.mControlPoint = point;
		
		if(point != null){
			
			Date date = point.getInstallationDate();
			String number = Integer.toString(point.getNumber());
			String instDate = date == null ? "-" : new SimpleDateFormat(Global.usualDateFromat).format(date) .toString();;
			String owner = point.getOwner() == null ? "Михайличенко" : point.getOwner().getName();
			String type = point.getGroup() == null ? "-" : point.getGroup().getName();
			String multiBurrierLevel = point.getContour() == null ? "-" : point.getContour().getName();
			String monitoringObject = point.getPlan() == null ? "-" : point.getPlan().getName();
			String status = point.getStatus() == null ? "-" : point.getStatus().getName() == null ? "-" : point.getStatus().getName();
			
			
			((TextView)contentViews.get(viewsTags[0])).setText(number);
			((TextView)contentViews.get(viewsTags[1])).setText(instDate);
			((TextView)contentViews.get(viewsTags[2])).setText(owner);
			((TextView)contentViews.get(viewsTags[3])).setText(type);
			((TextView)contentViews.get(viewsTags[4])).setText(multiBurrierLevel);
			((TextView)contentViews.get(viewsTags[5])).setText(monitoringObject);
			((TextView)contentViews.get(viewsTags[6])).setText(status);

		}
		else{
			
			String empty = "";
			((TextView)contentViews.get(viewsTags[0])).setText(empty);
			((TextView)contentViews.get(viewsTags[1])).setText(empty);
			((TextView)contentViews.get(viewsTags[2])).setText(empty);
			((TextView)contentViews.get(viewsTags[3])).setText(empty);
			((TextView)contentViews.get(viewsTags[4])).setText(empty);
			((TextView)contentViews.get(viewsTags[5])).setText(empty);
			((TextView)contentViews.get(viewsTags[6])).setText(empty);
			
		}
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if(mControlPoint != null)
			outState.putSerializable(POINT_INFO_SAVE, mControlPoint);
	}

	public void changeStatusOnAttributes(PointStatus status) {
		((TextView)contentViews.get(viewsTags[6])).setText(status.getName());
	}
}
