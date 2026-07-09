package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import utils.DBConnection;

import java.util.logging.Level;
import java.util.logging.Logger;

public class CommandeDAO {

    private static final Logger LOGGER = Logger.getLogger(CommandeDAO.class.getName());

    public void creerCommande(int idGest, int idMed, int qte) {

        String sql = "INSERT INTO Commande(id_gestionnaire, id_medicament, quantite) VALUES(?,?,?)";

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, idGest);
            ps.setInt(2, idMed);
            ps.setInt(3, qte);

            ps.executeUpdate();

            LOGGER.info("Commande créée avec succès");

        } catch (SQLException e) {

            LOGGER.log(Level.SEVERE, "Erreur lors de la création de la commande", e);
        }
    }
}
