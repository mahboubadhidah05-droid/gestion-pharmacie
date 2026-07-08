package service;

import First_project.MedicamentDAO;
import First_project.StockHistoriqueDAO;
import First_project.VenteDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VenteServiceTest {

    @Mock
    private MedicamentDAO medicamentDAO;

    @Mock
    private VenteDAO venteDAO;

    @Mock
    private StockHistoriqueDAO stockHistoriqueDAO;

    private VenteService venteService;

    @BeforeEach
    void setUp() {
        venteService = new VenteService(medicamentDAO, venteDAO, stockHistoriqueDAO);
    }

    @Test
    void vendre_stockSuffisant_doitEnregistrerVenteEtMettreAJourStock() {
        when(medicamentDAO.getStock(1)).thenReturn(10);

        venteService.vendre(2, 3, 1, 4);

        verify(venteDAO).enregistrerVente(2, 3, 1, 4);
        verify(medicamentDAO).updateStock(1, 6);
        verify(stockHistoriqueDAO).ajouterHistorique(1, -4);
    }

    @Test
    void vendre_stockInsuffisant_neDoitRienEnregistrer() {
        when(medicamentDAO.getStock(1)).thenReturn(2);

        venteService.vendre(2, 3, 1, 10);

        verify(venteDAO, never()).enregistrerVente(anyInt(), anyInt(), anyInt(), anyInt());
        verify(medicamentDAO, never()).updateStock(anyInt(), anyInt());
        verify(stockHistoriqueDAO, never()).ajouterHistorique(anyInt(), anyInt());
    }

    @Test
    void annulerVente_doitAppelerLeDao() {
        venteService.annulerVente(99);

        verify(venteDAO).annulerVente(99);
    }

    @Test
    void ventesParMedicament_doitDelegueeAuDao() {
        venteService.ventesParMedicament(1);

        verify(venteDAO).ventesParMedicament(1);
    }

    @Test
    void ventesParPeriode_doitDelegueeAuDao() {
        venteService.ventesParPeriode("2026-01-01", "2026-01-31");

        verify(venteDAO).ventesParPeriode("2026-01-01", "2026-01-31");
    }
}