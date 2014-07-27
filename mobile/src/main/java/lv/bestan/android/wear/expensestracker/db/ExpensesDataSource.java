package lv.bestan.android.wear.expensestracker.db;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import lv.bestan.android.wear.expensestracker.models.Expense;

/**
 * Created by Stan on 11/07/2014.
 */
public class ExpensesDataSource {

    private static final String TAG = "ExpensesDataSource";

    private static ExpensesDataSource instance = null;
    public static ExpensesDataSource getInstance(Context context) {
        if (instance == null) {
            instance = new ExpensesDataSource(context);
        }
        return instance;
    }

    private Context context;
    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;
    private String[] allColumns = {
            MySQLiteHelper.COLUMN_ID,
            MySQLiteHelper.COLUMN_AMOUNT,
            MySQLiteHelper.COLUMN_TIME
    };


    public ExpensesDataSource(Context context) {
        dbHelper = new MySQLiteHelper(context);
        this.context = context;
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Expense saveExpense(Expense expense) {
        open();
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_AMOUNT, expense.getAmount());
        values.put(MySQLiteHelper.COLUMN_TIME, expense.getTime().getTimeInMillis());

        long insertId = database.insert(MySQLiteHelper.TABLE_EXPENSES, null, values);
        Cursor cursor = database.query(MySQLiteHelper.TABLE_EXPENSES,
                allColumns, MySQLiteHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Expense updatedExpense = cursorToExpense(cursor);
        cursor.close();
        close();

        Intent intent = new Intent("add_expense_event");
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        sendUpdateToWear();

        return updatedExpense;
    }

    public void deleteExpense(Expense expense) {
        open();
        long id = expense.getId();
        database.delete(MySQLiteHelper.TABLE_EXPENSES, MySQLiteHelper.COLUMN_ID
                + " = " + id, null);
        close();

        Intent intent = new Intent("delete_expense_event");
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        sendUpdateToWear();
    }

    public List<Expense> getAllExpenses() {
        open();
        List<Expense> expenses = new ArrayList<Expense>();

        Cursor cursor = database.query(MySQLiteHelper.TABLE_EXPENSES,
                allColumns, null, null, null, null, MySQLiteHelper.COLUMN_TIME + " DESC");

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Expense expense = cursorToExpense(cursor);
            expenses.add(expense);
            cursor.moveToNext();
        }
        cursor.close();
        close();
        return expenses;
    }

    public List<Expense> getExpensesForCurrentMonth() {
        return getExpensesForMonth(Calendar.getInstance().get(Calendar.MONTH));
    }

    public List<Expense> getExpensesForMonth(int month) {
        open();
        List<Expense> expenses = new ArrayList<Expense>();

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        long monthBeginningMillis = calendar.getTimeInMillis();

        calendar.set(Calendar.MONTH, month+1);
        long monthEndingMillis = calendar.getTimeInMillis();
        String selection = "time >= " + monthBeginningMillis + " AND time < " + monthEndingMillis;

        Cursor cursor = database.query(MySQLiteHelper.TABLE_EXPENSES,
                allColumns, selection, null, null, null, MySQLiteHelper.COLUMN_TIME + " DESC");

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Expense expense = cursorToExpense(cursor);
            expenses.add(expense);
            cursor.moveToNext();
        }
        cursor.close();
        close();
        Log.d(TAG, "month expenses size: " + expenses.size());
        return expenses;
    }

    private Expense cursorToExpense(Cursor cursor) {
        Expense expense = new Expense();
        expense.setId(cursor.getLong(0));
        expense.setAmount(cursor.getDouble(1));
        expense.setTimeInMillis(cursor.getLong(2));
        return expense;
    }

    public void sendUpdateToWear() {
        Log.d(TAG, "Sending update to wear");
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    Log.d(TAG, "running thread");
                    GoogleApiClient googleApiClient = new GoogleApiClient.Builder(context)
                            .addApi(Wearable.API)
                            .build();
                    googleApiClient.connect();
                    Log.d(TAG, "Nodes: " + getNodes(googleApiClient));

                    double totalAmount = 0;
                    List<Expense> expenses = getExpensesForCurrentMonth();
                    for (Expense expense : expenses) {
                        totalAmount += expense.getAmount();
                    }
                    byte[] data = String.valueOf(totalAmount).getBytes("UTF-8");
                    for (String node : getNodes(googleApiClient)) {
                        MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(
                                googleApiClient, node, "/expense", data).await();
                        if (!result.getStatus().isSuccess()) {
                            Log.e(TAG, "ERROR: failed to send Message: " + result.getStatus());
                        } else {
                            Log.d(TAG, "Successfully sent a Message");
                        }

                    }

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private Collection<String> getNodes(GoogleApiClient googleApiClient) {
        HashSet<String> results = new HashSet<String>();
        NodeApi.GetConnectedNodesResult nodes =
                Wearable.NodeApi.getConnectedNodes(googleApiClient).await();
        for (Node node : nodes.getNodes()) {
            results.add(node.getId());
        }
        return results;
    }
}
