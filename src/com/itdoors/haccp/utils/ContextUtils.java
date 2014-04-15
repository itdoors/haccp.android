package com.itdoors.haccp.utils;


import com.itdoors.haccp.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

public class ContextUtils {
	public static View getLoadingView(Context context){
		return ((LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE )).inflate(R.layout.loading, null);
	}
	public static View getErrorWhileConnectionView(Context context, View.OnClickListener retryListener){
		LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		View root = inflater.inflate(R.layout.failed, null);
		Button retryBtn = (Button)root.findViewById(R.id.retry_load_btn);
		retryBtn.setOnClickListener(retryListener);
		return root;
	}
}
