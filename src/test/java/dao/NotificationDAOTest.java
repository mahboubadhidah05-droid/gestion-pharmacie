package dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import dto.NotificationResponse;
import exception.AccesDonneesException;
import utils.DBConnection;

class NotificationDAOTest {

    private NotificationDAO notificationDAO;

    @BeforeEach
    void setUp() {
        notificationDAO = new NotificationDAO();
    }

    @Test
    void doitEnregistrerUneNotification() throws Exception {

        Connection connection =
                mock(Connection.class);

        PreparedStatement statement =
                mock(PreparedStatement.class);

        when(
                connection.prepareStatement(anyString())
        ).thenReturn(statement);

        try (MockedStatic<DBConnection> dbConnection =
                     mockStatic(DBConnection.class)) {

            dbConnection.when(
                    DBConnection::getConnection
            ).thenReturn(connection);

            notificationDAO.enregistrer(
                    "Médicament en stock critique : Paracetamol"
            );

            verify(statement).setString(
                    1,
                    "Médicament en stock critique : Paracetamol"
            );

            verify(statement).executeUpdate();
        }
    }

    @Test
    void doitLeverExceptionSiEnregistrementEchoue()
            throws Exception {

        Connection connection =
                mock(Connection.class);

        when(
                connection.prepareStatement(anyString())
        ).thenThrow(
                new SQLException("Erreur SQL")
        );

        try (MockedStatic<DBConnection> dbConnection =
                     mockStatic(DBConnection.class)) {

            dbConnection.when(
                    DBConnection::getConnection
            ).thenReturn(connection);

            assertThrows(
                    AccesDonneesException.class,
                    () -> notificationDAO.enregistrer("Test")
            );
        }
    }

    @Test
    void doitListerLesNotifications() throws Exception {

        Connection connection =
                mock(Connection.class);

        PreparedStatement statement =
                mock(PreparedStatement.class);

        ResultSet result =
                mock(ResultSet.class);

        when(
                connection.prepareStatement(anyString())
        ).thenReturn(statement);

        when(
                statement.executeQuery()
        ).thenReturn(result);

        when(
                result.next()
        ).thenReturn(true, false);

        when(
                result.getInt("id_notification")
        ).thenReturn(1);

        when(
                result.getString("message")
        ).thenReturn("Médicament en stock critique : Paracetamol");

        when(
                result.getTimestamp("date_creation")
        ).thenReturn(Timestamp.valueOf("2026-07-23 10:00:00"));

        when(
                result.getBoolean("lue")
        ).thenReturn(false);

        try (MockedStatic<DBConnection> dbConnection =
                     mockStatic(DBConnection.class)) {

            dbConnection.when(
                    DBConnection::getConnection
            ).thenReturn(connection);

            List<NotificationResponse> resultat =
                    notificationDAO.lister();

            assertEquals(1, resultat.size());
            assertEquals(
                    "Médicament en stock critique : Paracetamol",
                    resultat.get(0).message()
            );
        }
    }

    @Test
    void doitLeverExceptionSiListeEchoue() throws Exception {

        Connection connection =
                mock(Connection.class);

        when(
                connection.prepareStatement(anyString())
        ).thenThrow(
                new SQLException("Erreur SQL")
        );

        try (MockedStatic<DBConnection> dbConnection =
                     mockStatic(DBConnection.class)) {

            dbConnection.when(
                    DBConnection::getConnection
            ).thenReturn(connection);

            assertThrows(
                    AccesDonneesException.class,
                    () -> notificationDAO.lister()
            );
        }
    }
}