
package com.itdoors.haccp.utils;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

public final class ToastUtil {

    private ToastUtil() {
    }

    public static void ToastShort(Context context, String str) {
        Toast toast = Toast.makeText(context, str, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM, 0, 0);
        toast.show();
    }

    public static void ToastLong(Context context, String str) {
        Toast toast = Toast.makeText(context, str, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.BOTTOM, 0, 0);
        toast.show();
    }

}
