package lv.bestan.android.wear.expensestracker;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Wearable;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainWearActivity extends Activity {

    private static final String TAG = "MyWearActivity";
    private TextView mAmount;
    private TextView mAmountText;
    private Button mAddExpense;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive");
            retrieveDataFromSharedPreferences();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_wear);
        mAmount = (TextView) findViewById(R.id.amount);
        mAmountText = (TextView) findViewById(R.id.amount_text);
        mAddExpense = (Button) findViewById(R.id.button_add_expense);

        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM");
        mAmountText.setText(dateFormat.format(Calendar.getInstance().getTime()) + " expenses");

        mAddExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openNewExpenseWearActivity();
            }
        });

        retrieveDataFromSharedPreferences();

        IntentFilter intentFilter = new IntentFilter("expenses_update");
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, intentFilter);

        if (getIntent().getPackage() != null && getIntent().getPackage().equals("lv.bestan.android.wear.androidwearexpensetracker")) {
            openNewExpenseWearActivity();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        requestExpensesUpdate();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }

    private void openNewExpenseWearActivity() {
        Intent intent = new Intent(this, NewExpenseWearActivity.class);
        startActivity(intent);
    }

    private void requestExpensesUpdate() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                byte[] data = {};
                WearUtils wearUtils = WearUtils.getInstance(MainWearActivity.this);
                for (String node : wearUtils.getNodes()) {
                    MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(
                            wearUtils.getGoogleApiClient(), node, DataLayerWearListenerService.REQUEST_EXPENSES_UPDATE_PATH, data).await();
                    if (!result.getStatus().isSuccess()) {
                        Log.e(TAG, "ERROR: failed to send Message: " + result.getStatus());
                    } else {
                        Log.d(TAG, "Successfully sent a Message");
                    }

                }
            }
        }).start();
    }

    private void retrieveDataFromSharedPreferences() {
        SharedPreferences prefs = this.getSharedPreferences("android_wear_expenses", Context.MODE_PRIVATE);
        double amount = Double.valueOf(prefs.getString("amount", "0.00"));

        if (mAmount != null)
            mAmount.setText(String.format("%.2f", amount));
    }
}
