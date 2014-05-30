package com.itdoors.haccp.sync;

import java.io.File;
import java.io.IOException;

import android.accounts.Account;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SyncResult;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;

import com.itdoors.haccp.Intents;
import com.itdoors.haccp.exceptions.rest.AuthenticationFailureException;
import com.itdoors.haccp.exceptions.rest.DeviceConnectionException;
import com.itdoors.haccp.exceptions.rest.NetworkSystemException;
import com.itdoors.haccp.exceptions.rest.WebServiceConnectionException;
import com.itdoors.haccp.exceptions.rest.WebServiceFailedException;
import com.itdoors.haccp.provider.HaccpContract;
import com.itdoors.haccp.provider.HaccpDatabase;
import com.itdoors.haccp.rest.HttpMethod;
import com.itdoors.haccp.rest.InsertStatisticsCommand;
import com.itdoors.haccp.rest.BaseRESTCommand;
import com.itdoors.haccp.rest.QueryTransactionInfo;
import com.itdoors.haccp.rest.RESTCommand;
import com.itdoors.haccp.rest.RESTMethod;
import com.itdoors.haccp.rest.RestAction;
import com.itdoors.haccp.rest.UpdatePointStatusCommand;
import com.itdoors.haccp.utils.BundleUtils;
import com.itdoors.haccp.utils.Enviroment;
import com.itdoors.haccp.utils.HttpHelper;
import com.itdoors.haccp.utils.Logger;
import com.itdoors.haccp.utils.StreamParser;

/**
 * A helper class for dealing with sync and other remote persistence operations.
 * All operations occur on the thread they're called from, so it's best to wrap
 * calls in an {@link android.os.AsyncTask}, or better yet, a
 * {@link android.app.Service}.
 */
public class SyncHelper {
	
    private static final String TAG = SyncHelper.class.getSimpleName();
    private static final String TEMP_DB_NAME = "dump.gzip";
    private static final String DB_URI = "http://haccp.itdoors.com.ua/api/v1/backup/";
			

    public static final int FLAG_SYNC_INITIAL = 0x1;
    public static final int FLAG_SYNC_REMOTE = 0x2;
    
    public static final int LOCAL_VERSION_CURRENT = 1;
    
    private Context mContext;

    public SyncHelper(Context context) {
        mContext = context;
    }

    public static void requestManualSync(Account mChosenAccount) {
        Bundle b = new Bundle();
        b.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(
                mChosenAccount,
                HaccpContract.CONTENT_AUTHORITY, b);
    }

    /**
     * 1) In initial setup loading zipped json db representation, unzipping it and parsing to sqlite.
     * 2) All actions, that need to be queued and performed in sync, saves in "transactions" table, from where they are taken and handled by this adapter
     * 3) Server to device sync  happens when it need and also performed here.
     * @param syncResult Optional {@link SyncResult} object to populate.
     * @throws IOException
     */
    
