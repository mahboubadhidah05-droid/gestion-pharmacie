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
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import dto.MedicamentResponse;
import exception.AccesDonneesException;
import utils.DBConnection;

class MedicamentDAOTest {

    private MedicamentDAO medicamentDAO;

    @BeforeEach
    void setUp() {
        medicamentDAO = new MedicamentDAO();
    }

    @Test
    void doitAjouterMedicament() throws Exception {

        Connection connection =
                mock(Connection.class);

        PreparedStatement statement =
                mock(PreparedStatement.class);

        when(
                connection.prepareStatement(anyString())
        ).thenReturn(statement);

        try (MockedStatic<DBConnection> dbConnection =
                     mockStatic(DBConnection.class)) {

            dbConnection.when(
                    DBConnection::getConnection
            ).thenReturn(connection);

            medicamentDAO.ajouterMedicament(
                    "Paracetamol",
                    "500mg",
                    100,
                    5.5,
                    10
            );

            verify(statement).setString(
                    1,
                    "Paracetamol"
            );

            verify(statement).setString(
                    2,
                    "500mg"
            );

            verify(statement).setInt(
                    3,
                    100
            );

            verify(statement).setDouble(
                    4,
                    5.5
            );

            verify(statement).setInt(
                    5,
                    10
            );

            verify(statement).executeUpdate();
        }
    }

    @Test
    void doitLeverExceptionLorsAjoutMedicament()
            throws Exception {

        Connection connection =
                mock(Connection.class);

        when(
                connection.prepareStatement(anyString())
        ).thenThrow(
                new SQLException("Erreur SQL")
        );

        try (MockedStatic<DBConnection> dbConnection =
                     mockStatic(DBConnection.class)) {

            dbConnection.when(
                    DBConnection::getConnection
            ).thenReturn(connection);

            assertThrows(
                    AccesDonneesException.class,
                    () -> medicamentDAO.ajouterMedicament(
                            "Paracetamol",
                            "500mg",
                            100,
                            5.5,
                            10
                    )
            );
        }
    }

    @Test
    void doitRetournerStock() throws Exception {

        Connection connection =
                mock(Connection.class);

        PreparedStatement statement =
                mock(PreparedStatement.class);

        ResultSet result =
                mock(ResultSet.class);

        when(
                connection.prepareStatement(anyString())
        ).thenReturn(statement);

        when(
                statement.executeQuery()
        ).thenReturn(result);

        when(
                result.next()
        ).thenReturn(true);

        when(
                result.getInt("stock")
        ).thenReturn(100);

        try (MockedStatic<DBConnection> dbConnection =
                     mockStatic(DBConnection.class)) {

            dbConnection.when(
                    DBConnection::getConnection
            ).thenReturn(connection);

            int stock =
                    medicamentDAO.getStock(1);

            assertEquals(
                    100,
                    stock
            );

            verify(statement).setInt(
                    1,
                    1
            );
        }
    }

    @Test
    void doitRetournerMoinsUnSiMedicamentIntrouvable()
            throws Exception {

        Connection connection =
                mock(Connection.class);

        PreparedStatement statement =
                mock(PreparedStatement.class);

        ResultSet result =
                mock(ResultSet.class);

        when(
                connection.prepareStatement(anyString())
        ).thenReturn(statement);

        when(
                statement.executeQuery()
        ).thenReturn(result);

        when(
                result.next()
        ).thenReturn(false);

        try (MockedStatic<DBConnection> dbConnection =
                     mockStatic(DBConnection.class)) {

            dbConnection.when(
                    DBConnection::getConnection
            ).thenReturn(connection);

            int stock =
                    medicamentDAO.getStock(1);

            assertEquals(
                    -1,
                    stock
            );
        }
    }

    @Test
    void doitLeverExceptionLorsRecuperationStock()
            throws Exception {

        Connection connection =
                mock(Connection.class);

        when(
                connection.prepareStatement(anyString())
        ).thenThrow(
                new SQLException("Erreur SQL")
        );

        try (MockedStatic<DBConnection> dbConnection =
                     mockStatic(DBConnection.class)) {

            dbConnection.when(
                    DBConnection::getConnection
            ).thenReturn(connection);

            assertThrows(
                    AccesDonneesException.class,
                    () -> medicamentDAO.getStock(1)
            );
        }
    }

