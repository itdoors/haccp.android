
package com.itdoors.haccp.ui.activities;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.content.AsyncQueryHandler;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.itdoors.haccp.Config;
import com.itdoors.haccp.R;
import com.itdoors.haccp.analytics.Analytics;
import com.itdoors.haccp.analytics.Analytics.Action;
import com.itdoors.haccp.analytics.Analytics.Category;
import com.itdoors.haccp.analytics.TrackerName;
import com.itdoors.haccp.oauth.AccessToken;
import com.itdoors.haccp.oauth.HaccpOAuthService;
import com.itdoors.haccp.oauth.HaccpOAuthServiceApi;
import com.itdoors.haccp.oauth.HaccpOAuthServiceApi.User;
import com.itdoors.haccp.oauth.OAuthError;
import com.itdoors.haccp.oauth.OAuthErrorType;
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

                HaccpOAuthService.getService().getAccessToken(Config.CLIENT_ID,
                        Config.CLIENT_SECRET,
                        Config.GRAND_TYPE_PASSWORD, login, password, mLoginCallback);
                showProgress();
                EventBus.getDefault().postSticky(new LoginInitEvent());

                Analytics.getInstance(this).sendEvent(TrackerName.APP_TRACKER, Category.Login,
                        Action.Login);

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
                            EventBus.getDefault().postSticky(new SuccessLoginEvent(token, user));
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            EventBus.getDefault().postSticky(new FailureLoginEvent());
                        }
                    }
                    );
        }

        @Override
        public void failure(RetrofitError error) {

            if (error.getResponse() != null) {
                OAuthError oauthError = (OAuthError) error.getBodyAs(OAuthError.class);
                if (oauthError != null) {
                    EventBus.getDefault().postSticky(new FailureLoginEvent(oauthError));
                }
                else {
                    EventBus.getDefault().postSticky(new FailureLoginEvent());
                }
            }
            else {
                EventBus.getDefault().postSticky(new FailureLoginEvent());
            }
        }

    };

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    public static class LoginInitEvent {

    }

    private static class SuccessLoginEvent {

        private final AccessToken token;
        private final User user;

        public SuccessLoginEvent(AccessToken token, User user) {
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

    public static class FailureLoginEvent {
        private final OAuthError restError;

        public FailureLoginEvent() {
            this.restError = null;
        }

        public FailureLoginEvent(OAuthError error) {
            this.restError = error;
        }

        public OAuthError getRestError() {
            return restError;
        }
    }

    public static class UserAddedEvent {

        private final AccessToken token;

        public UserAddedEvent(AccessToken token) {
            this.token = token;
        }

        public AccessToken getAccessToken() {
            return token;
        }
    }

    private static void addUser(Context context, final User user, final AccessToken accessToken) {

        ContentValues values = new ContentValues();
        values.put(HaccpContract.User.NAME, user.getName());
        values.put(HaccpContract.User.EMAIL, user.getEmail());
        values.put(HaccpContract.User.BIG_AVATAR, user.getBigAvatar());
        values.put(HaccpContract.User.SMALL_AVATAR, user.getSmallAvatar());

        AsyncQueryHandler handler = new AsyncQueryHandler(context.getContentResolver()) {
            @Override
            protected void onInsertComplete(int token, Object cookie, Uri uri) {
                EventBus.getDefault().postSticky(new UserAddedEvent(accessToken));
            }
        };
        handler.startInsert(0, null, HaccpContract.User.CONTENT_URI, values);

    }

    public void onEventMainThread(SuccessLoginEvent event) {

        AccessToken accessToken = event.getAccessToken();
        User user = event.getUser();
        if (user != null && accessToken != null) {
            addUser(getApplicationContext(), user, accessToken);
        }
        hideProgress();
    }

    public void onEventMainThread(FailureLoginEvent event) {

        OAuthError error = event.getRestError();
        if (error != null && error.getType() == OAuthErrorType.INVALID_GRANT) {
            ToastUtil.ToastLong(getApplicationContext(),
                    getString(R.string.failed_to_log_in_wrong_data));
        }
        else {

            ToastUtil.ToastLong(getApplicationContext(),
                    getString(R.string.failed_to_log_in_unknown_error));

        }
        hideProgress();
    }

    public void onEventMainThread(UserAddedEvent event) {

        Logger.Logd(getClass(), "UserAddedEvent");

        SyncUtils.logIn(getApplicationContext(), event.getAccessToken(), mFinishIntent);
        finish();
    }

}
