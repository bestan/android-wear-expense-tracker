package lv.bestan.android.wear.expensestracker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.speech.RecognizerIntent;
import android.support.wearable.activity.ConfirmationActivity;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Wearable;

import java.util.List;

public class NewExpenseWearActivity extends Activity {

    private static final String TAG = "NewExpenseWearActivity";
    private static final int SPEECH_REQUEST_CODE = 0;

    private TextView mTitle;


    private int onResumeCount;

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
                sendExpense(amount);
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

    private void sendExpense(final double expense) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    byte[] data = String.valueOf(expense).getBytes("UTF-8");
                    WearUtils wearUtils = WearUtils.getInstance(NewExpenseWearActivity.this);
                    for (String node : wearUtils.getNodes()) {
                        MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(
                                wearUtils.getGoogleApiClient(), node, DataLayerWearListenerService.NEW_EXPENSE_PATH, data).await();
                        if (!result.getStatus().isSuccess()) {
                            Log.e(TAG, "ERROR: failed to send Message: " + result.getStatus());
                        } else {
                            Log.d(TAG, "Successfully sent a Message");
                        }

                    }
                    NewExpenseWearActivity.this.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            Intent intent = new Intent(NewExpenseWearActivity.this, ConfirmationActivity.class);
                            intent.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE, ConfirmationActivity.SUCCESS_ANIMATION);
                            startActivity(intent);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}
