package dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import dto.VenteResponse;
import exception.AccesDonneesException;
import utils.DBConnection;

class VenteDAOTest {

    @Test
    void enregistrerVenteDoitRetournerTrueSiInsertionReussie()
            throws Exception {

        Connection connection =
                mock(Connection.class);

        PreparedStatement statement =
                mock(PreparedStatement.class);

        when(
                connection.prepareStatement(
                        Mockito.anyString()
                )
        ).thenReturn(statement);

        when(
                statement.executeUpdate()
        ).thenReturn(1);

        try (
                MockedStatic<DBConnection> db =
                        Mockito.mockStatic(DBConnection.class)
        ) {
            db.when(
                    DBConnection::getConnection
            ).thenReturn(connection);

            VenteDAO dao =
                    new VenteDAO();

            boolean resultat =
                    dao.enregistrerVente(
                            1,
                            2,
                            3,
                            5
                    );

            assertTrue(resultat);
        }
    }

    @Test
    void enregistrerVenteDoitRetournerFalseSiAucuneLigneInseree()
            throws Exception {

        Connection connection =
                mock(Connection.class);

        PreparedStatement statement =
                mock(PreparedStatement.class);

        when(
                connection.prepareStatement(
                        Mockito.anyString()
                )
        ).thenReturn(statement);

        when(
                statement.executeUpdate()
        ).thenReturn(0);

        try (
                MockedStatic<DBConnection> db =
                        Mockito.mockStatic(DBConnection.class)
        ) {
            db.when(
                    DBConnection::getConnection
            ).thenReturn(connection);

            VenteDAO dao =
                    new VenteDAO();

            boolean resultat =
                    dao.enregistrerVente(
                            1,
                            2,
                            3,
                            5
                    );

            assertFalse(resultat);
        }
    }

    @Test
    void enregistrerVenteDoitLeverExceptionSiErreurSQL()
            throws Exception {

        Connection connection =
                mock(Connection.class);

        when(
                connection.prepareStatement(
                        Mockito.anyString()
                )
        ).thenThrow(
                new java.sql.SQLException(
                        "Erreur SQL"
                )
        );

        try (
                MockedStatic<DBConnection> db =
                        Mockito.mockStatic(DBConnection.class)
        ) {
            db.when(
                    DBConnection::getConnection
            ).thenReturn(connection);

            VenteDAO dao =
                    new VenteDAO();

            assertThrows(
                    AccesDonneesException.class,
                    () -> dao.enregistrerVente(
                            1,
                            2,
                            3,
                            5
                    )
            );
        }
    }

    @Test
    void annulerVenteDoitRetournerTrueSiVenteSupprimee()
            throws Exception {

        Connection connection =
                mock(Connection.class);

        PreparedStatement statement =
                mock(PreparedStatement.class);

        when(
                connection.prepareStatement(
                        Mockito.anyString()
                )
        ).thenReturn(statement);

        when(
                statement.executeUpdate()
        ).thenReturn(1);

        try (
                MockedStatic<DBConnection> db =
                        Mockito.mockStatic(DBConnection.class)
        ) {
            db.when(
                    DBConnection::getConnection
            ).thenReturn(connection);

            VenteDAO dao =
                    new VenteDAO();

            assertTrue(
                    dao.annulerVente(1)
            );
        }
    }

    @Test
    void annulerVenteDoitRetournerFalseSiVenteIntrouvable()
            throws Exception {

        Connection connection =
                mock(Connection.class);

        PreparedStatement statement =
                mock(PreparedStatement.class);

        when(
                connection.prepareStatement(
                        Mockito.anyString()
                )
        ).thenReturn(statement);

        when(
                statement.executeUpdate()
        ).thenReturn(0);

        try (
                MockedStatic<DBConnection> db =
                        Mockito.mockStatic(DBConnection.class)
        ) {
            db.when(
                    DBConnection::getConnection
            ).thenReturn(connection);

            VenteDAO dao =
                    new VenteDAO();

            assertFalse(
                    dao.annulerVente(999)
            );
        }
    }

