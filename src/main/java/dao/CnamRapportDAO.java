package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import dto.CnamRapportResponse;
import exception.AccesDonneesException;
import utils.DBConnection;

public class CnamRapportDAO {

    private static final String SELECT_RAPPORT =
            "SELECT COUNT(*) AS nb, "
            + "SUM(montant_rembourse) AS total_rembourse, "
            + "SUM(ticket_moderateur) AS total_ticket "
            + "FROM vente "
            + "WHERE date_vente BETWEEN ? AND ? "
            + "AND montant_rembourse > 0";

    public CnamRapportResponse genererRapport(
            String dateDebut,
            String dateFin) {

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(SELECT_RAPPORT)) {

            statement.setString(1, dateDebut);
            statement.setString(2, dateFin + " 23:59:59");

            try (ResultSet result = statement.executeQuery()) {

                if (result.next()) {

                    return new CnamRapportResponse(
                            dateDebut,
                            dateFin,
                            result.getInt("nb"),
                            result.getDouble("total_rembourse"),
                            result.getDouble("total_ticket")
                    );
                }
            }

        } catch (SQLException exception) {

            throw new AccesDonneesException(
                    "Échec de la génération du rapport CNAM pour la "
                            + "période du " + dateDebut
                            + " au " + dateFin,
                    exception
            );
        }

        return new CnamRapportResponse(dateDebut, dateFin, 0, 0.0, 0.0);
    }
}