package service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dao.MedicamentDAO;
import dao.StockHistoriqueDAO;
import dao.VenteDAO;
import dto.VenteResponse;

class VenteServiceTest {

    private MedicamentDAO medDAO;
    private VenteDAO venteDAO;
    private StockHistoriqueDAO histDAO;
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

        venteService = new VenteService(
                medDAO,
                venteDAO,
                histDAO
        );
    }

    @Test
    void vendreDoitRetournerFalseSiStockInsuffisant() {

        when(
                medDAO.getStock(1)
        ).thenReturn(5);

        boolean resultat =
                venteService.vendre(
                        10,
                        20,
                        1,
                        10
                );

        assertFalse(resultat);

        verify(
                venteDAO,
                never()
        ).enregistrerVente(
                10,
                20,
                1,
                10
        );

        verify(
                medDAO,
                never()
        ).updateStock(
                1,
                -5
        );

        verify(
                histDAO,
                never()
        ).ajouterHistorique(
                1,
                -10
        );
    }

    @Test
    void vendreDoitEnregistrerVenteEtMettreAJourStock() {

        when(
                medDAO.getStock(1)
        ).thenReturn(100);

        when(
                venteDAO.enregistrerVente(
                        10,
                        20,
                        1,
                        30
                )
        ).thenReturn(true);

        when(
                medDAO.stockCritique(1)
        ).thenReturn(null);

        boolean resultat =
                venteService.vendre(
                        10,
                        20,
                        1,
                        30
                );

        assertTrue(resultat);

        verify(
                venteDAO
        ).enregistrerVente(
                10,
                20,
                1,
                30
        );

        verify(
                medDAO
        ).updateStock(
                1,
                70
        );

        verify(
                histDAO
        ).ajouterHistorique(
                1,
                -30
        );

        verify(
                medDAO
        ).stockCritique(1);
    }

    @Test
    void vendreDoitRetournerFalseSiEnregistrementEchoue() {

        when(
                medDAO.getStock(1)
        ).thenReturn(100);

        when(
                venteDAO.enregistrerVente(
                        10,
                        20,
                        1,
                        30
                )
        ).thenReturn(false);

        boolean resultat =
                venteService.vendre(
                        10,
                        20,
                        1,
                        30
                );

        assertFalse(resultat);

        verify(
                venteDAO
        ).enregistrerVente(
                10,
                20,
                1,
                30
        );

        verify(
                medDAO,
                never()
        ).updateStock(
                1,
                70
        );

        verify(
                histDAO,
                never()
        ).ajouterHistorique(
                1,
                -30
        );

        verify(
                medDAO,
                never()
        ).stockCritique(1);
    }

    @Test
    void vendreDoitVerifierStockCritiqueApresVente() {

        when(
                medDAO.getStock(1)
        ).thenReturn(100);

        when(
                venteDAO.enregistrerVente(
                        10,
                        20,
                        1,
                        95
                )
        ).thenReturn(true);

        when(
                medDAO.stockCritique(1)
        ).thenReturn(
                "Stock critique"
        );

        boolean resultat =
                venteService.vendre(
                        10,
                        20,
                        1,
                        95
                );

        assertTrue(resultat);

        verify(
                medDAO
        ).stockCritique(1);
    }

    @Test
    void ventesParMedicamentDoitDeleguerAuDAO() {

        List<VenteResponse> ventes =
                List.of();

        when(
                venteDAO.ventesParMedicament(1)
        ).thenReturn(ventes);

        List<VenteResponse> resultat =
                venteService.ventesParMedicament(1);

        assertEquals(
                ventes,
                resultat
        );

        verify(
                venteDAO
        ).ventesParMedicament(1);
    }

    @Test
    void ventesParClientDoitDeleguerAuDAO() {

        List<VenteResponse> ventes =
                List.of();

        when(
                venteDAO.ventesParClient(2)
        ).thenReturn(ventes);

        List<VenteResponse> resultat =
                venteService.ventesParClient(2);

        assertEquals(
                ventes,
                resultat
        );

        verify(
                venteDAO
        ).ventesParClient(2);
    }

    @Test
    void ventesParPeriodeDoitDeleguerAuDAO() {

        List<VenteResponse> ventes =
                List.of();

        when(
                venteDAO.ventesParPeriode(
                        "2026-01-01",
                        "2026-01-31"
                )
        ).thenReturn(ventes);

        List<VenteResponse> resultat =
                venteService.ventesParPeriode(
                        "2026-01-01",
                        "2026-01-31"
                );

        assertEquals(
                ventes,
                resultat
        );

        verify(
                venteDAO
        ).ventesParPeriode(
                "2026-01-01",
                "2026-01-31"
        );
    }

    @Test
    void annulerVenteDoitDeleguerAuDAO() {

        when(
                venteDAO.annulerVente(5)
        ).thenReturn(true);

        boolean resultat =
                venteService.annulerVente(5);

        assertTrue(resultat);

        verify(
                venteDAO
        ).annulerVente(5);
    }

    @Test
    void annulerVenteDoitRetournerFalseSiDAOEchoue() {

        when(
                venteDAO.annulerVente(5)
        ).thenReturn(false);

        boolean resultat =
                venteService.annulerVente(5);

        assertFalse(resultat);

        verify(
                venteDAO
        ).annulerVente(5);
    }
}