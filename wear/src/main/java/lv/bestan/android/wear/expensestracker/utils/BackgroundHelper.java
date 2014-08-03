package lv.bestan.android.wear.expensestracker.utils;

import android.graphics.Color;
import android.view.ViewGroup;

/**
 * Created by Stan on 03/08/2014.
 */
public class BackgroundHelper {

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
