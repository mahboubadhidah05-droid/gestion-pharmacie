package dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import utils.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests unitaires pour {@link CommandeDAO}.
 *
 * Note : creerCommande() n'attrape que SQLException ; on suppose donc que
 * DBConnection.getConnection() déclare "throws SQLException".
 */
@ExtendWith(MockitoExtension.class)
class CommandeDAOTest {

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement preparedStatement;

    private CommandeDAO commandeDAO;

    @BeforeEach
    void setUp() {
        commandeDAO = new CommandeDAO();
    }

    @Test
    void testCreerCommande_Nominal() throws SQLException {
        try (MockedStatic<DBConnection> mockedDb = mockStatic(DBConnection.class)) {
            mockedDb.when(DBConnection::getConnection).thenReturn(connection);
            when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);

            commandeDAO.creerCommande(1, 10, 25);

            verify(preparedStatement).setInt(1, 1);
            verify(preparedStatement).setInt(2, 10);
            verify(preparedStatement).setInt(3, 25);
            verify(preparedStatement).executeUpdate();
        }
    }

    @Test
    void testCreerCommande_SQLException_NePasPropager() throws SQLException {
        try (MockedStatic<DBConnection> mockedDb = mockStatic(DBConnection.class)) {
            mockedDb.when(DBConnection::getConnection).thenReturn(connection);
            when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
            when(preparedStatement.executeUpdate()).thenThrow(new SQLException("Erreur SQL simulée"));

            assertDoesNotThrow(() -> commandeDAO.creerCommande(1, 10, 25));

            verify(preparedStatement).executeUpdate();
        }
    }

    @Test
    void testCreerCommande_ErreurConnexion_NePasPropager() throws SQLException {
        try (MockedStatic<DBConnection> mockedDb = mockStatic(DBConnection.class)) {
            mockedDb.when(DBConnection::getConnection)
                    .thenThrow(new SQLException("Connexion impossible"));

            assertDoesNotThrow(() -> commandeDAO.creerCommande(1, 10, 25));
        }
    }

    @Test
    void testCreerCommande_PrepareStatementLeveErreur() throws SQLException {
        try (MockedStatic<DBConnection> mockedDb = mockStatic(DBConnection.class)) {
            mockedDb.when(DBConnection::getConnection).thenReturn(connection);
            when(connection.prepareStatement(anyString()))
                    .thenThrow(new SQLException("Erreur préparation requête"));

            assertDoesNotThrow(() -> commandeDAO.creerCommande(1, 10, 25));
        }
    }
}
