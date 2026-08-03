package service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import dao.UtilisateurGestionDAO;
import dto.UtilisateurGestionResponse;
import utils.MotDePasseUtils;

@ExtendWith(MockitoExtension.class)
class UtilisateurGestionServiceTest {

    @Mock
    private UtilisateurGestionDAO dao;

    private UtilisateurGestionService service;

    @BeforeEach
    void setUp() {
        service = new UtilisateurGestionService(dao);
    }

    @Test
    void doitListerTousLesUtilisateurs() {

        List<UtilisateurGestionResponse> utilisateurs =
                List.of(new UtilisateurGestionResponse(
                        1, "Ben Salah", "Ali", "pharma", "PHARMACIEN"
                ));

        when(dao.listerTous()).thenReturn(utilisateurs);

        assertEquals(utilisateurs, service.listerTous());
    }

    @Test
    void doitRefuserRoleInvalideALaCreation() {

        int resultat =
                service.creer("Nom", "Prenom", "login1", "motdepasse1", "ADMIN");

        assertEquals(-1, resultat);

        verify(dao, never()).creerUtilisateur(
                anyString(), anyString(), anyString(), anyString(), anyString()
        );
    }

    @Test
    void doitRefuserMotDePasseTropFaibleALaCreation() {

        int resultat =
                service.creer(
                        "Nom", "Prenom", "login1", "faible", "PHARMACIEN"
                );

        assertEquals(-2, resultat);

        verify(dao, never()).creerUtilisateur(
                anyString(), anyString(), anyString(), anyString(), anyString()
        );
    }

    @Test
    void doitRefuserLoginDejaUtilise() {

        when(dao.loginExiste("pharma")).thenReturn(true);

        int resultat =
                service.creer(
                        "Nom", "Prenom", "pharma", "motdepasse1", "PHARMACIEN"
                );

        assertEquals(-3, resultat);
    }

    @Test
    void doitCreerUtilisateurAvecMotDePasseHache() {

        when(dao.loginExiste("pharma3")).thenReturn(false);

        when(
                dao.creerUtilisateur(
                        eq("Nom"), eq("Prenom"), eq("pharma3"),
                        anyString(), eq("PHARMACIEN")
                )
        ).thenReturn(4);

        int resultat =
                service.creer(
                        "Nom", "Prenom", "pharma3", "motdepasse1", "PHARMACIEN"
                );

        assertEquals(4, resultat);

        verify(dao).creerUtilisateur(
                eq("Nom"),
                eq("Prenom"),
                eq("pharma3"),
                argThat(MotDePasseUtils::estHache),
                eq("PHARMACIEN")
        );
    }

    @Test
    void doitModifierSansChangerLeMotDePasseSiVide() {

        when(
                dao.modifierUtilisateur(
                        1, "PHARMACIEN", "Nom", "Prenom", "login1", null
                )
        ).thenReturn(true);

        int resultat =
                service.modifier(
                        1, "PHARMACIEN", "Nom", "Prenom", "login1", ""
                );

        assertEquals(0, resultat);

        verify(dao).modifierUtilisateur(
                1, "PHARMACIEN", "Nom", "Prenom", "login1", null
        );
    }

    @Test
    void doitRefuserModificationAvecMotDePasseTropFaible() {

        int resultat =
                service.modifier(
                        1, "PHARMACIEN", "Nom", "Prenom", "login1", "abc"
                );

        assertEquals(-2, resultat);
    }

    @Test
    void doitRetournerErreurSiUtilisateurIntrouvableALaModification() {

        when(
                dao.modifierUtilisateur(
                        999, "PHARMACIEN", "Nom", "Prenom", "login1", null
                )
        ).thenReturn(false);

        int resultat =
                service.modifier(
                        999, "PHARMACIEN", "Nom", "Prenom", "login1", null
                );

        assertEquals(-4, resultat);
    }

    @Test
    void doitSupprimerUtilisateur() {

        when(dao.supprimerUtilisateur(1, "GESTIONNAIRE")).thenReturn(true);

        assertTrue(service.supprimer(1, "GESTIONNAIRE"));
    }

    @Test
    void doitRefuserSuppressionAvecRoleInvalide() {

        assertFalse(service.supprimer(1, "ADMIN"));

        verify(dao, never()).supprimerUtilisateur(anyInt(), anyString());
    }
}