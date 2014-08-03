package lv.bestan.android.wear.expensestracker.models;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Stan on 03/08/2014.
 */
public class Budget {

    public static double getAmount(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("budget", Context.MODE_PRIVATE);
        return prefs.getFloat("budget", 500);
    }

    public static void setAmount(Context context, double amount) {
        SharedPreferences prefs = context.getSharedPreferences("budget", Context.MODE_PRIVATE);
        prefs.edit().putFloat("budget", (float)amount).commit();
    }
}
