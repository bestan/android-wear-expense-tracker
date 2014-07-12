package lv.bestan.androidwearexpensetracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import lv.bestan.androidwearexpensetracker.db.ExpensesDataSource;
import lv.bestan.androidwearexpensetracker.models.Expense;


public class MainActivity extends ActionBarActivity {

    public static List<Expense> expenses;

    private LinearLayout mListContainer;
    private TextView mTotalAmount;
    private Button mHistory;
    private RelativeLayout mContainer;
    private ImageView mAddExpense;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContainer = (RelativeLayout) findViewById(R.id.container);
        mHistory = (Button) findViewById(R.id.button_history);
        mTotalAmount = (TextView) findViewById(R.id.total_amount);
        mAddExpense = (ImageView) findViewById(R.id.button_add_expense);

        createTestExpenses();
        updateTotalAmount();

        getActionBar().setTitle("Overview");

        mHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openHistoryActivity();
            }
        });

        LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                expenses = ExpensesDataSource.getInstance(MainActivity.this).getAllExpenses();
                updateTotalAmount();
            }
        }, new IntentFilter("add_expense_event"));
    }

    private void updateTotalAmount() {
        double total_amount = 0;
        expenses = ExpensesDataSource.getInstance(this).getAllExpenses();
        for (Expense expense : expenses) {
            total_amount += expense.getAmount();
        }
        mTotalAmount.setText("" + total_amount);
    }

    private void deleteAllExpenses() {
        expenses = ExpensesDataSource.getInstance(this).getAllExpenses();
        for (Expense expense : expenses) {
            ExpensesDataSource.getInstance(this).deleteExpense(expense);
        }
    }

    private void createTestExpenses() {
        expenses = ExpensesDataSource.getInstance(this).getAllExpenses();
        for (Expense expense : expenses) {
            ExpensesDataSource.getInstance(this).deleteExpense(expense);
        }

        expenses = new ArrayList<Expense>();
        expenses.add(new Expense(13.55));

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 2);
        expenses.add(new Expense(27.13, calendar));

        calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, Calendar.JUNE);
        expenses.add(new Expense(8.44, calendar));

        calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, Calendar.MAY);
        expenses.add(new Expense(19, calendar));


        for (Expense expense: expenses) {
            ExpensesDataSource.getInstance(this).saveExpense(expense);
        }
    }

    private void createTestUI() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        for (Expense expense : expenses) {
            LinearLayout layout = new LinearLayout(this);
            layout.setOrientation(LinearLayout.HORIZONTAL);

            TextView amount = new TextView(this);
            amount.setText("" + expense.getAmount());
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_add_expense:
                openAddExpenseActivity();
                return true;
            case R.id.action_view_history:
                openHistoryActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void openAddExpenseActivity() {
    }

    private void openHistoryActivity() {
        Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
        startActivity(intent);
    }

}
