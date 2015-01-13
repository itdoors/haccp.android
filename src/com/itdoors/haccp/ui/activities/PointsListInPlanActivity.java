
package com.itdoors.haccp.ui.activities;

import java.util.ArrayList;
import java.util.List;

import android.content.AsyncQueryHandler;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.itdoors.haccp.Intents;
import com.itdoors.haccp.R;
import com.itdoors.haccp.events.AddZeroExceptEvent;
import com.itdoors.haccp.events.ApplyAddZeroExceptEvent;
import com.itdoors.haccp.events.ConfirmZeroToAllPointInPlanEvent;
import com.itdoors.haccp.events.ConfirmZeroToOthersPointsInPlanEvent;
import com.itdoors.haccp.events.PointSelectedEvent;
import com.itdoors.haccp.model.CompanyObject;
import com.itdoors.haccp.model.Contour;
import com.itdoors.haccp.model.Plan;
import com.itdoors.haccp.model.Point;
import com.itdoors.haccp.provider.HaccpContract;
import com.itdoors.haccp.rest.AsyncSQLiteTransactionalOperations;
import com.itdoors.haccp.ui.fragments.AddStatisticsAndChangeStatusFragment.GroupCharacteristicsQuery;
import com.itdoors.haccp.ui.fragments.PointSectionedListSelectedFragment;
import com.itdoors.haccp.ui.fragments.SelectedPointsFragment;
import com.itdoors.haccp.ui.fragments.dialogs.ConfirmZeroToAllPointsDialog;
import com.itdoors.haccp.ui.fragments.dialogs.ConfirmZeroToOtherPointsDialog;
import com.itdoors.haccp.ui.fragments.dialogs.ConfirmationDialogFragment;
import com.itdoors.haccp.utils.DateUtils;
import com.itdoors.haccp.utils.Logger;
import com.itdoors.haccp.utils.ToastUtil;

import de.greenrobot.event.EventBus;

public class PointsListInPlanActivity extends GooglePlayServicesLocationActivity {

    public static Intent newIntent(Context context, Plan plan, CompanyObject companyobject,
            Contour contour) {

        Intent intent = new Intent(context, PointsListInPlanActivity.class);
        intent.putExtra(Intents.Plan.PLAN, plan);
        intent.putExtra(Intents.CompanyObject.COMPANY_OBJECT, companyobject);
        intent.putExtra(Intents.Contour.CONTOUR, contour);

        return intent;

    }

    private static final String FRAGMENT_TAG = "com.itdoors.haccp.activities.PointsListInPlanActivity.FRAGMENT_TAG";
    private static final String CONFIRM_ZERO_TO_ALL_DIALOG_TAG = "com.itdoors.haccp.activities.PointsListInPlanActivity.CONFIRM_ZERO_TO_ALL_DIALOG_TAG";
    private static final String CONFIRM_ZERO_TO_ALL_EXCEPT_DIALOG_TAG = "com.itdoors.haccp.activities.PointsListInPlanActivity.CONFIRM_ZERO_TO_ALL_EXCEPT_DIALOG_TAG";

