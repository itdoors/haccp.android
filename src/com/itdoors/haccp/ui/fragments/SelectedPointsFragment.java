
package com.itdoors.haccp.ui.fragments;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockListFragment;
import com.itdoors.haccp.R;
import com.itdoors.haccp.events.PointSelectedEvent;
import com.itdoors.haccp.model.CompanyObject;
import com.itdoors.haccp.model.Contour;
import com.itdoors.haccp.model.Plan;
import com.itdoors.haccp.model.Point;
import com.itdoors.haccp.ui.adapters.SimpleSectionedListAdapter;
import com.itdoors.haccp.ui.adapters.SimpleSectionedListAdapter.Section;
import com.itdoors.haccp.ui.fragments.PointsSectionesListFragment.MySectionedAdapter;
import com.itdoors.haccp.utils.ContextUtils;

import de.greenrobot.event.EventBus;

public class SelectedPointsFragment extends SherlockListFragment implements
        ListView.OnItemClickListener {

    private static final String COMPANY_OBJECT_TAG = "com.itdoors.haccp.fragments.SelectedPointsFragment.COMPANY_OBJECT_TAG";
    private static final String CONTOUR_TAG = "com.itdoors.haccp.fragments.SelectedPointsFragment.CONTOUR_TAG";
    private static final String PLAN_TAG = "com.itdoors.haccp.fragments.SelectedPointsFragment.PLAN_TAG";
    private static final String POINTS_TAG = "com.itdoors.haccp.fragments.SelectedPointsFragment.POINTS_TAG";

    public static SelectedPointsFragment newInstance(CompanyObject companyObject,
            Contour contour, Plan plan, ArrayList<Point> points) {

        SelectedPointsFragment fragment = new SelectedPointsFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(COMPANY_OBJECT_TAG, companyObject);
        bundle.putSerializable(CONTOUR_TAG, contour);
        bundle.putSerializable(PLAN_TAG, plan);
        bundle.putSerializable(POINTS_TAG, points);

        fragment.setArguments(bundle);
        return fragment;
    }

    private MySectionedAdapter mSectionedListAdapter;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ListView mListView = getListView();

        mListView.setDrawSelectorOnTop(true);
        mListView.setOnItemClickListener(this);
        ContextUtils.wrapListView(mListView);

        mSectionedListAdapter = new PointsSectionesListFragment.MySectionedAdapter(
                getActivity(),
                R.layout.list_item_point_plans_header,
                new MyPointsAdapter(getActivity(), getPoints()),
                false);

        Plan plan = getPlan();
        Section[] sections = {
                new SimpleSectionedListAdapter.Section(0, plan.getName(), plan)
        };

        mSectionedListAdapter.setSections(sections);

    }

    @SuppressWarnings("unchecked")
    public ArrayList<Point> getPoints() {
        return (ArrayList<Point>) getArguments().getSerializable(POINTS_TAG);
    }

    public Plan getPlan() {
        return (Plan) getArguments().getSerializable(PLAN_TAG);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setListAdapter(mSectionedListAdapter);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        if (!mSectionedListAdapter.isSectionHeaderPosition(position)) {
            Point point = (Point) mSectionedListAdapter.getItem(position);
            EventBus.getDefault().post(new PointSelectedEvent(point));
        }

    }

    private static class MyPointsAdapter extends BaseAdapter {

        private List<Point> mPoints;
        private LayoutInflater mInflater;

        public MyPointsAdapter(Context context, List<Point> points) {
            this.mInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.mPoints = points;
        }

        @Override
        public int getCount() {
            return mPoints.size();
        }

        @Override
        public Object getItem(int position) {
            return mPoints.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.list_item_point, parent, false);
                holder = new ViewHolder();
                holder.numberView = (TextView) convertView.findViewById(R.id.point_item_name);
                convertView.setTag(holder);
            }
            else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.numberView.setText(mPoints.get(position).getNumber());
            return convertView;
        }

        private static class ViewHolder {
            TextView numberView;
        }
    }
}
