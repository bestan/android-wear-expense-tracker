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
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Wearable;

import java.util.List;

import lv.bestan.android.wear.expensestracker.utils.BackgroundHelper;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class NewExpenseWearActivity extends Activity {

    private static final String TAG = "NewExpenseWearActivity";
    private static final int SPEECH_REQUEST_CODE = 0;

    private TextView mTitle;


    private int onResumeCount;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(new CalligraphyContextWrapper(newBase));
    }

    @Override
    protected void onResume() {
        super.onResume();
        onResumeCount++;
        if (onResumeCount >= 2) {
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        onResumeCount = 0;

        boolean skipSplash = getIntent().getExtras().getBoolean("SKIP_SPLASH");
        if (skipSplash) {
            startSpeechRecognizer();
        } else {
            setContentView(R.layout.activity_new_expense_wear);
            mTitle = (TextView) findViewById(R.id.title);
            ViewGroup viewGroup = (ViewGroup) findViewById(R.id.container);

            SharedPreferences prefs = this.getSharedPreferences("android_wear_expenses", Context.MODE_PRIVATE);
            double amount = Double.valueOf(prefs.getString("amount", "0.00"));
            double budget = Double.valueOf(prefs.getString("budget", "500"));
            BackgroundHelper.updateBackground(viewGroup, amount, budget);
            new CountDownTimer(3000, 1000) {

                public void onTick(long millisUntilFinished) {
                    mTitle.setText(mTitle.getText() + ".");
                }

                public void onFinish() {
                    startSpeechRecognizer();
                }
            }.start();
        }

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
                WearUtils.getInstance(this).sendExpense(amount);
                break;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            List<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            analyseSpokenText(results.get(0));
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
