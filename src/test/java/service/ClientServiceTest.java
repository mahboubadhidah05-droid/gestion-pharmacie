package service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import dao.ClientDAO;

@ExtendWith(MockitoExtension.class)
class ClientServiceTest {

    @Mock
    private ClientDAO clientDAO;

    private ClientService clientService;

    @BeforeEach
    void setUp() {
        clientService = new ClientService(clientDAO);
    }

    @Test
    void creerClient_doitDeleguerAuDaoAvecNumeroCnam() {

        when(
                clientDAO.ajouterClient(
                        "Ben Ali", "Sami", "sami@exemple.com",
                        "Rue de la Paix", "CNAM12345"
                )
        ).thenReturn(3);

        int resultat = clientService.creerClient(
                "Ben Ali", "Sami", "sami@exemple.com",
                "Rue de la Paix", "CNAM12345"
        );

        assertEquals(3, resultat);

        verify(clientDAO).ajouterClient(
                "Ben Ali", "Sami", "sami@exemple.com",
                "Rue de la Paix", "CNAM12345"
        );
    }

    @Test
    void creerClient_doitAccepterNumeroCnamNul() {

        when(
                clientDAO.ajouterClient(
                        "Nom", "Prenom", "email@exemple.com",
                        "Adresse", null
                )
        ).thenReturn(5);

        int resultat = clientService.creerClient(
                "Nom", "Prenom", "email@exemple.com", "Adresse", null
        );

        assertEquals(5, resultat);
    }

    @Test
    void existeClient_doitDeleguerAuDao() {

        when(clientDAO.existeClient(1)).thenReturn(true);

        assertTrue(clientService.existeClient(1));

        verify(clientDAO).existeClient(1);
    }

    @Test
    void existeClient_doitRetournerFalseSiInexistant() {

        when(clientDAO.existeClient(999)).thenReturn(false);

        assertFalse(clientService.existeClient(999));
    }
}