
package com.itdoors.haccp.ui.fragments;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.widget.Button;

import com.actionbarsherlock.app.SherlockFragment;
import com.itdoors.haccp.Intents;
import com.itdoors.haccp.provider.HaccpContract;

public class AddDataFragment extends SherlockFragment {

    @SuppressWarnings("unused")
    private interface PointQuery {

        int _TOKEN = 0;
        String[] PROJECTION = new String[] {
                HaccpContract.Points._ID,
                HaccpContract.Points.UID,
                HaccpContract.Points.NAME,
                HaccpContract.Points.GROUP_UID_PROJECTION,
                HaccpContract.Points.GROUP_NAME_PROJECTION,
                HaccpContract.Points.STATUS_UID_PROJECTION
        };

        int _ID = 0;
        int UID = 1;
        int NAME = 2;
        int GROUP_UID = 3;
        int GROUP_NAME = 4;
        int STATUS_UID = 5;
    }

    protected Button addButton;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(PointQuery._TOKEN, null,
                new LoaderManager.LoaderCallbacks<Cursor>() {

                    @Override
                    public Loader<Cursor> onCreateLoader(int id, Bundle data) {

                        if (id == PointQuery._TOKEN) {

                            return new CursorLoader(
                                    getActivity(),
                                    getPointInfoUri(),
                                    PointQuery.PROJECTION,
                                    null,
                                    null,
                                    null);
                        }

                        throw new IllegalArgumentException("unknow loader id");
                    }

                    @Override
                    public void onLoadFinished(final Loader<Cursor> loader, final Cursor cursor) {

                    }

                    @Override
                    public void onLoaderReset(Loader<Cursor> loader) {

                    }

                });

    }

    private Uri getPointInfoUri() {
        if (getActivity() != null)
            return HaccpContract.Points.buildPointUri(getActivity().getIntent().getStringExtra(
                    Intents.Point.UID));
        else
            throw new IllegalStateException(" fragment is not Attached to activity");
    }
}
