package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import utils.DBConnection;

public class MedicamentDAO {

    private static final Logger LOGGER =
            Logger.getLogger(MedicamentDAO.class.getName());

    private static final String STOCK_COLUMN = "stock";
    private static final String ID_COLUMN = "id_medicament";
    private static final String NOM_COLUMN = "nom";
    private static final String TABLE_MEDICAMENT = "Medicament";

    public void ajouterMedicament(
            String nom,
            String dosage,
            int stock,
            double prix,
            int seuil) {

        String sql = "INSERT INTO " + TABLE_MEDICAMENT
                + " (nom, dosage, stock, prix, seuil_critique)"
                + " VALUES(?,?,?,?,?)";

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, nom);
            ps.setString(2, dosage);
            ps.setInt(3, stock);
            ps.setDouble(4, prix);
            ps.setInt(5, seuil);
            ps.executeUpdate();

            LOGGER.info("Médicament ajouté avec succès");

        } catch (SQLException e) {
            LOGGER.log(
                    Level.SEVERE,
                    "Erreur lors de l ajout du medicament",
                    e
            );
        }
    }

    public int getStock(int idMed) {

        String sql = "SELECT " + STOCK_COLUMN
                + " FROM " + TABLE_MEDICAMENT
                + " WHERE " + ID_COLUMN + " = ?";

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, idMed);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(STOCK_COLUMN);
                }
            }

        } catch (SQLException e) {
            LOGGER.log(
                    Level.SEVERE,
                    "Erreur lors de la récupération du stock",
                    e
            );
        }
        return -1;
    }

    public void updateStock(int idMed, int qte) {

        String sql = "UPDATE " + TABLE_MEDICAMENT
                + " SET " + STOCK_COLUMN + "=? WHERE " + ID_COLUMN + "=?";

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, qte);
            ps.setInt(2, idMed);
            ps.executeUpdate();

            LOGGER.info("Stock mis à jour avec succès");

        } catch (SQLException e) {
            LOGGER.log(
                    Level.SEVERE,
                    "Erreur lors de la mise à jour du stock",
                    e
            );
        }
    }

    public String stockCritique(int idMed) {

        String sql = "SELECT * FROM " + TABLE_MEDICAMENT
                + " WHERE " + ID_COLUMN + " = ? "
                + "AND " + STOCK_COLUMN + " <= seuil_critique";

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, idMed);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return String.format(
                            "Médicament en stock critique : %s (ID %d) | Stock actuel = %d",
                            rs.getString(NOM_COLUMN),
                            rs.getInt(ID_COLUMN),
                            rs.getInt(STOCK_COLUMN)
                    );
                }
                LOGGER.log(
                        Level.INFO,
                        "Le medicament ID {0} n est pas en stock critique.",
                        idMed
                );
                return null;
            }

        } catch (SQLException e) {
            LOGGER.log(
                    Level.SEVERE,
                    "Erreur lors de la vérification du stock critique",
                    e
            );
            return null;
        }
    }

    public int getIdMedicamentParNomEtDosage(
            String nom,
            String dosage) {

        String sql = "SELECT " + ID_COLUMN
                + " FROM " + TABLE_MEDICAMENT
                + " WHERE " + NOM_COLUMN + "=? AND dosage=?";

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, nom);
            ps.setString(2, dosage);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(ID_COLUMN);
                }
            }

        } catch (SQLException e) {
            LOGGER.log(
                    Level.SEVERE,
                    "Erreur lors de la recherche du médicament",
                    e
            );
        }
        return -1;
    }
}