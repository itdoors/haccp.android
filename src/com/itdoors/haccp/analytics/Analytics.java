
package com.itdoors.haccp.analytics;

import java.util.HashMap;

import android.content.Context;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Logger.LogLevel;
import com.google.android.gms.analytics.Tracker;
import com.itdoors.haccp.Config;
import com.itdoors.haccp.R;
import com.itdoors.haccp.utils.Logger;

public final class Analytics {

    private static final String TAG = Analytics.class.getSimpleName();

    private static Analytics sInstance;
    private final Context mContext;

    private HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();

    public synchronized static Analytics getInstance(Context context) {
        if (sInstance == null)
            sInstance = new Analytics(context);
        return sInstance;
    }

    private Analytics(Context context) {

        mContext = context.getApplicationContext();
        setup();

    }

    private synchronized Tracker getTracker(TrackerName trackerName) {

        if (!mTrackers.containsKey(trackerName)) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(mContext);
            if (trackerName == TrackerName.APP_TRACKER) {
                Tracker tracker = analytics.newTracker(R.xml.global_tracker);
                mTrackers.put(trackerName, tracker);
            }
        }

        return mTrackers.get(trackerName);
    }

    private void setup() {

        if (Config.debugAnalytics) {

            GoogleAnalytics analytics = GoogleAnalytics.getInstance(mContext);
            analytics.getLogger().setLogLevel(LogLevel.VERBOSE);
            analytics.setDryRun(true);
        }

    }

    private static synchronized void sendEvent(Tracker tracker, String category, String action,
            String label,
            long value) {

        tracker.send(
                new HitBuilders.EventBuilder()
                        .setCategory(category)
                        .setAction(action)
                        .setLabel(label)
                        .setValue(value)
                        .build());
        if (Config.debugAnalytics) {
            Logger.Logv(TAG, "GA: sending event\n\tcategory = '" + category + "'" + "\n\taction ='"
                    + action + "'" + "\n\tlabel ='" + label + "'" + "\n\tvalue = " + value);
        }
    }

    public synchronized void sendEvent(TrackerName name, Category category, Action action,
            String label, long value) {
        sendEvent(getTracker(name), category.name(), action.name(), label, value);
    }

    public synchronized void sendEvent(TrackerName name, Category category, Action action,
            String label) {
        sendEvent(name, category, action, label, 0L);
    }

    public synchronized void sendEvent(TrackerName name, Category category, Action action) {
        sendEvent(name, category, action, null, 0L);
    }

    private static synchronized void sendException(Tracker tracker, String description,
            boolean fatal) {

        tracker.send(
                new HitBuilders.ExceptionBuilder()
                        .setDescription(description)
                        .setFatal(fatal)
                        .build()
                );

        if (Config.debugAnalytics) {
            Logger.Logv(TAG, "GA: sending exception \n\tdescrition = '" + description + "'"
                    + "\n\fatal ='" + fatal);
        }
    }

    private static synchronized void sendException(Tracker tracker, String description) {
        sendException(tracker, description, false);
    }

    public synchronized void sendException(TrackerName trackerName, String description) {
        sendException(getTracker(trackerName), description);
    }

    public enum Category {
        Click,
        Login
    }

    public enum Action {
        Login,
        Logout
    }

    public enum View {
        Login
    }
}
