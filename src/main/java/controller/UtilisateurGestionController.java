package controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dto.MessageResponse;
import dto.UtilisateurCreationRequest;
import dto.UtilisateurGestionResponse;
import dto.UtilisateurModificationRequest;
import service.UtilisateurGestionService;

@RestController
@RequestMapping("/api/comptes")
public class UtilisateurGestionController {

    private final UtilisateurGestionService service;

    public UtilisateurGestionController(UtilisateurGestionService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<UtilisateurGestionResponse>> lister() {
        return ResponseEntity.ok(service.listerTous());
    }

    @PostMapping
    public ResponseEntity<MessageResponse> creer(
            @RequestBody UtilisateurCreationRequest request) {

        int resultat = service.creer(
                request.nom(),
                request.prenom(),
                request.login(),
                request.pwd(),
                request.role()
        );

        if (resultat == -1) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse(
                            "Rôle invalide : doit être PHARMACIEN ou GESTIONNAIRE"));
        }

        if (resultat == -2) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse(
                            "Mot de passe trop faible : 8 caractères minimum, "
                                    + "avec au moins une lettre et un chiffre"));
        }

        if (resultat == -3) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(new MessageResponse(
                            "Ce login est déjà utilisé"));
        }

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new MessageResponse(
                        "Utilisateur créé avec succès (ID : " + resultat + ")"));
    }

    @PutMapping("/{role}/{id}")
    public ResponseEntity<MessageResponse> modifier(
            @PathVariable String role,
            @PathVariable int id,
            @RequestBody UtilisateurModificationRequest request) {

        int resultat = service.modifier(
                id,
                role,
                request.nom(),
                request.prenom(),
                request.login(),
                request.pwd()
        );

        if (resultat == -1) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse(
                            "Rôle invalide : doit être PHARMACIEN ou GESTIONNAIRE"));
        }

        if (resultat == -2) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse(
                            "Mot de passe trop faible : 8 caractères minimum, "
                                    + "avec au moins une lettre et un chiffre"));
        }

        if (resultat == -4) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(
                new MessageResponse("Utilisateur modifié avec succès")
        );
    }

    @DeleteMapping("/{role}/{id}")
    public ResponseEntity<MessageResponse> supprimer(
            @PathVariable String role,
            @PathVariable int id) {

        boolean supprime = service.supprimer(id, role);

        if (!supprime) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(
                new MessageResponse("Utilisateur supprimé avec succès")
        );
    }
}