package lv.bestan.androidwearexpensetracker;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.ExpandableListView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import lv.bestan.androidwearexpensetracker.adapters.HistoryAdapter;
import lv.bestan.androidwearexpensetracker.db.ExpensesDataSource;
import lv.bestan.androidwearexpensetracker.models.Expense;

/**
 * Created by Stan on 05/07/2014.
 */
public class HistoryActivity extends ActionBarActivity {

    private static final String TAG = "HistoryActivity";
    private ExpandableListView mList;
    private HistoryAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        getActionBar().setTitle("History");

        this.mList = (ExpandableListView) findViewById(R.id.list);

        ArrayList<String> months = new ArrayList<String>();

        HashMap<String, List<Expense>> expensesMonthMap = new HashMap<String, List<Expense>>();
        List<Expense> expenses = ExpensesDataSource.getInstance(this).getAllExpenses();

        int currentMonth = 12;
        String currentMonthString = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM");

        for (Expense expense : expenses) {
            int month = expense.getTime().get(Calendar.MONTH);
            Log.d(TAG, "expense: " + expense.getAmount() + " month: " + month);

            if (month < currentMonth) {
                currentMonth = month;
                currentMonthString = dateFormat.format(expense.getTime().getTime());

                months.add(currentMonthString);
                expensesMonthMap.put(currentMonthString, new ArrayList<Expense>());
            }

            expensesMonthMap.get(currentMonthString).add(expense);
        }

        mAdapter = new HistoryAdapter(this, months, expensesMonthMap);
        mList.setAdapter(mAdapter);
        mList.expandGroup(0);
    }

}
