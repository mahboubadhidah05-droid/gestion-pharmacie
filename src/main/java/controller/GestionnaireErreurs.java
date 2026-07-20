package controller;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import dto.MessageResponse;
import exception.AccesDonneesException;

/**
 * Transforme les exceptions techniques en réponses HTTP propres,
 * sans exposer les détails internes (SQL, stack trace) au client.
 */
@RestControllerAdvice
public class GestionnaireErreurs {

    private static final Logger LOGGER =
            Logger.getLogger(GestionnaireErreurs.class.getName());

    private static final String MESSAGE_ERREUR_DONNEES =
            "Erreur d'accès aux données";

    private static final String MESSAGE_REPONSE_CLIENT =
            "Erreur interne : opération impossible sur la base de données";

    @ExceptionHandler(AccesDonneesException.class)
    public ResponseEntity<MessageResponse> gererAccesDonnees(
            AccesDonneesException exception) {

        LOGGER.log(
                Level.SEVERE,
                MESSAGE_ERREUR_DONNEES,
                exception
        );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new MessageResponse(MESSAGE_REPONSE_CLIENT));
    }
}