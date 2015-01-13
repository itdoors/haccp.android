
package com.itdoors.haccp.ui.fragments.dialogs;

import com.itdoors.haccp.events.ConfirmZeroToAllPointInPlanEvent;

import de.greenrobot.event.EventBus;

public class ConfirmZeroToAllPointsDialog extends ConfirmationDialogFragment {

    @Override
    protected void onConfirm() {
        EventBus.getDefault().post(new ConfirmZeroToAllPointInPlanEvent());
    }

    @Override
    protected void onCancel() {

    }

}
