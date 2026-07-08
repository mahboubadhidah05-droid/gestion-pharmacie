package service;

import First_project.MedicamentDAO;
import First_project.StockHistoriqueDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.MockedStatic;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MedicamentServiceTest {

    @Mock
    private MedicamentDAO medicamentDAO;

    @Mock
    private StockHistoriqueDAO stockHistoriqueDAO;

    private MedicamentService medicamentService;

    @BeforeEach
    void setUp() {
        medicamentService = new MedicamentService(medicamentDAO, stockHistoriqueDAO);
    }

    @Test
    void ajouter_doitInsererLeMedicamentEtEnregistrerUnHistorique() {
        when(medicamentDAO.getIdMedicamentParNomEtDosage("Doliprane", "500mg")).thenReturn(12);

        medicamentService.ajouter("Doliprane", "500mg", 100, 3.5, 10);

        verify(medicamentDAO).ajouterMedicament("Doliprane", "500mg", 100, 3.5, 10);
        verify(stockHistoriqueDAO).ajouterHistorique(12, 100);
    }

    @Test
    void ajouter_medicamentIntrouvableApresInsertion_doitEnregistrerHistoriqueAvecIdInvalide() {
        when(medicamentDAO.getIdMedicamentParNomEtDosage("Inconnu", "0mg")).thenReturn(-1);

        medicamentService.ajouter("Inconnu", "0mg", 5, 1.0, 1);

        verify(stockHistoriqueDAO).ajouterHistorique(-1, 5);
    }

    @Test
    void updateStock_doitAppelerLeDao() {
        medicamentService.updateStock(3, 20);

        verify(medicamentDAO).updateStock(3, 20);
    }

    @Test
    void stockCritique_quandCritique_doitDeclencherUneNotification() {
        when(medicamentDAO.stockCritique(7)).thenReturn("Médicament en stock critique : Doliprane (ID 7) | Stock actuel = 2");

        try (MockedStatic<NotificationService> mockedNotif = mockStatic(NotificationService.class)) {
            medicamentService.stockCritique(7);

            mockedNotif.verify(() -> NotificationService.notifierStockCritique(
                    "Médicament en stock critique : Doliprane (ID 7) | Stock actuel = 2"));
        }
    }

    @Test
    void stockCritique_quandNonCritique_neDoitPasNotifier() {
        when(medicamentDAO.stockCritique(8)).thenReturn(null);

        try (MockedStatic<NotificationService> mockedNotif = mockStatic(NotificationService.class)) {
            medicamentService.stockCritique(8);

            mockedNotif.verifyNoInteractions();
        }
    }
}