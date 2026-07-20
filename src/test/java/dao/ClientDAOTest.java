package dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
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
class ClientDAOTest {

    private static final String NOM = "Trabelsi";
    private static final String PRENOM = "Amine";
    private static final String EMAIL = "amine.trabelsi@mail.com";
    private static final String ADRESSE = "Ariana";
    private static final int ID_GENERE = 7;

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private ResultSet generatedKeys;

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

            when(connection.prepareStatement(anyString(), anyInt()))
                    .thenReturn(preparedStatement);

            when(preparedStatement.getGeneratedKeys())
                    .thenReturn(generatedKeys);

            when(generatedKeys.next())
                    .thenReturn(true);

            when(generatedKeys.getInt(1))
                    .thenReturn(ID_GENERE);


            int idResultat = clientDAO.ajouterClient(
                    NOM,
                    PRENOM,
                    EMAIL,
                    ADRESSE
            );


            assertEquals(ID_GENERE, idResultat);

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
    void ajouterClient_erreurSQL_doitLeverAccesDonneesException()
            throws SQLException {

        try (MockedStatic<DBConnection> mockedDb =
                     mockStatic(DBConnection.class)) {


            mockedDb.when(DBConnection::getConnection)
                    .thenThrow(
                            new SQLException(
                                    "Erreur connexion"
                            )
                    );


            assertThrows(
                    AccesDonneesException.class,
                    () -> clientDAO.ajouterClient(
                            NOM,
                            PRENOM,
                            EMAIL,
                            ADRESSE
                    )
            );
        }
    }


    @Test
    void ajouterClient_erreurPreparation_doitLeverAccesDonneesException()
            throws SQLException {

        try (MockedStatic<DBConnection> mockedDb =
                     mockStatic(DBConnection.class)) {


            mockedDb.when(DBConnection::getConnection)
                    .thenReturn(connection);


            when(connection.prepareStatement(anyString(), anyInt()))
                    .thenThrow(
                            new SQLException(
                                    "Erreur préparation"
                            )
                    );


            assertThrows(
                    AccesDonneesException.class,
                    () -> clientDAO.ajouterClient(
                            NOM,
                            PRENOM,
                            EMAIL,
                            ADRESSE
                    )
            );
        }
    }


    @Test
    void ajouterClient_executionSQL_doitLeverAccesDonneesException()
            throws SQLException {

        try (MockedStatic<DBConnection> mockedDb =
                     mockStatic(DBConnection.class)) {


            mockedDb.when(DBConnection::getConnection)
                    .thenReturn(connection);


            when(connection.prepareStatement(anyString(), anyInt()))
                    .thenReturn(preparedStatement);


            when(preparedStatement.executeUpdate())
                    .thenThrow(
                            new SQLException(
                                    "Erreur SQL"
                            )
                    );


            assertThrows(
                    AccesDonneesException.class,
                    () -> clientDAO.ajouterClient(
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