package dao;

import java.sql.*;
import utils.DBConnection;

/**
 * DAO responsable de l'authentification des utilisateurs.
 */
public class UtilisateurDAO {

    /**
     * Vérifie le login et le mot de passe et retourne le rôle.
     */
    public String getRole(String login, String pwd) {
        try (Connection c = DBConnection.getConnection()) {

            // Vérification pharmacien
            String sqlPh = "SELECT * FROM pharmacien WHERE login=? AND pwd=?";
            try (PreparedStatement ps = c.prepareStatement(sqlPh)) {
                ps.setString(1, login);
                ps.setString(2, pwd);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return "PHARMACIEN";
                    }
                }
            }

            // Vérification gestionnaire
            String sqlGest = "SELECT * FROM gestionnaire WHERE login=? AND pwd=?";
            try (PreparedStatement ps = c.prepareStatement(sqlGest)) {
                ps.setString(1, login);
                ps.setString(2, pwd);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return "GESTIONNAIRE";
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "ECHEC";
    }
}