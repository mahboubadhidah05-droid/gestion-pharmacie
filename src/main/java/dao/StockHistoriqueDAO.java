package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.ArrayList;
import java.util.List;
import dto.StockHistoriqueResponse;

import utils.DBConnection;

public class StockHistoriqueDAO {

    private static final Logger LOGGER =
            Logger.getLogger(StockHistoriqueDAO.class.getName());


    private static final String ID_MEDICAMENT =
            "id_medicament";

    private static final String QUANTITE =
            "quantite";

    private static final String DATE_MODIFICATION =
            "date_modification";


    private static final String INSERT_HISTORIQUE =
            "INSERT INTO stock_historique "
            + "(id_medicament, quantite, date_modification) "
            + "VALUES (?, ?, NOW())";


    private static final String SELECT_HISTORIQUE =
            "SELECT * FROM stock_historique";


    public void ajouterHistorique(
            int idMedicament,
            int quantite) {


        try (Connection connection =
                     DBConnection.getConnection();

             PreparedStatement statement =
                     connection.prepareStatement(
                             INSERT_HISTORIQUE)) {


            statement.setInt(
                    1,
                    idMedicament
            );

            statement.setInt(
                    2,
                    quantite
            );


            statement.executeUpdate();


            LOGGER.log(
                    Level.INFO,
                    "Historique stock enregistré : idMed={0} qte={1}",
                    new Object[]{
                            idMedicament,
                            quantite
                    }
            );


        } catch (SQLException e) {

            LOGGER.log(
                    Level.SEVERE,
                    "Erreur lors de l'enregistrement de l'historique stock",
                    e
            );
        }
    }


    public List<StockHistoriqueResponse> afficherHistorique() {

        List<StockHistoriqueResponse> historique = new ArrayList<>();

        try (Connection connection =
                     DBConnection.getConnection();
             Statement statement =
                     connection.createStatement();
             ResultSet resultSet =
                     statement.executeQuery(
                             SELECT_HISTORIQUE)) {

            while (resultSet.next()) {

                historique.add(new StockHistoriqueResponse(
                        resultSet.getInt(ID_MEDICAMENT),
                        resultSet.getInt(QUANTITE),
                        resultSet.getTimestamp(DATE_MODIFICATION)
                ));
            }

        } catch (SQLException e) {

            LOGGER.log(
                    Level.SEVERE,
                    "Erreur lors de l'affichage de l'historique stock",
                    e
            );
        }

        return historique;
    }
}