    private boolean selectedMode = true;

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
        setContentView(R.layout.activity_points);
        setTitle(R.string.points);
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        super.onConnected(connectionHint);
        initFragment();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("selectedMode", selectedMode);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        selectedMode = savedInstanceState.getBoolean("selectedMode");
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    protected void initFragment() {

        if (getIntent().getExtras() != null) {

            Fragment mFragment = getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG);
            if (mFragment == null) {

                CompanyObject companyObject = getCompanyObject();
                Contour contour = getContour();
                Plan plan = getPlan();
                mFragment = PointSectionedListSelectedFragment
                        .newInstance(companyObject, contour, plan);
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.cpoints_list_frame, mFragment, FRAGMENT_TAG)
                        .commit();
            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        if (selectedMode) {

            menu.add(Menu.NONE, 1, Menu.NONE, R.string.zero_to_others)
                    .setShowAsAction(
                            MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
            menu.add(Menu.NONE, 2, Menu.NONE, R.string.zero_to_all)
                    .setShowAsAction(
                            MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
            return true;
        }

        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case 1:
                onAddExceptClicked();
                return true;
            case 2:
                onAddZeroToAllClicked();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void onAddZeroToAllClicked() {

        Plan plan = getPlan();
        Contour contour = (Contour) getIntent().getExtras()
                .getSerializable(Intents.Contour.CONTOUR);

        String msg = String.format(getString(R.string.conrifmation_for_zero_to_all_msg),
                plan.getName(),
                contour.getName());

        DialogFragment dialogFragment = new ConfirmZeroToAllPointsDialog();
        dialogFragment.setArguments(
                ConfirmationDialogFragment.prepareArguments(
                        msg,
                        getString(R.string.confirm_action),
                        getString(R.string.confirm),
                        getString(R.string.cancel)
                        )
                );

        dialogFragment.show(getSupportFragmentManager(), CONFIRM_ZERO_TO_ALL_DIALOG_TAG);

    }

    private void onAddExceptClicked() {
        EventBus.getDefault().post(new AddZeroExceptEvent());
    }

    public void onEventMainThread(ConfirmZeroToAllPointInPlanEvent event) {
        onZeroSubmited();
    }

    public void onEventMainThread(ConfirmZeroToOthersPointsInPlanEvent event) {
        onZeroToOthersASubmited(event.getPoints());
    }

    public void onEventMainThread(PointSelectedEvent event) {
        Point point = event.getPoint();
        Intent intent = PointDetailsActivity.newIntent(this, point.getUID());
        startActivity(intent);
    }

    public void onEventMainThread(ApplyAddZeroExceptEvent event) {
        onApplyAddZeroToOthersEvent(event.getPoints());
    }

    private void onApplyAddZeroToOthersEvent(List<Point> points) {

        Plan plan = getPlan();
        Contour contour = (Contour) getIntent().getExtras()
                .getSerializable(Intents.Contour.CONTOUR);

        StringBuilder pointsSb = new StringBuilder();
        int index = 0;
        for (Point point : points) {
            pointsSb.append(point.getNumber());
            index++;
            if (index < points.size()) {
                pointsSb.append(", ");
            }
        }

        String pointsList = pointsSb.toString();

        String msg = String.format(getString(R.string.conrifmation_for_zero_to_others_msg),
                plan.getName(),
                contour.getName(),
                pointsList
                );

        DialogFragment dialogFragment = new ConfirmZeroToOtherPointsDialog();

        Bundle argsBundle = ConfirmationDialogFragment.prepareArguments(
                msg,
                getString(R.string.confirm_action),
                getString(R.string.confirm),
                getString(R.string.cancel)
                );

        argsBundle.putSerializable(ConfirmZeroToOtherPointsDialog.POINTS_TAG,
                (ArrayList<Point>) points);

        dialogFragment.setArguments(argsBundle);
        dialogFragment.show(getSupportFragmentManager(), CONFIRM_ZERO_TO_ALL_EXCEPT_DIALOG_TAG);

    }

    private void onZeroSubmited() {

        PointSectionedListSelectedFragment mFragment = (PointSectionedListSelectedFragment) getSupportFragmentManager()
                .findFragmentByTag(FRAGMENT_TAG);
        List<Point> points = mFragment.getAllPoints();

        Location currentLocation = getLocationClient().getLastLocation();
        String date = Long.toString(DateUtils.getCurrentTime());
        for (Point point : points) {
            addZero(getApplicationContext(), point, currentLocation, date);
        }

        Logger.Logd(getClass(), "onZeroSubmited(), points count : " + points.size());
        ToastUtil.ToastLong(getApplicationContext(),
                getString(R.string.data_will_be_entered_on_the_server));

        finish();

    }

    private void onZeroToOthersASubmited(List<Point> selectedPoints) {

        Logger.Logd(getClass(), "points submited: " + selectedPoints.toString());

        PointSectionedListSelectedFragment mFragment =
                (PointSectionedListSelectedFragment) getSupportFragmentManager()
                        .findFragmentByTag(FRAGMENT_TAG);
        List<Point> allPoints = mFragment.getAllPoints();
        boolean removed = allPoints.removeAll(selectedPoints);

        Logger.Logd(getClass(), "removed:" + removed);

        Location currentLocation = getLocationClient().getLastLocation();
        String date = Long.toString(DateUtils.getCurrentTime());
        for (Point point : allPoints) {
            addZero(getApplicationContext(), point, currentLocation, date);
        }

        Logger.Logd(getClass(), "onZeroToOthersASubmited(), points count : " + allPoints.size());

        selectedMode = false;
        supportInvalidateOptionsMenu();

        Plan plan = getPlan();
        Contour contour = getContour();
        CompanyObject companyObject = getCompanyObject();

        if (!allPoints.isEmpty())
            ToastUtil.ToastLong(getApplicationContext(),
                    getString(R.string.data_will_be_entered_on_the_server));

        SelectedPointsFragment newFragment = SelectedPointsFragment.newInstance(companyObject,
                contour, plan, (ArrayList<Point>) selectedPoints);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.cpoints_list_frame, newFragment, FRAGMENT_TAG)
                .commit();

    }

    public Plan getPlan() {
        return (Plan) getIntent().getExtras().getSerializable(Intents.Plan.PLAN);
    }

    public Contour getContour() {
        return (Contour) getIntent().getExtras().getSerializable(Intents.Contour.CONTOUR);
    }

    public CompanyObject getCompanyObject() {
        return (CompanyObject) getIntent().getExtras().getSerializable(
                Intents.CompanyObject.COMPANY_OBJECT);
    }

    private static void addZero(final Context context, final Point point, final Location location,
            final String date) {

        final Double value = 0d;
        AsyncQueryHandler handler = new AsyncQueryHandler(context.getContentResolver()) {
            @Override
            protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
                try {
                    cursor.moveToFirst();
                    if (cursor.getColumnCount() > 0) {
                        while (!cursor.isAfterLast()) {
                            int characteristicsId = cursor.getInt(GroupCharacteristicsQuery.UID);
                            AsyncSQLiteTransactionalOperations.startInsertStatistics(
                                    context.getContentResolver(),
                                    point.getUID(),
                                    characteristicsId, date, date,
                                    Integer.toString(value.intValue()),
                                    location);
                            cursor.moveToNext();
                        }
                    }
                }
                finally {
                    cursor.close();
                }
            }
        };

        Uri characteristicsUri = HaccpContract.GroupCharacterisitcs.buildUriForGroup(point
                .getGroup().getId());
        handler.startQuery(0, null, characteristicsUri, GroupCharacteristicsQuery.PROJECTION, null,
                null, null);

    }
}
