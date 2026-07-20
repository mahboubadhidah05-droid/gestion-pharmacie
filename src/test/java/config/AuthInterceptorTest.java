package config;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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

    @Test
    void doitAutoriserGestionnairePourMedicaments()
            throws Exception {

        HttpSession session =
                mock(HttpSession.class);

        when(request.getSession(false))
                .thenReturn(session);

        when(session.getAttribute(
                AuthController.ATTR_LOGIN))
                .thenReturn("gestionnaire");

        when(session.getAttribute(
                AuthController.ATTR_ROLE))
                .thenReturn("GESTIONNAIRE");

        when(request.getRequestURI())
                .thenReturn("/api/medicaments");

        boolean resultat =
                interceptor.preHandle(
                        request,
                        response,
                        new Object()
                );

        assertTrue(resultat);
    }

    @Test
    void doitRefuserPharmacienPourMedicaments()
            throws Exception {

        HttpSession session =
                mock(HttpSession.class);

        when(request.getSession(false))
                .thenReturn(session);

        when(session.getAttribute(
                AuthController.ATTR_LOGIN))
                .thenReturn("pharma");

        when(session.getAttribute(
                AuthController.ATTR_ROLE))
                .thenReturn("PHARMACIEN");

        when(request.getRequestURI())
                .thenReturn("/api/medicaments");

        boolean resultat =
                interceptor.preHandle(
                        request,
                        response,
                        new Object()
                );

        assertFalse(resultat);
    }

    @Test
    void doitAutoriserPharmacienPourVentes()
            throws Exception {

        HttpSession session =
                mock(HttpSession.class);

        when(request.getSession(false))
                .thenReturn(session);

        when(session.getAttribute(
                AuthController.ATTR_LOGIN))
                .thenReturn("pharma");

        when(session.getAttribute(
                AuthController.ATTR_ROLE))
                .thenReturn("PHARMACIEN");

        when(request.getRequestURI())
                .thenReturn("/api/ventes");

        boolean resultat =
                interceptor.preHandle(
                        request,
                        response,
                        new Object()
                );

        assertTrue(resultat);
    }

    @Test
    void doitAutoriserUrlSansRegleSpecifique()
            throws Exception {

        HttpSession session =
                mock(HttpSession.class);

        when(request.getSession(false))
                .thenReturn(session);

        when(session.getAttribute(
                AuthController.ATTR_LOGIN))
                .thenReturn("pharma");

        when(session.getAttribute(
                AuthController.ATTR_ROLE))
                .thenReturn("PHARMACIEN");

        when(request.getRequestURI())
                .thenReturn("/api/stock/historique");

        boolean resultat =
                interceptor.preHandle(
                        request,
                        response,
                        new Object()
                );

        assertTrue(resultat);
    }
}