
package com.itdoors.haccp.ui.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.actionbarsherlock.app.SherlockFragment;
import com.itdoors.haccp.R;
import com.itdoors.haccp.ui.activities.InitActivity;
import com.itdoors.haccp.utils.ProgressWheel;

import de.greenrobot.event.EventBus;

public class InitFragment extends SherlockFragment {

    private ProgressWheel progressView;
    private Button retryBtn;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater
                .inflate(R.layout.fragment_material_progress_init, container, false);
        progressView = (ProgressWheel) rootView.findViewById(R.id.progress_wheel);
        retryBtn = (Button) rootView.findViewById(R.id.retry_btn);
        retryBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                retryBtn.setVisibility(View.GONE);
                progressView.setVisibility(View.VISIBLE);

                EventBus.getDefault().postSticky(new InitActivity.RetryInitSyncEvent());
            }
        });
        return rootView;
    }

    public void onEventMainThread(InitActivity.InitSyncFailedEvent event) {

        progressView.setVisibility(View.GONE);
        retryBtn.setVisibility(View.VISIBLE);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        EventBus.getDefault().unregister(this);
    }

}
