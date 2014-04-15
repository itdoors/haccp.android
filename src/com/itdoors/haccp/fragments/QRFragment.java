package com.itdoors.haccp.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.actionbarsherlock.app.SherlockFragment;
import com.itdoors.haccp.R;
import com.itdoors.haccp.interfaces.TakeQRCallback;
import com.itdoors.haccp.interfaces.TakeQRListener;
import com.itdoors.haccp.interfaces.SetQRCallback;

public class QRFragment extends SherlockFragment implements TakeQRCallback{
	
	private static final String VERFICATION_CODE_SAVE_KEY = "com.itdoors.fragments.QRFagment.SAVE_SCANED_CODE";

	private View QRBtn;
	private View QRResultHolder;
	
	private EditText QRResultView;
	private TakeQRListener mListener;
	private String QRResult;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mListener = (TakeQRListener)activity;
		((SetQRCallback)activity).setTakeCodeFromCameraCallBack(this);
	}
	
	@Override
	public void codeFromCameraCallback(Intent data) {
		if(data.getExtras() != null){
			QRResult = data.getStringExtra("SCAN_RESULT");
			if(QRResult != null && !QRResult.equals("")){
				if(QRResultHolder != null){
					QRResultHolder.setVisibility(View.VISIBLE);
					QRResultView.setText(QRResult);
				}
				else if(QRResultView!=null){
					QRResultView.setVisibility(View.VISIBLE);
					QRResultView.setText(QRResult);
				}
				
			}
		}
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
		((SetQRCallback)getActivity()).setTakeCodeFromCameraCallBack(null);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		
		View view = inflater.inflate(R.layout.scan_code_frame, null);
		
		QRBtn = view.findViewById(R.id.qr_btn);
		QRResultHolder = view.findViewById(R.id.rq_result_holder);
		QRResultView = (EditText)view.findViewById(R.id.qr_result);
		if(QRResult != null && !QRResult.equals("")){
			if(QRResultHolder != null){
				QRResultHolder.setVisibility(View.VISIBLE);
				QRResultView.setText(QRResult);
			}
			else if(QRResultView!=null){
				QRResultView.setVisibility(View.VISIBLE);
				QRResultView.setText(QRResult);
			}
			
		}

		QRBtn.setOnClickListener( new View.OnClickListener() {
			
		
				@Override
				public void onClick(View v) {
				
					if(mListener != null){
						
						mListener.takeQR();
							/*							
							Integer id = Integer.valueOf(15);
							Intent intent = PointDetailsActivity.newInstance(getActivity(), id.intValue());
							startActivity(intent);
 							*/
						}
				}
			});
		
		return view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setRetainInstance(true);
		
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if(QRResult != null)
			outState.putString(VERFICATION_CODE_SAVE_KEY, QRResult);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		if(savedInstanceState != null){
			QRResult = savedInstanceState.getString(VERFICATION_CODE_SAVE_KEY);
			
		}
	}
	


}
