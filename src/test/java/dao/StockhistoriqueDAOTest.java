package dao;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import utils.DBConnection;

@ExtendWith(MockitoExtension.class)
class StockHistoriqueDAOTest {

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private Statement statement;

    @Mock
    private ResultSet resultSet;

    private StockHistoriqueDAO stockHistoriqueDAO;

    @BeforeEach
    void setUp() {
        stockHistoriqueDAO = new StockHistoriqueDAO();
    }

    /**
     * Évite la duplication SonarQube.
     */
    private MockedStatic<DBConnection> mockDatabase()
            throws SQLException {

        MockedStatic<DBConnection> mockedDb =
                mockStatic(DBConnection.class);

        mockedDb.when(DBConnection::getConnection)
                .thenReturn(connection);

        return mockedDb;
    }

    // ---------- ajouterHistorique ----------

    @Test
    void testAjouterHistorique_Nominal()
            throws SQLException {

        try (MockedStatic<DBConnection> mockedDb =
                     mockDatabase()) {

            when(connection.prepareStatement(anyString()))
                    .thenReturn(preparedStatement);

            stockHistoriqueDAO.ajouterHistorique(7, 50);

            verify(preparedStatement).setInt(1, 7);
            verify(preparedStatement).setInt(2, 50);
            verify(preparedStatement).executeUpdate();
        }
    }

    @Test
    void testAjouterHistorique_SQLException_NePasPropager()
            throws SQLException {

        try (MockedStatic<DBConnection> mockedDb =
                     mockDatabase()) {

            when(connection.prepareStatement(anyString()))
                    .thenReturn(preparedStatement);

            when(preparedStatement.executeUpdate())
                    .thenThrow(new SQLException("Erreur SQL simulée"));

            assertDoesNotThrow(() ->
                    stockHistoriqueDAO.ajouterHistorique(7, 50));

            verify(preparedStatement).executeUpdate();
        }
    }

    @Test
    void testAjouterHistorique_ErreurConnexion_NePasPropager()
            throws SQLException {

        try (MockedStatic<DBConnection> mockedDb =
                     mockStatic(DBConnection.class)) {

            mockedDb.when(DBConnection::getConnection)
                    .thenThrow(new SQLException("Connexion impossible"));

            assertDoesNotThrow(() ->
                    stockHistoriqueDAO.ajouterHistorique(7, 50));
        }
    }
    // ---------- afficherHistorique ----------

    @Test
    void testAfficherHistorique_AvecResultats()
            throws SQLException {

        try (MockedStatic<DBConnection> mockedDb =
                     mockDatabase()) {

            when(connection.createStatement())
                    .thenReturn(statement);

            when(statement.executeQuery(anyString()))
                    .thenReturn(resultSet);

            when(resultSet.next())
                    .thenReturn(true, true, false);

            when(resultSet.getInt("id_medicament"))
                    .thenReturn(1, 2);

            when(resultSet.getInt("quantite"))
                    .thenReturn(30, 40);

            Timestamp timestamp =
                    new Timestamp(System.currentTimeMillis());

            when(resultSet.getTimestamp("date_modification"))
                    .thenReturn(timestamp, timestamp);

            assertDoesNotThrow(() ->
                    stockHistoriqueDAO.afficherHistorique());

            verify(resultSet,
                    org.mockito.Mockito.times(3))
                    .next();
        }
    }

    @Test
    void testAfficherHistorique_AucunResultat()
            throws SQLException {

        try (MockedStatic<DBConnection> mockedDb =
                     mockDatabase()) {

            when(connection.createStatement())
                    .thenReturn(statement);

            when(statement.executeQuery(anyString()))
                    .thenReturn(resultSet);

            when(resultSet.next())
                    .thenReturn(false);

            assertDoesNotThrow(() ->
                    stockHistoriqueDAO.afficherHistorique());

            verify(resultSet).next();
        }
    }

    @Test
    void testAfficherHistorique_SQLException_NePasPropager()
            throws SQLException {

        try (MockedStatic<DBConnection> mockedDb =
                     mockDatabase()) {

            when(connection.createStatement())
                    .thenReturn(statement);

            when(statement.executeQuery(anyString()))
                    .thenThrow(
                            new SQLException("Erreur SQL simulée"));

            assertDoesNotThrow(() ->
                    stockHistoriqueDAO.afficherHistorique());
        }
    }

    @Test
    void testAfficherHistorique_ErreurConnexion_NePasPropager()
            throws SQLException {

        try (MockedStatic<DBConnection> mockedDb =
                     mockStatic(DBConnection.class)) {

            mockedDb.when(DBConnection::getConnection)
                    .thenThrow(
                            new SQLException("Connexion impossible"));

            assertDoesNotThrow(() ->
                    stockHistoriqueDAO.afficherHistorique());
        }
    }
}