package controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dao.UtilisateurDAO;
import dao.UtilisateurGestionDAO;
import dto.ChangerMotDePasseRequest;
import dto.MessageResponse;
import dto.ModifierInfosRequest;
import dto.MonProfilResponse;
import jakarta.servlet.http.HttpSession;
import utils.MotDePasseUtils;

/**
 * Actions en libre-service sur son propre compte
 * (accessible aux deux rôles, chacun n'agit que sur son propre compte,
 * identifié via la session — jamais via un ID fourni par le client).
 */
@RestController
@RequestMapping("/api/mon-compte")
public class MonCompteController {

    private static final String ROLE_ECHEC = "ECHEC";

    private final UtilisateurDAO utilisateurDAO;
    private final UtilisateurGestionDAO utilisateurGestionDAO;

    public MonCompteController(
            UtilisateurDAO utilisateurDAO,
            UtilisateurGestionDAO utilisateurGestionDAO) {

        this.utilisateurDAO = utilisateurDAO;
        this.utilisateurGestionDAO = utilisateurGestionDAO;
    }

    @GetMapping
    public ResponseEntity<MonProfilResponse> monProfil(HttpSession session) {

        String login =
                (String) session.getAttribute(AuthController.ATTR_LOGIN);

        String role =
                (String) session.getAttribute(AuthController.ATTR_ROLE);

        MonProfilResponse profil =
                utilisateurGestionDAO.getMonProfil(login, role);

        if (profil == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(profil);
    }

    @PutMapping("/infos")
    public ResponseEntity<MessageResponse> modifierMesInfos(
            @RequestBody ModifierInfosRequest request,
            HttpSession session) {

        String login =
                (String) session.getAttribute(AuthController.ATTR_LOGIN);

        String role =
                (String) session.getAttribute(AuthController.ATTR_ROLE);

        boolean modifie =
                utilisateurGestionDAO.modifierInfosParLogin(
                        login,
                        role,
                        request.nom(),
                        request.prenom(),
                        request.email()
                );

        if (!modifie) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(
                new MessageResponse("Informations mises à jour avec succès")
        );
    }

    @PutMapping("/mot-de-passe")
    public ResponseEntity<MessageResponse> changerMotDePasse(
            @RequestBody ChangerMotDePasseRequest request,
            HttpSession session) {

        String login =
                (String) session.getAttribute(AuthController.ATTR_LOGIN);

        String role =
                (String) session.getAttribute(AuthController.ATTR_ROLE);

        String roleVerifie =
                utilisateurDAO.getRole(login, request.pwdActuel());

        if (ROLE_ECHEC.equals(roleVerifie) || !role.equals(roleVerifie)) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageResponse(
                            "Mot de passe actuel incorrect"));
        }

        if (!MotDePasseUtils.respecteReglesBasiques(request.pwdNouveau())) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse(
                            "Nouveau mot de passe trop faible : 8 caractères "
                                    + "minimum, avec au moins une lettre "
                                    + "et un chiffre"));
        }

        String pwdHache =
                MotDePasseUtils.hacher(request.pwdNouveau());

        utilisateurGestionDAO.changerMotDePasseParLogin(
                login,
                role,
                pwdHache
        );

        return ResponseEntity.ok(
                new MessageResponse("Mot de passe modifié avec succès")
        );
    }
}