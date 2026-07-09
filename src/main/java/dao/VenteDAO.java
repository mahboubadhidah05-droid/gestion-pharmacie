package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import utils.DBConnection;

public class VenteDAO {

    private static final Logger LOGGER = Logger.getLogger(VenteDAO.class.getName());

    private static final String VENTE_ID = "Vente ID=";
    private static final String PHARMACIEN_ID = ", Pharmacie ID=";
    private static final String CLIENT_ID = ", Client ID=";
    private static final String MEDICAMENT_ID = ", Médicament ID=";
    private static final String QTE = ", Qte=";
    private static final String DATE = ", Date=";

    private static final String COL_VENTE_ID = "id_vente";
    private static final String COL_PHARMACIEN_ID = "id_pharmacien";
    private static final String COL_CLIENT_ID = "id_client";
    private static final String COL_MEDICAMENT_ID = "id_medicament";
    private static final String COL_QUANTITE = "quantite";
    private static final String COL_DATE = "date_vente";

    private static final String AUCUNE_VENTE =
            "Aucune vente trouvée pour ce médicament.";


    public void enregistrerVente(int idPh, int idCl, int idMed, int qte) {

        String sql = "INSERT INTO Vente(id_pharmacien, id_client, id_medicament, quantite, date_vente) "
                + "VALUES(?,?,?,?,?)";

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, idPh);
            ps.setInt(2, idCl);
            ps.setInt(3, idMed);
            ps.setInt(4, qte);
            ps.setDate(5, new java.sql.Date(new Date().getTime()));

            int rows = ps.executeUpdate();

            LOGGER.info(
                    String.format(
                            "%d vente(s) enregistrée(s).",
                            rows
                    )
            );

        } catch (SQLException e) {

            LOGGER.log(
                    Level.SEVERE,
                    "Erreur lors de l'enregistrement de la vente",
                    e
            );
        }
    }


    public void annulerVente(int idVente) {

        String sql = "DELETE FROM vente WHERE id_vente = ?";

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, idVente);

            int rows = ps.executeUpdate();

            LOGGER.info(
                    String.format(
                            "%d vente(s) annulée(s).",
                            rows
                    )
            );

        } catch (SQLException e) {

            LOGGER.log(
                    Level.SEVERE,
                    "Erreur lors de l'annulation de la vente",
                    e
            );
        }
    }


    public void ventesParMedicament(int idMed) {

        String sql = "SELECT * FROM vente WHERE id_medicament = ?";

        afficherVentes(sql, idMed);
    }


    public void ventesParClient(int idClient) {

        String sql = "SELECT * FROM vente WHERE id_client = ?";

        afficherVentes(sql, idClient);
    }


    public void ventesParPeriode(String dateDebut, String dateFin) {

        String sql = "SELECT * FROM vente WHERE date_vente BETWEEN ? AND ?";

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setDate(1, java.sql.Date.valueOf(dateDebut));
            ps.setDate(2, java.sql.Date.valueOf(dateFin));

            afficherResultat(ps);

        } catch (SQLException e) {

            LOGGER.log(
                    Level.SEVERE,
                    "Erreur lors de la recherche des ventes par période",
                    e
            );
        }
    }


    private void afficherVentes(String sql, int id) {

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);

            afficherResultat(ps);

        } catch (SQLException e) {

            LOGGER.log(
                    Level.SEVERE,
                    "Erreur lors de la récupération des ventes",
                    e
            );
        }
    }


    private void afficherResultat(PreparedStatement ps) throws SQLException {

        boolean trouve = false;

        try (ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                trouve = true;

                LOGGER.info(
                        String.format(
                                "%s%d%s%d%s%d%s%d%s%d%s%s",
                                VENTE_ID,
                                rs.getInt(COL_VENTE_ID),
                                PHARMACIEN_ID,
                                rs.getInt(COL_PHARMACIEN_ID),
                                CLIENT_ID,
                                rs.getInt(COL_CLIENT_ID),
                                MEDICAMENT_ID,
                                rs.getInt(COL_MEDICAMENT_ID),
                                QTE,
                                rs.getInt(COL_QUANTITE),
                                DATE + rs.getDate(COL_DATE)
                        )
                );
            }
        }


        if (!trouve) {

            LOGGER.info(AUCUNE_VENTE);
        }
    }
}