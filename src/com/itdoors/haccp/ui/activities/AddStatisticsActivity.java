
package com.itdoors.haccp.ui.activities;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import android.app.Dialog;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.itdoors.haccp.Intents;
import com.itdoors.haccp.R;
import com.itdoors.haccp.model.GroupCharacteristic;
import com.itdoors.haccp.model.PointStatus;
import com.itdoors.haccp.rest.AsyncSQLiteOperations;
import com.itdoors.haccp.ui.fragments.AddStatisticsFragment;
import com.itdoors.haccp.ui.fragments.AddStatisticsFragment.Action;
import com.itdoors.haccp.utils.LocationUtils;
import com.itdoors.haccp.utils.Logger;
import com.itdoors.haccp.utils.ToastUtil;

public class AddStatisticsActivity extends SherlockFragmentActivity implements
        AddStatisticsFragment.OnAddPressedListener,
        GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener
{

    private static final String ADD_STATICTICS_FRAGMENT_TAG = "com.itdoors.haccp.activities.AddStatisticsActivity.ADD_STATICTICS_FRAGMENT_TAG";

    private Fragment mFragment;
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

        requestWindowFeature(com.actionbarsherlock.view.Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setSupportProgressBarIndeterminateVisibility(false);
        setContentView(R.layout.activity_add_statictics);
        setTitle(R.string.add_statistic_record);

        /*
         * Create a new location client, using the enclosing class to handle
         * callbacks.
         */
        mLocationClient = new LocationClient(this, this, this);

    }

    /*
     * @Override public boolean onCreateOptionsMenu(Menu menu) {
     * menu.add(Menu.NONE, 0, Menu.NONE, getString(R.string.done))
     * .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS); return true; }
     */

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                setResult(RESULT_CANCELED);
                finish();
                return true;
                /*
                 * case 0: onDonePressed(); return true;
                 */

        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    protected void onStop() {

        // After disconnect() is called, the client is considered "dead".
        mLocationClient.disconnect();
        super.onStop();
    }

    public void onDonePressed() {

        if (mFragment != null && mFragment.isAdded()) {
            AddStatisticsFragment fragment = (AddStatisticsFragment) mFragment;
            Action action = fragment.getActionType();
            if (action != null) {
                switch (action) {
                    case CHANGE_STATUS:
                        onChangeStatusPressed(fragment.getStatus());
                        break;
                    case ADD_STATISTICS:
                        onAddPressed(fragment.getValues());
                        break;
                }
            }

        }
    }

    @Override
    public void onAddPressed(HashMap<GroupCharacteristic, Double> values) {

        String pointId = getIntent().getStringExtra(Intents.Point.UID);
        if (pointId == null)
            return;

        Iterator<Entry<GroupCharacteristic, Double>> iterator = values.entrySet().iterator();
        Entry<GroupCharacteristic, Double> entry = null;

        if (iterator.hasNext())
            entry = iterator.next();
        if (entry == null)
            return;

        GroupCharacteristic characteristic = entry.getKey();
        Double value = entry.getValue();
        String date = Long.toString(Calendar.getInstance().getTime().getTime() / 1000);
        // Get the current location
        Location currentLocation = mLocationClient.getLastLocation();

        AsyncSQLiteOperations.startInsertStatistics(getContentResolver(), pointId,
                characteristic.getId(), date, date, Integer.toString(value.intValue()),
                currentLocation);
        ToastUtil.ToastLong(getApplicationContext(),
                getString(R.string.data_will_be_entered_on_the_server));
        finish();
    }

    @Override
    public void onChangeStatusPressed(PointStatus status) {

        String pointId = getIntent().getStringExtra(Intents.Point.UID);
        if (pointId == null)
            return;
        AsyncSQLiteOperations.startUpdatePointStatus(getContentResolver(), pointId, status.getId());
        ToastUtil.ToastLong(getApplicationContext(),
                getString(R.string.data_will_be_entered_on_the_server));
        finish();
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
        mFragment = getSupportFragmentManager().findFragmentByTag(ADD_STATICTICS_FRAGMENT_TAG);
        if (mFragment == null) {
            mFragment = new AddStatisticsFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.add_statictics_frame, mFragment, ADD_STATICTICS_FRAGMENT_TAG)
                    .commit();
        }

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
