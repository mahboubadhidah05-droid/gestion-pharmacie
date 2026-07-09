package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import utils.DBConnection;

public class UserDAO {

    private static final Logger LOGGER = Logger.getLogger(UserDAO.class.getName());

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

            LOGGER.log(
                    Level.SEVERE,
                    "Erreur lors de la récupération du profil utilisateur",
                    e
            );
        }

        return new String[0];
    }
}