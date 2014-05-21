package com.itdoors.haccp.ui.fragments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import android.app.Activity;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.itdoors.haccp.R;
import com.itdoors.haccp.model.GroupCharacteristic;
import com.itdoors.haccp.model.GroupCharacteristicField;
import com.itdoors.haccp.model.InputType;
import com.itdoors.haccp.model.Point;
import com.itdoors.haccp.model.PointStatus;
import com.itdoors.haccp.model.StatististicsItemStatus;
import com.itdoors.haccp.model.PointStatus.CODE;
import com.itdoors.haccp.parser.StaticticsAddInputParser.Content;

public class AddStatisticsFragmentV0 extends SherlockFragment {
	
	public interface OnAddPressedListener{
		public void onAddPressed(HashMap<GroupCharacteristic, Double> values);
		public void changeStatusPressed(PointStatus status);
	}
	
	private static final String CONTENT_TAG = "com.itdoors.haccp.fragments.AddStatisticsFragment.CONTENT_TAG";
	private static final String POINT_TAG = "com.itdoors.haccp.fragments.AddStatisticsFragment.POINT_TAG";
	private static final String STATUSES_TAG = "com.itdoors.haccp.fragments.AddStatisticsFragment.STATUSES_TAG";
	
	private static final String CHEAKED_RADIO_BTN_SAVE = "com.itdoors.haccp.fragments.AddStatisticsFragment";
	
	private HashMap<GroupCharacteristicField, View> viewValueContainersMap;
	private HashMap<GroupCharacteristicField, View> viewContainersMap;
	
	
	
	private OnAddPressedListener mOnAddPressedListener;
	private RadioGroup mRadioGroup;
	
	public static AddStatisticsFragmentV0 newInstance(Content content){
		
		Bundle args = new Bundle();
		args.putSerializable(CONTENT_TAG, content);
		AddStatisticsFragmentV0 f = new AddStatisticsFragmentV0();
		f.setArguments(args);
		
		return f;
	}

