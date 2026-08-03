package controller;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import dto.UtilisateurGestionResponse;
import service.UtilisateurGestionService;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UtilisateurGestionControllerTest {

    private static final String CONTENT_TYPE = "application/json";

    @Mock
    private UtilisateurGestionService service;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new UtilisateurGestionController(service))
                .build();
    }

    @Test
    void lister_doitRetourner200EtLaListe() throws Exception {

        when(service.listerTous()).thenReturn(
                List.of(new UtilisateurGestionResponse(
                        1, "Ben Salah", "Ali", "pharma", "PHARMACIEN"
                ))
        );

        mockMvc.perform(get("/api/comptes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].login").value("pharma"));
    }

    @Test
    void creer_reussi_doitRetourner201() throws Exception {

        when(service.creer("Nom", "Prenom", "login1", "motdepasse1", "PHARMACIEN"))
                .thenReturn(4);

        mockMvc.perform(post("/api/comptes")
                        .contentType(CONTENT_TYPE)
                        .content("{\"nom\":\"Nom\",\"prenom\":\"Prenom\","
                                + "\"login\":\"login1\",\"pwd\":\"motdepasse1\","
                                + "\"role\":\"PHARMACIEN\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    void creer_roleInvalide_doitRetourner400() throws Exception {

        when(service.creer("Nom", "Prenom", "login1", "motdepasse1", "ADMIN"))
                .thenReturn(-1);

        mockMvc.perform(post("/api/comptes")
                        .contentType(CONTENT_TYPE)
                        .content("{\"nom\":\"Nom\",\"prenom\":\"Prenom\","
                                + "\"login\":\"login1\",\"pwd\":\"motdepasse1\","
                                + "\"role\":\"ADMIN\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void creer_motDePasseFaible_doitRetourner400() throws Exception {

        when(service.creer("Nom", "Prenom", "login1", "faible", "PHARMACIEN"))
                .thenReturn(-2);

        mockMvc.perform(post("/api/comptes")
                        .contentType(CONTENT_TYPE)
                        .content("{\"nom\":\"Nom\",\"prenom\":\"Prenom\","
                                + "\"login\":\"login1\",\"pwd\":\"faible\","
                                + "\"role\":\"PHARMACIEN\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void creer_loginExistant_doitRetourner409() throws Exception {

        when(service.creer("Nom", "Prenom", "pharma", "motdepasse1", "PHARMACIEN"))
                .thenReturn(-3);

        mockMvc.perform(post("/api/comptes")
                        .contentType(CONTENT_TYPE)
                        .content("{\"nom\":\"Nom\",\"prenom\":\"Prenom\","
                                + "\"login\":\"pharma\",\"pwd\":\"motdepasse1\","
                                + "\"role\":\"PHARMACIEN\"}"))
                .andExpect(status().isConflict());
    }

    @Test
    void modifier_reussi_doitRetourner200() throws Exception {

        when(
                service.modifier(1, "PHARMACIEN", "Nom", "Prenom", "login1", null)
        ).thenReturn(0);

        mockMvc.perform(put("/api/comptes/PHARMACIEN/1")
                        .contentType(CONTENT_TYPE)
                        .content("{\"nom\":\"Nom\",\"prenom\":\"Prenom\","
                                + "\"login\":\"login1\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void modifier_introuvable_doitRetourner404() throws Exception {

        when(
                service.modifier(999, "PHARMACIEN", "Nom", "Prenom", "login1", null)
        ).thenReturn(-4);

        mockMvc.perform(put("/api/comptes/PHARMACIEN/999")
                        .contentType(CONTENT_TYPE)
                        .content("{\"nom\":\"Nom\",\"prenom\":\"Prenom\","
                                + "\"login\":\"login1\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void supprimer_reussi_doitRetourner200() throws Exception {

        when(service.supprimer(1, "GESTIONNAIRE")).thenReturn(true);

        mockMvc.perform(delete("/api/comptes/GESTIONNAIRE/1"))
                .andExpect(status().isOk());
    }

    @Test
    void supprimer_introuvable_doitRetourner404() throws Exception {

        when(service.supprimer(999, "GESTIONNAIRE")).thenReturn(false);

        mockMvc.perform(delete("/api/comptes/GESTIONNAIRE/999"))
                .andExpect(status().isNotFound());
    }
}