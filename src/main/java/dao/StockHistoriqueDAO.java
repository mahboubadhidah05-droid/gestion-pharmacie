package dao;

import java.sql.*;
import utils.DBConnection;

public class StockHistoriqueDAO {

    // Cette méthode est utilisée dans TOUS les cas
    public void ajouterHistorique(int idMed, int quantite) {
        String sql = "INSERT INTO stock_historique (id_medicament, quantite, date_modification) VALUES (?, ?, NOW())";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idMed);
            ps.setInt(2, quantite);
            ps.executeUpdate();
            System.out.println("Historique stock enregistré : idMed=" + idMed + " qte=" + quantite);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Affichage historique
    public void afficherHistorique() {
        String sql = "SELECT * FROM stock_historique";
        try (Connection c = DBConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                System.out.println(
                        " | Med=" + rs.getInt("id_medicament")
                                + " | Qte=" + rs.getInt("quantite")
                                + " | Date=" + rs.getTimestamp("date_modification"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}