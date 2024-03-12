package app.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    public static Connection connect() throws SQLException, ClassNotFoundException {
        Class.forName(DBConfig.getDriver());
        return DriverManager.getConnection(
                DBConfig.getURL(),
                DBConfig.getUsername(),
                DBConfig.getPassword());
    }
}