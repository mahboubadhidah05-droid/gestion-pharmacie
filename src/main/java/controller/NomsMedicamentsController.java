package controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dto.NomMedicamentResponse;
import service.MedicamentService;

/**
 * Expose uniquement les noms/dosages des médicaments, accessible aux
 * deux rôles (contrairement à /api/medicaments, réservé au Gestionnaire).
 * Sert à alimenter les listes de recherche (ex: "Consulter les ventes"
 * côté Pharmacien) sans exposer le stock, le prix ni le seuil critique.
 */
@RestController
@RequestMapping("/api/noms-medicaments")
public class NomsMedicamentsController {

    private final MedicamentService medicamentService;

    public NomsMedicamentsController(MedicamentService medicamentService) {
        this.medicamentService = medicamentService;
    }

    @GetMapping
    public ResponseEntity<List<NomMedicamentResponse>> listerNoms() {
        return ResponseEntity.ok(medicamentService.listerNoms());
    }
}