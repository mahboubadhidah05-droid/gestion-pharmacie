package service;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class NotificationService {

    private static final Logger LOGGER =
            Logger.getLogger(NotificationService.class.getName());

    private static final String MESSAGE_VIDE =
            "Aucun stock critique à signaler.";

    private static final String TITRE_DEBUT =
            "=== ENVOI DE NOTIFICATIONS ===";

    private static final String TITRE_FIN =
            "=== FIN DES NOTIFICATIONS ===";


    private NotificationService() {
        // Classe utilitaire : empêche l'instanciation
    }


    public static void envoyerEmail(
            List<String> medicamentsCritiques) {


        if (medicamentsCritiques == null
                || medicamentsCritiques.isEmpty()) {

            LOGGER.info(MESSAGE_VIDE);
            return;
        }


        LOGGER.info(TITRE_DEBUT);


        medicamentsCritiques.forEach(
                NotificationService::envoyerNotification
        );


        LOGGER.info(TITRE_FIN);
    }


    private static void envoyerNotification(
            String medicament) {


        LOGGER.log(
                Level.WARNING,
                "Notification : {0}",
                medicament
        );
    }


    public static void notifierStockCritique(
            String medicament) {

        envoyerEmail(
                List.of(medicament)
        );
    }
}