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


    private static final String TABLE_MEDICAMENT =
            "Medicament";

    private static final String COL_ID =
            "id_medicament";

    private static final String COL_NOM =
            "nom";

    private static final String COL_STOCK =
            "stock";

    private static final String WHERE_ID =
            " WHERE " + COL_ID + " = ?";



    public void ajouterMedicament(
            String nom,
            String dosage,
            int stock,
            double prix,
            int seuil) {


        String sql =
                "INSERT INTO "
                + TABLE_MEDICAMENT
                + " (nom, dosage, stock, prix, seuil_critique)"
                + " VALUES(?,?,?,?,?)";


        try (Connection connection =
                     DBConnection.getConnection();

             PreparedStatement statement =
                     connection.prepareStatement(sql)) {


            statement.setString(1, nom);
            statement.setString(2, dosage);
            statement.setInt(3, stock);
            statement.setDouble(4, prix);
            statement.setInt(5, seuil);

            statement.executeUpdate();


            LOGGER.info(
                    "Médicament ajouté avec succès"
            );


        } catch (SQLException e) {

            LOGGER.log(
                    Level.SEVERE,
                    "Erreur lors de l'ajout du médicament",
                    e
            );
        }
    }



    public int getStock(int idMed) {


        String sql =
                "SELECT "
                + COL_STOCK
                + " FROM "
                + TABLE_MEDICAMENT
                + WHERE_ID;


        try (Connection connection =
                     DBConnection.getConnection();

             PreparedStatement statement =
                     connection.prepareStatement(sql)) {


            statement.setInt(1, idMed);


            try (ResultSet result =
                         statement.executeQuery()) {


                if (result.next()) {

                    return result.getInt(
                            COL_STOCK
                    );
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



    public void updateStock(
            int idMed,
            int quantite) {


        String sql =
                "UPDATE "
                + TABLE_MEDICAMENT
                + " SET "
                + COL_STOCK
                + "=?"
                + WHERE_ID;



        try (Connection connection =
                     DBConnection.getConnection();

             PreparedStatement statement =
                     connection.prepareStatement(sql)) {


            statement.setInt(1, quantite);
            statement.setInt(2, idMed);

            statement.executeUpdate();


            LOGGER.info(
                    "Stock mis à jour avec succès"
            );


        } catch (SQLException e) {

            LOGGER.log(
                    Level.SEVERE,
                    "Erreur lors de la mise à jour du stock",
                    e
            );
        }
    }



    public String stockCritique(int idMed) {


        String sql =
                "SELECT * FROM "
                + TABLE_MEDICAMENT
                + WHERE_ID
                + " AND "
                + COL_STOCK
                + " <= seuil_critique";


        try (Connection connection =
                     DBConnection.getConnection();

             PreparedStatement statement =
                     connection.prepareStatement(sql)) {


            statement.setInt(1, idMed);



            try (ResultSet result =
                         statement.executeQuery()) {


                if (result.next()) {

                    return String.format(
                            "Médicament en stock critique : %s (ID %d) | Stock actuel = %d",
                            result.getString(COL_NOM),
                            result.getInt(COL_ID),
                            result.getInt(COL_STOCK)
                    );
                }
            }


        } catch (SQLException e) {

            LOGGER.log(
                    Level.SEVERE,
                    "Erreur lors de la vérification du stock critique",
                    e
            );
        }


        return null;
    }



    public int getIdMedicamentParNomEtDosage(
            String nom,
            String dosage) {


        String sql =
                "SELECT "
                + COL_ID
                + " FROM "
                + TABLE_MEDICAMENT
                + " WHERE "
                + COL_NOM
                + "=? AND dosage=?";



        try (Connection connection =
                     DBConnection.getConnection();

             PreparedStatement statement =
                     connection.prepareStatement(sql)) {


            statement.setString(1, nom);
            statement.setString(2, dosage);



            try (ResultSet result =
                         statement.executeQuery()) {


                if (result.next()) {

                    return result.getInt(
                            COL_ID
                    );
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