
package com.itdoors.haccp.ui.fragments;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockListFragment;
import com.itdoors.haccp.R;
import com.itdoors.haccp.model.Company;
import com.itdoors.haccp.provider.HaccpContract;

public class CompaniesFragment extends SherlockListFragment implements
        LoaderManager.LoaderCallbacks<Cursor> {

    public interface OnCompanyPressedListener {
        public void onCompanyPressedListener(Company companyObject);
    }

    private SimpleCursorAdapter mAdapter;
    private OnCompanyPressedListener mOnCompanyPressedListener;

    @SuppressWarnings("unused")
    private interface CompaniesQuery {

        String[] PROJECTION = new String[] {
                HaccpContract.Companies._ID,
                HaccpContract.CompanyObjects.NAME,
                HaccpContract.CompanyObjects.UID
        };

        int _ID = 0;
        int NAME = 1;
        int UID = 2;

    }

    private static final String[] FROM_COLUMNS = new String[] {
            HaccpContract.Companies.NAME,
    };

    private static final int[] TO_FIELDS = new int[] {
            R.id.comp_obj_name
    };

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mOnCompanyPressedListener = (OnCompanyPressedListener) activity;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final ListView mListView = getListView();
        mListView.setSelector(R.drawable.abs__tab_indicator_ab_holo);
        mListView.setCacheColorHint(Color.TRANSPARENT);

        mAdapter = new SimpleCursorAdapter(getActivity(), R.layout.list_item_company_obj,
                null, FROM_COLUMNS, TO_FIELDS, 0
                );

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setListAdapter(mAdapter);
        getLoaderManager().initLoader(0, null, this);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(getActivity(), // Context
                HaccpContract.Companies.CONTENT_URI, // URI
                CompaniesQuery.PROJECTION, // Projection
                null, // Selection
                null, // Selection args
                HaccpContract.Companies.SORT_BY_NAME); // Sort
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        if (mAdapter != null && cursor != null) {
            mAdapter.swapCursor(cursor); // swap the new cursor in.
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        if (mAdapter != null) {
            mAdapter.swapCursor(null);
        }
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);

        Cursor c = (Cursor) mAdapter.getItem(position);
        int uid = c.getInt(CompaniesQuery.UID);
        String name = c.getString(CompaniesQuery.NAME);
        Company company = new Company(uid, name);

        if (mOnCompanyPressedListener != null) {
            mOnCompanyPressedListener.onCompanyPressedListener(company);

        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mOnCompanyPressedListener = null;
    }
}
