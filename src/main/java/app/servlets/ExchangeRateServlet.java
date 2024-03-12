package app.servlets;

import app.dao.ExchangeRateDAO;
import app.dao.impl.ExchangeRateDAOImpl;
import app.entities.ExchangeRate;
import app.utils.ExchangeRateUtils;
import app.utils.Utils;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Locale;
import java.util.Optional;

@WebServlet(name = "ExchangeRateServlet", value = "/exchangeRate/*")
public class ExchangeRateServlet extends HttpServlet {
    private ExchangeRateDAO<ExchangeRate> exchangeRateDAO;

    @Override
    public void init(ServletConfig config) {
        this.exchangeRateDAO = new ExchangeRateDAOImpl();
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getMethod().equals("PATCH")) {
            doPatch(req, resp);
        } else {
            super.service(req, resp);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String codes = req.getPathInfo().toUpperCase(Locale.ROOT).replaceFirst("/", "");
        if (!ExchangeRateUtils.isValid(codes)) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Коды валют пары отсутствуют в адресе");
            return;
        }
        String baseCurrencyCode = codes.substring(0, 3);
        String targetCurrencyCode = codes.substring(3, 6);
        Optional<ExchangeRate> exchangeRate = exchangeRateDAO.findByCurrencyCodes(baseCurrencyCode, targetCurrencyCode);
        if (!exchangeRate.isPresent()) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Обменный курс для пары не найден");
            return;
        }
        resp.setStatus(HttpServletResponse.SC_OK);
        Utils.writeValue(resp, exchangeRate.get());
    }

    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String rate = req.getParameter("rate");
        String codes = req.getPathInfo().toUpperCase(Locale.ROOT).replaceFirst("/", "");
        if (!ExchangeRateUtils.isValid(codes, rate)) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Отсутствует нужное поле формы");
            return;
        }
        String baseCurrencyCode = codes.substring(0, 3);
        String targetCurrencyCode = codes.substring(3, 6);
        Optional<ExchangeRate> exchangeRate = exchangeRateDAO.findByCurrencyCodes(baseCurrencyCode, targetCurrencyCode);
        if (!exchangeRate.isPresent()) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Валютная пара отсутствует в базе данных");
            return;
        }
        exchangeRate.get().setRate(new BigDecimal(rate));
        int affectedRows = exchangeRateDAO.update(exchangeRate.get());
        if (affectedRows < 1) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "База данных недоступна");
            return;
        }
        resp.setStatus(HttpServletResponse.SC_OK);
        doGet(req, resp);
    }
}
