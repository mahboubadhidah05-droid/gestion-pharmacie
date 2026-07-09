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

    private static final Logger LOGGER =
            Logger.getLogger(StockHistoriqueDAO.class.getName());

    private static final String ID_MEDICAMENT = "id_medicament";
    private static final String QUANTITE = "quantite";
    private static final String DATE_MODIFICATION = "date_modification";

    public void ajouterHistorique(int idMed, int quantite) {

        String sql = "INSERT INTO stock_historique "
                + "(id_medicament, quantite, date_modification) "
                + "VALUES (?, ?, NOW())";

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, idMed);
            ps.setInt(2, quantite);

            ps.executeUpdate();

            LOGGER.log(
                    Level.INFO,
                    "Historique stock enregistré : idMed={0} qte={1}",
                    new Object[]{idMed, quantite}
            );

        } catch (SQLException e) {

            LOGGER.log(
                    Level.SEVERE,
                    "Erreur lors de l'enregistrement de l'historique stock",
                    e
            );
        }
    }


    public void afficherHistorique() {

        String sql = "SELECT * FROM stock_historique";

        try (Connection c = DBConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {

                LOGGER.log(
                        Level.INFO,
                        "Med={0} | Qte={1} | Date={2}",
                        new Object[]{
                                rs.getInt(ID_MEDICAMENT),
                                rs.getInt(QUANTITE),
                                rs.getTimestamp(DATE_MODIFICATION)
                        }
                );
            }

        } catch (SQLException e) {

            LOGGER.log(
                    Level.SEVERE,
                    "Erreur lors de l'affichage de l'historique stock",
                    e
            );
        }
    }
}