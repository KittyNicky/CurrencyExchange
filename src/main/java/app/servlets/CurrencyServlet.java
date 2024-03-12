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

import java.util.Locale;
import java.util.Optional;

@WebServlet(name = "CurrencyServlet", value = "/currency/*")
public class CurrencyServlet extends HttpServlet {
    private CurrencyDAO<Currency> currencyDAO;

    @Override
    public void init(ServletConfig config) {
        currencyDAO = new CurrencyDAOImpl();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String code = req.getPathInfo().toUpperCase(Locale.ROOT).replaceFirst("/", "");
        if (!CurrencyUtils.isValid(code)) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Код валюты отсутствует в адресе");
            return;
        }
        Optional<Currency> currency = currencyDAO.findByCode(code);
        if (!currency.isPresent()) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Валюта не найдена");
            return;
        }
        resp.setStatus(HttpServletResponse.SC_OK);
        Utils.writeValue(resp, currency.get());
    }
}
