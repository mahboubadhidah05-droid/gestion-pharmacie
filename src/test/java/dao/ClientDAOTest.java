package dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import exception.AccesDonneesException;
import utils.DBConnection;

class ClientDAOTest {

    private ClientDAO clientDAO;

    @BeforeEach
    void setUp() {
        clientDAO = new ClientDAO();
    }

    @Test
    void doitDetecterClientExistant() throws Exception {

        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        ResultSet result = mock(ResultSet.class);

        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(statement.executeQuery()).thenReturn(result);
        when(result.next()).thenReturn(true);

        try (MockedStatic<DBConnection> db = mockStatic(DBConnection.class)) {

            db.when(DBConnection::getConnection).thenReturn(connection);

            assertTrue(clientDAO.existeClient(1));
        }
    }

    @Test
    void doitDetecterClientInexistant() throws Exception {

        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        ResultSet result = mock(ResultSet.class);

        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(statement.executeQuery()).thenReturn(result);
        when(result.next()).thenReturn(false);

        try (MockedStatic<DBConnection> db = mockStatic(DBConnection.class)) {

            db.when(DBConnection::getConnection).thenReturn(connection);

            assertFalse(clientDAO.existeClient(999));
        }
    }

    @Test
    void doitLeverExceptionSiVerificationExistenceEchoue() throws Exception {

        Connection connection = mock(Connection.class);

        when(connection.prepareStatement(anyString()))
                .thenThrow(new SQLException("Erreur SQL"));

        try (MockedStatic<DBConnection> db = mockStatic(DBConnection.class)) {

            db.when(DBConnection::getConnection).thenReturn(connection);

            assertThrows(
                    AccesDonneesException.class,
                    () -> clientDAO.existeClient(1)
            );
        }
    }

    @Test
    void doitAjouterUnClientEtRetournerId() throws Exception {

        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        ResultSet cles = mock(ResultSet.class);

        when(
                connection.prepareStatement(
                        anyString(), org.mockito.ArgumentMatchers.eq(
                                Statement.RETURN_GENERATED_KEYS)
                )
        ).thenReturn(statement);

        when(statement.getGeneratedKeys()).thenReturn(cles);
        when(cles.next()).thenReturn(true);
        when(cles.getInt(1)).thenReturn(7);

        try (MockedStatic<DBConnection> db = mockStatic(DBConnection.class)) {

            db.when(DBConnection::getConnection).thenReturn(connection);

            int resultat = clientDAO.ajouterClient(
                    "Ben Ali", "Sami", "sami@exemple.com",
                    "Rue de la Paix", "CNAM12345"
            );

            assertEquals(7, resultat);

            verify(statement).setString(1, "Ben Ali");
            verify(statement).setString(2, "Sami");
            verify(statement).setString(3, "sami@exemple.com");
            verify(statement).setString(4, "Rue de la Paix");
            verify(statement).setString(5, "CNAM12345");
        }
    }

    @Test
    void doitAjouterUnClientSansNumeroCnam() throws Exception {

        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        ResultSet cles = mock(ResultSet.class);

        when(
                connection.prepareStatement(
                        anyString(), org.mockito.ArgumentMatchers.eq(
                                Statement.RETURN_GENERATED_KEYS)
                )
        ).thenReturn(statement);

        when(statement.getGeneratedKeys()).thenReturn(cles);
        when(cles.next()).thenReturn(true);
        when(cles.getInt(1)).thenReturn(8);

        try (MockedStatic<DBConnection> db = mockStatic(DBConnection.class)) {

            db.when(DBConnection::getConnection).thenReturn(connection);

            int resultat = clientDAO.ajouterClient(
                    "Trabelsi", "Amel", "amel@exemple.com",
                    "Avenue Habib Bourguiba", null
            );

            assertEquals(8, resultat);

            verify(statement).setString(5, null);
        }
    }

    @Test
    void doitRetournerMoinsUnSiAucuneCleGeneree() throws Exception {

        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        ResultSet cles = mock(ResultSet.class);

        when(
                connection.prepareStatement(
                        anyString(), org.mockito.ArgumentMatchers.eq(
                                Statement.RETURN_GENERATED_KEYS)
                )
        ).thenReturn(statement);

        when(statement.getGeneratedKeys()).thenReturn(cles);
        when(cles.next()).thenReturn(false);

        try (MockedStatic<DBConnection> db = mockStatic(DBConnection.class)) {

            db.when(DBConnection::getConnection).thenReturn(connection);

            int resultat = clientDAO.ajouterClient(
                    "Nom", "Prenom", "email@exemple.com", "Adresse", null
            );

            assertEquals(-1, resultat);
        }
    }

    @Test
    void doitLeverExceptionSiAjoutClientEchoue() throws Exception {

        Connection connection = mock(Connection.class);

        when(
                connection.prepareStatement(
                        anyString(), org.mockito.ArgumentMatchers.eq(
                                Statement.RETURN_GENERATED_KEYS)
                )
        ).thenThrow(new SQLException("Erreur SQL"));

        try (MockedStatic<DBConnection> db = mockStatic(DBConnection.class)) {

            db.when(DBConnection::getConnection).thenReturn(connection);

            assertThrows(
                    AccesDonneesException.class,
                    () -> clientDAO.ajouterClient(
                            "Nom", "Prenom", "email@exemple.com",
                            "Adresse", "CNAM999"
                    )
            );
        }
    }
}