package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

import utils.DBConnection;

/**
 * DAO pour la gestion des clients.
 */
public class ClientDAO {

    private static final Logger LOGGER =
            Logger.getLogger(ClientDAO.class.getName());

    private static final String INSERT_CLIENT =
            "INSERT INTO client VALUES(NULL,?,?,?,?)";

    private static final String EXISTE_CLIENT =
            "SELECT 1 FROM client WHERE id_client=?";

    private static final String ERREUR_AJOUT_CLIENT =
            "Erreur lors de l'ajout du client";

    private static final String ERREUR_VERIF_CLIENT =
            "Erreur lors de la vérification du client";

    private static final int ID_INVALIDE = -1;

    /**
     * Vérifie si un client existe déjà avec cet ID.
     */
    public boolean existeClient(int idClient) {

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(EXISTE_CLIENT)) {

            statement.setInt(1, idClient);

            try (ResultSet rs = statement.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, e, () -> ERREUR_VERIF_CLIENT);
        }

        return false;
    }

    /**
     * Ajoute un client et retourne son nouvel ID généré.
     * Retourne -1 en cas d'échec.
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
                             Statement.RETURN_GENERATED_KEYS
                     )) {

            statement.setString(1, nom);
            statement.setString(2, prenom);
            statement.setString(3, email);
            statement.setString(4, adresse);
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    int nouvelId = keys.getInt(1);
                    LOGGER.info(() -> "Client ajouté avec succès, ID=" + nouvelId);
                    return nouvelId;
                }
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, e, () -> ERREUR_AJOUT_CLIENT);
        }

        return ID_INVALIDE;
    }
}