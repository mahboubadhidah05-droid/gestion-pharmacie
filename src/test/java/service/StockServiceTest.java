package service;

import First_project.MedicamentDAO;
import First_project.StockHistoriqueDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StockServiceTest {

    @Mock
    private MedicamentDAO medicamentDAO;

    @Mock
    private StockHistoriqueDAO stockHistoriqueDAO;

    private StockService stockService;

    @BeforeEach
    void setUp() {
        stockService = new StockService(medicamentDAO, stockHistoriqueDAO);
    }

    @Test
    void ajouterStock_medicamentExistant_doitMettreAJourStockEtHistoriser() {
        when(medicamentDAO.getStock(4)).thenReturn(30);

        stockService.ajouterStock(4, 15);

        verify(medicamentDAO).updateStock(4, 45);
        verify(stockHistoriqueDAO).ajouterHistorique(4, 15);
    }

    @Test
    void ajouterStock_medicamentIntrouvable_neDoitRienMettreAJour() {
        when(medicamentDAO.getStock(404)).thenReturn(-1);

        stockService.ajouterStock(404, 10);

        verify(medicamentDAO, never()).updateStock(anyInt(), anyInt());
        verify(stockHistoriqueDAO, never()).ajouterHistorique(anyInt(), anyInt());
    }
}