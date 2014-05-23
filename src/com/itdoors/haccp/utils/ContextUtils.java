package com.itdoors.haccp.utils;


import com.itdoors.haccp.R;

import android.content.Context;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.TextView;

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
	
	public static View getEmptyListView(Context context){
		
		TextView textView = new TextView(context);
		textView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		textView.setTextSize(TypedValue.COMPLEX_UNIT_SP , 20);
		textView.setTextColor(context.getResources().getColor(android.R.color.black));
		textView.setText(context.getString(R.string.loading));
		textView.setGravity(Gravity.CENTER);
		return textView;
	}
}
