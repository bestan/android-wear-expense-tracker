package lv.bestan.androidwearexpensetracker.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import lv.bestan.androidwearexpensetracker.models.Expense;

/**
 * Created by Stan on 11/07/2014.
 */
public class ExpensesDataSource {

    private static ExpensesDataSource instance = null;
    public static ExpensesDataSource getInstance(Context context) {
        if (instance == null) {
            instance = new ExpensesDataSource(context);
        }
        return instance;
    }

    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;
    private String[] allColumns = {
            MySQLiteHelper.COLUMN_ID,
            MySQLiteHelper.COLUMN_AMOUNT,
            MySQLiteHelper.COLUMN_TIME
    };

    public ExpensesDataSource(Context context) {
        dbHelper = new MySQLiteHelper(context);
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
        return updatedExpense;
    }

    public void deleteExpense(Expense expense) {
        open();
        long id = expense.getId();
        database.delete(MySQLiteHelper.TABLE_EXPENSES, MySQLiteHelper.COLUMN_ID
                + " = " + id, null);
        close();
    }

    public List<Expense> getAllExpenses() {
        open();
        List<Expense> comments = new ArrayList<Expense>();

        Cursor cursor = database.query(MySQLiteHelper.TABLE_EXPENSES,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Expense expense = cursorToExpense(cursor);
            comments.add(expense);
            cursor.moveToNext();
        }
        cursor.close();
        close();
        return comments;
    }

    private Expense cursorToExpense(Cursor cursor) {
        Expense expense = new Expense();
        expense.setId(cursor.getLong(0));
        expense.setAmount(cursor.getDouble(1));
        expense.setTimeInMillis(cursor.getLong(2));
        return expense;
    }
}
