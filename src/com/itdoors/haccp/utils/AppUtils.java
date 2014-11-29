
package com.itdoors.haccp.utils;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.TextView;

import com.itdoors.haccp.R;
import com.itdoors.haccp.model.StatististicsItemStatus;

public final class AppUtils {

    private AppUtils() {
    }

    public static StatististicsItemStatus getStatus(double value, double top, double bottom) {
        return (value <= bottom) ? StatististicsItemStatus.APPROVED :
                ((value > bottom && value <= top) ? StatististicsItemStatus.WARNING
                        : StatististicsItemStatus.DANGER);
    }

    public static StatististicsItemStatus getStatus(String v, String t, String b) {
        try {
            return getStatus(Double.parseDouble(v), Double.parseDouble(t), Double.parseDouble(b));
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void setUpStatusViewColor(StatististicsItemStatus status, View view) {
        switch (status) {
            case WARNING:
                view.setBackgroundResource(R.color.status_warning);
                break;
            case DANGER:
                view.setBackgroundResource(R.color.status_danger);
                break;
            default:
                view.setBackgroundResource(R.color.status_approved);
                break;
        }

    }

    public static void setUpStatusView(StatististicsItemStatus status, TextView tv,
            Map<StatististicsItemStatus, String> statuses) {
        switch (status) {
            case WARNING:
                tv.setText(statuses.get(StatististicsItemStatus.WARNING));
                tv.setBackgroundResource(R.color.status_warning);
                break;
            case DANGER:
                tv.setText(statuses.get(StatististicsItemStatus.DANGER));
                tv.setBackgroundResource(R.color.status_danger);
                break;
            default:
                tv.setText(statuses.get(StatististicsItemStatus.APPROVED));
                tv.setBackgroundResource(R.color.status_approved);
                break;
        }
    }

    public static HashMap<StatististicsItemStatus, String> getStatusesMap(Context context) {

        HashMap<StatististicsItemStatus, String> map = new HashMap<StatististicsItemStatus, String>();
        map.put(StatististicsItemStatus.APPROVED,
                context.getString(R.string.cp_statistics_type_approved));
        map.put(StatististicsItemStatus.WARNING,
                context.getString(R.string.cp_statistics_type_warning));
        map.put(StatististicsItemStatus.DANGER,
                context.getString(R.string.cp_statistics_type_danger));

        return map;
    }

    public static void setScaledCompoundDrawable(Context context, int id, TextView text) {
        Drawable drawable = context.getResources().getDrawable(id);
        int iconSize = context.getResources().getDimensionPixelSize(R.dimen.slidingmenu_icons_size);
        int padding = context.getResources().getDimensionPixelSize(R.dimen.drawer_list_img_padding);
        text.setCompoundDrawablePadding(padding);
        text.setCompoundDrawables(ImageHelper.scaleDrawable(drawable, iconSize, iconSize), null,
                null, null);
    }
}
