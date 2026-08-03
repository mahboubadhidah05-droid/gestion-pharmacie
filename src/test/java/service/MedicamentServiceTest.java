package service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import dto.LotResponse;
import dto.MedicamentResponse;
import dao.MedicamentDAO;
import dao.StockHistoriqueDAO;
import dao.LotMedicamentDAO;
import utils.DBConnection;

import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
class MedicamentServiceTest {

    @Mock
    private MedicamentDAO dao;

    @Mock
    private StockHistoriqueDAO histDAO;

    @Mock
    private LotMedicamentDAO lotDAO;

    private MedicamentService service;

    @BeforeEach
    void setUp() {
        service =
                new MedicamentService(
                        dao,
                        histDAO,
                        lotDAO
                );
    }

    @Test
    void doitAjouterMedicamentEtHistoriqueSiNouveau() {

        String nom = "Paracetamol";
        String dosage = "500mg";
        int stock = 100;
        double prix = 5.5;
        int seuil = 10;
        String datePeremption = "2026-12-31";
        boolean conventionneCnam = true;
        double tauxRemboursement = 0.7;
        int idMedicament = 1;

        when(
                dao.getIdMedicamentParNomEtDosage(
                        nom,
                        dosage
                )
        ).thenReturn(-1, idMedicament);

        service.ajouter(
                nom,
                dosage,
                stock,
                prix,
                seuil,
                datePeremption,
                conventionneCnam,
                tauxRemboursement
        );

        verify(dao).ajouterMedicament(
                nom,
                dosage,
                stock,
                prix,
                seuil,
                datePeremption,
                conventionneCnam,
                tauxRemboursement
        );

        verify(histDAO).ajouterHistorique(
                idMedicament,
                stock
        );

        verify(lotDAO).ajouterLot(
                idMedicament,
                stock,
                datePeremption
        );
    }

    @Test
    void doitAugmenterLeStockSiMedicamentExisteDeja() {

        String nom = "Paracetamol";
        String dosage = "500mg";
        int stockAjoute = 50;
        double prix = 5.5;
        int seuil = 10;
        String datePeremption = "2026-12-31";
        int idExistant = 1;
        int stockActuel = 100;

        when(
                dao.getIdMedicamentParNomEtDosage(
                        nom,
                        dosage
                )
        ).thenReturn(idExistant);

        when(
                dao.getStock(idExistant)
        ).thenReturn(stockActuel);

        service.ajouter(
                nom,
                dosage,
                stockAjoute,
                prix,
                seuil,
                datePeremption,
                false,
                0.0
        );

        verify(dao, never()).ajouterMedicament(
                any(),
                any(),
                anyInt(),
                anyDouble(),
                anyInt(),
                any(),
                org.mockito.ArgumentMatchers.anyBoolean(),
                anyDouble()
        );

        verify(dao).updateStock(
                idExistant,
                stockActuel + stockAjoute
        );

        verify(histDAO).ajouterHistorique(
                idExistant,
                stockAjoute
        );

        verify(lotDAO).ajouterLot(
                idExistant,
                stockAjoute,
                datePeremption
        );
    }

    @Test
    void doitMettreAJourLeStock() {

        int id = 1;
        int quantite = 50;

        service.updateStock(
                id,
                quantite
        );

        verify(dao).updateStock(
                id,
                quantite
        );
    }

    @Test
    void doitListerLesMedicamentsAvecListeVide() {

        List<MedicamentResponse> medicaments =
                List.of();

        when(
                dao.listerMedicaments()
        ).thenReturn(medicaments);

        List<MedicamentResponse> resultat =
                service.listerMedicaments();

        assertEquals(
                medicaments,
                resultat
        );

        verify(dao).listerMedicaments();
    }

    @Test
    void doitRemplacerLaDateParLaPlusProcheDuLotEtGarderCnam() {

        MedicamentResponse ancien = new MedicamentResponse(
                4, "antafen", "100mg", 160, 8.0, 20,
                "2026-02-24", 0, true, 0.7
        );

        when(dao.listerMedicaments()).thenReturn(List.of(ancien));

        when(lotDAO.getDatePeremptionLaPlusProche(4))
                .thenReturn("2027-02-24");

        when(lotDAO.getQuantitePerimee(
                org.mockito.ArgumentMatchers.eq(4), anyString()
        )).thenReturn(0);

        List<MedicamentResponse> resultat =
                service.listerMedicaments();

        assertEquals(1, resultat.size());
        assertEquals("2027-02-24", resultat.get(0).datePeremption());
        assertEquals("antafen", resultat.get(0).nom());
        assertEquals(true, resultat.get(0).conventionneCnam());
        assertEquals(0.7, resultat.get(0).tauxRemboursement());
    }

