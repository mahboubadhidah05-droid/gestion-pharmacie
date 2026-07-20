package controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dto.ClientRequest;
import dto.ClientResponse;
import dto.ExistenceResponse;
import service.ClientService;

@RestController
@RequestMapping("/api/clients")
public class ClientController {

    /** Valeur renvoyée par le DAO quand la création a échoué. */
    private static final int ID_INVALIDE = -1;


    private final ClientService clientService;


    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }


    @PostMapping
    public ResponseEntity<ClientResponse> creerClient(
            @RequestBody ClientRequest request) {

        int id = clientService.creerClient(
                request.nom(),
                request.prenom(),
                request.email(),
                request.adresse()
        );

        if (id == ID_INVALIDE) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ClientResponse(
                            ID_INVALIDE,
                            "Erreur lors de la création du client"));
        }

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ClientResponse(
                        id,
                        "Client créé avec succès"));
    }


    @GetMapping("/{id}/existe")
    public ResponseEntity<ExistenceResponse> existeClient(
            @PathVariable int id) {

        return ResponseEntity.ok(
                new ExistenceResponse(
                        clientService.existeClient(id))
        );
    }
}