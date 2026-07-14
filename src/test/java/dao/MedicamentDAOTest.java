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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MedicamentDAOTest {

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private ResultSet resultSet;

    private MedicamentDAO medicamentDAO;

    @BeforeEach
    void setUp() {
        medicamentDAO = new MedicamentDAO();
    }

    // ---------- ajouterMedicament ----------

    @Test
    void ajouterMedicament_casNominal_doitExecuterLInsertion() throws SQLException {
        try (MockedStatic<DBConnection> mockedDb = mockStatic(DBConnection.class)) {
            mockedDb.when(DBConnection::getConnection).thenReturn(connection);
            when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);

            medicamentDAO.ajouterMedicament("Doliprane", "500mg", 100, 3.5, 10);

            verify(preparedStatement).setString(1, "Doliprane");
            verify(preparedStatement).setString(2, "500mg");
            verify(preparedStatement).setInt(3, 100);
            verify(preparedStatement).setDouble(4, 3.5);
            verify(preparedStatement).setInt(5, 10);
            verify(preparedStatement, times(1)).executeUpdate();
        }
    }

    @Test
    void ajouterMedicament_erreurSQL_neDoitPasPropagerException() throws SQLException {
        try (MockedStatic<DBConnection> mockedDb = mockStatic(DBConnection.class)) {
            mockedDb.when(DBConnection::getConnection)
                    .thenThrow(new SQLException("Connexion impossible"));

            // Ne doit pas lever d'exception grâce au catch interne
            medicamentDAO.ajouterMedicament("Doliprane", "500mg", 100, 3.5, 10);
        }
    }

    // ---------- getStock ----------

    @Test
    void getStock_medicamentExistant_doitRetournerLeStock() throws SQLException {
        try (MockedStatic<DBConnection> mockedDb = mockStatic(DBConnection.class)) {
            mockedDb.when(DBConnection::getConnection).thenReturn(connection);
            when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
            when(preparedStatement.executeQuery()).thenReturn(resultSet);
            when(resultSet.next()).thenReturn(true);
            when(resultSet.getInt("stock")).thenReturn(50);

            int stock = medicamentDAO.getStock(1);

            assertEquals(50, stock);
        }
    }

    @Test
    void getStock_medicamentIntrouvable_doitRetournerMoinsUn() throws SQLException {
        try (MockedStatic<DBConnection> mockedDb = mockStatic(DBConnection.class)) {
            mockedDb.when(DBConnection::getConnection).thenReturn(connection);
            when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
            when(preparedStatement.executeQuery()).thenReturn(resultSet);
            when(resultSet.next()).thenReturn(false);

            int stock = medicamentDAO.getStock(999);

            assertEquals(-1, stock);
        }
    }

    @Test
    void getStock_erreurSQL_doitRetournerMoinsUn() throws SQLException {
        try (MockedStatic<DBConnection> mockedDb = mockStatic(DBConnection.class)) {
            mockedDb.when(DBConnection::getConnection)
                    .thenThrow(new SQLException("Erreur connexion"));

            int stock = medicamentDAO.getStock(1);

            assertEquals(-1, stock);
        }
    }

    // ---------- updateStock ----------

    @Test
    void updateStock_casNominal_doitExecuterLaMiseAJour() throws SQLException {
        try (MockedStatic<DBConnection> mockedDb = mockStatic(DBConnection.class)) {
            mockedDb.when(DBConnection::getConnection).thenReturn(connection);
            when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);

            medicamentDAO.updateStock(5, 80);

            verify(preparedStatement).setInt(1, 80);
            verify(preparedStatement).setInt(2, 5);
            verify(preparedStatement, times(1)).executeUpdate();
        }
    }

    @Test
    void updateStock_erreurSQL_neDoitPasPropagerException() throws SQLException {
        try (MockedStatic<DBConnection> mockedDb = mockStatic(DBConnection.class)) {
            mockedDb.when(DBConnection::getConnection)
                    .thenThrow(new SQLException("Erreur connexion"));

            medicamentDAO.updateStock(5, 80);
        }
    }

    // ---------- stockCritique ----------

    @Test
    void stockCritique_medicamentEnStockCritique_doitRetournerMessage() throws SQLException {
        try (MockedStatic<DBConnection> mockedDb = mockStatic(DBConnection.class)) {
            mockedDb.when(DBConnection::getConnection).thenReturn(connection);
            when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
            when(preparedStatement.executeQuery()).thenReturn(resultSet);
            when(resultSet.next()).thenReturn(true);
            when(resultSet.getString("nom")).thenReturn("Doliprane");
            when(resultSet.getInt("id_medicament")).thenReturn(7);
            when(resultSet.getInt("stock")).thenReturn(2);

            String message = medicamentDAO.stockCritique(7);

            assertEquals(
                    "Médicament en stock critique : Doliprane (ID 7) | Stock actuel = 2",
                    message
            );
        }
    }

    @Test
    void stockCritique_medicamentNonCritique_doitRetournerNull() throws SQLException {
        try (MockedStatic<DBConnection> mockedDb = mockStatic(DBConnection.class)) {
            mockedDb.when(DBConnection::getConnection).thenReturn(connection);
            when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
            when(preparedStatement.executeQuery()).thenReturn(resultSet);
            when(resultSet.next()).thenReturn(false);

            String message = medicamentDAO.stockCritique(8);

            assertNull(message);
        }
    }

    @Test
    void stockCritique_erreurSQL_doitRetournerNull() throws SQLException {
        try (MockedStatic<DBConnection> mockedDb = mockStatic(DBConnection.class)) {
            mockedDb.when(DBConnection::getConnection)
                    .thenThrow(new SQLException("Erreur connexion"));

            String message = medicamentDAO.stockCritique(1);

            assertNull(message);
        }
    }

    // ---------- getIdMedicamentParNomEtDosage ----------

    @Test
    void getIdMedicamentParNomEtDosage_trouve_doitRetournerLId() throws SQLException {
        try (MockedStatic<DBConnection> mockedDb = mockStatic(DBConnection.class)) {
            mockedDb.when(DBConnection::getConnection).thenReturn(connection);
            when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
            when(preparedStatement.executeQuery()).thenReturn(resultSet);
            when(resultSet.next()).thenReturn(true);
            when(resultSet.getInt("id_medicament")).thenReturn(12);

            int id = medicamentDAO.getIdMedicamentParNomEtDosage("Doliprane", "500mg");

            assertEquals(12, id);
        }
    }

    @Test
    void getIdMedicamentParNomEtDosage_nonTrouve_doitRetournerMoinsUn() throws SQLException {
        try (MockedStatic<DBConnection> mockedDb = mockStatic(DBConnection.class)) {
            mockedDb.when(DBConnection::getConnection).thenReturn(connection);
            when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
            when(preparedStatement.executeQuery()).thenReturn(resultSet);
            when(resultSet.next()).thenReturn(false);

            int id = medicamentDAO.getIdMedicamentParNomEtDosage("Inconnu", "0mg");

            assertEquals(-1, id);
        }
    }

    @Test
    void getIdMedicamentParNomEtDosage_erreurSQL_doitRetournerMoinsUn() throws SQLException {
        try (MockedStatic<DBConnection> mockedDb = mockStatic(DBConnection.class)) {
            mockedDb.when(DBConnection::getConnection)
                    .thenThrow(new SQLException("Erreur connexion"));

            int id = medicamentDAO.getIdMedicamentParNomEtDosage("Doliprane", "500mg");

            assertEquals(-1, id);
        }
    }
}
