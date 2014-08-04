package lv.bestan.android.wear.expensestracker;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import lv.bestan.android.wear.expensestracker.utils.BackgroundHelper;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by Stan on 04/08/2014.
 */
public class NumberPadActivity extends Activity implements View.OnClickListener {

    private static final int ACTION_DELETE = -1;
    private static final int ACTION_DOT = -2;
    private static final int ACTION_DONE = -3;

    private TextView mNumber;
    private TextView mDelete;
    private LinearLayout mContainer;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(new CalligraphyContextWrapper(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_number_pad);

        mContainer = (LinearLayout) findViewById(R.id.container);
        mNumber = (TextView) findViewById(R.id.number);
        mDelete = (TextView) findViewById(R.id.delete);

        mDelete.setOnClickListener(this);
        mDelete.setTag(ACTION_DELETE);

        for (int i = 0; i < 3; i++) {
            LinearLayout layout = new LinearLayout(this);
            layout.setOrientation(LinearLayout.HORIZONTAL);
            for (int j = 0; j < 3; j++) {
                int number = j + i * 3 + 1;
                TextView textView = new TextView(this);
                textView.setText("" + number);
                textView.setTag(number);
                styleTextView(textView);
                layout.addView(textView);
            }
            mContainer.addView(layout);
        }

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        TextView textView = new TextView(this);
        textView.setText(".");
        textView.setTag(ACTION_DOT);
        layout.addView(styleTextView(textView));

        textView = new TextView(this);
        textView.setText("0");
        textView.setTag(0);
        layout.addView(styleTextView(textView));

        textView = new TextView(this);
        textView.setText("D");
        textView.setTag(ACTION_DONE);
        layout.addView(styleTextView(textView));

        mContainer.addView(layout);
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences prefs = this.getSharedPreferences("android_wear_expenses", Context.MODE_PRIVATE);
        double amount = Double.valueOf(prefs.getString("amount", "0.00"));
        double budget = Double.valueOf(prefs.getString("budget", "500"));
        BackgroundHelper.updateBackground(mContainer, amount, budget);
    }

    private TextView styleTextView(TextView textView) {
        textView.setTextColor(Color.parseColor("#FFFFFF"));
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(30);
        textView.setOnClickListener(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1);
        textView.setLayoutParams(params);
        return textView;
    }

    @Override
    public void onClick(View v) {
        int tag = (Integer) v.getTag();

        String numberText = (String) mNumber.getText();
        if (tag >= 0) {
            mNumber.setText(numberText + tag);
        }

        if (tag == ACTION_DOT) {
            mNumber.setText(numberText + ".");
        }

        if (tag == ACTION_DELETE) {
            mNumber.setText(numberText.substring(0, numberText.length() -1));
        }

        if (tag == ACTION_DONE) {
            double number = Double.valueOf((String) mNumber.getText());
            WearUtils.getInstance(this).sendExpense(number);
            finish();
        }

    }
}
