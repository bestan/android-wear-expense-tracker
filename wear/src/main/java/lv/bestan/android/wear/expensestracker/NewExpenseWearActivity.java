package lv.bestan.android.wear.expensestracker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.speech.RecognizerIntent;
import android.support.wearable.activity.ConfirmationActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Wearable;

import java.util.Currency;
import java.util.List;
import java.util.Locale;

import lv.bestan.android.wear.expensestracker.utils.BackgroundHelper;
import lv.bestan.android.wear.expensestracker.utils.CurrencyUtils;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class NewExpenseWearActivity extends Activity {

    private static final String TAG = "NewExpenseWearActivity";
    private static final int SPEECH_REQUEST_CODE = 0;
    private static final int NUMBERPAD_REQUEST_CODE = 1;

    private TextView mTitle;
    private boolean confirmingAmount = false;
    private int onResumeCount;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onResume() {
        super.onResume();
        onResumeCount++;
        if (onResumeCount >= 2 && !confirmingAmount) {
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        boolean skipSplash = getIntent().getExtras().getBoolean("SKIP_SPLASH");
        if (skipSplash) {
            startSpeechRecognizer();
        } else {
            setContentView(R.layout.activity_new_expense_wear);
            mTitle = (TextView) findViewById(R.id.title);
            ViewGroup viewGroup = (ViewGroup) findViewById(R.id.container);

            SharedPreferences prefs = this.getSharedPreferences("android_wear_expenses", Context.MODE_PRIVATE);
            BackgroundHelper.updateBackground(viewGroup);
            new CountDownTimer(3000, 1000) {

                public void onTick(long millisUntilFinished) {
                    mTitle.setText(mTitle.getText() + ".");
                }

                public void onFinish() {
                    startSpeechRecognizer();
                }
            }.start();
        }

        onResumeCount = 0;

    }

    private void startSpeechRecognizer() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Expense");
        startActivityForResult(intent, SPEECH_REQUEST_CODE);
    }

    private void analyseSpokenText(String spokenText) {
        spokenText = spokenText.replaceAll("\\$", "").replaceAll("€", "").replaceAll("£", "");

        for (String word : spokenText.split(" ")) {
            try {
                Double amount = Double.valueOf(word);
                expenseConfirmation(amount);
                break;
            } catch (Exception e) {
                e.printStackTrace();
                expenseConfirmation(0);
            }
        }
    }

    private void expenseConfirmation(final double amount) {
        setContentView(R.layout.activity_new_expense_confirm_wear);
        ViewGroup viewGroup = (ViewGroup) findViewById(R.id.container);
        BackgroundHelper.updateBackground(viewGroup);

        TextView amountTextView = (TextView) findViewById(R.id.amount);
        amountTextView.setText(CurrencyUtils.getCurrencySymbol() + String.format("%.2f", amount));

        findViewById(R.id.confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WearUtils.getInstance(NewExpenseWearActivity.this).sendExpense(amount);
                finish();
            }
        });

        findViewById(R.id.numberpad).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NewExpenseWearActivity.this, NumberPadActivity.class);
                startActivityForResult(intent, NUMBERPAD_REQUEST_CODE);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            List<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            analyseSpokenText(results.get(0));
            confirmingAmount = true;
        } else if (requestCode == NUMBERPAD_REQUEST_CODE && resultCode == RESULT_OK) {
            finish();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

}
