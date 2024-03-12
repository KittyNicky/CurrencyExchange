package app.utils;

import app.entities.Currency;

public class CurrencyUtils {
    public static boolean isValid(String fullName, String code, String sign) {
        if (fullName == null || fullName.isEmpty() || fullName.length() > Currency.NAME_LENGTH) return false;
        if (code == null || code.isEmpty() || code.length() > Currency.CODE_LENGTH) return false;
        if (sign != null && sign.length() > Currency.SIGN_LENGTH) return false;
        return true;
    }

    public static boolean isValid(String code) {
        return code != null && code.length() == Currency.CODE_LENGTH;
    }
}
