package com.itdoors.haccp.ui.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class NotificationDialogFragment extends DialogFragment{
	
	public static NotificationDialogFragment newInstance(String title, String msg){
		
		NotificationDialogFragment fragment = new NotificationDialogFragment();
		
		Bundle args = new Bundle();
		args.putString("title", title);
		args.putString("msg", msg);
		fragment.setArguments(args);
		
		return fragment;
	}
	

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		return new AlertDialog.Builder(getActivity()).setTitle(getArguments().getString("title"))
			.setMessage(getArguments().getString("msg"))
			.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			})
			.create();
	}
}
