package dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import dto.FournisseurResponse;
import exception.AccesDonneesException;
import utils.DBConnection;

class FournisseurDAOTest {

    private FournisseurDAO fournisseurDAO;

    @BeforeEach
    void setUp() {
        fournisseurDAO = new FournisseurDAO();
    }

    @Test
    void doitAjouterFournisseurEtRetournerId()
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
        ).thenReturn(4);

        try (
                MockedStatic<DBConnection> dbConnection =
                        mockStatic(DBConnection.class)
        ) {

            dbConnection.when(
                    DBConnection::getConnection
            ).thenReturn(connection);

            int resultat =
                    fournisseurDAO.ajouterFournisseur(
                            "Laboratoire Tunisie Pharma",
                            "71234567",
                            "contact@ltp.tn",
                            "Tunis"
                    );

            assertEquals(
                    4,
                    resultat
            );

            verify(statement)
                    .setString(
                            1,
                            "Laboratoire Tunisie Pharma"
                    );

            verify(statement)
                    .setString(
                            2,
                            "71234567"
                    );

            verify(statement)
                    .setString(
                            3,
                            "contact@ltp.tn"
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
                    fournisseurDAO.ajouterFournisseur(
                            "Laboratoire Tunisie Pharma",
                            "71234567",
                            "contact@ltp.tn",
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
                    () -> fournisseurDAO.ajouterFournisseur(
                            "Laboratoire Tunisie Pharma",
                            "71234567",
                            "contact@ltp.tn",
                            "Tunis"
                    )
            );
        }
    }

    @Test
    void doitListerLesFournisseurs()
            throws Exception {

        Connection connection =
                mock(Connection.class);

        PreparedStatement statement =
                mock(PreparedStatement.class);

        ResultSet resultSet =
                mock(ResultSet.class);

        when(
                connection.prepareStatement(
                        anyString()
                )
        ).thenReturn(statement);

        when(
                statement.executeQuery()
        ).thenReturn(resultSet);

        when(
                resultSet.next()
        ).thenReturn(true, false);

        when(
                resultSet.getInt("id_fournisseur")
        ).thenReturn(1);

        when(
                resultSet.getString("nom")
        ).thenReturn("Laboratoire Tunisie Pharma");

        when(
                resultSet.getString("telephone")
        ).thenReturn("71234567");

        when(
                resultSet.getString("email")
        ).thenReturn("contact@ltp.tn");

        when(
                resultSet.getString("adresse")
        ).thenReturn("Tunis");

        try (
                MockedStatic<DBConnection> dbConnection =
                        mockStatic(DBConnection.class)
        ) {

            dbConnection.when(
                    DBConnection::getConnection
            ).thenReturn(connection);

            List<FournisseurResponse> resultat =
                    fournisseurDAO.listerFournisseurs();

            assertEquals(1, resultat.size());
            assertTrue(
                    resultat.get(0).nom()
                            .equals("Laboratoire Tunisie Pharma")
            );
        }
    }

    @Test
    void doitLeverExceptionSiListeEchoue()
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
                    () -> fournisseurDAO.listerFournisseurs()
            );
        }
    }
}