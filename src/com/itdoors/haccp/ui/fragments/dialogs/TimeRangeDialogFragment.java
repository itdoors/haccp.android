
package com.itdoors.haccp.ui.fragments.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.itdoors.haccp.R;
import com.itdoors.haccp.ui.interfaces.OnTimeRangeChooseListener;

public class TimeRangeDialogFragment extends DialogFragment {

    private OnTimeRangeChooseListener mOnTimeRangeChooseListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mOnTimeRangeChooseListener = (OnTimeRangeChooseListener) activity;

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        String[] items = getResources().getStringArray(R.array.choos_time_range);

        return new AlertDialog.Builder(getActivity()).setTitle(R.string.choose_range)
                .setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mOnTimeRangeChooseListener.onTimeRagneClicked(which);
                    }
                }).create();

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mOnTimeRangeChooseListener = null;
    }
}
