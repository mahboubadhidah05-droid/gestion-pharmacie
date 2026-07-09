package service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import dao.CommandeDAO;
import dao.MedicamentDAO;
import dao.StockHistoriqueDAO;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommandeServiceTest {

    @Mock
    private CommandeDAO commandeDAO;

    @Mock
    private MedicamentDAO medicamentDAO;

    @Mock
    private StockHistoriqueDAO stockHistoriqueDAO;

    private CommandeService commandeService;

    @BeforeEach
    void setUp() {
        commandeService = new CommandeService(commandeDAO, medicamentDAO, stockHistoriqueDAO);
    }

    @Test
    void creerCommande_doitCreerLaCommandeMettreAJourLeStockEtHistoriser() {
        when(medicamentDAO.getStock(5)).thenReturn(50);

        commandeService.creerCommande(1, 5, 20);

        InOrder ordre = inOrder(commandeDAO, medicamentDAO, stockHistoriqueDAO);
        ordre.verify(commandeDAO).creerCommande(1, 5, 20);
        ordre.verify(medicamentDAO).getStock(5);
        ordre.verify(medicamentDAO).updateStock(5, 70);
        ordre.verify(stockHistoriqueDAO).ajouterHistorique(5, 20);
    }

    @Test
    void creerCommande_medicamentIntrouvable_doitPropagerStockNegatif() {
        when(medicamentDAO.getStock(999)).thenReturn(-1);

        commandeService.creerCommande(1, 999, 10);

        verify(medicamentDAO).updateStock(999, 9);
    }
}