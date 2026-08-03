package service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dao.MedicamentDAO;
import dao.StockHistoriqueDAO;
import dao.VenteDAO;
import dao.LotMedicamentDAO;
import dto.LotResponse;
import dto.MedicamentVenteInfo;
import dto.VenteResponse;

class VenteServiceTest {

    private MedicamentDAO medDAO;
    private VenteDAO venteDAO;
    private StockHistoriqueDAO histDAO;
    private LotMedicamentDAO lotDAO;
    private VenteService venteService;

    @BeforeEach
    void setUp() {
        medDAO = org.mockito.Mockito.mock(
                MedicamentDAO.class
        );

        venteDAO = org.mockito.Mockito.mock(
                VenteDAO.class
        );

        histDAO = org.mockito.Mockito.mock(
                StockHistoriqueDAO.class
        );

        lotDAO = org.mockito.Mockito.mock(
                LotMedicamentDAO.class
        );

        venteService = new VenteService(
                medDAO,
                venteDAO,
                histDAO,
                lotDAO
        );
    }

    @Test
    void vendreDoitRetournerFalseSiStockInsuffisant() {

        when(medDAO.getStock(1)).thenReturn(5);

        boolean resultat = venteService.vendre(10, 20, 1, 10);

        assertFalse(resultat);

        verify(venteDAO, never()).enregistrerVente(
                anyInt(), anyInt(), anyInt(), anyInt(),
                anyDouble(), anyDouble()
        );

        verify(medDAO, never()).updateStock(anyInt(), anyInt());
        verify(histDAO, never()).ajouterHistorique(anyInt(), anyInt());
    }

    @Test
    void vendreDoitEnregistrerVenteEtMettreAJourStockSansCnam() {

        when(medDAO.getStock(1)).thenReturn(100);

        when(medDAO.getInfosVente(1)).thenReturn(
                new MedicamentVenteInfo(5.0, false, 0.0)
        );

        when(venteDAO.enregistrerVente(10, 20, 1, 30, 0.0, 150.0))
                .thenReturn(true);

        when(medDAO.stockCritique(1)).thenReturn(null);

        boolean resultat = venteService.vendre(10, 20, 1, 30);

        assertTrue(resultat);

        verify(venteDAO).enregistrerVente(10, 20, 1, 30, 0.0, 150.0);
        verify(medDAO).updateStock(1, 70);
        verify(histDAO).ajouterHistorique(1, -30);
        verify(medDAO).stockCritique(1);
    }

    @Test
    void vendreDoitCalculerLeRemboursementCnam() {

        /* Médicament conventionné à 70% : prix 10 x qté 30 = 300 total,
           210 remboursés par la CNAM, 90 restent à la charge du client. */
        when(medDAO.getStock(1)).thenReturn(100);

        when(medDAO.getInfosVente(1)).thenReturn(
                new MedicamentVenteInfo(10.0, true, 0.7)
        );

        when(venteDAO.enregistrerVente(10, 20, 1, 30, 210.0, 90.0))
                .thenReturn(true);

        when(medDAO.stockCritique(1)).thenReturn(null);

        boolean resultat = venteService.vendre(10, 20, 1, 30);

        assertTrue(resultat);

        verify(venteDAO).enregistrerVente(10, 20, 1, 30, 210.0, 90.0);
    }

    @Test
    void vendreDoitGererMedicamentIntrouvablePourLeCalculCnam() {

        /* Si getInfosVente renvoie null (médicament introuvable côté
           infos), le remboursement doit rester à 0 sans planter. */
        when(medDAO.getStock(1)).thenReturn(100);
        when(medDAO.getInfosVente(1)).thenReturn(null);

        when(venteDAO.enregistrerVente(10, 20, 1, 30, 0.0, 0.0))
                .thenReturn(true);

        when(medDAO.stockCritique(1)).thenReturn(null);

        boolean resultat = venteService.vendre(10, 20, 1, 30);

        assertTrue(resultat);

        verify(venteDAO).enregistrerVente(10, 20, 1, 30, 0.0, 0.0);
    }

    @Test
    void vendreDoitRetournerFalseSiEnregistrementEchoue() {

        when(medDAO.getStock(1)).thenReturn(100);

        when(medDAO.getInfosVente(1)).thenReturn(
                new MedicamentVenteInfo(5.0, false, 0.0)
        );

        when(venteDAO.enregistrerVente(10, 20, 1, 30, 0.0, 150.0))
                .thenReturn(false);

        boolean resultat = venteService.vendre(10, 20, 1, 30);

        assertFalse(resultat);

        verify(venteDAO).enregistrerVente(10, 20, 1, 30, 0.0, 150.0);
        verify(medDAO, never()).updateStock(anyInt(), anyInt());
        verify(histDAO, never()).ajouterHistorique(anyInt(), anyInt());
        verify(medDAO, never()).stockCritique(1);
    }

