package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import exception.AccesDonneesException;
import utils.DBConnection;

public class UserDAO {

    private static final String NOM_COLUMN =
            "nom";

    private static final String PRENOM_COLUMN =
            "prenom";

    private static final String EMAIL_COLUMN =
            "email";

    private static final String SQL_PHARMACIEN =
            "SELECT nom, prenom "
                    + "FROM pharmacien "
                    + "WHERE login=?";

    private static final String SQL_GESTIONNAIRE =
            "SELECT nom, prenom "
                    + "FROM gestionnaire "
                    + "WHERE login=?";

    private static final String SQL_EMAILS_GESTIONNAIRES =
            "SELECT email "
                    + "FROM gestionnaire "
                    + "WHERE email IS NOT NULL";

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

        } catch (SQLException exception) {

            throw new AccesDonneesException(
                    "Échec de la récupération du profil "
                            + "pour l'utilisateur : "
                            + login,
                    exception
            );
        }
    }

    public List<String> getEmailsGestionnaires() {

        List<String> emails = new ArrayList<>();

        try (Connection connection =
                     DBConnection.getConnection();
             PreparedStatement ps =
                     connection.prepareStatement(
                             SQL_EMAILS_GESTIONNAIRES
                     );
             ResultSet rs =
                     ps.executeQuery()) {

            while (rs.next()) {

                String email =
                        rs.getString(EMAIL_COLUMN);

                if (email != null && !email.isBlank()) {
                    emails.add(email);
                }
            }

            return emails;

        } catch (SQLException exception) {

            throw new AccesDonneesException(
                    "Échec de la récupération des emails "
                            + "des gestionnaires",
                    exception
            );
        }
    }

    private String[] rechercherProfil(
            Connection connection,
            String sql,
            String login)
            throws SQLException {

        try (PreparedStatement ps =
                     connection.prepareStatement(sql)) {

            ps.setString(
                    1,
                    login
            );

            try (ResultSet rs =
                         ps.executeQuery()) {

                if (rs.next()) {

                    return new String[]{
                            rs.getString(
                                    NOM_COLUMN
                            ),
                            rs.getString(
                                    PRENOM_COLUMN
                            )
                    };
                }
            }
        }

        return new String[0];
    }
}