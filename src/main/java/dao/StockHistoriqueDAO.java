package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

import utils.DBConnection;

public class StockHistoriqueDAO {

    private static final Logger LOGGER = Logger.getLogger(StockHistoriqueDAO.class.getName());


    // Cette méthode est utilisée dans TOUS les cas
    public void ajouterHistorique(int idMed, int quantite) {

        String sql = "INSERT INTO stock_historique "
                + "(id_medicament, quantite, date_modification) "
                + "VALUES (?, ?, NOW())";

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, idMed);
            ps.setInt(2, quantite);

            ps.executeUpdate();

            LOGGER.info("Historique stock enregistré : idMed="
                    + idMed + " qte=" + quantite);

        } catch (SQLException e) {

            LOGGER.log(Level.SEVERE,
                    "Erreur lors de l'enregistrement de l'historique stock",
                    e);
        }
    }


    // Affichage historique
    public void afficherHistorique() {

        String sql = "SELECT * FROM stock_historique";

        try (Connection c = DBConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {

                LOGGER.info(
                        "Med=" + rs.getInt("id_medicament")
                                + " | Qte=" + rs.getInt("quantite")
                                + " | Date=" + rs.getTimestamp("date_modification")
                );
            }

        } catch (SQLException e) {

            LOGGER.log(Level.SEVERE,
                    "Erreur lors de l'affichage de l'historique stock",
                    e);
        }
    }
}