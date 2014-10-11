package lv.bestan.android.wear.expensestracker;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.widget.ImageView;
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

    private WatchViewStub stub;
    private TextView mNumber;
    private ImageView mDelete;
    private LinearLayout mContainer;
    private LinearLayout mNumberPadContainer;
    private boolean isRound = false;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(new CalligraphyContextWrapper(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("test", "build: " + Build.DEVICE + " : " + Build.MODEL);

        if (Build.MODEL.contains("360")) {
            isRound = true;
            setContentView(R.layout.layout_number_pad_round);
        } else {
            setContentView(R.layout.layout_number_pad_square);
        }

        initUI();

    }

    private void initUI() {
        mContainer = (LinearLayout) findViewById(R.id.container);
        mNumberPadContainer = (LinearLayout) findViewById(R.id.numberpad_container);
        mNumber = (TextView) findViewById(R.id.number);
        mDelete = (ImageView) findViewById(R.id.delete);

        mContainer.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
            @Override
            public WindowInsets onApplyWindowInsets(View view, WindowInsets windowInsets) {
                Log.d("test", "isRound: " + windowInsets.isRound());
                if (windowInsets.isRound()) {

                }
                return windowInsets;
            }
        });

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
                setNumberPadRowParams(layout);
                layout.addView(textView);
            }
            mNumberPadContainer.addView(layout);
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

        ImageView imageView = new ImageView(this);
        imageView.setImageResource(R.drawable.button_accept);
        imageView.setTag(ACTION_DONE);
        imageView.setOnClickListener(this);
        imageView.setPadding(10, 10, 10, 10);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1);
        imageView.setLayoutParams(params);
        layout.addView(imageView);

        setNumberPadRowParams(layout);
        mNumberPadContainer.addView(layout);
    }

    private void setNumberPadRowParams(LinearLayout layout) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1);
        layout.setLayoutParams(params);
    }

    @Override
    protected void onResume() {
        super.onResume();
        BackgroundHelper.updateBackground(mContainer);
    }

    private TextView styleTextView(TextView textView) {
        textView.setTextColor(Color.parseColor("#FFFFFF"));
        textView.setGravity(Gravity.CENTER);
        if (isRound) {
            textView.setTextSize(18);
            textView.setPadding(0, 3, 0, 3);
        } else {
            textView.setTextSize(25);
            textView.setPadding(0, 5, 0, 5);
        }
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
            if (!TextUtils.isEmpty(numberText)) {
                mNumber.setText(numberText.substring(0, numberText.length() - 1));
            }
        }

        if (tag == ACTION_DONE && !((String) mNumber.getText()).isEmpty()) {
            double number = Double.valueOf((String) mNumber.getText());
            WearUtils.getInstance(this).sendExpense(number);
            setResult(RESULT_OK);
            finish();
        }

    }
}
