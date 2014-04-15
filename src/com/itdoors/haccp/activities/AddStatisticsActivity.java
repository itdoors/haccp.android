package com.itdoors.haccp.activities;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.json.JSONException;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.itdoors.haccp.Global;
import com.itdoors.haccp.Intents;
import com.itdoors.haccp.R;
import com.itdoors.haccp.exceptions.ServerFailedException;
import com.itdoors.haccp.fragments.AddStatisticsFragment;
import com.itdoors.haccp.loaders.RESTLoader;
import com.itdoors.haccp.loaders.RESTLoader.RESTResponse;
import com.itdoors.haccp.model.GroupCharacteristic;
import com.itdoors.haccp.model.Point;
import com.itdoors.haccp.model.PointStatus;
import com.itdoors.haccp.model.StatisticsRecord;
import com.itdoors.haccp.parser.AddStatisticResponceParser;
import com.itdoors.haccp.parser.StaticticsAddInputParser.Content;
import com.itdoors.haccp.utils.Logger;
import com.itdoors.haccp.utils.ToastUtil;

public class AddStatisticsActivity extends SherlockFragmentActivity implements LoaderCallbacks<RESTLoader.RESTResponse>, AddStatisticsFragment.OnAddPressedListener{
	
	public static final String CHARACTERISTICS_TAG = "com.itdoors.haccp.activities.AddStatisticsActivity.CHARACTERISTICS_TAG";
	public static final String POINT_TAG = "com.itdoors.haccp.activities.AddStatisticsActivity.PRODUCT_TAG";
	public static final String STATUSES_TAG = "com.itdoors.haccp.activities.AddStatisticsActivity.STATUSES_TAG";
	private static final String STATUSES_TO_CHANGE_SAVE_TAG = "com.itdoors.haccp.activities.AddStatisticsActivity.STATUSES_TO_CHANGE_SAVE_TAG";

	private static final String ADD_STATICTICS_FRAGMENT_TAG = "com.itdoors.haccp.activities.AddStatisticsActivity.ADD_STATICTICS_FRAGMENT_TAG";
	
	protected static final String ARGS_PIAS_URI = "com.itdoors.haccp.activities.AddStatisticsActivity.ARGS_PIAS_URI";
	protected static final String ARGS_PIAS_PARAMS_URI = "com.itdoors.haccp.activities.PointAddStatisticsActivity.ARGS_PIAS_PARAMS_URI";
	
	protected static final int ADD_STATISTIC_RECORD_CODE = 1;
	protected static final int CHANGE_STATUS_POINT_CODE = 2;

	private Fragment mFragment;
	
	private PointStatus statusToChange;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		requestWindowFeature(com.actionbarsherlock.view.Window.FEATURE_INDETERMINATE_PROGRESS);
		super.onCreate(savedInstanceState);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		setSupportProgressBarIndeterminateVisibility(false);
		setContentView(R.layout.add_statictics_frame);
		setTitle(R.string.add_statistic_record);
		
		initFragment();
		