    @Test
    void doitCalculerLaQuantitePerimeePartielle() {

        MedicamentResponse ancien = new MedicamentResponse(
                4, "antafen", "100mg", 160, 8.0, 20,
                "2026-02-24", 0, false, 0.0
        );

        when(dao.listerMedicaments()).thenReturn(List.of(ancien));

        when(lotDAO.getDatePeremptionLaPlusProche(4))
                .thenReturn("2027-02-24");

        when(lotDAO.getQuantitePerimee(
                org.mockito.ArgumentMatchers.eq(4), anyString()
        )).thenReturn(20);

        List<MedicamentResponse> resultat =
                service.listerMedicaments();

        assertEquals(20, resultat.get(0).quantitePerimee());
        assertEquals(160, resultat.get(0).stock());
    }

    @Test
    void doitRetournerLeStock() {

        int id = 1;
        int stock = 100;

        when(
                dao.getStock(id)
        ).thenReturn(stock);

        int resultat =
                service.getStock(id);

        assertEquals(
                stock,
                resultat
        );

        verify(dao).getStock(id);
    }

    @Test
    void doitRetournerLesInfosVente() {

        dto.MedicamentVenteInfo infos =
                new dto.MedicamentVenteInfo(5.5, true, 0.7);

        when(dao.getInfosVente(1)).thenReturn(infos);

        assertEquals(infos, service.getInfosVente(1));
    }

    @Test
    void doitNotifierSiStockCritique() throws Exception {

        int idMedicament = 1;

        String message =
                "Médicament en stock critique";

        when(
                dao.stockCritique(idMedicament)
        ).thenReturn(message);

        Connection connection =
                mock(Connection.class);

        PreparedStatement statement =
                mock(PreparedStatement.class);

        when(
                connection.prepareStatement(anyString())
        ).thenReturn(statement);

        try (MockedStatic<DBConnection> dbConnection =
                     mockStatic(DBConnection.class)) {

            dbConnection.when(
                    DBConnection::getConnection
            ).thenReturn(connection);

            String resultat =
                    service.stockCritique(idMedicament);

            assertEquals(
                    message,
                    resultat
            );
        }

        verify(dao).stockCritique(
                idMedicament
        );
    }

    @Test
    void neDoitPasNotifierSiStockNonCritique() {

        int idMedicament = 1;

        when(
                dao.stockCritique(idMedicament)
        ).thenReturn(null);

        String resultat =
                service.stockCritique(idMedicament);

        assertNull(resultat);

        verify(dao).stockCritique(
                idMedicament
        );

        verifyNoInteractions(histDAO);
    }

    @Test
    void doitListerLesNoms() {

        List<dto.NomMedicamentResponse> noms =
                List.of(new dto.NomMedicamentResponse("Paracetamol", "500mg"));

        when(dao.listerNoms()).thenReturn(noms);

        assertEquals(noms, service.listerNoms());
    }

    @Test
    void retirerStockPerime_medicamentIntrouvable_doitRetournerMoinsUn() {

        when(dao.getIdMedicamentParNomEtDosage("Inconnu", "1g"))
                .thenReturn(-1);

        int resultat =
                service.retirerStockPerime("Inconnu", "1g");

        assertEquals(-1, resultat);
    }

    @Test
    void retirerStockPerime_aucunLotPerime_doitRetournerMoinsDeux() {

        when(dao.getIdMedicamentParNomEtDosage("Paracetamol", "500mg"))
                .thenReturn(1);

        when(lotDAO.getLotsExpires(org.mockito.ArgumentMatchers.eq(1), anyString()))
                .thenReturn(List.of());

        int resultat =
                service.retirerStockPerime("Paracetamol", "500mg");

        assertEquals(-2, resultat);

        verify(dao, never()).updateStock(anyInt(), anyInt());
    }

