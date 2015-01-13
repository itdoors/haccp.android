
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
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.itdoors.haccp.R;
import com.itdoors.haccp.model.CompanyObject;
import com.itdoors.haccp.model.Contour;
import com.itdoors.haccp.model.Plan;
import com.itdoors.haccp.provider.HaccpContract;
import com.itdoors.haccp.ui.adapters.SimpleSectionedListAdapter;
import com.itdoors.haccp.ui.adapters.SimpleSectionedListAdapter.Section;
import com.itdoors.haccp.ui.interfaces.OnPlanPressedListener;
import com.itdoors.haccp.ui.interfaces.OnPointPressedListener;
import com.itdoors.haccp.utils.ContextUtils;

public class PointsSectionesListFragment extends ListFragment implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final String COMPANY_OBJECT_TAG = "com.itdoors.haccp.fragments.PointsSectionesListFragment.COMPANY_OBJECT_TAG";
    private static final String CONTOUR_TAG = "com.itdoors.haccp.fragments.PointsSectionesListFragment.CONTOUR_TAG";
    private static final String QUERY_TAG = "com.itdoors.haccp.fragments.PointsSectionesListFragment.QUERY_TAG";

    private MyPointsAdapter mPointsAdapter;
    private MySectionedAdapter mSectionedListAdapter;

    private OnPointPressedListener mOnPointPressedListener;
    private OnPlanPressedListener mOnPlanPressedListener;

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

    public interface PointsQuery {

        int _TOKEN = 0;
        String[] PROJECTION = new String[] {
                HaccpContract.Points._ID,
                HaccpContract.Points.UID,
                HaccpContract.Points.NAME,

                HaccpContract.Points.PLANS_UID_PROJECTION,
                HaccpContract.Points.PLANS_NAME_PROJECTION,

                HaccpContract.Points.GROUP_UID_PROJECTION,
                HaccpContract.Points.GROUP_NAME_PROJECTION,

        };

        int _ID = 0;
        int UID = 1;
        int NAME = 2;
        int PLANS_UID = 3;
        int PLANS_NAME = 4;
        int GROUP_UID = 5;
        int GROUP_NAME = 6;

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mOnPointPressedListener = (OnPointPressedListener) activity;
        if (activity instanceof OnPlanPressedListener) {
            mOnPlanPressedListener = (OnPlanPressedListener) activity;
        }
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
        mSectionedListAdapter = new MySectionedAdapter(getActivity(),
                R.layout.list_item_point_plans_header, mPointsAdapter, !isInSeachMode());

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

            int previousHeaderId = -1;
            int headerId;

            while (!cursor.isAfterLast()) {
                headerId = cursor.getInt(PointsQuery.PLANS_UID);
                if (headerId != previousHeaderId) {
                    int position = cursor.getPosition();
                    String title = cursor.getString(PointsQuery.PLANS_NAME);
                    Plan plan = new Plan(headerId, title);
                    sections.add(new SimpleSectionedListAdapter.Section(position, title, plan));
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
                    uri = HaccpContract.Points.buildUriForCompanyObjectInContour(companyObjectId,
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
        // super.onListItemClick(l, v, position, id);

        if (!mSectionedListAdapter.isSectionHeaderPosition(position)
                && mOnPointPressedListener != null) {
            Cursor cursor = (Cursor) mSectionedListAdapter.getItem(position);
            String pointId = cursor.getString(PointsQuery.UID);
            mOnPointPressedListener.onPointPressed(pointId);
        }

        if (!isInSeachMode() && mSectionedListAdapter.isSectionHeaderPosition(position)
                && mOnPlanPressedListener != null) {
            Plan plan = (Plan) ((Section) mSectionedListAdapter.getItem(position)).getTag();
            mOnPlanPressedListener.onPlanPressed(plan);
        }
    }

    public static class MyPointsAdapter extends CursorAdapter {
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

    public static class MySectionedAdapter extends SimpleSectionedListAdapter {

        private boolean isHeaderViewEnabled;

        public MySectionedAdapter(Context context, int sectionResourceId, ListAdapter baseAdapter,
                boolean isHeaderViewEnabled) {
            super(context, sectionResourceId, baseAdapter);
            this.isHeaderViewEnabled = isHeaderViewEnabled;
        }

        @Override
        public boolean isEnabled(int position) {
            return isSectionHeaderPosition(position) ? isHeaderViewEnabled
                    : getBaseAdapter().isEnabled(sectionedPositionToPosition(position));
        }

    }

}
