package service;

import java.util.List;
import java.util.logging.Logger;

public final class NotificationService {

    private static final Logger LOGGER =
            Logger.getLogger(NotificationService.class.getName());


    private NotificationService() {
        // Classe utilitaire : empêche l'instanciation
    }


    public static void envoyerEmail(List<String> medicamentsCritiques) {

        if (medicamentsCritiques == null || medicamentsCritiques.isEmpty()) {

            LOGGER.info("Aucun stock critique à signaler.");
            return;
        }


        LOGGER.info("=== ENVOI DE NOTIFICATIONS ===");

        for (String med : medicamentsCritiques) {

            LOGGER.warning("Notification : " + med);
        }


        LOGGER.info("=== FIN DES NOTIFICATIONS ===");
    }


    public static void notifierStockCritique(String medicament) {

        envoyerEmail(List.of(medicament));
    }
}