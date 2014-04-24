package com.itdoors.haccp.fragments;



import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragment;

import com.itdoors.haccp.R;

public class InitFragment extends SherlockFragment {

    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.init_test_frame, container, false);
	}
	
		
    
}
