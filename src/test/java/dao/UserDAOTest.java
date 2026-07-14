package dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import utils.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

/**
 * Tests unitaires pour {@link UserDAO}.
 *
 * getProfil() exécute deux requêtes distinctes (Pharmacien puis Gestionnaire)
 * sur la MÊME connexion mockée. Pour distinguer les deux PreparedStatement
 * retournés par connection.prepareStatement(sql), on utilise thenAnswer() en
 * inspectant le texte SQL passé en paramètre plutôt qu'un matcher contains(...),
 * ce qui évite tout risque d'InvalidUseOfMatchersException.
 */
@ExtendWith(MockitoExtension.class)
class UserDAOTest {

    @Mock
    private Connection connection;

    private UserDAO userDAO;

    @BeforeEach
    void setUp() {
        userDAO = new UserDAO();
    }

    @Test
    void testGetProfil_TrouvePharmacien() throws SQLException {
        PreparedStatement psPharmacien = mock(PreparedStatement.class);
        ResultSet rsPharmacien = mock(ResultSet.class);

        try (MockedStatic<DBConnection> mockedDb = mockStatic(DBConnection.class)) {
            mockedDb.when(DBConnection::getConnection).thenReturn(connection);

            when(connection.prepareStatement(anyString())).thenAnswer(invocation -> {
                String sql = invocation.getArgument(0);
                return sql.contains("Pharmacien") ? psPharmacien : mock(PreparedStatement.class);
            });

            when(psPharmacien.executeQuery()).thenReturn(rsPharmacien);
            when(rsPharmacien.next()).thenReturn(true);
            when(rsPharmacien.getString("nom")).thenReturn("Gharbi");
            when(rsPharmacien.getString("prenom")).thenReturn("Nour");

            String[] resultat = userDAO.getProfil("nour.gharbi");

            assertArrayEquals(new String[]{"Gharbi", "Nour"}, resultat);
        }
    }

    @Test
    void testGetProfil_TrouveGestionnaire() throws SQLException {
        PreparedStatement psPharmacien = mock(PreparedStatement.class);
        ResultSet rsPharmacien = mock(ResultSet.class);
        PreparedStatement psGestionnaire = mock(PreparedStatement.class);
        ResultSet rsGestionnaire = mock(ResultSet.class);

        try (MockedStatic<DBConnection> mockedDb = mockStatic(DBConnection.class)) {
            mockedDb.when(DBConnection::getConnection).thenReturn(connection);

            when(connection.prepareStatement(anyString())).thenAnswer(invocation -> {
                String sql = invocation.getArgument(0);
                return sql.contains("Pharmacien") ? psPharmacien : psGestionnaire;
            });

            when(psPharmacien.executeQuery()).thenReturn(rsPharmacien);
            when(rsPharmacien.next()).thenReturn(false);

            when(psGestionnaire.executeQuery()).thenReturn(rsGestionnaire);
            when(rsGestionnaire.next()).thenReturn(true);
            when(rsGestionnaire.getString("nom")).thenReturn("Khemiri");
            when(rsGestionnaire.getString("prenom")).thenReturn("Wassim");

            String[] resultat = userDAO.getProfil("wassim.khemiri");

            assertArrayEquals(new String[]{"Khemiri", "Wassim"}, resultat);
        }
    }

    @Test
    void testGetProfil_AucunUtilisateurTrouve() throws SQLException {
        PreparedStatement psPharmacien = mock(PreparedStatement.class);
        ResultSet rsPharmacien = mock(ResultSet.class);
        PreparedStatement psGestionnaire = mock(PreparedStatement.class);
        ResultSet rsGestionnaire = mock(ResultSet.class);

        try (MockedStatic<DBConnection> mockedDb = mockStatic(DBConnection.class)) {
            mockedDb.when(DBConnection::getConnection).thenReturn(connection);

            when(connection.prepareStatement(anyString())).thenAnswer(invocation -> {
                String sql = invocation.getArgument(0);
                return sql.contains("Pharmacien") ? psPharmacien : psGestionnaire;
            });

            when(psPharmacien.executeQuery()).thenReturn(rsPharmacien);
            when(rsPharmacien.next()).thenReturn(false);

            when(psGestionnaire.executeQuery()).thenReturn(rsGestionnaire);
            when(rsGestionnaire.next()).thenReturn(false);

            String[] resultat = userDAO.getProfil("inconnu");

            assertEquals(0, resultat.length);
        }
    }

    @Test
    void testGetProfil_SQLException_RetourneTableauVide() throws SQLException {
        try (MockedStatic<DBConnection> mockedDb = mockStatic(DBConnection.class)) {
            mockedDb.when(DBConnection::getConnection).thenReturn(connection);
            when(connection.prepareStatement(anyString()))
                    .thenThrow(new SQLException("Erreur SQL simulée"));

            String[] resultat = userDAO.getProfil("nour.gharbi");

            assertEquals(0, resultat.length);
        }
    }

    @Test
    void testGetProfil_ErreurConnexion_RetourneTableauVide() throws SQLException {
        try (MockedStatic<DBConnection> mockedDb = mockStatic(DBConnection.class)) {
            mockedDb.when(DBConnection::getConnection)
                    .thenThrow(new SQLException("Connexion impossible"));

            String[] resultat = userDAO.getProfil("nour.gharbi");

            assertEquals(0, resultat.length);
        }
    }
}