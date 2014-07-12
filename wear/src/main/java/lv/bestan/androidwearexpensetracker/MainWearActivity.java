package lv.bestan.androidwearexpensetracker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Wearable;

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
                openNewExpenseWearActivity();
            }
        });
    }

    private void openNewExpenseWearActivity() {
        Intent intent = new Intent(this, NewExpenseWearActivity.class);
        startActivity(intent);
    }
}
