package com.itdoors.haccp.rest;

import java.util.Calendar;

import com.itdoors.haccp.Global;
import com.itdoors.haccp.sync.SyncAdapter;
import com.itdoors.haccp.utils.CalendarUtils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
 
public class QueryTransactionInfo {
 
    private static String TAG = "QueryTransactionInfo";
     
    private static final QueryTransactionInfo instance = new QueryTransactionInfo();
     
    private int transacting = TransactionState.TRANSACTION_COMPLETED;
    private int tryCount = 0;
    private int result = 0;
     
    private QueryTransactionInfo() {}
     
    public static QueryTransactionInfo getInstance()
    {
        return instance;
    }
     
    /**
     * Mark the query request as completed. 
     * 
     * @param httpResult The HttpStatus code.
     */
    public synchronized void markCompleted( int httpResult )
    {
        transacting = TransactionState.TRANSACTION_COMPLETED;
        tryCount = 0;
        result = httpResult;
    }
     
    /**
     * Mark the query request as pending.
     */
    public void markPending()
    {
        if ( transacting == TransactionState.TRANSACTION_PENDING ) {
            return;
        }
         
        synchronized(this) {
            transacting = TransactionState.TRANSACTION_PENDING;
            tryCount = 0;
            result = 0;
        }
    }
     
    /**
     * If the call to the REST API fails, the query request
     * will be attempted during five sync operations before
     * it is marked as completed.  
     * 
     * @param httpResult The HttpStatus code.
     * @return True if query should be retried.
     */
    public synchronized boolean markRetry( int httpResult ) 
    {
        boolean markedRetry;
         
        tryCount++;
         
        if ( Log.isLoggable( TAG, Log.INFO ) ) {
            Log.i( TAG, "retrieve.retry.httpResult[" + httpResult + "]" );
            Log.i( TAG, "retrieve.retry.tryCount[" + tryCount + "]" );
        }
         
        if ( tryCount < SyncAdapter.MAX_RETRY_COUNT_FOR_TRANSACTION ) {
            if ( Log.isLoggable( TAG, Log.INFO) ) {
                Log.i( TAG, "retrieve.retry.RETRY" );
            }
            transacting = TransactionState.TRANSACTION_RETRY;
            markedRetry = true;
        } else {
            if ( Log.isLoggable( TAG, Log.INFO) ) {
                Log.i( TAG, "retrieve.retry.COMPLETE" );
            }
            transacting = TransactionState.TRANSACTION_COMPLETED;
            tryCount = 0;
            markedRetry = false;
        }
        result = httpResult;
         
        return markedRetry;
    }
     
    /**
     * Mark the query request as in-progress.
     */
    public synchronized void markInProgress()
    {
        transacting = TransactionState.TRANSACTION_IN_PROGRESS;
    }
     
    /**
     * A refresh is outstanding if:
     * - transacting = TRANSACTION_PENDING
     * - transacting = TRANSACTION_RETRY
     * - autoSync is true and data has not been refreshed in the last hour
     * 
     * @param autoSync Automatically request a sync 
     * from the REST API if data has not been refreshed 
     * in the last hour. 
     * @return True is a sync operation should be requested.
     */
    public synchronized boolean isRefreshOutstanding( boolean autoSync, Context context )
    {
        boolean refresh;
             
        if ( transacting == TransactionState.TRANSACTION_PENDING ||
             transacting == TransactionState.TRANSACTION_RETRY ) {
            refresh = true;
        } else if ( transacting == TransactionState.TRANSACTION_IN_PROGRESS ) {
            refresh = false;
        } else {
 
            if ( !autoSync ) {
                refresh = false;
            } else {
                // If transaction is in COMPLETED state
                // and data has not been refresh for
                // one hour a refresh will be requested
                // automatically.
                 
                SharedPreferences prefs;
                long dlMillis;
                long cutoffMillis;
     
                prefs = context.getSharedPreferences( 
                		Global.REST_PREFS, 0 );
                 
                dlMillis = prefs.getLong( Global.PREFERENCES_DOWNLOAD_DATE, 0 );
                cutoffMillis = CalendarUtils.addToCurrent( Calendar.HOUR_OF_DAY, -1 ).getTime();
                 
                if ( dlMillis <= cutoffMillis ) {
                    refresh = true;
                    transacting = TransactionState.TRANSACTION_PENDING;
                } else {
                    refresh = false;
                }
            }
        }
 
        return refresh;
    }
     
    @Override
    public String toString() {
        return "QueryTransactionInfo [transacting="
                + transacting + ", tryCount=" + tryCount
                + ", result=" + result + "]";
    }
     
}
