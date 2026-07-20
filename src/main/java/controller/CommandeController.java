package controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dto.CommandeRequest;
import dto.MessageResponse;
import service.CommandeService;

@RestController
@RequestMapping("/api/commandes")
public class CommandeController {

    private final CommandeService commandeService;


    public CommandeController(CommandeService commandeService) {
        this.commandeService = commandeService;
    }


    @PostMapping
    public ResponseEntity<MessageResponse> creerCommande(
            @RequestBody CommandeRequest request) {

        boolean creee = commandeService.creerCommande(
                request.idGestionnaire(),
                request.idMedicament(),
                request.quantite()
        );

        if (!creee) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new MessageResponse(
                            "Commande refusée : médicament introuvable"));
        }

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new MessageResponse(
                        "Commande créée, stock mis à jour"));
    }
}
