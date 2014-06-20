package com.itdoors.haccp.rest;

import java.util.ArrayList;
import java.util.Date;

import com.itdoors.haccp.model.rest.PointRecord;
import com.itdoors.haccp.model.rest.StatisticsRecord;
import com.itdoors.haccp.parser.rest.AddStatisticsResponce;
import com.itdoors.haccp.parser.rest.Responce;
import com.itdoors.haccp.parser.rest.UpdatePointStatusResponce;
import com.itdoors.haccp.provider.HaccpContract;
import com.itdoors.haccp.sync.SyncAdapter;
import com.itdoors.haccp.utils.CalendarUtils;
import com.itdoors.haccp.utils.Logger;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;

public class Processor {
	
	@SuppressWarnings("unused")
	private static final String TAG = "Processor";
	
	private final Context mContext;
	
	private Processor(Context context) {
		this.mContext = context;
	}
	
	public static Processor getInstance(Context context){
		return new Processor(context);
	}
	
	/**
	 * Read request. 
	 * Mark transaction completed.
	 * Make changes in database.
	 * Remove transaction.
	 */
	
	public void handleResponce(Responce responce) {
		
		
		final Context context = mContext;
		final ContentResolver cr = context.getApplicationContext().getContentResolver();
		
		Logger.Logi(getClass(), "handleResponce");
		
		long requestId = responce.getRequestId();
		Uri requestUri = HaccpContract.Transactions.buildUriForId(requestId);
		
		int actionType;
		RestAction action;
		
		Cursor cursor = cr.query(requestUri, TransactionQuery.PROJECTION, null, null, null);
		if(cursor.moveToFirst()){
		
			actionType = cursor.getInt(TransactionQuery.ACTION_TYPE);
			action = RestAction.valueOf(actionType);
			String transactingDate = cursor.getString(TransactionQuery.TRANSACTING_DATE);
			Date date = CalendarUtils.fromTimeStamp(transactingDate);
			Logger.Logi(getClass(), "handleResponce, transactingDate : " + date.toString());
			
		}else{
			Logger.Logi(getClass(), "row has beeb removed by another thread");
        	return;
		}
		
		
		
		ArrayList<ContentProviderOperation> batch = new ArrayList<ContentProviderOperation>();
		
		//mark transaction completed
		Logger.Logi(getClass(), "mark transaction " + requestId +" completed");
		
		Uri transactionUri = HaccpContract.Transactions.buildUriForId(requestId);
		ContentValues markCompletedValues = new ContentValues();
		markCompletedValues.put(HaccpContract.Transactions.RESULT, responce.getHttpStatusCode());
		markCompletedValues.put(HaccpContract.Transactions.TRANSACTING, TransactionState.TRANSACTION_COMPLETED);
		ContentProviderOperation markTransactionCompleted = ContentProviderOperation
				.newUpdate(transactionUri)
				.withValues(markCompletedValues)
				.build();
		batch.add(markTransactionCompleted);
		
		switch (action) {
			case INSERT_STATISTICS:
			{
			
				//insert statistics 
				Logger.Logi(getClass(), "insert statistics " + requestId);
				
				final AddStatisticsResponce  addStatisticsResponce = (AddStatisticsResponce) responce;
				final StatisticsRecord record = addStatisticsResponce.getStatisticRecord();
			    
				if(record != null){
					
					final int pointID = record.getPointId();
					
					ContentValues statisticsInsertValues = new ContentValues();
			    	statisticsInsertValues.put(HaccpContract.Statistics.UID, record.getId());
			    	statisticsInsertValues.put(HaccpContract.Statistics.CHARACTERISTICS_ID, record.getGroupCharacteristicsId());
			    	if(record.getCreatedAt() != null)
			    		statisticsInsertValues.put(HaccpContract.Statistics.CREATED_AT, Long.toString(record.getCreatedAt().getTime()/1000));
			    	if(record.getEntryDate() != null)
			    		statisticsInsertValues.put(HaccpContract.Statistics.ENTRY_DATE, Long.toString(record.getEntryDate().getTime()/1000));
			    	statisticsInsertValues.put(HaccpContract.Statistics.VALUE, record.getValue());
			    	statisticsInsertValues.put(HaccpContract.Statistics.POINT_ID, pointID);
			    
			    	Uri statisticsUri = HaccpContract.Statistics.buildUriForPoint(pointID);
			    	ContentProviderOperation insertStatistics = ContentProviderOperation
			    			.newInsert(statisticsUri)
			    			.withValues(statisticsInsertValues)
			    			.build();
			    	batch.add(insertStatistics);
			    }
			    
			}
			break;

			case UPDATE_POINT_STATUS:
			{
				//update point status
				Logger.Logi(getClass(), "update point status " + requestId);
				
				final UpdatePointStatusResponce updatePointStatusResponce = (UpdatePointStatusResponce) responce;
				final PointRecord record = updatePointStatusResponce.getPointRecord();
				if(record != null){
					
					final int pointId = record.getId();
					final int statusId = record.getStatusId();
					
					ContentValues updatePointStatusValues = new ContentValues();
					updatePointStatusValues.put(HaccpContract.Points.STATUS_ID, statusId);
					
					Uri pointUri = HaccpContract.Points.buildPointUri(pointId);
					ContentProviderOperation updateStatus = ContentProviderOperation
							.newUpdate(pointUri)
							.withValues(updatePointStatusValues)
							.build();
					batch.add(updateStatus);
					}
			}
			break;
			default:
				throw new UnsupportedOperationException("unknown action on Processor.handleResponce : " + action.toString());
		}
		
	    //remove transaction
		Logger.Logi(getClass(), "remove transaction " + requestId);
		ContentProviderOperation removeTransaction = ContentProviderOperation.newDelete(transactionUri).build();
		batch.add(removeTransaction);
		
		try {
			Logger.Logi(getClass(), "aplly batch operation");
			cr.applyBatch(HaccpContract.CONTENT_AUTHORITY, batch);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (OperationApplicationException e) {
			e.printStackTrace();
		}
	}
	
	
	
	
	
	
	
	/**
     * Check try_count from database against MAX_REQUEST_ATTEMPTS.
     * If try_count < MAX_REQUEST_ATTEMPTS then increment tryCount. 
     * If try_count >= MAX_REQUEST_ATTEMPTS then delete transaction
     * and show notification. 
     * 
     * @param requestId The _ID column from the table to update.
     * @param result The HttpStatus code.
     * @param allowRetry If true and less then MAX_REQUEST_ATTEMPTS
     * increment retry counter and try again later.  If false then
     * mark transaction as completed.
     */
    public boolean requestFailure( long requestId, int result, boolean allowRetry )  {
    	
    	Logger.Logi(getClass(), "requestFailure requestId: " + requestId + ", " + "result: " + result + ", " + "allowRetry: " + allowRetry);
    	
    	boolean tryAgain;
    	
    	final Context context = mContext;
    	final ContentResolver cr = context.getApplicationContext().getContentResolver();
    	
    	Uri failedRequestUri = HaccpContract.Transactions.buildUriForId(requestId);
    	Cursor cursor = cr.query(failedRequestUri, TransactionQuery.PROJECTION, null, null, null);
    	
    	int tryCount;
    	String httpStatus;
    	@SuppressWarnings("unused")
		HttpMethod httpMethod;
    	ContentValues values;
    	
    	if(cursor.moveToFirst()){
    		tryCount = cursor.getInt(TransactionQuery.TRY_COUNT);
        	httpStatus = cursor.getString(TransactionQuery.STATUS);
        	httpMethod = HttpMethod.fromString(httpStatus);
        } else{
        	Logger.Logi(getClass(), "row has beeb removed by another thread");
        	return false;
        }
    	Logger.Logi(getClass(), "processing request failure: tryCount=" + tryCount);
    	
    	values = new ContentValues();
    	values.put(HaccpContract.Transactions.RESULT, result);
    	if(allowRetry && tryCount < SyncAdapter.MAX_RETRY_COUNT_FOR_TRANSACTION){
    		
    		tryCount++;
    		
    		Logger.Loge(getClass(), "processing request failure. requestId :" + requestId + ", set retry tryCount=" + tryCount);
    		
    		values.put(HaccpContract.Transactions.TRANSACTING, TransactionState.TRANSACTION_RETRY);
    		values.put(HaccpContract.Transactions.TRY_COUNT, tryCount);
    		cr.update(HaccpContract.Transactions.buildUriForId(requestId), values, null, null);
    		
    		tryAgain = true;
    	}
    	else{
    		String causeMsg = (allowRetry) ? "Not retryable" : "To much retrying!";
    		Logger.Loge(getClass(), "remove failed transaction. requestId : " + requestId +"." + causeMsg);
    		cr.delete(HaccpContract.Transactions.buildUriForId(requestId), null, null);
    		tryAgain = false;
    	}
    	
    	return tryAgain;
    }
 
    @SuppressWarnings("unused")
   	private interface TransactionQuery{
      	 
   		String[] PROJECTION = new String[]{
   				HaccpContract.Transactions._ID,
   				HaccpContract.Transactions.ACTION_TYPE,
   				HaccpContract.Transactions.URI,
   				HaccpContract.Transactions.PARAMS,
   				HaccpContract.Transactions.METHOD,
   				HaccpContract.Transactions.TRANSACTING,
   				HaccpContract.Transactions.RESULT,
   				HaccpContract.Transactions.TRANSACTING_DATE,
   				HaccpContract.Transactions.TRY_COUNT
   				
   		};
   	 	
   		int _ID = 0;
   		int ACTION_TYPE = 1;
   		int URI = 2;
   		int PARAMS = 3;
   		int STATUS = 4;
   		int TRANSACTING = 5;
   		int RESULT = 6;
   		int TRANSACTING_DATE = 7;
   		int TRY_COUNT = 8;
   		
   	}
}
