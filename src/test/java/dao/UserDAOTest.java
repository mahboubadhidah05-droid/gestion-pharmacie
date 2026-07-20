package dao;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
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

class UserDAOTest {

    private UserDAO userDAO;

    @BeforeEach
    void setUp() {
        userDAO = new UserDAO();
    }

    @Test
    void doitRetournerProfilPharmacien()
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
                result.getString("nom")
        ).thenReturn("Dupont");

        when(
                result.getString("prenom")
        ).thenReturn("Jean");

        try (MockedStatic<DBConnection> dbConnection =
                     Mockito.mockStatic(DBConnection.class)) {

            dbConnection.when(
                    DBConnection::getConnection
            ).thenReturn(connection);

            String[] profil =
                    userDAO.getProfil("jean");

            assertArrayEquals(
                    new String[]{
                            "Dupont",
                            "Jean"
                    },
                    profil
            );
        }
    }

    @Test
    void doitRetournerProfilGestionnaire()
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
         * Premier appel : pharmacien absent.
         * Deuxième appel : gestionnaire trouvé.
         */
        when(
                result.next()
        ).thenReturn(false, true);

        when(
                result.getString("nom")
        ).thenReturn("Martin");

        when(
                result.getString("prenom")
        ).thenReturn("Paul");

        try (MockedStatic<DBConnection> dbConnection =
                     Mockito.mockStatic(DBConnection.class)) {

            dbConnection.when(
                    DBConnection::getConnection
            ).thenReturn(connection);

            String[] profil =
                    userDAO.getProfil("paul");

            assertArrayEquals(
                    new String[]{
                            "Martin",
                            "Paul"
                    },
                    profil
            );
        }
    }

    @Test
    void doitRetournerProfilVideSiUtilisateurIntrouvable()
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

            String[] profil =
                    userDAO.getProfil("inconnu");

            assertArrayEquals(
                    new String[0],
                    profil
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
                    () -> userDAO.getProfil("jean")
            );
        }
    }
}