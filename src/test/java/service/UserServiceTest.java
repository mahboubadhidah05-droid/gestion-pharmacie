package service;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dao.UserDAO;

class UserServiceTest {

    private UserDAO userDAO;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userDAO = mock(UserDAO.class);
        userService = new UserService(userDAO);
    }

    @Test
    void consulterProfilDoitAfficherProfilValide() {

        String[] profil = {
                "Dupont",
                "Jean"
        };

        when(
                userDAO.getProfil("jean")
        ).thenReturn(profil);

        userService.consulterProfil("jean");

        verify(userDAO).getProfil("jean");
    }

    @Test
    void consulterProfilDoitGererProfilNull() {

        when(
                userDAO.getProfil("inconnu")
        ).thenReturn(null);

        userService.consulterProfil("inconnu");

        verify(userDAO).getProfil("inconnu");
    }

    @Test
    void consulterProfilDoitGererProfilVide() {

        when(
                userDAO.getProfil("inconnu")
        ).thenReturn(new String[0]);

        userService.consulterProfil("inconnu");

        verify(userDAO).getProfil("inconnu");
    }

    @Test
    void consulterProfilDoitGererProfilAvecUnSeulElement() {

        when(
                userDAO.getProfil("test")
        ).thenReturn(
                new String[]{"Dupont"}
        );

        userService.consulterProfil("test");

        verify(userDAO).getProfil("test");
    }

    @Test
    void getProfilDoitRetournerLeProfil() {

        String[] profil = {
                "Dupont",
                "Jean"
        };

        when(
                userDAO.getProfil("jean")
        ).thenReturn(profil);

        String[] resultat =
                userService.getProfil("jean");

        assertSame(
                profil,
                resultat
        );

        verify(userDAO).getProfil("jean");
    }

    @Test
    void getProfilDoitRetournerProfilVide() {

        String[] profilVide =
                new String[0];

        when(
                userDAO.getProfil("inconnu")
        ).thenReturn(profilVide);

        String[] resultat =
                userService.getProfil("inconnu");

        assertArrayEquals(
                profilVide,
                resultat
        );

        verify(userDAO).getProfil("inconnu");
    }
}