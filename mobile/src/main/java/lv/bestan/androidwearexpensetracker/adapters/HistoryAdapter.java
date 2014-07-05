package lv.bestan.androidwearexpensetracker.adapters;

/**
 * Created by Stan on 05/07/2014.
 */

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

import lv.bestan.androidwearexpensetracker.R;
import lv.bestan.androidwearexpensetracker.models.Expense;

public class HistoryAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<String> months;
    private HashMap<String, List<Expense>> expensesMap;

    public HistoryAdapter(Context context, List<String> months, HashMap<String, List<Expense>> expensesMap) {
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
    public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        final Expense expense = (Expense) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item, null);
        }

        TextView textView = (TextView) convertView.findViewById(R.id.text);
        textView.setText(""+expense.getAmount());

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
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_group, null);
        }

        TextView lblListHeader = (TextView) convertView.findViewById(R.id.month);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);

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
