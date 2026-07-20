package dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import dto.VenteResponse;
import exception.AccesDonneesException;
import utils.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests unitaires pour {@link VenteDAO}.
 *
 * ventesParMedicament() et ventesParClient() délèguent toutes deux à la méthode
 * privée chercherVentes(sql, id), elle-même basée sur remplirVentes(rs, liste) :
 * on couvre donc le chemin "résultats trouvés" / "aucun résultat" / "erreur SQL"
 * une fois via ventesParMedicament (le comportement de ventesParClient étant
 * strictement identique côté DAO) et on ajoute un test dédié pour ventesParClient
 * afin de couvrir explicitement cette méthode publique.
 * Les erreurs SQL lèvent désormais AccesDonneesException (contrat web : 500).
 */
@ExtendWith(MockitoExtension.class)
class VenteDAOTest {

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private ResultSet resultSet;

    private VenteDAO venteDAO;

    @BeforeEach
    void setUp() {
        venteDAO = new VenteDAO();
    }

    // ---------- enregistrerVente ----------

    @Test
    void testEnregistrerVente_Nominal() throws SQLException {
        try (MockedStatic<DBConnection> mockedDb = mockStatic(DBConnection.class)) {
            mockedDb.when(DBConnection::getConnection).thenReturn(connection);
            when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
            when(preparedStatement.executeUpdate()).thenReturn(1);

            venteDAO.enregistrerVente(1, 2, 3, 10);

            verify(preparedStatement).setInt(1, 1);
            verify(preparedStatement).setInt(2, 2);
            verify(preparedStatement).setInt(3, 3);
            verify(preparedStatement).setInt(4, 10);
            verify(preparedStatement).setTimestamp(org.mockito.ArgumentMatchers.eq(5), org.mockito.ArgumentMatchers.any(java.sql.Timestamp.class));
            verify(preparedStatement).executeUpdate();
        }
    }

    @Test
    void testEnregistrerVente_SQLException_DoitLeverAccesDonneesException() throws SQLException {
        try (MockedStatic<DBConnection> mockedDb = mockStatic(DBConnection.class)) {
            mockedDb.when(DBConnection::getConnection).thenReturn(connection);
            when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
            when(preparedStatement.executeUpdate()).thenThrow(new SQLException("Erreur SQL simulée"));

            assertThrows(AccesDonneesException.class,
                    () -> venteDAO.enregistrerVente(1, 2, 3, 10));
        }
    }

    // ---------- annulerVente ----------

    @Test
    void testAnnulerVente_Nominal() throws SQLException {
        try (MockedStatic<DBConnection> mockedDb = mockStatic(DBConnection.class)) {
            mockedDb.when(DBConnection::getConnection).thenReturn(connection);
            when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
            when(preparedStatement.executeUpdate()).thenReturn(1);

            boolean resultat = venteDAO.annulerVente(5);

            assertTrue(resultat);
            verify(preparedStatement).setInt(1, 5);
            verify(preparedStatement).executeUpdate();
        }
    }

    @Test
    void testAnnulerVente_AucuneLigneAffectee() throws SQLException {
        try (MockedStatic<DBConnection> mockedDb = mockStatic(DBConnection.class)) {
            mockedDb.when(DBConnection::getConnection).thenReturn(connection);
            when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
            when(preparedStatement.executeUpdate()).thenReturn(0);

            boolean resultat = venteDAO.annulerVente(999);

            assertFalse(resultat);
        }
    }

    @Test
    void testAnnulerVente_SQLException_DoitLeverAccesDonneesException() throws SQLException {
        try (MockedStatic<DBConnection> mockedDb = mockStatic(DBConnection.class)) {
            mockedDb.when(DBConnection::getConnection).thenReturn(connection);
            when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
            when(preparedStatement.executeUpdate()).thenThrow(new SQLException("Erreur SQL simulée"));

            assertThrows(AccesDonneesException.class,
                    () -> venteDAO.annulerVente(5));
        }
    }

    // ---------- ventesParMedicament (couvre chercherVentes + remplirVentes) ----------

    @Test
    void testVentesParMedicament_AvecResultats() throws SQLException {
        try (MockedStatic<DBConnection> mockedDb = mockStatic(DBConnection.class)) {
            mockedDb.when(DBConnection::getConnection).thenReturn(connection);
            when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
            when(preparedStatement.executeQuery()).thenReturn(resultSet);

            when(resultSet.next()).thenReturn(true, false);
            when(resultSet.getInt("id_vente")).thenReturn(100);
            when(resultSet.getInt("id_pharmacien")).thenReturn(1);
            when(resultSet.getInt("id_client")).thenReturn(2);
            when(resultSet.getInt("id_medicament")).thenReturn(3);
            when(resultSet.getInt("quantite")).thenReturn(10);
            when(resultSet.getTimestamp("date_vente"))
                    .thenReturn(Timestamp.valueOf("2026-07-14 10:30:00"));

            List<VenteResponse> ventes = venteDAO.ventesParMedicament(3);

            assertEquals(1, ventes.size());
            assertEquals(100, ventes.get(0).id());
            assertEquals(3, ventes.get(0).idMedicament());
            assertEquals(10, ventes.get(0).quantite());

            verify(preparedStatement).setInt(1, 3);
            verify(resultSet, times(2)).next();
        }
    }

