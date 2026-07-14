package service;

import dao.UtilisateurDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    private static final String LOGIN_PHARMACIEN = "phar1";
    private static final String LOGIN_GESTIONNAIRE = "gest1";
    private static final String LOGIN_INCONNU = "inconnu";

    private static final String PASSWORD = "motdepasse";

    private static final String ROLE_PHARMACIEN = "PHARMACIEN";
    private static final String ROLE_GESTIONNAIRE = "GESTIONNAIRE";
    private static final String ROLE_ECHEC = "ECHEC";

    @Mock
    private UtilisateurDAO utilisateurDAO;

    private AuthService authService;


    @BeforeEach
    void setUp() {
        authService = new AuthService(utilisateurDAO);
    }


    @Test
    void login_pharmacien_doitRetournerRolePharmacien() {

        when(utilisateurDAO.getRole(
                LOGIN_PHARMACIEN,
                PASSWORD))
                .thenReturn(ROLE_PHARMACIEN);


        String role =
                authService.login(
                        LOGIN_PHARMACIEN,
                        PASSWORD);


        assertEquals(
                ROLE_PHARMACIEN,
                role);

        verify(utilisateurDAO)
                .getRole(
                        LOGIN_PHARMACIEN,
                        PASSWORD);
    }


    @Test
    void login_gestionnaire_doitRetournerRoleGestionnaire() {

        when(utilisateurDAO.getRole(
                LOGIN_GESTIONNAIRE,
                PASSWORD))
                .thenReturn(ROLE_GESTIONNAIRE);


        String role =
                authService.login(
                        LOGIN_GESTIONNAIRE,
                        PASSWORD);


        assertEquals(
                ROLE_GESTIONNAIRE,
                role);

        verify(utilisateurDAO)
                .getRole(
                        LOGIN_GESTIONNAIRE,
                        PASSWORD);
    }


    @Test
    void login_identifiantsInvalides_doitRetournerEchec() {

        when(utilisateurDAO.getRole(
                LOGIN_INCONNU,
                PASSWORD))
                .thenReturn(ROLE_ECHEC);


        String role =
                authService.login(
                        LOGIN_INCONNU,
                        PASSWORD);


        assertEquals(
                ROLE_ECHEC,
                role);

        verify(utilisateurDAO)
                .getRole(
                        LOGIN_INCONNU,
                        PASSWORD);
    }
}