    public void performSync(SyncResult syncResult, int flags) throws IOException {

    	Logger.Logi(TAG, "Performing sync");
    	 
    	final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        final int localVersion = prefs.getInt("local_data_version", 0);
         
        
        if ((flags & FLAG_SYNC_INITIAL) != 0) {
        	
        	final boolean localParse = localVersion < LOCAL_VERSION_CURRENT;
        	if(localParse) {
        		
        		Logger.Logi(getClass(), "Perform initial loading ...");
        		
	        	final long startLocal = System.currentTimeMillis();
	            HaccpDatabase dbHelper = new HaccpDatabase(mContext);
	        	SQLiteDatabase db = dbHelper.getWritableDatabase();
	        	
	        	boolean sucess = false;
	        	File tempFile = Enviroment.getDiskDir(mContext, TEMP_DB_NAME);
	        	Logger.Logi(getClass(), "Create temp file:" + tempFile.getAbsolutePath());
	        	
        		try {
        				
        				final long startloadingLocal = System.currentTimeMillis();
    	            	HttpHelper.downloadViaHttp(DB_URI, tempFile, HttpHelper.ContentType.GZIP );
		        		
	                	Logger.Logi(TAG, "Loading took " + (System.currentTimeMillis() - startloadingLocal) + "ms");
	                	
	                	final long startParsingLocal = System.currentTimeMillis();
		        		try{
		        			
		        			Logger.Logi(getClass(), "Perform parsing, file :" + tempFile.getAbsolutePath());
		            		
	                		db.beginTransaction();
			        		StreamParser.parseZippedDatabase(tempFile, db);
			        		db.setTransactionSuccessful();
			        		
			        		if (syncResult != null) {
			                    ++syncResult.stats.numUpdates; // TODO: better way of indicating progress?
			                    ++syncResult.stats.numEntries;
			                }
			        		sucess = true;
			        	}
			        	catch(SQLiteException e){
			        		
			        		Logger.Loge(getClass().getSimpleName(), "Eroor write to db");
			        		e.printStackTrace();
			        		
			        		// hard error
			        		syncResult.databaseError = true;
			        		
			        		Intent intent = new Intent(Intents.SyncComplete.ACTION_FINISHED_SYNC);
			   		 		intent.putExtra(Intents.SyncComplete.LOCAL_SYNC_COMPELTED_SUCCESFULLY, false);
			   		 		LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
			   		 		
			        	} 
			        	finally{
			        		db.endTransaction();
			        		if(db.isOpen()){
			        			db.close();
			        		}
			        	}
		        		
			        	Logger.Logi(TAG, "Parsing file took " + (System.currentTimeMillis() - startParsingLocal) + "ms");
	        	}
	        	finally {
	        			String path = tempFile.getAbsolutePath();
	        			boolean exeption = !tempFile.delete();
	        			Logger.Logi(getClass(), "Removing temp file :" + path + ", with exeption  - "  + (exeption ? "yes" : "no" ));
	        	}
	        	
        		
	            Logger.Logi(TAG, "Local sync took " + (System.currentTimeMillis() - startLocal) + "ms");
	            if(sucess){
	            	
	   		    	Intent intent = new Intent(Intents.SyncComplete.ACTION_FINISHED_SYNC);
	   		 		intent.putExtra(Intents.SyncComplete.LOCAL_SYNC_COMPELTED_SUCCESFULLY, true);
	   		 		LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
	   		 		
	   		 		prefs.edit().putBoolean(SyncUtils.PREF_SETUP_COMPLETE, true).commit();
	   		        prefs.edit().putInt("local_data_version", LOCAL_VERSION_CURRENT).commit();
	            }
	        }
        }
        
        if((flags & FLAG_SYNC_REMOTE) != 0){
        	
        	// device to server
        	{
        		
        		BaseRESTCommand restCommand;
        		
        		ContentResolver cr = mContext.getContentResolver();
        		Logger.Logi(getClass(), "Performing transactions sync...");
        		
        		Cursor cursor = cr.query(HaccpContract.Transactions.PENDING_TRANSACTIONS_URI, PendingTransactionsQuery.PROJECTION, null, null, null);
        		
        		cursor.moveToFirst();
        		while(!cursor.isAfterLast()){
        			
        			long requestId = cursor.getLong(PendingTransactionsQuery._ID);
        			int actionType = cursor.getInt(PendingTransactionsQuery.ACTION_TYPE);
        			String methodType = cursor.getString(PendingTransactionsQuery.METHOD);
        			
        			String uri = cursor.getString(PendingTransactionsQuery.URI);
					Bundle params = BundleUtils.deserialize(cursor.getString(PendingTransactionsQuery.PARAMS));
		
        			RestAction action;
        			HttpMethod method;
        			
        			try{
        				action = RestAction.valueOf(actionType);
        			}
        			catch(IllegalArgumentException e){
        				 
        				 Logger.Loge( TAG, "Cannot create action: [" + actionType + "]", e );
        	             
        				 syncResult.databaseError = true;
        	             clearTransacting(requestId);
        	             continue;
        			}
        			
        			try{
            			method = HttpMethod.fromString(methodType);
        			}
        			catch(IllegalArgumentException e){
        				 
        				 Logger.Loge( TAG, "Cannot create http method: [" + methodType + "]", e );
        	             
        				 syncResult.databaseError = true;
        	             clearTransacting(requestId);
        	             continue;
        			}
        			
        			// If the row still exists in transactions table and is still pending, it will be
                    // updated to transacting in-progress.
        			
        			Uri updaterequestUri = HaccpContract.Transactions.buildInProgressUriForId(requestId);
        			Logger.Logi(getClass(), "update request uri: " + updaterequestUri.toString());
        			
        			int count = cr.update(updaterequestUri, new ContentValues(), null, null);
        			if(count > 0){
        				
        				Logger.Logi(getClass(), "update in-progress successfull, count : " + count);
        				if(action == RestAction.INSERT_STATISTICS){
            				if(method == HttpMethod.POST){
            					restCommand = new InsertStatisticsCommand(mContext, requestId, uri, params);
            					Logger.Logi(getClass(), "create new insert statistics command: " + restCommand.toString());
            				}
            				else{
            					Logger.Loge(getClass(), "invalid request method for insert statistics action : " + method.toString() );
            					syncResult.databaseError = true;
            					clearTransacting(requestId);
            					continue;
            				}
            			}
            			else if( action == RestAction.UPDATE_POINT_STATUS){
            				if(method == HttpMethod.POST){
            					restCommand = new UpdatePointStatusCommand(mContext, requestId, uri, params);
            					Logger.Logi(getClass(), "create new update point status action : " + method.toString());
            				}
            				else{
            					Logger.Loge(getClass(), "invalid request method for update point status action : " + method.toString() );
            					syncResult.databaseError = true;
            					clearTransacting(requestId);
            					continue;
            				}
            			}
            			else{
            				Logger.Loge(getClass(), "invalid request action : " + action.toString() );
        					syncResult.databaseError = true;
        					clearTransacting(requestId);
        					continue;
        				}
        				if(!handleSync(restCommand, syncResult))
        					break;
        			}
        			
        			cursor.moveToNext();
        		}
        		
        		cursor.close();
        	}
        	
        	//server to device
        	{
        		QueryTransactionInfo queryTransInfo = QueryTransactionInfo.getInstance();
        		if( queryTransInfo.isRefreshOutstanding(true, mContext)){
        			/*
        			queryTransInfo.markInProgress();
        			restCommand = new UpdateDatabaseCommand();
        			handleSync(restCommand, syncResult);
        			*/
        		}
        	}	
        	Logger.Logi(getClass(),"Finish transaction sync.");
        }
        
        Logger.Logi(getClass(), "Sync complete");
    
    }
    
