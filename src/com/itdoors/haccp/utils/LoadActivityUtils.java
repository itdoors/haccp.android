
package com.itdoors.haccp.utils;

import java.util.concurrent.atomic.AtomicInteger;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.itdoors.haccp.R;

public final class LoadActivityUtils {

    private LoadActivityUtils() {
    }

    private static final AtomicInteger sNextLoadingGeneratedId = new AtomicInteger(1);
    private static final AtomicInteger sNextErrorGeneratedId = new AtomicInteger(2);
    private static final AtomicInteger sNextEmptyGeneratedId = new AtomicInteger(3);

    private static final int loading_id = LoadActivityUtils.generateViewId(sNextLoadingGeneratedId);
    private static final int error_id = LoadActivityUtils.generateViewId(sNextErrorGeneratedId);
    private static final int empty_id = LoadActivityUtils.generateViewId(sNextEmptyGeneratedId);

    /**
     * Generate a value suitable for use in {@link #setId(int)}. This value will
     * not collide with ID values generated at build time by aapt for R.id.
     * 
     * @return a generated ID value
     */
    public static int generateViewId(AtomicInteger sNextGeneratedId) {
        for (;;) {
            final int result = sNextGeneratedId.get();
            // aapt-generated IDs have the high byte nonzero; clamp to the range
            // under that.
            int newValue = result + 1;
            if (newValue > 0x00FFFFFF)
                newValue = 1; // Roll over to 1, not 0.
            if (sNextGeneratedId.compareAndSet(result, newValue)) {
                return result;
            }
        }
    }

    public static void removeLoadingView(Activity activity) {

        ViewGroup view = (ViewGroup) getView(activity);
        view.removeView(view.findViewById(loading_id));

    }

    public static void removeErrorView(Activity activity) {

        ViewGroup view = (ViewGroup) getView(activity);
        view.removeView(view.findViewById(error_id));
    }

    public static void addLoadingView(Activity activity) {

        View loading = getLoadingView(activity);
        ((ViewGroup) getView(activity)).addView(loading);

    }

    public static void addLoadingView(Activity activity, int stringResources) {

        View loading = getLoadingView(activity);
        ((TextView) loading.findViewById(R.id.loading_tv)).setText(stringResources);
        ((ViewGroup) getView(activity)).addView(loading);

    }

    public static void addErrorView(Activity activity, View.OnClickListener retry) {

        View error = getErrorView(activity, retry);
        ((ViewGroup) getView(activity)).addView(error);

    }

    public static View getView(Activity activity) {
        return activity.findViewById(android.R.id.content);
    }

    public static View getLoadingView(Activity activity) {
        View loading = activity.getLayoutInflater().inflate(R.layout.loading, null);
        loading.setId(loading_id);
        return loading;
    }

    private static View getErrorView(Activity activity, View.OnClickListener retryListener) {
        View root = activity.getLayoutInflater().inflate(R.layout.failed, null);
        Button retryBtn = (Button) root.findViewById(R.id.retry_load_btn);
        retryBtn.setOnClickListener(retryListener);
        root.setId(error_id);
        return root;
    }

    private static View getEmptyListView(Context context, String message) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.empty_list, null);
        TextView messageTV = (TextView) view.findViewById(R.id.list_emty_message);
        messageTV.setText(message);
        return view;

    }

    public static void addEmptyViewIfNotExist(Fragment fragment, String message) {

        if (fragment.getActivity() != null) {

            ViewGroup fragmentView = (ViewGroup) fragment.getView();
            if (fragmentView != null) {

                View emptyView = fragmentView.findViewById(empty_id);

                if (emptyView == null) {

                    emptyView = getEmptyListView(fragment.getActivity(), message);
                    emptyView.setId(empty_id);
                    fragmentView.addView(emptyView);
                }

            }
        }
    }

    public static void removeEmptyViewIfExist(Fragment fragment) {
        ViewGroup fragmentView = (ViewGroup) fragment.getView();
        if (fragmentView != null) {
            View emptyView = fragmentView.findViewById(empty_id);
            if (emptyView != null)
                fragmentView.removeView(emptyView);
        }
    }
}
