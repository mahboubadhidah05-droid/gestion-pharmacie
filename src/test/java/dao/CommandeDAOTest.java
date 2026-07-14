package dao;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import utils.DBConnection;

@ExtendWith(MockitoExtension.class)
class CommandeDAOTest {

    private static final int ID_GESTIONNAIRE = 1;
    private static final int ID_MEDICAMENT = 10;
    private static final int QUANTITE = 25;


    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement preparedStatement;


    private CommandeDAO commandeDAO;


    @BeforeEach
    void setUp() {
        commandeDAO = new CommandeDAO();
    }


    @Test
    void creerCommande_casNominal_executeInsertion()
            throws SQLException {

        try (MockedStatic<DBConnection> mockedDb =
                     mockStatic(DBConnection.class)) {


            mockedDb.when(DBConnection::getConnection)
                    .thenReturn(connection);


            when(connection.prepareStatement(anyString()))
                    .thenReturn(preparedStatement);


            commandeDAO.creerCommande(
                    ID_GESTIONNAIRE,
                    ID_MEDICAMENT,
                    QUANTITE
            );


            verify(preparedStatement)
                    .setInt(1, ID_GESTIONNAIRE);

            verify(preparedStatement)
                    .setInt(2, ID_MEDICAMENT);

            verify(preparedStatement)
                    .setInt(3, QUANTITE);

            verify(preparedStatement)
                    .executeUpdate();
        }
    }


    @Test
    void creerCommande_erreurConnexion_nePropagePasException()
            throws SQLException {

        try (MockedStatic<DBConnection> mockedDb =
                     mockStatic(DBConnection.class)) {


            mockedDb.when(DBConnection::getConnection)
                    .thenThrow(
                            new SQLException(
                                    "Erreur connexion"
                            )
                    );


            assertDoesNotThrow(() ->
                    commandeDAO.creerCommande(
                            ID_GESTIONNAIRE,
                            ID_MEDICAMENT,
                            QUANTITE
                    )
            );
        }
    }


    @Test
    void creerCommande_erreurPreparation_nePropagePasException()
            throws SQLException {

        try (MockedStatic<DBConnection> mockedDb =
                     mockStatic(DBConnection.class)) {


            mockedDb.when(DBConnection::getConnection)
                    .thenReturn(connection);


            when(connection.prepareStatement(anyString()))
                    .thenThrow(
                            new SQLException(
                                    "Erreur préparation"
                            )
                    );


            assertDoesNotThrow(() ->
                    commandeDAO.creerCommande(
                            ID_GESTIONNAIRE,
                            ID_MEDICAMENT,
                            QUANTITE
                    )
            );
        }
    }


    @Test
    void creerCommande_executionSQL_EchoueNePropagePasException()
            throws SQLException {

        try (MockedStatic<DBConnection> mockedDb =
                     mockStatic(DBConnection.class)) {


            mockedDb.when(DBConnection::getConnection)
                    .thenReturn(connection);


            when(connection.prepareStatement(anyString()))
                    .thenReturn(preparedStatement);


            when(preparedStatement.executeUpdate())
                    .thenThrow(
                            new SQLException(
                                    "Erreur SQL"
                            )
                    );


            assertDoesNotThrow(() ->
                    commandeDAO.creerCommande(
                            ID_GESTIONNAIRE,
                            ID_MEDICAMENT,
                            QUANTITE
                    )
            );


            verify(preparedStatement)
                    .executeUpdate();
        }
    }
}