package dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import exception.AccesDonneesException;
import utils.DBConnection;
import utils.MotDePasseUtils;

class UtilisateurDAOTest {

    private UtilisateurDAO utilisateurDAO;

    @BeforeEach
    void setUp() {
        utilisateurDAO = new UtilisateurDAO();
    }

    @Test
    void doitRetournerRolePharmacien()
            throws Exception {

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
        ).thenReturn(true);

        when(
                result.getString("pwd")
        ).thenReturn("123");

        try (MockedStatic<DBConnection> dbConnection =
                     Mockito.mockStatic(DBConnection.class)) {

            dbConnection.when(
                    DBConnection::getConnection
            ).thenReturn(connection);

            String role =
                    utilisateurDAO.getRole(
                            "pharma",
                            "123"
                    );

            assertEquals(
                    "PHARMACIEN",
                    role
            );
        }
    }

    @Test
    void doitRetournerRoleAvecMotDePasseHache()
            throws Exception {

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
        ).thenReturn(true);

        String hash =
                MotDePasseUtils.hacher("motDePasse1");

        when(
                result.getString("pwd")
        ).thenReturn(hash);

        try (MockedStatic<DBConnection> dbConnection =
                     Mockito.mockStatic(DBConnection.class)) {

            dbConnection.when(
                    DBConnection::getConnection
            ).thenReturn(connection);

            String role =
                    utilisateurDAO.getRole(
                            "pharma",
                            "motDePasse1"
                    );

            assertEquals(
                    "PHARMACIEN",
                    role
            );
        }
    }

    @Test
    void doitRetournerRoleGestionnaire()
            throws Exception {

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

        /*
         * Premier appel : pharmacien non trouvé.
         * Deuxième appel : gestionnaire trouvé.
         */
        when(
                result.next()
        ).thenReturn(false, true);

        when(
                result.getString("pwd")
        ).thenReturn("123");

        try (MockedStatic<DBConnection> dbConnection =
                     Mockito.mockStatic(DBConnection.class)) {

            dbConnection.when(
                    DBConnection::getConnection
            ).thenReturn(connection);

            String role =
                    utilisateurDAO.getRole(
                            "gestionnaire",
                            "123"
                    );

            assertEquals(
                    "GESTIONNAIRE",
                    role
            );
        }
    }

    @Test
    void doitRetournerEchecSiIdentifiantsInvalides()
            throws Exception {

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
        ).thenReturn(false);

        try (MockedStatic<DBConnection> dbConnection =
                     Mockito.mockStatic(DBConnection.class)) {

            dbConnection.when(
                    DBConnection::getConnection
            ).thenReturn(connection);

            String role =
                    utilisateurDAO.getRole(
                            "inconnu",
                            "incorrect"
                    );

            assertEquals(
                    "ECHEC",
                    role
            );
        }
    }

    @Test
    void doitRetournerEchecSiMotDePasseIncorrect()
            throws Exception {

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

        /* Utilisateur trouvé dans les deux tables,
           mais le mot de passe ne correspond jamais. */
        when(
                result.next()
        ).thenReturn(true, true);

        when(
                result.getString("pwd")
        ).thenReturn("123");

        try (MockedStatic<DBConnection> dbConnection =
                     Mockito.mockStatic(DBConnection.class)) {

            dbConnection.when(
                    DBConnection::getConnection
            ).thenReturn(connection);

            String role =
                    utilisateurDAO.getRole(
                            "pharma",
                            "mauvais-mot-de-passe"
                    );

            assertEquals(
                    "ECHEC",
                    role
            );
        }
    }

    @Test
    void doitLeverAccesDonneesExceptionSiErreurSQL()
            throws Exception {

        Connection connection =
                mock(Connection.class);

        when(
                connection.prepareStatement(anyString())
        ).thenThrow(
                new SQLException("Erreur SQL")
        );

        try (MockedStatic<DBConnection> dbConnection =
                     Mockito.mockStatic(DBConnection.class)) {

            dbConnection.when(
                    DBConnection::getConnection
            ).thenReturn(connection);

            assertThrows(
                    AccesDonneesException.class,
                    () -> utilisateurDAO.getRole(
                            "pharma",
                            "123"
                    )
            );
        }
    }
}