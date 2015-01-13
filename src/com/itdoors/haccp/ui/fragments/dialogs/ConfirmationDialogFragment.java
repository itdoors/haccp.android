
package com.itdoors.haccp.ui.fragments.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public abstract class ConfirmationDialogFragment extends DialogFragment {

    public static Bundle prepareArguments(String msg, String title, String cofirmMsg,
            String cancelMsg) {
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("msg", msg);
        args.putString("ok", cofirmMsg);
        args.putString("cancel", cancelMsg);
        return args;
    }

    protected abstract void onConfirm();

    protected abstract void onCancel();

    @Override
    public Dialog onCreateDialog(Bundle savedInstance) {

        return new AlertDialog.Builder(getActivity())
                .setTitle(getArguments().getString("title"))
                .setMessage(getArguments().getString("msg"))
                .setPositiveButton(getArguments().getString("ok"),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ConfirmationDialogFragment.this.onConfirm();
                                dialog.dismiss();
                            }
                        })
                .setNegativeButton(getArguments().getString("cancel"),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ConfirmationDialogFragment.this.onCancel();
                                dialog.dismiss();
                            }
                        })
                .create();
    }

}
