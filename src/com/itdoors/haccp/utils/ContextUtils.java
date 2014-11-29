
package com.itdoors.haccp.utils;

import android.content.Context;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.FrameLayout;
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

    public static FrameLayout wrapListFragment(FrameLayout frameLayout) {
        Context context = frameLayout.getContext();
        ListView list = (ListView) frameLayout.findViewById(android.R.id.list);
        boolean isTablet = Enviroment.isTablet(context);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(list.getLayoutParams());
        if (isTablet) {
            lp.width = context.getResources().getDimensionPixelSize(R.dimen.list_widht);
            lp.gravity = Gravity.CENTER_HORIZONTAL;
            list.setLayoutParams(lp);
            list.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
        }
        return frameLayout;
    }
}