    @Test
    void doitListerMedicaments() throws Exception {

        Connection connection =
                mock(Connection.class);

        PreparedStatement statement =
                mock(PreparedStatement.class);

        ResultSet result =
                mock(ResultSet.class);

        when(
                connection.prepareStatement(anyString())
        ).thenReturn(statement);

        when(
                statement.executeQuery()
        ).thenReturn(result);

        when(
                result.next()
        ).thenReturn(true, false);

        when(
                result.getInt("id_medicament")
        ).thenReturn(1);

        when(
                result.getString("nom")
        ).thenReturn("Paracetamol");

        when(
                result.getString("dosage")
        ).thenReturn("500mg");

        when(
                result.getInt("stock")
        ).thenReturn(100);

        when(
                result.getDouble("prix")
        ).thenReturn(5.5);

        when(
                result.getInt("seuil_critique")
        ).thenReturn(10);

        try (MockedStatic<DBConnection> dbConnection =
                     mockStatic(DBConnection.class)) {

            dbConnection.when(
                    DBConnection::getConnection
            ).thenReturn(connection);

            List<MedicamentResponse> resultat =
                    medicamentDAO.listerMedicaments();

            assertEquals(
                    1,
                    resultat.size()
            );
        }
    }

    @Test
    void doitLeverExceptionLorsListeMedicaments()
            throws Exception {

        Connection connection =
                mock(Connection.class);

        when(
                connection.prepareStatement(anyString())
        ).thenThrow(
                new SQLException("Erreur SQL")
        );

        try (MockedStatic<DBConnection> dbConnection =
                     mockStatic(DBConnection.class)) {

            dbConnection.when(
                    DBConnection::getConnection
            ).thenReturn(connection);

            assertThrows(
                    AccesDonneesException.class,
                    () -> medicamentDAO.listerMedicaments()
            );
        }
    }

    @Test
    void doitMettreAJourStock() throws Exception {

        Connection connection =
                mock(Connection.class);

        PreparedStatement statement =
                mock(PreparedStatement.class);

        when(
                connection.prepareStatement(anyString())
        ).thenReturn(statement);

        try (MockedStatic<DBConnection> dbConnection =
                     mockStatic(DBConnection.class)) {

            dbConnection.when(
                    DBConnection::getConnection
            ).thenReturn(connection);

            medicamentDAO.updateStock(
                    1,
                    50
            );

            verify(statement).setInt(
                    1,
                    50
            );

            verify(statement).setInt(
                    2,
                    1
            );

            verify(statement).executeUpdate();
        }
    }

    @Test
    void doitLeverExceptionLorsMiseAJourStock()
            throws Exception {

        Connection connection =
                mock(Connection.class);

        when(
                connection.prepareStatement(anyString())
        ).thenThrow(
                new SQLException("Erreur SQL")
        );

        try (MockedStatic<DBConnection> dbConnection =
                     mockStatic(DBConnection.class)) {

            dbConnection.when(
                    DBConnection::getConnection
            ).thenReturn(connection);

            assertThrows(
                    AccesDonneesException.class,
                    () -> medicamentDAO.updateStock(
                            1,
                            50
                    )
            );
        }
    }

    @Test
    void doitRetournerMessageSiStockCritique()
            throws Exception {

        Connection connection =
                mock(Connection.class);

        PreparedStatement statement =
                mock(PreparedStatement.class);

        ResultSet result =
                mock(ResultSet.class);

        when(
                connection.prepareStatement(anyString())
        ).thenReturn(statement);

        when(
                statement.executeQuery()
        ).thenReturn(result);

        when(
                result.next()
        ).thenReturn(true);

        when(
                result.getString("nom")
        ).thenReturn("Paracetamol");

        when(
                result.getInt("id_medicament")
        ).thenReturn(1);

        when(
                result.getInt("stock")
        ).thenReturn(5);

        try (MockedStatic<DBConnection> dbConnection =
                     mockStatic(DBConnection.class)) {

            dbConnection.when(
                    DBConnection::getConnection
            ).thenReturn(connection);

            String resultat =
                    medicamentDAO.stockCritique(1);

            assertEquals(
                    "Médicament en stock critique : "
                            + "Paracetamol (ID 1) "
                            + "| Stock actuel = 5",
                    resultat
            );
        }
    }

