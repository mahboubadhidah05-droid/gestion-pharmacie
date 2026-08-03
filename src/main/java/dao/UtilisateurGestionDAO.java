package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import dto.MonProfilResponse;
import dto.UtilisateurGestionResponse;
import exception.AccesDonneesException;
import utils.DBConnection;

/**
 * DAO de gestion des comptes utilisateurs (pharmaciens et gestionnaires).
 * Séparé de {@link UtilisateurDAO}, qui reste dédié à l'authentification,
 * pour ne jamais risquer de perturber la logique de connexion existante.
 *
 * Sécurité SQL : le nom de table/colonne inséré dans les requêtes n'est
 * JAMAIS une valeur libre — il est toujours résolu via
 * {@link #resoudreTable(String)} / {@link #resoudreColonneId(String)},
 * qui valident explicitement le rôle contre une liste fermée de valeurs
 * connues (PHARMACIEN / GESTIONNAIRE) et rejettent tout le reste.
 * Toutes les données utilisateur (nom, login, etc.) restent, elles,
 * toujours passées en paramètres liés (PreparedStatement), jamais
 * concaténées.
 */
public class UtilisateurGestionDAO {

    private static final String ROLE_PHARMACIEN = "PHARMACIEN";
    private static final String ROLE_GESTIONNAIRE = "GESTIONNAIRE";

    private static final String TABLE_PHARMACIEN = "pharmacien";
    private static final String TABLE_GESTIONNAIRE = "gestionnaire";

    private static final String COLONNE_ID_PHARMACIEN = "id_pharmacien";
    private static final String COLONNE_ID_GESTIONNAIRE = "id_gestionnaire";

    private static final String SELECT_TOUS =
            "SELECT id_pharmacien AS id, nom, prenom, login, "
            + "'PHARMACIEN' AS role FROM pharmacien "
            + "UNION ALL "
            + "SELECT id_gestionnaire AS id, nom, prenom, login, "
            + "'GESTIONNAIRE' AS role FROM gestionnaire "
            + "ORDER BY role, nom";

    private static final String SELECT_LOGIN_EXISTE =
            "SELECT 1 FROM pharmacien WHERE login=? "
            + "UNION SELECT 1 FROM gestionnaire WHERE login=?";

    /**
     * Résout le nom de table associé à un rôle, en le validant contre
     * une liste fermée de valeurs connues — jamais une valeur libre
     * injectée directement dans le SQL.
     *
     * @throws IllegalArgumentException si le rôle n'est ni PHARMACIEN
     *         ni GESTIONNAIRE.
     */
    private String resoudreTable(String role) {

        if (ROLE_PHARMACIEN.equals(role)) {
            return TABLE_PHARMACIEN;
        }

        if (ROLE_GESTIONNAIRE.equals(role)) {
            return TABLE_GESTIONNAIRE;
        }

        throw new IllegalArgumentException(
                "Rôle invalide : " + role
        );
    }

    /**
     * Résout le nom de la colonne d'identifiant associée à un rôle,
     * avec la même validation stricte que {@link #resoudreTable}.
     */
    private String resoudreColonneId(String role) {

        if (ROLE_PHARMACIEN.equals(role)) {
            return COLONNE_ID_PHARMACIEN;
        }

        if (ROLE_GESTIONNAIRE.equals(role)) {
            return COLONNE_ID_GESTIONNAIRE;
        }

        throw new IllegalArgumentException(
                "Rôle invalide : " + role
        );
    }

    public List<UtilisateurGestionResponse> listerTous() {

        List<UtilisateurGestionResponse> utilisateurs =
                new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(SELECT_TOUS);
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {

                utilisateurs.add(
                        new UtilisateurGestionResponse(
                                result.getInt("id"),
                                result.getString("nom"),
                                result.getString("prenom"),
                                result.getString("login"),
                                result.getString("role")
                        )
                );
            }

        } catch (SQLException exception) {

            throw new AccesDonneesException(
                    "Échec de la récupération de la liste des utilisateurs",
                    exception
            );
        }

