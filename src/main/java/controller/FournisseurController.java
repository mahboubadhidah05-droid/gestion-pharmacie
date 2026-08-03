package controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dto.FournisseurCreeResponse;
import dto.FournisseurRequest;
import dto.FournisseurResponse;
import service.FournisseurService;

@RestController
@RequestMapping("/api/fournisseurs")
public class FournisseurController {

    private final FournisseurService fournisseurService;

    public FournisseurController(FournisseurService fournisseurService) {
        this.fournisseurService = fournisseurService;
    }

    @PostMapping
    public ResponseEntity<FournisseurCreeResponse> ajouterFournisseur(
            @RequestBody FournisseurRequest request) {

        int id = fournisseurService.ajouter(
                request.nom(),
                request.telephone(),
                request.email(),
                request.adresse()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new FournisseurCreeResponse(
                        "Fournisseur créé avec succès",
                        id
                ));
    }

    @GetMapping
    public ResponseEntity<List<FournisseurResponse>> listerFournisseurs() {

        return ResponseEntity.ok(
                fournisseurService.listerFournisseurs()
        );
    }
}