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
    private static final String VENTES_PREFIXE = "/api/ventes";
    private static final String METHODE_GET = "GET";
    /**
     * Rôle requis par préfixe d'URL.
     * Les URL absentes de cette table (ex. /api/stock/historique)
     * sont accessibles aux deux rôles.
     */
    private static final Map<String, String> ROLE_PAR_PREFIXE =
            Map.of(
                    "/api/medicaments", ROLE_GESTIONNAIRE,
                    "/api/commandes", ROLE_GESTIONNAIRE,
                    "/api/fournisseurs", ROLE_GESTIONNAIRE,
                    "/api/comptes", ROLE_GESTIONNAIRE,
                    "/api/cnam", ROLE_GESTIONNAIRE,
                    "/api/alertes", ROLE_GESTIONNAIRE,
                    VENTES_PREFIXE, ROLE_PHARMACIEN,
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
        if (!roleAutorise(
                request.getRequestURI(),
                request.getMethod(),
                role)) {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            return false;
        }
        return true;
    }
    private static final String CODE_BARRE_PREFIXE =
            "/api/medicaments/code-barre/";

    private static final String RECHERCHE_PREFIXE =
            "/api/medicaments/recherche";

    private static final String STOCK_SUFFIXE =
            "/stock";

    private boolean roleAutorise(String uri, String methode, String role) {

        /*
         * Lecture des ventes (GET) ouverte aux deux rôles : le
         * Gestionnaire doit pouvoir consulter les rapports de ventes,
         * même si créer/annuler une vente reste réservé au Pharmacien.
         */
        if (uri.startsWith(VENTES_PREFIXE)
                && METHODE_GET.equalsIgnoreCase(methode)) {
            return true;
        }

        /*
         * Recherche d'un médicament par code-barres ouverte aux deux
         * rôles : le Pharmacien en a besoin pour scanner un produit
         * au moment d'enregistrer une vente, même si la gestion
         * complète des médicaments reste réservée au Gestionnaire.
         */
        if (uri.startsWith(CODE_BARRE_PREFIXE)) {
            return true;
        }

        /*
         * Recherche par autocomplétion (nom + dosage) ouverte aux deux
         * rôles pour la même raison : alternative au scan pendant une
         * vente, utilisable même si le médicament n'a pas de
         * code-barres enregistré.
         */
        if (uri.startsWith(RECHERCHE_PREFIXE)) {
            return true;
        }

        /*
         * Consultation du stock (lecture seule) ouverte aux deux
         * rôles : le Pharmacien doit pouvoir vérifier combien il
         * reste d'un médicament, même si la gestion complète (mise
         * à jour, ajout...) reste réservée au Gestionnaire. Couvre
         * à la fois "/stock?codeBarre=..." et "/{id}/stock".
         */
        if (uri.startsWith("/api/medicaments")
                && uri.endsWith(STOCK_SUFFIXE)
                && METHODE_GET.equalsIgnoreCase(methode)) {
            return true;
        }

        for (Map.Entry<String, String> regle
                : ROLE_PAR_PREFIXE.entrySet()) {
            if (uri.startsWith(regle.getKey())) {
                return regle.getValue().equals(role);
            }
        }
        return true;
    }
}