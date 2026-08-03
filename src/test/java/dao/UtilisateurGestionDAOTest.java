package dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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

import dto.UtilisateurGestionResponse;
import exception.AccesDonneesException;
import utils.DBConnection;

class UtilisateurGestionDAOTest {

    private UtilisateurGestionDAO dao;

    @BeforeEach
    void setUp() {
        dao = new UtilisateurGestionDAO();
    }

    @Test
    void doitListerTousLesUtilisateurs() throws Exception {

        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        ResultSet result = mock(ResultSet.class);

        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(statement.executeQuery()).thenReturn(result);
        when(result.next()).thenReturn(true, false);
        when(result.getInt("id")).thenReturn(1);
        when(result.getString("nom")).thenReturn("Ben Salah");
        when(result.getString("prenom")).thenReturn("Ali");
        when(result.getString("login")).thenReturn("pharma");
        when(result.getString("role")).thenReturn("PHARMACIEN");

        try (MockedStatic<DBConnection> db = mockStatic(DBConnection.class)) {

            db.when(DBConnection::getConnection).thenReturn(connection);

            List<UtilisateurGestionResponse> resultat = dao.listerTous();

            assertEquals(1, resultat.size());
            assertEquals("pharma", resultat.get(0).login());
        }
    }

    @Test
    void doitLeverExceptionSiListeEchoue() throws Exception {

        Connection connection = mock(Connection.class);

        when(connection.prepareStatement(anyString()))
                .thenThrow(new SQLException("Erreur SQL"));

        try (MockedStatic<DBConnection> db = mockStatic(DBConnection.class)) {

            db.when(DBConnection::getConnection).thenReturn(connection);

            assertThrows(
                    AccesDonneesException.class,
                    () -> dao.listerTous()
            );
        }
    }

    @Test
    void doitDetecterLoginExistant() throws Exception {

        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        ResultSet result = mock(ResultSet.class);

        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(statement.executeQuery()).thenReturn(result);
        when(result.next()).thenReturn(true);

        try (MockedStatic<DBConnection> db = mockStatic(DBConnection.class)) {

            db.when(DBConnection::getConnection).thenReturn(connection);

            assertTrue(dao.loginExiste("pharma"));
        }
    }

    @Test
    void doitDetecterLoginInexistant() throws Exception {

        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        ResultSet result = mock(ResultSet.class);

        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(statement.executeQuery()).thenReturn(result);
        when(result.next()).thenReturn(false);

        try (MockedStatic<DBConnection> db = mockStatic(DBConnection.class)) {

            db.when(DBConnection::getConnection).thenReturn(connection);

            assertFalse(dao.loginExiste("inconnu"));
        }
    }

    @Test
    void doitCreerUnPharmacienEtRetournerId() throws Exception {

        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        ResultSet cles = mock(ResultSet.class);

        when(
                connection.prepareStatement(
                        anyString(), eq(Statement.RETURN_GENERATED_KEYS)
                )
        ).thenReturn(statement);

        when(statement.getGeneratedKeys()).thenReturn(cles);
        when(cles.next()).thenReturn(true);
        when(cles.getInt(1)).thenReturn(4);

        try (MockedStatic<DBConnection> db = mockStatic(DBConnection.class)) {

            db.when(DBConnection::getConnection).thenReturn(connection);

            int resultat = dao.creerUtilisateur(
                    "Nom", "Prenom", "login1", "hash", "PHARMACIEN"
            );

            assertEquals(4, resultat);

            verify(statement).setString(1, "Nom");
            verify(statement).setString(2, "Prenom");
            verify(statement).setString(3, "login1");
            verify(statement).setString(4, "hash");
        }
    }

    @Test
    void doitModifierUtilisateurAvecNouveauMotDePasse() throws Exception {

        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);

        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(statement.executeUpdate()).thenReturn(1);

