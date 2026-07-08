package First_project;

import java.sql.*;
import java.util.Date;
import utils.DBConnection;

public class VenteDAO {

    public void enregistrerVente(int idPh, int idCl, int idMed, int qte) {
        String sql = "INSERT INTO Vente(id_pharmacien , id_client, id_medicament, quantite, date_vente) VALUES(?,?,?,?,?)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idPh);
            ps.setInt(2, idCl);
            ps.setInt(3, idMed);
            ps.setInt(4, qte);
            ps.setDate(5, new java.sql.Date(new Date().getTime()));
            int rows = ps.executeUpdate();
            System.out.println(rows + " vente(s) enregistrée(s).");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void annulerVente(int idVente) {
        String sql = "DELETE FROM vente WHERE id_vente = ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idVente);
            int rows = ps.executeUpdate();
            System.out.println(rows + " vente(s) annulée(s).");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void ventesParMedicament(int idMed) {
        String sql = "SELECT * FROM vente WHERE id_medicament = ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idMed);
            ResultSet rs = ps.executeQuery();
            boolean trouve = false;
            while (rs.next()) {
                trouve = true;
                System.out.println("Vente ID=" + rs.getInt("id_vente")
                        + ", Pharmacie ID=" + rs.getInt("id_pharmacien")
                        + ", Client ID=" + rs.getInt("id_client")
                        + ", Qte=" + rs.getInt("quantite")
                        + ", Date=" + rs.getDate("date_vente"));
            }
            if (!trouve) {
                System.out.println("Aucune vente trouvée pour ce médicament.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void ventesParClient(int idClient) {
        String sql = "SELECT * FROM vente WHERE id_client = ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idClient);
            ResultSet rs = ps.executeQuery();
            boolean trouve = false;
            while (rs.next()) {
                trouve = true;
                System.out.println("Vente ID=" + rs.getInt("id_vente")
                        + ", Pharmacie ID=" + rs.getInt("id_pharmacien")
                        + ", Client ID=" + rs.getInt("id_client")
                        + ", Médicament ID=" + rs.getInt("id_medicament")
                        + ", Qte=" + rs.getInt("quantite")
                        + ", Date=" + rs.getDate("date_vente"));
            }
            if (!trouve) {
                System.out.println("Aucune vente trouvée pour ce médicament.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void ventesParPeriode(String dateDebut, String dateFin) {
        String sql = "SELECT * FROM vente WHERE date_vente BETWEEN ? AND ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setDate(1, java.sql.Date.valueOf(dateDebut));
            ps.setDate(2, java.sql.Date.valueOf(dateFin));
            ResultSet rs = ps.executeQuery();
            boolean trouve = false;
            while (rs.next()) {
                trouve = true;
                System.out.println("Vente ID=" + rs.getInt("id_vente")
                        + ", Pharmacie ID=" + rs.getInt("id_pharmacien")
                        + ", Client ID=" + rs.getInt("id_client")
                        + ", Médicament ID=" + rs.getInt("id_medicament")
                        + ", Qte=" + rs.getInt("quantite")
                        + ", Date=" + rs.getDate("date_vente"));
            }
            if (!trouve) {
                System.out.println("Aucune vente trouvée pour ce médicament.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
