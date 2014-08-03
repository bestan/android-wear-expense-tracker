package lv.bestan.android.wear.expensestracker;

import android.app.Application;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by Stan on 03/08/2014.
 */
public class WearApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        CalligraphyConfig.initDefault("Campton.Light.otf", R.attr.fontPath);
    }
}
