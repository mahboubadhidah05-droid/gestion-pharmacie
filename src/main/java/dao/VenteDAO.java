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

    private static final Logger LOGGER =
            Logger.getLogger(VenteDAO.class.getName());

    private static final String TABLE_VENTE = "vente";

    private static final String COL_VENTE_ID =
            "id_vente";

    private static final String COL_PHARMACIEN_ID =
            "id_pharmacien";

    private static final String COL_CLIENT_ID =
            "id_client";

    private static final String COL_MEDICAMENT_ID =
            "id_medicament";

    private static final String COL_QUANTITE =
            "quantite";

    private static final String COL_DATE =
            "date_vente";

    private static final String AUCUNE_VENTE =
            "Aucune vente trouvée pour ce médicament.";


    public boolean enregistrerVente(
            int idPh,
            int idCl,
            int idMed,
            int qte) {

        String sql =
                "INSERT INTO " + TABLE_VENTE +
                " (id_pharmacien,id_client,id_medicament,quantite,date_vente)"
                + " VALUES(?,?,?,?,?)";

        try (Connection connection =
                     DBConnection.getConnection();

             PreparedStatement ps =
                     connection.prepareStatement(sql)) {

            ps.setInt(1, idPh);
            ps.setInt(2, idCl);
            ps.setInt(3, idMed);
            ps.setInt(4, qte);

            ps.setDate(
                    5,
                    new java.sql.Date(new Date().getTime())
            );

            int lignesAffectees = ps.executeUpdate();

            LOGGER.log(
                    Level.INFO,
                    "{0} vente(s) enregistrée(s).",
                    lignesAffectees
            );

            return lignesAffectees > 0;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, e, () -> "Erreur lors de l'enregistrement de la vente");
            return false;
        }
    }


    public boolean annulerVente(int idVente) {

        String sql =
                "DELETE FROM vente WHERE id_vente=?";

        return executerModification(
                sql,
                idVente
        );
    }


    public void ventesParMedicament(int idMed) {

        afficherVentes(
                "SELECT * FROM vente WHERE id_medicament=?",
                idMed
        );
    }


    public void ventesParClient(int idClient) {

        afficherVentes(
                "SELECT * FROM vente WHERE id_client=?",
                idClient
        );
    }


    public void ventesParPeriode(
            String dateDebut,
            String dateFin) {

        String sql =
                "SELECT * FROM vente "
                + "WHERE date_vente BETWEEN ? AND ?";

        java.sql.Date debut;
        java.sql.Date fin;

        try {
            debut = java.sql.Date.valueOf(dateDebut);
            fin = java.sql.Date.valueOf(dateFin);
        } catch (IllegalArgumentException e) {
            LOGGER.log(Level.WARNING, "Format de date invalide : {0} / {1}", new Object[]{dateDebut, dateFin});
            return;
        }

        try (Connection connection =
                     DBConnection.getConnection();

             PreparedStatement ps =
                     connection.prepareStatement(sql)) {

            ps.setDate(1, debut);
            ps.setDate(2, fin);

            afficherResultat(ps);

        } catch (SQLException e) {
            LOGGER.log(
                    Level.SEVERE,
                    "Erreur lors de la recherche des ventes par période",
                    e
            );
        }
    }

    private void afficherVentes(
            String sql,
            int id) {


        try (Connection connection =
                     DBConnection.getConnection();

             PreparedStatement ps =
                     connection.prepareStatement(sql)) {


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


    private boolean executerModification(
            String sql,
            int id) {

        try (Connection connection =
                     DBConnection.getConnection();

             PreparedStatement ps =
                     connection.prepareStatement(sql)) {

            ps.setInt(1, id);

            int lignesAffectees = ps.executeUpdate();

            LOGGER.log(
                    Level.INFO,
                    "{0} vente(s) modifiée(s).",
                    lignesAffectees
            );

            return lignesAffectees > 0;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, e, () -> "Erreur lors de la modification de la vente");
            return false;
        }
    }


    private void afficherResultat(
            PreparedStatement ps)
            throws SQLException {


        boolean trouve = false;


        try (ResultSet rs =
                     ps.executeQuery()) {


            while (rs.next()) {

                trouve = true;

                LOGGER.log(
                        Level.INFO,
                        "Vente ID={0}, Pharmacien ID={1}, Client ID={2}, "
                        + "Médicament ID={3}, Qte={4}, Date={5}",
                        new Object[]{
                                rs.getInt(COL_VENTE_ID),
                                rs.getInt(COL_PHARMACIEN_ID),
                                rs.getInt(COL_CLIENT_ID),
                                rs.getInt(COL_MEDICAMENT_ID),
                                rs.getInt(COL_QUANTITE),
                                rs.getDate(COL_DATE)
                        }
                );
            }
        }


        if (!trouve) {
            LOGGER.info(AUCUNE_VENTE);
        }
    }
}