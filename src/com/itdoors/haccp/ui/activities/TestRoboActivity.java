package com.itdoors.haccp.ui.activities;

import com.itdoors.haccp.R;
import com.itdoors.haccp.model.rest.retrofit.MoreStatistics;
import com.itdoors.haccp.rest.robospice_retrofit.GetStatisticsRequest;
import com.itdoors.haccp.rest.robospice_retrofit.MySpiceService;
import com.itdoors.haccp.utils.ToastUtil;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.PendingRequestListener;
import com.octo.android.robospice.request.listener.RequestListener;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;


public class TestRoboActivity extends Activity implements OnClickListener{

	private SpiceManager spiceManager = new SpiceManager(MySpiceService.class);
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test_robo);
		Button submit = (Button)findViewById(R.id.test_query_btn);
		submit.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.test_query_btn){
			
			int pointId = 2;
			GetStatisticsRequest request = new GetStatisticsRequest.Builder()
									.setId(pointId)
									.build();
			
			spiceManager.execute(request, "test", 5*DurationInMillis.ONE_MINUTE, mStatisticsRequestListener);

		}
	}
	
	@Override
	protected void onStart() {
		spiceManager.start(this);
		super.onStart();
	}
	@Override
	protected void onResume() {
		super.onResume();
		spiceManager.addListenerIfPending(MoreStatistics.class, "test", mStatisticsPendingRequestListener);
		
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	}
	
	
	@Override
	protected void onStop() {
		spiceManager.shouldStop();
		super.onStop();
	}
	
	public SpiceManager getSpiceManager(){
		return spiceManager;
	}
	
	RequestListener<MoreStatistics> mStatisticsRequestListener = new RequestListener<MoreStatistics>() {
		@Override
		public void onRequestFailure(SpiceException exception) {
			ToastUtil.ToastLong(getApplicationContext(), getString(R.string.failed_to_load_data));
		}
		@Override
		public void onRequestSuccess(MoreStatistics statistics) {
			ToastUtil.ToastLong(getApplicationContext(), statistics.toString());
		}
	};
	
	PendingRequestListener<MoreStatistics> mStatisticsPendingRequestListener = new PendingRequestListener<MoreStatistics>() {

		@Override
		public void onRequestFailure(SpiceException exception) {
			ToastUtil.ToastLong(getApplicationContext(), getString(R.string.failed_to_load_data));
		}

		@Override
		public void onRequestSuccess(MoreStatistics statistics) {
			ToastUtil.ToastLong(getApplicationContext(), statistics.toString());
		}

		@Override
		public void onRequestNotFound() {
		}
	};
}
