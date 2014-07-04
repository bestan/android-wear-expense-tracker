package lv.bestan.androidwearexpensetracker;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import lv.bestan.androidwearexpensetracker.models.Expense;


public class MainActivity extends ActionBarActivity {

    private ArrayList<Expense> mExpensesList;
    private LinearLayout mListContainer;
    private TextView mTotalAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mListContainer = (LinearLayout) findViewById(R.id.list_container);
        mTotalAmount = (TextView) findViewById(R.id.total_amount);

        mExpensesList = new ArrayList<Expense>();
        createTestExpenses();
        createTestUI();
        setTotalAmount();
    }

    private void setTotalAmount() {
        double total_amount = 0;
        for (Expense expense : mExpensesList) {
            total_amount += expense.getAmount();
        }
        mTotalAmount.setText(""+total_amount);
    }

    private void createTestExpenses() {
        mExpensesList.add(new Expense(13.55));
        mExpensesList.add(new Expense(27.13));
        mExpensesList.add(new Expense(8.44));
        mExpensesList.add(new Expense(19));
    }

    private void createTestUI() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        for (Expense expense : mExpensesList) {
            LinearLayout layout = new LinearLayout(this);
            layout.setOrientation(LinearLayout.HORIZONTAL);

            TextView amount = new TextView(this);
            amount.setText(""+expense.getAmount());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-2, -2);
            params.leftMargin = 40;
            amount.setLayoutParams(params);
            layout.addView(amount);

            TextView time = new TextView(this);
            time.setText(sdf.format(expense.getTime().getTime()));
            params = new LinearLayout.LayoutParams(-2, -2);
            params.leftMargin = 40;
            time.setLayoutParams(params);
            layout.addView(time);

            mListContainer.addView(layout);
        }
    }

}
