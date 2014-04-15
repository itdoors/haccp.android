package com.itdoors.haccp.fragments;

import java.util.ArrayList;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.itdoors.haccp.R;
import com.itdoors.haccp.model.Point;
import com.itdoors.haccp.model.StatisticsRecord;
import com.itdoors.haccp.parser.ControlPointParser.Content;

public class PointShortInfoFragment extends SherlockFragment {
	
	public static final String CONTROL_POINT_INFO_TAG = "com.itdoors.haccp.fragments.ControlPointShortInfoFragment.CONTROL_POINT_INFO_TAG";
    public static final String STATISTICS_INFO_TAG = "com.itdoors.haccp.fragments.ControlPointShortInfoFragment.STATISTICS_INFO_TAG";
    
    private static final String POINT_INFO_SAVE = "com.itdoord.haccp.fragments.ControlPointShortInfoFragment.POINT_INFO_SAVE";
	private Point mControlPoint;
	
	public static PointShortInfoFragment newInstance(Point point, ArrayList<StatisticsRecord> records){
		
		PointShortInfoFragment f = new PointShortInfoFragment();
		
		Bundle bundle = new Bundle();
		bundle.putSerializable(CONTROL_POINT_INFO_TAG, point);
		bundle.putSerializable(STATISTICS_INFO_TAG, records);
		f.setArguments(bundle);
		
		return f;
	}

	private TextView pointNameTv;
	private TextView pointTypeTv;
	
	
	@SuppressWarnings("unchecked")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.control_point_short, null);
		pointNameTv = (TextView)view.findViewById(R.id.cp_short_name);
		pointTypeTv = (TextView)view.findViewById(R.id.cp_short_type);
		
		Point point = null;
		if(getArguments() != null){
			
			Content content = new Content();
			content.point = (Point)getArguments().getSerializable(CONTROL_POINT_INFO_TAG);
			content.records = (ArrayList<StatisticsRecord>)getArguments().getSerializable(STATISTICS_INFO_TAG);
			fillUI(content);
			
			point = content.point;
		}
		
		if(savedInstanceState != null)
			point = (Point)savedInstanceState.getSerializable(POINT_INFO_SAVE);
		if(point != null)
			updateUI(point);
		
		return view;
		
	}

	public void fillUI(Content content) {
		
		if(content != null)
		{
			this.mControlPoint = content.point; 
		
			pointNameTv.setText(content.point == null || content.point.getPlan() == null ? "-" : content.point.getPlan().getName());
			
			if(content.point != null && content.point.getGroup() != null)
				pointTypeTv.setText(content.point.getGroup().getName());
		
		}	
		else{
			
			pointNameTv.setText("");
			pointTypeTv.setText("");
		
		}
	}
	
	public void updateUI(Point point) {
		
		this.mControlPoint = point;
		
		pointNameTv.setText(point == null || point.getPlan() == null ? "-" : point.getPlan().getName());
		if(point != null && point.getGroup() != null)
			pointTypeTv.setText(point.getGroup().getName());
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if(mControlPoint != null){
			outState.putSerializable(POINT_INFO_SAVE, mControlPoint);
		}
	}
}
