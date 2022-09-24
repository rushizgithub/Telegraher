package org.telegram.messenger;

import android.content.Context;
import com.google.android.exoplayer2.util.Util;
import org.json.JSONObject;
import java.io.InputStream;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class BillingController {
    private static BillingController instance;

    private Map<String, Integer> currencyExpMap = new HashMap<>();

    public static BillingController getInstance() {
        if (instance == null) {
            instance = new BillingController(ApplicationLoader.applicationContext);
        }
        return instance;
    }

    private BillingController(Context ctx) {
    }

    public String formatCurrency(long amount, String currency) {
        return formatCurrency(amount, currency, getCurrencyExp(currency));
    }

    public String formatCurrency(long amount, String currency, int exp) {
        if (currency.isEmpty()) {
            return String.valueOf(amount);
        }
        Currency cur = Currency.getInstance(currency);
        if (cur != null) {
            NumberFormat numberFormat = NumberFormat.getCurrencyInstance();
            numberFormat.setCurrency(cur);

            return numberFormat.format(amount / Math.pow(10, exp));
        }
        return amount + " " + currency;
    }

    public int getCurrencyExp(String currency) {
        Integer exp = currencyExpMap.get(currency);
        if (exp == null) {
            return 0;
        }
        return exp;
    }

    public void startConnection() {
        try {
            Context ctx = ApplicationLoader.applicationContext;
            InputStream in = ctx.getAssets().open("currencies.json");
            JSONObject obj = new JSONObject(new String(Util.toByteArray(in), "UTF-8"));
            parseCurrencies(obj);
            in.close();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    private void parseCurrencies(JSONObject obj) {
        Iterator<String> it = obj.keys();
        while (it.hasNext()) {
            String key = it.next();
            JSONObject currency = obj.optJSONObject(key);
            currencyExpMap.put(key, currency.optInt("exp"));
        }
    }
}
