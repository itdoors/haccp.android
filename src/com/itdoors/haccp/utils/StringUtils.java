
package com.itdoors.haccp.utils;

import android.util.SparseBooleanArray;

public class StringUtils {

    public static String toString(SparseBooleanArray array) {

        if (array == null)
            return null;
        if (array.size() == 0)
            return new String("empty");

        StringBuilder sb = new StringBuilder();
        sb.append("size: ").append(array.size()).append(";")
                .append("data:").append("[");
        for (int index = 0; index < array.size(); index++) {
            sb.append(index + ": " + "(" + array.keyAt(index) + "," + array.valueAt(index) + ")");
            if (index != array.size() - 1)
                sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }

}
