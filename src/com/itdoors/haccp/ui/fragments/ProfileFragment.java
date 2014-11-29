
package com.itdoors.haccp.ui.fragments;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.itdoors.haccp.R;
import com.itdoors.haccp.provider.HaccpContract;

public class ProfileFragment extends SherlockFragment implements
        LoaderManager.LoaderCallbacks<Cursor> {

    public interface OnLogoutPressedListener {
        public void onLogoutPressed();
    }

    private TextView nameView;
    private TextView emailView;
    private Button logOutBtn;

    private OnLogoutPressedListener mOnLogoutPressedListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mOnLogoutPressedListener = (OnLogoutPressedListener) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        nameView = (TextView) rootView.findViewById(R.id.user_name);
        emailView = (TextView) rootView.findViewById(R.id.user_email);
        logOutBtn = (Button) rootView.findViewById(R.id.logout_btn);
        logOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnLogoutPressedListener != null)
                    mOnLogoutPressedListener.onLogoutPressed();
            }
        });
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(getActivity(), // Context
                HaccpContract.User.CONTENT_URI, // URI
                UserQuery.PROJECTION, // Projection
                null, // Selection
                null, // Selection args
                null); // Sort
    }

    private void buildUiFrom(Cursor cursor) {
        if (getActivity() == null)
            return;
        cursor.moveToFirst();
        if (cursor.getColumnCount() == 0)
            return;
        String name = cursor.getString(UserQuery.NAME);
        String email = cursor.getString(UserQuery.EMAIL);
        nameView.setText(getString(R.string.login) + ":" + name);
        emailView.setText(getString(R.string.email) + ":" + email);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        buildUiFrom(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mOnLogoutPressedListener = null;
    }

    @SuppressWarnings("unused")
    private interface UserQuery {

        String[] PROJECTION = new String[] {

                HaccpContract.User.NAME,
                HaccpContract.User.EMAIL
        };

        int NAME = 0;
        int EMAIL = 1;

    }
}
