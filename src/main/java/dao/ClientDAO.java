package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
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
            "INSERT INTO Client VALUES(NULL,?,?,?,?)";

    public void ajouterClient(
            String nom,
            String prenom,
            String email,
            String adresse) {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(INSERT_CLIENT)) {
            statement.setString(1, nom);
            statement.setString(2, prenom);
            statement.setString(3, email);
            statement.setString(4, adresse);
            statement.executeUpdate();
            LOGGER.info("Client ajouté avec succès");
        } catch (SQLException e) {
            LOGGER.log(
                    Level.SEVERE,
                    "Erreur lors de l'ajout du client",
                    e
            );
        }
    }
}