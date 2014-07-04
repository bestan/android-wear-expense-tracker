package lv.bestan.androidwearexpensetracker.models;

import java.util.Calendar;

/**
 * Created by Stan on 04/07/2014.
 */
public class Expense {

    private double amount;
    private Calendar time;

    public Expense(double amount) {
        this.amount = amount;
        this.time = Calendar.getInstance();
    }

    public Calendar getTime() {
        return time;
    }

    public void setTime(Calendar time) {
        this.time = time;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
