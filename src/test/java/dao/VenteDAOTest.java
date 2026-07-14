package dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import utils.DBConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests unitaires pour {@link VenteDAO}.
 *
 * ventesParMedicament() et ventesParClient() délèguent toutes deux à la méthode
 * privée afficherVentes(sql, id), elle-même basée sur afficherResultat(ps) :
 * on couvre donc le chemin "résultats trouvés" / "aucun résultat" / "erreur SQL"
 * une fois via ventesParMedicament (le comportement de ventesParClient étant
 * strictement identique côté DAO) et on ajoute un test dédié pour ventesParClient
 * afin de couvrir explicitement cette méthode publique.
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
            verify(preparedStatement).setDate(org.mockito.ArgumentMatchers.eq(5), org.mockito.ArgumentMatchers.any(Date.class));
            verify(preparedStatement).executeUpdate();
        }
    }

    @Test
    void testEnregistrerVente_SQLException_NePasPropager() throws SQLException {
        try (MockedStatic<DBConnection> mockedDb = mockStatic(DBConnection.class)) {
            mockedDb.when(DBConnection::getConnection).thenReturn(connection);
            when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
            when(preparedStatement.executeUpdate()).thenThrow(new SQLException("Erreur SQL simulée"));

            assertDoesNotThrow(() -> venteDAO.enregistrerVente(1, 2, 3, 10));
        }
    }

    // ---------- annulerVente ----------

    @Test
    void testAnnulerVente_Nominal() throws SQLException {
        try (MockedStatic<DBConnection> mockedDb = mockStatic(DBConnection.class)) {
            mockedDb.when(DBConnection::getConnection).thenReturn(connection);
            when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
            when(preparedStatement.executeUpdate()).thenReturn(1);

            venteDAO.annulerVente(5);

            verify(preparedStatement).setInt(1, 5);
            verify(preparedStatement).executeUpdate();
        }
    }

    @Test
    void testAnnulerVente_SQLException_NePasPropager() throws SQLException {
        try (MockedStatic<DBConnection> mockedDb = mockStatic(DBConnection.class)) {
            mockedDb.when(DBConnection::getConnection).thenReturn(connection);
            when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
            when(preparedStatement.executeUpdate()).thenThrow(new SQLException("Erreur SQL simulée"));

            assertDoesNotThrow(() -> venteDAO.annulerVente(5));
        }
    }

    // ---------- ventesParMedicament (couvre afficherVentes + afficherResultat) ----------

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
            when(resultSet.getDate("date_vente")).thenReturn(Date.valueOf("2026-07-14"));

            assertDoesNotThrow(() -> venteDAO.ventesParMedicament(3));

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

            assertDoesNotThrow(() -> venteDAO.ventesParMedicament(99));

            verify(resultSet).next();
        }
    }

    @Test
    void testVentesParMedicament_SQLException_NePasPropager() throws SQLException {
        try (MockedStatic<DBConnection> mockedDb = mockStatic(DBConnection.class)) {
            mockedDb.when(DBConnection::getConnection).thenReturn(connection);
            when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
            when(preparedStatement.executeQuery()).thenThrow(new SQLException("Erreur SQL simulée"));

            assertDoesNotThrow(() -> venteDAO.ventesParMedicament(3));
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
            when(resultSet.getDate("date_vente")).thenReturn(Date.valueOf("2026-07-14"));

            assertDoesNotThrow(() -> venteDAO.ventesParClient(4));

            verify(preparedStatement).setInt(1, 4);
        }
    }

    @Test
    void testVentesParClient_ErreurConnexion_NePasPropager() throws SQLException {
        try (MockedStatic<DBConnection> mockedDb = mockStatic(DBConnection.class)) {
            mockedDb.when(DBConnection::getConnection)
                    .thenThrow(new SQLException("Connexion impossible"));

            assertDoesNotThrow(() -> venteDAO.ventesParClient(4));
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
            when(resultSet.getDate("date_vente")).thenReturn(Date.valueOf("2026-07-01"));

            assertDoesNotThrow(() -> venteDAO.ventesParPeriode("2026-07-01", "2026-07-14"));

            verify(preparedStatement).setDate(1, Date.valueOf("2026-07-01"));
            verify(preparedStatement).setDate(2, Date.valueOf("2026-07-14"));
        }
    }

    @Test
    void testVentesParPeriode_AucunResultat() throws SQLException {
        try (MockedStatic<DBConnection> mockedDb = mockStatic(DBConnection.class)) {
            mockedDb.when(DBConnection::getConnection).thenReturn(connection);
            when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
            when(preparedStatement.executeQuery()).thenReturn(resultSet);
            when(resultSet.next()).thenReturn(false);

            assertDoesNotThrow(() -> venteDAO.ventesParPeriode("2026-07-01", "2026-07-14"));
        }
    }

    @Test
    void testVentesParPeriode_SQLException_NePasPropager() throws SQLException {
        try (MockedStatic<DBConnection> mockedDb = mockStatic(DBConnection.class)) {
            mockedDb.when(DBConnection::getConnection).thenReturn(connection);
            when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
            when(preparedStatement.executeQuery()).thenThrow(new SQLException("Erreur SQL simulée"));

            assertDoesNotThrow(() -> venteDAO.ventesParPeriode("2026-07-01", "2026-07-14"));
        }
    }

    /**
     * Point d'attention (non un bug à corriger dans ce lot de tests, mais à
     * documenter) : ventesParPeriode() appelle java.sql.Date.valueOf(dateDebut)
     * AVANT d'entrer dans un contexte protégé par catch(SQLException). Un format
     * de date invalide lève une IllegalArgumentException qui n'est PAS interceptée
     * par le catch(SQLException) existant et se propage donc à l'appelant.
     */
    @Test
    void testVentesParPeriode_FormatDateInvalide_SePropage() {
        try (MockedStatic<DBConnection> mockedDb = mockStatic(DBConnection.class)) {
            mockedDb.when(DBConnection::getConnection).thenReturn(connection);

            org.junit.jupiter.api.Assertions.assertThrows(
                    IllegalArgumentException.class,
                    () -> venteDAO.ventesParPeriode("date-invalide", "2026-07-14"));
        }
    }
}
