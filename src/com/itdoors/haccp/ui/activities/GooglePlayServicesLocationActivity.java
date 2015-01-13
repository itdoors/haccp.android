
package com.itdoors.haccp.ui.activities;

import android.app.Dialog;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.itdoors.haccp.utils.LocationUtils;
import com.itdoors.haccp.utils.Logger;

public class GooglePlayServicesLocationActivity extends SherlockFragmentActivity implements

        GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener {

    // Stores the current instantiation of the location client in this object
    private LocationClient mLocationClient;

    @Override
    public void onStart() {

        super.onStart();
        /*
         * Connect the client. Don't re-start any requests here; instead, wait
         * for onResume()
         */
        mLocationClient.connect();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*
         * Create a new location client, using the enclosing class to handle
         * callbacks.
         */
        mLocationClient = new LocationClient(this, this, this);
    }

    @Override
    protected void onStop() {

        // After disconnect() is called, the client is considered "dead".
        mLocationClient.disconnect();
        super.onStop();
    }

    protected LocationClient getLocationClient() {
        return mLocationClient;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                setResult(RESULT_CANCELED);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);

    }

    // ----------------------------------------------------------------------------------------------------
    // Location Callbacks
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        /*
         * Google Play services can resolve some errors it detects. If the error
         * has a resolution, try sending an Intent to start a Google Play
         * services activity that can resolve error.
         */
        if (connectionResult.hasResolution()) {
            try {

                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(
                        this,
                        LocationUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST);

                /*
                 * Thrown if Google Play services canceled the original
                 * PendingIntent
                 */

            } catch (IntentSender.SendIntentException e) {

                // Log the error
                e.printStackTrace();
            }
        } else {

            // If no resolution is available, display a dialog to the user with
            showErrorDialog(connectionResult.getErrorCode());
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {

        Logger.Logd(getClass(), "Connected to Google Location Client...");

    }

    @Override
    public void onDisconnected() {
        Logger.Logd(getClass(), "Disconnected to Google Location Client...");
    }

    /**
     * Show a dialog returned by Google Play services for the connection error
     * code
     * 
     * @param errorCode An error code returned from onConnectionFailed
     */
    private void showErrorDialog(int errorCode) {

        // Get the error dialog from Google Play services
        Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(
                errorCode,
                this,
                LocationUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST);

        // If Google Play services can provide an error dialog
        if (errorDialog != null) {

            // Create a new DialogFragment in which to show the error dialog
            ErrorDialogFragment errorFragment = new ErrorDialogFragment();

            // Set the dialog in the DialogFragment
            errorFragment.setDialog(errorDialog);

            // Show the error dialog in the DialogFragment
            errorFragment.show(getSupportFragmentManager(), LocationUtils.APPTAG);
        }
    }

    /**
     * Define a DialogFragment to display the error dialog generated in
     * showErrorDialog.
     */
    public static class ErrorDialogFragment extends DialogFragment {

        // Global field to contain the error dialog
        private Dialog mDialog;

        /**
         * Default constructor. Sets the dialog field to null
         */
        public ErrorDialogFragment() {
            super();
            mDialog = null;
        }

        /**
         * Set the dialog to display
         * 
         * @param dialog An error dialog
         */
        public void setDialog(Dialog dialog) {
            mDialog = dialog;
        }

        /*
         * This method must return a Dialog to the DialogFragment.
         */
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return mDialog;
        }
    }
}
