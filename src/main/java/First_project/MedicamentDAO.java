package First_project;

import java.sql.*;
import utils.DBConnection;

public class MedicamentDAO {

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
            System.out.println("Médicament ajouté");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Récupérer le stock d'un médicament
    public int getStock(int idMed) {
        String sql = "SELECT stock FROM Medicament WHERE id_medicament = ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idMed);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("stock");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // médicament non trouvé
    }

    public void updateStock(int idMed, int qte) {
        String sql = "UPDATE Medicament SET stock=? WHERE id_medicament=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, qte);
            ps.setInt(2, idMed);
            ps.executeUpdate();
            System.out.println("Stock mis à jour");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Vérifie si le médicament est en stock critique.
     * Ne notifie plus directement : c'est la couche Service qui décide
     * quoi faire du résultat (le DAO ne doit pas dépendre du Service).
     * Retourne le message d'alerte si le stock est critique, sinon null.
     */
    public String stockCritique(int idMed) {
        String sql = "SELECT * FROM Medicament WHERE id_medicament = ? AND stock <= seuil_critique";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement pst = c.prepareStatement(sql)) {
            pst.setInt(1, idMed);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return "Médicament en stock critique : " + rs.getString("nom")
                            + " (ID " + rs.getInt("id_medicament") + ") | Stock actuel = " + rs.getInt("stock");
                } else {
                    System.out.println("Le médicament avec ID " + idMed + " n'est pas en stock critique.");
                    return null;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
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
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                id = rs.getInt("id_medicament");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return id;
    }
}