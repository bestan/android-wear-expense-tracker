package lv.bestan.androidwearexpensetracker;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Sensor;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.wearable.view.BoxInsetLayout;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.text.NumberFormat;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainWearActivity extends Activity {

    private static final String TAG = "MyWearActivity";
    private TextView mTextView;
    private Button mButton;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_wear);
        mTextView = (TextView) findViewById(R.id.textView4);
        mButton = (Button) findViewById(R.id.button2);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displaySpeechRecognizer();
//                sendExpense(5.0f);
            }
        });

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();
        mGoogleApiClient.connect();
    }

    private static final int SPEECH_REQUEST_CODE = 0;

    // Create an intent that can start the Speech Recognizer activity
    private void displaySpeechRecognizer() {
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
            mTextView.setText(spokenText);

            String numberString = null;
            for (String word : spokenText.split(" ")) {
                if (word.contains("£")) {
                    numberString = word;
                }
            }

            Number number = 0;
            try {
                number = NumberFormat.getCurrencyInstance(Locale.UK).parse(numberString);
                mTextView.setText("" + number.doubleValue() + "(original: " + spokenText + ")");
                sendExpense((number.doubleValue()));
            } catch (Exception e) {
                e.printStackTrace();
                mTextView.setText("exception");
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void sendExpense(final double expense) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    Log.e(TAG, "Nodes: " + getNodes());
                    byte[] data = String.valueOf(expense).getBytes("UTF-8");
                    for (String node : getNodes()) {
                        MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(
                                mGoogleApiClient, node, "/expense", data).await();
                        if (!result.getStatus().isSuccess()) {
                            Log.e(TAG, "ERROR: failed to send Message: " + result.getStatus());
                        } else {
                            Log.d(TAG, "Successfully sent a Message");
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    private Collection<String> getNodes() {
        HashSet<String> results = new HashSet<String>();
        NodeApi.GetConnectedNodesResult nodes =
                Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).await();
        for (Node node : nodes.getNodes()) {
            results.add(node.getId());
        }
        return results;
    }
}
