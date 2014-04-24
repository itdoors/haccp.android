package com.itdoors.haccp.activities;

import com.itdoors.haccp.Intents;
import com.itdoors.haccp.Intents.SyncComplete;
import com.itdoors.haccp.R;
import com.itdoors.haccp.fragments.InitFragment;
import com.itdoors.haccp.provider.PointContract;
import com.itdoors.haccp.sync.SyncUtils;
import com.itdoors.haccp.sync.accounts.GenericAccountService;
import com.itdoors.haccp.utils.LoadActivityUtils;
import com.itdoors.haccp.utils.ToastUtil;

import android.accounts.Account;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SyncStatusObserver;
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
    
   
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    	LoadActivityUtils.addLoadingView(this, R.string.synchronization);
    	SyncUtils.CreateSyncAccount(this);
    	
    }
	
	@Override
	public void onResume() {
		super.onResume();
		
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
	

	private BroadcastReceiver syncFinishedReceiver = new BroadcastReceiver() {

	    @Override
	    public void onReceive(Context context, Intent intent) {
	    	
	    	boolean localInsertSuccesfull = intent.getExtras().getBoolean(Intents.SyncComplete.LOCAL_SYNC_COMPELTED_SUCCESFULLY);
	    	if(localInsertSuccesfull){
	    		Intent newIntent = new Intent(InitActivity.this, MainActivity.class);
	    		startActivity(newIntent);
	    		finish();
	    	}
	    	else{
	    		ToastUtil.ToastLong(InitActivity.this, "Harr error has happened. Retry later.");
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
                            account, PointContract.CONTENT_AUTHORITY);
                    boolean syncPending = ContentResolver.isSyncPending(
                            account, PointContract.CONTENT_AUTHORITY);
                 
                    
                }
            });
        }
    };
}
