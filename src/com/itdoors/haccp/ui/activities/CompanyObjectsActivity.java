
package com.itdoors.haccp.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.itdoors.haccp.Intents;
import com.itdoors.haccp.R;
import com.itdoors.haccp.model.Company;
import com.itdoors.haccp.model.CompanyObject;
import com.itdoors.haccp.ui.fragments.CompanyObjectsFragment;
import com.itdoors.haccp.utils.Logger;

public class CompanyObjectsActivity extends SherlockFragmentActivity implements
        CompanyObjectsFragment.OnCompanyObjectItemPressedListener {

    private static final String FRAGMENT_TAG = "com.itdoors.haccp.activities.CompanyObjectsActivity.FRAGMENT_TAG";

    private Fragment mFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setSupportProgressBarIndeterminateVisibility(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.activity_company_objects);
        setTitle(R.string.company_objects);

        initFragment();

    }

    public static Intent newIntentInstance(Context context, Company company) {

        Intent intent = new Intent(context, CompanyObjectsActivity.class);
        intent.putExtra(Intents.Company.COMPANY, company);
        return intent;
    }

    protected void initFragment() {

        if (getIntent().getExtras() != null) {

            mFragment = getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG);
            if (mFragment == null) {
                Company company = (Company) getIntent().getExtras().getSerializable(
                        Intents.Company.COMPANY);

                Logger.Logd(getClass(), "company:" + company.toString());

                mFragment = CompanyObjectsFragment.newInstance(company);
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.company_objects_frame, mFragment, FRAGMENT_TAG)
                        .commit();
            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onCompanyObjectPressedListener(CompanyObject companyObject) {
        if (getIntent().getExtras() != null) {
            startActivity(ServicesAndContoursActivity.newIntentInstance(this, companyObject));
        }
    }

}
