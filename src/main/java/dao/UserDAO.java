package dao;

import java.sql.*;
import utils.DBConnection;

public class UserDAO {

    /**
     * Récupère les informations de l'utilisateur selon le login.
     * Retourne un tableau de String : [nom, prenom]
     */
    public String[] getProfil(String login) {
        try (Connection c = DBConnection.getConnection()) {

            // Vérifier si l'utilisateur est Pharmacien
            String sqlPh = "SELECT nom, prenom FROM Pharmacien WHERE login=?";
            try (PreparedStatement ps = c.prepareStatement(sqlPh)) {
                ps.setString(1, login);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return new String[]{
                                rs.getString("nom"),
                                rs.getString("prenom")
                        };
                    }
                }
            }

            // Vérifier si l'utilisateur est Gestionnaire
            String sqlGest = "SELECT nom, prenom FROM Gestionnaire WHERE login=?";
            try (PreparedStatement ps = c.prepareStatement(sqlGest)) {
                ps.setString(1, login);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return new String[]{
                                rs.getString("nom"),
                                rs.getString("prenom")
                        };
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // si login inconnu
    }
}