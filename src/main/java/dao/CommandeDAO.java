package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import utils.DBConnection;

public class CommandeDAO {

    private static final Logger LOGGER =
            Logger.getLogger(CommandeDAO.class.getName());

    private static final String INSERT_COMMANDE =
            "INSERT INTO Commande"
            + "(id_gestionnaire, id_medicament, quantite)"
            + " VALUES(?,?,?)";


    public void creerCommande(
            int idGestionnaire,
            int idMedicament,
            int quantite) {

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(INSERT_COMMANDE)) {

            statement.setInt(1, idGestionnaire);
            statement.setInt(2, idMedicament);
            statement.setInt(3, quantite);

            statement.executeUpdate();

            LOGGER.info("Commande créée avec succès");

        } catch (SQLException e) {

            LOGGER.log(
                    Level.SEVERE,
                    "Erreur lors de la création de la commande",
                    e
            );
        }
    }
}