package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import utils.DBConnection;

public class MedicamentDAO {

    private static final Logger LOGGER = Logger.getLogger(MedicamentDAO.class.getName());

    private static final String STOCK_COLUMN = "stock";

    public void ajouterMedicament(String nom, String dosage, int stock, double prix, int seuil) {

        String sql = "INSERT INTO Medicament(nom, dosage, stock, prix, seuil_critique) VALUES(?,?,?,?,?)";

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

            LOGGER.log(Level.SEVERE, "Erreur lors de l'ajout du médicament", e);
        }
    }


    public int getStock(int idMed) {

        String sql = "SELECT stock FROM Medicament WHERE id_medicament = ?";

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, idMed);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    return rs.getInt(STOCK_COLUMN);
                }
            }

        } catch (SQLException e) {

            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération du stock", e);
        }

        return -1;
    }


    public void updateStock(int idMed, int qte) {

        String sql = "UPDATE Medicament SET stock=? WHERE id_medicament=?";

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, qte);
            ps.setInt(2, idMed);

            ps.executeUpdate();

            LOGGER.info("Stock mis à jour avec succès");

        } catch (SQLException e) {

            LOGGER.log(Level.SEVERE, "Erreur lors de la mise à jour du stock", e);
        }
    }


    public String stockCritique(int idMed) {

        String sql = "SELECT * FROM Medicament WHERE id_medicament = ? AND stock <= seuil_critique";

        try (Connection c = DBConnection.getConnection();
             PreparedStatement pst = c.prepareStatement(sql)) {


            pst.setInt(1, idMed);

            try (ResultSet rs = pst.executeQuery()) {

                if (rs.next()) {

                    return String.format(
                            "Médicament en stock critique : %s (ID %d) | Stock actuel = %d",
                            rs.getString("nom"),
                            rs.getInt("id_medicament"),
                            rs.getInt(STOCK_COLUMN)
                    );

                } else {

                    LOGGER.info(
                            String.format(
                                    "Le médicament ID %d n'est pas en stock critique.",
                                    idMed
                            )
                    );

                    return null;
                }
            }

        } catch (SQLException e) {

            LOGGER.log(Level.SEVERE, "Erreur lors de la vérification du stock critique", e);

            return null;
        }
    }


    public int getIdMedicamentParNomEtDosage(String nom, String dosage) {

        String sql = "SELECT id_medicament FROM Medicament WHERE nom=? AND dosage=?";

        int id = -1;


        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {


            ps.setString(1, nom);
            ps.setString(2, dosage);


            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    id = rs.getInt("id_medicament");
                }
            }


        } catch (SQLException e) {

            LOGGER.log(Level.SEVERE, "Erreur lors de la recherche du médicament", e);
        }


        return id;
    }
}