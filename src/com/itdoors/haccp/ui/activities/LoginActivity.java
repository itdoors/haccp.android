
package com.itdoors.haccp.ui.activities;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.content.AsyncQueryHandler;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.itdoors.haccp.Global;
import com.itdoors.haccp.R;
import com.itdoors.haccp.oauth.AccessToken;
import com.itdoors.haccp.oauth.HaccpOAuthService;
import com.itdoors.haccp.oauth.HaccpOAuthServiceApi;
import com.itdoors.haccp.oauth.HaccpOAuthServiceApi.User;
import com.itdoors.haccp.provider.HaccpContract;
import com.itdoors.haccp.sync.SyncUtils;
import com.itdoors.haccp.ui.fragments.LoginFragment;
import com.itdoors.haccp.utils.Enviroment;
import com.itdoors.haccp.utils.Logger;
import com.itdoors.haccp.utils.ToastUtil;

import de.greenrobot.event.EventBus;

public class LoginActivity extends BaseSherlockFragmentActivity implements
        LoginFragment.OnLoginClickListener {

    private Intent mFinishIntent;

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        final Intent intent = getIntent();
        if (intent.hasExtra(SyncUtils.FINISH_INTENT_EXTRA)) {
            mFinishIntent = intent.getParcelableExtra(SyncUtils.FINISH_INTENT_EXTRA);
        }

    }

    @Override
    public void initFragment() {
        Fragment fragment = new LoginFragment();
        setContentFragment(fragment);
    }

    @Override
    public void onLoginClick(String login, String password) {

        if (Enviroment.isNetworkAvaliable(getApplicationContext())) {
            if (login != null && !login.equals("") && password != null && !password.equals("")) {
                HaccpOAuthService.getService().getAccessToken(Global.CLIENT_ID,
                        Global.CLIENT_SECRET,
                        Global.GRAND_TYPE_PASSWORD, login, password, mLoginCallback);
            }
            else {
                ToastUtil.ToastShort(getApplicationContext(), getString(R.string.fill_the_fields));
            }
        }
        else {
            ToastUtil.ToastShort(getApplicationContext(),
                    getString(R.string.not_avalieble_without_any_interent_connection));
        }
    }

    private static Callback<AccessToken> mLoginCallback = new Callback<AccessToken>() {

        @Override
        public void success(final AccessToken token, final Response response) {

            HaccpOAuthService.getService().getUser(
                    token.getToken(),
                    new Callback<HaccpOAuthServiceApi.User>() {
                        @Override
                        public void success(HaccpOAuthServiceApi.User user, Response response) {
                            Logger.Logd(getClass(),
                                    "user:" + (user == null ? "null" : user.toString()));
                            EventBus.getDefault().postSticky(new LoginEvent(token, user));
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            Logger.Logd(getClass(), "error:" + error.toString());
                            EventBus.getDefault().postSticky(new FailedLoginEvent());
                        }
                    }
                    );
        }

        @Override
        public void failure(RetrofitError error) {
            EventBus.getDefault().postSticky(new FailedLoginEvent());
        }
    };

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    private static class LoginEvent {

        private final AccessToken token;
        private final User user;

        public LoginEvent(AccessToken token, User user) {
            this.token = token;
            this.user = user;
        }

        public AccessToken getAccessToken() {
            return token;
        }

        public User getUser() {
            return user;
        }
    }

    private static class FailedLoginEvent {

    }

    private void addUser(final User user, final AccessToken accessToken) {

        ContentValues values = new ContentValues();
        values.put(HaccpContract.User.NAME, user.getName());
        values.put(HaccpContract.User.EMAIL, user.getEmail());

        Logger.Logd(getClass(), "user:" + user.toString() + " token:" + accessToken.getToken());

        AsyncQueryHandler handler = new AsyncQueryHandler(getContentResolver()) {

            @Override
            protected void onInsertComplete(int token, Object cookie, Uri uri) {
                Logger.Logd(LoginActivity.class, "User added");
                if (accessToken != null) {
                    SyncUtils.logIn(getApplicationContext(), accessToken, mFinishIntent);
                    finish();
                }

            }
        };
        handler.startInsert(0, null, HaccpContract.User.CONTENT_URI, values);
    }

    public void onEventMainThread(LoginEvent event) {

        AccessToken accessToken = event.getAccessToken();
        User user = event.getUser();
        if (user != null && accessToken != null) {
            addUser(user, accessToken);
        }

    }

    public void onEventMainThread(FailedLoginEvent event) {
        ToastUtil.ToastLong(getApplicationContext(), getString(R.string.failed_to_log_in));
    }

}
