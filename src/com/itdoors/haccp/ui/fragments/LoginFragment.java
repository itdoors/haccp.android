
package com.itdoors.haccp.ui.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.itdoors.haccp.R;
import com.itdoors.haccp.ui.activities.LoginActivity;

import de.greenrobot.event.EventBus;

public class LoginFragment extends Fragment {

    public interface OnLoginClickListener {
        public void onLoginClick(String email, String password);
    }

    private final static String DISABLED_LOGIN_TAG = "LoginFragment.DISABLED_LOGIN_TAG";

    private Button loginBtn;
    private EditText passwordView;

    private OnLoginClickListener mOnLoginClickListener;

    private boolean disabledLogin = false;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mOnLoginClickListener = (OnLoginClickListener) activity;
        EventBus.getDefault().register(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(DISABLED_LOGIN_TAG, disabledLogin);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        View layout = inflater.inflate(R.layout.fragment_login, container, false);

        final EditText loginView = (EditText) layout.findViewById(R.id.user_login);

        if (savedInstanceState != null)
            disabledLogin = savedInstanceState.getBoolean(DISABLED_LOGIN_TAG);

        passwordView = (EditText) layout.findViewById(R.id.user_pass);
        passwordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (!disabledLogin && actionId == EditorInfo.IME_ACTION_DONE) {
                    String login = loginView.getText().toString();
                    String password = passwordView.getText().toString();
                    if (mOnLoginClickListener != null)
                        mOnLoginClickListener.onLoginClick(login, password);
                    handled = true;
                }
                return handled;
            }
        });

        loginBtn = (Button) layout.findViewById(R.id.login_bnt);
        loginBtn.setEnabled(!disabledLogin);

        loginBtn.setOnClickListener(new View.OnClickListener() {

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

    private static void hideKeyboard(Context context, EditText editText) {

        InputMethodManager imm = (InputMethodManager) context.getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    public void onEventMainThread(LoginActivity.LoginInitEvent event) {
        disabledLogin = true;
        loginBtn.setEnabled(false);

        hideKeyboard(getActivity().getApplicationContext(), passwordView);
    }

    public void onEventMainThread(LoginActivity.FailureLoginEvent event) {
        disabledLogin = false;
        loginBtn.setEnabled(true);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mOnLoginClickListener = null;
        EventBus.getDefault().unregister(this);

    }
}
