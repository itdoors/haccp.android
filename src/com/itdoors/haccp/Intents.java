
package com.itdoors.haccp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

public final class Intents {

    public static final class CalendarTimeRange {

        public static final String FROM_TIME_STAMP = "com.itdoors.haccp.Intents.CalendarTimeRange.FROM_TIME_STAMP";
        public static final String TO_TIME_STAMP = "com.itdoors.haccp.Intents.CalendarTimeRange.TO_TIME_STAMP";
    }

    public static final class Statistic {
        public static final String STATISTIC_RECORD = "com.itdoors.haccp.Intents.Statistic.STATISTIC_RECORD";

    }

    public static final class Status {
        public static final String CHANGED_STATUS = "com.itdoors.haccp.Intents.Statistic.STATUS_RECORD";
    }

    public static final class SyncComplete {

        public static final String ACTION_FINISHED_SYNC = "com.itdoors.haccp.action.ACTION_FINISHED_SYNC";
        public static final String LOCAL_SYNC_SUCCESFULLY = "com.itdoors.haccp.Intents.LOCAL_SYNC_COMPELTED_SUCCESFULLY";

    }

    public static final class Company {
        public static final String UID = "com.itdoors.haccp.Intents.Company.UID";
        public static final String COMPANY = "com.itdoors.haccp.Intents.Company.COMPANY";
    }

    public static final class CompanyObject {
        public static final String UID = "com.itdoors.haccp.Intents.CompanyObject.UID";
        public static final String COMPANY_OBJECT = "com.itdoors.haccp.Intents.CompanyObject.COMPANY_OBJECT";
    }

    public static final class Contour {
        public static final String UID = "com.itdoors.haccp.Intents.Contour.UID";
        public static final String CONTOUR = "com.itdoors.haccp.Intents.Contour.CONTOUR";

    }

    public static final class Point {
        public static final String UID = "com.itdoors.haccp.Intents.Point.UID";
        public static final String POINT = "com.itdoors.haccp.Intents.Point.POINT";

    }

    public static final class Plan {
        public static final String UID = "com.itdoors.haccp.Intents.Plan.UID";
        public static final String PLAN = "com.itdoors.haccp.Intents.Plan.PLAN";
    }

    /**
     * Converts an intent into a {@link Bundle} suitable for use as fragment
     * arguments.
     */
    public static Bundle intentToFragmentArguments(Intent intent) {
        Bundle arguments = new Bundle();
        if (intent == null) {
            return arguments;
        }

        final Uri data = intent.getData();
        if (data != null) {
            arguments.putParcelable("_uri", data);
        }

        final Bundle extras = intent.getExtras();
        if (extras != null) {
            arguments.putAll(intent.getExtras());
        }

        return arguments;
    }

    /**
     * Converts a fragment arguments bundle into an intent.
     */
    public static Intent fragmentArgumentsToIntent(Bundle arguments) {
        Intent intent = new Intent();
        if (arguments == null) {
            return intent;
        }

        final Uri data = arguments.getParcelable("_uri");
        if (data != null) {
            intent.setData(data);
        }

        intent.putExtras(arguments);
        intent.removeExtra("_uri");
        return intent;
    }

}
