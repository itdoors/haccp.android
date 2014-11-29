
package com.itdoors.haccp.ui.adapters;

import java.util.Arrays;
import java.util.Comparator;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.itdoors.haccp.utils.Logger;

public class SectionedListAdapter extends BaseAdapter {

    private boolean mValid = false;
    private boolean mSectionValid = false;

    private ListAdapter mSectionBaseAdapter;
    private ListAdapter mBaseAdapter;

    @SuppressWarnings("unused")
    private Context mContext;

    private SparseArray<Section> mSections = new SparseArray<Section>();

    public static class Section {
        int firstPosition;
        int sectionedPosition;

        public Section(int postion) {
            this.firstPosition = postion;
        }
    }

    public SectionedListAdapter(Context context, ListAdapter sectionBaseAdapter,
            ListAdapter baseAdapter) {

        mContext = context;
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

        mSectionBaseAdapter = sectionBaseAdapter;
        mSectionBaseAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                mSectionValid = !mSectionBaseAdapter.isEmpty();
                notifyDataSetChanged();
            }

            @Override
            public void onInvalidated() {
                mSectionValid = false;
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

    public int indexOfSection(int position) {
        int index = -1;
        for (int i = 0; i < mSections.size(); i++) {
            if (mSections.valueAt(i).sectionedPosition == position)
                index = i;
        }
        return index;
    }

    public boolean isSectionHeaderPosition(int position) {
        return mSections.get(position) != null;
    }

    @Override
    public int getCount() {
        // return (mSectionValid ? mSectionBaseAdapter.getCount() : 0) +
        // (mValid ? mBaseAdapter.getCount() : 0) ;

        return mSectionValid && mValid ? mSectionBaseAdapter.getCount() + mBaseAdapter.getCount()
                : 0;

    }

    @Override
    public Object getItem(int position) {
        return isSectionHeaderPosition(position)
                ? mSectionBaseAdapter.getItem(indexOfSection(position))
                : mBaseAdapter.getItem(sectionedPositionToPosition(position));
    }

    @Override
    public long getItemId(int position) {
        return isSectionHeaderPosition(position)
                ? mSectionBaseAdapter.getItemId(indexOfSection(position))
                : mBaseAdapter.getItemId(sectionedPositionToPosition(position));
    }

    @Override
    public int getItemViewType(int position) {
        return isSectionHeaderPosition(position)
                ? mSectionBaseAdapter.getItemViewType(indexOfSection(position))
                : mBaseAdapter.getItemViewType(sectionedPositionToPosition(position));
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
        return mBaseAdapter.getViewTypeCount() + mSectionBaseAdapter.getViewTypeCount(); // the
                                                                                         // section
                                                                                         // headings
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean hasStableIds() {
        return mBaseAdapter.hasStableIds() && mSectionBaseAdapter.hasStableIds();
    }

    @Override
    public boolean isEmpty() {
        return mBaseAdapter.isEmpty() && mSectionBaseAdapter.isEmpty();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (isSectionHeaderPosition(position)) {
            try {
                return mSectionBaseAdapter.getView(indexOfSection(position), convertView, parent);
            } catch (Exception e) {
                Logger.Loge(getClass(), "position:" + position);
                return convertView = new View(parent.getContext());
            }
        } else {
            try {
                return mBaseAdapter.getView(sectionedPositionToPosition(position), convertView,
                        parent);
            } catch (Exception e) {
                Logger.Loge(getClass(), "position:" + position);
                return convertView = new View(parent.getContext());
            }
        }
    }

}