    @Test
    void ventesParMedicamentDoitRetournerLesVentes()
            throws Exception {

        Connection connection =
                mock(Connection.class);

        PreparedStatement statement =
                mock(PreparedStatement.class);

        ResultSet resultSet =
                mock(ResultSet.class);

        when(
                connection.prepareStatement(
                        Mockito.anyString()
                )
        ).thenReturn(statement);

        when(
                statement.executeQuery()
        ).thenReturn(resultSet);

        when(
                resultSet.next()
        ).thenReturn(true, false);

        when(
                resultSet.getInt("id_vente")
        ).thenReturn(1);

        when(
                resultSet.getInt("id_pharmacien")
        ).thenReturn(10);

        when(
                resultSet.getInt("id_client")
        ).thenReturn(20);

        when(
                resultSet.getInt("id_medicament")
        ).thenReturn(30);

        when(
                resultSet.getInt("quantite")
        ).thenReturn(2);

        when(
                resultSet.getTimestamp("date_vente")
        ).thenReturn(
                Timestamp.valueOf(
                        LocalDateTime.of(
                                2026,
                                7,
                                20,
                                10,
                                0
                        )
                )
        );

        try (
                MockedStatic<DBConnection> db =
                        Mockito.mockStatic(DBConnection.class)
        ) {
            db.when(
                    DBConnection::getConnection
            ).thenReturn(connection);

            VenteDAO dao =
                    new VenteDAO();

            List<VenteResponse> ventes =
                    dao.ventesParMedicament(30);

            assertEquals(
                    1,
                    ventes.size()
            );
        }
    }

    @Test
    void ventesParClientDoitRetournerLesVentes()
            throws Exception {

        Connection connection =
                mock(Connection.class);

        PreparedStatement statement =
                mock(PreparedStatement.class);

        ResultSet resultSet =
                mock(ResultSet.class);

        when(
                connection.prepareStatement(
                        Mockito.anyString()
                )
        ).thenReturn(statement);

        when(
                statement.executeQuery()
        ).thenReturn(resultSet);

        when(
                resultSet.next()
        ).thenReturn(false);

        try (
                MockedStatic<DBConnection> db =
                        Mockito.mockStatic(DBConnection.class)
        ) {
            db.when(
                    DBConnection::getConnection
            ).thenReturn(connection);

            VenteDAO dao =
                    new VenteDAO();

            List<VenteResponse> ventes =
                    dao.ventesParClient(20);

            assertTrue(
                    ventes.isEmpty()
            );
        }
    }

    @Test
    void ventesParPeriodeDoitRetournerLesVentes()
            throws Exception {

        Connection connection =
                mock(Connection.class);

        PreparedStatement statement =
                mock(PreparedStatement.class);

        ResultSet resultSet =
                mock(ResultSet.class);

        when(
                connection.prepareStatement(
                        Mockito.anyString()
                )
        ).thenReturn(statement);

        when(
                statement.executeQuery()
        ).thenReturn(resultSet);

        when(
                resultSet.next()
        ).thenReturn(false);

        try (
                MockedStatic<DBConnection> db =
                        Mockito.mockStatic(DBConnection.class)
        ) {
            db.when(
                    DBConnection::getConnection
            ).thenReturn(connection);

            VenteDAO dao =
                    new VenteDAO();

            List<VenteResponse> ventes =
                    dao.ventesParPeriode(
                            "2026-07-01",
                            "2026-07-20"
                    );

            assertTrue(
                    ventes.isEmpty()
            );
        }
    }

    @Test
    void ventesParMedicamentDoitLeverExceptionSiErreurSQL()
            throws Exception {

        Connection connection =
                mock(Connection.class);

        when(
                connection.prepareStatement(
                        Mockito.anyString()
                )
        ).thenThrow(
                new java.sql.SQLException(
                        "Erreur SQL"
                )
        );

        try (
                MockedStatic<DBConnection> db =
                        Mockito.mockStatic(DBConnection.class)
        ) {
            db.when(
                    DBConnection::getConnection
            ).thenReturn(connection);

            VenteDAO dao =
                    new VenteDAO();

            assertThrows(
                    AccesDonneesException.class,
                    () -> dao.ventesParMedicament(1)
            );
        }
    }
}