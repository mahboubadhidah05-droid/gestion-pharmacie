package dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import utils.DBConnection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UtilisateurDAOTest {

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement psPharmacien;

    @Mock
    private PreparedStatement psGestionnaire;

    @Mock
    private ResultSet rsPharmacien;

    @Mock
    private ResultSet rsGestionnaire;

    private UtilisateurDAO utilisateurDAO;

    @BeforeEach
    void setUp() {
        utilisateurDAO = new UtilisateurDAO();
    }

    @Test
    void getRole_pharmacien_doitRetournerPharmacien() throws SQLException {
        try (MockedStatic<DBConnection> mockedDb = mockStatic(DBConnection.class)) {
            mockedDb.when(DBConnection::getConnection).thenReturn(connection);

            when(connection.prepareStatement(contains("pharmacien"))).thenReturn(psPharmacien);
            when(psPharmacien.executeQuery()).thenReturn(rsPharmacien);
            when(rsPharmacien.next()).thenReturn(true);

            String role = utilisateurDAO.getRole("phar1", "motdepasse");

            assertEquals("PHARMACIEN", role);
        }
    }

    @Test
    void getRole_gestionnaire_doitRetournerGestionnaire() throws SQLException {
        try (MockedStatic<DBConnection> mockedDb = mockStatic(DBConnection.class)) {
            mockedDb.when(DBConnection::getConnection).thenReturn(connection);

            when(connection.prepareStatement(contains("pharmacien"))).thenReturn(psPharmacien);
            when(psPharmacien.executeQuery()).thenReturn(rsPharmacien);
            when(rsPharmacien.next()).thenReturn(false);

            when(connection.prepareStatement(contains("gestionnaire"))).thenReturn(psGestionnaire);
            when(psGestionnaire.executeQuery()).thenReturn(rsGestionnaire);
            when(rsGestionnaire.next()).thenReturn(true);

            String role = utilisateurDAO.getRole("gest1", "motdepasse");

            assertEquals("GESTIONNAIRE", role);
        }
    }

    @Test
    void getRole_identifiantsInvalides_doitRetournerEchec() throws SQLException {
        try (MockedStatic<DBConnection> mockedDb = mockStatic(DBConnection.class)) {
            mockedDb.when(DBConnection::getConnection).thenReturn(connection);

            when(connection.prepareStatement(contains("pharmacien"))).thenReturn(psPharmacien);
            when(psPharmacien.executeQuery()).thenReturn(rsPharmacien);
            when(rsPharmacien.next()).thenReturn(false);

            when(connection.prepareStatement(contains("gestionnaire"))).thenReturn(psGestionnaire);
            when(psGestionnaire.executeQuery()).thenReturn(rsGestionnaire);
            when(rsGestionnaire.next()).thenReturn(false);

            String role = utilisateurDAO.getRole("inconnu", "faux");

            assertEquals("ECHEC", role);
        }
    }

    @Test
    void getRole_erreurSQL_doitRetournerEchec() throws SQLException {
        try (MockedStatic<DBConnection> mockedDb = mockStatic(DBConnection.class)) {
            mockedDb.when(DBConnection::getConnection)
                    .thenThrow(new SQLException("Erreur connexion"));

            String role = utilisateurDAO.getRole("phar1", "motdepasse");

            assertEquals("ECHEC", role);
        }
    }

    // Petit matcher utilitaire pour cibler la bonne requête SQL selon la table visée
    private static String contains(String texte) {
        return org.mockito.ArgumentMatchers.matches(".*" + texte + ".*");
    }
}
