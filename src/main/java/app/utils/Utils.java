package app.utils;

import app.dao.DAO;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

public class Utils {
    public static void writeValue(HttpServletResponse response, Object object) throws IOException {
        PrintWriter printWriter = response.getWriter();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValue(printWriter, object);
    }
}
