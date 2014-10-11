package lv.bestan.android.wear.expensestracker.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.ViewGroup;

/**
 * Created by Stan on 03/08/2014.
 */
public class BackgroundHelper {


    public static void updateBackground(ViewGroup viewGroup) {
        if (viewGroup != null) {
            SharedPreferences prefs = viewGroup.getContext().getSharedPreferences("android_wear_expenses", Context.MODE_PRIVATE);
            double amount = Double.valueOf(prefs.getString("amount", "0.00"));
            double budget = Double.valueOf(prefs.getString("budget", "500"));
            updateBackground(viewGroup, amount, budget);
        }
    }

    public static void updateBackground(ViewGroup viewGroup, double amount, double budget) {
        double percentage = amount / budget;
        if (percentage > 0.9) {
            viewGroup.setBackgroundColor(Color.parseColor("#8f3329"));
        } else if (percentage > 0.7) {
            viewGroup.setBackgroundColor(Color.parseColor("#8f4d29"));
        } else {
            viewGroup.setBackgroundColor(Color.parseColor("#298f38"));
        }
    }

}
