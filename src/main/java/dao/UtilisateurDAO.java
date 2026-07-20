package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import exception.AccesDonneesException;
import utils.DBConnection;

/**
 * DAO responsable de l'authentification des utilisateurs.
 */
public class UtilisateurDAO {

    private static final Logger LOGGER =
            Logger.getLogger(UtilisateurDAO.class.getName());

    private static final String ROLE_PHARMACIEN =
            "PHARMACIEN";

    private static final String ROLE_GESTIONNAIRE =
            "GESTIONNAIRE";

    private static final String ROLE_ECHEC =
            "ECHEC";

    private static final String SQL_PHARMACIEN =
            "SELECT * FROM pharmacien WHERE login=? AND pwd=?";

    private static final String SQL_GESTIONNAIRE =
            "SELECT * FROM gestionnaire WHERE login=? AND pwd=?";


    /**
     * Vérifie le login et le mot de passe et retourne le rôle,
     * ou ROLE_ECHEC si les identifiants sont invalides.
     */
    public String getRole(String login, String pwd) {

        try (Connection connection =
                     DBConnection.getConnection()) {


            if (verifierUtilisateur(
                    connection,
                    SQL_PHARMACIEN,
                    login,
                    pwd)) {

                return ROLE_PHARMACIEN;
            }


            if (verifierUtilisateur(
                    connection,
                    SQL_GESTIONNAIRE,
                    login,
                    pwd)) {

                return ROLE_GESTIONNAIRE;
            }


        } catch (SQLException e) {

            LOGGER.log(
                    Level.SEVERE,
                    "Erreur lors de la verification du role",
                    e
            );

            throw new AccesDonneesException(
                    "Échec de la vérification des identifiants",
                    e
            );
        }


        return ROLE_ECHEC;
    }


    private boolean verifierUtilisateur(
            Connection connection,
            String sql,
            String login,
            String pwd)
            throws SQLException {


        try (PreparedStatement ps =
                     connection.prepareStatement(sql)) {


            ps.setString(1, login);
            ps.setString(2, pwd);


            try (ResultSet rs =
                         ps.executeQuery()) {


                return rs.next();
            }
        }
    }
}