package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import dto.NotificationResponse;
import exception.AccesDonneesException;
import utils.DBConnection;

public class NotificationDAO {

    private static final String INSERT_NOTIFICATION =
            "INSERT INTO notification (message) VALUES (?)";

    private static final String SELECT_NOTIFICATIONS =
            "SELECT * FROM notification ORDER BY id_notification DESC";

    public void enregistrer(String message) {

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(INSERT_NOTIFICATION)) {

            statement.setString(1, message);
            statement.executeUpdate();

        } catch (SQLException exception) {

            throw new AccesDonneesException(
                    "Échec de l'enregistrement de la notification : " + message,
                    exception
            );
        }
    }

    public List<NotificationResponse> lister() {

        List<NotificationResponse> notifications =
                new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(SELECT_NOTIFICATIONS);
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {

                notifications.add(
                        new NotificationResponse(
                                result.getInt("id_notification"),
                                result.getString("message"),
                                result.getTimestamp("date_creation"),
                                result.getBoolean("lue")
                        )
                );
            }

        } catch (SQLException exception) {

            throw new AccesDonneesException(
                    "Échec de la récupération des notifications",
                    exception
            );
        }

        return notifications;
    }
}