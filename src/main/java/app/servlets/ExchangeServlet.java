package app.servlets;

import app.dao.ExchangeRateDAO;
import app.dao.impl.ExchangeRateDAOImpl;
import app.dto.Exchange;
import app.entities.ExchangeRate;
import app.utils.ExchangeRateUtils;
import app.utils.Utils;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

@WebServlet(name = "ExchangeServlet", value = "/exchange")
public class ExchangeServlet extends HttpServlet {
    private ExchangeRateDAO<ExchangeRate> exchangeRateDAO;

    @Override
    public void init(ServletConfig config) {
        this.exchangeRateDAO = new ExchangeRateDAOImpl();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String baseCurrencyCode = req.getParameter("from");
        String targetCurrencyCode = req.getParameter("to");
        String amount = req.getParameter("amount");
        if (!ExchangeRateUtils.isValid(baseCurrencyCode, targetCurrencyCode, amount)) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Отсутствует нужное поле формы");
            return;
        }
        Optional<Exchange> exchange = getExchange(baseCurrencyCode, targetCurrencyCode, new BigDecimal(amount));
        if (!exchange.isPresent()) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Валюты не найдены");
            return;
        }
        resp.setStatus(HttpServletResponse.SC_OK);
        Utils.writeValue(resp, exchange.get());
    }

    private Optional<Exchange> getExchange(String baseCurrencyCode, String targetCurrencyCode, BigDecimal amount) {
        Optional<Exchange> exchange;
        exchange = getDirectExchange(baseCurrencyCode, targetCurrencyCode, amount);
        if (exchange.isPresent()) return exchange;

        exchange = getReverseExchange(baseCurrencyCode, targetCurrencyCode, amount);
        if (exchange.isPresent()) return exchange;

        exchange = getExchangeThroughCommonCurrency(baseCurrencyCode, targetCurrencyCode, amount);
        return exchange;
    }

    private Optional<Exchange> getDirectExchange(String baseCurrencyCode, String targetCurrencyCode, BigDecimal amount) {
        BigDecimal rate, convertedAmount;
        Optional<ExchangeRate> directExchangeRate = exchangeRateDAO.findByCurrencyCodes(baseCurrencyCode, targetCurrencyCode);
        if (directExchangeRate.isPresent()) {
            rate = directExchangeRate.get().getRate();
            convertedAmount = amount.multiply(rate).setScale(2, RoundingMode.HALF_DOWN);
            return Optional.of(
                    new Exchange(
                            directExchangeRate.get().getBaseCurrency(),
                            directExchangeRate.get().getTargetCurrency(),
                            rate,
                            amount,
                            convertedAmount)
            );
        }
        return Optional.empty();
    }

    private Optional<Exchange> getReverseExchange(String baseCurrencyCode, String targetCurrencyCode, BigDecimal amount) {
        BigDecimal rate, convertedAmount;
        Optional<ExchangeRate> reverseExchangeRate = exchangeRateDAO.findByCurrencyCodes(targetCurrencyCode, baseCurrencyCode);
        if (reverseExchangeRate.isPresent()) {
            rate = new BigDecimal(1).divide(reverseExchangeRate.get().getRate(), 4, RoundingMode.HALF_DOWN);
            convertedAmount = amount.multiply(rate).setScale(2, RoundingMode.HALF_DOWN);
            return Optional.of(
                    new Exchange(
                            reverseExchangeRate.get().getTargetCurrency(),
                            reverseExchangeRate.get().getBaseCurrency(),
                            rate,
                            amount,
                            convertedAmount)
            );
        }
        return Optional.empty();
    }

    private Optional<Exchange> getExchangeThroughCommonCurrency(String baseCurrencyCode, String targetCurrencyCode, BigDecimal amount) {
        BigDecimal rate, convertedAmount;
        Optional<ExchangeRate> baseToRUBExchangeRate = exchangeRateDAO.findByCurrencyCodes(baseCurrencyCode, "RUB");
        Optional<ExchangeRate> targetToRUBExchangeRate = exchangeRateDAO.findByCurrencyCodes(targetCurrencyCode, "RUB");
        if (baseToRUBExchangeRate.isPresent() && targetToRUBExchangeRate.isPresent()) {
            rate = baseToRUBExchangeRate.get().getRate()
                    .divide(targetToRUBExchangeRate.get().getRate(), 4, RoundingMode.HALF_DOWN);
            convertedAmount = amount.multiply(rate).setScale(2, RoundingMode.HALF_DOWN);
            return Optional.of(
                    new Exchange(
                            baseToRUBExchangeRate.get().getBaseCurrency(),
                            targetToRUBExchangeRate.get().getBaseCurrency(),
                            rate,
                            amount,
                            convertedAmount)
            );
        }
        return Optional.empty();
    }
}