    @Test
    void vendreDoitVerifierStockCritiqueApresVente() {

        when(medDAO.getStock(1)).thenReturn(100);

        when(medDAO.getInfosVente(1)).thenReturn(
                new MedicamentVenteInfo(5.0, false, 0.0)
        );

        when(venteDAO.enregistrerVente(10, 20, 1, 95, 0.0, 475.0))
                .thenReturn(true);

        when(medDAO.stockCritique(1)).thenReturn("Stock critique");

        boolean resultat = venteService.vendre(10, 20, 1, 95);

        assertTrue(resultat);

        verify(medDAO).stockCritique(1);
    }

    @Test
    void ventesParMedicamentDoitDeleguerAuDAO() {

        List<VenteResponse> ventes = List.of();

        when(venteDAO.ventesParMedicament(1)).thenReturn(ventes);

        List<VenteResponse> resultat = venteService.ventesParMedicament(1);

        assertEquals(ventes, resultat);

        verify(venteDAO).ventesParMedicament(1);
    }

    @Test
    void ventesParClientDoitDeleguerAuDAO() {

        List<VenteResponse> ventes = List.of();

        when(venteDAO.ventesParClient(2)).thenReturn(ventes);

        List<VenteResponse> resultat = venteService.ventesParClient(2);

        assertEquals(ventes, resultat);

        verify(venteDAO).ventesParClient(2);
    }

    @Test
    void ventesParPeriodeDoitDeleguerAuDAO() {

        List<VenteResponse> ventes = List.of();

        when(venteDAO.ventesParPeriode("2026-01-01", "2026-01-31"))
                .thenReturn(ventes);

        List<VenteResponse> resultat =
                venteService.ventesParPeriode("2026-01-01", "2026-01-31");

        assertEquals(ventes, resultat);

        verify(venteDAO).ventesParPeriode("2026-01-01", "2026-01-31");
    }

    @Test
    void annulerVenteDoitDeleguerAuDAO() {

        when(venteDAO.annulerVente(5)).thenReturn(true);

        boolean resultat = venteService.annulerVente(5);

        assertTrue(resultat);

        verify(venteDAO).annulerVente(5);
    }

    @Test
    void annulerVenteDoitRetournerFalseSiDAOEchoue() {

        when(venteDAO.annulerVente(5)).thenReturn(false);

        boolean resultat = venteService.annulerVente(5);

        assertFalse(resultat);

        verify(venteDAO).annulerVente(5);
    }

    @Test
    void vendreDoitDeduireDUnSeulLotSiSuffisant() {

        when(medDAO.getStock(1)).thenReturn(100);

        when(medDAO.getInfosVente(1)).thenReturn(
                new MedicamentVenteInfo(5.0, false, 0.0)
        );

        when(venteDAO.enregistrerVente(10, 20, 1, 30, 0.0, 150.0))
                .thenReturn(true);

        when(medDAO.stockCritique(1)).thenReturn(null);

        when(lotDAO.getLotsDisponibles(1)).thenReturn(
                List.of(new LotResponse(1, 1, 50, "2026-08-10"))
        );

        boolean resultat = venteService.vendre(10, 20, 1, 30);

        assertTrue(resultat);

        verify(lotDAO).mettreAJourQuantiteLot(1, 20);
    }

    @Test
    void vendreDoitDeduireEnCascadeSurPlusieursLots() {

        when(medDAO.getStock(1)).thenReturn(100);

        when(medDAO.getInfosVente(1)).thenReturn(
                new MedicamentVenteInfo(5.0, false, 0.0)
        );

        when(venteDAO.enregistrerVente(10, 20, 1, 30, 0.0, 150.0))
                .thenReturn(true);

        when(medDAO.stockCritique(1)).thenReturn(null);

        when(lotDAO.getLotsDisponibles(1)).thenReturn(
                List.of(
                        new LotResponse(1, 1, 20, "2026-08-10"),
                        new LotResponse(2, 1, 50, "2027-01-01")
                )
        );

        boolean resultat = venteService.vendre(10, 20, 1, 30);

        assertTrue(resultat);

        verify(lotDAO).mettreAJourQuantiteLot(1, 0);
        verify(lotDAO).mettreAJourQuantiteLot(2, 40);
    }

    @Test
    void vendreNeDoitPasEchouerSiAucunLotDisponible() {

        when(medDAO.getStock(1)).thenReturn(100);

        when(medDAO.getInfosVente(1)).thenReturn(
                new MedicamentVenteInfo(5.0, false, 0.0)
        );

        when(venteDAO.enregistrerVente(10, 20, 1, 30, 0.0, 150.0))
                .thenReturn(true);

        when(medDAO.stockCritique(1)).thenReturn(null);

        when(lotDAO.getLotsDisponibles(1)).thenReturn(List.of());

        boolean resultat = venteService.vendre(10, 20, 1, 30);

        assertTrue(resultat);

        verify(lotDAO, never()).mettreAJourQuantiteLot(
                anyInt(), anyInt()
        );
    }
}