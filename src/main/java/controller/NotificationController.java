package controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import config.StockAlertScheduler;
import dao.NotificationDAO;
import dto.MessageResponse;
import dto.NotificationResponse;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationDAO notificationDAO;
    private final StockAlertScheduler stockAlertScheduler;

    public NotificationController(
            NotificationDAO notificationDAO,
            StockAlertScheduler stockAlertScheduler) {
        this.notificationDAO = notificationDAO;
        this.stockAlertScheduler = stockAlertScheduler;
    }

    @GetMapping
    public ResponseEntity<List<NotificationResponse>> lister() {
        return ResponseEntity.ok(notificationDAO.lister());
    }

    /**
     * Déclenche immédiatement la vérification des stocks critiques
     * et l'envoi du mail récapitulatif, sans attendre l'exécution
     * automatique quotidienne (8h) — utile pour tester/démontrer
     * la fonctionnalité.
     */
    @PostMapping("/verifier-stocks")
    public ResponseEntity<MessageResponse> declencherVerificationManuelle() {

        int nombreCritiques =
                stockAlertScheduler.executerVerification();

        return ResponseEntity.ok(
                new MessageResponse(
                        "Vérification effectuée : "
                                + nombreCritiques
                                + " médicament(s) en stock critique."
                )
        );
    }
}