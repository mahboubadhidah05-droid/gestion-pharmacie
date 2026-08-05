package controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dto.MessageResponse;
import service.AlerteStockService;

/**
 * Permet de déclencher manuellement l'email d'alerte de stock critique
 * (normalement envoyé automatiquement chaque jour à 8h) — utile pour
 * démontrer la fonctionnalité sans attendre l'horaire réel.
 */
@RestController
@RequestMapping("/api/alertes")
public class AlerteStockController {

    private final AlerteStockService alerteStockService;

    public AlerteStockController(AlerteStockService alerteStockService) {
        this.alerteStockService = alerteStockService;
    }

    @PostMapping("/tester-email")
    public ResponseEntity<MessageResponse> testerEmail() {

        int nombreCritiques = alerteStockService.verifierEtEnvoyer();

        if (nombreCritiques == 0) {

            return ResponseEntity.ok(
                    new MessageResponse(
                            "Aucun médicament en stock critique actuellement "
                                    + "— aucun email envoyé."
                    )
            );
        }

        return ResponseEntity.ok(
                new MessageResponse(
                        "Email envoyé pour " + nombreCritiques
                                + " médicament(s) en stock critique."
                )
        );
    }
}