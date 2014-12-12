
package com.itdoors.haccp.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.itdoors.haccp.R;

public final class ContextUtils {
    private ContextUtils() {
    }

    public static View getLoadingView(Context context, ViewGroup parent) {
        return ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.loading, parent, false);
    }

    public static View getErrorWhileConnectionView(Context context, ViewGroup parent,
            View.OnClickListener retryListener) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View root = inflater.inflate(R.layout.failed, parent, false);
        Button retryBtn = (Button) root.findViewById(R.id.retry_load_btn);
        retryBtn.setOnClickListener(retryListener);
        return root;
    }

    public static View getEmptyListView(Context context) {

        TextView textView = new TextView(context);
        textView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        textView.setTextColor(context.getResources().getColor(android.R.color.black));
        textView.setText(context.getString(R.string.loading));
        textView.setGravity(Gravity.CENTER);
        return textView;
    }

    public static void wrapListView(ListView listView) {

        Context context = listView.getContext();

        if (Enviroment.isTablet(context)
                && Configuration.ORIENTATION_LANDSCAPE == orientation(context)) {

            DisplayMetrics metrics = getMetrics(context);
            int padding = (metrics.widthPixels - metrics.heightPixels) / 2;

            listView.setPadding(padding, 0, padding, 0);
        }
    }

    public static void wrapContentView(View contentView) {

        if (contentView != null) {
            Context context = contentView.getContext();
            if (Enviroment.isTablet(context)) {
                DisplayMetrics metrics = getMetrics(context);
                int padding;
                if (Configuration.ORIENTATION_LANDSCAPE == orientation(context)) {
                    padding = (metrics.widthPixels - metrics.heightPixels) / 2;
                    contentView.setPadding(padding, 0, padding, 0);
                }
                else {
                    int dimenPix = (int) context.getResources().getDimension(
                            R.dimen.add_statistics_width);
                    padding = (metrics.widthPixels - dimenPix) / 2;
                    contentView.setPadding(padding, 0, padding, 0);

                }
            }
        }
    }

    public static int orientation(Context context) {
        return context.getResources().getConfiguration().orientation;

    }

    public static DisplayMetrics getMetrics(Context context) {

        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metrics);
        return metrics;

    }
}
