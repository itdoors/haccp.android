package com.itdoors.haccp.utils;


import com.itdoors.haccp.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class ContextUtils {
	public static View getLoadingView(Context context, ViewGroup parent){
		return ((LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE )).inflate(R.layout.loading, parent, false);
	}
	public static View getErrorWhileConnectionView(Context context, ViewGroup parent, View.OnClickListener retryListener){
		LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		View root = inflater.inflate(R.layout.failed, parent, false);
		Button retryBtn = (Button)root.findViewById(R.id.retry_load_btn);
		retryBtn.setOnClickListener(retryListener);
		return root;
	}
}
