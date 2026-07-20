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
                        true
                ),
                Arguments.of(
                        "pharma",
                        "PHARMACIEN",
                        "/api/medicaments",
                        false
                ),
                Arguments.of(
                        "pharma",
                        "PHARMACIEN",
                        "/api/ventes",
                        true
                ),
                Arguments.of(
                        "pharma",
                        "PHARMACIEN",
                        "/api/stock/historique",
                        true
                )
        );
    }
}