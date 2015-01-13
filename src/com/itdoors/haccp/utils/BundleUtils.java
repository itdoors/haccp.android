
package com.itdoors.haccp.utils;

import android.os.Bundle;

public final class BundleUtils {

    private static final String empty = "#";

    private BundleUtils() {
    }

    public static String serialize(Bundle bundle) {

        StringBuilder sb = new StringBuilder();
        sb.append('{');

        int size = bundle.size();

        int count = 0;
        for (String key : bundle.keySet()) {

            String value = bundle.getString(key);
            if (value != null) {
                sb.append(key).append(':').append(value.isEmpty() ? empty : value);
                if (count < size - 1)
                    sb.append(';');
            }
            count++;
        }
        sb.append('}');
        return sb.toString();
    }

    public static Bundle deserialize(String string) {
        Bundle bundle = new Bundle();
        string = string.substring(1, string.length() - 1);
        String pairs[] = string.split(";");
        for (String pair : pairs) {

            String keyValue[] = pair.split(":");

            String key = keyValue[0];
            String value = keyValue[1];

            if (value.equals(empty))
                value = "";

            bundle.putString(key, value);
        }
        return bundle;
    }

}
