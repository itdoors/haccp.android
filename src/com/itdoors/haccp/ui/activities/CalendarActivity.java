package com.itdoors.haccp.ui.activities;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.itdoors.haccp.Intents;
import com.itdoors.haccp.R;
import com.itdoors.haccp.utils.CalendarUtils;
import com.itdoors.haccp.utils.ToastUtil;
import com.squareup.timessquare.CalendarPickerView;
import com.squareup.timessquare.CalendarPickerView.FluentInitializer;
import com.squareup.timessquare.CalendarPickerView.SelectionMode;

public class CalendarActivity extends SherlockActivity {
	
	@SuppressWarnings("unused")
	private static final String TAG = "com.itdoors.haccp.activities.ChoosingCustomTimeRangeActivity";
	private static final String SELECTED_DATES = "com.itdoors.haccp.activities.ChoosingCustomTimeRangeActivity.SELECTED_DATES";
	
	private CalendarPickerView calendar;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_custom_time_range);
	    setTitle(R.string.choose_range);
	    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	    
	    final Calendar nextYear = Calendar.getInstance();
	    nextYear.add(Calendar.YEAR, 1);

	    final Calendar lastYear = Calendar.getInstance();
		lastYear.add(Calendar.YEAR, -1);

		calendar = (CalendarPickerView) findViewById(R.id.calendar_view);
		
		FluentInitializer initializer = calendar.init(lastYear.getTime(), nextYear.getTime()) //
	    										.inMode(SelectionMode.RANGE);
	    restoreSelectedRange(initializer, savedInstanceState);
	   
	}
	
	private void restoreSelectedRange( FluentInitializer initializer, Bundle savedInstanceState){
		 
		Calendar today = Calendar.getInstance();
		if(savedInstanceState == null){
			initializer.withSelectedDate(today.getTime());
			return;
		}
		
	   	List<Date> dates = restoreSelectedDates(savedInstanceState);
		if(dates == null || dates.isEmpty()){
			initializer.withSelectedDate(today.getTime());
			return;
		}
			
		List<Date> firstAndLast = new ArrayList<Date>();
		firstAndLast.add(dates.get(0));
		if(dates.size() > 1)
			firstAndLast.add( dates.get( dates.size() - 1));
		initializer.withSelectedDates(firstAndLast);
		 
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    menu.add(Menu.NONE, 0 , Menu.NONE, getString(R.string.done))
	        .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				setResult(RESULT_CANCELED);
				finish();
			return true;
			case 0 :
				onDonePressed();
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void onDonePressed(){
		
		List<Date> dates = calendar.getSelectedDates();
		
		if(dates != null && dates.size() >= 2){
			
			Date fromDate = CalendarUtils.getStartOfDay(dates.get(0));
			Date toDate = CalendarUtils.getEndOfDay(dates.get(dates.size() - 1));
			
			
			String fromTimeStamp = Long.toString(fromDate.getTime() / 1000);
			String toTimeStamp =  Long.toString(toDate.getTime() / 1000);
			
			Intent intent = new Intent();
			intent.putExtra(Intents.CalendarTimeRange.FROM_TIME_STAMP, fromTimeStamp);
			intent.putExtra(Intents.CalendarTimeRange.TO_TIME_STAMP, toTimeStamp);
			setResult(RESULT_OK, intent);
			finish();
		}
		else{
			ToastUtil.ToastLong(this, getResources().getString(R.string.no_time_range_has_been_selected));
		}
	}
	

	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		saveSelectedDates(outState);
	}
	
	private void saveSelectedDates(Bundle outState){

		List<Date> dates = calendar.getSelectedDates();
		if(dates != null && !dates.isEmpty())
			outState.putSerializable(SELECTED_DATES, (ArrayList<Date>)dates);
	}
	
	
	@SuppressWarnings("unchecked")
	private List<Date> restoreSelectedDates(Bundle savedInstanceState){
		return (ArrayList<Date>)savedInstanceState.getSerializable(SELECTED_DATES);
	}
	
}
