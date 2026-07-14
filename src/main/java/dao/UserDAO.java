package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import utils.DBConnection;

public class UserDAO {

    private static final Logger LOGGER =
            Logger.getLogger(UserDAO.class.getName());

    private static final String NOM_COLUMN = "nom";
    private static final String PRENOM_COLUMN = "prenom";

    private static final String SQL_PHARMACIEN =
            "SELECT nom, prenom FROM Pharmacien WHERE login=?";

    private static final String SQL_GESTIONNAIRE =
            "SELECT nom, prenom FROM Gestionnaire WHERE login=?";


    public String[] getProfil(String login) {

        try (Connection connection =
                     DBConnection.getConnection()) {


            String[] profil =
                    rechercherProfil(
                            connection,
                            SQL_PHARMACIEN,
                            login
                    );


            if (profil.length > 0) {
                return profil;
            }


            return rechercherProfil(
                    connection,
                    SQL_GESTIONNAIRE,
                    login
            );


        } catch (SQLException e) {

            LOGGER.log(
                    Level.SEVERE,
                    "Erreur lors de la récupération du profil utilisateur",
                    e
            );
        }


        return new String[0];
    }


    private String[] rechercherProfil(
            Connection connection,
            String sql,
            String login)
            throws SQLException {


        try (PreparedStatement ps =
                     connection.prepareStatement(sql)) {


            ps.setString(1, login);


            try (ResultSet rs =
                         ps.executeQuery()) {


                if (rs.next()) {

                    return new String[]{
                            rs.getString(NOM_COLUMN),
                            rs.getString(PRENOM_COLUMN)
                    };
                }
            }
        }


        return new String[0];
    }
}