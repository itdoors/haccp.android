
package com.itdoors.haccp.ui.fragments;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.itdoors.haccp.R;
import com.itdoors.haccp.model.CompanyObject;
import com.itdoors.haccp.model.Contour;
import com.itdoors.haccp.model.Point;
import com.itdoors.haccp.provider.HaccpContract;
import com.itdoors.haccp.ui.adapters.SimpleSectionedListAdapter;
import com.itdoors.haccp.utils.ContextUtils;

public class PointsSectionesListFragment extends ListFragment implements
        LoaderManager.LoaderCallbacks<Cursor> {

    public interface OnPointPressedListener {
        public void onPointPressed(Point point);

        public void onPointPressed(String pointId);
    }

    private static final String COMPANY_OBJECT_TAG = "com.itdoors.haccp.fragments.PointsSectionesListFragment.COMPANY_OBJECT_TAG";
    private static final String CONTOUR_TAG = "com.itdoors.haccp.fragments.PointsSectionesListFragment.CONTOUR_TAG";
    private static final String QUERY_TAG = "com.itdoors.haccp.fragments.PointsSectionesListFragment.QUERY_TAG";

    private MyPointsAdapter mPointsAdapter;
    private SimpleSectionedListAdapter mSectionedListAdapter;
    private OnPointPressedListener mOnPointPressedListener;

    public static PointsSectionesListFragment newInstance(CompanyObject companyObject,
            Contour contour) {
        PointsSectionesListFragment fragment = new PointsSectionesListFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(COMPANY_OBJECT_TAG, companyObject);
        bundle.putSerializable(CONTOUR_TAG, contour);

        fragment.setArguments(bundle);
        return fragment;

    }

    public static Fragment newInstance(CompanyObject companyObject,
            Contour contour, String query) {

        PointsSectionesListFragment fragment = new PointsSectionesListFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(COMPANY_OBJECT_TAG, companyObject);
        bundle.putSerializable(CONTOUR_TAG, contour);
        bundle.putString(QUERY_TAG, query);

        fragment.setArguments(bundle);
        return fragment;
    }

    @SuppressWarnings("unused")
    private interface PointsQuery {

        int _TOKEN = 0;
        String[] PROJECTION = new String[] {
                HaccpContract.Points._ID,
                HaccpContract.Points.UID,
                HaccpContract.Points.NAME,

                HaccpContract.Points.PLANS_UID_PROJECTION,
                HaccpContract.Points.PLANS_NAME_PROJECTION
        };

        int _ID = 0;
        int UID = 1;
        int NAME = 2;
        int PLANS_UID = 3;
        int PLANS_NAME = 4;

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mOnPointPressedListener = (OnPointPressedListener) activity;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final ListView mListView = getListView();
        mListView.setDrawSelectorOnTop(true);
        mListView.setSelector(R.drawable.abs__tab_indicator_ab_holo);
        mListView.setCacheColorHint(Color.TRANSPARENT);

        ContextUtils.wrapListView(mListView);

        mPointsAdapter = new MyPointsAdapter(getActivity());
        mSectionedListAdapter = new SimpleSectionedListAdapter(getActivity(),
                R.layout.list_item_point_plans_header, mPointsAdapter);

        if (isInSeachMode()) {
            int emptyResourceId = R.string.no_matches;
            setEmptyText(getString(emptyResourceId));
        }

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setListAdapter(mSectionedListAdapter);
        getLoaderManager().initLoader(PointsQuery._TOKEN, null, this);
    }

    private boolean isInSeachMode() {
        return getArguments() != null && getArguments().getString(QUERY_TAG) != null;
    }

    private void onPointsLoadFinished(Cursor cursor) {
        if (mPointsAdapter != null && cursor != null) {

            List<SimpleSectionedListAdapter.Section> sections = new ArrayList<SimpleSectionedListAdapter.Section>();
            cursor.moveToFirst();

            long previousHeaderId = -1;
            long headerId;

            while (!cursor.isAfterLast()) {
                headerId = cursor.getInt(PointsQuery.PLANS_UID);
                if (headerId != previousHeaderId) {
                    int position = cursor.getPosition();
                    String title = cursor.getString(PointsQuery.PLANS_NAME);
                    sections.add(new SimpleSectionedListAdapter.Section(position, title));
                }
                previousHeaderId = headerId;
                cursor.moveToNext();
            }
            mPointsAdapter.swapCursor(cursor); // swap the new cursor in.
            SimpleSectionedListAdapter.Section[] dummy =
                    new SimpleSectionedListAdapter.Section[sections.size()];
            mSectionedListAdapter.setSections(sections.toArray(dummy));

        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle data) {

        if (id == PointsQuery._TOKEN) {
            if (getArguments() != null) {

                int companyObjectId = ((CompanyObject) getArguments().getSerializable(
                        COMPANY_OBJECT_TAG)).getId();
                int contourId = ((Contour) getArguments().getSerializable(CONTOUR_TAG)).getId();
                String query = getArguments().getString(QUERY_TAG);

                Uri uri;
                if (query == null)
                    uri = HaccpContract.Points.builduriForCompanyObjectInContour(companyObjectId,
                            contourId);
                else
                    uri = HaccpContract.Points.buildSearchUri(companyObjectId, contourId, query);

                return new CursorLoader(
                        getActivity(),
                        uri,
                        PointsQuery.PROJECTION,
                        null,
                        null,
                        null);
            }
            return null;
        }
        throw new IllegalArgumentException("unknown loader id: " + id);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        if (loader.getId() == PointsQuery._TOKEN) {
            onPointsLoadFinished(cursor);
        }
        else {
            throw new IllegalArgumentException("unknown loader id: " + loader.getId());
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch (loader.getId()) {
            case PointsQuery._TOKEN:
                if (mPointsAdapter != null)
                    mPointsAdapter.swapCursor(null);
                break;
            default:
                throw new IllegalArgumentException("unknown loader id: " + loader.getId());
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        if (!mSectionedListAdapter.isSectionHeaderPosition(position)
                && mOnPointPressedListener != null) {
            Cursor cursor = (Cursor) mSectionedListAdapter.getItem(position);
            String pointId = cursor.getString(PointsQuery.UID);
            mOnPointPressedListener.onPointPressed(pointId);
        }
    }

    private class MyPointsAdapter extends CursorAdapter {
        public MyPointsAdapter(Context context) {
            super(context, null, 0);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            return LayoutInflater.from(context).inflate(R.layout.list_item_point,
                    parent, false);
        }

        @Override
        public void bindView(View view, Context context, final Cursor cursor) {
            TextView nameView = (TextView) view.findViewById(R.id.point_item_name);
            String name = cursor.getString(PointsQuery.NAME);
            nameView.setText(name);
        }
    }

}