        try (MockedStatic<DBConnection> db = mockStatic(DBConnection.class)) {

            db.when(DBConnection::getConnection).thenReturn(connection);

            boolean resultat = dao.modifierUtilisateur(
                    1, "GESTIONNAIRE", "Nom", "Prenom", "login1", "hash"
            );

            assertTrue(resultat);

            verify(statement).setInt(5, 1);
        }
    }

    @Test
    void doitModifierUtilisateurSansChangerMotDePasse() throws Exception {

        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);

        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(statement.executeUpdate()).thenReturn(1);

        try (MockedStatic<DBConnection> db = mockStatic(DBConnection.class)) {

            db.when(DBConnection::getConnection).thenReturn(connection);

            boolean resultat = dao.modifierUtilisateur(
                    1, "PHARMACIEN", "Nom", "Prenom", "login1", null
            );

            assertTrue(resultat);

            /* 4 paramètres seulement : nom, prenom, login, id (pas de pwd) */
            verify(statement).setInt(4, 1);
        }
    }

    @Test
    void doitSupprimerUtilisateur() throws Exception {

        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);

        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(statement.executeUpdate()).thenReturn(1);

        try (MockedStatic<DBConnection> db = mockStatic(DBConnection.class)) {

            db.when(DBConnection::getConnection).thenReturn(connection);

            assertTrue(dao.supprimerUtilisateur(1, "PHARMACIEN"));

            verify(statement).setInt(1, 1);
        }
    }

    @Test
    void doitLeverExceptionSiSuppressionEchoue() throws Exception {

        Connection connection = mock(Connection.class);

        when(connection.prepareStatement(anyString()))
                .thenThrow(new SQLException("Erreur SQL"));

        try (MockedStatic<DBConnection> db = mockStatic(DBConnection.class)) {

            db.when(DBConnection::getConnection).thenReturn(connection);

            assertThrows(
                    AccesDonneesException.class,
                    () -> dao.supprimerUtilisateur(1, "PHARMACIEN")
            );
        }
    }

    @Test
    void doitChangerLeMotDePasseParLogin() throws Exception {

        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);

        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(statement.executeUpdate()).thenReturn(1);

        try (MockedStatic<DBConnection> db = mockStatic(DBConnection.class)) {

            db.when(DBConnection::getConnection).thenReturn(connection);

            boolean resultat = dao.changerMotDePasseParLogin(
                    "pharma", "PHARMACIEN", "hash"
            );

            assertTrue(resultat);

            verify(statement).setString(1, "hash");
            verify(statement).setString(2, "pharma");
        }
    }

    @Test
    void doitLeverExceptionSiChangementMotDePasseEchoue() throws Exception {

        Connection connection = mock(Connection.class);

        when(connection.prepareStatement(anyString()))
                .thenThrow(new SQLException("Erreur SQL"));

        try (MockedStatic<DBConnection> db = mockStatic(DBConnection.class)) {

            db.when(DBConnection::getConnection).thenReturn(connection);

            assertThrows(
                    AccesDonneesException.class,
                    () -> dao.changerMotDePasseParLogin(
                            "pharma", "PHARMACIEN", "hash"
                    )
            );
        }
    }

    @Test
    void doitRetournerMonProfil() throws Exception {

        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        ResultSet result = mock(ResultSet.class);

        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(statement.executeQuery()).thenReturn(result);
        when(result.next()).thenReturn(true);
        when(result.getString("nom")).thenReturn("Ben Salah");
        when(result.getString("prenom")).thenReturn("Ali");
        when(result.getString("login")).thenReturn("pharma");
        when(result.getString("email")).thenReturn("ali@pharmacie.tn");

        try (MockedStatic<DBConnection> db = mockStatic(DBConnection.class)) {

            db.when(DBConnection::getConnection).thenReturn(connection);

            dto.MonProfilResponse profil =
                    dao.getMonProfil("pharma", "PHARMACIEN");

            assertEquals("Ben Salah", profil.nom());
            assertEquals("ali@pharmacie.tn", profil.email());
        }
    }

    @Test
    void doitRetournerNullSiProfilIntrouvable() throws Exception {

        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        ResultSet result = mock(ResultSet.class);

        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(statement.executeQuery()).thenReturn(result);
        when(result.next()).thenReturn(false);

        try (MockedStatic<DBConnection> db = mockStatic(DBConnection.class)) {

            db.when(DBConnection::getConnection).thenReturn(connection);

            assertEquals(
                    null,
                    dao.getMonProfil("inconnu", "PHARMACIEN")
            );
        }
    }

    @Test
    void doitLeverExceptionSiRecuperationProfilEchoue() throws Exception {

        Connection connection = mock(Connection.class);

        when(connection.prepareStatement(anyString()))
                .thenThrow(new SQLException("Erreur SQL"));

        try (MockedStatic<DBConnection> db = mockStatic(DBConnection.class)) {

            db.when(DBConnection::getConnection).thenReturn(connection);

            assertThrows(
                    AccesDonneesException.class,
                    () -> dao.getMonProfil("pharma", "PHARMACIEN")
            );
        }
    }

    @Test
    void doitModifierMesInfosParLogin() throws Exception {

        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);

        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(statement.executeUpdate()).thenReturn(1);

        try (MockedStatic<DBConnection> db = mockStatic(DBConnection.class)) {

            db.when(DBConnection::getConnection).thenReturn(connection);

            boolean resultat = dao.modifierInfosParLogin(
                    "pharma", "PHARMACIEN",
                    "Ben Salah", "Ali", "ali@pharmacie.tn"
            );

            assertTrue(resultat);

            verify(statement).setString(1, "Ben Salah");
            verify(statement).setString(2, "Ali");
            verify(statement).setString(3, "ali@pharmacie.tn");
            verify(statement).setString(4, "pharma");
        }
    }

    @Test
    void doitLeverExceptionSiModificationInfosEchoue() throws Exception {

        Connection connection = mock(Connection.class);

        when(connection.prepareStatement(anyString()))
                .thenThrow(new SQLException("Erreur SQL"));

        try (MockedStatic<DBConnection> db = mockStatic(DBConnection.class)) {

            db.when(DBConnection::getConnection).thenReturn(connection);

            assertThrows(
                    AccesDonneesException.class,
                    () -> dao.modifierInfosParLogin(
                            "pharma", "PHARMACIEN",
                            "Ben Salah", "Ali", "ali@pharmacie.tn"
                    )
            );
        }
    }
}