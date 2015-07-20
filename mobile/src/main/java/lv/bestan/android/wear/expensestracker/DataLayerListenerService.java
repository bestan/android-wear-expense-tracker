package lv.bestan.android.wear.expensestracker;

import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import lv.bestan.android.wear.expensestracker.db.ExpensesDataSource;
import lv.bestan.android.wear.expensestracker.models.Expense;

/**
 * Created by Stan on 07/07/2014.
 */
public class DataLayerListenerService extends WearableListenerService {

    private static final String TAG = "DataLayerSample";
    public static final String NEW_EXPENSE_PATH = "/new_expense";
    public static final String REQUEST_EXPENSES_UPDATE_PATH = "/request_expenses_update";

    public static final String CRASH_PATH = "/crash_path";

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        if (messageEvent.getPath().equals(NEW_EXPENSE_PATH)) {
            try {
                byte[] data = messageEvent.getData();
                String text = new String(data, "UTF-8");
                Double amount = Double.valueOf(text);
                Log.d(TAG, "Message Received, expense: :" + amount);

                Expense expense = new Expense(amount);
                ExpensesDataSource.getInstance(this).saveExpense(expense);

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (messageEvent.getPath().equals(REQUEST_EXPENSES_UPDATE_PATH)) {
            ExpensesDataSource.getInstance(this).sendUpdateToWear();
        } else if (messageEvent.getPath().equals(CRASH_PATH)) {
            try {
                byte[] data = messageEvent.getData();
                String text = new String(data, "UTF-8");
                Log.d("test", text);

                //TODO store exceptions (used to send them by email)

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

}
