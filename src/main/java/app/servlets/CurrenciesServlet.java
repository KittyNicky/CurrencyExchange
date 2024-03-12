package app.servlets;

import app.dao.CurrencyDAO;
import app.dao.impl.CurrencyDAOImpl;
import app.entities.Currency;
import app.utils.CurrencyUtils;
import app.utils.Utils;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "CurrenciesServlet", value = "/currencies")
public class CurrenciesServlet extends HttpServlet {
    private CurrencyDAO<Currency> currencyDAO;

    @Override
    public void init(ServletConfig config) {
        currencyDAO = new CurrencyDAOImpl();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        List<Currency> currencies = currencyDAO.findAll();
        if (currencies.isEmpty()) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "База данных недоступна");
            return;
        }
        resp.setStatus(HttpServletResponse.SC_OK);
        Utils.writeValue(resp, currencies);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String name = req.getParameter("name");
        String code = req.getParameter("code");
        String sign = req.getParameter("sign");
        if (!CurrencyUtils.isValid(name, code, sign)) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Отсутствует нужное поле формы");
            return;
        }
        if (currencyDAO.findByCode(code).isPresent()) {
            resp.sendError(HttpServletResponse.SC_CONFLICT, "Валюта с таким кодом уже существует");
            return;
        }
        Currency currency = new Currency(name, code, sign);
        int id = currencyDAO.add(currency);
        if (id == -1) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "База данных недоступна");
            return;
        }
        resp.setStatus(HttpServletResponse.SC_CREATED);
        Utils.writeValue(resp, currencyDAO.findById(id).get());
    }
}