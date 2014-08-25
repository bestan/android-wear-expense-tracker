package lv.bestan.android.wear.expensestracker.adapters;

/**
 * Created by Stan on 05/07/2014.
 */

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import lv.bestan.android.wear.expensestracker.R;
import lv.bestan.android.wear.expensestracker.db.ExpensesDataSource;
import lv.bestan.android.wear.expensestracker.models.Expense;
import lv.bestan.android.wear.expensestracker.models.MonthlyExpenses;
import lv.bestan.android.wear.expensestracker.utils.CurrencyUtils;

public class HistoryAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<MonthlyExpenses> months;
    private HashMap<MonthlyExpenses, List<Expense>> expensesMap;

    public HistoryAdapter(Context context, List<MonthlyExpenses> months, HashMap<MonthlyExpenses, List<Expense>> expensesMap) {
        this.context = context;
        this.months = months;
        this.expensesMap = expensesMap;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this.expensesMap.get(this.months.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        final Expense expense = (Expense) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item, null);
        }

        TextView amount = (TextView) convertView.findViewById(R.id.amount);
        amount.setText(CurrencyUtils.getCurrencySymbol() + expense.getAmount());

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        TextView time = (TextView) convertView.findViewById(R.id.time);
        time.setText(sdf.format(expense.getTime().getTime()));

        ImageView delete = (ImageView) convertView.findViewById(R.id.delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExpensesDataSource.getInstance(context).deleteExpense(expense);
            }
        });

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this.expensesMap.get(this.months.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.months.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this.months.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        MonthlyExpenses monthlyExpenses = (MonthlyExpenses) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_group, null);
        }

        TextView lblListHeader = (TextView) convertView.findViewById(R.id.month);
        lblListHeader.setText(monthlyExpenses.getMonth());

        TextView total = (TextView) convertView.findViewById(R.id.total);
        total.setText(CurrencyUtils.getCurrencySymbol() + String.format("%.2f", monthlyExpenses.getTotalAmount()));

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
