package app.utils;

import app.entities.Currency;

import java.math.BigDecimal;

public class ExchangeRateUtils {
    public static boolean isValid(String baseCurrencyCode, String targetCurrencyCode, String rate) {
        if (rate == null || rate.isEmpty()) return false;
        if (new BigDecimal(rate).signum() == -1) return false;
        if (baseCurrencyCode == null || baseCurrencyCode.length() != Currency.CODE_LENGTH) return false;
        if (targetCurrencyCode == null || targetCurrencyCode.length() != Currency.CODE_LENGTH) return false;
        return true;
    }

    public static boolean isValid(String codes) {
        return codes != null && codes.length() == Currency.CODE_LENGTH * 2;
    }

    public static boolean isValid(String codes, String rate) {
        if (codes == null || codes.length() != Currency.CODE_LENGTH * 2) return false;
        if (rate == null || rate.isEmpty()) return false;
        return true;
    }
}
