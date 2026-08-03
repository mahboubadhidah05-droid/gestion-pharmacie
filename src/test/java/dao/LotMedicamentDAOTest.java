package dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
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
import java.sql.Statement;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import dto.LotResponse;
import exception.AccesDonneesException;
import utils.DBConnection;

class LotMedicamentDAOTest {

    private LotMedicamentDAO dao;

    @BeforeEach
    void setUp() {
        dao = new LotMedicamentDAO();
    }

    @Test
    void doitAjouterUnLotEtRetournerId() throws Exception {

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
        when(cles.getInt(1)).thenReturn(5);

        try (MockedStatic<DBConnection> db = mockStatic(DBConnection.class)) {

            db.when(DBConnection::getConnection).thenReturn(connection);

            int resultat = dao.ajouterLot(1, 50, "2027-01-01");

            assertEquals(5, resultat);

            verify(statement).setInt(1, 1);
            verify(statement).setInt(2, 50);
            verify(statement).setString(3, "2027-01-01");
        }
    }

    @Test
    void doitLeverExceptionSiAjoutLotEchoue() throws Exception {

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
                    () -> dao.ajouterLot(1, 50, "2027-01-01")
            );
        }
    }

    @Test
    void doitRetournerLesLotsDisponibles() throws Exception {

        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        ResultSet result = mock(ResultSet.class);

        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(statement.executeQuery()).thenReturn(result);
        when(result.next()).thenReturn(true, false);
        when(result.getInt("id_lot")).thenReturn(1);
        when(result.getInt("id_medicament")).thenReturn(1);
        when(result.getInt("quantite")).thenReturn(30);
        when(result.getString("date_peremption")).thenReturn("2026-08-10");

        try (MockedStatic<DBConnection> db = mockStatic(DBConnection.class)) {

            db.when(DBConnection::getConnection).thenReturn(connection);

            List<LotResponse> resultat = dao.getLotsDisponibles(1);

            assertEquals(1, resultat.size());
            assertEquals(30, resultat.get(0).quantite());
        }
    }

    @Test
    void doitLeverExceptionSiRecuperationLotsEchoue() throws Exception {

        Connection connection = mock(Connection.class);

        when(connection.prepareStatement(anyString()))
                .thenThrow(new SQLException("Erreur SQL"));

        try (MockedStatic<DBConnection> db = mockStatic(DBConnection.class)) {

            db.when(DBConnection::getConnection).thenReturn(connection);

            assertThrows(
                    AccesDonneesException.class,
                    () -> dao.getLotsDisponibles(1)
            );
        }
    }

    @Test
    void doitRetournerLesLotsExpires() throws Exception {

        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        ResultSet result = mock(ResultSet.class);

        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(statement.executeQuery()).thenReturn(result);
        when(result.next()).thenReturn(true, false);
        when(result.getInt("id_lot")).thenReturn(2);
        when(result.getInt("id_medicament")).thenReturn(1);
        when(result.getInt("quantite")).thenReturn(10);
        when(result.getString("date_peremption")).thenReturn("2020-01-01");

        try (MockedStatic<DBConnection> db = mockStatic(DBConnection.class)) {

            db.when(DBConnection::getConnection).thenReturn(connection);

            List<LotResponse> resultat =
                    dao.getLotsExpires(1, "2026-07-27");

            assertEquals(1, resultat.size());
            verify(statement).setString(2, "2026-07-27");
        }
    }

    @Test
    void doitMettreAJourLaQuantiteDUnLot() throws Exception {

        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);

        when(connection.prepareStatement(anyString())).thenReturn(statement);

        try (MockedStatic<DBConnection> db = mockStatic(DBConnection.class)) {

            db.when(DBConnection::getConnection).thenReturn(connection);

            dao.mettreAJourQuantiteLot(1, 0);

            verify(statement).setInt(1, 0);
            verify(statement).setInt(2, 1);
            verify(statement).executeUpdate();
        }
    }

    @Test
    void doitLeverExceptionSiMiseAJourQuantiteEchoue() throws Exception {

        Connection connection = mock(Connection.class);

        when(connection.prepareStatement(anyString()))
                .thenThrow(new SQLException("Erreur SQL"));

        try (MockedStatic<DBConnection> db = mockStatic(DBConnection.class)) {

            db.when(DBConnection::getConnection).thenReturn(connection);

            assertThrows(
                    AccesDonneesException.class,
                    () -> dao.mettreAJourQuantiteLot(1, 0)
            );
        }
    }

    @Test
    void doitRetournerLaDatePeremptionLaPlusProche() throws Exception {

        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        ResultSet result = mock(ResultSet.class);

        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(statement.executeQuery()).thenReturn(result);
        when(result.next()).thenReturn(true);
        when(result.getString("date_min")).thenReturn("2026-08-10");

        try (MockedStatic<DBConnection> db = mockStatic(DBConnection.class)) {

            db.when(DBConnection::getConnection).thenReturn(connection);

            assertEquals(
                    "2026-08-10",
                    dao.getDatePeremptionLaPlusProche(1)
            );
        }
    }

    @Test
    void doitRetournerNullSiAucuneDateDisponible() throws Exception {

        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        ResultSet result = mock(ResultSet.class);

        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(statement.executeQuery()).thenReturn(result);
        when(result.next()).thenReturn(false);

        try (MockedStatic<DBConnection> db = mockStatic(DBConnection.class)) {

            db.when(DBConnection::getConnection).thenReturn(connection);

            assertNull(dao.getDatePeremptionLaPlusProche(1));
        }
    }

    @Test
    void doitLeverExceptionSiRecuperationDatePlusProcheEchoue()
            throws Exception {

        Connection connection = mock(Connection.class);

        when(connection.prepareStatement(anyString()))
                .thenThrow(new SQLException("Erreur SQL"));

        try (MockedStatic<DBConnection> db = mockStatic(DBConnection.class)) {

            db.when(DBConnection::getConnection).thenReturn(connection);

            assertThrows(
                    AccesDonneesException.class,
                    () -> dao.getDatePeremptionLaPlusProche(1)
            );
        }
    }

    @Test
    void doitRetournerLaQuantitePerimee() throws Exception {

        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        ResultSet result = mock(ResultSet.class);

        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(statement.executeQuery()).thenReturn(result);
        when(result.next()).thenReturn(true);
        when(result.getInt("total")).thenReturn(10);

        try (MockedStatic<DBConnection> db = mockStatic(DBConnection.class)) {

            db.when(DBConnection::getConnection).thenReturn(connection);

            int resultat = dao.getQuantitePerimee(1, "2026-07-28");

            assertEquals(10, resultat);
            verify(statement).setString(2, "2026-07-28");
        }
    }

    @Test
    void doitRetournerZeroSiAucuneQuantitePerimee() throws Exception {

        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        ResultSet result = mock(ResultSet.class);

        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(statement.executeQuery()).thenReturn(result);
        when(result.next()).thenReturn(false);

        try (MockedStatic<DBConnection> db = mockStatic(DBConnection.class)) {

            db.when(DBConnection::getConnection).thenReturn(connection);

            assertEquals(0, dao.getQuantitePerimee(1, "2026-07-28"));
        }
    }

    @Test
    void doitLeverExceptionSiRecuperationQuantitePerimeeEchoue()
            throws Exception {

        Connection connection = mock(Connection.class);

        when(connection.prepareStatement(anyString()))
                .thenThrow(new SQLException("Erreur SQL"));

        try (MockedStatic<DBConnection> db = mockStatic(DBConnection.class)) {

            db.when(DBConnection::getConnection).thenReturn(connection);

            assertThrows(
                    AccesDonneesException.class,
                    () -> dao.getQuantitePerimee(1, "2026-07-28")
            );
        }
    }
}