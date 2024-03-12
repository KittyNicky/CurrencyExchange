package app.dao;

import java.util.Optional;

public interface CurrencyDAO<T> extends DAO<T> {
    Optional<T> findByCode(String code);
}
