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
                    10,
                    "2026-12-31",
                    true,
                    0.7,
                    "1234567890123",
                    "comprimé",
                    "Sanofi"
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

            verify(statement).setString(
                    6,
                    "2026-12-31"
            );

            verify(statement).setBoolean(
                    7,
                    true
            );

            verify(statement).setDouble(
                    8,
                    0.7
            );

            verify(statement).setString(
                    9,
                    "1234567890123"
            );

            verify(statement).setString(
                    10,
                    "comprimé"
            );

            verify(statement).setString(
                    11,
                    "Sanofi"
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
                            10,
                            "2026-12-31",
                            true,
                            0.7,
                            "1234567890123",
                            "comprimé",
                            "Sanofi"
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

        when(
                result.getString("date_peremption")
        ).thenReturn("2026-12-31");

        when(
                result.getBoolean("conventionne_cnam")
        ).thenReturn(true);

        when(
                result.getDouble("taux_remboursement")
        ).thenReturn(0.7);

        when(
                result.getString("code_barre")
        ).thenReturn("1234567890123");

        when(
                result.getString("forme")
        ).thenReturn("comprimé");

        when(
                result.getString("fabricant")
        ).thenReturn("Sanofi");

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

            assertEquals(
                    "2026-12-31",
                    resultat.get(0).datePeremption()
            );

            assertEquals(
                    true,
                    resultat.get(0).conventionneCnam()
            );

            assertEquals(
                    0.7,
                    resultat.get(0).tauxRemboursement()
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

    @Test
    void doitListerLesNomsDeMedicaments() throws Exception {

        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        ResultSet result = mock(ResultSet.class);

        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(statement.executeQuery()).thenReturn(result);
        when(result.next()).thenReturn(true, false);
        when(result.getString("nom")).thenReturn("Paracetamol");
        when(result.getString("dosage")).thenReturn("500mg");

        try (MockedStatic<DBConnection> dbConnection =
                     mockStatic(DBConnection.class)) {

            dbConnection.when(
                    DBConnection::getConnection
            ).thenReturn(connection);

            List<dto.NomMedicamentResponse> resultat =
                    medicamentDAO.listerNoms();

            assertEquals(1, resultat.size());
            assertEquals("Paracetamol", resultat.get(0).nom());
        }
    }

    @Test
    void doitLeverExceptionSiListeNomsEchoue() throws Exception {

        Connection connection = mock(Connection.class);

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
                    () -> medicamentDAO.listerNoms()
            );
        }
    }

    @Test
    void doitRetournerLaDatePeremption() throws Exception {

        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        ResultSet result = mock(ResultSet.class);

        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(statement.executeQuery()).thenReturn(result);
        when(result.next()).thenReturn(true);
        when(result.getString("date_peremption")).thenReturn("2026-01-01");

        try (MockedStatic<DBConnection> dbConnection =
                     mockStatic(DBConnection.class)) {

            dbConnection.when(
                    DBConnection::getConnection
            ).thenReturn(connection);

            assertEquals(
                    "2026-01-01",
                    medicamentDAO.getDatePeremption(1)
            );
        }
    }

    @Test
    void doitRetournerNullSiPasDeDatePeremption() throws Exception {

        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        ResultSet result = mock(ResultSet.class);

        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(statement.executeQuery()).thenReturn(result);
        when(result.next()).thenReturn(false);

        try (MockedStatic<DBConnection> dbConnection =
                     mockStatic(DBConnection.class)) {

            dbConnection.when(
                    DBConnection::getConnection
            ).thenReturn(connection);

            assertEquals(
                    null,
                    medicamentDAO.getDatePeremption(999)
            );
        }
    }

    @Test
    void doitLeverExceptionSiRecuperationDatePeremptionEchoue()
            throws Exception {

        Connection connection = mock(Connection.class);

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
                    () -> medicamentDAO.getDatePeremption(1)
            );
        }
    }

    @Test
    void doitRetournerLesInfosVente() throws Exception {

        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        ResultSet result = mock(ResultSet.class);

        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(statement.executeQuery()).thenReturn(result);
        when(result.next()).thenReturn(true);
        when(result.getDouble("prix")).thenReturn(5.5);
        when(result.getBoolean("conventionne_cnam")).thenReturn(true);
        when(result.getDouble("taux_remboursement")).thenReturn(0.7);

        try (MockedStatic<DBConnection> dbConnection =
                     mockStatic(DBConnection.class)) {

            dbConnection.when(
                    DBConnection::getConnection
            ).thenReturn(connection);

            dto.MedicamentVenteInfo resultat =
                    medicamentDAO.getInfosVente(1);

            assertEquals(5.5, resultat.prix());
            assertEquals(true, resultat.conventionneCnam());
            assertEquals(0.7, resultat.tauxRemboursement());
        }
    }

    @Test
    void doitRetournerNullSiInfosVenteIntrouvables() throws Exception {

        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        ResultSet result = mock(ResultSet.class);

        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(statement.executeQuery()).thenReturn(result);
        when(result.next()).thenReturn(false);

        try (MockedStatic<DBConnection> dbConnection =
                     mockStatic(DBConnection.class)) {

            dbConnection.when(
                    DBConnection::getConnection
            ).thenReturn(connection);

            assertNull(medicamentDAO.getInfosVente(999));
        }
    }

    @Test
    void doitLeverExceptionSiInfosVenteEchoue() throws Exception {

        Connection connection = mock(Connection.class);

        when(connection.prepareStatement(anyString()))
                .thenThrow(new SQLException("Erreur SQL"));

        try (MockedStatic<DBConnection> dbConnection =
                     mockStatic(DBConnection.class)) {

            dbConnection.when(
                    DBConnection::getConnection
            ).thenReturn(connection);

            assertThrows(
                    AccesDonneesException.class,
                    () -> medicamentDAO.getInfosVente(1)
            );
        }
    }

    @Test
    void doitRetournerIdParCodeBarre() throws Exception {

        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        ResultSet result = mock(ResultSet.class);

        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(statement.executeQuery()).thenReturn(result);
        when(result.next()).thenReturn(true);
        when(result.getInt("id_medicament")).thenReturn(4);

        try (MockedStatic<DBConnection> dbConnection =
                     mockStatic(DBConnection.class)) {

            dbConnection.when(
                    DBConnection::getConnection
            ).thenReturn(connection);

            int resultat = medicamentDAO.getIdParCodeBarre("1234567890123");

            assertEquals(4, resultat);

            verify(statement).setString(1, "1234567890123");
        }
    }

    @Test
    void doitRetournerMoinsUnSiCodeBarreIntrouvable() throws Exception {

        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        ResultSet result = mock(ResultSet.class);

        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(statement.executeQuery()).thenReturn(result);
        when(result.next()).thenReturn(false);

        try (MockedStatic<DBConnection> dbConnection =
                     mockStatic(DBConnection.class)) {

            dbConnection.when(
                    DBConnection::getConnection
            ).thenReturn(connection);

            assertEquals(
                    -1,
                    medicamentDAO.getIdParCodeBarre("0000000000000")
            );
        }
    }

    @Test
    void doitLeverExceptionSiRecherchesParCodeBarreEchoue() throws Exception {

        Connection connection = mock(Connection.class);

        when(connection.prepareStatement(anyString()))
                .thenThrow(new SQLException("Erreur SQL"));

        try (MockedStatic<DBConnection> dbConnection =
                     mockStatic(DBConnection.class)) {

            dbConnection.when(
                    DBConnection::getConnection
            ).thenReturn(connection);

            assertThrows(
                    AccesDonneesException.class,
                    () -> medicamentDAO.getIdParCodeBarre("1234567890123")
            );
        }
    }

    @Test
    void doitRetournerResumeParId() throws Exception {

        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        ResultSet result = mock(ResultSet.class);

        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(statement.executeQuery()).thenReturn(result);
        when(result.next()).thenReturn(true);
        when(result.getString("nom")).thenReturn("antafen");
        when(result.getString("dosage")).thenReturn("100mg");
        when(result.getInt("stock")).thenReturn(160);

        try (MockedStatic<DBConnection> dbConnection =
                     mockStatic(DBConnection.class)) {

            dbConnection.when(
                    DBConnection::getConnection
            ).thenReturn(connection);

            dto.MedicamentScanResponse resultat =
                    medicamentDAO.getResumeParId(4);

            assertEquals("antafen", resultat.nom());
            assertEquals(160, resultat.stock());
        }
    }

    @Test
    void doitRetournerNullSiResumeIntrouvable() throws Exception {

        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        ResultSet result = mock(ResultSet.class);

        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(statement.executeQuery()).thenReturn(result);
        when(result.next()).thenReturn(false);

        try (MockedStatic<DBConnection> dbConnection =
                     mockStatic(DBConnection.class)) {

            dbConnection.when(
                    DBConnection::getConnection
            ).thenReturn(connection);

            assertEquals(null, medicamentDAO.getResumeParId(999));
        }
    }

    @Test
    void doitLeverExceptionSiResumeEchoue() throws Exception {

        Connection connection = mock(Connection.class);

        when(connection.prepareStatement(anyString()))
                .thenThrow(new SQLException("Erreur SQL"));

        try (MockedStatic<DBConnection> dbConnection =
                     mockStatic(DBConnection.class)) {

            dbConnection.when(
                    DBConnection::getConnection
            ).thenReturn(connection);

            assertThrows(
                    AccesDonneesException.class,
                    () -> medicamentDAO.getResumeParId(4)
            );
        }
    }

    @Test
    void doitRechercherParDebutNom() throws Exception {

        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        ResultSet result = mock(ResultSet.class);

        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(statement.executeQuery()).thenReturn(result);
        when(result.next()).thenReturn(true, true, false);
        when(result.getInt("id_medicament")).thenReturn(1, 2);
        when(result.getString("nom")).thenReturn("Doliprane", "Doliprane");
        when(result.getString("dosage")).thenReturn("500mg", "1g");
        when(result.getString("forme")).thenReturn("comprimé", "comprimé");
        when(result.getString("fabricant")).thenReturn("Sanofi", "GenPharma");

        try (MockedStatic<DBConnection> dbConnection =
                     mockStatic(DBConnection.class)) {

            dbConnection.when(
                    DBConnection::getConnection
            ).thenReturn(connection);

            List<dto.MedicamentAutocompleteResponse> resultat =
                    medicamentDAO.rechercherParDebutNom("Do");

            assertEquals(2, resultat.size());
            assertEquals("Sanofi", resultat.get(0).fabricant());
            assertEquals("GenPharma", resultat.get(1).fabricant());

            verify(statement).setString(1, "Do%");
        }
    }

    @Test
    void doitLeverExceptionSiRechercheParDebutNomEchoue() throws Exception {

        Connection connection = mock(Connection.class);

        when(connection.prepareStatement(anyString()))
                .thenThrow(new SQLException("Erreur SQL"));

        try (MockedStatic<DBConnection> dbConnection =
                     mockStatic(DBConnection.class)) {

            dbConnection.when(
                    DBConnection::getConnection
            ).thenReturn(connection);

            assertThrows(
                    AccesDonneesException.class,
                    () -> medicamentDAO.rechercherParDebutNom("Do")
            );
        }
    }
}