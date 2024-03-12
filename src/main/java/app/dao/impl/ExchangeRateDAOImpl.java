package app.dao.impl;

import app.connection.DBConnection;
import app.dao.ExchangeRateDAO;
import app.entities.Currency;
import app.entities.ExchangeRate;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExchangeRateDAOImpl implements ExchangeRateDAO<ExchangeRate> {

    @Override
    public List<ExchangeRate> findAll() {
        final String query = "SELECT ER.id AS id, " +
                "BC.id               AS base_currency_id, " +
                "BC.name             AS base_name, " +
                "BC.code             AS base_code, " +
                "BC.sign             AS base_sign, " +
                "TC.id               AS target_currency_id, " +
                "TC.name             AS target_name, " +
                "TC.code             AS target_code, " +
                "TC.sign             AS target_sign, " +
                "ER.rate             AS rate " +
                "FROM public.exchange_rates AS ER " +
                "LEFT JOIN public.currency AS BC ON BC.id = ER.base_currency_id " +
                "LEFT JOIN public.currency AS TC ON TC.id = ER.target_currency_id;";
        ArrayList<ExchangeRate> exchangeRates = new ArrayList<>();
        try (Connection connection = DBConnection.connect();
             PreparedStatement readStatement = connection.prepareStatement(query)) {
            ResultSet resultSet = readStatement.executeQuery();
            while (resultSet.next()) {
                exchangeRates.add(getExchangeRate(resultSet));
            }
            return exchangeRates;
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<ExchangeRate> findById(int id) {
        final String query = "SELECT ER.id AS id, " +
                "BC.id               AS base_currency_id, " +
                "BC.name             AS base_name, " +
                "BC.code             AS base_code, " +
                "BC.sign             AS base_sign, " +
                "TC.id               AS target_currency_id, " +
                "TC.name             AS target_name, " +
                "TC.code             AS target_code, " +
                "TC.sign             AS target_sign, " +
                "ER.rate             AS rate " +
                "FROM public.exchange_rates AS ER " +
                "LEFT JOIN public.currency AS BC ON BC.id = ER.base_currency_id " +
                "LEFT JOIN public.currency AS TC ON TC.id = ER.target_currency_id " +
                "WHERE ER.id = ?;";
        try (Connection connection = DBConnection.connect();
             PreparedStatement readStatement = connection.prepareStatement(query)) {
            readStatement.setInt(1, id);
            ResultSet resultSet = readStatement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(getExchangeRate(resultSet));
            }
            return Optional.empty();
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int add(ExchangeRate exchangeRate) {
        final String query = "INSERT INTO public.exchange_rates (base_currency_id, target_currency_id, rate) VALUES (?, ?, ?)";
        try (Connection connection = DBConnection.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setInt(1, exchangeRate.getBaseCurrency().getId());
            preparedStatement.setInt(2, exchangeRate.getTargetCurrency().getId());
            preparedStatement.setBigDecimal(3, exchangeRate.getRate());
            int insertedRow = preparedStatement.executeUpdate();
            if (insertedRow > 0) {
                ResultSet resultSet = preparedStatement.getGeneratedKeys();
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
    public int update(ExchangeRate exchangeRate) {
        final String query = "UPDATE public.exchange_rates " +
                "SET base_currency_id = ?, " +
                "    target_currency_id = ?, " +
                "    rate = ? " +
                "WHERE id = ?;";
        try (Connection connection = DBConnection.connect();
             PreparedStatement updateStatement = connection.prepareStatement(query)) {
            updateStatement.setInt(1, exchangeRate.getBaseCurrency().getId());
            updateStatement.setInt(2, exchangeRate.getTargetCurrency().getId());
            updateStatement.setBigDecimal(3, exchangeRate.getRate());
            updateStatement.setLong(4, exchangeRate.getId());
            return updateStatement.executeUpdate();
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int delete(ExchangeRate exchangeRate) {
        final String query = "DELETE FROM public.exchange_rates " +
                "WHERE base_currency_id = ? " +
                "AND target_currency_id = ?;";
        try (Connection connection = DBConnection.connect();
             PreparedStatement deleteStatement = connection.prepareStatement(query)) {
            deleteStatement.setInt(1, exchangeRate.getBaseCurrency().getId());
            deleteStatement.setInt(2, exchangeRate.getTargetCurrency().getId());
            return deleteStatement.executeUpdate();
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<ExchangeRate> findByCurrencyCodes(String baseCurrencyCode, String targetCurrencyCode) {
        final String query = "SELECT ER.id AS id, " +
                "BC.id               AS base_currency_id, " +
                "BC.name             AS base_name, " +
                "BC.code             AS base_code, " +
                "BC.sign             AS base_sign, " +
                "TC.id               AS target_currency_id, " +
                "TC.name             AS target_name, " +
                "TC.code             AS target_code, " +
                "TC.sign             AS target_sign, " +
                "ER.rate             AS rate " +
                "FROM public.exchange_rates AS ER " +
                "LEFT JOIN public.currency AS BC ON BC.id = ER.base_currency_id " +
                "LEFT JOIN public.currency AS TC ON TC.id = ER.target_currency_id " +
                "WHERE BC.code = ? AND TC.code = ?;";
        try (Connection connection = DBConnection.connect();
             PreparedStatement readStatement = connection.prepareStatement(query)) {
            readStatement.setString(1, baseCurrencyCode);
            readStatement.setString(2, targetCurrencyCode);
            ResultSet resultSet = readStatement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(getExchangeRate(resultSet));
            }
            return Optional.empty();
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private ExchangeRate getExchangeRate(ResultSet resultSet) throws SQLException {
        return new ExchangeRate(
                resultSet.getInt("id"),
                new Currency(
                        resultSet.getInt("base_currency_id"),
                        resultSet.getString("base_name"),
                        resultSet.getString("base_code"),
                        resultSet.getString("base_sign")),
                new Currency(
                        resultSet.getInt("target_currency_id"),
                        resultSet.getString("target_name"),
                        resultSet.getString("target_code"),
                        resultSet.getString("target_sign")),
                resultSet.getBigDecimal("rate")
        );
    }
}