    @Test
    void doitRetournerNullSiStockNonCritique()
            throws Exception {

        Connection connection =
                mock(Connection.class);

        PreparedStatement statement =
                mock(PreparedStatement.class);

        ResultSet result =
                mock(ResultSet.class);

        when(
                connection.prepareStatement(anyString())
        ).thenReturn(statement);

        when(
                statement.executeQuery()
        ).thenReturn(result);

        when(
                result.next()
        ).thenReturn(false);

        try (MockedStatic<DBConnection> dbConnection =
                     mockStatic(DBConnection.class)) {

            dbConnection.when(
                    DBConnection::getConnection
            ).thenReturn(connection);

            assertNull(
                    medicamentDAO.stockCritique(1)
            );
        }
    }

    @Test
    void doitLeverExceptionLorsVerificationStockCritique()
            throws Exception {

        Connection connection =
                mock(Connection.class);

        when(
                connection.prepareStatement(anyString())
        ).thenThrow(
                new SQLException("Erreur SQL")
        );

        try (MockedStatic<DBConnection> dbConnection =
                     mockStatic(DBConnection.class)) {

            dbConnection.when(
                    DBConnection::getConnection
            ).thenReturn(connection);

            assertThrows(
                    AccesDonneesException.class,
                    () -> medicamentDAO.stockCritique(1)
            );
        }
    }

    @Test
    void doitRetournerIdParNomEtDosage()
            throws Exception {

        Connection connection =
                mock(Connection.class);

        PreparedStatement statement =
                mock(PreparedStatement.class);

        ResultSet result =
                mock(ResultSet.class);

        when(
                connection.prepareStatement(anyString())
        ).thenReturn(statement);

        when(
                statement.executeQuery()
        ).thenReturn(result);

        when(
                result.next()
        ).thenReturn(true);

        when(
                result.getInt("id_medicament")
        ).thenReturn(1);

        try (MockedStatic<DBConnection> dbConnection =
                     mockStatic(DBConnection.class)) {

            dbConnection.when(
                    DBConnection::getConnection
            ).thenReturn(connection);

            int resultat =
                    medicamentDAO
                            .getIdMedicamentParNomEtDosage(
                                    "Paracetamol",
                                    "500mg"
                            );

            assertEquals(
                    1,
                    resultat
            );

            verify(statement).setString(
                    1,
                    "Paracetamol"
            );

            verify(statement).setString(
                    2,
                    "500mg"
            );
        }
    }

    @Test
    void doitRetournerMoinsUnSiMedicamentNonTrouve()
            throws Exception {

        Connection connection =
                mock(Connection.class);

        PreparedStatement statement =
                mock(PreparedStatement.class);

        ResultSet result =
                mock(ResultSet.class);

        when(
                connection.prepareStatement(anyString())
        ).thenReturn(statement);

        when(
                statement.executeQuery()
        ).thenReturn(result);

        when(
                result.next()
        ).thenReturn(false);

        try (MockedStatic<DBConnection> dbConnection =
                     mockStatic(DBConnection.class)) {

            dbConnection.when(
                    DBConnection::getConnection
            ).thenReturn(connection);

            int resultat =
                    medicamentDAO
                            .getIdMedicamentParNomEtDosage(
                                    "Paracetamol",
                                    "500mg"
                            );

            assertEquals(
                    -1,
                    resultat
            );
        }
    }

    @Test
    void doitLeverExceptionLorsRechercheMedicament()
            throws Exception {

        Connection connection =
                mock(Connection.class);

        when(
                connection.prepareStatement(anyString())
        ).thenThrow(
                new SQLException("Erreur SQL")
        );

        try (MockedStatic<DBConnection> dbConnection =
                     mockStatic(DBConnection.class)) {

            dbConnection.when(
                    DBConnection::getConnection
            ).thenReturn(connection);

            assertThrows(
                    AccesDonneesException.class,
                    () -> medicamentDAO
                            .getIdMedicamentParNomEtDosage(
                                    "Paracetamol",
                                    "500mg"
                            )
            );
        }
    }
}