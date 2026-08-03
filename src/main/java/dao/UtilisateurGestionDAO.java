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
 * Sécurité SQL : chaque requête est un texte SQL entièrement figé
 * (jamais construit par concaténation d'un nom de table/colonne).
 * Le rôle ne sert qu'à choisir laquelle de ces requêtes fixes utiliser —
 * il n'entre jamais dans le texte de la requête elle-même. Toutes les
 * données utilisateur (nom, login, etc.) restent des paramètres liés
 * (PreparedStatement).
 */
public class UtilisateurGestionDAO {

    private static final String ROLE_PHARMACIEN = "PHARMACIEN";

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

    private static final String INSERT_PHARMACIEN =
            "INSERT INTO pharmacien (nom, prenom, login, pwd) "
            + "VALUES (?,?,?,?)";

    private static final String INSERT_GESTIONNAIRE =
            "INSERT INTO gestionnaire (nom, prenom, login, pwd) "
            + "VALUES (?,?,?,?)";

    private static final String UPDATE_PHARMACIEN_AVEC_MDP =
            "UPDATE pharmacien SET nom=?, prenom=?, login=?, pwd=? "
            + "WHERE id_pharmacien=?";

    private static final String UPDATE_PHARMACIEN_SANS_MDP =
            "UPDATE pharmacien SET nom=?, prenom=?, login=? "
            + "WHERE id_pharmacien=?";

    private static final String UPDATE_GESTIONNAIRE_AVEC_MDP =
            "UPDATE gestionnaire SET nom=?, prenom=?, login=?, pwd=? "
            + "WHERE id_gestionnaire=?";

    private static final String UPDATE_GESTIONNAIRE_SANS_MDP =
            "UPDATE gestionnaire SET nom=?, prenom=?, login=? "
            + "WHERE id_gestionnaire=?";

    private static final String DELETE_PHARMACIEN =
            "DELETE FROM pharmacien WHERE id_pharmacien=?";

    private static final String DELETE_GESTIONNAIRE =
            "DELETE FROM gestionnaire WHERE id_gestionnaire=?";

    private static final String UPDATE_MDP_PHARMACIEN_PAR_LOGIN =
            "UPDATE pharmacien SET pwd=? WHERE login=?";

    private static final String UPDATE_MDP_GESTIONNAIRE_PAR_LOGIN =
            "UPDATE gestionnaire SET pwd=? WHERE login=?";

    private static final String SELECT_PROFIL_PHARMACIEN =
            "SELECT nom, prenom, login, email FROM pharmacien "
            + "WHERE login=?";

    private static final String SELECT_PROFIL_GESTIONNAIRE =
            "SELECT nom, prenom, login, email FROM gestionnaire "
            + "WHERE login=?";

    private static final String UPDATE_INFOS_PHARMACIEN_PAR_LOGIN =
            "UPDATE pharmacien SET nom=?, prenom=?, email=? "
            + "WHERE login=?";

    private static final String UPDATE_INFOS_GESTIONNAIRE_PAR_LOGIN =
            "UPDATE gestionnaire SET nom=?, prenom=?, email=? "
            + "WHERE login=?";

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

        String sql =
                ROLE_PHARMACIEN.equals(role)
                        ? INSERT_PHARMACIEN
                        : INSERT_GESTIONNAIRE;

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

        boolean estPharmacien = ROLE_PHARMACIEN.equals(role);
        boolean changerMotDePasse = pwdHache != null;

        String sql;

        if (estPharmacien) {
            sql = changerMotDePasse
                    ? UPDATE_PHARMACIEN_AVEC_MDP
                    : UPDATE_PHARMACIEN_SANS_MDP;
        } else {
            sql = changerMotDePasse
                    ? UPDATE_GESTIONNAIRE_AVEC_MDP
                    : UPDATE_GESTIONNAIRE_SANS_MDP;
        }

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

        String sql =
                ROLE_PHARMACIEN.equals(role)
                        ? DELETE_PHARMACIEN
                        : DELETE_GESTIONNAIRE;

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

        String sql =
                ROLE_PHARMACIEN.equals(role)
                        ? UPDATE_MDP_PHARMACIEN_PAR_LOGIN
                        : UPDATE_MDP_GESTIONNAIRE_PAR_LOGIN;

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

        String sql =
                ROLE_PHARMACIEN.equals(role)
                        ? SELECT_PROFIL_PHARMACIEN
                        : SELECT_PROFIL_GESTIONNAIRE;

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

        String sql =
                ROLE_PHARMACIEN.equals(role)
                        ? UPDATE_INFOS_PHARMACIEN_PAR_LOGIN
                        : UPDATE_INFOS_GESTIONNAIRE_PAR_LOGIN;

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