package lv.bestan.android.wear.expensestracker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

/**
 * Created by Stan on 07/07/2014.
 */
public class DataLayerWearListenerService extends WearableListenerService {

    private static final String TAG = "DataLayerSample";
    public static final String NEW_EXPENSE_PATH = "/new_expense";
    public static final String REQUEST_EXPENSES_UPDATE_PATH = "/request_expenses_update";

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        try {
            byte[] data = messageEvent.getData();
            String text = new String(data, "UTF-8");
            Double amount = Double.valueOf(text);
            Log.d(TAG, "Expense: :" + amount);

            SharedPreferences prefs = this.getSharedPreferences("android_wear_expenses", Context.MODE_PRIVATE);
            prefs.edit().putString("amount", String.valueOf(amount)).commit();

            Intent intent = new Intent("expenses_update");
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
