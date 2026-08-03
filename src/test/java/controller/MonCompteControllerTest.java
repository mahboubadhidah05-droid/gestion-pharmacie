package controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import dao.UtilisateurDAO;
import dao.UtilisateurGestionDAO;
import dto.MonProfilResponse;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class MonCompteControllerTest {

    private static final String CONTENT_TYPE = "application/json";

    @Mock
    private UtilisateurDAO utilisateurDAO;

    @Mock
    private UtilisateurGestionDAO utilisateurGestionDAO;

    private MockMvc mockMvc;

    private MockHttpSession session;

    @BeforeEach
    void setUp() {

        mockMvc = MockMvcBuilders
                .standaloneSetup(
                        new MonCompteController(
                                utilisateurDAO,
                                utilisateurGestionDAO
                        )
                )
                .build();

        session = new MockHttpSession();
        session.setAttribute(AuthController.ATTR_LOGIN, "pharma");
        session.setAttribute(AuthController.ATTR_ROLE, "PHARMACIEN");
    }

    @Test
    void changerMotDePasse_reussi_doitRetourner200() throws Exception {

        when(utilisateurDAO.getRole("pharma", "ancienMdp1"))
                .thenReturn("PHARMACIEN");

        mockMvc.perform(put("/api/mon-compte/mot-de-passe")
                        .session(session)
                        .contentType(CONTENT_TYPE)
                        .content("{\"pwdActuel\":\"ancienMdp1\","
                                + "\"pwdNouveau\":\"nouveauMdp1\"}"))
                .andExpect(status().isOk());

        verify(utilisateurGestionDAO).changerMotDePasseParLogin(
                org.mockito.ArgumentMatchers.eq("pharma"),
                org.mockito.ArgumentMatchers.eq("PHARMACIEN"),
                org.mockito.ArgumentMatchers.anyString()
        );
    }

    @Test
    void changerMotDePasse_ancienMotDePasseIncorrect_doitRetourner401()
            throws Exception {

        when(utilisateurDAO.getRole("pharma", "mauvaisMdp"))
                .thenReturn("ECHEC");

        mockMvc.perform(put("/api/mon-compte/mot-de-passe")
                        .session(session)
                        .contentType(CONTENT_TYPE)
                        .content("{\"pwdActuel\":\"mauvaisMdp\","
                                + "\"pwdNouveau\":\"nouveauMdp1\"}"))
                .andExpect(status().isUnauthorized());

        verify(utilisateurGestionDAO, never()).changerMotDePasseParLogin(
                org.mockito.ArgumentMatchers.anyString(),
                org.mockito.ArgumentMatchers.anyString(),
                org.mockito.ArgumentMatchers.anyString()
        );
    }

    @Test
    void changerMotDePasse_nouveauMotDePasseFaible_doitRetourner400()
            throws Exception {

        when(utilisateurDAO.getRole("pharma", "ancienMdp1"))
                .thenReturn("PHARMACIEN");

        mockMvc.perform(put("/api/mon-compte/mot-de-passe")
                        .session(session)
                        .contentType(CONTENT_TYPE)
                        .content("{\"pwdActuel\":\"ancienMdp1\","
                                + "\"pwdNouveau\":\"faible\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void monProfil_doitRetourner200EtLesInfos() throws Exception {

        when(utilisateurGestionDAO.getMonProfil("pharma", "PHARMACIEN"))
                .thenReturn(new MonProfilResponse(
                        "Ben Salah", "Ali", "pharma", "ali@pharmacie.tn"
                ));

        mockMvc.perform(get("/api/mon-compte").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nom").value("Ben Salah"))
                .andExpect(jsonPath("$.email").value("ali@pharmacie.tn"));
    }

    @Test
    void monProfil_introuvable_doitRetourner404() throws Exception {

        when(utilisateurGestionDAO.getMonProfil("pharma", "PHARMACIEN"))
                .thenReturn(null);

        mockMvc.perform(get("/api/mon-compte").session(session))
                .andExpect(status().isNotFound());
    }

    @Test
    void modifierMesInfos_reussi_doitRetourner200() throws Exception {

        when(
                utilisateurGestionDAO.modifierInfosParLogin(
                        "pharma", "PHARMACIEN",
                        "Ben Salah", "Ali", "ali@pharmacie.tn"
                )
        ).thenReturn(true);

        mockMvc.perform(put("/api/mon-compte/infos")
                        .session(session)
                        .contentType(CONTENT_TYPE)
                        .content("{\"nom\":\"Ben Salah\",\"prenom\":\"Ali\","
                                + "\"email\":\"ali@pharmacie.tn\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void modifierMesInfos_introuvable_doitRetourner404() throws Exception {

        when(
                utilisateurGestionDAO.modifierInfosParLogin(
                        "pharma", "PHARMACIEN",
                        "Ben Salah", "Ali", "ali@pharmacie.tn"
                )
        ).thenReturn(false);

        mockMvc.perform(put("/api/mon-compte/infos")
                        .session(session)
                        .contentType(CONTENT_TYPE)
                        .content("{\"nom\":\"Ben Salah\",\"prenom\":\"Ali\","
                                + "\"email\":\"ali@pharmacie.tn\"}"))
                .andExpect(status().isNotFound());
    }
}