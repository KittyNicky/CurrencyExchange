package app.dao.impl;

import app.connection.DBConnection;
import app.dao.CurrencyDAO;
import app.entities.Currency;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CurrencyDAOImpl implements CurrencyDAO<Currency> {

    @Override
    public List<Currency> findAll() {
        final String query = "SELECT * FROM public.currency;";
        ArrayList<Currency> currencies = new ArrayList<>();
        try (Connection connection = DBConnection.connect();
             PreparedStatement readStatement = connection.prepareStatement(query)) {
            ResultSet resultSet = readStatement.executeQuery();
            while (resultSet.next()) {
                currencies.add(getCurrency(resultSet));
            }
            return currencies;
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Currency> findById(int id) {
        final String query = "SELECT * FROM public.currency WHERE id = ?;";
        try (Connection connection = DBConnection.connect();
             PreparedStatement readStatement = connection.prepareStatement(query)) {
            readStatement.setInt(1, id);
            ResultSet resultSet = readStatement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(getCurrency(resultSet));
            }
            return Optional.empty();
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int add(Currency currency) {
        final String query = "INSERT INTO public.currency (name, code, sign) VALUES (?, ?, ?);";
        try (Connection connection = DBConnection.connect();
             PreparedStatement insertStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            insertStatement.setString(1, currency.getName());
            insertStatement.setString(2, currency.getCode());
            insertStatement.setString(3, currency.getSign());
            int insertedRow = insertStatement.executeUpdate();
            if (insertedRow > 0) {
                ResultSet resultSet = insertStatement.getGeneratedKeys();
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return -1;
    }


    @Override
    public int update(Currency currency) {
        final String query = "UPDATE public.currency " +
                "SET name = ?, " +
                "    sign = ? " +
                "WHERE code = ?;";
        try (Connection connection = DBConnection.connect();
             PreparedStatement updateStatement = connection.prepareStatement(query)) {
            updateStatement.setString(1, currency.getName());
            updateStatement.setString(2, currency.getSign());
            updateStatement.setString(3, currency.getCode());
            return updateStatement.executeUpdate();
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int delete(Currency currency) {
        final String query = "DELETE FROM public.currency " +
                "WHERE code = ?;";
        try (Connection connection = DBConnection.connect();
             PreparedStatement deleteStatement = connection.prepareStatement(query)) {
            deleteStatement.setString(1, currency.getCode());
            return deleteStatement.executeUpdate();
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Currency> findByCode(String code) {
        final String query = "SELECT * FROM public.currency WHERE code = ?;";
        try (Connection connection = DBConnection.connect();
             PreparedStatement readStatement = connection.prepareStatement(query)) {
            readStatement.setString(1, code);
            ResultSet resultSet = readStatement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(getCurrency(resultSet));
            }
            return Optional.empty();
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private Currency getCurrency(ResultSet resultSet) throws SQLException {
        return new Currency(
                resultSet.getInt("id"),
                resultSet.getString("name"),
                resultSet.getString("code"),
                resultSet.getString("sign")
        );
    }
}