package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Classe utilitaire responsable de la connexion à la base de données MySQL
 * via JDBC.
 */
public class DBConnection {

    // URL de la base de données
    private static final String URL = "jdbc:mysql://127.0.0.1:3306/pharmacie";
    // Nom d'utilisateur MySQL
    private static final String USER = "root";
    // Mot de passe MySQL
    private static final String PASSWORD = "douaa";

    /**
     * Méthode qui retourne une connexion JDBC prête à l'emploi.
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
   