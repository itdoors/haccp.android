
package com.itdoors.haccp.ui.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.itdoors.haccp.R;

public class LoginFragment extends Fragment {

    public interface OnLoginClickListener {
        public void onLoginClick(String email, String password);
    }

    private OnLoginClickListener mOnLoginClickListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mOnLoginClickListener = (OnLoginClickListener) activity;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        View layout = inflater.inflate(R.layout.fragment_login, container, false);

        final EditText loginView = (EditText) layout.findViewById(R.id.user_login);
        final EditText passwordView = (EditText) layout.findViewById(R.id.user_pass);

        passwordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    String login = loginView.getText().toString();
                    String password = passwordView.getText().toString();
                    if (mOnLoginClickListener != null)
                        mOnLoginClickListener.onLoginClick(login, password);
                    handled = true;
                }
                return handled;
            }
        });
        final Button login = (Button) layout.findViewById(R.id.login_bnt);
        login.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String login = loginView.getText().toString();
                String password = passwordView.getText().toString();
                if (mOnLoginClickListener != null)
                    mOnLoginClickListener.onLoginClick(login, password);

            }

        });

        return layout;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mOnLoginClickListener = null;
    }
}
