package lv.bestan.android.wear.expensestracker;

import android.app.Application;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.HashMap;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by Stan on 27/07/2014.
 */
public class ExpensesApplication extends Application {

    public enum TrackerName {
        APP_TRACKER, // Tracker used only in this app.
        GLOBAL_TRACKER, // Tracker used by all the apps from a company. eg: roll-up tracking.
        ECOMMERCE_TRACKER, // Tracker used by all ecommerce transactions from a company.
    }

    private static ExpensesApplication instance;
    public static ExpensesApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        CalligraphyConfig.initDefault("campton_light.otf", R.attr.fontPath);
        instance = this;
    }

    public void sendScreenView(String screen) {
        Tracker t = getTracker(ExpensesApplication.TrackerName.APP_TRACKER);
        t.setScreenName(screen);
        t.send(new HitBuilders.AppViewBuilder().build());
    }

    HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();
    synchronized Tracker getTracker(TrackerName trackerId) {
        if (!mTrackers.containsKey(trackerId)) {

            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            Tracker t = analytics.newTracker(R.xml.global_tracker);
            mTrackers.put(trackerId, t);

        }
        return mTrackers.get(trackerId);
    }
}
