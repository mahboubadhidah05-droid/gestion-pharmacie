package service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import dao.UtilisateurDAO;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UtilisateurDAO utilisateurDAO;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(utilisateurDAO);
    }

    @Test
    void login_pharmacien_doitRetournerRolePharmacien() {

        when(utilisateurDAO.getRole("phar1", "motdepasse"))
                .thenReturn("PHARMACIEN");

        String role = authService.login("phar1", "motdepasse");

        assertEquals("PHARMACIEN", role);
    }

    @Test
    void login_gestionnaire_doitRetournerRoleGestionnaire() {

        when(utilisateurDAO.getRole("gest1", "motdepasse"))
                .thenReturn("GESTIONNAIRE");

        String role = authService.login("gest1", "motdepasse");

        assertEquals("GESTIONNAIRE", role);
    }

    @Test
    void login_identifiantsInvalides_doitRetournerEchec() {

        when(utilisateurDAO.getRole("inconnu", "faux"))
                .thenReturn("ECHEC");

        String role = authService.login("inconnu", "faux");

        assertEquals("ECHEC", role);
    }
}