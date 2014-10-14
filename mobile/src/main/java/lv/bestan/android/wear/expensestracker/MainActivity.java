package lv.bestan.android.wear.expensestracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.doomonafireball.betterpickers.numberpicker.NumberPickerBuilder;
import com.doomonafireball.betterpickers.numberpicker.NumberPickerDialogFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

import lv.bestan.android.wear.expensestracker.db.ExpensesDataSource;
import lv.bestan.android.wear.expensestracker.models.Budget;
import lv.bestan.android.wear.expensestracker.models.Expense;
import lv.bestan.android.wear.expensestracker.utils.BackgroundHelper;
import lv.bestan.android.wear.expensestracker.utils.CurrencyUtils;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class MainActivity extends FragmentActivity {

    private static final String TAG = "MainActivity";
    public static List<Expense> expenses;

    private TextView mTitle;
    private TextView mAmount;
    private Button mHistory;
    private Button mAddExpense;
    private TextView mMonth;
    private TextView mBudget;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            expenses = ExpensesDataSource.getInstance(MainActivity.this).getAllExpenses();
            updateValues();
        }
    };
    private RelativeLayout mContainer;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(new CalligraphyContextWrapper(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContainer = (RelativeLayout) findViewById(R.id.container);
        mTitle = (TextView) findViewById(R.id.title);
        mHistory = (Button) findViewById(R.id.button_history);
        mAmount = (TextView) findViewById(R.id.amount);
        mAddExpense = (Button) findViewById(R.id.button_add_expense);
        mMonth = (TextView) findViewById(R.id.month);
        mBudget = (TextView) findViewById(R.id.budget);

        updateValues();

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

        mBudget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                NumberPickerBuilder npb = new NumberPickerBuilder()
                        .setFragmentManager(getSupportFragmentManager())
                        .setPlusMinusVisibility(NumberPicker.INVISIBLE)
                        .setStyleResId(R.style.BetterPickersDialogFragment);
                npb.show();
                npb.addNumberPickerDialogHandler(new NumberPickerDialogFragment.NumberPickerDialogHandler() {
                    @Override
                    public void onDialogNumberSet(int reference, int number, double decimal, boolean isNegative, double fullNumber) {
                        Budget.setAmount(MainActivity.this, fullNumber);
                        updateValues();
                        ExpensesDataSource.getInstance(MainActivity.this).sendUpdateToWear();
                    }
                });
            }
        });

        IntentFilter intentFilter = new IntentFilter("add_expense_event");
        intentFilter.addAction("delete_expense_event");
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, intentFilter);

        ExpensesApplication.getInstance().sendScreenView("MainActivity");
    }

    @Override
    protected void onResume() {
        super.onResume();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM");
        mMonth.setText(String.format(getString(R.string.month), dateFormat.format(Calendar.getInstance().getTime())));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }

    private void updateValues() {
        double total_amount = 0;
        expenses = ExpensesDataSource.getInstance(this).getExpensesForCurrentMonth();
        for (Expense expense : expenses) {
            total_amount += expense.getAmount();
        }

        mAmount.setText(CurrencyUtils.getCurrencySymbol() + String.format("%.2f", total_amount));

        double budget = Budget.getAmount(this);
        mBudget.setText(String.format(getString(R.string.budget), CurrencyUtils.getCurrencySymbol() + String.format("%.2f", budget)));

        BackgroundHelper.updateBackground(mContainer, total_amount, budget);
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

    private void openNewExpenseActivity() {
        Intent intent = new Intent(MainActivity.this, NewExpenseActivity.class);
        startActivity(intent);
    }

    private void openHistoryActivity() {
        Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
        startActivity(intent);
    }

}
