package lv.bestan.androidwearexpensetracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.ExpandableListView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import lv.bestan.androidwearexpensetracker.adapters.HistoryAdapter;
import lv.bestan.androidwearexpensetracker.db.ExpensesDataSource;
import lv.bestan.androidwearexpensetracker.models.Expense;
import lv.bestan.androidwearexpensetracker.models.MonthlyExpenses;

/**
 * Created by Stan on 05/07/2014.
 */
public class HistoryActivity extends ActionBarActivity {

    private static final String TAG = "HistoryActivity";
    private ExpandableListView mList;
    private HistoryAdapter mAdapter;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateList();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        getActionBar().setTitle("History");

        mList = (ExpandableListView) findViewById(R.id.list);

        updateList();

        IntentFilter intentFilter = new IntentFilter("add_expense_event");
        intentFilter.addAction("delete_expense_event");
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }

    private void updateList() {
        ArrayList<MonthlyExpenses> months = new ArrayList<MonthlyExpenses>();

        HashMap<MonthlyExpenses, List<Expense>> expensesMonthMap = new HashMap<MonthlyExpenses, List<Expense>>();
        List<Expense> expenses = ExpensesDataSource.getInstance(this).getAllExpenses();

        int currentMonth = 12;
        int currentYear = 3000;
        MonthlyExpenses currentMonthlyExpenses = null;
        String currentMonthString = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM yyyy");

        if (expenses != null && expenses.size() > 0) {
            for (Expense expense : expenses) {
                int month = expense.getTime().get(Calendar.MONTH);
                int year = expense.getTime().get(Calendar.YEAR);
                Log.d(TAG, "expense: " + expense.getAmount() + " month: " + month);

                if (year < currentYear || month < currentMonth) {
                    currentMonth = month;
                    currentMonthString = dateFormat.format(expense.getTime().getTime());
                    currentMonthlyExpenses = new MonthlyExpenses(currentMonthString);

                    months.add(currentMonthlyExpenses);
                    expensesMonthMap.put(currentMonthlyExpenses, new ArrayList<Expense>());
                }

                currentMonthlyExpenses.increaseTotalAmount(expense.getAmount());
                expensesMonthMap.get(currentMonthlyExpenses).add(expense);
            }

            mAdapter = new HistoryAdapter(this, months, expensesMonthMap);
            mList.setAdapter(mAdapter);
            mList.expandGroup(0);

            mAdapter.notifyDataSetInvalidated();
            mList.invalidate();
        } else {
            Toast.makeText(this, R.string.message_history_empty, Toast.LENGTH_LONG).show();
            finish();
        }

    }

}
