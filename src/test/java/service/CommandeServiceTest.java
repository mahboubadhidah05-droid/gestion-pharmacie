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
import dao.LotMedicamentDAO;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommandeServiceTest {

    @Mock
    private CommandeDAO commandeDAO;

    @Mock
    private MedicamentDAO medicamentDAO;

    @Mock
    private StockHistoriqueDAO stockHistoriqueDAO;

    @Mock
    private LotMedicamentDAO lotMedicamentDAO;

    private CommandeService commandeService;

    @BeforeEach
    void setUp() {
        commandeService = new CommandeService(
                commandeDAO, medicamentDAO, stockHistoriqueDAO, lotMedicamentDAO
        );
    }

    @Test
    void creerCommande_sansFournisseur_doitCreerLaCommandeMettreAJourLeStockEtHistoriser() {
        when(medicamentDAO.getStock(5)).thenReturn(50);

        boolean resultat = commandeService.creerCommande(1, 5, 20, null, "2027-01-01");

        assertTrue(resultat);

        InOrder ordre = inOrder(medicamentDAO, commandeDAO, stockHistoriqueDAO, lotMedicamentDAO);
        ordre.verify(medicamentDAO).getStock(5);
        ordre.verify(commandeDAO).creerCommande(1, 5, 20, null);
        ordre.verify(medicamentDAO).updateStock(5, 70);
        ordre.verify(stockHistoriqueDAO).ajouterHistorique(5, 20);
        ordre.verify(lotMedicamentDAO).ajouterLot(5, 20, "2027-01-01");
    }

    @Test
    void creerCommande_avecFournisseur_doitTransmettreLIdFournisseur() {
        when(medicamentDAO.getStock(5)).thenReturn(50);

        boolean resultat = commandeService.creerCommande(1, 5, 20, 4, "2027-01-01");

        assertTrue(resultat);

        verify(commandeDAO).creerCommande(1, 5, 20, 4);
    }

    @Test
    void creerCommande_sansDatePeremption_doitQuandMemeCreerLeLot() {
        when(medicamentDAO.getStock(5)).thenReturn(50);

        boolean resultat = commandeService.creerCommande(1, 5, 20, null, null);

        assertTrue(resultat);

        verify(lotMedicamentDAO).ajouterLot(5, 20, null);
    }

    @Test
    void creerCommande_medicamentIntrouvable_neDoitRienCreer() {
        when(medicamentDAO.getStock(999)).thenReturn(-1);

        boolean resultat = commandeService.creerCommande(1, 999, 10, null, "2027-01-01");

        assertFalse(resultat);
        verify(commandeDAO, never())
                .creerCommande(anyInt(), anyInt(), anyInt(), nullable(Integer.class));
        verify(medicamentDAO, never()).updateStock(anyInt(), anyInt());
        verify(stockHistoriqueDAO, never()).ajouterHistorique(anyInt(), anyInt());
        verify(lotMedicamentDAO, never()).ajouterLot(anyInt(), anyInt(), anyString());
    }
}