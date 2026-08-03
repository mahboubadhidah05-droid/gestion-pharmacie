package service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import utils.DBConnection;

class NotificationServiceTest {

    @Test
    void neDoitPasLeverExceptionAvecListeNull() {

        assertDoesNotThrow(
                () -> NotificationService.envoyerEmail(null)
        );
    }

    @Test
    void neDoitPasLeverExceptionAvecListeVide() {

        assertDoesNotThrow(
                () -> NotificationService.envoyerEmail(
                        List.of()
                )
        );
    }

    @Test
    void doitEnvoyerNotificationsPourMedicamentsCritiques()
            throws Exception {

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

            List<String> medicamentsCritiques =
                    List.of(
                            "Paracétamol",
                            "Amoxicilline"
                    );

            assertDoesNotThrow(
                    () -> NotificationService.envoyerEmail(
                            medicamentsCritiques
                    )
            );
        }
    }

    @Test
    void doitNotifierUnMedicamentCritique()
            throws Exception {

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

            assertDoesNotThrow(
                    () -> NotificationService.notifierStockCritique(
                            "Paracétamol"
                    )
            );
        }
    }
}