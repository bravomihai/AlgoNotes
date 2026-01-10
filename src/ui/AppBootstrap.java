package ui;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class AppBootstrap {

    private static final String APP_NAME = "AlgoNotes";

    public static Path getAppDir() {
        return Paths.get(
                System.getProperty("user.home"),
                "AppData",
                "Local",
                APP_NAME
        );
    }

    public static Path getDatabasePath() {
        return getAppDir().resolve("algonotes.db");
    }

    public static void init() {
        try {
            Path appDir = getAppDir();

            Files.createDirectories(appDir);

            Path dbPath = getDatabasePath();

            if (!Files.exists(dbPath)) {
                createDatabase(dbPath);
                applySchema(dbPath);
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize application", e);
        }
    }

    private static void createDatabase(Path dbPath) throws Exception {
        String url = "jdbc:sqlite:" + dbPath.toAbsolutePath();

        try (var conn = java.sql.DriverManager.getConnection(url)) {
        }

    }

    private static void applySchema(Path dbPath) throws Exception {
        String url = "jdbc:sqlite:" + dbPath.toAbsolutePath();

        try (var conn = java.sql.DriverManager.getConnection(url);
             var stmt = conn.createStatement();
             var in = AppBootstrap.class.getResourceAsStream("/schema.sql")) {

            if (in == null) {
                throw new RuntimeException("schema.sql not found in resources");
            }

            stmt.execute("PRAGMA foreign_keys = ON");

            String sql = new String(in.readAllBytes());

            // split statements safely
            for (String s : sql.split(";")) {
                String statement = s.trim();
                if (!statement.isEmpty()) {
                    stmt.execute(statement);
                }
            }
        }
    }



}
