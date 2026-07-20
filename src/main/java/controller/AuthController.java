package controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dto.LoginRequest;
import dto.LoginResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import service.AuthService;

/**
 * Contrôleur REST d'authentification basé sur la session HTTP.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    /** Valeur renvoyée par AuthService quand les identifiants sont invalides. */
    private static final String ROLE_ECHEC = "ECHEC";

    /** Clés utilisées pour stocker l'utilisateur dans la session. */
    public static final String ATTR_LOGIN = "login";
    public static final String ATTR_ROLE = "role";


    private final AuthService authService;


    public AuthController(AuthService authService) {
        this.authService = authService;
    }


    /**
     * Vérifie les identifiants et ouvre une session HTTP.
     * 200 + {login, role} si OK, 401 sinon.
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @RequestBody LoginRequest requete,
            HttpServletRequest request) {

        String role = authService.login(
                requete.login(),
                requete.pwd()
        );

        if (ROLE_ECHEC.equals(role)) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .build();
        }

        HttpSession session = request.getSession(true);
        session.setAttribute(ATTR_LOGIN, requete.login());
        session.setAttribute(ATTR_ROLE, role);

        return ResponseEntity.ok(
                new LoginResponse(requete.login(), role)
        );
    }


    /**
     * Invalide la session courante. Toujours 204.
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {

        HttpSession session = request.getSession(false);

        if (session != null) {
            session.invalidate();
        }

        return ResponseEntity.noContent().build();
    }


    /**
     * Retourne l'utilisateur connecté (200) ou 401 si aucune session.
     * Servira au frontend pour savoir quel menu afficher.
     */
    @GetMapping("/me")
    public ResponseEntity<LoginResponse> me(HttpServletRequest request) {

        HttpSession session = request.getSession(false);

        if (session == null
                || session.getAttribute(ATTR_LOGIN) == null) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .build();
        }

        return ResponseEntity.ok(
                new LoginResponse(
                        session.getAttribute(ATTR_LOGIN).toString(),
                        session.getAttribute(ATTR_ROLE).toString()
                )
        );
    }
}