package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import dto.VenteResponse;
import exception.AccesDonneesException;
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

            ps.setTimestamp(
                    5,
                    new java.sql.Timestamp(new Date().getTime())
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

            throw new AccesDonneesException(
                    "Échec de l'enregistrement de la vente",
                    e
            );
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


    public List<VenteResponse> ventesParMedicament(int idMed) {

        return chercherVentes(
                "SELECT * FROM vente WHERE id_medicament=?",
                idMed
        );
    }


    public List<VenteResponse> ventesParClient(int idClient) {

        return chercherVentes(
                "SELECT * FROM vente WHERE id_client=?",
                idClient
        );
    }


    public List<VenteResponse> ventesParPeriode(
            String dateDebut,
            String dateFin) {

        String sql =
                "SELECT * FROM vente "
                + "WHERE date_vente BETWEEN ? AND ?";

        List<VenteResponse> ventes = new ArrayList<>();

        try (Connection connection =
                     DBConnection.getConnection();

             PreparedStatement ps =
                     connection.prepareStatement(sql)) {

            ps.setString(1, dateDebut);
            ps.setString(2, dateFin + " 23:59:59");

            try (ResultSet rs = ps.executeQuery()) {
                remplirVentes(rs, ventes);
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, e, () -> "Erreur lors de la consultation des ventes par période");

            throw new AccesDonneesException(
                    "Échec de la consultation des ventes par période",
                    e
            );
        }

        return ventes;
    }


    private List<VenteResponse> chercherVentes(
            String sql,
            int parametre) {

        List<VenteResponse> ventes = new ArrayList<>();

        try (Connection connection =
                     DBConnection.getConnection();

             PreparedStatement ps =
                     connection.prepareStatement(sql)) {

            ps.setInt(1, parametre);

            try (ResultSet rs = ps.executeQuery()) {
                remplirVentes(rs, ventes);
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, e, () -> "Erreur lors de la consultation des ventes");

            throw new AccesDonneesException(
                    "Échec de la consultation des ventes",
                    e
            );
        }

        return ventes;
    }


    private void remplirVentes(
            ResultSet rs,
            List<VenteResponse> ventes)
            throws SQLException {

        while (rs.next()) {

            ventes.add(
                    new VenteResponse(
                            rs.getInt(COL_VENTE_ID),
                            rs.getInt(COL_PHARMACIEN_ID),
                            rs.getInt(COL_CLIENT_ID),
                            rs.getInt(COL_MEDICAMENT_ID),
                            rs.getInt(COL_QUANTITE),
                            rs.getTimestamp(COL_DATE).toLocalDateTime()
                    )
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

            throw new AccesDonneesException(
                    "Échec de la modification de la vente",
                    e
            );
        }
    }
}