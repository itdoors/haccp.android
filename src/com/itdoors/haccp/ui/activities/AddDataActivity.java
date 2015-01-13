
package com.itdoors.haccp.ui.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import de.greenrobot.event.EventBus;

public abstract class AddDataActivity extends GooglePlayServicesLocationActivity {

    private static final String ADD_DATA_FRAGMENT_TAG = "com.itdoors.haccp.activities.AddStatisticsActivity.ADD_DATA_FRAGMENT_TAG";
    private static final String ADD_DATA_SAVE_FRAGMENT_TAG = "com.itdoors.haccp.activities.AddStatisticsActivity.ADD_DATA_SAVE_FRAGMENT_TAG";

    protected Fragment mFragment;

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(com.actionbarsherlock.view.Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setSupportProgressBarIndeterminateVisibility(false);

        if (savedInstanceState != null) {
            mFragment = getSupportFragmentManager().getFragment(savedInstanceState,
                    ADD_DATA_SAVE_FRAGMENT_TAG);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mFragment != null)
            getSupportFragmentManager().putFragment(outState, ADD_DATA_SAVE_FRAGMENT_TAG,
                    mFragment);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        super.onConnected(connectionHint);

        if (mFragment == null) {
            mFragment = getSupportFragmentManager().findFragmentByTag(ADD_DATA_FRAGMENT_TAG);
            if (mFragment == null)
                mFragment = getNewAddDataFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(getFragmentResourceId(), mFragment, ADD_DATA_FRAGMENT_TAG)
                    .commit();

        }
    }

    protected abstract Fragment getNewAddDataFragment();

    protected abstract int getFragmentResourceId();

}
