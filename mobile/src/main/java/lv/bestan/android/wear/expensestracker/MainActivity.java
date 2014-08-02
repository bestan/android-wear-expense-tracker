package lv.bestan.android.wear.expensestracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import lv.bestan.android.wear.expensestracker.db.ExpensesDataSource;
import lv.bestan.android.wear.expensestracker.models.Expense;


public class MainActivity extends ActionBarActivity {

    private static final String TAG = "MainActivity";
    public static List<Expense> expenses;

    private TextView mTotalAmount;
    private Button mHistory;
    private Button mAddExpense;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            expenses = ExpensesDataSource.getInstance(MainActivity.this).getAllExpenses();
            updateTotalAmount();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getActionBar().setTitle("Wear Expense Tracker");

        mHistory = (Button) findViewById(R.id.button_history);
        mTotalAmount = (TextView) findViewById(R.id.total_amount);
        mAddExpense = (Button) findViewById(R.id.button_add_expense);

        updateTotalAmount();

        mHistory.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                openHistoryActivity();
            }
        });

        mAddExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openNewExpenseActivity();
            }
        });

        IntentFilter intentFilter = new IntentFilter("add_expense_event");
        intentFilter.addAction("delete_expense_event");
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, intentFilter);

        ExpensesApplication.getInstance().sendScreenView("MainActivity");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }

    private void updateTotalAmount() {
        double total_amount = 0;
        expenses = ExpensesDataSource.getInstance(this).getAllExpenses();
        for (Expense expense : expenses) {
            total_amount += expense.getAmount();
        }
        mTotalAmount.setText("" + String.format("%.2f", total_amount));
    }

    private void deleteAllExpenses() {
        expenses = ExpensesDataSource.getInstance(this).getAllExpenses();
        for (Expense expense : expenses) {
            ExpensesDataSource.getInstance(this).deleteExpense(expense);
        }
    }

    private void createTestExpenses() {
        deleteAllExpenses();

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

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.main, menu);
//        return super.onCreateOptionsMenu(menu);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.action_add_expense:
//                openNewExpenseActivity();
//                return true;
//            case R.id.action_view_history:
//                openHistoryActivity();
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }

    private void openNewExpenseActivity() {
        Intent intent = new Intent(MainActivity.this, NewExpenseActivity.class);
        startActivity(intent);
    }

    private void openHistoryActivity() {
        Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
        startActivity(intent);
    }

}
