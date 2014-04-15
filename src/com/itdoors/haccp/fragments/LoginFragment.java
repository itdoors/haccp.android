package com.itdoors.haccp.fragments;


import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.itdoors.haccp.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class LoginFragment extends Fragment{

	private Button submit;
	
	@SuppressWarnings("unused")
	private EditText email;
	
	@SuppressWarnings("unused")
	private EditText password;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.login_frame, null);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		submit = (Button)view.findViewById(R.id.login_bnt);
		email = (EditText)view.findViewById(R.id.user_email);
		password = (EditText)view.findViewById(R.id.user_pass);
		
		submit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//login();
			}
		});
	}
	
	
	
	@SuppressWarnings("unused")
	private void showProgress(){
		if(getActivity() != null)
			((SherlockFragmentActivity)getActivity())
				.setSupportProgressBarIndeterminateVisibility(true);
	}
	
	@SuppressWarnings("unused")
	private void hideProgress(){
		if(getActivity() != null)
			((SherlockFragmentActivity)getActivity())
				.setSupportProgressBarIndeterminateVisibility(false);
	}
	
	
	
	
	
}
