package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import exception.AccesDonneesException;
import utils.DBConnection;

/**
 * DAO pour la gestion des commandes.
 */
public class CommandeDAO {

    private static final String INSERT_COMMANDE =
            "INSERT INTO commande "
            + "(id_gestionnaire, id_medicament, quantite) "
            + "VALUES (?, ?, ?)";

    /**
     * Crée une nouvelle commande.
     *
     * @param idGestionnaire identifiant du gestionnaire
     * @param idMedicament identifiant du médicament
     * @param quantite quantité commandée
     * @throws AccesDonneesException en cas d'erreur d'accès à la base de données
     */
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

        } catch (SQLException exception) {

            throw new AccesDonneesException(
                    "Échec de la création de la commande pour le gestionnaire "
                            + idGestionnaire
                            + ", médicament "
                            + idMedicament
                            + ", quantité "
                            + quantite,
                    exception
            );
        }
    }
}