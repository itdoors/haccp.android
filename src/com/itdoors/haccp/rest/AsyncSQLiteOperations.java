package com.itdoors.haccp.rest;

import com.itdoors.haccp.Global;
import com.itdoors.haccp.provider.HaccpContract;
import com.itdoors.haccp.utils.BundleUtils;
import com.itdoors.haccp.utils.Logger;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;

public class AsyncSQLiteOperations {
	
		public static void startInsertStatistics(ContentResolver cr, int pointId, int characterisrticId, String createdAt, String entryDate, String value){
		
				AsyncQueryHandler handler = new AsyncQueryHandler(cr){
					@Override
					protected void onInsertComplete(int token, Object cookie, Uri uri) {
						Logger.Logi(getClass(), "Async insert completed. Uri:" + uri.toString());
					}
				};
				
				ContentValues values = new ContentValues();
				
				int action_type = RestAction.INSERT_STATISTICS_CODE;
				String url = Global.API_URL + "/point/" + Integer.toString(pointId) + "/statistics"; 
				
				Bundle params = new Bundle();
				params.putString("pointStatisticsApiForm[characteristic]", Integer.toString(characterisrticId));
				
				params.putString("pointStatisticsApiForm[createdAt]", createdAt);
				params.putString("pointStatisticsApiForm[entryDate]", entryDate);
				params.putString("pointStatisticsApiForm[value]", value);
				
				values.put(HaccpContract.Transactions.ACTION_TYPE, action_type);
				values.put(HaccpContract.Transactions.URI, url);
				values.put(HaccpContract.Transactions.PARAMS, BundleUtils.serialize(params));
				
				handler.startInsert(0, null, HaccpContract.Transactions.PENDING_TRANSACTIONS_URI, values);
				
	}
	
	public static void startUpdatePointStatus(ContentResolver cr, int pointId, int statusId){
		
		int action_type = RestAction.UPDATE_POINT_STATUS_CODE;
		String url = Global.API_URL + "/point/" + Integer.toString(pointId) + "/status"; 
		
		Bundle params = new Bundle();
		params.putString("pointStatusApiForm[statusId]", Integer.toString(statusId));
		
		ContentValues values = new ContentValues();
		values.put(HaccpContract.Transactions.ACTION_TYPE, action_type);
		values.put(HaccpContract.Transactions.URI, url);
		values.put(HaccpContract.Transactions.PARAMS, BundleUtils.serialize(params));
		
		AsyncQueryHandler handler = new AsyncQueryHandler(cr) {
			@Override
			protected void onInsertComplete(int token, Object cookie, Uri uri) {
				Logger.Logi(getClass(), "Async insert complete. Uri: " + uri.toString());
			}
		};
		
		handler.startInsert(0, null, HaccpContract.Transactions.PENDING_TRANSACTIONS_URI, values);
	
	}
	
		
}
