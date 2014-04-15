package com.itdoors.haccp.fragments;

import java.util.List;

import org.apache.http.NameValuePair;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.actionbarsherlock.app.SherlockFragment;
import com.itdoors.haccp.R;
import com.itdoors.haccp.json.JSON;
import com.itdoors.haccp.json.JSONRequestNameValuePair;
import com.itdoors.haccp.json.JSONResponce;
import com.itdoors.haccp.parser.Parser;

public abstract class  LoadingFragment extends SherlockFragment {
	
	private String url;
	private List<NameValuePair> loadingParams;
	private Parser mParser;
	private static final int delay = 1000; //1 second
	
	private View mErrorView;
	private View mLoadingView;
	
	private Object mContent;
	
	private boolean isError = false;
	
	public abstract void fillViews();
	
	public Object getContent(){
		return this.mContent;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setRetainInstance(true);
		
			final DisplayMetrics displayMetrics = new DisplayMetrics();
	        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		      
	    	mLoadingView = getLoadingView();
			mErrorView = getErrorView();
			
			if(mContent == null){
				if(!isError)((ViewGroup)getView()).addView(mLoadingView);
				else ((ViewGroup)getView()).addView(mErrorView);
			}
				
	        
	}
	
	public void load(){
		isError = false;
		JSONRequestNameValuePair request = new JSONRequestNameValuePair(url, loadingParams, new JSONRequestNameValuePair.Callback() {
			@Override
			public void onComplete(JSONResponce responce) {
				processResponce(responce);
			}

		});
		JSON.process(request, mParser, delay);
	}
	
	protected void setUrl(String url){
		this.url = url;
	}
	protected void setLoadingParams(List<NameValuePair> params){
		this.loadingParams = params;
	}
	protected void setPraser(Parser parser){
		this.mParser = parser;
	}
	
	protected void onError(Exception e){
		mContent = null;
		isError = true;
	
		if(isAdded()){
			((ViewGroup)getView()).removeView(mLoadingView);
			((ViewGroup)getView()).addView(mErrorView);
		}
	}
	
	private View getLoadingView(){
		return getActivity().getLayoutInflater().inflate(R.layout.loading, null);
	}
	
	private View getErrorView(){
		View root = getActivity().getLayoutInflater().inflate(R.layout.failed, null);
		Button retryBtn = (Button)root.findViewById(R.id.retry_load_btn);
		retryBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				retry();
			}
		});
		return root;
	}
	
	private void retry(){
		if(isAdded()){
			((ViewGroup)getView()).removeView(mErrorView);
			((ViewGroup)getView()).addView(mLoadingView);
			load();
		}
	}
	
	
	
	protected void processResponce(JSONResponce responce){
		if(responce.getException()!=null){
			onError(responce.getException());
		}
		else{
			onDataReady(responce.getContent());
		}
	}
	protected void onDataReady(Object result){
		
		if(result != null){
			mContent = result;
			if(isAdded()){
				((ViewGroup)getView()).removeView(mLoadingView);
			}
			fillViews();
		}
	}
}
