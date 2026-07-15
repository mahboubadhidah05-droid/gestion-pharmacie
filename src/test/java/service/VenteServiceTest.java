package service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import dao.MedicamentDAO;
import dao.StockHistoriqueDAO;
import dao.VenteDAO;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
        when(venteDAO.enregistrerVente(2, 3, 1, 4)).thenReturn(true);

        venteService.vendre(2, 3, 1, 4);

        verify(venteDAO).enregistrerVente(2, 3, 1, 4);
        verify(medicamentDAO).updateStock(1, 6);
        verify(stockHistoriqueDAO).ajouterHistorique(1, -4);
    }

    @Test
    void vendre_echecEnregistrement_neDoitPasMettreAJourStock() {
        when(medicamentDAO.getStock(1)).thenReturn(10);
        when(venteDAO.enregistrerVente(2, 3, 1, 4)).thenReturn(false);

        venteService.vendre(2, 3, 1, 4);

        verify(venteDAO).enregistrerVente(2, 3, 1, 4);
        verify(medicamentDAO, never()).updateStock(anyInt(), anyInt());
        verify(stockHistoriqueDAO, never()).ajouterHistorique(anyInt(), anyInt());
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
    void annulerVente_doitAppelerLeDaoEtRetournerVrai() {
        when(venteDAO.annulerVente(99)).thenReturn(true);

        boolean resultat = venteService.annulerVente(99);

        assertTrue(resultat);
        verify(venteDAO).annulerVente(99);
    }

    @Test
    void annulerVente_venteInexistante_doitRetournerFaux() {
        when(venteDAO.annulerVente(999)).thenReturn(false);

        boolean resultat = venteService.annulerVente(999);

        assertFalse(resultat);
        verify(venteDAO).annulerVente(999);
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