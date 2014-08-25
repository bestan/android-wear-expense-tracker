package lv.bestan.android.wear.expensestracker.utils;

import java.util.Currency;
import java.util.Locale;

/**
 * Created by Stan on 25/08/2014.
 */
public class CurrencyUtils {

    public static String getCurrencySymbol() {
        try {
            Currency currency = Currency.getInstance(Locale.getDefault());
            String symbol = currency.getSymbol();
            if (symbol != null) {
                return symbol;
            } else {
                return "";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
