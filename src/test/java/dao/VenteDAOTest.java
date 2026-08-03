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
import java.sql.Timestamp;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import dto.VenteResponse;
import exception.AccesDonneesException;
import utils.DBConnection;

class VenteDAOTest {

    private VenteDAO venteDAO;

    @BeforeEach
    void setUp() {
        venteDAO = new VenteDAO();
    }

    @Test
    void doitEnregistrerUneVenteAvecRemboursement() throws Exception {

        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);

        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(statement.executeUpdate()).thenReturn(1);

        try (MockedStatic<DBConnection> db = mockStatic(DBConnection.class)) {

            db.when(DBConnection::getConnection).thenReturn(connection);

            boolean resultat = venteDAO.enregistrerVente(
                    10, 20, 1, 30, 210.0, 90.0
            );

            assertTrue(resultat);

            verify(statement).setInt(1, 10);
            verify(statement).setInt(2, 20);
            verify(statement).setInt(3, 1);
            verify(statement).setInt(4, 30);
            verify(statement).setDouble(6, 210.0);
            verify(statement).setDouble(7, 90.0);
        }
    }

    @Test
    void doitEnregistrerUneVenteSansRemboursement() throws Exception {

        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);

        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(statement.executeUpdate()).thenReturn(1);

        try (MockedStatic<DBConnection> db = mockStatic(DBConnection.class)) {

            db.when(DBConnection::getConnection).thenReturn(connection);

            boolean resultat = venteDAO.enregistrerVente(
                    10, 20, 1, 30, 0.0, 150.0
            );

            assertTrue(resultat);

            verify(statement).setDouble(6, 0.0);
            verify(statement).setDouble(7, 150.0);
        }
    }

    @Test
    void doitRetournerFalseSiAucuneLigneAffecteeALEnregistrement()
            throws Exception {

        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);

        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(statement.executeUpdate()).thenReturn(0);

        try (MockedStatic<DBConnection> db = mockStatic(DBConnection.class)) {

            db.when(DBConnection::getConnection).thenReturn(connection);

            assertFalse(
                    venteDAO.enregistrerVente(10, 20, 1, 30, 0.0, 150.0)
            );
        }
    }

    @Test
    void doitLeverExceptionSiEnregistrementEchoue() throws Exception {

        Connection connection = mock(Connection.class);

        when(connection.prepareStatement(anyString()))
                .thenThrow(new SQLException("Erreur SQL"));

        try (MockedStatic<DBConnection> db = mockStatic(DBConnection.class)) {

            db.when(DBConnection::getConnection).thenReturn(connection);

            assertThrows(
                    AccesDonneesException.class,
                    () -> venteDAO.enregistrerVente(
                            10, 20, 1, 30, 0.0, 150.0
                    )
            );
        }
    }

    @Test
    void doitAnnulerUneVente() throws Exception {

        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);

        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(statement.executeUpdate()).thenReturn(1);

        try (MockedStatic<DBConnection> db = mockStatic(DBConnection.class)) {

            db.when(DBConnection::getConnection).thenReturn(connection);

            assertTrue(venteDAO.annulerVente(5));

            verify(statement).setInt(1, 5);
        }
    }

    @Test
    void doitRetournerFalseSiAnnulationEchoue() throws Exception {

        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);

        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(statement.executeUpdate()).thenReturn(0);

        try (MockedStatic<DBConnection> db = mockStatic(DBConnection.class)) {

            db.when(DBConnection::getConnection).thenReturn(connection);

            assertFalse(venteDAO.annulerVente(999));
        }
    }

    @Test
    void doitLeverExceptionSiAnnulationEchoue() throws Exception {

        Connection connection = mock(Connection.class);

        when(connection.prepareStatement(anyString()))
                .thenThrow(new SQLException("Erreur SQL"));

        try (MockedStatic<DBConnection> db = mockStatic(DBConnection.class)) {

            db.when(DBConnection::getConnection).thenReturn(connection);

            assertThrows(
                    AccesDonneesException.class,
                    () -> venteDAO.annulerVente(5)
            );
        }
    }

    private void configurerResultSetVente(
            ResultSet result,
            double montantRembourse,
            double ticketModerateur) throws SQLException {

        when(result.next()).thenReturn(true, false);
        when(result.getInt("id_vente")).thenReturn(1);
        when(result.getInt("id_pharmacien")).thenReturn(10);
        when(result.getInt("id_client")).thenReturn(20);
        when(result.getInt("id_medicament")).thenReturn(1);
        when(result.getInt("quantite")).thenReturn(30);
        when(result.getTimestamp("date_vente"))
                .thenReturn(Timestamp.valueOf("2026-07-28 10:00:00"));
        when(result.getDouble("montant_rembourse"))
                .thenReturn(montantRembourse);
        when(result.getDouble("ticket_moderateur"))
                .thenReturn(ticketModerateur);
    }

    @Test
    void doitListerLesVentesParMedicamentAvecRemboursement()
            throws Exception {

        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        ResultSet result = mock(ResultSet.class);

        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(statement.executeQuery()).thenReturn(result);
        configurerResultSetVente(result, 210.0, 90.0);

        try (MockedStatic<DBConnection> db = mockStatic(DBConnection.class)) {

            db.when(DBConnection::getConnection).thenReturn(connection);

            List<VenteResponse> resultat =
                    venteDAO.ventesParMedicament(1);

            assertEquals(1, resultat.size());
            assertEquals(210.0, resultat.get(0).montantRembourse());
            assertEquals(90.0, resultat.get(0).ticketModerateur());
        }
    }

    @Test
    void doitLeverExceptionSiVentesParMedicamentEchoue() throws Exception {

        Connection connection = mock(Connection.class);

        when(connection.prepareStatement(anyString()))
                .thenThrow(new SQLException("Erreur SQL"));

        try (MockedStatic<DBConnection> db = mockStatic(DBConnection.class)) {

            db.when(DBConnection::getConnection).thenReturn(connection);

            assertThrows(
                    AccesDonneesException.class,
                    () -> venteDAO.ventesParMedicament(1)
            );
        }
    }

    @Test
    void doitListerLesVentesParNomMedicament() throws Exception {

        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        ResultSet result = mock(ResultSet.class);

        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(statement.executeQuery()).thenReturn(result);
        configurerResultSetVente(result, 0.0, 150.0);

        try (MockedStatic<DBConnection> db = mockStatic(DBConnection.class)) {

            db.when(DBConnection::getConnection).thenReturn(connection);

            List<VenteResponse> resultat =
                    venteDAO.ventesParNomMedicament("Paracetamol");

            assertEquals(1, resultat.size());

            verify(statement).setString(1, "Paracetamol");
        }
    }

    @Test
    void doitLeverExceptionSiVentesParNomMedicamentEchoue()
            throws Exception {

        Connection connection = mock(Connection.class);

        when(connection.prepareStatement(anyString()))
                .thenThrow(new SQLException("Erreur SQL"));

        try (MockedStatic<DBConnection> db = mockStatic(DBConnection.class)) {

            db.when(DBConnection::getConnection).thenReturn(connection);

            assertThrows(
                    AccesDonneesException.class,
                    () -> venteDAO.ventesParNomMedicament("Paracetamol")
            );
        }
    }

    @Test
    void doitListerLesVentesParNomClient() throws Exception {

        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        ResultSet result = mock(ResultSet.class);

        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(statement.executeQuery()).thenReturn(result);
        configurerResultSetVente(result, 0.0, 150.0);

        try (MockedStatic<DBConnection> db = mockStatic(DBConnection.class)) {

            db.when(DBConnection::getConnection).thenReturn(connection);

            List<VenteResponse> resultat =
                    venteDAO.ventesParNomClient("Ben Ali", "Sami");

            assertEquals(1, resultat.size());

            verify(statement).setString(1, "Ben Ali");
            verify(statement).setString(2, "Sami");
        }
    }

    @Test
    void doitLeverExceptionSiVentesParNomClientEchoue() throws Exception {

        Connection connection = mock(Connection.class);

        when(connection.prepareStatement(anyString()))
                .thenThrow(new SQLException("Erreur SQL"));

        try (MockedStatic<DBConnection> db = mockStatic(DBConnection.class)) {

            db.when(DBConnection::getConnection).thenReturn(connection);

            assertThrows(
                    AccesDonneesException.class,
                    () -> venteDAO.ventesParNomClient("Ben Ali", "Sami")
            );
        }
    }

    @Test
    void doitListerLesVentesParClient() throws Exception {

        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        ResultSet result = mock(ResultSet.class);

        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(statement.executeQuery()).thenReturn(result);
        configurerResultSetVente(result, 0.0, 150.0);

        try (MockedStatic<DBConnection> db = mockStatic(DBConnection.class)) {

            db.when(DBConnection::getConnection).thenReturn(connection);

            List<VenteResponse> resultat =
                    venteDAO.ventesParClient(20);

            assertEquals(1, resultat.size());
        }
    }

    @Test
    void doitLeverExceptionSiVentesParClientEchoue() throws Exception {

        Connection connection = mock(Connection.class);

        when(connection.prepareStatement(anyString()))
                .thenThrow(new SQLException("Erreur SQL"));

        try (MockedStatic<DBConnection> db = mockStatic(DBConnection.class)) {

            db.when(DBConnection::getConnection).thenReturn(connection);

            assertThrows(
                    AccesDonneesException.class,
                    () -> venteDAO.ventesParClient(20)
            );
        }
    }

    @Test
    void doitListerLesVentesParPeriode() throws Exception {

        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        ResultSet result = mock(ResultSet.class);

        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(statement.executeQuery()).thenReturn(result);
        configurerResultSetVente(result, 0.0, 150.0);

        try (MockedStatic<DBConnection> db = mockStatic(DBConnection.class)) {

            db.when(DBConnection::getConnection).thenReturn(connection);

            List<VenteResponse> resultat = venteDAO.ventesParPeriode(
                    "2026-01-01", "2026-01-31"
            );

            assertEquals(1, resultat.size());

            verify(statement).setString(1, "2026-01-01");
            verify(statement).setString(2, "2026-01-31 23:59:59");
        }
    }

    @Test
    void doitLeverExceptionSiVentesParPeriodeEchoue() throws Exception {

        Connection connection = mock(Connection.class);

        when(connection.prepareStatement(anyString()))
                .thenThrow(new SQLException("Erreur SQL"));

        try (MockedStatic<DBConnection> db = mockStatic(DBConnection.class)) {

            db.when(DBConnection::getConnection).thenReturn(connection);

            assertThrows(
                    AccesDonneesException.class,
                    () -> venteDAO.ventesParPeriode(
                            "2026-01-01", "2026-01-31"
                    )
            );
        }
    }
}