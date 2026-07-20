package service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import dto.MedicamentResponse;
import dao.MedicamentDAO;
import dao.StockHistoriqueDAO;

import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
class MedicamentServiceTest {

    @Mock
    private MedicamentDAO dao;

    @Mock
    private StockHistoriqueDAO histDAO;

    private MedicamentService service;

    @BeforeEach
    void setUp() {
        service =
                new MedicamentService(
                        dao,
                        histDAO
                );
    }

    @Test
    void doitAjouterMedicamentEtHistorique() {

        String nom = "Paracetamol";
        String dosage = "500mg";
        int stock = 100;
        double prix = 5.5;
        int seuil = 10;
        int idMedicament = 1;

        when(
                dao.getIdMedicamentParNomEtDosage(
                        nom,
                        dosage
                )
        ).thenReturn(idMedicament);

        service.ajouter(
                nom,
                dosage,
                stock,
                prix,
                seuil
        );

        verify(dao).ajouterMedicament(
                nom,
                dosage,
                stock,
                prix,
                seuil
        );

        verify(dao).getIdMedicamentParNomEtDosage(
                nom,
                dosage
        );

        verify(histDAO).ajouterHistorique(
                idMedicament,
                stock
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
    void doitListerLesMedicaments() {

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
    void doitNotifierSiStockCritique() {

        int idMedicament = 1;

        String message =
                "Médicament en stock critique";

        when(
                dao.stockCritique(idMedicament)
        ).thenReturn(message);

        String resultat =
                service.stockCritique(idMedicament);

        assertEquals(
                message,
                resultat
        );

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
}