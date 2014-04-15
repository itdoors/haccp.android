package com.itdoors.haccp.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import com.itdoors.haccp.exceptions.ServerFailedException;


public class NetworkHelper {
	
	
	public static HttpPost getHttPost(String urlString, List<NameValuePair> params){
		
		int timeoutConnection = 10000;
        int timeoutSocket = 10000;
     	HttpParams httpParameters = new BasicHttpParams();
    	HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
    	HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
     
       	HttpPost httpPost = new HttpPost(urlString);

	   	Logger.Logi(NetworkHelper.class, "url:" + urlString +";\n post:"+params.toString());
		
		try {
		
			httpPost.setEntity(new UrlEncodedFormEntity(params));
		
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return httpPost;
	}
	
	
	public static String executeHttpPost(HttpPost httpPost) throws IOException, ServerFailedException{
		
		DefaultHttpClient httpClient = new DefaultHttpClient(httpPost.getParams());
		HttpResponse httpResponse = httpClient.execute(httpPost);
		StatusLine statusLine = httpResponse.getStatusLine();
	    
		int statusCode = statusLine.getStatusCode();
		Logger.Logi(NetworkHelper.class,"status:"+ statusCode);
		
		StringBuilder builder = new StringBuilder();
		if (statusCode == HttpStatus.SC_OK) {
	    	 HttpEntity entity = httpResponse.getEntity();
	         InputStream content = entity.getContent();
	         BufferedReader reader = new BufferedReader(new InputStreamReader(content));
	         String line;
	         while ((line = reader.readLine()) != null) {
	        	 builder.append(line);
	         }
	    }
	    
		else {
	    	Logger.Loge(NetworkHelper.class,"Failed to download from " + httpPost.getURI() + " params: "+httpPost.getParams().toString());
	    	throw new ServerFailedException("Server side exception. Failed to download.");
	    }
    	
	    return builder.toString();
	}
	
	public static String request(String urlString, List<NameValuePair> params) throws IOException, ServerFailedException{
	    	
	    	int timeoutConnection = 10000;
	        int timeoutSocket = 10000;
	        StringBuilder builder = new StringBuilder();
	        
	    	HttpParams httpParameters = new BasicHttpParams();
	    	HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
        	HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
         
  
         	DefaultHttpClient httpClient = new DefaultHttpClient(httpParameters);
		   	HttpPost httpPost = new HttpPost(urlString);

		   	Logger.Logi(NetworkHelper.class, "url:" + urlString +";\n post:"+params.toString());
			
			httpPost.setEntity(new UrlEncodedFormEntity(params));
			HttpResponse httpResponse = httpClient.execute(httpPost);
			
			StatusLine statusLine = httpResponse.getStatusLine();
		    
			int statusCode = statusLine.getStatusCode();
			Logger.Logi(NetworkHelper.class,"status:"+ statusCode);
					
			if (statusCode == HttpStatus.SC_OK) {
		    	 HttpEntity entity = httpResponse.getEntity();
		         InputStream content = entity.getContent();
		         BufferedReader reader = new BufferedReader(new InputStreamReader(content));
		         String line;
		         while ((line = reader.readLine()) != null) {
		        	 builder.append(line);
		         }
		    }
		    
		    else {
		    	
		    	Logger.Loge(NetworkHelper.class,"Failed to download from " + urlString + " params: "+params.toString());
		    	throw new ServerFailedException("Server side exception. Failed to download.");
		    }
	    	
		    return builder.toString();
	    }
	
	public static String requestGet(String urlString) throws IOException, ServerFailedException{
    	
    	int timeoutConnection = 10000;
        int timeoutSocket = 10000;
        StringBuilder builder = new StringBuilder();
        
    	HttpParams httpParameters = new BasicHttpParams();
    	HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
    	HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
     

     	DefaultHttpClient httpClient = new DefaultHttpClient(httpParameters);

	   	HttpGet httpGet = new HttpGet(urlString);
	   	Logger.Logi(NetworkHelper.class, "url:" + urlString +";");
		
	   	HttpResponse httpResponse = httpClient.execute(httpGet);
	   	
	   	StatusLine statusLine = httpResponse.getStatusLine();
	    int statusCode = statusLine.getStatusCode();
		Logger.Logi(NetworkHelper.class,"status:"+ statusCode);
				
		if (statusCode == HttpStatus.SC_OK) {
	    	 HttpEntity entity = httpResponse.getEntity();
	         InputStream content = entity.getContent();
	         BufferedReader reader = new BufferedReader(new InputStreamReader(content));
	         String line;
	         while ((line = reader.readLine()) != null) {
	        	 builder.append(line);
	         }
	    }
	    
	    else {
	    	
	    	Logger.Loge(NetworkHelper.class,"Failed to download from " + urlString + ";");
	    	throw new ServerFailedException("Server side exception. Failed to download.");
	    }
    	
	    return builder.toString();
    }

	
	
	

	
	
}
