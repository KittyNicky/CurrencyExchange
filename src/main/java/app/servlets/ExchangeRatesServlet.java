package app.servlets;

import app.dao.CurrencyDAO;
import app.dao.ExchangeRateDAO;
import app.dao.impl.CurrencyDAOImpl;
import app.dao.impl.ExchangeRateDAOImpl;
import app.entities.Currency;
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
import java.util.List;
import java.util.Optional;

@WebServlet(name = "ExchangeRatesServlet", value = "/exchangeRates")
public class ExchangeRatesServlet extends HttpServlet {
    private ExchangeRateDAO<ExchangeRate> exchangeRateDAO;
    private CurrencyDAO<Currency> currencyDAO;

    @Override
    public void init(ServletConfig config) {
        this.exchangeRateDAO = new ExchangeRateDAOImpl();
        this.currencyDAO = new CurrencyDAOImpl();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        List<ExchangeRate> exchangeRates = exchangeRateDAO.findAll();
        if (exchangeRates.isEmpty()) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "База данных недоступна");
            return;
        }
        resp.setStatus(HttpServletResponse.SC_OK);
        Utils.writeValue(resp, exchangeRates);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String baseCurrencyCode = req.getParameter("baseCurrencyCode");
        String targetCurrencyCode = req.getParameter("targetCurrencyCode");
        String rate = req.getParameter("rate");
        if (!ExchangeRateUtils.isValid(baseCurrencyCode, targetCurrencyCode, rate)) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Отсутствует нужное поле формы");
            return;
        }
        Optional<Currency> baseCurrency = currencyDAO.findByCode(baseCurrencyCode);
        Optional<Currency> targetCurrency = currencyDAO.findByCode(targetCurrencyCode);
        if (!baseCurrency.isPresent() || !targetCurrency.isPresent()) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Одна (или обе) валюта из валютной пары не существуют в базе данных");
            return;
        }
        if (exchangeRateDAO.findByCurrencyCodes(baseCurrencyCode, targetCurrencyCode).isPresent()) {
            resp.sendError(HttpServletResponse.SC_CONFLICT, "Валютная пара с таким кодом уже существует");
            return;
        }
        ExchangeRate exchangeRate = new ExchangeRate(baseCurrency.get(), targetCurrency.get(), new BigDecimal(rate));
        int id = exchangeRateDAO.add(exchangeRate);
        if (id == -1) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "База данных недоступна");
            return;
        }
        resp.setStatus(HttpServletResponse.SC_CREATED);
        Utils.writeValue(resp, exchangeRateDAO.findById(id).get());
    }
}
