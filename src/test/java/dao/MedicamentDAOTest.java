package dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import exception.AccesDonneesException;
import utils.DBConnection;

@ExtendWith(MockitoExtension.class)
class MedicamentDAOTest {

    private static final String NOM = "Doliprane";
    private static final String DOSAGE = "500mg";
    private static final int STOCK = 100;
    private static final double PRIX = 3.5;
    private static final int SEUIL = 10;

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


    @Test
    void ajouterMedicament_casNominal_executeInsertion()
            throws SQLException {

        try (MockedStatic<DBConnection> mockedDb =
                     mockStatic(DBConnection.class)) {

            mockedDb.when(DBConnection::getConnection)
                    .thenReturn(connection);

            when(connection.prepareStatement(anyString()))
                    .thenReturn(preparedStatement);

            medicamentDAO.ajouterMedicament(
                    NOM,
                    DOSAGE,
                    STOCK,
                    PRIX,
                    SEUIL
            );

            verify(preparedStatement).setString(1, NOM);
            verify(preparedStatement).setString(2, DOSAGE);
            verify(preparedStatement).setInt(3, STOCK);
            verify(preparedStatement).setDouble(4, PRIX);
            verify(preparedStatement).setInt(5, SEUIL);
            verify(preparedStatement).executeUpdate();
        }
    }


    @Test
    void ajouterMedicament_erreurSQL_doitLeverAccesDonneesException()
            throws SQLException {

        try (MockedStatic<DBConnection> mockedDb =
                     mockStatic(DBConnection.class)) {

            mockedDb.when(DBConnection::getConnection)
                    .thenThrow(new SQLException("Erreur connexion"));

            assertThrows(
                    AccesDonneesException.class,
                    () -> medicamentDAO.ajouterMedicament(
                            NOM,
                            DOSAGE,
                            STOCK,
                            PRIX,
                            SEUIL
                    )
            );
        }
    }


    @Test
    void getStock_medicamentExiste_retourneStock()
            throws SQLException {

        try (MockedStatic<DBConnection> mockedDb =
                     mockStatic(DBConnection.class)) {

            mockedDb.when(DBConnection::getConnection)
                    .thenReturn(connection);

            when(connection.prepareStatement(anyString()))
                    .thenReturn(preparedStatement);

            when(preparedStatement.executeQuery())
                    .thenReturn(resultSet);

            when(resultSet.next())
                    .thenReturn(true);

            when(resultSet.getInt("stock"))
                    .thenReturn(50);

            assertEquals(
                    50,
                    medicamentDAO.getStock(1)
            );
        }
    }


    @Test
    void getStock_medicamentAbsent_retourneMoinsUn()
            throws SQLException {

        try (MockedStatic<DBConnection> mockedDb =
                     mockStatic(DBConnection.class)) {

            mockedDb.when(DBConnection::getConnection)
                    .thenReturn(connection);

            when(connection.prepareStatement(anyString()))
                    .thenReturn(preparedStatement);

            when(preparedStatement.executeQuery())
                    .thenReturn(resultSet);

            when(resultSet.next())
                    .thenReturn(false);

            assertEquals(
                    -1,
                    medicamentDAO.getStock(999)
            );
        }
    }


    @Test
    void getStock_erreurConnexion_doitLeverAccesDonneesException()
            throws SQLException {

        try (MockedStatic<DBConnection> mockedDb =
                     mockStatic(DBConnection.class)) {

            mockedDb.when(DBConnection::getConnection)
                    .thenThrow(new SQLException("Erreur connexion"));

            assertThrows(
                    AccesDonneesException.class,
                    () -> medicamentDAO.getStock(1)
            );
        }
    }


    @Test
    void updateStock_casNominal_executeMiseAJour()
            throws SQLException {

        try (MockedStatic<DBConnection> mockedDb =
                     mockStatic(DBConnection.class)) {

            mockedDb.when(DBConnection::getConnection)
                    .thenReturn(connection);

            when(connection.prepareStatement(anyString()))
                    .thenReturn(preparedStatement);

            medicamentDAO.updateStock(5, 80);

            verify(preparedStatement)
                    .setInt(1, 80);

            verify(preparedStatement)
                    .setInt(2, 5);

            verify(preparedStatement)
                    .executeUpdate();
        }
    }


    @Test
    void updateStock_erreurConnexion_doitLeverAccesDonneesException()
            throws SQLException {

        try (MockedStatic<DBConnection> mockedDb =
                     mockStatic(DBConnection.class)) {

            mockedDb.when(DBConnection::getConnection)
                    .thenThrow(new SQLException("Erreur connexion"));

            assertThrows(
                    AccesDonneesException.class,
                    () -> medicamentDAO.updateStock(5, 80)
            );
        }
    }


    @Test
    void stockCritique_nonCritique_retourneNull()
            throws SQLException {

        try (MockedStatic<DBConnection> mockedDb =
                     mockStatic(DBConnection.class)) {

            mockedDb.when(DBConnection::getConnection)
                    .thenReturn(connection);

            when(connection.prepareStatement(anyString()))
                    .thenReturn(preparedStatement);

            when(preparedStatement.executeQuery())
                    .thenReturn(resultSet);

            when(resultSet.next())
                    .thenReturn(false);

            assertNull(
                    medicamentDAO.stockCritique(8)
            );
        }
    }


    @Test
    void getIdMedicamentParNomEtDosage_trouve_retourneId()
            throws SQLException {

        try (MockedStatic<DBConnection> mockedDb =
                     mockStatic(DBConnection.class)) {

            mockedDb.when(DBConnection::getConnection)
                    .thenReturn(connection);

            when(connection.prepareStatement(anyString()))
                    .thenReturn(preparedStatement);

            when(preparedStatement.executeQuery())
                    .thenReturn(resultSet);

            when(resultSet.next())
                    .thenReturn(true);

            when(resultSet.getInt("id_medicament"))
                    .thenReturn(12);

            assertEquals(
                    12,
                    medicamentDAO.getIdMedicamentParNomEtDosage(
                            NOM,
                            DOSAGE
                    )
            );
        }
    }
}