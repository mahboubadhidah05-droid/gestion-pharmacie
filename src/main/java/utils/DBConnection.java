package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DBConnection {

    private static final String URL =
            System.getenv().getOrDefault(
                    "DB_URL",
                    "jdbc:mysql://mysql-container:3306/pharmacie"
            );

    private static final String USER =
            System.getenv().getOrDefault(
                    "DB_USER",
                    "root"
            );

    private static final String PASSWORD =
            System.getenv("DB_PASSWORD");


    private DBConnection() {
        throw new IllegalStateException("Utility class");
    }


    public static Connection getConnection() throws SQLException {

        validatePassword(PASSWORD);

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

        } catch (ClassNotFoundException e) {

            throw new SQLException(
                    "Driver MySQL introuvable",
                    e
            );
        }

        return DriverManager.getConnection(
                URL,
                USER,
                PASSWORD
        );
    }


    static void validatePassword(String password)
            throws SQLException {

        if (password == null || password.isBlank()) {

            throw new SQLException(
                    "Database password not configured"
            );
        }
    }
}