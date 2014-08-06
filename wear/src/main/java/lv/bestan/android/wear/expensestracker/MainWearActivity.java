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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Wearable;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Currency;
import java.util.Locale;

import lv.bestan.android.wear.expensestracker.utils.BackgroundHelper;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainWearActivity extends Activity {

    private static final String TAG = "MyWearActivity";
    private RelativeLayout mContainer;
    private TextView mAmount;
    private TextView mMonth;
    private Button mAddExpense;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive");
            retrieveDataFromSharedPreferences();
        }
    };

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(new CalligraphyContextWrapper(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_wear);
        mContainer = (RelativeLayout) findViewById(R.id.container);
        mAmount = (TextView) findViewById(R.id.amount);
        mMonth = (TextView) findViewById(R.id.month);
        mAddExpense = (Button) findViewById(R.id.button_add_expense);


        mAddExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openNewExpenseWearActivity(true);
//                openNumberPadActivity();
            }
        });

        retrieveDataFromSharedPreferences();

        IntentFilter intentFilter = new IntentFilter("expenses_update");
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, intentFilter);

        if (getIntent().getPackage() != null && getIntent().getPackage().equals("lv.bestan.android.wear.expensestracker")) {
            openNewExpenseWearActivity(false);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM");
        mMonth.setText(String.format(getString(R.string.month), dateFormat.format(Calendar.getInstance().getTime())));
        requestExpensesUpdate();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }

    private void openNumberPadActivity() {
        Intent intent = new Intent(this, NumberPadActivity.class);
        startActivity(intent);
    }

    private void openNewExpenseWearActivity(boolean skipSplash) {
        Intent intent = new Intent(this, NewExpenseWearActivity.class);
        intent.putExtra("SKIP_SPLASH", skipSplash);
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

        if (mAmount != null) {
            Currency currency = Currency.getInstance(Locale.getDefault());
            mAmount.setText(currency.getSymbol() + String.format("%.2f", amount));
        }

        double budget = Double.valueOf(prefs.getString("budget", "500"));
        BackgroundHelper.updateBackground(mContainer, amount, budget);
    }
}
