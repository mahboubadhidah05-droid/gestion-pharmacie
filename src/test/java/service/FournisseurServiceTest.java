package service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import dao.FournisseurDAO;
import dto.FournisseurResponse;

@ExtendWith(MockitoExtension.class)
class FournisseurServiceTest {

    @Mock
    private FournisseurDAO dao;

    private FournisseurService service;

    @BeforeEach
    void setUp() {
        service = new FournisseurService(dao);
    }

    @Test
    void doitAjouterUnFournisseurEtRetournerSonId() {

        String nom = "Laboratoire Tunisie Pharma";
        String telephone = "71234567";
        String email = "contact@ltp.tn";
        String adresse = "Tunis";
        int idAttendu = 4;

        when(
                dao.ajouterFournisseur(nom, telephone, email, adresse)
        ).thenReturn(idAttendu);

        int resultat =
                service.ajouter(nom, telephone, email, adresse);

        assertEquals(idAttendu, resultat);

        verify(dao).ajouterFournisseur(nom, telephone, email, adresse);
    }

    @Test
    void doitListerLesFournisseurs() {

        List<FournisseurResponse> fournisseurs =
                List.of(
                        new FournisseurResponse(
                                1,
                                "Laboratoire Tunisie Pharma",
                                "71234567",
                                "contact@ltp.tn",
                                "Tunis"
                        )
                );

        when(dao.listerFournisseurs()).thenReturn(fournisseurs);

        List<FournisseurResponse> resultat =
                service.listerFournisseurs();

        assertEquals(fournisseurs, resultat);

        verify(dao).listerFournisseurs();
    }
}