package dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
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
    void doitRetournerTrueSiClientExiste()
            throws Exception {

        Connection connection =
                mock(Connection.class);

        PreparedStatement statement =
                mock(PreparedStatement.class);

        ResultSet resultSet =
                mock(ResultSet.class);

        when(
                resultSet.next()
        ).thenReturn(true);

        when(
                connection.prepareStatement(
                        anyString()
                )
        ).thenReturn(statement);

        when(
                statement.executeQuery()
        ).thenReturn(resultSet);

        try (
                MockedStatic<DBConnection> dbConnection =
                        mockStatic(DBConnection.class)
        ) {

            dbConnection.when(
                    DBConnection::getConnection
            ).thenReturn(connection);

            boolean resultat =
                    clientDAO.existeClient(1);

            assertTrue(resultat);

            verify(statement)
                    .setInt(1, 1);

            verify(statement)
                    .executeQuery();
        }
    }

    @Test
    void doitRetournerFalseSiClientNExistePas()
            throws Exception {

        Connection connection =
                mock(Connection.class);

        PreparedStatement statement =
                mock(PreparedStatement.class);

        ResultSet resultSet =
                mock(ResultSet.class);

        when(
                resultSet.next()
        ).thenReturn(false);

        when(
                connection.prepareStatement(
                        anyString()
                )
        ).thenReturn(statement);

        when(
                statement.executeQuery()
        ).thenReturn(resultSet);

        try (
                MockedStatic<DBConnection> dbConnection =
                        mockStatic(DBConnection.class)
        ) {

            dbConnection.when(
                    DBConnection::getConnection
            ).thenReturn(connection);

            boolean resultat =
                    clientDAO.existeClient(1);

            assertFalse(resultat);
        }
    }

    @Test
    void doitLeverExceptionSiVerificationClientEchoue()
            throws Exception {

        Connection connection =
                mock(Connection.class);

        when(
                connection.prepareStatement(
                        anyString()
                )
        ).thenThrow(
                new SQLException("Erreur SQL")
        );

        try (
                MockedStatic<DBConnection> dbConnection =
                        mockStatic(DBConnection.class)
        ) {

            dbConnection.when(
                    DBConnection::getConnection
            ).thenReturn(connection);

            assertThrows(
                    AccesDonneesException.class,
                    () -> clientDAO.existeClient(1)
            );
        }
    }

    @Test
    void doitAjouterClientEtRetournerId()
            throws Exception {

        Connection connection =
                mock(Connection.class);

        PreparedStatement statement =
                mock(PreparedStatement.class);

        ResultSet keys =
                mock(ResultSet.class);

        when(
                connection.prepareStatement(
                        anyString(),
                        eq(
                                Statement.RETURN_GENERATED_KEYS
                        )
                )
        ).thenReturn(statement);

        when(
                statement.getGeneratedKeys()
        ).thenReturn(keys);

        when(
                keys.next()
        ).thenReturn(true);

        when(
                keys.getInt(1)
        ).thenReturn(10);

        try (
                MockedStatic<DBConnection> dbConnection =
                        mockStatic(DBConnection.class)
        ) {

            dbConnection.when(
                    DBConnection::getConnection
            ).thenReturn(connection);

            int resultat =
                    clientDAO.ajouterClient(
                            "Dupont",
                            "Jean",
                            "jean@test.com",
                            "Tunis"
                    );

            assertEquals(
                    10,
                    resultat
            );

            verify(statement)
                    .setString(
                            1,
                            "Dupont"
                    );

            verify(statement)
                    .setString(
                            2,
                            "Jean"
                    );

            verify(statement)
                    .setString(
                            3,
                            "jean@test.com"
                    );

            verify(statement)
                    .setString(
                            4,
                            "Tunis"
                    );

            verify(statement)
                    .executeUpdate();
        }
    }

    @Test
    void doitRetournerIdInvalideSiAucunIdGenere()
            throws Exception {

        Connection connection =
                mock(Connection.class);

        PreparedStatement statement =
                mock(PreparedStatement.class);

        ResultSet keys =
                mock(ResultSet.class);

        when(
                connection.prepareStatement(
                        anyString(),
                        eq(
                                Statement.RETURN_GENERATED_KEYS
                        )
                )
        ).thenReturn(statement);

        when(
                statement.getGeneratedKeys()
        ).thenReturn(keys);

        when(
                keys.next()
        ).thenReturn(false);

        try (
                MockedStatic<DBConnection> dbConnection =
                        mockStatic(DBConnection.class)
        ) {

            dbConnection.when(
                    DBConnection::getConnection
            ).thenReturn(connection);

            int resultat =
                    clientDAO.ajouterClient(
                            "Dupont",
                            "Jean",
                            "jean@test.com",
                            "Tunis"
                    );

            assertEquals(
                    -1,
                    resultat
            );
        }
    }

    @Test
    void doitLeverExceptionSiAjoutEchoue()
            throws Exception {

        Connection connection =
                mock(Connection.class);

        when(
                connection.prepareStatement(
                        anyString(),
                        eq(
                                Statement.RETURN_GENERATED_KEYS
                        )
                )
        ).thenThrow(
                new SQLException("Erreur SQL")
        );

        try (
                MockedStatic<DBConnection> dbConnection =
                        mockStatic(DBConnection.class)
        ) {

            dbConnection.when(
                    DBConnection::getConnection
            ).thenReturn(connection);

            assertThrows(
                    AccesDonneesException.class,
                    () -> clientDAO.ajouterClient(
                            "Dupont",
                            "Jean",
                            "jean@test.com",
                            "Tunis"
                    )
            );
        }
    }
}