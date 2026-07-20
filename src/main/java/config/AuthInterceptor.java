package config;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;

import controller.AuthController;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Intercepteur qui protège les API REST :
 * - 401 si aucun utilisateur n'est en session ;
 * - 403 si l'utilisateur n'a pas le rôle requis pour l'URL.
 */
public class AuthInterceptor implements HandlerInterceptor {

    private static final String ROLE_GESTIONNAIRE = "GESTIONNAIRE";
    private static final String ROLE_PHARMACIEN = "PHARMACIEN";

    /**
     * Rôle requis par préfixe d'URL.
     * Les URL absentes de cette table (ex. /api/stock/historique)
     * sont accessibles aux deux rôles.
     */
    private static final Map<String, String> ROLE_PAR_PREFIXE =
            Map.of(
                    "/api/medicaments", ROLE_GESTIONNAIRE,
                    "/api/commandes", ROLE_GESTIONNAIRE,
                    "/api/ventes", ROLE_PHARMACIEN,
                    "/api/clients", ROLE_PHARMACIEN
            );


    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler) {

        HttpSession session = request.getSession(false);

        boolean connecte =
                session != null
                && session.getAttribute(AuthController.ATTR_LOGIN) != null;

        if (!connecte) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return false;
        }

        String role =
                session.getAttribute(AuthController.ATTR_ROLE).toString();

        if (!roleAutorise(request.getRequestURI(), role)) {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            return false;
        }

        return true;
    }


    private boolean roleAutorise(String uri, String role) {

        for (Map.Entry<String, String> regle
                : ROLE_PAR_PREFIXE.entrySet()) {

            if (uri.startsWith(regle.getKey())) {
                return regle.getValue().equals(role);
            }
        }

        return true;
    }
}