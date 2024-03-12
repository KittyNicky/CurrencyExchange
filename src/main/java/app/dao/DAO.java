package app.dao;

import java.util.List;
import java.util.Optional;

public interface DAO<T> {
    List<T> findAll();

    Optional<T> findById(int id);

    int add(T entity);

    int update(T entity);

    int delete(T entity);
}
