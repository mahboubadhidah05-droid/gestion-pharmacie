package dao;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import utils.DBConnection;

/**
 * DAO pour la gestion des clients.
 */
public class ClientDAO {

	private static final Logger LOGGER = Logger.getLogger(ClientDAO.class.getName());

	public void ajouterClient(String nom, String prenom, String email, String adresse) {
	    String sql = "INSERT INTO Client VALUES(NULL,?,?,?,?)";
	    try (Connection c = DBConnection.getConnection();
	         PreparedStatement ps = c.prepareStatement(sql)) {
	        ps.setString(1, nom);
	        ps.setString(2, prenom);
	        ps.setString(3, email);
	        ps.setString(4, adresse);
	        ps.executeUpdate();
	    } catch (Exception e) {
	        LOGGER.log(Level.SEVERE, "Erreur lors de l ajout du client", e);
	    }
	} }