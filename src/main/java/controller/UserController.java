package controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dto.ProfilResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import service.UserService;

@RestController
@RequestMapping("/api/utilisateurs")
public class UserController {

    private final UserService userService;


    public UserController(UserService userService) {
        this.userService = userService;
    }


    /**
     * Profil de l'utilisateur connecté, identifié par sa session.
     */
    @GetMapping("/profil")
    public ResponseEntity<ProfilResponse> profil(
            HttpServletRequest request) {

        HttpSession session = request.getSession(false);

        /* L'interceptor garantit une session, mais on reste défensif. */
        if (session == null
                || session.getAttribute(AuthController.ATTR_LOGIN) == null) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .build();
        }

        String login =
                session.getAttribute(AuthController.ATTR_LOGIN).toString();

        String role =
                session.getAttribute(AuthController.ATTR_ROLE).toString();

        String[] profil = userService.getProfil(login);

        if (profil.length < 2) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(
                new ProfilResponse(
                        login,
                        profil[0],
                        profil[1],
                        role)
        );
    }
}