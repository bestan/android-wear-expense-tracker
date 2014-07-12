package lv.bestan.androidwearexpensetracker.models;

import java.util.Calendar;

/**
 * Created by Stan on 04/07/2014.
 */
public class Expense {

    private long id;
    private double amount;
    private Calendar time;

    public Expense() {

    }

    public Expense(double amount) {
        this.amount = amount;
        this.time = Calendar.getInstance();
    }

    public Expense(double amount, Calendar time) {
        this.amount = amount;
        this.time = time;
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Calendar getTime() {
        return time;
    }

    public void setTime(Calendar time) {
        this.time = time;
    }

    public void setTimeInMillis(long millis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        this.time = calendar;
    }

    public double getAmount() {
        return amount;
    }

    public String getAmountTwoDecimalPlaces() {
        return String.format("%.2f", amount);
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