    @Test
    void retirerStockPerime_neTouchePasLesLotsEncoreValides() {

        when(dao.getIdMedicamentParNomEtDosage("Paracetamol", "500mg"))
                .thenReturn(1);

        when(lotDAO.getLotsExpires(org.mockito.ArgumentMatchers.eq(1), anyString()))
                .thenReturn(List.of(
                        new LotResponse(10, 1, 20, "2020-01-01")
                ));

        when(dao.getStock(1)).thenReturn(100);

        int resultat =
                service.retirerStockPerime("Paracetamol", "500mg");

        assertEquals(20, resultat);

        verify(lotDAO).mettreAJourQuantiteLot(10, 0);

        verify(dao).updateStock(1, 80);
        verify(histDAO).ajouterHistorique(1, -20);
    }

    @Test
    void retirerStockPerime_plusieursLotsPerimes_doitToutRetirer() {

        when(dao.getIdMedicamentParNomEtDosage("Paracetamol", "500mg"))
                .thenReturn(1);

        when(lotDAO.getLotsExpires(org.mockito.ArgumentMatchers.eq(1), anyString()))
                .thenReturn(List.of(
                        new LotResponse(10, 1, 15, "2020-01-01"),
                        new LotResponse(11, 1, 5, "2021-06-15")
                ));

        when(dao.getStock(1)).thenReturn(50);

        int resultat =
                service.retirerStockPerime("Paracetamol", "500mg");

        assertEquals(20, resultat);

        verify(lotDAO).mettreAJourQuantiteLot(10, 0);
        verify(lotDAO).mettreAJourQuantiteLot(11, 0);
        verify(dao).updateStock(1, 30);
        verify(histDAO).ajouterHistorique(1, -20);
    }

    @Test
    void verifier_medicamentIntrouvable_doitRetournerNull() {

        when(dao.getIdMedicamentParNomEtDosage("Inconnu", "1g"))
                .thenReturn(-1);

        assertEquals(null, service.verifier("Inconnu", "1g"));
    }

    @Test
    void verifier_stockNormalEtRienDePerime() {

        when(dao.getIdMedicamentParNomEtDosage("Ibuprofene", "400mg"))
                .thenReturn(2);

        when(dao.getStock(2)).thenReturn(50);
        when(dao.stockCritique(2)).thenReturn(null);
        when(lotDAO.getQuantitePerimee(anyInt(), anyString())).thenReturn(0);
        when(lotDAO.getDatePeremptionLaPlusProche(2)).thenReturn("2028-01-01");

        dto.VerifierMedicamentResponse resultat =
                service.verifier("Ibuprofene", "400mg");

        assertEquals(50, resultat.stock());
        assertEquals(false, resultat.stockCritique());
        assertEquals(0, resultat.quantitePerimee());
        assertEquals(false, resultat.bientotPerime());
    }

    @Test
    void verifier_stockCritiqueEtPerime() {

        when(dao.getIdMedicamentParNomEtDosage("antafen", "100mg"))
                .thenReturn(4);

        when(dao.getStock(4)).thenReturn(5);
        when(dao.stockCritique(4)).thenReturn("Stock critique");
        when(lotDAO.getQuantitePerimee(anyInt(), anyString())).thenReturn(20);
        when(lotDAO.getDatePeremptionLaPlusProche(4)).thenReturn("2020-01-01");

        dto.VerifierMedicamentResponse resultat =
                service.verifier("antafen", "100mg");

        assertEquals(true, resultat.stockCritique());
        assertEquals(20, resultat.quantitePerimee());
        assertEquals(false, resultat.bientotPerime());
    }

    @Test
    void verifier_bientotPerimeSiDansLes30Jours() {

        when(dao.getIdMedicamentParNomEtDosage("Paracetamol", "500mg"))
                .thenReturn(1);

        when(dao.getStock(1)).thenReturn(20);
        when(dao.stockCritique(1)).thenReturn(null);
        when(lotDAO.getQuantitePerimee(anyInt(), anyString())).thenReturn(0);

        String dansDixJours =
                java.time.LocalDate.now().plusDays(10).toString();

        when(lotDAO.getDatePeremptionLaPlusProche(1))
                .thenReturn(dansDixJours);

        dto.VerifierMedicamentResponse resultat =
                service.verifier("Paracetamol", "500mg");

        assertEquals(true, resultat.bientotPerime());
    }
}