package service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import dao.ClientDAO;

import static org.mockito.Mockito.*;

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
    void creerClient_doitAppelerLeDaoAvecLesBonnesDonnees() {
        clientService.creerClient("Ben Ali", "Sami", "sami@mail.com", "Tunis");

        verify(clientDAO, times(1))
                .ajouterClient("Ben Ali", "Sami", "sami@mail.com", "Tunis");
    }

    @Test
    void creerClient_avecEmailNull_doitTransmettreTelQuelAuDao() {
        clientService.creerClient("Trabelsi", "Amine", null, "Sfax");

        verify(clientDAO).ajouterClient("Trabelsi", "Amine", null, "Sfax");
    }
}