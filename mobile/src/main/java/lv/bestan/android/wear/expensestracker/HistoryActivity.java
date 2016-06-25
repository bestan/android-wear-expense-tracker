package lv.bestan.android.wear.expensestracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import lv.bestan.android.wear.expensestracker.adapters.HistoryAdapter;
import lv.bestan.android.wear.expensestracker.db.ExpensesDataSource;
import lv.bestan.android.wear.expensestracker.models.Expense;
import lv.bestan.android.wear.expensestracker.models.MonthlyExpenses;
import lv.bestan.android.wear.expensestracker.utils.BackgroundHelper;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by Stan on 05/07/2014.
 */
public class HistoryActivity extends FragmentActivity {

    private static final String TAG = "HistoryActivity";
    private LinearLayout mContainer;
    private ExpandableListView mList;
    private HistoryAdapter mAdapter;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateList();
        }
    };

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        mContainer = (LinearLayout) findViewById(R.id.container);
        mList = (ExpandableListView) findViewById(R.id.list);

        updateList();

        IntentFilter intentFilter = new IntentFilter("add_expense_event");
        intentFilter.addAction("delete_expense_event");
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, intentFilter);

        ExpensesApplication.getInstance().sendScreenView("HistoryActivity");
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
            BackgroundHelper.updateBackground(mContainer);
            for (Expense expense : expenses) {
                int month = expense.getTime().get(Calendar.MONTH);
                int year = expense.getTime().get(Calendar.YEAR);

                if (year < currentYear || month < currentMonth) {
                    currentMonth = month;
                    currentYear = year;
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
