
package com.itdoors.haccp.ui.fragments;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.itdoors.haccp.Intents;
import com.itdoors.haccp.R;
import com.itdoors.haccp.model.DataType;
import com.itdoors.haccp.model.GroupCharacteristic;
import com.itdoors.haccp.model.GroupCharacteristicField;
import com.itdoors.haccp.model.InputType;
import com.itdoors.haccp.model.PointStatus;
import com.itdoors.haccp.model.PointStatus.CODE;
import com.itdoors.haccp.model.StatististicsItemStatus;
import com.itdoors.haccp.provider.HaccpContract;
import com.itdoors.haccp.utils.AppUtils;
import com.itdoors.haccp.utils.Logger;

public class AddStatisticsFragment extends SherlockFragment implements LoaderCallbacks<Cursor> {

    public interface OnAddPressedListener {
        public void onAddPressed(HashMap<GroupCharacteristic, Double> values);

        public void onChangeStatusPressed(PointStatus status);
    }

    private static final String CHEAKED_RADIO_BTN_SAVE = "com.itdoors.haccp.fragments.AddStatisticsFragment";

    private HashMap<GroupCharacteristicField, View> viewValueContainersMap;
    private HashMap<GroupCharacteristicField, View> viewContainersMap;

    private OnAddPressedListener mOnAddPressedListener;
    private RadioGroup mRadioGroup;

    private ArrayList<GroupCharacteristicField> mGroupCharacteristicsFields;
    private ArrayList<PointStatus> mStatuses;

    // three queries to db

    private Handler handler = new MyLoadingHandler(this);
    private CountDownLatch allLoadsCompleted;
    private Thread waitingThread;

    private Button addBtn;

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

    @SuppressWarnings("unused")
    private interface StatusesQuery {
        int _TOKEN = 1;
        String[] PROJECTION = new String[] {
                HaccpContract.Statuses._ID,
                HaccpContract.Statuses.UID,
                HaccpContract.Statuses.NAME,
                HaccpContract.Statuses.SLUG
        };

        int _ID = 0;
        int UID = 1;
        int NAME = 2;
        int SLUG = 3;

    }

    @SuppressWarnings("unused")
    private interface GroupCharacteristicsQuery {
        int _TOKEN = 2;
        String[] PROJECTION = new String[] {
                HaccpContract.GroupCharacterisitcs._ID,
                HaccpContract.GroupCharacterisitcs.UID,
                HaccpContract.GroupCharacterisitcs.NAME,
                HaccpContract.GroupCharacterisitcs.INPUT_TYPE,
                HaccpContract.GroupCharacterisitcs.DATA_TYPE,
                HaccpContract.GroupCharacterisitcs.UNIT,
                HaccpContract.GroupCharacterisitcs.ALLOW_VALUE_MIN,
                HaccpContract.GroupCharacterisitcs.ALLOW_VALUE_MAX,
                HaccpContract.GroupCharacterisitcs.CRITICAL_VALUE_BOTTOM,
                HaccpContract.GroupCharacterisitcs.CRITICAL_VALUE_TOP
        };

        int _ID = 0;
        int UID = 1;
        int NAME = 2;
        int INPUT_TYPE = 3;
        int DATA_TYPE = 4;
        int UNIT = 5;
        int ALLOW_VALUE_MIN = 6;
        int ALLOW_VALUE_MAX = 7;
        int CRITICAL_VALUE_BOTTOM = 8;
        int CRITICAL_VALUE_TOP = 9;
    }

