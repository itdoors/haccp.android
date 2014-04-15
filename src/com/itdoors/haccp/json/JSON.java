package com.itdoors.haccp.json;

import java.io.IOException;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONException;

import com.itdoors.haccp.exceptions.ServerFailedException;
import com.itdoors.haccp.parser.Parser;
import com.itdoors.haccp.utils.NetworkHelper;
import com.itdoors.haccp.utils.ThreadUtils;

import android.os.AsyncTask;
import android.os.SystemClock;
import android.util.Log;


public class JSON {

	public static abstract class FetchAndProcessTask extends AsyncTask<Void, Void, JSONResponce>{
		
		protected Parser mParser;
		protected long mDelay;
		protected JSONRequest request;

		public FetchAndProcessTask(JSONRequest request, Parser parser){
			this.mParser = parser;
			this.request = request;
		}
		
		public void setDelay(long delay) {
			this.mDelay = delay;
		}
		
		public JSONRequest getRequest() {
			return request;
		}
		
		@Override
		protected void onPostExecute(JSONResponce responce) {
			super.onPostExecute(responce);
			JSONRequest request = (JSONRequest)responce.getRequest();
			JSONRequest.Callback callback = request.getCallback();
			if(callback != null && !request.isCanceled()){
				callback.onComplete(responce);
			}
		}
		
		@Override
		public boolean equals(Object o) {
			if(this == o) return true;
			if(! (o instanceof FetchAndProcessTask) ) return false;
			FetchAndProcessJSONStringTask task = (FetchAndProcessJSONStringTask)o;
			return task.mParser.equals(this.mParser) && task.request.equals(this.request);
		}
		
		@Override
		public int hashCode() {
			return mParser.hashCode() + request.hashCode();
		}
		
		
	}
	public static class FetchAndProcessJSONStringTask extends FetchAndProcessTask{
	
		
		public FetchAndProcessJSONStringTask(JSONRequestNameValuePair request, Parser parser){
			super(request, parser);
		}
		
		@Override
		protected JSONResponce doInBackground(Void... in) {
			long startTime = SystemClock.currentThreadTimeMillis();
			
			boolean failedDownMore = false;
		 	boolean failedParse = false;
		 	
			JSONRequestNameValuePair jsonRequest = (JSONRequestNameValuePair)request;
			String jsonString = null;
			Exception exception = null;
			
		    List<NameValuePair> loadingParams = jsonRequest.getParams();
		    String url = jsonRequest.getUrl();
		    
		    try{
		    	switch (request.getHttpMethod()) {
						case GET:	jsonString = NetworkHelper.requestGet(url);
						break;
						default:	jsonString = NetworkHelper.request(url, loadingParams);
						break;
				}
		    	
				Log.i("responce",jsonString);
			}
			catch (IOException e) {
				failedDownMore = true;
				exception  = e;
			}
			catch(ServerFailedException e){
				failedDownMore = true;
				exception = e;
			}
			
		    Object parsedObj = null;
			if(!failedDownMore){
				try{
					parsedObj = mParser.parse(jsonString);
				}
				catch (JSONException e) {
					exception = e;
					e.printStackTrace();
				} catch (ServerFailedException e) {
					exception = e;
					e.printStackTrace();
				}
			}
			
			if(!failedDownMore && !failedParse){
				long loadTime = SystemClock.currentThreadTimeMillis() - startTime;
	    		long delay  = ( mDelay <= loadTime )? 0 : mDelay - loadTime;
	    		ThreadUtils.addDelay(delay);
	    	}
			
			return new JSONResponce(jsonRequest, exception, parsedObj);
		}
		
		
		@Override
		public boolean equals(Object o) {
			if(!super.equals(o) ) return false;
			if(!(o instanceof FetchAndProcessJSONStringTask)) return false;
			return true;
		}
	}
	
	public static FetchAndProcessJSONStringTask process(JSONRequestNameValuePair request, Parser parser, long delay){
		FetchAndProcessJSONStringTask fetchAndProcessTask = new FetchAndProcessJSONStringTask(request, parser);
		fetchAndProcessTask.setDelay(delay);
		fetchAndProcessTask.execute();
		return fetchAndProcessTask;
	}
	
	
}
