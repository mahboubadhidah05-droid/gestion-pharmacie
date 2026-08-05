package config;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import controller.AuthController;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

class AuthInterceptorTest {

    private AuthInterceptor interceptor;
    private HttpServletRequest request;
    private HttpServletResponse response;

    @BeforeEach
    void setUp() {
        interceptor = new AuthInterceptor();
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
    }

    @Test
    void doitRefuserUtilisateurNonConnecte()
            throws Exception {

        when(request.getSession(false))
                .thenReturn(null);

        boolean resultat =
                interceptor.preHandle(
                        request,
                        response,
                        new Object()
                );

        assertFalse(resultat);
    }

    @ParameterizedTest
    @MethodSource("casAutorisationEtRefus")
    void doitGererAccesSelonRoleEtUrl(
            String login,
            String role,
            String url,
            String methode,
            boolean accesAttendu)
            throws Exception {

        HttpSession session =
                mock(HttpSession.class);

        when(request.getSession(false))
                .thenReturn(session);

        when(session.getAttribute(
                AuthController.ATTR_LOGIN))
                .thenReturn(login);

        when(session.getAttribute(
                AuthController.ATTR_ROLE))
                .thenReturn(role);

        when(request.getRequestURI())
                .thenReturn(url);

        when(request.getMethod())
                .thenReturn(methode);

        boolean resultat =
                interceptor.preHandle(
                        request,
                        response,
                        new Object()
                );

        if (accesAttendu) {
            assertTrue(resultat);
        } else {
            assertFalse(resultat);
        }
    }

    private static Stream<Arguments>
    casAutorisationEtRefus() {

        return Stream.of(
                Arguments.of(
                        "gestionnaire",
                        "GESTIONNAIRE",
                        "/api/medicaments",
                        "GET",
                        true
                ),
                Arguments.of(
                        "pharma",
                        "PHARMACIEN",
                        "/api/medicaments",
                        "GET",
                        false
                ),
                Arguments.of(
                        "pharma",
                        "PHARMACIEN",
                        "/api/ventes",
                        "GET",
                        true
                ),
                Arguments.of(
                        "pharma",
                        "PHARMACIEN",
                        "/api/stock/historique",
                        "GET",
                        true
                ),
                /* Nouveau : le Gestionnaire peut LIRE les ventes
                   (rapports), mais ne peut pas en créer/annuler. */
                Arguments.of(
                        "gestionnaire",
                        "GESTIONNAIRE",
                        "/api/ventes",
                        "GET",
                        true
                ),
                Arguments.of(
                        "gestionnaire",
                        "GESTIONNAIRE",
                        "/api/ventes",
                        "POST",
                        false
                ),
                Arguments.of(
                        "gestionnaire",
                        "GESTIONNAIRE",
                        "/api/ventes/5",
                        "DELETE",
                        false
                ),
                Arguments.of(
                        "pharma",
                        "PHARMACIEN",
                        "/api/ventes",
                        "POST",
                        true
                ),
                /* Le Pharmacien doit pouvoir scanner un médicament
                   pendant une vente, malgré la restriction générale
                   de /api/medicaments au Gestionnaire. */
                Arguments.of(
                        "pharma",
                        "PHARMACIEN",
                        "/api/medicaments/code-barre/1234567890123",
                        "GET",
                        true
                ),
                Arguments.of(
                        "gestionnaire",
                        "GESTIONNAIRE",
                        "/api/medicaments/code-barre/1234567890123",
                        "GET",
                        true
                ),
                Arguments.of(
                        "pharma",
                        "PHARMACIEN",
                        "/api/medicaments/recherche",
                        "GET",
                        true
                )
        );
    }
}