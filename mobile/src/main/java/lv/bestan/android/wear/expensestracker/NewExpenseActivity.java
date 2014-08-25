package lv.bestan.android.wear.expensestracker;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.doomonafireball.betterpickers.datepicker.DatePickerBuilder;
import com.doomonafireball.betterpickers.datepicker.DatePickerDialogFragment;
import com.doomonafireball.betterpickers.numberpicker.NumberPickerBuilder;
import com.doomonafireball.betterpickers.numberpicker.NumberPickerDialogFragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Currency;
import java.util.Locale;

import lv.bestan.android.wear.expensestracker.db.ExpensesDataSource;
import lv.bestan.android.wear.expensestracker.models.Expense;
import lv.bestan.android.wear.expensestracker.utils.BackgroundHelper;
import lv.bestan.android.wear.expensestracker.utils.CurrencyUtils;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class NewExpenseActivity extends FragmentActivity {

    private TextView mAmount;
    private TextView mTime;

    private double amount;
    private Calendar time;
    private Button mDone;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(new CalligraphyContextWrapper(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_expense);

        mAmount = (TextView) findViewById(R.id.amount);
        mTime = (TextView) findViewById(R.id.time);
        mDone = (Button) findViewById(R.id.done);

        amount = 0;
        time = Calendar.getInstance();
        updateFields();

        showNumberPicker();

        mAmount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNumberPicker();
            }
        });

        mTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerBuilder picker = new DatePickerBuilder()
                        .setFragmentManager(getSupportFragmentManager())
                        .setStyleResId(R.style.BetterPickersDialogFragment);
                picker.show();
                picker.addDatePickerDialogHandler(new DatePickerDialogFragment.DatePickerDialogHandler() {
                    @Override
                    public void onDialogDateSet(int reference, int year, int monthOfYear, int dayOfMonth) {
                        time = Calendar.getInstance();
                        time.set(Calendar.YEAR, year);
                        time.set(Calendar.MONTH, monthOfYear);
                        time.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        updateFields();
                    }
                });


            }
        });

        mDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Expense expense = new Expense(amount, time);
                ExpensesDataSource.getInstance(NewExpenseActivity.this).saveExpense(expense);
                onBackPressed();
            }

        });

        ViewGroup viewGroup = (ViewGroup) findViewById(R.id.container);
        BackgroundHelper.updateBackground(viewGroup);

        ExpensesApplication.getInstance().sendScreenView("NewExpenseActivity");
    }

    private void updateFields() {
        mAmount.setText(CurrencyUtils.getCurrencySymbol() + String.format("%.2f", amount));

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy");
        mTime.setText(dateFormat.format(time.getTime()));
    }

    private void showNumberPicker() {
        NumberPickerBuilder npb = new NumberPickerBuilder()
                .setFragmentManager(getSupportFragmentManager())
                .setPlusMinusVisibility(NumberPicker.INVISIBLE)
                .setStyleResId(R.style.BetterPickersDialogFragment);
        npb.show();
        npb.addNumberPickerDialogHandler(new NumberPickerDialogFragment.NumberPickerDialogHandler() {
            @Override
            public void onDialogNumberSet(int reference, int number, double decimal, boolean isNegative, double fullNumber) {
                amount = fullNumber;
                updateFields();
            }
        });
    }
}
