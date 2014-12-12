
package com.itdoors.haccp.ui.fragments;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.itdoors.haccp.Global;
import com.itdoors.haccp.Intents;
import com.itdoors.haccp.R;
import com.itdoors.haccp.provider.HaccpContract;
import com.itdoors.haccp.utils.DateUtils;
import com.itdoors.haccp.utils.Logger;

public class AttributesFragment extends Fragment implements LoaderCallbacks<Cursor> {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_point_attributes, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(PointQuery._TOKEN, null, this);

    }

    @SuppressWarnings("unused")
    private interface PointQuery {

        int _TOKEN = 0;
        String[] PROJECTION = new String[] {
                HaccpContract.Points._ID,
                HaccpContract.Points.UID,
                HaccpContract.Points.NAME,
                HaccpContract.Points.INSTALATION_DATE,
                HaccpContract.Points.PLANS_UID_PROJECTION,
                HaccpContract.Points.PLANS_NAME_PROJECTION,
                HaccpContract.Points.CONTOUR_UID_PROJECTION,
                HaccpContract.Points.CONTOUR_NAME_PROJECTION,
                HaccpContract.Points.CONTOUR_SLUG_PROJECTION,
                HaccpContract.Points.STATUS_UID_PROJECTION,
                HaccpContract.Points.STATUS_NAME_PROJECTION,
                HaccpContract.Points.STATUS_SLUG_PROJECTION,
                HaccpContract.Points.GROUP_UID_PROJECTION,
                HaccpContract.Points.GROUP_NAME_PROJECTION,

                HaccpContract.Points.POISON_UID_PROJECTION,
                HaccpContract.Points.POISON_NAME_PROJECTION,
                HaccpContract.Points.POISON_ACTIVE_SUBSTANCE_PROJECTION

        };

        int _ID = 0;
        int UID = 1;
        int NAME = 2;
        int INSTALATION_DATE = 3;
        int PLANS_UID = 4;
        int PLANS_NAME = 5;
        int CONTOUR_UID = 6;
        int CONTOUR_NAME = 7;
        int CONTOUR_SLUG = 8;
        int STATUS_UID = 9;
        int STATUS_NAME = 10;
        int STATUS_SLUG = 11;
        int GROUP_UID = 12;
        int GROUP_NAME = 13;

        int POISON_UID = 14;
        int POISON_NAME = 15;
        int POISON_ACTIVE_SUBSTANCE = 16;

    }

    @SuppressLint("SimpleDateFormat")
    private void fillViews(final Cursor cursor) {

        if (cursor != null) {

            cursor.moveToFirst();

            String timeStamp = formatEmpty(cursor.getString(PointQuery.INSTALATION_DATE), "-");
            Date date = DateUtils.getDate(timeStamp);

            String owner = "-";
            String instDate = fomatDateEmpty(date, "-");
            String number = formatEmpty(cursor.getString(PointQuery.NAME), "-");
            String type = formatEmpty(cursor.getString(PointQuery.GROUP_NAME), "-");
            String multiBurrierLevel = formatEmpty(cursor.getString(PointQuery.CONTOUR_NAME), "-");
            String monitoringObject = formatEmpty(cursor.getString(PointQuery.PLANS_NAME), "-");
            String status = formatEmpty(cursor.getString(PointQuery.STATUS_NAME), "-");

            ((TextView) getView().findViewById(R.id.cp_attr_point_number)).setText(number);
            ((TextView) getView().findViewById(R.id.cp_attr_inst_date)).setText(instDate);
            ((TextView) getView().findViewById(R.id.cp_attr_who_set)).setText(owner);
            ((TextView) getView().findViewById(R.id.cp_attr_point_type)).setText(type);
            ((TextView) getView().findViewById(R.id.cp_attr_mlevel)).setText(multiBurrierLevel);
            ((TextView) getView().findViewById(R.id.cp_attr_object)).setText(monitoringObject);
            ((TextView) getView().findViewById(R.id.cp_attr_status)).setText(status);

            getActivity().setTitle(type);

            String poison_uid = cursor.getString(PointQuery.POISON_UID);
            if (poison_uid != null) {

                String poisonName = formatEmpty(cursor.getString(PointQuery.POISON_NAME), "-");
                String poisonActiveSubstance = formatEmpty(
                        cursor.getString(PointQuery.POISON_ACTIVE_SUBSTANCE), "-");

                getView().findViewById(R.id.product_descr_poison_holder)
                        .setVisibility(View.VISIBLE);
                getView().findViewById(R.id.product_descr_poison_active_substance_holder)
                        .setVisibility(View.VISIBLE);

                ((TextView) getView().findViewById(R.id.cp_poison)).setText(poisonName);
                ((TextView) getView().findViewById(R.id.cp_poison_active_substance))
                        .setText(poisonActiveSubstance);

            }
        }
        else {

            String empty = "";
            ((TextView) getView().findViewById(R.id.cp_attr_point_number)).setText(empty);
            ((TextView) getView().findViewById(R.id.cp_attr_inst_date)).setText(empty);
            ((TextView) getView().findViewById(R.id.cp_attr_who_set)).setText(empty);
            ((TextView) getView().findViewById(R.id.cp_attr_point_type)).setText(empty);
            ((TextView) getView().findViewById(R.id.cp_attr_mlevel)).setText(empty);
            ((TextView) getView().findViewById(R.id.cp_attr_object)).setText(empty);
            ((TextView) getView().findViewById(R.id.cp_attr_status)).setText(empty);

        }
    }

    private static String formatEmpty(String value, String replacement) {
        return TextUtils.isEmpty(value) ? replacement : value;
    }

    @SuppressLint("SimpleDateFormat")
    private static String fomatDateEmpty(Date date, String replacement) {
        return date == null ? replacement : new SimpleDateFormat(Global.usualDateFromat)
                .format(date).toString();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == PointQuery._TOKEN) {

            Logger.Logd(getClass(), "onCreateLoader point id: "
                    + getActivity().getIntent().getStringExtra(
                            Intents.Point.UID));

            return new CursorLoader(getActivity(),
                    HaccpContract.Points.buildPointUri(getActivity().getIntent().getStringExtra(
                            Intents.Point.UID)),
                    PointQuery.PROJECTION,
                    null,
                    null,
                    null);

        }
        else
            throw new IllegalArgumentException("Unknown loader id: " + id);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (getActivity() == null) {
            return;
        }
        if (cursor != null && cursor.getCount() > 0) {
            fillViews(cursor);
        } else {
            fillViews((Cursor) null);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
}
