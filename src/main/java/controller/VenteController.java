package controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dto.MessageResponse;
import dto.VenteRequest;
import dto.VenteResponse;
import service.VenteService;

@RestController
@RequestMapping("/api/ventes")
public class VenteController {

    private final VenteService venteService;


    public VenteController(VenteService venteService) {
        this.venteService = venteService;
    }


    @PostMapping
    public ResponseEntity<MessageResponse> enregistrerVente(
            @RequestBody VenteRequest request) {

        boolean reussie = venteService.vendre(
                request.idPharmacien(),
                request.idClient(),
                request.idMedicament(),
                request.quantite()
        );

        if (!reussie) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(new MessageResponse(
                            "Vente refusée : stock insuffisant ou erreur d'enregistrement"));
        }

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new MessageResponse("Vente enregistrée avec succès"));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> annulerVente(
            @PathVariable int id) {

        boolean annulee = venteService.annulerVente(id);

        if (!annulee) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(
                new MessageResponse("Vente annulée avec succès")
        );
    }


    @GetMapping(params = "medicament")
    public ResponseEntity<List<VenteResponse>> ventesParMedicament(
            @RequestParam String medicament) {

        return ResponseEntity.ok(
                venteService.ventesParNomMedicament(medicament)
        );
    }


    @GetMapping(params = "client")
    public ResponseEntity<List<VenteResponse>> ventesParClient(
            @RequestParam int client) {

        return ResponseEntity.ok(
                venteService.ventesParClient(client)
        );
    }


    @GetMapping(params = {"clientNom", "clientPrenom"})
    public ResponseEntity<List<VenteResponse>> ventesParNomClient(
            @RequestParam String clientNom,
            @RequestParam String clientPrenom) {

        return ResponseEntity.ok(
                venteService.ventesParNomClient(clientNom, clientPrenom)
        );
    }


    @GetMapping(params = {"debut", "fin"})
    public ResponseEntity<List<VenteResponse>> ventesParPeriode(
            @RequestParam String debut,
            @RequestParam String fin) {

        return ResponseEntity.ok(
                venteService.ventesParPeriode(debut, fin)
        );
    }
}