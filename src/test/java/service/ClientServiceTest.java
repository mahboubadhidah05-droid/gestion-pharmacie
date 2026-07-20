package service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import dao.ClientDAO;

import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
class ClientServiceTest {

    @Mock
    private ClientDAO clientDAO;

    private ClientService service;

    @BeforeEach
    void setUp() {
        service =
                new ClientService(clientDAO);
    }

    @Test
    void doitCreerClient() {

        String nom = "Dupont";
        String prenom = "Jean";
        String email = "jean@example.com";
        String adresse = "Tunis";
        int idClient = 1;

        when(
                clientDAO.ajouterClient(
                        nom,
                        prenom,
                        email,
                        adresse
                )
        ).thenReturn(idClient);

        int resultat =
                service.creerClient(
                        nom,
                        prenom,
                        email,
                        adresse
                );

        assertEquals(
                idClient,
                resultat
        );

        verify(clientDAO).ajouterClient(
                nom,
                prenom,
                email,
                adresse
        );
    }

    @Test
    void doitRetournerTrueSiClientExiste() {

        int idClient = 1;

        when(
                clientDAO.existeClient(idClient)
        ).thenReturn(true);

        boolean resultat =
                service.existeClient(idClient);

        assertTrue(resultat);

        verify(clientDAO).existeClient(
                idClient
        );
    }

    @Test
    void doitRetournerFalseSiClientNExistePas() {

        int idClient = 99;

        when(
                clientDAO.existeClient(idClient)
        ).thenReturn(false);

        boolean resultat =
                service.existeClient(idClient);

        assertFalse(resultat);

        verify(clientDAO).existeClient(
                idClient
        );
    }
}