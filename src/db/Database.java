package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {

    private static final String DB_PATH = "db/dev.db";

    private static final String URL = "jdbc:sqlite:" + DB_PATH;

    public static Connection getConnection() throws SQLException {
        Connection c = DriverManager.getConnection(URL);
        try (Statement st = c.createStatement()) {
            st.execute("PRAGMA foreign_keys = ON");
        }
        return c;
    }
}
