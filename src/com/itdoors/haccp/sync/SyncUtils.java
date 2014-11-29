/*
 * Copyright 2013 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.itdoors.haccp.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.itdoors.haccp.oauth.AccessToken;
import com.itdoors.haccp.provider.HaccpContract;
import com.itdoors.haccp.sync.accounts.GenericAccountService;
import com.itdoors.haccp.ui.activities.HomeActivity;
import com.itdoors.haccp.ui.activities.InitActivity;
import com.itdoors.haccp.ui.activities.LoginActivity;
import com.itdoors.haccp.utils.Logger;

/**
 * Static helper methods for working with the sync framework.
 */
public class SyncUtils {
    private static final long SYNC_FREQUENCY = 60 * 60; // 1 hour (in seconds)
    private static final String CONTENT_AUTHORITY = HaccpContract.CONTENT_AUTHORITY;

    public static String FINISH_INTENT_EXTRA = "com.itdoors.sync.SyncUtils.FINISH_INTENT_EXTRA";

    public static final String PREF_SETUP_COMPLETE = "setup_complete";
    public static final String PREF_SETUP_DONE = "setup_done";

    public static final String PREF_TOKEN = "oauth_token";

    /**
     * Create an entry for this application in the system account list, if it
     * isn't already there.
     * 
     * @param context Context
     */

    public static synchronized void CreateSyncAccount(Context context) {
        @SuppressWarnings("unused")
        boolean newAccount = false;
        @SuppressWarnings("unused")
        boolean setupComplete = PreferenceManager
                .getDefaultSharedPreferences(context).getBoolean(PREF_SETUP_COMPLETE, false);
        boolean setupDone = PreferenceManager.getDefaultSharedPreferences(context).getBoolean(
                PREF_SETUP_DONE, false);

        // Create account, if it's missing. (Either first run, or user has
        // deleted account.)
        Account account = GenericAccountService.GetAccount();
        AccountManager accountManager = (AccountManager) context
                .getSystemService(Context.ACCOUNT_SERVICE);
        if (accountManager.addAccountExplicitly(account, null, null)) {
            // Inform the system that this account supports sync
            ContentResolver.setIsSyncable(account, CONTENT_AUTHORITY, 1);
            // Inform the system that this account is eligible for auto sync
            // when the network is up
            ContentResolver.setSyncAutomatically(account, CONTENT_AUTHORITY, true);
            // Recommend a schedule for automatic synchronization. The system
            // may modify this based
            // on other scheduled syncs and network utilization.
            ContentResolver.addPeriodicSync(
                    account, CONTENT_AUTHORITY, new Bundle(), SYNC_FREQUENCY);
            newAccount = true;
        }

        // Schedule an initial sync if we detect problems with either our
        // account or our local
        // data has been deleted. (Note that it's possible to clear app data
        // WITHOUT affecting
        // the account list, so wee need to check both.)
        if (!setupDone) {
            requestManualSync(context);
        }
    }

    public static synchronized boolean isLoggedIn(Context context, Intent intentToRedirect) {
        String token = PreferenceManager.getDefaultSharedPreferences(context)
                .getString(PREF_TOKEN, "");

        boolean loggedIn = !token.equals("");
        if (loggedIn) {

            return true;

        }
        else {

            Intent intent = new Intent(context, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(FINISH_INTENT_EXTRA, intentToRedirect);
            context.startActivity(intent);
            return false;

        }

    }

    public static synchronized void logIn(Context context, AccessToken assessToken,
            Intent redirectIntent) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putString(PREF_TOKEN, assessToken.getToken()).commit();

        Intent intent = new Intent(context, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(FINISH_INTENT_EXTRA, redirectIntent);
        context.startActivity(intent);
    }

    public static synchronized void logOut(Context context, Intent finishIntent) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().clear().commit();

        Intent intent = new Intent(context, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(FINISH_INTENT_EXTRA, finishIntent);
        context.startActivity(intent);

    }

    public static synchronized String getAccessToken(Context context) {
        String token = PreferenceManager.getDefaultSharedPreferences(context)
                .getString(PREF_TOKEN, null);
        Log.d(SyncUtils.class.getSimpleName(), token);
        return token;
    }

    public static synchronized boolean cheakSync(Context context, Intent finishIntent) {

        if (syncCompleted(context)) {
            return true;
        }
        else {

            Intent intent = new Intent(context, InitActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(FINISH_INTENT_EXTRA, finishIntent);
            context.startActivity(intent);
            return false;

        }

    }

    public static synchronized boolean syncCompleted(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).
                getBoolean(SyncUtils.PREF_SETUP_COMPLETE, false);

    }

    public static synchronized boolean wasSynced(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).
                getBoolean(SyncUtils.PREF_SETUP_DONE, false);

    }

    /**
     * Helper method to trigger an immediate sync ("refresh").
     * <p>
     * This should only be used when we need to preempt the normal sync
     * schedule. Typically, this means the user has pressed the "refresh"
     * button. Note that SYNC_EXTRAS_MANUAL will cause an immediate sync,
     * without any optimization to preserve battery life. If you know new data
     * is available (perhaps via a GCM notification), but the user is not
     * actively waiting for that data, you should omit this flag; this will give
     * the OS additional freedom in scheduling your sync request.
     */

    public static synchronized void requestManualSync(Context context) {
        Logger.Logi(SyncUtils.class, "request Manual Sync");
        Bundle b = new Bundle();
        // Disable sync backoff and ignore sync preferences. In other
        // words...perform sync NOW!
        b.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        b.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        ContentResolver.requestSync(
                GenericAccountService.GetAccount(), // Sync account
                HaccpContract.CONTENT_AUTHORITY, // Content authority
                b); // Extras
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putBoolean(PREF_SETUP_DONE, true).commit();

    }
}
