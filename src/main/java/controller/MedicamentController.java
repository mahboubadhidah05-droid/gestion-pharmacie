package controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dto.MedicamentRequest;
import dto.StockUpdateRequest;
import service.MedicamentService;

@RestController
@RequestMapping("/api/medicaments")
public class MedicamentController {

    private final MedicamentService medicamentService;

    public MedicamentController(MedicamentService medicamentService) {
        this.medicamentService = medicamentService;
    }

    @PostMapping
    public ResponseEntity<String> ajouterMedicament(
            @RequestBody MedicamentRequest request) {

        medicamentService.ajouter(
                request.nom(),
                request.dosage(),
                request.stock(),
                request.prix(),
                request.seuil()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("Médicament créé avec succès");
    }

    @GetMapping("/{id}/stock")
    public ResponseEntity<Integer> consulterStock(@PathVariable int id) {
        int stock = medicamentService.getStock(id);
        return ResponseEntity.ok(stock);
    }

    @PutMapping("/{id}/stock")
    public ResponseEntity<String> mettreAJourStock(
            @PathVariable int id,
            @RequestBody StockUpdateRequest request) {

        medicamentService.updateStock(id, request.quantite());
        return ResponseEntity.ok("Stock mis à jour avec succès");
    }

    @GetMapping("/{id}/stock-critique")
    public ResponseEntity<String> verifierStockCritique(@PathVariable int id) {
        String message = medicamentService.stockCritique(id);

        if (message != null) {
            return ResponseEntity.ok(message);
        }
        return ResponseEntity.ok("Stock normal");
    }
}