	public static AddStatisticsFragmentV0 newInstance(Content content, Point point, ArrayList<PointStatus> statuses){
		
		Bundle args = new Bundle();
		args.putSerializable(CONTENT_TAG, content);
		args.putSerializable(POINT_TAG, point);
		args.putSerializable(STATUSES_TAG, statuses);
		
		AddStatisticsFragmentV0 f = new AddStatisticsFragmentV0();
		f.setArguments(args);
		
		return f;
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mOnAddPressedListener = (OnAddPressedListener)activity;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ViewGroup root = (ViewGroup)inflater.inflate(R.layout.fragment_add_statistics, container, false);
		TextView number = (TextView)root.findViewById(R.id.add_st_char_point_number);
		
		if(getArguments() != null){
			
			Point point = (Point)getArguments().getSerializable(POINT_TAG);
			if(number != null)
				number.setText(Integer.toString(point.getNumber()));
			
			Content mContent = (Content)getArguments().getSerializable(CONTENT_TAG);
			setCharacteristicsViews(inflater, root, mContent);
		
			@SuppressWarnings({ "unchecked"})
			ArrayList<PointStatus> statuses = (ArrayList<PointStatus>)getArguments().getSerializable(STATUSES_TAG);
			setStatusesRadioGroup(inflater, root, statuses, point);
			
		}
		
		Button addBtn = (Button)root.findViewById(R.id.add_st_done_btn);
		addBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(mOnAddPressedListener != null){
					final int chechedId = mRadioGroup.getCheckedRadioButtonId(); 
					if( chechedId!= -1 )	{
						PointStatus status = (PointStatus)mRadioGroup.findViewById(chechedId).getTag();
						mOnAddPressedListener.changeStatusPressed(status);
					}
					else{
						mOnAddPressedListener.onAddPressed(getValues());
					}
				}
			}
		});
		if(savedInstanceState != null){
		
			int checkedId = savedInstanceState.getInt(CHEAKED_RADIO_BTN_SAVE);
			if(checkedId != -1){
				PointStatus status = (PointStatus)mRadioGroup.findViewById(checkedId).getTag();
				if(!status.getCode().equals(CODE.WORKING))
					blockAllCharacteristicsFields();
			}
				
		}
		return root;
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		int checkedId = mRadioGroup.getCheckedRadioButtonId();
		outState.putInt(CHEAKED_RADIO_BTN_SAVE, checkedId);
		
	}
	
	private void setStatusesRadioGroup(LayoutInflater inflater, ViewGroup container, ArrayList<PointStatus> statuses, Point point){
		
		RadioGroup group = (RadioGroup)container.findViewById(R.id.add_st_statuses_radio_group);
		Iterator<PointStatus> iterator = statuses.iterator();
		
		int checkedId = - 1;
		while (iterator.hasNext()) {
			PointStatus status = iterator.next();
			if(status.getCode().equals(point.getStatus().getCode()) && !status.getCode().equals(PointStatus.CODE.WORKING))
				checkedId = status.getId();
			
			View view = getRadioView(inflater, container, status);
			group.addView(view);
		}
		
		
		group.check(checkedId);
		if(checkedId != -1) 
			blockAllCharacteristicsFields();
		
		group.setOnCheckedChangeListener( new RadioGroup.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if(checkedId == -1){
					releasAllCharacteristicsFields();
				}
				else {
					blockAllCharacteristicsFields();
				}
			}
		});
		
		this.mRadioGroup = group;
		
	}
	
	

	
	private void blockAllCharacteristicsFields(){
		
		for(Map.Entry<GroupCharacteristicField,View> entry : viewContainersMap.entrySet()){
			
			GroupCharacteristicField field = entry.getKey();
			View view = entry.getValue();
			InputType inputType = field.getInputType();
			
			switch (inputType) {
				case RANGE:
					
					final TextView nameTV = (TextView)view.findViewById(R.id.add_st_char_name);
					final SeekBar seekBar = (SeekBar)view.findViewById(R.id.add_st_char_seak_bar);
					
					if(seekBar.isEnabled())
						nameTV.setPaintFlags(nameTV.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
					seekBar.setEnabled(false);
					
					
				break;
			}
		}
	
	}
	
	private void releasAllCharacteristicsFields(){
		
		for(Map.Entry<GroupCharacteristicField,View> entry : viewContainersMap.entrySet()){
			
			GroupCharacteristicField field = entry.getKey();
			View view = entry.getValue();
			InputType inputType = field.getInputType();
			
			switch (inputType) {
				case RANGE:
					
					final TextView nameTV = (TextView)view.findViewById(R.id.add_st_char_name);
					final SeekBar seekBar = (SeekBar)view.findViewById(R.id.add_st_char_seak_bar);
					
					if(!seekBar.isEnabled())
						nameTV.setPaintFlags(nameTV.getPaintFlags() ^ Paint.STRIKE_THRU_TEXT_FLAG);
					seekBar.setEnabled(true);
					
					
					
				break;
			}
		}
	}
	
	private void setCharacteristicsViews(LayoutInflater inflater, ViewGroup container, Content content){
		LinearLayout holder = (LinearLayout)container.findViewById(R.id.add_st_char_fields_holder);
		
		viewValueContainersMap = new HashMap<GroupCharacteristicField, View>();
		viewContainersMap = new HashMap<GroupCharacteristicField, View>();
		
		Iterator<GroupCharacteristicField> iterator = content.characteristicFields.iterator();
		while(iterator.hasNext()){
			
			GroupCharacteristicField field = iterator.next();
			View view = getFieldView(inflater, holder, field);
			holder.addView(view);
			viewContainersMap.put(field, view);
		}
	}
	
	private View getRadioView(LayoutInflater inflater, ViewGroup container, PointStatus status){
		
		RadioButton button = new RadioButton(getActivity());
		@SuppressWarnings("deprecation")
		RadioGroup.LayoutParams lp = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.FILL_PARENT, RadioGroup.LayoutParams.WRAP_CONTENT);
		button.setLayoutParams(lp);
		button.setId(status.getId());
		button.setText(status.getName());
		button.setTag(status);
		
		return button;
	}
	
	private View getFieldView(LayoutInflater inflater, ViewGroup container, GroupCharacteristicField field){
		
		InputType inputType = field.getInputType();
		View view = null;
		switch (inputType) {
			case RANGE:
			{
				
				view = inflater.inflate(R.layout.list_item_range_add_statisctics,container, false);
				
				final TextView unitTV = (TextView)view.findViewById(R.id.add_st_char_unit);
				final TextView valueTV = (TextView)view.findViewById(R.id.add_st_char_value);
				final TextView nameTV = (TextView)view.findViewById(R.id.add_st_char_name);
				final SeekBar seekBar = (SeekBar)view.findViewById(R.id.add_st_char_seak_bar);
				
				final TextView minTV = (TextView)view.findViewById(R.id.add_st_char_min_value);
				final TextView maxTV = (TextView)view.findViewById(R.id.add_st_char_max_value);
				
				final View statusView = view.findViewById(R.id.add_st_char_status);
				
				if(field.getCharacteristic() != null){
					
					GroupCharacteristic characteristics = field.getCharacteristic();
					unitTV.setText(characteristics.getUnit());
					nameTV.setText(characteristics.getName() + ":");
					
					final int minimum = characteristics.getMinValue();
					final int maximum = characteristics.getMaxValue();
					
					minTV.setText(Integer.toString(minimum) );
					maxTV.setText(Integer.toString(maximum) );
					
					
					final int bottom = characteristics.getCriticalBottomValue();
					final int top = characteristics.getCriticalTopValue();
					
					int defValue = (bottom + top) / 2;
					valueTV.setText(Integer.toString(defValue));
					
					StatististicsItemStatus status = getStatus(defValue, top, bottom);
					
					setStatus(status, statusView);
					
					//seekBar.setMax(maximum);
					seekBar.setProgress(defValue);
				
				
					seekBar.setOnSeekBarChangeListener( new SeekBar.OnSeekBarChangeListener() {
						
						@Override
						public void onStopTrackingTouch(SeekBar seekBar){}
						
						@Override
						public void onStartTrackingTouch(SeekBar seekBar){}
						
						@Override
						public void onProgressChanged(SeekBar seekBar, int progress,
								boolean fromUser) {
							
							int value = getValue(seekBar, minimum, maximum);
							valueTV.setText(Integer.toString(value));
							setStatus( getStatus(value, top, bottom), statusView);
							
						}
					});
				
				}
				viewValueContainersMap.put(field, seekBar);
			
			}	
			break;
		}
		if( view != null ){
			view.setOnClickListener( new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					mRadioGroup.clearCheck();
					releasAllCharacteristicsFields();
				}
			});
		}
		
		return view;
		
	}
	
	private StatististicsItemStatus getStatus(double value, double top, double bottom){
		
		StatististicsItemStatus cpStatus = StatististicsItemStatus.APPROVED;
		if(value <= bottom)
			cpStatus = StatististicsItemStatus.APPROVED;
		else if( value > bottom && value < top )
			cpStatus = StatististicsItemStatus.WARNING;
		else if( value >= top)
			cpStatus = StatististicsItemStatus.DANGER;
		return cpStatus;
			
	}
	
	private void setStatus(StatististicsItemStatus status, View view){
		switch (status) {
			case WARNING:	view.setBackgroundResource(R.color.status_warning);		break;
			case DANGER:	view.setBackgroundResource(R.color.status_danger);		break;
			default:		view.setBackgroundResource(R.color.status_approved);	break;
		}	
		
	}
	
	private int getValue (SeekBar seekBar, int min, int max){
	
		int progress = seekBar.getProgress();
		double value = (double)(max - min) * ( (double)progress / 100);
		int intVlue = Double.valueOf(value).intValue();
		return intVlue;
	}
	
	public HashMap<GroupCharacteristic, Double> getValues(){
		
		HashMap<GroupCharacteristic, Double> values = new HashMap<GroupCharacteristic, Double>();
		for (Map.Entry<GroupCharacteristicField,View> entry : viewValueContainersMap.entrySet()) {
			
			GroupCharacteristicField characteristicField = entry.getKey();
			View view = entry.getValue();
			InputType inputType = characteristicField.getInputType();
			
			Double value = 0.0;
			switch (inputType) {
				case RANGE:
					
					int minimum = characteristicField.getCharacteristic().getMinValue();
					int maximum = characteristicField.getCharacteristic().getMaxValue();
					value = Double.valueOf( getValue((SeekBar)view, minimum, maximum));
				
					break;
			}
			
			GroupCharacteristic characteristic = characteristicField.getCharacteristic();
			values.put(characteristic, value);
		}
		return values;
	}
}
