package com.itdoors.haccp.activities;

import java.util.List;

import org.apache.http.NameValuePair;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.itdoors.haccp.R;
import com.itdoors.haccp.json.JSON;
import com.itdoors.haccp.json.JSONRequestNameValuePair;
import com.itdoors.haccp.json.JSONResponce;
import com.itdoors.haccp.model.enums.HTTP_METHOD;
import com.itdoors.haccp.parser.Parser;

public abstract class BaseLoadActivity extends SherlockFragmentActivity {
	
	private String url;
	private Parser mParser;
	private List<NameValuePair> loadingParams;
	private HTTP_METHOD httMethod;
	
	private boolean isError = false;
	private static final int delay = 1000; //1 second
	
	private View mErrorView;
	private View mLoadingView;
	
	private Object mContent;
	
	public abstract void onDataReady();
	
	public Object getContent(){
		return this.mContent;
	}
	
	public void setContent (Object o){
		mContent = o;
	}
	
	public void initLoadViews(){
		mLoadingView = getLoadingView();
		mErrorView = getErrorView();
			
		if(mContent == null){
			if(!isError)((ViewGroup)getView()).addView(mLoadingView);
			else ((ViewGroup)getView()).addView(mErrorView);
		}
	}
	

	
	public View getView(){
		return this.findViewById(android.R.id.content);
	}
	
	public void initLoadViewsIfNeed(){
		if(mContent == null) 
			initLoadViews();
	}
	public void loadIfNeed(){
		if(mContent == null) 
			load();
	}
	
	public void load(){
		isError = false;
		JSONRequestNameValuePair request = new JSONRequestNameValuePair(url, httMethod, loadingParams, new JSONRequestNameValuePair.Callback() {
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
	protected void setHttpMethod(HTTP_METHOD method){
		this.httMethod = method;
	}
	
	protected void setLoadingParams(List<NameValuePair> params){
		this.loadingParams = params;
	}
	protected void setParser(Parser parser){
		this.mParser = parser;
	}
	
	protected void onError(Exception e){
		mContent = null;
		isError = true;
	
		((ViewGroup)getView()).removeView(mLoadingView);
		((ViewGroup)getView()).addView(mErrorView);
		
	}
	
	private View getLoadingView(){
		return getLayoutInflater().inflate(R.layout.loading, null);
	}
	
	private View getErrorView(){
		View root = getLayoutInflater().inflate(R.layout.failed, null);
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
		((ViewGroup)getView()).removeView(mErrorView);
		((ViewGroup)getView()).addView(mLoadingView);
		load();
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
			((ViewGroup)getView()).removeView(mLoadingView);
			onDataReady();
		}
	}
	
}