    @Test
    void testVentesParMedicament_AucunResultat() throws SQLException {
        try (MockedStatic<DBConnection> mockedDb = mockStatic(DBConnection.class)) {
            mockedDb.when(DBConnection::getConnection).thenReturn(connection);
            when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
            when(preparedStatement.executeQuery()).thenReturn(resultSet);
            when(resultSet.next()).thenReturn(false);

            List<VenteResponse> ventes = venteDAO.ventesParMedicament(99);

            assertTrue(ventes.isEmpty());
            verify(resultSet).next();
        }
    }

    @Test
    void testVentesParMedicament_SQLException_DoitLeverAccesDonneesException() throws SQLException {
        try (MockedStatic<DBConnection> mockedDb = mockStatic(DBConnection.class)) {
            mockedDb.when(DBConnection::getConnection).thenReturn(connection);
            when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
            when(preparedStatement.executeQuery()).thenThrow(new SQLException("Erreur SQL simulée"));

            assertThrows(AccesDonneesException.class,
                    () -> venteDAO.ventesParMedicament(3));
        }
    }

    // ---------- ventesParClient ----------

    @Test
    void testVentesParClient_AvecResultats() throws SQLException {
        try (MockedStatic<DBConnection> mockedDb = mockStatic(DBConnection.class)) {
            mockedDb.when(DBConnection::getConnection).thenReturn(connection);
            when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
            when(preparedStatement.executeQuery()).thenReturn(resultSet);

            when(resultSet.next()).thenReturn(true, false);
            when(resultSet.getInt("id_vente")).thenReturn(101);
            when(resultSet.getInt("id_pharmacien")).thenReturn(1);
            when(resultSet.getInt("id_client")).thenReturn(4);
            when(resultSet.getInt("id_medicament")).thenReturn(3);
            when(resultSet.getInt("quantite")).thenReturn(5);
            when(resultSet.getTimestamp("date_vente"))
                    .thenReturn(Timestamp.valueOf("2026-07-14 14:00:00"));

            List<VenteResponse> ventes = venteDAO.ventesParClient(4);

            assertEquals(1, ventes.size());
            assertEquals(4, ventes.get(0).idClient());

            verify(preparedStatement).setInt(1, 4);
        }
    }

    @Test
    void testVentesParClient_ErreurConnexion_DoitLeverAccesDonneesException() throws SQLException {
        try (MockedStatic<DBConnection> mockedDb = mockStatic(DBConnection.class)) {
            mockedDb.when(DBConnection::getConnection)
                    .thenThrow(new SQLException("Connexion impossible"));

            assertThrows(AccesDonneesException.class,
                    () -> venteDAO.ventesParClient(4));
        }
    }

    // ---------- ventesParPeriode ----------

    @Test
    void testVentesParPeriode_AvecResultats() throws SQLException {
        try (MockedStatic<DBConnection> mockedDb = mockStatic(DBConnection.class)) {
            mockedDb.when(DBConnection::getConnection).thenReturn(connection);
            when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
            when(preparedStatement.executeQuery()).thenReturn(resultSet);

            when(resultSet.next()).thenReturn(true, false);
            when(resultSet.getInt("id_vente")).thenReturn(200);
            when(resultSet.getInt("id_pharmacien")).thenReturn(1);
            when(resultSet.getInt("id_client")).thenReturn(2);
            when(resultSet.getInt("id_medicament")).thenReturn(3);
            when(resultSet.getInt("quantite")).thenReturn(8);
            when(resultSet.getTimestamp("date_vente"))
                    .thenReturn(Timestamp.valueOf("2026-07-01 09:00:00"));

            List<VenteResponse> ventes =
                    venteDAO.ventesParPeriode("2026-07-01", "2026-07-14");

            assertEquals(1, ventes.size());
            assertEquals(200, ventes.get(0).id());

            verify(preparedStatement).setString(1, "2026-07-01");
            verify(preparedStatement).setString(2, "2026-07-14 23:59:59");
        }
    }

    @Test
    void testVentesParPeriode_AucunResultat() throws SQLException {
        try (MockedStatic<DBConnection> mockedDb = mockStatic(DBConnection.class)) {
            mockedDb.when(DBConnection::getConnection).thenReturn(connection);
            when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
            when(preparedStatement.executeQuery()).thenReturn(resultSet);
            when(resultSet.next()).thenReturn(false);

            List<VenteResponse> ventes =
                    venteDAO.ventesParPeriode("2026-07-01", "2026-07-14");

            assertTrue(ventes.isEmpty());
        }
    }

    @Test
    void testVentesParPeriode_SQLException_DoitLeverAccesDonneesException() throws SQLException {
        try (MockedStatic<DBConnection> mockedDb = mockStatic(DBConnection.class)) {
            mockedDb.when(DBConnection::getConnection).thenReturn(connection);
            when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
            when(preparedStatement.executeQuery()).thenThrow(new SQLException("Erreur SQL simulée"));

            assertThrows(AccesDonneesException.class,
                    () -> venteDAO.ventesParPeriode("2026-07-01", "2026-07-14"));
        }
    }

    @Test
    void testVentesParPeriode_FormatDateInvalide_DoitLeverAccesDonneesException() throws SQLException {
        try (MockedStatic<DBConnection> mockedDb = mockStatic(DBConnection.class)) {
            mockedDb.when(DBConnection::getConnection).thenReturn(connection);
            when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
            when(preparedStatement.executeQuery())
                    .thenThrow(new SQLException("Format de date invalide"));

            assertThrows(AccesDonneesException.class,
                    () -> venteDAO.ventesParPeriode("date-invalide", "n-importe-quoi"));
        }
    }
}