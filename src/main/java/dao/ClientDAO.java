package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import exception.AccesDonneesException;
import utils.DBConnection;

/**
 * DAO pour la gestion des clients.
 */
public class ClientDAO {

    private static final String INSERT_CLIENT =
            "INSERT INTO client VALUES(NULL,?,?,?,?)";

    private static final String EXISTE_CLIENT =
            "SELECT 1 FROM client WHERE id_client=?";

    private static final int ID_INVALIDE = -1;

    /**
     * Vérifie si un client existe déjà avec cet ID.
     *
     * @param idClient identifiant du client
     * @return true si le client existe
     * @throws AccesDonneesException en cas d'erreur d'accès aux données
     */
    public boolean existeClient(int idClient) {

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(EXISTE_CLIENT)) {

            statement.setInt(1, idClient);

            try (ResultSet rs = statement.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException exception) {

            throw new AccesDonneesException(
                    "Échec de la vérification du client avec l'identifiant : "
                            + idClient,
                    exception
            );
        }
    }

    /**
     * Ajoute un client et retourne son nouvel ID généré.
     *
     * @param nom nom du client
     * @param prenom prénom du client
     * @param email adresse email
     * @param adresse adresse du client
     * @return nouvel identifiant généré ou -1 si aucun ID n'a été généré
     * @throws AccesDonneesException en cas d'erreur d'accès aux données
     */
    public int ajouterClient(
            String nom,
            String prenom,
            String email,
            String adresse) {

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(
                             INSERT_CLIENT,
                             Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, nom);
            statement.setString(2, prenom);
            statement.setString(3, email);
            statement.setString(4, adresse);

            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {

                if (keys.next()) {
                    return keys.getInt(1);
                }
            }

        } catch (SQLException exception) {

            throw new AccesDonneesException(
                    "Échec de l'ajout du client : "
                            + nom + " " + prenom,
                    exception
            );
        }

        return ID_INVALIDE;
    }
}