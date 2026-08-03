package controller;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import dto.FournisseurResponse;
import service.FournisseurService;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class FournisseurControllerTest {

    private static final String CONTENT_TYPE = "application/json";

    @Mock
    private FournisseurService fournisseurService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new FournisseurController(fournisseurService))
                .build();
    }

    @Test
    void ajouterFournisseur_doitRetourner201EtLId() throws Exception {

        when(
                fournisseurService.ajouter(
                        "Laboratoire Tunisie Pharma",
                        "71234567",
                        "contact@ltp.tn",
                        "Tunis"
                )
        ).thenReturn(4);

        mockMvc.perform(post("/api/fournisseurs")
                        .contentType(CONTENT_TYPE)
                        .content("{\"nom\":\"Laboratoire Tunisie Pharma\","
                                + "\"telephone\":\"71234567\","
                                + "\"email\":\"contact@ltp.tn\","
                                + "\"adresse\":\"Tunis\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Fournisseur créé avec succès"))
                .andExpect(jsonPath("$.id").value(4));
    }

    @Test
    void listerFournisseurs_doitRetourner200EtLaListe() throws Exception {

        when(fournisseurService.listerFournisseurs()).thenReturn(
                List.of(new FournisseurResponse(
                        1,
                        "Laboratoire Tunisie Pharma",
                        "71234567",
                        "contact@ltp.tn",
                        "Tunis"
                ))
        );

        mockMvc.perform(get("/api/fournisseurs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nom").value("Laboratoire Tunisie Pharma"));
    }
}