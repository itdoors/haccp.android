package com.itdoors.haccp.ui.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragment;
import com.itdoors.haccp.R;
import com.itdoors.haccp.ui.interfaces.SetQRCallback;
import com.itdoors.haccp.ui.interfaces.TakeQRCallback;
import com.itdoors.haccp.ui.interfaces.TakeQRListener;

public class QRFragment extends SherlockFragment implements TakeQRCallback{
	
	private static final String VERFICATION_CODE_SAVE_KEY = "com.itdoors.fragments.QRFagment.SAVE_SCANED_CODE";

	private View QRBtn;
	
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
		}
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
		((SetQRCallback)getActivity()).setTakeCodeFromCameraCallBack(null);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.fragment_scan_code, null);
		QRBtn = view.findViewById(R.id.qr_btn);
		QRBtn.setOnClickListener( new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(mListener != null){
					mListener.takeQR();
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
