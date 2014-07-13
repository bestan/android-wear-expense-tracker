package lv.bestan.androidwearexpensetracker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class NewExpenseWearActivity extends Activity {

    private static final String TAG = "NewExpenseWearActivity";
    private static final int SPEECH_REQUEST_CODE = 0;

    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new LinearLayout(this));


        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Voice recognition Demo...");
        // Start the activity, the intent will be populated with the speech text
        startActivityForResult(intent, SPEECH_REQUEST_CODE);
    }

    // This callback is invoked when the Speech Recognizer returns.
    // This is where you process the intent and extract the speech text from the intent.
    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            List<String> results = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
            String spokenText = results.get(0);
            // Do something with spokenText

            String numberString = null;
            for (String word : spokenText.split(" ")) {
                try {
                    Double amount = Double.valueOf(word);
                    sendExpense(amount);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

//            Number number = 0;
//            try {
//                number = NumberFormat.getCurrencyInstance(Locale.UK).parse(numberString);
//                mTextView.setText("" + number.doubleValue() + "(original: " + spokenText + ")");
//                sendExpense((number.doubleValue()));
//            } catch (Exception e) {
//                e.printStackTrace();
//                mTextView.setText("exception");
//            }

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
                            finish();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}
