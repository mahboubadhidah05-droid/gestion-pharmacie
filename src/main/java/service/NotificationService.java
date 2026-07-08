package service;

import java.util.List;

public class NotificationService {

    public static void envoyerEmail(List<String> medicamentsCritiques) {
        if (medicamentsCritiques == null || medicamentsCritiques.isEmpty()) {
            System.out.println("Aucun stock critique à signaler.");
            return;
        }
        System.out.println("=== ENVOI DE NOTIFICATIONS ===");
        for (String med : medicamentsCritiques) {
            System.out.println("⚠️ Notification : " + med);
        }
        System.out.println("=== FIN DES NOTIFICATIONS ===");
    }

    public static void notifierStockCritique(String medicament) {
        envoyerEmail(List.of(medicament));
    }
}