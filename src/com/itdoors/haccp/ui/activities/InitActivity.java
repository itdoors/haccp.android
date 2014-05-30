package com.itdoors.haccp.ui.activities;

import com.itdoors.haccp.Intents;
import com.itdoors.haccp.Intents.SyncComplete;
import com.itdoors.haccp.R;
import com.itdoors.haccp.provider.HaccpContract;
import com.itdoors.haccp.sync.SyncUtils;
import com.itdoors.haccp.sync.accounts.GenericAccountService;
import com.itdoors.haccp.ui.fragments.InitFragment;
import com.itdoors.haccp.utils.LoadActivityUtils;
import com.itdoors.haccp.utils.Logger;
import com.itdoors.haccp.utils.ToastUtil;

import android.accounts.Account;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SyncStatusObserver;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;

public class InitActivity extends BaseSherlockFragmentActivity {
	
	
	
	
    /**
     * Handle to a SyncObserver. The ProgressBar element is visible until the SyncObserver reports
     * that the sync is complete.
     *
     * <p>This allows us to delete our SyncObserver once the application is no longer in the
     * foreground.
     */
    private Object mSyncObserverHandle;


	private Intent mFinishIntent;
    
	private static final String POST_AUTH_CATEGORY
    	= "com.itdoors.haccp.POST_AUTH";
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    	LoadActivityUtils.addLoadingView(this, R.string.synchronization);
    	SyncUtils.CreateSyncAccount(this);
    	
    	final Intent intent = getIntent();
        if (intent.hasExtra(SyncUtils.FINISH_INTENT_EXTRA)) {
            mFinishIntent = intent.getParcelableExtra(SyncUtils.FINISH_INTENT_EXTRA);
        }
        
        ConnectivityManager cm = (ConnectivityManager)
                getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork == null || !activeNetwork.isConnected()) {
           ToastUtil.ToastLong(getApplicationContext(), getResources().getString(R.string.no_connection_cant_sync));
        }

    }
	
	@Override
	public void onResume() {
		super.onResume();
		
		if(SyncUtils.syncCompleted(this))
	       	finishInitialSetup();
		
	    mSyncStatusObserver.onStatusChanged(0);
        // Watch for sync state changes
        final int mask = ContentResolver.SYNC_OBSERVER_TYPE_PENDING |
               ContentResolver.SYNC_OBSERVER_TYPE_ACTIVE;
        
        mSyncObserverHandle = ContentResolver.addStatusChangeListener(mask, mSyncStatusObserver);
        LocalBroadcastManager.getInstance(this).registerReceiver(syncFinishedReceiver, new IntentFilter(SyncComplete.ACTION_FINISHED_SYNC));
        
       
	}
	
	@Override
	public void onPause() {
		super.onPause();
		if (mSyncObserverHandle != null) {
	       ContentResolver.removeStatusChangeListener(mSyncObserverHandle);
	       mSyncObserverHandle = null;
	    }
		
		LocalBroadcastManager.getInstance(this).unregisterReceiver(syncFinishedReceiver);
		
		
	}
	
	@Override
	public void initFragment() {
		// TODO Auto-generated method stub
		Fragment fragment = new InitFragment();
		setContentFragment(fragment);
	}
	
	private void finishInitialSetup(){
		Logger.Logi(getClass(), "finishInitialSetup");
		
		if(mFinishIntent != null){
			mFinishIntent.addCategory(POST_AUTH_CATEGORY);
			startActivity(mFinishIntent);
		}
		
		finish();
		
	}

	private BroadcastReceiver syncFinishedReceiver = new BroadcastReceiver() {

	    @Override
	    public void onReceive(Context context, Intent intent) {
	    	
	    	boolean localInsertSuccesfull = intent.getExtras().getBoolean(Intents.SyncComplete.LOCAL_SYNC_COMPELTED_SUCCESFULLY);
	    	if(localInsertSuccesfull){
	    		
	    		finishInitialSetup();
	    	
	    	}
	    	else{
	    		Logger.Loge(getClass(), "Hard error has happened when performing initial sync.");
	    		ToastUtil.ToastLong(InitActivity.this.getApplicationContext(), "Hard error has happened. Retry later.");
	    	}
	    	
	    }
	};
	
	
	/**
     * Crfate a new anonymous SyncStatusObserver. It's attached to the app's ContentResolver in
     * onResume(), and removed in onPause(). If status changes, it sets the state of the Refresh
     * button. If a sync is active or pending, the Refresh button is replaced by an indeterminate
     * ProgressBar; otherwise, the button itself is displayed.
     */
    private SyncStatusObserver mSyncStatusObserver = new SyncStatusObserver() {
        /** Callback invoked with the sync adapter status changes. */
        @Override
        public void onStatusChanged(int which) {
            runOnUiThread(new Runnable() {
                /**
                 * The SyncAdapter runs on a background thread. To update the UI, onStatusChanged()
                 * runs on the UI thread.
                 */
                @SuppressWarnings("unused")
				@Override
                public void run() {
                    // Create a handle to the account that was created by
                    // SyncService.CreateSyncAccount(). This will be used to query the system to
                    // see how the sync status has changed.
                    Account account = GenericAccountService.GetAccount();
                    if (account == null) {
                        // GetAccount() returned an invalid value. This shouldn't happen, but
                        // we'll set the status to "not refreshing".
                       return;
                    }

                    // Test the ContentResolver to see if the sync adapter is active or pending.
                    // Set the state of the refresh button accordingly.
                    boolean syncActive = ContentResolver.isSyncActive(
                            account, HaccpContract.CONTENT_AUTHORITY);
                    boolean syncPending = ContentResolver.isSyncPending(
                            account, HaccpContract.CONTENT_AUTHORITY);
                 
                    
                }
            });
        }
    };
}
