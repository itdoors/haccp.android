
package com.itdoors.haccp.ui.fragments.dialogs;

import java.util.List;

import com.itdoors.haccp.events.ConfirmZeroToOthersPointsInPlanEvent;
import com.itdoors.haccp.model.Point;

import de.greenrobot.event.EventBus;

public class ConfirmZeroToOtherPointsDialog extends ConfirmationDialogFragment {

    public static String POINTS_TAG = "com.itdoors.haccp.dialogs.ConfirmZeroToOtherPointsDialog.POINTS_TAG";

    @SuppressWarnings("unchecked")
    @Override
    protected void onConfirm() {
        EventBus.getDefault().post(
                new ConfirmZeroToOthersPointsInPlanEvent(
                        (List<Point>) getArguments()
                                .getSerializable(POINTS_TAG)));
    }

    @Override
    protected void onCancel() {

    }

}
