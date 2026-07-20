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
import dto.MessageResponse;
import dto.StockCritiqueResponse;
import dto.StockResponse;
import dto.StockUpdateRequest;
import service.MedicamentService;
import java.util.List;

import dto.MedicamentResponse;
@RestController
@RequestMapping("/api/medicaments")
public class MedicamentController {

    /** Valeur renvoyée par le DAO quand le médicament n'existe pas. */
    private static final int STOCK_INTROUVABLE = -1;


    private final MedicamentService medicamentService;


    public MedicamentController(MedicamentService medicamentService) {
        this.medicamentService = medicamentService;
    }


    @PostMapping
    public ResponseEntity<MessageResponse> ajouterMedicament(
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
                .body(new MessageResponse("Médicament créé avec succès"));
    }


    @GetMapping("/{id}/stock")
    public ResponseEntity<StockResponse> consulterStock(
            @PathVariable int id) {

        int stock = medicamentService.getStock(id);

        if (stock == STOCK_INTROUVABLE) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(
                new StockResponse(id, stock)
        );
    }


    @PutMapping("/{id}/stock")
    public ResponseEntity<MessageResponse> mettreAJourStock(
            @PathVariable int id,
            @RequestBody StockUpdateRequest request) {

        medicamentService.updateStock(id, request.quantite());

        return ResponseEntity.ok(
                new MessageResponse("Stock mis à jour avec succès")
        );
    }

    @GetMapping
    public ResponseEntity<List<MedicamentResponse>> listerMedicaments() {

        return ResponseEntity.ok(
                medicamentService.listerMedicaments()
        );
    }
    @GetMapping("/{id}/stock-critique")
    public ResponseEntity<StockCritiqueResponse> verifierStockCritique(
            @PathVariable int id) {

        String message = medicamentService.stockCritique(id);

        if (message != null) {
            return ResponseEntity.ok(
                    new StockCritiqueResponse(true, message)
            );
        }

        return ResponseEntity.ok(
                new StockCritiqueResponse(false, "Stock normal")
        );
    }
}