		if(savedInstanceState != null){
			statusToChange = (PointStatus) savedInstanceState.getSerializable(STATUSES_TO_CHANGE_SAVE_TAG);
		}
	}
	
	protected void initFragment(){
		
		mFragment = getSupportFragmentManager().findFragmentByTag(ADD_STATICTICS_FRAGMENT_TAG );
		if(mFragment == null){
			if(getIntent().getExtras() != null){
				
				Content content = (Content)getIntent().getExtras().getSerializable(CHARACTERISTICS_TAG);
				Point point = (Point)getIntent().getExtras().getSerializable(POINT_TAG);
			
				@SuppressWarnings("unchecked")
				ArrayList<PointStatus> statuses = (ArrayList<PointStatus>)getIntent().getExtras().getSerializable(STATUSES_TAG);
				
				mFragment = AddStatisticsFragment.newInstance(content, point, statuses);
				getSupportFragmentManager()
					.beginTransaction()
					.add(R.id.add_statictics_frame,mFragment, ADD_STATICTICS_FRAGMENT_TAG)
					.commit();
			}
		}
	}
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if(statusToChange != null)
			outState.putSerializable(STATUSES_TO_CHANGE_SAVE_TAG, statusToChange);
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
	
	private void beginAddRequest(Point point,HashMap<GroupCharacteristic, Double> values) {
		
		
		int pointId = point.getId();
		
		String url = Global.API_URL + "/point/" + Integer.toString(pointId) + "/statistics"; 
		Uri addRequest = Uri.parse(url);
		
		Iterator<Entry<GroupCharacteristic, Double>> iterator = values.entrySet().iterator();
		Entry<GroupCharacteristic, Double> entry = null;
		
		if(iterator.hasNext())		entry = iterator.next();
		if(entry == null) return;
		
		GroupCharacteristic characteristic = entry.getKey();
		Double value = entry.getValue();
		
		String date = Long.toString(Calendar.getInstance().getTime().getTime() / 1000);;
		
		Bundle jsonArrayParams = new Bundle();
		jsonArrayParams.putString("pointStatisticsApiForm[characteristic]", Integer.toString(characteristic.getId()));
	    jsonArrayParams.putString("pointStatisticsApiForm[createdAt]", date);
	    jsonArrayParams.putString("pointStatisticsApiForm[entryDate]", date);
	    jsonArrayParams.putString("pointStatisticsApiForm[value]", Integer.toString(value.intValue()));
	   
		
	    
			
		Bundle args = new Bundle();
	    args.putParcelable(ARGS_PIAS_URI, addRequest);
	    args.putParcelable(ARGS_PIAS_PARAMS_URI, jsonArrayParams);
	    
	    showProgress();
	       
			if(  getSupportLoaderManager().getLoader(ADD_STATISTIC_RECORD_CODE) == null )
				 getSupportLoaderManager().initLoader(ADD_STATISTIC_RECORD_CODE, args, this);
			else getSupportLoaderManager().restartLoader(ADD_STATISTIC_RECORD_CODE, args, this);
		
		
		
		
		
	}
	
	private void beginChangeStatusRequest(Point point, final PointStatus status) {
		
		
		int pointId = point.getId();
		
		String url = Global.API_URL + "/point/" + Integer.toString(pointId) + "/status"; 
		Uri addRequest = Uri.parse(url);
		
		
		Bundle jsonArrayParams = new Bundle();
		jsonArrayParams.putString("pointStatusApiForm[statusId]", Integer.toString(status.getId()));
	   	
		Bundle args = new Bundle();
	    args.putParcelable(ARGS_PIAS_URI, addRequest);
	    args.putParcelable(ARGS_PIAS_PARAMS_URI, jsonArrayParams);
	    
	    showProgress();
	    
	    this.statusToChange = status;
	    
			if(  getSupportLoaderManager().getLoader(CHANGE_STATUS_POINT_CODE) == null )
				 getSupportLoaderManager().initLoader(CHANGE_STATUS_POINT_CODE, args, this);
			else getSupportLoaderManager().restartLoader(CHANGE_STATUS_POINT_CODE, args, this);
		
	}
	

	
	public void onDonePressed(){
		
		if(getIntent().getExtras() != null){
			
			Point point = (Point)getIntent().getExtras().getSerializable(POINT_TAG);
			HashMap<GroupCharacteristic, Double> values = ((AddStatisticsFragment)mFragment).getValues();
			beginAddRequest(point, values);
		}
		
	}
	
	protected void showProgress(){
		setSupportProgressBarIndeterminateVisibility(true);
	}
	protected void hideProgress(){
		setSupportProgressBarIndeterminateVisibility(false);
	}

	@Override
	public Loader<RESTResponse> onCreateLoader(int id, Bundle args) {
		
		if(id == ADD_STATISTIC_RECORD_CODE || id == CHANGE_STATUS_POINT_CODE){
			
			if (args != null && args.containsKey(ARGS_PIAS_URI) && args.containsKey(ARGS_PIAS_PARAMS_URI)) {
	            
				Uri    action = args.getParcelable(ARGS_PIAS_URI);
	            Bundle params = args.getParcelable(ARGS_PIAS_PARAMS_URI);
	            
	            return new RESTLoader(this, RESTLoader.HTTPVerb.POST, action, params);
        	}
		}
		
        
        return null;
	}

	@Override
	public void onLoadFinished(Loader<RESTResponse> loader, RESTResponse data) {
	 	
	    switch (loader.getId()) {
			
        	case ADD_STATISTIC_RECORD_CODE:
        		onAddStatisticFinished(data);
        		break;
        	case CHANGE_STATUS_POINT_CODE:
        		onChangeStatusFinished(data);
        		break;
        	default:
        		break;
		}
    }

	@Override
	public void onLoaderReset(Loader<RESTResponse> loader) {
	}
	
	private void onChangeStatusFinished(RESTResponse data){
		
		int    code = data.getCode();
        String json = data.getData();
    	
        hideProgress();
        
        if ((code == 201 || code == 200) ) {
        	ToastUtil.ToastLong(this, getString(R.string.status_successfully_changed));
        	setResultAndFinish(statusToChange);
        }
        else {
        	Logger.Logi(getClass(), "code: " + code +"; json: " + json);
        	ToastUtil.ToastLong(this, getString(R.string.failed_to_change_status));
        }
	}
	private void onAddStatisticFinished(RESTResponse data){

		int    code = data.getCode();
        String json = data.getData();
    	
        hideProgress();
        
        if ((code == 201 || code == 200) && !json.equals("")) {
        	
        	AddStatisticResponceParser parser = new AddStatisticResponceParser();
        	try {
        		
				final Object content = parser.parse(json);
				
				//set the resulted parsed content into local variable
				Logger.Logi(getClass(), "onAddStatisticFinished, json: " + json);
				
				//remove loading view
				
				StatisticsRecord record = ((com.itdoors.haccp.parser.AddStatisticResponceParser.Content)content).record;
				@SuppressWarnings("unused")
				Point point = (Point)getIntent().getExtras().getSerializable(POINT_TAG);
				
				//Show result in short and details fragments
				setResultAndFinish(record);
				
        	}
        	catch (JSONException e) {
				
        		ToastUtil.ToastLong(this, "JSONException.");
				e.printStackTrace();
			
			}
        	catch (ServerFailedException e) {
        		ToastUtil.ToastLong(this, "ServerFailedException.");
    	        e.printStackTrace();
    		}
        }
        else {
        	Logger.Logi(getClass(), "code: " + code +"; json: " + json);
        	ToastUtil.ToastLong(this, getString(R.string.failed_to_load_data));
        }
	}
	
	private void setResultAndFinish(StatisticsRecord record){
		
		Intent intent = new Intent();
		intent.putExtra(Intents.Statistic.STATISTIC_RECORD, record);
		setResult(RESULT_OK, intent);
		finish();
	}
	
	private void setResultAndFinish(PointStatus status){
		
		Intent intent = new Intent();
		intent.putExtra(Intents.Status.CHANGED_STATUS, status);
		setResult(RESULT_OK, intent);
		finish();
	}

	@Override
	public void onAddPressed(HashMap<GroupCharacteristic, Double> values) {
		
		if(getIntent().getExtras() != null){
			Point point = (Point)getIntent().getExtras().getSerializable(POINT_TAG);
			beginAddRequest(point, values);
		}
		
	}

	@Override
	public void changeStatusPressed(PointStatus status) {
		if(getIntent().getExtras() != null){
			Point point = (Point)getIntent().getExtras().getSerializable(POINT_TAG);
			beginChangeStatusRequest(point, status);
		}
	}
}
