package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import dto.FournisseurResponse;
import exception.AccesDonneesException;
import utils.DBConnection;

public class FournisseurDAO {

    private static final String TABLE_FOURNISSEUR = "fournisseur";

    private static final String COL_ID = "id_fournisseur";
    private static final String COL_NOM = "nom";
    private static final String COL_TELEPHONE = "telephone";
    private static final String COL_EMAIL = "email";
    private static final String COL_ADRESSE = "adresse";

    public int ajouterFournisseur(
            String nom,
            String telephone,
            String email,
            String adresse) {

        String sql =
                "INSERT INTO " + TABLE_FOURNISSEUR
                + " (nom, telephone, email, adresse) VALUES (?, ?, ?, ?)";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(
                             sql,
                             Statement.RETURN_GENERATED_KEYS
                     )) {

            statement.setString(1, nom);
            statement.setString(2, telephone);
            statement.setString(3, email);
            statement.setString(4, adresse);

            statement.executeUpdate();

            try (ResultSet cles = statement.getGeneratedKeys()) {

                if (cles.next()) {
                    return cles.getInt(1);
                }
            }

            return -1;

        } catch (SQLException exception) {

            throw new AccesDonneesException(
                    "Échec de l'ajout du fournisseur : " + nom,
                    exception
            );
        }
    }

    public List<FournisseurResponse> listerFournisseurs() {

        String sql =
                "SELECT * FROM " + TABLE_FOURNISSEUR
                + " ORDER BY " + COL_ID;

        List<FournisseurResponse> fournisseurs =
                new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {

                fournisseurs.add(
                        new FournisseurResponse(
                                result.getInt(COL_ID),
                                result.getString(COL_NOM),
                                result.getString(COL_TELEPHONE),
                                result.getString(COL_EMAIL),
                                result.getString(COL_ADRESSE)
                        )
                );
            }

        } catch (SQLException exception) {

            throw new AccesDonneesException(
                    "Échec de la récupération de la liste des fournisseurs",
                    exception
            );
        }

        return fournisseurs;
    }
}