    private static final int READY_MSG = 0;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Logger.Logi(getClass(), "onAttach");
        mOnAddPressedListener = (OnAddPressedListener) activity;
    }

    /**
     * 
     class OuterClass { static class InnerClass { private final
     * WeakReference<OuterClass> mTarget; InnerClass(OuterClass target) {
     * mTarget = new WeakReference<OuterClass>(target); } void doSomething() {
     * OuterClass target = mTarget.get(); if (target != null) target.do(); } } }
     */

    private static class MyLoadingHandler extends Handler {

        private final WeakReference<AddStatisticsFragment> mWeakRef;

        MyLoadingHandler(AddStatisticsFragment target) {
            mWeakRef = new WeakReference<AddStatisticsFragment>(target);
        }

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == READY_MSG) {
                AddStatisticsFragment fragment = mWeakRef.get();
                if (fragment != null && fragment.isAdded()) {
                    fragment.fillViews();
                }
            }
            super.handleMessage(msg);
        }

    }

    private static class MyLoadingRunnable implements Runnable {

        private final WeakReference<AddStatisticsFragment> mWeakRef;

        MyLoadingRunnable(AddStatisticsFragment target) {
            mWeakRef = new WeakReference<AddStatisticsFragment>(target);
        }

        @Override
        public void run() {

            if (!Thread.currentThread().isInterrupted()) {
                try {
                    AddStatisticsFragment fragment = mWeakRef.get();
                    if (fragment != null && fragment.isAdded()) {
                        try {
                            Logger.Logi(getClass(), "waiting thread locked");

                            fragment.allLoadsCompleted.await();
                            fragment.handler.sendEmptyMessage(READY_MSG);
                            fragment.waitingThread = null;

                            Logger.Logi(getClass(), "waiting thread finished");
                        } catch (InterruptedException e) {
                            Logger.Logi(getClass(), "Interrupted exception");
                            // mWeakRef.clear();
                        }
                    }
                } finally {
                    Logger.Logi(getClass(), Thread.currentThread().getName() + " was finished!");
                }
            }

        }
    };

    private void fillViews() {

        if (!isAdded())
            return;

        if (mGroupCharacteristicsFields != null && mStatuses != null) {
            setCharacteristicsViews(getLayoutInflater(null), (ViewGroup) getView(),
                    mGroupCharacteristicsFields);
            setStatusesRadioGroup(getLayoutInflater(null), (ViewGroup) getView(), mStatuses);
            addBtn.setEnabled(true);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_add_statistics, container,
                false);
        addBtn = (Button) root.findViewById(R.id.add_st_done_btn);
        addBtn.setEnabled(false);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnAddPressedListener != null) {
                    Action action = getActionType();
                    if (action != null) {
                        switch (action) {
                            case CHANGE_STATUS:
                                mOnAddPressedListener.onChangeStatusPressed(getStatus());
                                break;
                            case ADD_STATISTICS:
                                mOnAddPressedListener.onAddPressed(getValues());
                                break;
                        }
                    }
                }
            }
        });
        return root;
    }

    // cheak if fragment was recreated or not
    boolean isFragmentLostHisState = true;

    public static enum Action {
        ADD_STATISTICS, CHANGE_STATUS;
    }

    public Action getActionType() {
        if (mRadioGroup != null) {
            final int chechedId = mRadioGroup.getCheckedRadioButtonId();
            if (chechedId != -1)
                return Action.CHANGE_STATUS;
            else
                return Action.ADD_STATISTICS;
        }

        return null;

    }

    public PointStatus getStatus() {

        final int chechedId = mRadioGroup.getCheckedRadioButtonId();
        PointStatus status = (PointStatus) mRadioGroup.findViewById(chechedId).getTag();
        return status;
    }

    @Override
    public void onResume() {
        super.onResume();
        Logger.Logi(getClass(), "onResume");

        allLoadsCompleted = new CountDownLatch(3);

        getLoaderManager().initLoader(PointQuery._TOKEN, null, this);
        getLoaderManager().initLoader(StatusesQuery._TOKEN, null, this);

        if (isFragmentLostHisState) {

            Logger.Logi(getClass(), "waiting thread created !");
            waitingThread = new Thread(new MyLoadingRunnable(this));
            waitingThread.setName("WaitForDBResponces Tread" + "#" + waitingThread.getId());
            waitingThread.start();

        } else {
            addBtn.setEnabled(true);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Logger.Logi(getClass(), "onPause");
        if (waitingThread != null) {
            if (waitingThread.isAlive()) {
                waitingThread.interrupt();
                Logger.Logi(getClass(), "waiting thread interrupted !");
            }
            waitingThread = null;
        }
        isFragmentLostHisState = false;
    }

    @Override
    public void onStop() {
        super.onStop();
        Logger.Logi(getClass(), "onStop");
        isFragmentLostHisState = false;
    }

    @Override
    public void onStart() {
        super.onStart();
        Logger.Logi(getClass(), "onStart");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mRadioGroup != null) {
            int checkedId = mRadioGroup.getCheckedRadioButtonId();
            outState.putInt(CHEAKED_RADIO_BTN_SAVE, checkedId);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Logger.Logi(getClass(), "onDetach");
        handler.removeCallbacksAndMessages(null);
    }

    private Uri getPointInfoUri() {
        if (getActivity() != null)
            return HaccpContract.Points.buildPointUri(getActivity().getIntent().getStringExtra(
                    Intents.Point.UID));
        else
            throw new IllegalStateException(" fragment is not Attached to activity");
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle data) {

        if (id == PointQuery._TOKEN) {

            Logger.Logi(getClass(), "onCreateLoader : " + "point query");
            return new CursorLoader(
                    getActivity(),
                    getPointInfoUri(),
                    PointQuery.PROJECTION,
                    null,
                    null,
                    null);
        } else if (id == StatusesQuery._TOKEN) {

            Logger.Logi(getClass(), "onCreateLoader : " + "statuses query");
            return new CursorLoader(getActivity(),
                    HaccpContract.Statuses.CONTENT_URI,
                    StatusesQuery.PROJECTION,
                    null,
                    null,
                    null);
        }
        else if (id == GroupCharacteristicsQuery._TOKEN) {

            Logger.Logi(getClass(), "onCreateLoader : " + "characteristics query");
            return new CursorLoader(getActivity(),
                    HaccpContract.GroupCharacterisitcs.buildUriForGroup(data.getInt("group_id")),
                    GroupCharacteristicsQuery.PROJECTION,
                    null,
                    null,
                    null);
        }

        throw new IllegalArgumentException("unknown loader id: " + id);
    }

    @Override
    public void onLoadFinished(final Loader<Cursor> loader, final Cursor cursor) {
        switch (loader.getId()) {
            case PointQuery._TOKEN:
                onPointLoadFinished(cursor);
                break;
            case StatusesQuery._TOKEN:
                onStatusesLoadFinished(cursor);
                break;
            case GroupCharacteristicsQuery._TOKEN:
                onGroupCharacteristicsLoadFinished(cursor);
                break;
            default:
                throw new UnsupportedOperationException("unknown loader id: " + loader.getId());
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        // clear UI if needed

        switch (loader.getId()) {
            case PointQuery._TOKEN:
                Logger.Logi(getClass(), "onLoaderReset: point");
                break;
            case StatusesQuery._TOKEN:
                Logger.Logi(getClass(), "onLoaderReset: statuses");
                clearStatuses();
                break;
            case GroupCharacteristicsQuery._TOKEN:
                Logger.Logi(getClass(), "onLoaderReset: characteristics");
                clearCharacteristics();
                break;
            default:
                throw new UnsupportedOperationException("unknown loader id: " + loader.getId());
        }
    }

    private void clearStatuses() {
        if (getView() != null) {
            Logger.Logi(getClass(), "remove all views from statuses");
            RadioGroup mRadioGroup = (RadioGroup) getView().findViewById(
                    R.id.add_st_statuses_radio_group);
            removeAllViews(mRadioGroup);
        }
    }

    private void clearCharacteristics() {
        if (getView() != null) {
            Logger.Logi(getClass(), "remove all views from characteristics");
            LinearLayout mLinearLayout = (LinearLayout) getView().findViewById(
                    R.id.add_st_char_fields_holder);
            removeAllViews(mLinearLayout);
        }
    }

    private static void removeAllViews(LinearLayout layout) {
        if (((LinearLayout) layout).getChildCount() > 0)
            ((LinearLayout) layout).removeAllViews();
    }

    private void onPointLoadFinished(Cursor cursor) {

        Logger.Logi(getClass(), "onLoadFinished : " + "point query");

        cursor.moveToFirst();
        if (cursor.getCount() == 0)
            return;

        String pointName = cursor.getString(PointQuery.NAME); // (number)

        int groupId = cursor.getInt(PointQuery.GROUP_UID);

        // int statusId = cursor.getInt(PointQuery.STATUS_UID);

        if (isAdded()) {
            TextView number = (TextView) getView().findViewById(R.id.add_st_char_point_number);
            if (number != null) // vertical orientation case
                number.setText(pointName);
        }

        // this.mPointStatusId = statusId;
        // this.mPointGroupId = groupId;

        Bundle args = new Bundle();
        args.putInt("group_id", groupId);

        LoaderManager loaderManager = getLoaderManager();
        loaderManager
                .initLoader(GroupCharacteristicsQuery._TOKEN, args, AddStatisticsFragment.this);
        Logger.Logi(getClass(), "point countDown");

        allLoadsCompleted.countDown();

    }

    private void onStatusesLoadFinished(Cursor cursor) {

        Logger.Logi(getClass(), "onLoadFinished : " + "statuses");

        ArrayList<PointStatus> statuses = new ArrayList<PointStatus>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {

            int uid = cursor.getInt(StatusesQuery.UID);
            String slug = cursor.getString(StatusesQuery.SLUG);
            String name = cursor.getString(StatusesQuery.NAME);

            PointStatus.CODE code = null;
            try {
                code = CODE.fromString(slug);
            } catch (IllegalArgumentException wrongCodeSlugArgumentException) {
                wrongCodeSlugArgumentException.printStackTrace();
            }

            PointStatus status = new PointStatus(uid, name, code);
            statuses.add(status);
            cursor.moveToNext();
        }

        this.mStatuses = statuses;

        Logger.Logi(getClass(), "statuses countDown");

        allLoadsCompleted.countDown();

    }

    private void onGroupCharacteristicsLoadFinished(Cursor cursor) {

        Logger.Logi(getClass(), "onLoadFinished : " + "characteristics");
        ArrayList<GroupCharacteristicField> records = new ArrayList<GroupCharacteristicField>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {

            int uid = cursor.getInt(GroupCharacteristicsQuery.UID);
            String name = cursor.getString(GroupCharacteristicsQuery.NAME);
            String unit = cursor.getString(GroupCharacteristicsQuery.UNIT);

            int min = cursor.getInt(GroupCharacteristicsQuery.ALLOW_VALUE_MIN);
            int max = cursor.getInt(GroupCharacteristicsQuery.ALLOW_VALUE_MAX);

            int criticalValueBottom = cursor
                    .getInt(GroupCharacteristicsQuery.CRITICAL_VALUE_BOTTOM);
            int criticalValueTop = cursor.getInt(GroupCharacteristicsQuery.CRITICAL_VALUE_TOP);

            String dataTypeStr = cursor.getString(GroupCharacteristicsQuery.DATA_TYPE);
            String inputTypeStr = cursor.getString(GroupCharacteristicsQuery.INPUT_TYPE);

            DataType dataType = null;
            InputType inputType = null;
            try {
                dataType = DataType.fromString(dataTypeStr);
                inputType = InputType.fromString(inputTypeStr);
            } catch (IllegalArgumentException wrongArgumentException) {
                wrongArgumentException.printStackTrace();
            }

            GroupCharacteristic groupCharacteristics = new GroupCharacteristic(uid, name, unit,
                    null, max, min, criticalValueBottom, criticalValueTop);
            GroupCharacteristicField field = new GroupCharacteristicField(groupCharacteristics,
                    dataType, inputType);
            records.add(field);
            cursor.moveToNext();
        }

        this.mGroupCharacteristicsFields = records;

        Logger.Logi(getClass(), "characteristics countDown");
        allLoadsCompleted.countDown();

    }

    private void setStatusesRadioGroup(LayoutInflater inflater, ViewGroup container,
            ArrayList<PointStatus> statuses) {

        RadioGroup group = (RadioGroup) container.findViewById(R.id.add_st_statuses_radio_group);
        Iterator<PointStatus> iterator = statuses.iterator();

        while (iterator.hasNext()) {
            PointStatus status = iterator.next();
            View view = getRadioView(inflater, status);
            group.addView(view);
        }

        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == -1) {
                    releasAllCharacteristicsFields();
                }
                else {
                    blockAllCharacteristicsFields();
                }
            }
        });

        this.mRadioGroup = group;

    }

    private void blockAllCharacteristicsFields() {

        for (Map.Entry<GroupCharacteristicField, View> entry : viewContainersMap.entrySet()) {

            GroupCharacteristicField field = entry.getKey();
            View view = entry.getValue();
            InputType inputType = field.getInputType();

            switch (inputType) {
                case RANGE:

                    final TextView nameTV = (TextView) view.findViewById(R.id.add_st_char_name);
                    final SeekBar seekBar = (SeekBar) view.findViewById(R.id.add_st_char_seak_bar);

                    if (seekBar.isEnabled())
                        nameTV.setPaintFlags(nameTV.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    seekBar.setEnabled(false);

                    break;
            }
        }

    }

    private void releasAllCharacteristicsFields() {

        for (Map.Entry<GroupCharacteristicField, View> entry : viewContainersMap.entrySet()) {

            GroupCharacteristicField field = entry.getKey();
            View view = entry.getValue();
            InputType inputType = field.getInputType();

            switch (inputType) {
                case RANGE:

                    final TextView nameTV = (TextView) view.findViewById(R.id.add_st_char_name);
                    final SeekBar seekBar = (SeekBar) view.findViewById(R.id.add_st_char_seak_bar);

                    if (!seekBar.isEnabled())
                        nameTV.setPaintFlags(nameTV.getPaintFlags() ^ Paint.STRIKE_THRU_TEXT_FLAG);
                    seekBar.setEnabled(true);

                    break;
            }
        }
    }

    private void setCharacteristicsViews(LayoutInflater inflater, ViewGroup container,
            List<GroupCharacteristicField> characteristicFields) {
        LinearLayout holder = (LinearLayout) container.findViewById(R.id.add_st_char_fields_holder);

        viewValueContainersMap = new HashMap<GroupCharacteristicField, View>();
        viewContainersMap = new HashMap<GroupCharacteristicField, View>();

        Iterator<GroupCharacteristicField> iterator = characteristicFields.iterator();
        while (iterator.hasNext()) {

            GroupCharacteristicField field = iterator.next();
            View view = getFieldView(inflater, holder, field);
            holder.addView(view);
            viewContainersMap.put(field, view);
        }
    }

    private View getRadioView(LayoutInflater inflater, PointStatus status) {

        RadioButton button = new RadioButton(getActivity());
        @SuppressWarnings("deprecation")
        RadioGroup.LayoutParams lp = new RadioGroup.LayoutParams(
                RadioGroup.LayoutParams.FILL_PARENT, RadioGroup.LayoutParams.WRAP_CONTENT);
        button.setLayoutParams(lp);
        button.setId(status.getId());
        button.setText(status.getName());
        button.setTag(status);

        return button;
    }

    private View getFieldView(LayoutInflater inflater, ViewGroup container,
            GroupCharacteristicField field) {

        InputType inputType = field.getInputType();
        View view = null;
        switch (inputType) {
            case RANGE: {

                view = inflater.inflate(R.layout.list_item_range_add_statisctics, container, false);

                final TextView unitTV = (TextView) view.findViewById(R.id.add_st_char_unit);
                final TextView valueTV = (TextView) view.findViewById(R.id.add_st_char_value);
                final TextView nameTV = (TextView) view.findViewById(R.id.add_st_char_name);
                final SeekBar seekBar = (SeekBar) view.findViewById(R.id.add_st_char_seak_bar);

                final TextView minTV = (TextView) view.findViewById(R.id.add_st_char_min_value);
                final TextView maxTV = (TextView) view.findViewById(R.id.add_st_char_max_value);

                final View statusView = view.findViewById(R.id.add_st_char_status);

                if (field.getCharacteristic() != null) {

                    GroupCharacteristic characteristics = field.getCharacteristic();
                    unitTV.setText(characteristics.getUnit());
                    nameTV.setText(characteristics.getName() + ":");

                    final int minimum = characteristics.getMinValue();
                    final int maximum = characteristics.getMaxValue();

                    minTV.setText(Integer.toString(minimum));
                    maxTV.setText(Integer.toString(maximum));

                    final int bottom = characteristics.getCriticalBottomValue();
                    final int top = characteristics.getCriticalTopValue();

                    int defValue = (bottom + top) / 2;
                    valueTV.setText(Integer.toString(defValue));

                    StatististicsItemStatus status = AppUtils.getStatus(defValue, top, bottom);

                    AppUtils.setUpStatusViewColor(status, statusView);

                    // seekBar.setMax(maximum);
                    seekBar.setProgress(defValue);

                    seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                        @Override
                        public void onStopTrackingTouch(SeekBar seekBar) {
                        }

                        @Override
                        public void onStartTrackingTouch(SeekBar seekBar) {
                        }

                        @Override
                        public void onProgressChanged(SeekBar seekBar, int progress,
                                boolean fromUser) {

                            int value = getValue(seekBar, minimum, maximum);
                            valueTV.setText(Integer.toString(value));
                            AppUtils.setUpStatusViewColor(AppUtils.getStatus(value, top, bottom),
                                    statusView);

                        }
                    });

                }
                viewValueContainersMap.put(field, seekBar);

            }
                break;
        }
        if (view != null) {
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mRadioGroup.clearCheck();
                    releasAllCharacteristicsFields();
                }
            });
        }

        return view;

    }

    private int getValue(SeekBar seekBar, int min, int max) {

        int progress = seekBar.getProgress();
        double value = (double) (max - min) * ((double) progress / 100);
        int intVlue = Double.valueOf(value).intValue();
        return intVlue;
    }

    public HashMap<GroupCharacteristic, Double> getValues() {

        HashMap<GroupCharacteristic, Double> values = new HashMap<GroupCharacteristic, Double>();
        for (Map.Entry<GroupCharacteristicField, View> entry : viewValueContainersMap.entrySet()) {

            GroupCharacteristicField characteristicField = entry.getKey();
            View view = entry.getValue();
            InputType inputType = characteristicField.getInputType();

            Double value = 0.0;
            switch (inputType) {
                case RANGE:

                    int minimum = characteristicField.getCharacteristic().getMinValue();
                    int maximum = characteristicField.getCharacteristic().getMaxValue();
                    value = Double.valueOf(getValue((SeekBar) view, minimum, maximum));

                    break;
            }

            GroupCharacteristic characteristic = characteristicField.getCharacteristic();
            values.put(characteristic, value);
        }
        return values;
    }
}
