package service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.util.List;

import org.junit.jupiter.api.Test;

class NotificationServiceTest {

    @Test
    void neDoitPasLeverExceptionAvecListeNull() {

        assertDoesNotThrow(
                () -> NotificationService.envoyerEmail(null)
        );
    }

    @Test
    void neDoitPasLeverExceptionAvecListeVide() {

        assertDoesNotThrow(
                () -> NotificationService.envoyerEmail(
                        List.of()
                )
        );
    }

    @Test
    void doitEnvoyerNotificationsPourMedicamentsCritiques() {

        List<String> medicamentsCritiques =
                List.of(
                        "Paracétamol",
                        "Amoxicilline"
                );

        assertDoesNotThrow(
                () -> NotificationService.envoyerEmail(
                        medicamentsCritiques
                )
        );
    }

    @Test
    void doitNotifierUnMedicamentCritique() {

        assertDoesNotThrow(
                () -> NotificationService.notifierStockCritique(
                        "Paracétamol"
                )
        );
    }
}