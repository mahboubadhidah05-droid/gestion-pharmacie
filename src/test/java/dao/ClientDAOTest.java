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
class ClientDAOTest {

    private static final String NOM = "Trabelsi";
    private static final String PRENOM = "Amine";
    private static final String EMAIL = "amine.trabelsi@mail.com";
    private static final String ADRESSE = "Ariana";

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement preparedStatement;

    private ClientDAO clientDAO;


    @BeforeEach
    void setUp() {
        clientDAO = new ClientDAO();
    }


    @Test
    void ajouterClient_casNominal_executeInsertion()
            throws SQLException {

        try (MockedStatic<DBConnection> mockedDb =
                     mockStatic(DBConnection.class)) {

            mockedDb.when(DBConnection::getConnection)
                    .thenReturn(connection);

            when(connection.prepareStatement(anyString()))
                    .thenReturn(preparedStatement);


            clientDAO.ajouterClient(
                    NOM,
                    PRENOM,
                    EMAIL,
                    ADRESSE
            );


            verify(preparedStatement)
                    .setString(1, NOM);

            verify(preparedStatement)
                    .setString(2, PRENOM);

            verify(preparedStatement)
                    .setString(3, EMAIL);

            verify(preparedStatement)
                    .setString(4, ADRESSE);

            verify(preparedStatement)
                    .executeUpdate();
        }
    }


    @Test
    void ajouterClient_erreurSQL_nePropagePasException()
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
                    clientDAO.ajouterClient(
                            NOM,
                            PRENOM,
                            EMAIL,
                            ADRESSE
                    )
            );
        }
    }


    @Test
    void ajouterClient_erreurPreparation_nePropagePasException()
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
                    clientDAO.ajouterClient(
                            NOM,
                            PRENOM,
                            EMAIL,
                            ADRESSE
                    )
            );
        }
    }


    @Test
    void ajouterClient_executionSQL_EchoueNePropagePasException()
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
                    clientDAO.ajouterClient(
                            NOM,
                            PRENOM,
                            EMAIL,
                            ADRESSE
                    )
            );


            verify(preparedStatement)
                    .executeUpdate();
        }
    }
}