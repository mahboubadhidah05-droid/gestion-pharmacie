package dao;

import java.sql.*;
import utils.DBConnection;

public class CommandeDAO {

    public void creerCommande(int idGest, int idMed, int qte) {
        String sql = "INSERT INTO Commande(id_gestionnaire, id_medicament, quantite) VALUES(?,?,?)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idGest);
            ps.setInt(2, idMed);
            ps.setInt(3, qte);
            ps.executeUpdate();
            System.out.println("Commande créée");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
