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
 * Tests unitaires pour {@link ClientDAO}.
 *
 * Note : ajouterClient() attrape un Exception générique (pas seulement SQLException),
 * donc une erreur levée par DBConnection.getConnection() (RuntimeException) est
 * également couverte et ne doit jamais se propager.
 */
@ExtendWith(MockitoExtension.class)
class ClientDAOTest {

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement preparedStatement;

    private ClientDAO clientDAO;

    @BeforeEach
    void setUp() {
        clientDAO = new ClientDAO();
    }

    @Test
    void testAjouterClient_Nominal() throws SQLException {
        try (MockedStatic<DBConnection> mockedDb = mockStatic(DBConnection.class)) {
            mockedDb.when(DBConnection::getConnection).thenReturn(connection);
            when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);

            clientDAO.ajouterClient("Trabelsi", "Amine", "amine.trabelsi@mail.com", "Ariana");

            verify(preparedStatement).setString(1, "Trabelsi");
            verify(preparedStatement).setString(2, "Amine");
            verify(preparedStatement).setString(3, "amine.trabelsi@mail.com");
            verify(preparedStatement).setString(4, "Ariana");
            verify(preparedStatement).executeUpdate();
        }
    }

    @Test
    void testAjouterClient_SQLException_NePasPropager() throws SQLException {
        try (MockedStatic<DBConnection> mockedDb = mockStatic(DBConnection.class)) {
            mockedDb.when(DBConnection::getConnection).thenReturn(connection);
            when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
            when(preparedStatement.executeUpdate()).thenThrow(new SQLException("Erreur SQL simulée"));

            assertDoesNotThrow(() ->
                    clientDAO.ajouterClient("Trabelsi", "Amine", "amine.trabelsi@mail.com", "Ariana"));

            verify(preparedStatement).executeUpdate();
        }
    }

    @Test
    void testAjouterClient_ErreurConnexion_NePasPropager() {
        try (MockedStatic<DBConnection> mockedDb = mockStatic(DBConnection.class)) {
            mockedDb.when(DBConnection::getConnection)
                    .thenThrow(new RuntimeException("Connexion impossible"));

            assertDoesNotThrow(() ->
                    clientDAO.ajouterClient("Trabelsi", "Amine", "amine.trabelsi@mail.com", "Ariana"));
        }
    }

    @Test
    void testAjouterClient_PrepareStatementLeveErreur() throws SQLException {
        try (MockedStatic<DBConnection> mockedDb = mockStatic(DBConnection.class)) {
            mockedDb.when(DBConnection::getConnection).thenReturn(connection);
            when(connection.prepareStatement(anyString()))
                    .thenThrow(new SQLException("Erreur préparation requête"));

            assertDoesNotThrow(() ->
                    clientDAO.ajouterClient("Trabelsi", "Amine", "amine.trabelsi@mail.com", "Ariana"));
        }
    }
}