        return utilisateurs;
    }

    public boolean loginExiste(String login) {

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(SELECT_LOGIN_EXISTE)) {

            statement.setString(1, login);
            statement.setString(2, login);

            try (ResultSet result = statement.executeQuery()) {
                return result.next();
            }

        } catch (SQLException exception) {

            throw new AccesDonneesException(
                    "Échec de la vérification d'unicité du login : " + login,
                    exception
            );
        }
    }

    public int creerUtilisateur(
            String nom,
            String prenom,
            String login,
            String pwdHache,
            String role) {

        String table = resoudreTable(role);

        String sql =
                "INSERT INTO " + table
                + " (nom, prenom, login, pwd) VALUES (?,?,?,?)";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(
                             sql,
                             Statement.RETURN_GENERATED_KEYS
                     )) {

            statement.setString(1, nom);
            statement.setString(2, prenom);
            statement.setString(3, login);
            statement.setString(4, pwdHache);

            statement.executeUpdate();

            try (ResultSet cles = statement.getGeneratedKeys()) {

                if (cles.next()) {
                    return cles.getInt(1);
                }
            }

            return -1;

        } catch (SQLException exception) {

            throw new AccesDonneesException(
                    "Échec de la création de l'utilisateur : " + login,
                    exception
            );
        }
    }

    public boolean modifierUtilisateur(
            int id,
            String role,
            String nom,
            String prenom,
            String login,
            String pwdHache) {

        String table = resoudreTable(role);
        String colonneId = resoudreColonneId(role);

        boolean changerMotDePasse = pwdHache != null;

        String sql =
                "UPDATE " + table
                + " SET nom=?, prenom=?, login=?"
                + (changerMotDePasse ? ", pwd=?" : "")
                + " WHERE " + colonneId + "=?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            int index = 1;

            statement.setString(index++, nom);
            statement.setString(index++, prenom);
            statement.setString(index++, login);

            if (changerMotDePasse) {
                statement.setString(index++, pwdHache);
            }

            statement.setInt(index, id);

            return statement.executeUpdate() > 0;

        } catch (SQLException exception) {

            throw new AccesDonneesException(
                    "Échec de la modification de l'utilisateur ID=" + id,
                    exception
            );
        }
    }

    public boolean supprimerUtilisateur(int id, String role) {

        String table = resoudreTable(role);
        String colonneId = resoudreColonneId(role);

        String sql =
                "DELETE FROM " + table + " WHERE " + colonneId + "=?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, id);

            return statement.executeUpdate() > 0;

        } catch (SQLException exception) {

            throw new AccesDonneesException(
                    "Échec de la suppression de l'utilisateur ID=" + id,
                    exception
            );
        }
    }

    /**
     * Change le mot de passe d'un utilisateur en le retrouvant par son
     * login (pratique pour le changement de mot de passe en libre-service,
     * où l'on connaît le login de la session mais pas l'ID).
     */
    public boolean changerMotDePasseParLogin(
            String login,
            String role,
            String pwdHache) {

        String table = resoudreTable(role);

        String sql =
                "UPDATE " + table + " SET pwd=? WHERE login=?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, pwdHache);
            statement.setString(2, login);

            return statement.executeUpdate() > 0;

        } catch (SQLException exception) {

            throw new AccesDonneesException(
                    "Échec du changement de mot de passe pour : " + login,
                    exception
            );
        }
    }

    /**
     * Récupère le profil (nom, prénom, login, email) d'un utilisateur
     * en le retrouvant par son login — utilisé pour le libre-service
     * (page "Paramètres").
     */
    public MonProfilResponse getMonProfil(String login, String role) {

        String table = resoudreTable(role);

        String sql =
                "SELECT nom, prenom, login, email FROM " + table
                + " WHERE login=?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, login);

            try (ResultSet result = statement.executeQuery()) {

                if (!result.next()) {
                    return null;
                }

                return new MonProfilResponse(
                        result.getString("nom"),
                        result.getString("prenom"),
                        result.getString("login"),
                        result.getString("email")
                );
            }

        } catch (SQLException exception) {

            throw new AccesDonneesException(
                    "Échec de la récupération du profil pour : " + login,
                    exception
            );
        }
    }

    /**
     * Modifie le nom, prénom et email d'un utilisateur en le retrouvant
     * par son login (libre-service, page "Paramètres").
     */
    public boolean modifierInfosParLogin(
            String login,
            String role,
            String nom,
            String prenom,
            String email) {

        String table = resoudreTable(role);

        String sql =
                "UPDATE " + table
                + " SET nom=?, prenom=?, email=? WHERE login=?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, nom);
            statement.setString(2, prenom);
            statement.setString(3, email);
            statement.setString(4, login);

            return statement.executeUpdate() > 0;

        } catch (SQLException exception) {

            throw new AccesDonneesException(
                    "Échec de la modification des informations pour : "
                            + login,
                    exception
            );
        }
    }
}