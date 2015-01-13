
package com.itdoors.haccp.ui.fragments;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.itdoors.haccp.R;
import com.itdoors.haccp.events.AddZeroExceptEvent;
import com.itdoors.haccp.events.ApplyAddZeroExceptEvent;
import com.itdoors.haccp.events.PointSelectedEvent;
import com.itdoors.haccp.model.CompanyObject;
import com.itdoors.haccp.model.Contour;
import com.itdoors.haccp.model.Group;
import com.itdoors.haccp.model.Plan;
import com.itdoors.haccp.model.Point;
import com.itdoors.haccp.provider.HaccpContract;
import com.itdoors.haccp.ui.adapters.SimpleSectionedListAdapter;
import com.itdoors.haccp.ui.fragments.PointsSectionesListFragment.MySectionedAdapter;
import com.itdoors.haccp.ui.fragments.PointsSectionesListFragment.PointsQuery;
import com.itdoors.haccp.utils.ContextUtils;

import de.greenrobot.event.EventBus;

public class PointSectionedListSelectedFragment extends SherlockListFragment implements
        LoaderManager.LoaderCallbacks<Cursor>, ListView.OnItemClickListener {

    private static final String COMPANY_OBJECT_TAG = "com.itdoors.haccp.fragments.PointSectionedListSelectedFragment.COMPANY_OBJECT_TAG";
    private static final String CONTOUR_TAG = "com.itdoors.haccp.fragments.PointSectionedListSelectedFragment.CONTOUR_TAG";
    private static final String PLAN_TAG = "com.itdoors.haccp.fragments.PointSectionedListSelectedFragment.PLAN_TAG";

    private SelectablePointsAdapter mPointsAdapter;
    private MySectionedAdapter mSectionedListAdapter;

    private ActionMode mMode;

    public static PointSectionedListSelectedFragment newInstance(CompanyObject companyObject,
            Contour contour, Plan plan) {
        PointSectionedListSelectedFragment fragment = new PointSectionedListSelectedFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(COMPANY_OBJECT_TAG, companyObject);
        bundle.putSerializable(CONTOUR_TAG, contour);
        bundle.putSerializable(PLAN_TAG, plan);
        fragment.setArguments(bundle);
        return fragment;
    }

    public List<Point> getAllPoints() {

        List<Point> points = new ArrayList<Point>();
        int count = mPointsAdapter.getCount();
        if (count > 0) {
            for (int position = 0; position < count; position++) {
                Cursor cursor =
                        (Cursor) mPointsAdapter.getItem(position);

                String pointUID = cursor.getString(PointsQuery.UID);
                int groupId = cursor.getInt(PointsQuery.GROUP_UID);
                String groupName = cursor.getString(PointsQuery.GROUP_NAME);
                String pointNumber = cursor.getString(PointsQuery.NAME);

                Point point = new Point.Builder(pointUID)
                        .number(pointNumber)
                        .group(
                                new Group.Builder(groupId)
                                        .name(groupName)
                                        .build())
                        .build();

                points.add(point);
            }
        }
        return points;
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ListView mListView = getListView();

        mListView.setDrawSelectorOnTop(true);
        mListView.setOnItemClickListener(this);
        ContextUtils.wrapListView(mListView);

        mPointsAdapter = new SelectablePointsAdapter(getActivity());
        mSectionedListAdapter = new PointsSectionesListFragment.MySectionedAdapter(getActivity(),
                R.layout.list_item_point_plans_header, mPointsAdapter, false);

    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    public void onEventMainThread(AddZeroExceptEvent event) {
        toggleSelectMode();
    }

    private void toggleSelectMode() {

        ListView mListView = getListView();
        int choiseMode = mListView.getChoiceMode();

        switch (choiseMode) {
            case ListView.CHOICE_MODE_MULTIPLE:
                mListView.setChoiceMode(ListView.CHOICE_MODE_NONE);
                finishActionModeIfAvaliable();
                break;
            case ListView.CHOICE_MODE_NONE:
                mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                startActionModeIfAvaliable();
                break;
        }

    }

    private void startActionModeIfAvaliable() {
        if (mMode == null) {
            mMode = ((SherlockFragmentActivity) getActivity())
                    .startActionMode(new ModeCallback());
        }
    }

    private void finishActionModeIfAvaliable() {
        if (mMode != null) {
            mMode.finish();
            mMode = null;
        }
        mPointsAdapter.removeSelection();
    }

    private void prepareActionModeTitleIfAvaliable() {
        if (mMode != null) {
            mMode.setTitle(String.format(getString(R.string.selected),
                    String.valueOf(mPointsAdapter.getSelectedCount())));
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // Notice how the ListView api is lame
        // You can use mListView.getCheckedItemIds() if the adapter
        // has stable ids, e.g you're using a CursorAdaptor

        final ListView mListView = getListView();

        int choiseMode = mListView.getChoiceMode();
        switch (choiseMode) {
            case ListView.CHOICE_MODE_NONE: {

                Cursor cursor = (Cursor) mSectionedListAdapter.getItem(position);
                String pointId = cursor.getString(PointsQuery.UID);
                String pointName = cursor.getString(PointsQuery.NAME);

                Point point = new Point.Builder(pointId)
                        .number(pointName)
                        .build();
                EventBus.getDefault().post(new PointSelectedEvent(point));

                break;
            }
            case ListView.CHOICE_MODE_MULTIPLE: {

                mPointsAdapter.toggleSelection(position);
                prepareActionModeTitleIfAvaliable();

                boolean hasCheckedElement = hasCheckedElement();
                if (hasCheckedElement) {
                    startActionModeIfAvaliable();
                } else {
                    finishActionModeIfAvaliable();
                }

                break;

            }
            default:
                break;
        }

    }

    private boolean hasCheckedElement() {

        SparseBooleanArray checked = getListView().getCheckedItemPositions();
        boolean hasCheckedElement = false;
        if (checked != null) {
            for (int i = 0; i < checked.size() && !hasCheckedElement; i++) {
                hasCheckedElement = checked.valueAt(i);
            }
        }
        return hasCheckedElement;
    }

    public static class SelectablePointsAdapter extends PointsSectionesListFragment.MyPointsAdapter {

        private SparseBooleanArray mSelectedItemsPositions;
        private int mSelectedBgColor;

        public SelectablePointsAdapter(Context context) {
            super(context);
            mSelectedItemsPositions = new SparseBooleanArray();
            mSelectedBgColor = mContext.getResources().getColor(R.color.abs__holo_blue_light);
        }

        public int getSelectedCount() {
            return mSelectedItemsPositions.size();// mSelectedCount;
        }

        public SparseBooleanArray getSelectedPositions() {
            return mSelectedItemsPositions;
        }

        public void toggleSelection(int position)
        {

            boolean toggle = !mSelectedItemsPositions.get(position);
            selectView(position, toggle);

        }

        public void setSelection(SparseBooleanArray selection) {
            if (selection != null) {
                mSelectedItemsPositions = new SparseBooleanArray();
                for (int index = 0; index < selection.size(); index++) {
                    if (selection.valueAt(index)) {
                        mSelectedItemsPositions.put(selection.keyAt(index), true);
                    }
                }
                notifyDataSetChanged();
            }
        }

        public void removeSelection() {
            mSelectedItemsPositions = new SparseBooleanArray();
            notifyDataSetChanged();
        }

        public void selectView(int position, boolean value) {
            if (value) {
                mSelectedItemsPositions.put(position, value);
            }
            else {
                mSelectedItemsPositions.delete(position);
            }
            notifyDataSetChanged();
        }

        @Override
        public void bindView(View view, Context context, final Cursor cursor) {
            super.bindView(view, context, cursor);

            boolean selected = mSelectedItemsPositions.get(mCursor.getPosition() + 1);
            view.setBackgroundColor(selected ? mSelectedBgColor : Color.TRANSPARENT);

            TextView nameStrView = (TextView) view.findViewById(R.id.point_item_name_str);
            nameStrView.setTextColor(selected ? Color.WHITE : Color.BLACK);

            TextView nameView = (TextView) view.findViewById(R.id.point_item_name);
            nameView.setTextColor(selected ? Color.WHITE : Color.BLACK);

        }

    }

    private final class ModeCallback implements ActionMode.Callback {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            menu.add(Menu.NONE, 0, Menu.NONE, R.string.apply)
                    .setShowAsAction(
                            MenuItem.SHOW_AS_ACTION_ALWAYS
                                    | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            // Here, you can checked selected items to adapt available actions
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            // Destroying action mode, let's unselect all items

            final ListView mListView = getListView();
            mListView.clearChoices();
            mPointsAdapter.removeSelection();
            mMode = null;
            toggleSelectMode();

        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

            switch (item.getItemId()) {

                case 0: // Apply zero to others
                    SparseBooleanArray positions = mPointsAdapter.getSelectedPositions();
                    if (positions != null && positions.size() > 0) {
                        List<Point> points = new ArrayList<Point>();
                        for (int index = 0; index < positions.size(); index++) {
                            if (positions.valueAt(index)) {

                                Cursor cursor = (Cursor) mPointsAdapter
                                        .getItem(positions.keyAt(index) - 1);

                                String pointUID = cursor.getString(PointsQuery.UID);
                                String pointNumber = cursor.getString(PointsQuery.NAME);

                                int groupId = cursor.getInt(PointsQuery.GROUP_UID);
                                String groupName = cursor.getString(PointsQuery.GROUP_NAME);

                                Point point = new Point.Builder(pointUID)
                                        .number(pointNumber)
                                        .group(
                                                new Group.Builder(groupId)
                                                        .name(groupName)
                                                        .build())
                                        .build();
                                points.add(point);

                            }
                        }
                        if (!points.isEmpty()) {
                            EventBus.getDefault().post(new ApplyAddZeroExceptEvent(points));
                        }
                    }

                    break;
            }

            mode.finish();
            return true;
        }

    };

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setListAdapter(mSectionedListAdapter);
        getLoaderManager().initLoader(PointsSectionesListFragment.PointsQuery._TOKEN, null, this);
    }

    private void onPointsLoadFinished(Cursor cursor) {
        if (mPointsAdapter != null && cursor != null) {

            List<SimpleSectionedListAdapter.Section> sections = new ArrayList<SimpleSectionedListAdapter.Section>();
            cursor.moveToFirst();

            int previousHeaderId = -1;
            int headerId;

            while (!cursor.isAfterLast()) {
                headerId = cursor.getInt(PointsSectionesListFragment.PointsQuery.PLANS_UID);
                if (headerId != previousHeaderId) {
                    int position = cursor.getPosition();
                    String title = cursor
                            .getString(PointsSectionesListFragment.PointsQuery.PLANS_NAME);
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

        if (id == PointsSectionesListFragment.PointsQuery._TOKEN) {
            int companyObjectId = ((CompanyObject) getArguments().getSerializable(
                    COMPANY_OBJECT_TAG)).getId();
            int contourId = ((Contour) getArguments().getSerializable(CONTOUR_TAG)).getId();
            int planId = ((Plan) getArguments().getSerializable(PLAN_TAG)).getId();

            Uri uri = HaccpContract.Points.buidUriForCompanyObjectInContourInPlan(
                    companyObjectId, contourId, planId);

            return new CursorLoader(
                    getActivity(),
                    uri,
                    PointsSectionesListFragment.PointsQuery.PROJECTION,
                    null,
                    null,
                    null);
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

}
