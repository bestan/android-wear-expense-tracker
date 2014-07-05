package lv.bestan.androidwearexpensetracker;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import lv.bestan.androidwearexpensetracker.adapters.HistoryAdapter;
import lv.bestan.androidwearexpensetracker.models.Expense;

/**
 * Created by Stan on 05/07/2014.
 */
public class HistoryActivity extends ActionBarActivity {

    private ExpandableListView mList;
    private HistoryAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        getActionBar().setTitle("History");

        this.mList = (ExpandableListView) findViewById(R.id.list);

        ArrayList<String> months = new ArrayList<String>();
        months.add("July");
        months.add("June");
        months.add("May");

        HashMap<String, List<Expense>> expensesMap = new HashMap<String, List<Expense>>();
        for (String month : months) {
            expensesMap.put(month, MainActivity.expenses);
        }

        mAdapter = new HistoryAdapter(this, months, expensesMap);
        mList.setAdapter(mAdapter);
    }

}