    /**
     * Sync the local database with the web service by executing the RESTCommand which
     * will call the REST API.  Hard errors will cancel the sync operation.
     * Soft errors will result in exponential back-off.
     * 
     * @param restCommand The command object to be executed.
     * @param syncResult SyncAdapter-specific parameters. Here we use it to set 
     * soft and hard errors. 
     * @return True if the call to the REST API was successful.
     *         True if the call fails and request should be retried.
     *         False if the call fails and request should NOT be retried.
     */
    private boolean handleSync( RESTCommand restCommand, SyncResult syncResult )
    {
        try {
            RESTMethod.getInstance().handleRequest( restCommand );
        } catch ( WebServiceConnectionException e ) {
            
            Logger.Logi(TAG, "Web service not available.");
             
            if ( e.isRetry() ) {
                // soft error
                syncResult.stats.numIoExceptions = 1;
                return true; 
            } else {
                // hard error
                syncResult.databaseError = true;
                return false;
            }
             
        } catch ( WebServiceFailedException e ) {
            Logger.Loge(TAG, "Error returned from web service.");
            return true;
        } catch ( DeviceConnectionException e ) { 
            Logger.Loge(TAG, "Device cannot connect to the network.");
            if ( e.isRetry() ) {
                // soft error
                syncResult.stats.numIoExceptions = 1;
                return true;
            } else {
                // hard error
                syncResult.databaseError = true;
                return false;
            }
        } catch ( NetworkSystemException e ) { 
        	Logger.Loge( TAG, "Error configuring http request.", e );
            // hard error
            syncResult.databaseError = true;
            return false;
        } catch ( AuthenticationFailureException e ) {
            Logger.Loge( TAG, "Authentication failure.", e );
            // hard error
            syncResult.stats.numAuthExceptions = 1;
            return false;
        }
        return true;
    }
    
    
    /**
     * Used to clear the transacting flags if the request fails before the 
     * call to the REST API.  We will hopefully never get here, but reason
     * for this most likely bad data in input.
     * 
     * @param requestId A value for the _ID column which will be used as the 
     * update key for the request that failed.
     */
    private void clearTransacting( long requestId )
    {
    	Logger.Loge(getClass(), "clear transaction: " + requestId);
        final ContentResolver cr = 
                mContext.getApplicationContext().getContentResolver();
        cr.delete(HaccpContract.Transactions.buildUriForId(requestId), null, null);
    }
  
    
    
    @SuppressWarnings("unused")
	private interface PendingTransactionsQuery{
   	 
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
		int METHOD = 4;
		int TRANSACTING = 5;
		int RESULT = 6;
		int TRANSACTING_DATE = 7;
		int TRY_COUNT = 8;
		
	}
    
}

