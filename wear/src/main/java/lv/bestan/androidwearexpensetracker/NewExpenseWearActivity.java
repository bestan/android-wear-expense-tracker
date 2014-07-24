package lv.bestan.androidwearexpensetracker;

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

    private TextView mCounter;


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
        setContentView(R.layout.activity_new_expense_wear);
        mCounter = (TextView) findViewById(R.id.counter);

        onResumeCount = 0;

        new CountDownTimer(3000, 1000) {

            public void onTick(long millisUntilFinished) {
                mCounter.setText(""+millisUntilFinished / 1000);
            }

            public void onFinish() {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Voice recognition Demo...");
                // Start the activity, the intent will be populated with the speech text
                startActivityForResult(intent, SPEECH_REQUEST_CODE);
            }
        }.start();


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            List<String> results = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
            String spokenText = results.get(0);

            String numberString = null;
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
