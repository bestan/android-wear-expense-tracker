package lv.bestan.androidwearexpensetracker.models;

/**
 * Created by Stan on 12/07/2014.
 */
public class MonthlyExpenses {

    private String month;
    private double totalAmount;

    public MonthlyExpenses(String month) {
        this.month = month;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotal_amount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void increaseTotalAmount(double amount) {
        this.totalAmount += amount;
    }


}
