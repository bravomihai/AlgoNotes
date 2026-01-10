package db;

import ui.AppBootstrap;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {

    private static String getUrl() {
        return "jdbc:sqlite:" +
                AppBootstrap.getDatabasePath().toAbsolutePath();
    }

    public static Connection getConnection() throws SQLException {
        Connection c = DriverManager.getConnection(getUrl());
        try (Statement st = c.createStatement()) {
            st.execute("PRAGMA foreign_keys = ON");
        }
        return c;
    }
}
