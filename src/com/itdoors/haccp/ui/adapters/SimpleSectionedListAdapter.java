
package com.itdoors.haccp.ui.adapters;

import java.util.Arrays;
import java.util.Comparator;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.itdoors.haccp.R;

public class SimpleSectionedListAdapter extends BaseAdapter {
    private boolean mValid = true;
    private int mSectionResourceId;
    private LayoutInflater mLayoutInflater;
    private ListAdapter mBaseAdapter;
    private SparseArray<Section> mSections = new SparseArray<Section>();

    public static class Section {
        int firstPosition;
        int sectionedPosition;
        CharSequence title;
        Object tag;

        public Section(int firstPosition, CharSequence title, Object tag) {
            this.firstPosition = firstPosition;
            this.title = title;
            this.tag = tag;
        }

        public CharSequence getTitle() {
            return title;
        }

        public Object getTag() {
            return tag;
        }
    }

    public SimpleSectionedListAdapter(Context context, int sectionResourceId,
            ListAdapter baseAdapter) {
        mLayoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mSectionResourceId = sectionResourceId;
        mBaseAdapter = baseAdapter;
        mBaseAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                mValid = !mBaseAdapter.isEmpty();
                notifyDataSetChanged();
            }

            @Override
            public void onInvalidated() {
                mValid = false;
                notifyDataSetInvalidated();
            }
        });
    }

    public void setSections(Section[] sections) {
        mSections.clear();

        Arrays.sort(sections, new Comparator<Section>() {
            @Override
            public int compare(Section o, Section o1) {
                return (o.firstPosition == o1.firstPosition)
                        ? 0
                        : ((o.firstPosition < o1.firstPosition) ? -1 : 1);
            }
        });

        int offset = 0; // offset positions for the headers we're adding
        for (Section section : sections) {
            section.sectionedPosition = section.firstPosition + offset;
            mSections.append(section.sectionedPosition, section);
            ++offset;
        }

        notifyDataSetChanged();
    }

    public int positionToSectionedPosition(int position) {
        int offset = 0;
        for (int i = 0; i < mSections.size(); i++) {
            if (mSections.valueAt(i).firstPosition > position) {
                break;
            }
            ++offset;
        }
        return position + offset;
    }

    public int sectionedPositionToPosition(int sectionedPosition) {
        if (isSectionHeaderPosition(sectionedPosition)) {
            return ListView.INVALID_POSITION;
        }

        int offset = 0;
        for (int i = 0; i < mSections.size(); i++) {
            if (mSections.valueAt(i).sectionedPosition > sectionedPosition) {
                break;
            }
            --offset;
        }
        return sectionedPosition + offset;
    }

    public boolean isSectionHeaderPosition(int position) {
        return mSections.get(position) != null;
    }

    @Override
    public int getCount() {
        return (mValid ? mBaseAdapter.getCount() + mSections.size() : 0);
    }

    @Override
    public Object getItem(int position) {
        return isSectionHeaderPosition(position)
                ? mSections.get(position)
                : mBaseAdapter.getItem(sectionedPositionToPosition(position));
    }

    @Override
    public long getItemId(int position) {
        return isSectionHeaderPosition(position)
                ? Integer.MAX_VALUE - mSections.indexOfKey(position)
                : mBaseAdapter.getItemId(sectionedPositionToPosition(position));
    }

    @Override
    public int getItemViewType(int position) {
        return isSectionHeaderPosition(position)
                ? getViewTypeCount() - 1
                : mBaseAdapter.getItemViewType(position);
    }

    @Override
    public boolean isEnabled(int position) {
        // noinspection SimplifiableConditionalExpression
        return isSectionHeaderPosition(position)
                ? false
                : mBaseAdapter.isEnabled(sectionedPositionToPosition(position));
    }

    @Override
    public int getViewTypeCount() {
        return mBaseAdapter.getViewTypeCount() + 1; // the section headings
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean hasStableIds() {
        return mBaseAdapter.hasStableIds();
    }

    @Override
    public boolean isEmpty() {
        return mBaseAdapter.isEmpty();
    }

    protected ListAdapter getBaseAdapter() {
        return mBaseAdapter;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (isSectionHeaderPosition(position)) {
            TextView view;
            if (convertView == null) {
                convertView = mLayoutInflater.inflate(mSectionResourceId, parent, false);
            }
            view = (TextView) convertView.findViewById(R.id.list_item_schedule_header_textview);
            view.setText(mSections.get(position).title);
            return convertView;

        } else {
            return mBaseAdapter.getView(sectionedPositionToPosition(position), convertView, parent);
        }
    }

}
