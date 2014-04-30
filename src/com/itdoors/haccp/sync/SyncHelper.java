package com.itdoors.haccp.sync;

import java.io.File;
import java.io.IOException;

import android.accounts.Account;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SyncResult;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;

import com.itdoors.haccp.Intents;
import com.itdoors.haccp.provider.HaccpContract;
import com.itdoors.haccp.provider.DatabaseUtils;
import com.itdoors.haccp.provider.HaccpDatabase;
import com.itdoors.haccp.utils.EnviromentUtils;
import com.itdoors.haccp.utils.HttpHelper;
import com.itdoors.haccp.utils.Logger;

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
			

    public static final int FLAG_SYNC_LOCAL = 0x1;
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
     * Loads conference information (sessions, rooms, tracks, speakers, etc.)
     * from a local static cache data and then syncs down data from the
     * Conference API.
     *
     * @param syncResult Optional {@link SyncResult} object to populate.
     * @throws IOException
     */
    
    public void performSync(SyncResult syncResult, int flags) throws IOException {

    	Logger.Logi(TAG, "Performing sync");
    	 
    	final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        final int localVersion = prefs.getInt("local_data_version", 0);
         
        
        if ((flags & FLAG_SYNC_LOCAL) != 0) {
        	
        	final boolean localParse = localVersion < LOCAL_VERSION_CURRENT;
        	if(localParse) {
        		
        		Logger.Logi(getClass(), "Perform initial loading ...");
        		
	        	final long startLocal = System.currentTimeMillis();
	            HaccpDatabase dbHelper = new HaccpDatabase(mContext);
	        	SQLiteDatabase db = dbHelper.getWritableDatabase();
	        	
	        	boolean sucess = false;
	        	File tempDBFile = EnviromentUtils.getDiskDir(mContext, TEMP_DB_NAME);
	        	Logger.Logi(getClass(), "Create temp file:" + tempDBFile.getAbsolutePath());
	        	
        		try {
        				
        				final long startloadingLocal = System.currentTimeMillis();
    	            	HttpHelper.downloadViaHttp(DB_URI, tempDBFile, HttpHelper.ContentType.GZIP );
		        		
	                	Logger.Logi(TAG, "Loading took " + (System.currentTimeMillis() - startloadingLocal) + "ms");
	                	
	                	final long startParsingLocal = System.currentTimeMillis();
		        		try{
		        			
		        			Logger.Logi(getClass(), "Perform parsing, file :" + tempDBFile.getAbsolutePath());
		            		
	                		db.beginTransaction();
			        		DatabaseUtils.parseZippedDatabase(tempDBFile, db);
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
	        		String path = tempDBFile.getAbsolutePath();
	        			boolean exeption = !tempDBFile.delete();
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
        	
        }
        
        Logger.Logi(getClass(), "Sync complete");
    
    }
  
    
}

