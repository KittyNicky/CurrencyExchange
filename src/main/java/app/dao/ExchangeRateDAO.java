package app.dao;

import java.util.Optional;

public interface ExchangeRateDAO<T> extends DAO<T> {
    Optional<T> findByCurrencyCodes(String baseCurrencyCode, String targetCurrencyCode);
}
